package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;

/**
 * 10분대 실적 서머리
 * 
 * @author shortstop
 */
@Table(name = "productivity", idStrategy = GenerationRule.UUID, uniqueFields="domainId,jobDate,batchId,stationCd,jobHour", indexes = {
	@Index(name = "ix_productivity_0", columnList = "domain_id,job_date,batch_id,station_cd,job_hour", unique = true),
	@Index(name = "ix_productivity_1", columnList = "domain_id,job_date,job_hour"),
	@Index(name = "ix_productivity_2", columnList = "domain_id,job_date,area_cd,stage_cd,equip_type,equip_cd")
})
public class Productivity extends xyz.elidom.orm.entity.basic.DomainTimeStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 771359824025937966L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "job_date", nullable = false, length = 10)
	private String jobDate;

	@Column (name = "batch_id", nullable = false, length = 40)
	private String batchId;

	@Column (name = "job_type", length = 20)
	private String jobType;

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

	@Column (name = "job_hour", nullable = false, length = 2)
	private String jobHour;
	
	@Column (name = "input_workers", length = 12)
	private Integer inputWorkers;
	
	@Column (name = "total_workers", length = 12)
	private Integer totalWorkers;

	@Column (name = "m10_result", length = 12)
	private Integer m10Result;

	@Column (name = "m20_result", length = 12)
	private Integer m20Result;

	@Column (name = "m30_result", length = 12)
	private Integer m30Result;

	@Column (name = "m40_result", length = 12)
	private Integer m40Result;

	@Column (name = "m50_result", length = 12)
	private Integer m50Result;

	@Column (name = "m60_result", length = 12)
	private Integer m60Result;
	
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

	public String getJobDate() {
		return jobDate;
	}

	public void setJobDate(String jobDate) {
		this.jobDate = jobDate;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
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

	public String getJobHour() {
		return jobHour;
	}

	public void setJobHour(String jobHour) {
		this.jobHour = jobHour;
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

	public Integer getM10Result() {
		return m10Result;
	}

	public void setM10Result(Integer m10Result) {
		this.m10Result = m10Result;
	}

	public Integer getM20Result() {
		return m20Result;
	}

	public void setM20Result(Integer m20Result) {
		this.m20Result = m20Result;
	}

	public Integer getM30Result() {
		return m30Result;
	}

	public void setM30Result(Integer m30Result) {
		this.m30Result = m30Result;
	}

	public Integer getM40Result() {
		return m40Result;
	}

	public void setM40Result(Integer m40Result) {
		this.m40Result = m40Result;
	}

	public Integer getM50Result() {
		return m50Result;
	}

	public void setM50Result(Integer m50Result) {
		this.m50Result = m50Result;
	}

	public Integer getM60Result() {
		return m60Result;
	}

	public void setM60Result(Integer m60Result) {
		this.m60Result = m60Result;
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
