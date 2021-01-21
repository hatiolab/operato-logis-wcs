package operato.logis.wcs.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import operato.logis.wcs.entity.Productivity;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.sys.util.AnyDateUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.util.ClassUtil;
import xyz.elidom.util.ValueUtil;

@Component
public class EquipProductivityService {
	
	private Logger logger = LoggerFactory.getLogger(EquipProductivityService.class);
		
	@Autowired
	IQueryManager queryManager;
	
//	@Scheduled(cron="0 7/10 * * * ?")
	@Scheduled(fixedDelay=30000)
	@Transactional
	public void summaryProductivity() {
		// 1. 대상 작업 배치 조회 ( 상태만 진행중, 마감 인 것 들 )
		// TODO 마감 배치인경우 작업 일자 로 생산성 집계 구간 정의 필요
		Query condition = new Query();
		condition.addFilter("status", OrmConstants.IN, ValueUtil.newStringList(JobBatch.STATUS_END, JobBatch.STATUS_RUNNING));
		List<JobBatch> runBatchList = this.queryManager.selectList(JobBatch.class, condition);
		
		// 2. Loop 대상 작업 배치 
		for (JobBatch jobBatch : runBatchList ) {
			// 2.1. 대상 작업 배치 PRODUCTIVITY upsert
			Date instructedAt = jobBatch.getInstructedAt();
			List<Date> prodDateList = null;
			
			if(ValueUtil.isEqualIgnoreCase(jobBatch.getStatus(), JobBatch.STATUS_END)){
				prodDateList = this.getIntervalTenMin(instructedAt, jobBatch.getFinishedAt());
			} else {
				prodDateList = this.getIntervalTenMin(instructedAt);
			}

			Productivity prod = null;
			for(Date currentDate : prodDateList) {
				prod = this.getProductivity(prod, jobBatch, currentDate);
				
				if(this.isCreateProductivityData(prod, currentDate)) {
					prod = this.createProductivity(jobBatch, currentDate);
				}
				
				int baseMin = AnyDateUtil.minInt(currentDate) + 10;
				
				if(ValueUtil.isNotEmpty(ClassUtil.getFieldValue(prod, "m" + baseMin + "ResultStr"))){
					continue;
				}
				
				prod = this.updateProductivity(prod, "m" + baseMin + "Result", 100);
			}

			// 2.2. 대상 작업 배치 DAILY_PROD_SUMMARY	 upsert
			
		}
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
		
		// TODO
		prod.setTotalWorkers(10);
		prod.setStationCd("00");
		
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
	private Productivity updateProductivity(Productivity prod, String fieldName, Object fieldValue) {
		ClassUtil.setFieldValue(prod, fieldName, fieldValue);
		this.queryManager.update(prod, fieldName, "updatedAt");
		
		return prod;
	}
	
	
	
	/**
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
}