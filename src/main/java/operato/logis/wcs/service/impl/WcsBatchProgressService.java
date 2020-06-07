package operato.logis.wcs.service.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import operato.logis.wcs.query.WcsQueryStore;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.elidom.util.ValueUtil;

/**
 * 작업 배치별 생산성 정보 계산 서비스
 * 
 * @author shortstop
 */
@Component
public class WcsBatchProgressService extends AbstractQueryService {
	
	/**
	 * WCS Query Store
	 */
	@Autowired
	private WcsQueryStore wcsQueryStore;
	
	/**
	 * 배치 작업 진행율 및 생산성 관련 정보 업데이트
	 *  
	 * @param batch
	 * @param toTime
	 */
	public void updateBatchProductionResult(JobBatch batch, Date toTime) {
		
		batch.setResultBoxQty(this.calcBatchResultBoxQty(batch));
		batch.setResultOrderQty(this.calcBatchResultOrderQty(batch));
		batch.setResultPcs(this.calcBatchResultPcs(batch));
		batch.setProgressRate(batch.getBatchOrderQty() == 0 ? 0 : ((float)batch.getResultOrderQty() / (float)batch.getBatchOrderQty() * 100.0f));
		batch.setEquipRuntime(this.calcBatchEquipRuntime(batch, toTime));
		batch.setUph(this.calcBatchUph(batch, toTime));
	}
	
	/**
	 * 작업 배치의 최종 처리 박스 총 수량을 구한다.
	 * 
	 * @param batch
	 * @return
	 */
	private int calcBatchResultBoxQty(JobBatch batch) {
		String sql = "select COALESCE(count(distinct(box_id)), 0) as result from box_packs where domain_id = :domainId batch_id = :batchId";
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getId());
		return this.queryManager.selectBySql(sql, params, Integer.class);
	}
	
	/**
	 * 작업 배치의 최종 처리 주문 총 수량을 구한다.
	 * 
	 * @param batch
	 * @return
	 */
	private int calcBatchResultOrderQty(JobBatch batch) {
		String sql = "select COALESCE(count(distinct(class_cd)), 0) as result from job_instances where domain_id = :domainId batch_id = :batchId";
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getId());
		return this.queryManager.selectBySql(sql, params, Integer.class);
	}
	
	/**
	 * 작업 배치의 최종 처리 총 수량을 구한다.
	 * 
	 * @param batch
	 * @return
	 */
	private int calcBatchResultPcs(JobBatch batch) {
		String sql = "select COALESCE(sum(picked_qty), 0) as result from job_instances where domain_id = :domainId batch_id = :batchId";
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getId());
		return this.queryManager.selectBySql(sql, params, Integer.class);
	}
	
	/**
	 * 작업 배치의 최종 설비 가동율을 구한다.
	 * 
	 * @param batch
	 * @param toTime
	 * @return
	 */
	private float calcBatchEquipRuntime(JobBatch batch, Date toTime) {
		// 배치 총 시간
		long gap = toTime.getTime() - batch.getInstructedAt().getTime();
		int totalMin = ValueUtil.toInteger(gap / ValueUtil.toLong(1000 * 60));
		
		// Productivity 정보에서 10분당 실적이 0인 구간을 모두 합쳐서 시간 계산
		String sql = this.wcsQueryStore.getBatchEquipmentIdleTime();
		int idleMin = this.queryManager.selectBySql(sql, ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getId()), Integer.class);
		
		// duration에서 일하지 않은 총 시간을 빼서 실제 가동 시간을 구함
		return ValueUtil.toFloat(totalMin - idleMin);
	}
	
	/**
	 * 작업 배치의 최종 시간당 생산성을 구한다.
	 * 
	 * @param batch
	 * @param toTime
	 * @return
	 */
	private float calcBatchUph(JobBatch batch, Date toTime) {
		long duration = toTime.getTime() - batch.getInstructedAt().getTime();
		int pcs = batch.getResultPcs();
		return ValueUtil.toFloat(ValueUtil.toFloat(pcs * 1000 * 60 * 60) / ValueUtil.toFloat(duration));
	}

}
