package operato.logis.wcs.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import operato.logis.wcs.entity.DailyProdSummary;
import operato.logis.wcs.entity.Productivity;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.sys.util.AnyDateUtil;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.util.ClassUtil;
import xyz.elidom.util.ValueUtil;

@Component
public class EquipProductivityService {
	
//	private Logger logger = LoggerFactory.getLogger(EquipProductivityService.class);
	
	private static final String PROD_LIKE_FORMAT= "yyyy-MM-dd HH:m";
		
	@Autowired
	IQueryManager queryManager;
	
//	@Scheduled(fixedDelay=300000, initialDelay=30000)
	@Transactional
	public void summaryProductivity() {
		// 1. 대상 작업 배치 조회 ( 상태만 진행중, 마감 인 것 들 )
		// TODO 마감 배치인경우 작업 일자 로 생산성 집계 구간 정의 필요
		Query condition = new Query();
		condition.addFilter("status", OrmConstants.IN, ValueUtil.newStringList(JobBatch.STATUS_END, JobBatch.STATUS_RUNNING));
		List<JobBatch> runBatchList = this.queryManager.selectList(JobBatch.class, condition);
		
		// 2. Loop 대상 작업 배치 
		for (JobBatch jobBatch : runBatchList ) {
			if(ValueUtil.isNotEqual("A1-B2B-21011311", jobBatch.getId())) {
				continue;
			}
			
			// 2.1. 대상 작업 배치 PRODUCTIVITY upsert
			Date instructedAt = jobBatch.getInstructedAt();
			List<Date> prodDateList = null;
			
			if(ValueUtil.isEqualIgnoreCase(jobBatch.getStatus(), JobBatch.STATUS_END)){
				// 2.2. 마감된 작업배치는 마감 시간 기준 
				prodDateList = this.getIntervalTenMin(instructedAt, jobBatch.getFinishedAt());
			} else {
				// 2.3. 진행중인 배치는 현재 시간 기준  
				prodDateList = this.getIntervalTenMin(instructedAt);
			}

			Productivity prod = null;
			// 2.4 전체 작업 시작 부터 10 분 단위 시간 Loop
			for(Date currentDate : prodDateList) {
				prod = this.getProductivity(prod, jobBatch, currentDate);
				
				// 2.4.1 생산성 데이터 생성 여부 판단 
				if(this.isCreateProductivityData(prod, currentDate)) {
					prod = this.createProductivity(jobBatch, currentDate);
				}
				
				// 2.4.2 기준 분 
				int endMin = AnyDateUtil.minInt(currentDate) + 10;
				
				// 2.4.3 기준 분에 데이터가 존재 하ㅏ면 기존에 집계된 것 
				if(ValueUtil.isNotEmpty(ClassUtil.getFieldValue(prod, "m" + endMin + "ResultStr"))){
					continue;
				}
				
				// 2.4.4 집계 데이터 update 	
				prod = this.updateProductivity(prod, jobBatch, currentDate, endMin);
				
				
				// 2.4.5 대상 작업 배치 DAILY_PROD_SUMMARY upsert
				this.upsertDailySummary(jobBatch, prod, endMin);
			}
		}
	}
	
	/**
	 * 일별 작업 서머리 정보 생성 
	 * @param jobBatch
	 * @param prod
	 * @param targetMin
	 */
	private void upsertDailySummary(JobBatch jobBatch, Productivity prod, int targetMin) {
		
		// 1. 현재 시간이 완료 되지 않으면 skip
		if(targetMin != 60) {
			return;
		}
		
		// 2. 데이터 생성 또는 업데이트 여부 판별  
		DailyProdSummary summary = this.getDailyProdSummary(jobBatch, prod);
		
		if(ValueUtil.isEmpty(summary)){
			// 2.1 데이터 생성 
			this.createDailySummary(jobBatch, prod);
		} else {
			// 2.2 데이터 update 
			this.updateDailySummary(jobBatch, prod, summary);
		}
	}
	
	/**
	 * 일별 서머리 정보 생성 
	 * @param jobBatch
	 * @param prod
	 */
	private void createDailySummary(JobBatch jobBatch, Productivity prod) {
		DailyProdSummary summary = new DailyProdSummary();
		
		summary.setDomainId(jobBatch.getDomainId());
		summary.setJobDate(prod.getJobDate());
		summary.setAreaCd(jobBatch.getAreaCd());
		summary.setStageCd(jobBatch.getStageCd());
		summary.setEquipGroupCd(jobBatch.getEquipGroupCd());
		summary.setEquipType(jobBatch.getEquipType());
		summary.setEquipCd(jobBatch.getEquipCd());
		summary.setJobType(jobBatch.getJobType());
		summary.setBatchId(jobBatch.getId());
		
		// 작업 일ㅈ 
		Date workDate = AnyDateUtil.parse(prod.getJobDate(), AnyDateUtil.getDateFormat());
		
		summary.setYear(AnyDateUtil.getYear(workDate));
		summary.setMonth(AnyDateUtil.getMonth(workDate));
		summary.setDay(AnyDateUtil.getDay(workDate));
		
		// 작업 자 수 
		Map<String,Object> workerMap = this.getWorkerCount(jobBatch, workDate, AnyDateUtil.addHours(workDate, ValueUtil.toInteger(prod.getJobHour()) + 1));
		
		summary.setInputWorkers(ValueUtil.toFloat(workerMap.get("input")));
		summary.setTotalWorkers(ValueUtil.toFloat(workerMap.get("total")));
		//TODO 
		summary.setUph(0f);
		
		// 작업 수량 정보 
		summary.setPlanQty(jobBatch.getBatchPcs());
		summary.setResultQty(jobBatch.getResultPcs());
		summary.setWrongPickingQty(jobBatch.getWrongPickingQty());
		summary.setLeftQty(jobBatch.getBatchPcs() - jobBatch.getResultPcs());
		summary.setProgressRate(jobBatch.getProgressRate());
		summary.setEquipRuntime(jobBatch.getEquipRuntime());
		
		// 시간 처리량 정보 
		String fieldName = String.format("h%02dResult", ValueUtil.toInteger(prod.getJobHour()) + 1);
		ClassUtil.setFieldValue(summary, fieldName, this.sumMinProdResults(prod));
		
		// TODO 
		summary.setEquipRate(9f);
		
		this.queryManager.insert(summary);
	}
	
	/**
	 * 일별 서머리 정보 업데이트 
	 * @param jobBatch
	 * @param prod
	 * @param summary
	 */
	private void updateDailySummary(JobBatch jobBatch, Productivity prod, DailyProdSummary summary) {
		// 작업일자ㅏ  
		Date workDate = AnyDateUtil.parse(prod.getJobDate(), AnyDateUtil.getDateFormat());
		
		Map<String,Object> workerMap = this.getWorkerCount(jobBatch, workDate, AnyDateUtil.addHours(workDate, ValueUtil.toInteger(prod.getJobHour()) + 1));
		
		summary.setInputWorkers(ValueUtil.toFloat(workerMap.get("input")));
		summary.setTotalWorkers(ValueUtil.toFloat(workerMap.get("total")));
		// TODO
		summary.setUph(0f);

		// 작업수량 정보 
		summary.setPlanQty(jobBatch.getBatchPcs());
		summary.setResultQty(jobBatch.getResultPcs());
		summary.setWrongPickingQty(jobBatch.getWrongPickingQty());
		summary.setLeftQty(jobBatch.getBatchPcs() - jobBatch.getResultPcs());
		summary.setProgressRate(jobBatch.getProgressRate());
		summary.setEquipRuntime(jobBatch.getEquipRuntime());
		
		// 시간 처리량 정보 
		String fieldName = String.format("h%02dResult", ValueUtil.toInteger(prod.getJobHour()) + 1);
		ClassUtil.setFieldValue(summary, fieldName, this.sumMinProdResults(prod));

		// TODO 
		summary.setEquipRate(9f);

		this.queryManager.update(summary, fieldName, "inputWorkers","totalWorkers","equipRate","uph","planQty","resultQty","wrongPickingQty","leftQty","progressRate","equipRuntime","updatedAt");
	}
	
	/**
	 * 일별 서머리 정보 조회 
	 * @param jobBatch
	 * @param prod
	 * @return
	 */
	private DailyProdSummary getDailyProdSummary(JobBatch jobBatch, Productivity prod) {
		Query condition = AnyOrmUtil.newConditionForExecution(jobBatch.getDomainId());
		condition.addSelect("id");
		condition.setFilter("batchId",jobBatch.getId());
		condition.setFilter("jobDate",prod.getJobDate());
		
		return this.queryManager.selectByCondition(DailyProdSummary.class, condition);
	}
	
	/**
	 * 10분 단위 실쩍 정보에서 시간 실적으로 sum 
	 * @param prod
	 * @return
	 */
	private int sumMinProdResults(Productivity prod) {
		return prod.getM10Result() + prod.getM20Result() + prod.getM30Result() + prod.getM40Result() + prod.getM50Result() + prod.getM60Result();
	}
	
	/**
	 * 10분별 생산성 데이터 생성 
	 * @param jobBatch
	 * @param date
	 * @return
	 */
	private Productivity createProductivity(JobBatch jobBatch, Date date) {
		Productivity prod = new Productivity();
		
		prod.setBatchId(jobBatch.getId());
		prod.setJobType(jobBatch.getJobType());
		prod.setAreaCd(jobBatch.getAreaCd());
		prod.setStageCd(jobBatch.getStageCd());
		prod.setEquipGroupCd(jobBatch.getEquipGroupCd());
		prod.setEquipType(jobBatch.getEquipType());
		prod.setEquipCd(jobBatch.getEquipCd());
		prod.setJobDate(AnyDateUtil.dateStr(date));
		prod.setJobHour(AnyDateUtil.hourStr(date));
		prod.setDomainId(jobBatch.getDomainId());

		// insert 
		return this.queryManager.insert(Productivity.class, prod);
	}
	
	/**
	 * 10분별 생산성 컬럼 update 
	 * @param prod
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	private Productivity updateProductivity(Productivity prod, JobBatch jobBatch, Date stDate, int endMin) {
		
		String updFieldName = "m" + endMin + "Result";
		String prodLikeStr = AnyDateUtil.dateTimeStr(stDate, PROD_LIKE_FORMAT) + "%";
		
		// 실적 조회 
		String sql = "select sum(picked_qty) from job_instances where domain_id = :domainId and batch_id = :batchId and pick_ended_at like :prodLikeStr";
		Map<String,Object> params = ValueUtil.newMap("domainId,batchId,prodLikeStr", jobBatch.getDomainId(), jobBatch.getId(), prodLikeStr);
		Integer fieldValue = this.queryManager.selectBySql(sql, params, Integer.class);
		
		ClassUtil.setFieldValue(prod, updFieldName, fieldValue);
		
		// 작업자ㅏ 수 정보 
		Date workDate = AnyDateUtil.parse(prod.getJobDate(), AnyDateUtil.getDateFormat());
		workDate = AnyDateUtil.addHours(workDate, ValueUtil.toInteger(prod.getJobHour()));
		
		Map<String,Object> workerMap = this.getWorkerCount(jobBatch, workDate, AnyDateUtil.addMinutes(workDate, endMin));
		
		prod.setInputWorkers(ValueUtil.toInteger(workerMap.get("input")));
		prod.setTotalWorkers(ValueUtil.toInteger(workerMap.get("total")));
		
		this.queryManager.update(prod, updFieldName, "inputWorkers", "totalWorkers", "updatedAt");
		
		return prod;
	}
	
	
	
	/**
	 * 10분 생산성 데이터 insert or update 구분 
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
		
		// 3. 최종 생성 데이터와 최종 생성 대상 시간이  다르면 생성 대상 
		if(ValueUtil.isNotEqual(prod.getJobHour(), ValueUtil.toString(AnyDateUtil.hourStr(compareDate)))) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * param 데이트로부터 10분씩 현재 시간 까지의 시간 정보 list 를 구한다 .
	 * @param date
	 * @return
	 */
	private List<Date> getIntervalTenMin(Date stDate){
		return this.getIntervalTenMin(stDate, new Date());
	}
	
	/**
	 * param 데이트로부터 10분씩 현재 시간 까지의 시간 정보 list 를 구한다 .
	 * @param date
	 * @return
	 */
	private List<Date> getIntervalTenMin(Date stDate, Date edDate){
		List<Date> dateList = new ArrayList<Date>();
		
		// 1. 시작 시간 구하기 
		Date startDate = (Date)stDate.clone();		
		int startMin = AnyDateUtil.minInt(startDate);
		startDate = AnyDateUtil.setMinutes(startDate, (startMin)/10*10 );
		startDate = AnyDateUtil.setSeconds(startDate, 0);
		
		
		// 2. 종료 시간 구하기 
		Date endDate = (Date)edDate.clone();
		endDate = AnyDateUtil.addMinutes(edDate, -10);
		
		int endMin = AnyDateUtil.minInt(endDate);
		endDate = AnyDateUtil.setMinutes(endDate, (endMin)/10*10 );
		endDate = AnyDateUtil.setSeconds(endDate, 0);
		
		String endDateStr = AnyDateUtil.dateTimeStr(endDate);
		
		// 3. 작업 시작후 생산성 데이터 생성 시간을 지나지 않은 경우 
		if(endDate.before(startDate)) {
			return dateList;
		}
		
		dateList.add(startDate);
		
		// 4. 시작 시간 종료 시간 사이의 10분 단위 구간 구하기 
		while(true) {
			Date lastDate = dateList.get(dateList.size()-1);
			
			// 4.1 종료 시간과 잊전 시간 값이 같으면 Loop 종료 
			if(ValueUtil.isEqual(AnyDateUtil.dateTimeStr(lastDate),endDateStr)) {
				break;
			}
			
			// 4.2 이전 시간 값에 10분 증가후 리스트에 젖장  
			dateList.add(AnyDateUtil.addMinutes(lastDate, 10));
		}
		
		return dateList;
	}

	/**
	 * 최종 생산성 데이터를 구한다. 
	 * @param jobBatch
	 */
	private Productivity getProductivity(Productivity prod, JobBatch jobBatch, Date date) {
		
		// 1. 기존 데이터와 같은 데이터를 사용하는 경우에는 기존 데이터 재사용 
		if(ValueUtil.isNotEmpty(prod) 
			&& ValueUtil.isEqual(prod.getBatchId(), jobBatch.getId()) 
			&& ValueUtil.isEqual(prod.getJobDate(), AnyDateUtil.dateStr(date)) 
			&& ValueUtil.isEqual(prod.getJobHour(), AnyDateUtil.hourStr(date))) {
			return prod;
		}
		
		// 2. 기존 10분별 집계 여부를 확인 하기위해 집계 값을 string 으로도 조회 
		// null 여부로 해당 시간에 집계 대상 분을 구하기 위함 
		String sql = "select x.*, m10_result as m10_result_str, m20_result as m20_result_str, m30_result as m30_result_str, m40_result as m40_result_str, m50_result as m50_result_str, m60_result as m60_result_str from productivity x where domain_id = :domainId and batch_id = :batchId and job_date = :jobDate and job_hour = :jobHour";
		Map<String,Object> params = ValueUtil.newMap("domainId,batchId,jobDate,jobHour", jobBatch.getDomainId(),jobBatch.getId(),AnyDateUtil.dateStr(date),AnyDateUtil.hourStr(date));
		
		List<Productivity> prodList = this.queryManager.selectListBySql(sql, params, Productivity.class, 0, 0);
		
		if(ValueUtil.isEmpty(prodList)) {
			return null;
		} else {
			return prodList.get(0);
		}
	}

	/**
	 * 시간 구간 내에 작업자 수를 구한다. 
	 * @param jobBatch
	 * @param stDate
	 * @param edDate
	 * @return
	 */
	private Map<String,Object> getWorkerCount(JobBatch jobBatch, Date stDate, Date edDate){
		// TODO 
		
		return ValueUtil.newMap("input,total", 9,9);
	}

}