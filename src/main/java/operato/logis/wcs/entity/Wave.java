package operato.logis.wcs.entity;

import java.util.Date; 
import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.Table;

@Table(name = "waves", idStrategy = GenerationRule.UUID, uniqueFields="domainId,jobDate,jobSeq", indexes = {
	@Index(name = "ix_wave_0", columnList = "domain_id,job_date,job_seq", unique = true),
	@Index(name = "ix_wave_1", columnList = "domain_id,wave_no"),
	@Index(name = "ix_wave_2", columnList = "domain_id,com_cd"),
	@Index(name = "ix_wave_3", columnList = "domain_id,equip_group_cd"),
	@Index(name = "ix_wave_4", columnList = "domain_id,status")
})
public class Wave extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 505358554632371456L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "wave_no", nullable = false, length = 40)
	private String waveNo;

	@Column (name = "com_cd", nullable = false, length = 30)
	private String comCd;

	@Column (name = "job_date", nullable = false, length = 10)
	private String jobDate;

	@Column (name = "job_seq", length = 10)
	private String jobSeq;

	@Column (name = "equip_group_cd", length = 30)
	private String equipGroupCd;

	@Column (name = "order_qty", nullable = false, length = 12)
	private Integer orderQty;

	@Column (name = "sku_qty", length = 12)
	private Integer skuQty;

	@Column (name = "total_pcs", nullable = false, length = 12)
	private Integer totalPcs;

	@Column (name = "confirmed_at", type = xyz.elidom.dbist.annotation.ColumnType.DATETIME)
	private Date confirmedAt;

	@Column (name = "status", length = 10)
	private String status;
	
	@Column (name = "progress_rate", length = 12)
	private Float progressRate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWaveNo() {
		return waveNo;
	}

	public void setWaveNo(String waveNo) {
		this.waveNo = waveNo;
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

	public String getJobSeq() {
		return jobSeq;
	}

	public void setJobSeq(String jobSeq) {
		this.jobSeq = jobSeq;
	}

	public String getEquipGroupCd() {
		return equipGroupCd;
	}

	public void setEquipGroupCd(String equipGroupCd) {
		this.equipGroupCd = equipGroupCd;
	}

	public Integer getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(Integer orderQty) {
		this.orderQty = orderQty;
	}

	public Integer getSkuQty() {
		return skuQty;
	}

	public void setSkuQty(Integer skuQty) {
		this.skuQty = skuQty;
	}

	public Integer getTotalPcs() {
		return totalPcs;
	}

	public void setTotalPcs(Integer totalPcs) {
		this.totalPcs = totalPcs;
	}

	public Date getConfirmedAt() {
		return confirmedAt;
	}

	public void setConfirmedAt(Date confirmedAt) {
		this.confirmedAt = confirmedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Float getProgressRate() {
		return progressRate;
	}

	public void setProgressRate(Float progressRate) {
		this.progressRate = progressRate;
	}

}
