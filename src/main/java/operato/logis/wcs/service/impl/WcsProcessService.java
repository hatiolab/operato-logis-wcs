package operato.logis.wcs.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import operato.logis.wcs.event.WaveReceiveEvent;
import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.BatchReceipt;
import xyz.anythings.base.entity.BatchReceiptItem;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.entity.Order;
import xyz.anythings.base.entity.OrderPreprocess;
import xyz.anythings.base.service.util.BatchJobConfigUtil;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.anythings.sys.service.ICustomService;
import xyz.anythings.sys.util.AnyEntityUtil;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.exception.server.ElidomRuntimeException;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

/**
 * WCS Wave 수신용 서비스
 * 
 * @author shortstop
 */
@Component
public class WcsProcessService extends AbstractQueryService {
	
	/**
	 * Wave 수신을 위한 정보 조회 커스텀 서비스
	 */
	public static final String READY_TO_RECEIVE_WAVE = "ready-to-receive-wave";
	/**
	 * Wave 수신 커스텀 서비스
	 */
	public static final String START_TO_RECEIVE_WAVE = "start-to-receive-wave";
	/**
	 * 커스텀 서비스
	 */
	@Autowired
	protected ICustomService customService;
	
	/**
	 * Wave 수신을 위한 수신 서머리 정보 조회
	 *  
	 * @param event
	 */
	@EventListener(classes = WaveReceiveEvent.class, condition = "#event.eventType == 10 and #event.eventStep == 1")
	public void handleReadyToReceiveWave(WaveReceiveEvent event) { 
		BatchReceipt receipt = event.getReceiptData();
		receipt = this.readyToReceiveWave(receipt);
		event.setReceiptData(receipt);
	}
	
	/**
	 * Wave 수신 시작
	 * 
	 * @param event
	 */
	@EventListener(classes = WaveReceiveEvent.class, condition = "#event.eventType == 20 and #event.eventStep == 1")
	public void handleStartToReceiveWave(WaveReceiveEvent event) {
		BatchReceipt receipt = event.getReceiptData();
		List<BatchReceiptItem> items = receipt.getItems();
		WcsProcessService self = BeanUtil.get(WcsProcessService.class);
		
		for(BatchReceiptItem item : items) {
			if(!item.getSkipFlag()) {
				self.startToReceiveWave(receipt, item);
			}
		}
	}
	
	/**
	 * Wave 수신 취소
	 * 
	 * @param event
	 */
	@EventListener(classes = WaveReceiveEvent.class, condition = "#event.eventType == 30 and #event.eventStep == 1")
	public void handleCancelReceived(WaveReceiveEvent event) {
		// 1. 작업 배치 추출 
		JobBatch batch = event.getJobBatch();
		
		// 2. 배치 상태 체크
		String sql = "select status from job_batches where domain_id = :domainId and id = :id";
		Map<String, Object> params = ValueUtil.newMap("domainId,id", batch.getDomainId(), batch.getId());
		String currentStatus = AnyEntityUtil.findItem(batch.getDomainId(), true, String.class, sql, params);
		
		// 3. Wave 상태 체크
		if(ValueUtil.isNotEmpty(currentStatus)) {
			throw new ElidomRuntimeException("Wave 상태가 null인 경우에만 취소가 가능합니다.");
		}
		
		// 4. Wave 취소 체크
		if(ValueUtil.isEqualIgnoreCase(currentStatus, JobBatch.STATUS_CANCEL)) {
			throw new ElidomRuntimeException("Wave가 이미 취소되었습니다.");
		}
		
		// 3. 주문 취소시 데이터 유지 여부에 따라서
		boolean isKeepData = BatchJobConfigUtil.isDeleteWhenOrderCancel(batch);
		int cancelledCnt = isKeepData ? this.cancelOrderKeepData(batch) : this.cancelOrderDeleteData(batch);
		event.setResult(cancelledCnt);
	}
	
	/**
	 * Wave 대상 분류
	 * 
	 * @param mainBatch
	 * @param classifyCodes
	 * @return
	 */
	public List<JobBatch> classifyWaves(JobBatch mainBatch, List<String> classifyCodes) {
		// TODO
		return null;
	}
	
	/**
	 * Wave 분할 개수로 균등 분할
	 * 
	 * @param mainBatch
	 * @param splitCount
	 * @return
	 */
	public List<JobBatch> splitWavesByEvenly(JobBatch mainBatch, int splitCount) {
		// TODO
		return null;
	}
	
	/**
	 * Wave 주문 수량으로 분할
	 * 
	 * @param mainBatch
	 * @param splitOrderQty
	 * @return
	 */
	public List<JobBatch> splitWavesByOrderQty(JobBatch mainBatch, int splitOrderQty) {
		// TODO
		return null;
	}
	
	/**
	 * Wave 병합
	 * 
	 * @param mainBatch
	 * @param targetBatch
	 * @return
	 */
	public JobBatch mergeWave(JobBatch mainBatch, JobBatch targetBatch) {
		// TODO
		return null;
	}
	
	/**
	 * Wave 확정 & 설비 전송
	 * 
	 * @param batch
	 * @return
	 */
	public JobBatch confirmWave(JobBatch batch) {
		// TODO
		return null;
	}
	
	/**
	 * 상위 Legacy 시스템으로 부터 수신할 Wave 조회
	 * 상위 Legacy 시스템은 정해지지가 않았으므로 커스텀 서비스로 구현한다.
	 * 
	 * @param receipt
	 * @param params
	 * @return
	 */
	private BatchReceipt readyToReceiveWave(BatchReceipt receipt, Object ... params) {
		// 1. 파라미터 설정 및 커스텀 서비스 호출
		Map<String, Object> parameters = ValueUtil.newMap("condition", receipt);
		this.customService.doCustomService(receipt.getDomainId(), READY_TO_RECEIVE_WAVE, parameters);
		List<BatchReceiptItem> receiptItems = receipt.getItems();
		
		// 2 수신 아이템 데이터 생성
		for(BatchReceiptItem item : receiptItems) {
			item.setBatchReceiptId(receipt.getId());
			receipt.addItem(item);
		}
		
		// 3. 수신 아이템 설정 및 리턴
		return receipt;
	}
	
	/**
	 * Wave 수신
	 * 
	 * @param receipt
	 * @param item
	 * @param params
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private BatchReceipt startToReceiveWave(BatchReceipt receipt, BatchReceiptItem item, Object ... params) {
		try {
			Map<String, Object> parameters = ValueUtil.newMap("condition", item);
			this.customService.doCustomService(receipt.getDomainId(), READY_TO_RECEIVE_WAVE, parameters);
			
		} catch(Throwable th) {
			String errMsg = th.getCause() != null ? th.getCause().getMessage() : th.getMessage();
			errMsg = errMsg.length() > 400 ? errMsg.substring(0,400) : errMsg;
			item.setStatus(LogisConstants.COMMON_STATUS_ERROR);
			item.setMessage(errMsg);
			receipt.setStatus(LogisConstants.COMMON_STATUS_ERROR);
		}

		return receipt;
	}
	
	/**
	 * 주문 데이터 삭제 update
	 * 
	 * seq = 0
	 * @param batch
	 * @return
	 */
	private int cancelOrderKeepData(JobBatch batch) {
		int cnt = 0;
		
		// 1. 배치 상태  update
		batch.updateStatus(JobBatch.STATUS_CANCEL);
		
		// 2. 주문 조회
		List<Order> orderList = AnyEntityUtil.searchEntitiesBy(batch.getDomainId(), false, Order.class, "id", "batchId", batch.getId());
		
		// 3. 취소 상태 , seq = 0 셋팅
		for(Order order : orderList) {
			order.setStatus(Order.STATUS_CANCEL);
			order.setJobSeq("0");
		}
		
		// 4. 배치 update
		this.queryManager.updateBatch(orderList, "jobSeq", "status");
		cnt += orderList.size();
		
		// 5. 주문 가공 데이터 삭제
		cnt += this.deleteBatchPreprocessData(batch);
		return cnt;
	}
	
	/**
	 * 주문 데이터 삭제
	 * 
	 * @param batch
	 * @return
	 */
	private int cancelOrderDeleteData(JobBatch batch) {
		int cnt = 0;
		
		// 1. 삭제 조건 생성
		Query condition = AnyOrmUtil.newConditionForExecution(batch.getDomainId());
		condition.addFilter("batchId", batch.getId());
		
		// 2. 삭제 실행
		cnt+= this.queryManager.deleteList(Order.class, condition);
		
		// 3. 주문 가공 데이터 삭제
		cnt += this.deleteBatchPreprocessData(batch);
		
		// 4. 배치 삭제
		this.queryManager.delete(batch);
		
		return cnt;
	}
	
	/**
	 * 주문 가공 데이터 삭제
	 * 
	 * @param batch
	 * @return
	 */
	private int deleteBatchPreprocessData(JobBatch batch) {
		// 1. 삭제 조건 생성
		Query condition = AnyOrmUtil.newConditionForExecution(batch.getDomainId());
		condition.addFilter("batchId", batch.getId());
		
		// 2. 삭제 실행
		return this.queryManager.deleteList(OrderPreprocess.class, condition);
	}

}
