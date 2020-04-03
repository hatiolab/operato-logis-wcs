package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;

@Table(name = "productivity", idStrategy = GenerationRule.UUID, uniqueFields="domainId,jobDate,batchId,stationCd,workerId,jobHour", indexes = {
	@Index(name = "ix_productivity_0", columnList = "domain_id,job_date,batch_id,station_cd,worker_id,job_hour", unique = true),
	@Index(name = "ix_productivity_1", columnList = "domain_id,job_date,job_hour"),
	@Index(name = "ix_productivity_2", columnList = "domain_id,job_date,area_cd,stage_cd,equip_type,equip_cd")
})
public class Productivity extends xyz.elidom.orm.entity.basic.ElidomStampHook {
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

	@Column (name = "equip_type", length = 20)
	private String equipType;

	@Column (name = "equip_cd", length = 30)
	private String equipCd;

	@Column (name = "station_cd", nullable = false, length = 30)
	private String stationCd;

	@Column (name = "worker_id", nullable = false, length = 32)
	private String workerId;
	
	@Column (name = "attr01", length = 40)
	private String attr01;
	
	@Column (name = "attr02", length = 40)
	private String attr02;
	
	@Column (name = "attr03", length = 40)
	private String attr03;

	@Column (name = "job_hour", nullable = false, length = 2)
	private String jobHour;

	@Column (name = "min_result_10", length = 12)
	private Integer minResult10;

	@Column (name = "min_result_20", length = 12)
	private Integer minResult20;

	@Column (name = "min_result_30", length = 12)
	private Integer minResult30;

	@Column (name = "min_result_40", length = 12)
	private Integer minResult40;

	@Column (name = "min_result_50", length = 12)
	private Integer minResult50;

	@Column (name = "min_result_60", length = 12)
	private Integer minResult60;
  
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

	public String getJobHour() {
		return jobHour;
	}

	public void setJobHour(String jobHour) {
		this.jobHour = jobHour;
	}

	public Integer getMinResult10() {
		return minResult10;
	}

	public void setMinResult10(Integer minResult10) {
		this.minResult10 = minResult10;
	}

	public Integer getMinResult20() {
		return minResult20;
	}

	public void setMinResult20(Integer minResult20) {
		this.minResult20 = minResult20;
	}

	public Integer getMinResult30() {
		return minResult30;
	}

	public void setMinResult30(Integer minResult30) {
		this.minResult30 = minResult30;
	}

	public Integer getMinResult40() {
		return minResult40;
	}

	public void setMinResult40(Integer minResult40) {
		this.minResult40 = minResult40;
	}

	public Integer getMinResult50() {
		return minResult50;
	}

	public void setMinResult50(Integer minResult50) {
		this.minResult50 = minResult50;
	}

	public Integer getMinResult60() {
		return minResult60;
	}

	public void setMinResult60(Integer minResult60) {
		this.minResult60 = minResult60;
	}	
}
