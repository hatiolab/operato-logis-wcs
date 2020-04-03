package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;

@Table(name = "hourly_productivity", idStrategy = GenerationRule.UUID, uniqueFields="domainId,jobDate,stationCd,workerId,batchId", indexes = {
	@Index(name = "ix_hourly_productivity_0", columnList = "domain_id,job_date,station_cd,worker_id,batch_id", unique = true),
	@Index(name = "ix_hourly_productivity_1", columnList = "domain_id,job_date"),
	@Index(name = "ix_hourly_productivity_2", columnList = "domain_id,year,month,day"),
	@Index(name = "ix_hourly_productivity_3", columnList = "domain_id,year,month,day,area_cd,stage_cd,equip_type,equip_cd,job_type"),
	@Index(name = "ix_hourly_productivity_4", columnList = "domain_id,job_date,area_cd,worker_id,equip_type,equip_cd,job_type")
})
public class HourlyProductivity extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 839819688546808534L;

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

	@Column (name = "equip_type", nullable = false, length = 20)
	private String equipType;

	@Column (name = "equip_cd", nullable = false, length = 30)
	private String equipCd;

	@Column (name = "station_cd", nullable = false, length = 30)
	private String stationCd;

	@Column (name = "worker_id", nullable = false, length = 32)
	private String workerId;

	@Column (name = "job_type", nullable = false, length = 20)
	private String jobType;

	@Column (name = "batch_id", nullable = false, length = 40)
	private String batchId;

	@Column (name = "hr_result_01", length = 2)
	private String hrResult01;

	@Column (name = "hr_result_02", length = 12)
	private Integer hrResult02;

	@Column (name = "hr_result_03", length = 12)
	private Integer hrResult03;

	@Column (name = "hr_result_04", length = 12)
	private Integer hrResult04;

	@Column (name = "hr_result_05", length = 12)
	private Integer hrResult05;

	@Column (name = "hr_result_06", length = 12)
	private Integer hrResult06;

	@Column (name = "hr_result_07", length = 12)
	private Integer hrResult07;

	@Column (name = "hr_result_08", length = 12)
	private Integer hrResult08;

	@Column (name = "hr_result_09", length = 12)
	private Integer hrResult09;

	@Column (name = "hr_result_10", length = 12)
	private Integer hrResult10;

	@Column (name = "hr_result_11", length = 12)
	private Integer hrResult11;

	@Column (name = "hr_result_12", length = 12)
	private Integer hrResult12;

	@Column (name = "hr_result_13", length = 12)
	private Integer hrResult13;

	@Column (name = "hr_result_14", length = 12)
	private Integer hrResult14;

	@Column (name = "hr_result_15", length = 12)
	private Integer hrResult15;

	@Column (name = "hr_result_16", length = 12)
	private Integer hrResult16;

	@Column (name = "hr_result_17", length = 12)
	private Integer hrResult17;

	@Column (name = "hr_result_18", length = 12)
	private Integer hrResult18;

	@Column (name = "hr_result_19", length = 12)
	private Integer hrResult19;

	@Column (name = "hr_result_20", length = 12)
	private Integer hrResult20;

	@Column (name = "hr_result_21", length = 12)
	private Integer hrResult21;

	@Column (name = "hr_result_22", length = 12)
	private Integer hrResult22;

	@Column (name = "hr_result_23", length = 12)
	private Integer hrResult23;

	@Column (name = "hr_result_24", length = 12)
	private Integer hrResult24;

	@Column (name = "plan_qty", length = 12)
	private Integer planQty;

	@Column (name = "result_qty", length = 12)
	private Integer resultQty;

	@Column (name = "left_qty", length = 12)
	private Integer leftQty;

	@Column (name = "uph", length = 19)
	private Float uph;
  
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

	public String getHrResult01() {
		return hrResult01;
	}

	public void setHrResult01(String hrResult01) {
		this.hrResult01 = hrResult01;
	}

	public Integer getHrResult02() {
		return hrResult02;
	}

	public void setHrResult02(Integer hrResult02) {
		this.hrResult02 = hrResult02;
	}

	public Integer getHrResult03() {
		return hrResult03;
	}

	public void setHrResult03(Integer hrResult03) {
		this.hrResult03 = hrResult03;
	}

	public Integer getHrResult04() {
		return hrResult04;
	}

	public void setHrResult04(Integer hrResult04) {
		this.hrResult04 = hrResult04;
	}

	public Integer getHrResult05() {
		return hrResult05;
	}

	public void setHrResult05(Integer hrResult05) {
		this.hrResult05 = hrResult05;
	}

	public Integer getHrResult06() {
		return hrResult06;
	}

	public void setHrResult06(Integer hrResult06) {
		this.hrResult06 = hrResult06;
	}

	public Integer getHrResult07() {
		return hrResult07;
	}

	public void setHrResult07(Integer hrResult07) {
		this.hrResult07 = hrResult07;
	}

	public Integer getHrResult08() {
		return hrResult08;
	}

	public void setHrResult08(Integer hrResult08) {
		this.hrResult08 = hrResult08;
	}

	public Integer getHrResult09() {
		return hrResult09;
	}

	public void setHrResult09(Integer hrResult09) {
		this.hrResult09 = hrResult09;
	}

	public Integer getHrResult10() {
		return hrResult10;
	}

	public void setHrResult10(Integer hrResult10) {
		this.hrResult10 = hrResult10;
	}

	public Integer getHrResult11() {
		return hrResult11;
	}

	public void setHrResult11(Integer hrResult11) {
		this.hrResult11 = hrResult11;
	}

	public Integer getHrResult12() {
		return hrResult12;
	}

	public void setHrResult12(Integer hrResult12) {
		this.hrResult12 = hrResult12;
	}

	public Integer getHrResult13() {
		return hrResult13;
	}

	public void setHrResult13(Integer hrResult13) {
		this.hrResult13 = hrResult13;
	}

	public Integer getHrResult14() {
		return hrResult14;
	}

	public void setHrResult14(Integer hrResult14) {
		this.hrResult14 = hrResult14;
	}

	public Integer getHrResult15() {
		return hrResult15;
	}

	public void setHrResult15(Integer hrResult15) {
		this.hrResult15 = hrResult15;
	}

	public Integer getHrResult16() {
		return hrResult16;
	}

	public void setHrResult16(Integer hrResult16) {
		this.hrResult16 = hrResult16;
	}

	public Integer getHrResult17() {
		return hrResult17;
	}

	public void setHrResult17(Integer hrResult17) {
		this.hrResult17 = hrResult17;
	}

	public Integer getHrResult18() {
		return hrResult18;
	}

	public void setHrResult18(Integer hrResult18) {
		this.hrResult18 = hrResult18;
	}

	public Integer getHrResult19() {
		return hrResult19;
	}

	public void setHrResult19(Integer hrResult19) {
		this.hrResult19 = hrResult19;
	}

	public Integer getHrResult20() {
		return hrResult20;
	}

	public void setHrResult20(Integer hrResult20) {
		this.hrResult20 = hrResult20;
	}

	public Integer getHrResult21() {
		return hrResult21;
	}

	public void setHrResult21(Integer hrResult21) {
		this.hrResult21 = hrResult21;
	}

	public Integer getHrResult22() {
		return hrResult22;
	}

	public void setHrResult22(Integer hrResult22) {
		this.hrResult22 = hrResult22;
	}

	public Integer getHrResult23() {
		return hrResult23;
	}

	public void setHrResult23(Integer hrResult23) {
		this.hrResult23 = hrResult23;
	}

	public Integer getHrResult24() {
		return hrResult24;
	}

	public void setHrResult24(Integer hrResult24) {
		this.hrResult24 = hrResult24;
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

	public Float getUph() {
		return uph;
	}

	public void setUph(Float uph) {
		this.uph = uph;
	}	
}
