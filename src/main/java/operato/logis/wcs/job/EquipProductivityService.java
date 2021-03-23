package operato.logis.wcs.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import operato.logis.wcs.entity.DailyProdSummary;
import operato.logis.wcs.entity.Productivity;
import operato.logis.wcs.entity.WorkerActual;
import operato.logis.wcs.query.WcsQueryStore;
import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.sys.util.AnyDateUtil;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Filters;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.util.ClassUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 생산성 데이터 수집 서비스
 * 
 * @author wryang
 */
@Component
public class EquipProductivityService {
	/**
	 * 10분대 생산성 시간 포맷
	 */
	private static final String PROD_LIKE_FORMAT= "yyyy-MM-dd HH:mm";
	/**
	 * 10분대 생산성 시간 포맷
	 */
	private static final String PROD_10_DATE_FORMAT = "yyyy-MM-dd HH:mm:00";
	/**
	 * 10분대 
	 */
	private static final List<String> prodMinList = ValueUtil.newStringList("10", "20", "30", "40", "50", "60");
	
	/**
	 * 쿼리 매니저
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * WCS 쿼리 스토어
	 */
	@Autowired
	private WcsQueryStore wcsQueryStore;
	
	/**
	 * 진행 중인 배치에 한해서 서머리 정보 생성
	 */
	@Transactional
	public void summaryProductivity() {
		// 1. 대상 작업 배치 조회 (상태만 진행 중)
		Query condition = new Query();
		condition.addFilter("status", ValueUtil.newStringList(JobBatch.STATUS_RUNNING));
		List<JobBatch> runBatchList = this.queryManager.selectList(JobBatch.class, condition);
		
		// 2. Loop 대상 작업 배치
		for (JobBatch batch : runBatchList) {
			this.summaryProductivity(batch);
		}
	}
	
	/**
	 * 배치 별로 생산정 정보 생성
	 * 
	 * @param batch
	 */
	public void summaryProductivity(JobBatch batch) {
		
		// 1. 배치 대상으로 배치 시작시간 부터 마감된 배치는 마감 시간 진행 중이라면 현재 시간까지 계산이 필요한 10분대 시간 리스트 추출
		Date calcEndDate = ValueUtil.isEqualIgnoreCase(batch.getStatus(), JobBatch.STATUS_END) ? 
				AnyDateUtil.addMinutes(batch.getFinishedAt(), 10) : new Date();
		List<Date> tenMinList = this.getIntervalTenMin(batch.getInstructedAt(), calcEndDate);
		
		// 2. 10분대 시간대가 없으면 리턴
		if(tenMinList.isEmpty()) {
			return;
		}

		// 3. 시간대 리스트
		List<Productivity> prodList = new ArrayList<Productivity>();
		Productivity prod = null;
		
		// 4. 전체 작업 시작 부터 10분 단위 시간 Loop
		for(Date currentDate : tenMinList) {
			// 4.1 기존 10분대 생산성 정보가 존재하는지 조회
			prod = this.getProductivity(prod, batch, currentDate);
			
			// 4.2 생산성 데이터 생성 여부 판단
			if(this.isCreateProductivityData(prod, currentDate)) {
				prod = this.createProductivity(batch, currentDate);
			}
			
			// 4.3 기준 분 설정
			int endMin = AnyDateUtil.minInt(currentDate) + 10;
			
			// 4.4 기준 분에 데이터가 존재하면 기존에 집계된 것
			Integer pickResult = this.getProdPickRes(prod, ValueUtil.toString(endMin));
			
			// 4.5 집계 데이터 업데이트
			if(pickResult == null) {
				prod = this.updateProductivity(prod, batch, currentDate, endMin);
			}
			
			// 4.6 생산성 정보 추가
			prodList.add(prod);
		}
		
		// 5. 대상 작업 배치 일별 서머리 업데이트
		for(Productivity p : prodList) {
			// 5.1 일별 서머리 조회
			DailyProdSummary summary = this.getDailyProdSummary(batch, p);
			
			if(summary == null) {
				// 5.1 일별 서머리 생성
				this.createDailySummary(batch, p);
			} else {
				// 5.2 일별 서머리 실적 업데이트
				this.updateDailySummary(batch, p, summary);
			}
		}
	}
	
	/**
	 * 일별 서머리 정보 생성
	 * 
	 * @param batch
	 * @param prod
	 */
	private void createDailySummary(JobBatch batch, Productivity prod) {
		
		// 일별 서머리 생성 
		DailyProdSummary summary = new DailyProdSummary();
		summary.setDomainId(batch.getDomainId());
		summary.setJobDate(prod.getJobDate());
		summary.setAreaCd(batch.getAreaCd());
		summary.setStageCd(batch.getStageCd());
		summary.setEquipGroupCd(batch.getEquipGroupCd());
		summary.setEquipType(batch.getEquipType());
		summary.setEquipCd(batch.getEquipCd());
		summary.setJobType(batch.getJobType());
		summary.setBatchId(batch.getId());
		
		// 작업 일자 설정
		Date workDate = AnyDateUtil.parse(prod.getJobDate(), AnyDateUtil.getDateFormat());
		summary.setYear(AnyDateUtil.getYear(workDate));
		summary.setMonth(AnyDateUtil.getMonth(workDate) + 1);
		summary.setDay(AnyDateUtil.getDay(workDate));
		
		// 투입 작업자 수, 총 작업자 수, UPH, 작업 진행율 설정 
		Map<String, Object> etcMap = this.calcDailySumUphAndEquipRate(batch, summary);
		summary.setInputWorkers(ValueUtil.toFloat(etcMap.get("input")));
		summary.setTotalWorkers(ValueUtil.toFloat(etcMap.get("total")));
		summary.setUph(ValueUtil.toFloat(etcMap.get("uph")));
		summary.setEquipRate(ValueUtil.toFloat(etcMap.get("rate")));
		
		// 작업 수량 정보
		summary.setPlanQty(batch.getBatchPcs());
		summary.setResultQty(batch.getResultPcs());
		summary.setWrongPickingQty(batch.getWrongPickingQty());
		summary.setLeftQty(batch.getBatchPcs() - batch.getResultPcs());
		summary.setProgressRate(batch.getProgressRate());
		summary.setEquipRuntime(batch.getEquipRuntime());
		
		// 시간 처리량 정보
		String fieldName = String.format("h%02dResult", ValueUtil.toInteger(prod.getJobHour()) + 1);
		ClassUtil.setFieldValue(summary, fieldName, this.sumMinProdResults(prod));
		
		// 일별 생산성 정보 생성
		this.queryManager.insert(summary);
	}
	
	/**
	 * 일별 서머리 정보 업데이트
	 * 
	 * @param batch
	 * @param prod
	 * @param summary
	 */
	private void updateDailySummary(JobBatch batch, Productivity prod, DailyProdSummary summary) {
		
		// 작업수량 정보
		summary.setPlanQty(batch.getBatchPcs());
		summary.setResultQty(batch.getResultPcs());
		summary.setWrongPickingQty(batch.getWrongPickingQty());
		summary.setLeftQty(batch.getBatchPcs() - batch.getResultPcs());
		summary.setProgressRate(batch.getProgressRate());
		summary.setEquipRuntime(batch.getEquipRuntime());
		
		// 시간 처리량 정보
		String fieldName = String.format("h%02dResult", ValueUtil.toInteger(prod.getJobHour()));
		ClassUtil.setFieldValue(summary, fieldName, this.sumMinProdResults(prod));
		Map<String,Object> etcMap = this.calcDailySumUphAndEquipRate(batch, summary);
		summary.setInputWorkers(ValueUtil.toFloat(etcMap.get("input")));
		summary.setTotalWorkers(ValueUtil.toFloat(etcMap.get("total")));
		summary.setUph(ValueUtil.toFloat(etcMap.get("uph")));
		summary.setEquipRate(ValueUtil.toFloat(etcMap.get("rate")));
		
		// 업데이트
		this.queryManager.update(summary, fieldName, "inputWorkers", "totalWorkers", "equipRate", "uph", "planQty", "resultQty", "wrongPickingQty", "leftQty", "progressRate", "equipRuntime", "updatedAt");
	}
	
	/**
	 * 일별 서머리 정보 조회
	 * 
	 * @param batch
	 * @param prod
	 * @return
	 */
	private DailyProdSummary getDailyProdSummary(JobBatch batch, Productivity prod) {
		Query condition = AnyOrmUtil.newConditionForExecution(batch.getDomainId());
		//condition.addSelect("id", "jobDate");
		condition.setFilter("batchId", batch.getId());
		condition.setFilter("jobDate", prod.getJobDate());
		return this.queryManager.selectByCondition(DailyProdSummary.class, condition);
	}
	
	/**
	 * 10분 단위 실쩍 정보에서 시간 실적으로 sum
	 * 
	 * @param prod
	 * @return
	 */
	private int sumMinProdResults(Productivity prod) {
		return ValueUtil.toInteger(prod.getM10Result(), 0)
				+ ValueUtil.toInteger(prod.getM20Result(), 0)
				+ ValueUtil.toInteger(prod.getM30Result(), 0)
				+ ValueUtil.toInteger(prod.getM40Result(), 0)
				+ ValueUtil.toInteger(prod.getM50Result(), 0)
				+ ValueUtil.toInteger(prod.getM60Result(), 0);
	}
	
	/**
	 * 10분별 생산성 데이터 생성
	 * 
	 * @param batch
	 * @param date
	 * @return
	 */
	private Productivity createProductivity(JobBatch batch, Date date) {
		// 1. 10분대별 생산성 정보 생성
		Productivity prod = new Productivity();
		prod.setBatchId(batch.getId());
		prod.setJobType(batch.getJobType());
		prod.setAreaCd(batch.getAreaCd());
		prod.setStageCd(batch.getStageCd());
		prod.setEquipGroupCd(batch.getEquipGroupCd());
		prod.setEquipType(batch.getEquipType());
		prod.setEquipCd(batch.getEquipCd());
		prod.setJobDate(AnyDateUtil.dateStr(date));
		prod.setJobHour(AnyDateUtil.hourStr(date));
		prod.setDomainId(batch.getDomainId());

		// 2. insert 
		return this.queryManager.insert(Productivity.class, prod);
	}
	
	/**
	 * 10분별 생산성 컬럼 update
	 * 
	 * @param prod
	 * @param batch
	 * @param stDate
	 * @param endMin
	 * @return
	 */
	private Productivity updateProductivity(Productivity prod, JobBatch batch, Date stDate, int endMin) {
		
		String updFieldName = "m" + endMin + "Result";
		String prodLikeStr = AnyDateUtil.dateTimeStr(stDate, PROD_LIKE_FORMAT);
		prodLikeStr = prodLikeStr.substring(0, prodLikeStr.length() - 1) + "%";
		
		// 10분대 실적 조회
		String sql = "select sum(picked_qty) from job_instances where domain_id = :domainId and batch_id = :batchId and pick_ended_at like :prodLikeStr";
		Map<String,Object> params = ValueUtil.newMap("domainId,batchId,prodLikeStr", batch.getDomainId(), batch.getId(), prodLikeStr);
		Integer fieldValue = this.queryManager.selectBySql(sql, params, Integer.class);
		ClassUtil.setFieldValue(prod, updFieldName, fieldValue);
		
		// 작업자 수 정보
		Date workDate = AnyDateUtil.parse(prod.getJobDate(), AnyDateUtil.getDateFormat());
		workDate = AnyDateUtil.addHours(workDate, ValueUtil.toInteger(prod.getJobHour()));
		Map<String, Object> workerMap = this.getWorkerCount(batch, workDate, AnyDateUtil.addMinutes(workDate, endMin),PROD_10_DATE_FORMAT);
		prod.setInputWorkers(ValueUtil.toInteger(workerMap.get("input")));
		prod.setTotalWorkers(ValueUtil.toInteger(workerMap.get("total")));
		
		// 10분대 실적, 투입 작업자 수, 총 작업자 수 업데이트
		this.queryManager.update(prod, updFieldName, "inputWorkers", "totalWorkers", "updatedAt");
		return prod;
	}
	
	/**
	 * 10분 생산성 데이터 insert or update 구분
	 * 
	 * @param prod
	 * @param compareDate
	 * @return
	 */
	private boolean isCreateProductivityData(Productivity prod, Date compareDate) {
		
		// 1. 최종 생성 데이터가 없으면 생성 대상
		if(ValueUtil.isEmpty(prod)) {
			return true;
		}
		
		// 2. 최종 생성 데이터와 최종 생성 대상 일자가 다르면 생성 대상
		if(ValueUtil.isNotEqual(prod.getJobDate(), AnyDateUtil.defaultDateStr(compareDate))) {
			return true;
		}
		
		// 3. 최종 생성 데이터와 최종 생성 대상 시간이 다르면 생성 대상
		if(ValueUtil.isNotEqual(prod.getJobHour(), ValueUtil.toString(AnyDateUtil.hourStr(compareDate)))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * from - to 시간으로 부터 10분씩 현재 시간까지의 10분 단위 시간대 리스트를 추출
	 * 
	 * @param stDate
	 * @param edDate
	 * @return
	 */
	private List<Date> getIntervalTenMin(Date stDate, Date edDate) {
		
		// 1. 시작 시간 구하기
		List<Date> dateList = new ArrayList<Date>();
		Date startDate = (Date)stDate.clone();
		int startMin = AnyDateUtil.minInt(startDate);
		startDate = AnyDateUtil.setMinutes(startDate, (startMin) / 10 * 10);
		startDate = AnyDateUtil.setSeconds(startDate, 0);
		
		// 2. 종료 시간 구하기
		Date endDate = (Date)edDate.clone();
		endDate = AnyDateUtil.addMinutes(edDate, -10);
		int endMin = AnyDateUtil.minInt(endDate);
		endDate = AnyDateUtil.setMinutes(endDate, (endMin) / 10 * 10);
		endDate = AnyDateUtil.setSeconds(endDate, 0);
		String endDateStr = AnyDateUtil.dateTimeStr(endDate);
		
		// 3. 작업 시작 후 생산성 데이터 생성 시간을 지나지 않은 경우
		if(endDate.before(startDate)) {
			return dateList;
		}
		
		dateList.add(startDate);
		
		// 4. 시작 시간 종료 시간 사이의 10분 단위 구간 구하기
		while(true) {
			Date lastDate = dateList.get(dateList.size() - 1);
			
			// 4.1 종료 시간과 이전 시간이 같으면 Loop 종료
			if(ValueUtil.isEqual(AnyDateUtil.dateTimeStr(lastDate), endDateStr)) {
				break;
			}
			
			// 4.2 이전 시간 값에 10분 증가후 리스트에 저장
			dateList.add(AnyDateUtil.addMinutes(lastDate, 10));
		}
		
		return dateList;
	}

	/**
	 * 최종 생산성 데이터 조회
	 * 
	 * @param prod
	 * @param batch
	 * @param date
	 * @return
	 */
	private Productivity getProductivity(Productivity prod, JobBatch batch, Date date) {
		
		// 1. 기존 데이터와 같은 데이터를 사용하는 경우에는 기존 데이터 재사용
		if(ValueUtil.isNotEmpty(prod) 
			&& ValueUtil.isEqual(prod.getBatchId(), batch.getId())
			&& ValueUtil.isEqual(prod.getJobDate(), AnyDateUtil.dateStr(date)) 
			&& ValueUtil.isEqual(prod.getJobHour(), AnyDateUtil.hourStr(date))) {
			return prod;
		}
		
		// 2. 기존 10분별 집계 여부를 확인하기 위해 집계 값을 string 으로도 조회 (null 여부로 해당 시간에 집계 대상 분을 구하기 위함)
		//String sql = "select x.*, m10_result as m10_result_str, m20_result as m20_result_str, m30_result as m30_result_str, m40_result as m40_result_str, m50_result as m50_result_str, m60_result as m60_result_str from productivity x where domain_id = :domainId and batch_id = :batchId and job_date = :jobDate and job_hour = :jobHour";
		String sql = this.wcsQueryStore.getFind10MinProductivityQuery();
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId,jobDate,jobHour", batch.getDomainId(), batch.getId(), AnyDateUtil.dateStr(date), AnyDateUtil.hourStr(date));
		List<Productivity> prodList = this.queryManager.selectListBySql(sql, params, Productivity.class, 0, 0);
		return ValueUtil.isEmpty(prodList) ? null : prodList.get(0);
	}

	/**
	 * 시간 구간 내에 작업자 수를 구한다.
	 * 
	 * @param batch
	 * @param stDate
	 * @param edDate
	 * @param dateCompareFormat
	 * @return
	 */
	private Map<String, Object> getWorkerCount(JobBatch batch, Date stDate, Date edDate, String dateCompareFormat) {
		
		String stDateStr = AnyDateUtil.dateTimeStr(stDate, dateCompareFormat);
		String edDateStr = AnyDateUtil.dateTimeStr(edDate, dateCompareFormat);
		
		Filters finishedAtFilters = new Filters();
		finishedAtFilters.setOperator(Filters.OPERATOR_OR);
		finishedAtFilters.addFilter("finishedAt", OrmConstants.IS_NULL, LogisConstants.EMPTY_STRING);
		finishedAtFilters.addFilter("finishedAt", LogisConstants.EMPTY_STRING);
		finishedAtFilters.addFilter("finishedAt", OrmConstants.GREATER_THAN_EQUAL, edDateStr);
		finishedAtFilters.addFilter("finishedAt", OrmConstants.GREATER_THAN, stDateStr);
		
		Query condition = new Query();
		condition.setOperator(Filters.OPERATOR_AND);
		condition.addFilter("domainId", batch.getDomainId());
		condition.addFilter("jobDate", AnyDateUtil.dateStr(stDate));
		condition.addFilter("equipType", batch.getEquipType());
		condition.addFilter("equipCd", batch.getEquipCd());
		condition.addFilter("startedAt", OrmConstants.LESS_THAN, edDateStr);
		condition.addFilters(finishedAtFilters);
		condition.addGroup("workerId");
		Integer workerCnt = this.queryManager.selectSize(WorkerActual.class, condition);
		
		// 조회 결과가 없으면 작업 배치의 작업자수가 기준 
		if(workerCnt == 0) {
			workerCnt = ValueUtil.toInteger(batch.getInputWorkers(), 0);
		}
		
		return ValueUtil.newMap("input,total", workerCnt,workerCnt);
	}
	
	/**
	 * 일 별 서머리 정보 작업자 수 / UPH / EquipRate
	 * 
	 * @param batch
	 * @param summary
	 * @return
	 */
	private Map<String,Object> calcDailySumUphAndEquipRate(JobBatch batch, DailyProdSummary summary) {
		
		//String sql = "select input_workers, total_workers, m10_result as m10_result_str, m20_result as m20_result_str, m30_result as m30_result_str, m40_result as m40_result_str, m50_result as m50_result_str, m60_result as m60_result_str from productivity where domain_id = :domainId and batch_id = :batchId and job_date = :jobDate";
		String sql = this.wcsQueryStore.getSearch10MinProductivityQuery();
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId,jobDate", batch.getDomainId(), batch.getId(), summary.getJobDate());
		List<Productivity> prodList = this.queryManager.selectListBySql(sql, params, Productivity.class, 0, 0);
		
		// 10분당 작업자 합계
		float sumMinInputWorker = 0;
		float sumMinTotalWorker = 0;
		// 10분당 피킹 결과 존재 수
		float cntPickResExists = 0;
		// 10분당 피킹수 합계 
		float sumPickRes = 0;
		// 작업 시작후 현재까지 10분당 틱 수
		float cntEquipOnTicks = 0;
		
		for(Productivity prod : prodList) {
			sumMinInputWorker += prod.getInputWorkers();
			sumMinTotalWorker += prod.getTotalWorkers();
			
			for(String prodMin : prodMinList) {
				Integer pickRes = this.getProdPickRes(prod, prodMin);
				
				// 피킹 결과가 null 이면 작업 시간내 데이터 아님
				if(ValueUtil.isNotEmpty(pickRes)) {
					// 10분 틱 카운트 증가
					cntEquipOnTicks += 1;
					// 피킹 수량 sum
					sumPickRes += pickRes;
					// 피킹 결과가 0이 아닌 경우 (작업 외 시간 제외)
					if(pickRes != 0) cntPickResExists += 1;
				}
			}
		}
		
		float hourCnt = ValueUtil.toFloat(prodList.size());
		float total = ValueUtil.isEmpty(prodList) ? 0f : sumMinTotalWorker / hourCnt; // 시간당 작업자 수
		float input = ValueUtil.isEmpty(prodList) ? 0f : sumMinInputWorker / hourCnt;
		float uph = total == 0 ? 0f : (cntPickResExists == 0 ? 0 : sumPickRes / cntPickResExists * 6) / total; // (전체 피킹 수 / 10분당 작업량 존재 수 * 6 = 시간당 피킹 수) / 시간당 작업자 수
		float rate = cntEquipOnTicks == 0 ? 0f : cntPickResExists/cntEquipOnTicks * 100; // 10분당 작업량 존재 수 / 작업시작후 10분당 틱수
		return ValueUtil.newMap("total,input,uph,rate", total, input, uph, rate);
	}
	
	/**
	 * 파라미터 10분대의 생산 수량 조회
	 * 
	 * @param prod
	 * @param min
	 * @return
	 */
	private Integer getProdPickRes(Productivity prod, String min) {
		Object valueObj = ClassUtil.getFieldValue(prod, "m" + min + "ResultStr");
		return (ValueUtil.isEmpty(valueObj)) ? null : ValueUtil.toInteger(valueObj);
	}

}