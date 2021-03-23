package operato.logis.wcs.query;

import org.springframework.stereotype.Component;

import xyz.anythings.sys.service.AbstractQueryStore;
import xyz.elidom.sys.SysConstants;

/**
 * WCS 쿼리
 * 
 * @author shortstop
 */
@Component
public class WcsQueryStore extends AbstractQueryStore {

	@Override
	public void initQueryStore(String databaseType) {
		this.databaseType = databaseType;
		this.basePath = "operato/logis/wcs/query/" + this.databaseType + SysConstants.SLASH;
		this.defaultBasePath = "operato/logis/wcs/query/ansi/";
	}
	
	/**
	 * BatchReceipt 조회
	 * 상세 Item 에 Order 타입이 있는 Case
	 * 
	 * @return
	 */
	public String getBatchReceiptOrderTypeStatusQuery() {
		return this.getQueryByPath("batch/BatchReceiptOrderTypeStatus");
	}
	
	/**
	 * WMS I/F 테이블로 부터 반품 BatchReceipt 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToReceiptDataQuery() {
		return this.getQueryByPath("batch/WmsIfToReceiptData");
	}
	
	/**
	 * WMS I/F 테이블로 부터  주문수신 완료된 데이터 변경('Y')
	 * 
	 * @return
	 */
	public String getWmsIfToReceiptUpdateQuery() {
		return this.getQueryByPath("batch/WmsIfToReceiptUpdate");
	}
	
	/**
	 * 작업 배치의 설비 유휴 시간 조회 계산
	 * 
	 * @return
	 */
	public String getBatchEquipmentIdleTime() {
		return this.getQueryByPath("batch/EquipmentIdleTime");
	}
	
	/**
	 * 배치별 10분대 실적 조회 쿼리
	 * 
	 * @return
	 */
	public String getFind10MinProductivityQuery() {
		return this.getQueryByPath("summary/Find10MinProductivity");
	}
	
	/**
	 * 배치별 10분대 실적 조회 쿼리
	 * 
	 * @return
	 */
	public String getSearch10MinProductivityQuery() {
		return this.getQueryByPath("summary/Search10MinProductivity");
	}

}
