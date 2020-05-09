package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;

@Table(name = "daily_prod_summary", idStrategy = GenerationRule.UUID, uniqueFields="domainId,jobDate,stationCd,batchId", indexes = {
	@Index(name = "ix_daily_prod_summary_0", columnList = "domain_id,job_date,station_cd,batch_id", unique = true),
	@Index(name = "ix_daily_prod_summary_1", columnList = "domain_id,job_date"),
	@Index(name = "ix_daily_prod_summary_2", columnList = "domain_id,year,month,day"),
	@Index(name = "ix_daily_prod_summary_3", columnList = "domain_id,year,month,day,area_cd,stage_cd,equip_type,equip_cd,job_type"),
	@Index(name = "ix_daily_prod_summary_4", columnList = "domain_id,job_date,area_cd,worker_id,equip_type,equip_cd,job_type")

})
public class DailyProdSummary extends xyz.elidom.orm.entity.basic.DomainTimeStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 425796427498795393L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "year", nullable = false, length = 4)
	private String year;

	@Column (name = "month", nullable = false, length = 2)
	private String month;

	@Column (name = "day", nullable = false, length = 2)
	private String day;

	@Column (name = "job_date", nullable = false, length = 10)
	private String jobDate;

	@Column (name = "area_cd", length = 30)
	private String areaCd;

	@Column (name = "stage_cd", length = 30)
	private String stageCd;
	
	@Column (name = "equip_group_cd", length = 30)
	private String equipGroupCd;

	@Column (name = "equip_type", length = 20)
	private String equipType;

	@Column (name = "equip_cd", length = 30)
	private String equipCd;

	@Column (name = "station_cd", length = 30)
	private String stationCd;

	@Column (name = "worker_id", length = 32)
	private String workerId;

	@Column (name = "job_type", length = 20)
	private String jobType;

	@Column (name = "batch_id", length = 40)
	private String batchId;

	@Column (name = "h01_result", length = 12)
	private Integer h01Result;

	@Column (name = "h02_result", length = 12)
	private Integer h02Result;

	@Column (name = "h03_result", length = 12)
	private Integer h03Result;

	@Column (name = "h04_result", length = 12)
	private Integer h04Result;

	@Column (name = "h05_result", length = 12)
	private Integer h05Result;

	@Column (name = "h06_result", length = 12)
	private Integer h06Result;

	@Column (name = "h07_result", length = 12)
	private Integer h07Result;

	@Column (name = "h08_result", length = 12)
	private Integer h08Result;

	@Column (name = "h09_result", length = 12)
	private Integer h09Result;

	@Column (name = "h10_result", length = 12)
	private Integer h10Result;

	@Column (name = "h11_result", length = 12)
	private Integer h11Result;

	@Column (name = "h12_result", length = 12)
	private Integer h12Result;

	@Column (name = "h13_result", length = 12)
	private Integer h13Result;

	@Column (name = "h14_result", length = 12)
	private Integer h14Result;

	@Column (name = "h15_result", length = 12)
	private Integer h15Result;

	@Column (name = "h16_result", length = 12)
	private Integer h16Result;

	@Column (name = "h17_result", length = 12)
	private Integer h17Result;

	@Column (name = "h18_result", length = 12)
	private Integer h18Result;

	@Column (name = "h19_result", length = 12)
	private Integer h19Result;

	@Column (name = "h20_result", length = 12)
	private Integer h20Result;

	@Column (name = "h21_result", length = 12)
	private Integer h21Result;

	@Column (name = "h22_result", length = 12)
	private Integer h22Result;

	@Column (name = "h23_result", length = 12)
	private Integer h23Result;

	@Column (name = "h24_result", length = 12)
	private Integer h24Result;
	
	@Column (name = "input_workers", length = 12)
	private Integer inputWorkers;
	
	@Column (name = "total_workers", length = 12)
	private Integer totalWorkers;

	@Column (name = "plan_qty", length = 12)
	private Integer planQty;

	@Column (name = "result_qty", length = 12)
	private Integer resultQty;

	@Column (name = "left_qty", length = 12)
	private Integer leftQty;

	@Column (name = "progress_rate", length = 19)
	private Float progressRate;
	
	@Column (name = "uph", length = 19)
	private Float uph;
	
	@Column (name = "equip_runtime", length = 12)
	private Float equipRuntime;
	
	@Column (name = "equip_rate", length = 19)
	private Float equipRate;

	@Column (name = "attr01", length = 40)
	private String attr01;

	@Column (name = "attr02", length = 40)
	private String attr02;

	@Column (name = "attr03", length = 40)
	private String attr03;

	@Column (name = "attr04", length = 40)
	private String attr04;

	@Column (name = "attr05", length = 40)
	private String attr05;
  
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getJobDate() {
		return jobDate;
	}

	public void setJobDate(String jobDate) {
		this.jobDate = jobDate;
	}

	public String getAreaCd() {
		return areaCd;
	}

	public void setAreaCd(String areaCd) {
		this.areaCd = areaCd;
	}

	public String getStageCd() {
		return stageCd;
	}

	public void setStageCd(String stageCd) {
		this.stageCd = stageCd;
	}
	
	public String getEquipGroupCd() {
		return equipGroupCd;
	}

	public void setEquipGroupCd(String equipGroupCd) {
		this.equipGroupCd = equipGroupCd;
	}	

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public String getEquipCd() {
		return equipCd;
	}

	public void setEquipCd(String equipCd) {
		this.equipCd = equipCd;
	}

	public String getStationCd() {
		return stationCd;
	}

	public void setStationCd(String stationCd) {
		this.stationCd = stationCd;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public Integer getH01Result() {
		return h01Result;
	}

	public void setH01Result(Integer h01Result) {
		this.h01Result = h01Result;
	}

	public Integer getH02Result() {
		return h02Result;
	}

	public void setH02Result(Integer h02Result) {
		this.h02Result = h02Result;
	}

	public Integer getH03Result() {
		return h03Result;
	}

	public void setH03Result(Integer h03Result) {
		this.h03Result = h03Result;
	}

	public Integer getH04Result() {
		return h04Result;
	}

	public void setH04Result(Integer h04Result) {
		this.h04Result = h04Result;
	}

	public Integer getH05Result() {
		return h05Result;
	}

	public void setH05Result(Integer h05Result) {
		this.h05Result = h05Result;
	}

	public Integer getH06Result() {
		return h06Result;
	}

	public void setH06Result(Integer h06Result) {
		this.h06Result = h06Result;
	}

	public Integer getH07Result() {
		return h07Result;
	}

	public void setH07Result(Integer h07Result) {
		this.h07Result = h07Result;
	}

	public Integer getH08Result() {
		return h08Result;
	}

	public void setH08Result(Integer h08Result) {
		this.h08Result = h08Result;
	}

	public Integer getH09Result() {
		return h09Result;
	}

	public void setH09Result(Integer h09Result) {
		this.h09Result = h09Result;
	}

	public Integer getH10Result() {
		return h10Result;
	}

	public void setH10Result(Integer h10Result) {
		this.h10Result = h10Result;
	}

	public Integer getH11Result() {
		return h11Result;
	}

	public void setH11Result(Integer h11Result) {
		this.h11Result = h11Result;
	}

	public Integer getH12Result() {
		return h12Result;
	}

	public void setH12Result(Integer h12Result) {
		this.h12Result = h12Result;
	}

	public Integer getH13Result() {
		return h13Result;
	}

	public void setH13Result(Integer h13Result) {
		this.h13Result = h13Result;
	}

	public Integer getH14Result() {
		return h14Result;
	}

	public void setH14Result(Integer h14Result) {
		this.h14Result = h14Result;
	}

	public Integer getH15Result() {
		return h15Result;
	}

	public void setH15Result(Integer h15Result) {
		this.h15Result = h15Result;
	}

	public Integer getH16Result() {
		return h16Result;
	}

	public void setH16Result(Integer h16Result) {
		this.h16Result = h16Result;
	}

	public Integer getH17Result() {
		return h17Result;
	}

	public void setH17Result(Integer h17Result) {
		this.h17Result = h17Result;
	}

	public Integer getH18Result() {
		return h18Result;
	}

	public void setH18Result(Integer h18Result) {
		this.h18Result = h18Result;
	}

	public Integer getH19Result() {
		return h19Result;
	}

	public void setH19Result(Integer h19Result) {
		this.h19Result = h19Result;
	}

	public Integer getH20Result() {
		return h20Result;
	}

	public void setH20Result(Integer h20Result) {
		this.h20Result = h20Result;
	}

	public Integer getH21Result() {
		return h21Result;
	}

	public void setH21Result(Integer h21Result) {
		this.h21Result = h21Result;
	}

	public Integer getH22Result() {
		return h22Result;
	}

	public void setH22Result(Integer h22Result) {
		this.h22Result = h22Result;
	}

	public Integer getH23Result() {
		return h23Result;
	}

	public void setH23Result(Integer h23Result) {
		this.h23Result = h23Result;
	}

	public Integer getH24Result() {
		return h24Result;
	}

	public void setH24Result(Integer h24Result) {
		this.h24Result = h24Result;
	}

	public Integer getInputWorkers() {
		return inputWorkers;
	}

	public void setInputWorkers(Integer inputWorkers) {
		this.inputWorkers = inputWorkers;
	}

	public Integer getTotalWorkers() {
		return totalWorkers;
	}

	public void setTotalWorkers(Integer totalWorkers) {
		this.totalWorkers = totalWorkers;
	}

	public Integer getPlanQty() {
		return planQty;
	}

	public void setPlanQty(Integer planQty) {
		this.planQty = planQty;
	}

	public Integer getResultQty() {
		return resultQty;
	}

	public void setResultQty(Integer resultQty) {
		this.resultQty = resultQty;
	}

	public Integer getLeftQty() {
		return leftQty;
	}

	public void setLeftQty(Integer leftQty) {
		this.leftQty = leftQty;
	}

	public Float getProgressRate() {
		return progressRate;
	}

	public void setProgressRate(Float progressRate) {
		this.progressRate = progressRate;
	}
	
	public Float getUph() {
		return uph;
	}

	public void setUph(Float uph) {
		this.uph = uph;
	}	

	public Float getEquipRuntime() {
		return equipRuntime;
	}

	public void setEquipRuntime(Float equipRuntime) {
		this.equipRuntime = equipRuntime;
	}

	public Float getEquipRate() {
		return equipRate;
	}

	public void setEquipRate(Float equipRate) {
		this.equipRate = equipRate;
	}

	public String getAttr01() {
		return attr01;
	}

	public void setAttr01(String attr01) {
		this.attr01 = attr01;
	}

	public String getAttr02() {
		return attr02;
	}

	public void setAttr02(String attr02) {
		this.attr02 = attr02;
	}

	public String getAttr03() {
		return attr03;
	}

	public void setAttr03(String attr03) {
		this.attr03 = attr03;
	}

	public String getAttr04() {
		return attr04;
	}

	public void setAttr04(String attr04) {
		this.attr04 = attr04;
	}

	public String getAttr05() {
		return attr05;
	}

	public void setAttr05(String attr05) {
		this.attr05 = attr05;
	}

}
