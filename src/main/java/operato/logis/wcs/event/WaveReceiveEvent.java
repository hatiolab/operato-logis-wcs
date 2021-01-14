package operato.logis.wcs.event;

import xyz.anythings.base.entity.BatchReceipt;
import xyz.anythings.base.event.main.BatchRootEvent;

/**
 * Wave 수신 이벤트
 * 
 * @author shortstop
 */
public class WaveReceiveEvent extends BatchRootEvent {

	/**
	 * 10 : EVENT_RECEIVE_TYPE_RECEIPT
	 * 20 : EVENT_RECEIVE_TYPE_RECEIVE
	 * 30 : EVENT_RECEIVE_TYPE_CANCEL
	 */
	private short eventType;
	/**
	 * Area 코드 
	 */
	protected String areaCd;
	/**
	 * 화주 코드 
	 */
	protected String comCd;
	/**
	 * 작업 일자 
	 */
	protected String jobDate;
	/**
	 * 리셉트 데이터 
	 */
	private BatchReceipt receiptData;
	
	/**
	 * 이벤트 생성자 1
	 * 
	 * @param domainId
	 * @param eventType
	 * @param eventStep
	 */
	public WaveReceiveEvent(long domainId, short eventType, short eventStep) {
		super(domainId, eventStep);
		
		this.setEventType(eventType);
	}
	
	/**
	 * 이벤트 생성자 2
	 * 
	 * @param domainId
	 * @param eventType
	 * @param eventStep
	 * @param areaCd
	 * @param stageCd
	 * @param jobDate
	 * @param comCd
	 */
	public WaveReceiveEvent(long domainId, short eventType, short eventStep, String areaCd, String stageCd, String jobDate, String comCd) {
		this(domainId, eventStep, eventType);
		
		this.setAreaCd(areaCd);
		this.setStageCd(stageCd);
		this.setJobDate(jobDate);
		this.setComCd(comCd);
	}

	public short getEventType() {
		return eventType;
	}

	public void setEventType(short eventType) {
		this.eventType = eventType;
	}
	
	public void setReceiptData(BatchReceipt receiptData) {
		this.receiptData = receiptData;
	}
	
	public BatchReceipt getReceiptData() {
		return receiptData;
	}

	public String getAreaCd() {
		return areaCd;
	}

	public void setAreaCd(String areaCd) {
		this.areaCd = areaCd;
	}

	public String getComCd() {
		return comCd;
	}

	public void setComCd(String comCd) {
		this.comCd = comCd;
	}

	public String getJobDate() {
		return jobDate;
	}

	public void setJobDate(String jobDate) {
		this.jobDate = jobDate;
	}
	
}
