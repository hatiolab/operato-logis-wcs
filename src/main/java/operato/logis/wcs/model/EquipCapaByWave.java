package operato.logis.wcs.model;

/**
 * Wave별 설비 가용성 정보
 * 
 * @author shortstop
 *
 */
public class EquipCapaByWave {
	/**
	 * 설비 그룹 코드
	 */
	private String equipGroupCd;
	/**
	 * 설비 상태
	 */
	private String equipStatus;
	/**
	 * 설비 가용 셀 개수
	 */
	private Float equipCellCapa;
	/**
	 * 설비 Capa / Hr
	 */
	private Float equipCapa;
	/**
	 * 투입 인력
	 */
	private Integer inputWorkers;
	/**
	 * 현재 설비에 진행 중인 작업의 완료 예상 시간 (분)
	 */
	private Float runningExpEndTime;
	/**
	 * Wave의 완료 예상 시간 (분)
	 */
	private Float waveExpEndTime;
	/**
	 * 총 작업 완료 예상 시간 (분)
	 */
	private Float totalExpEndTime;
	
	
	public String getEquipGroupCd() {
		return equipGroupCd;
	}

	public void setEquipGroupCd(String equipGroupCd) {
		this.equipGroupCd = equipGroupCd;
	}

	public String getEquipStatus() {
		return equipStatus;
	}
	
	public void setEquipStatus(String equipStatus) {
		this.equipStatus = equipStatus;
	}
	
	public Float getEquipCellCapa() {
		return equipCellCapa;
	}
	
	public void setEquipCellCapa(Float equipCellCapa) {
		this.equipCellCapa = equipCellCapa;
	}
	
	public Float getEquipCapa() {
		return equipCapa;
	}
	
	public void setEquipCapa(Float equipCapa) {
		this.equipCapa = equipCapa;
	}
	
	public Integer getInputWorkers() {
		return inputWorkers;
	}
	
	public void setInputWorkers(Integer inputWorkers) {
		this.inputWorkers = inputWorkers;
	}
	
	public Float getRunningExpEndTime() {
		return runningExpEndTime;
	}
	
	public void setRunningExpEndTime(Float runningExpEndTime) {
		this.runningExpEndTime = runningExpEndTime;
	}
	
	public Float getWaveExpEndTime() {
		return waveExpEndTime;
	}
	
	public void setWaveExpEndTime(Float waveExpEndTime) {
		this.waveExpEndTime = waveExpEndTime;
	}
	
	public Float getTotalExpEndTime() {
		return totalExpEndTime;
	}

	public void setTotalExpEndTime(Float totalExpEndTime) {
		this.totalExpEndTime = totalExpEndTime;
	}

}
