package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;

/**
 * 작업자 투입 실적
 * 
 * @author shortstop
 */
@Table(name = "worker_actuals", idStrategy = GenerationRule.UUID, indexes = {
	@Index(name = "ix_worker_actuals_0", columnList = "domain_id,worker_id,job_date,work_type"),
	@Index(name = "ix_worker_actuals_1", columnList = "domain_id,worker_id,equip_type,equip_cd"),
	@Index(name = "ix_worker_actuals_2", columnList = "domain_id,worker_id,started_at,finished_at")
})
public class WorkerActual extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 246666318068713102L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "worker_id", length = 32)
	private String workerId;
	
	@Column (name = "worker_name", length = 30)
	private String workerName;

	@Column (name = "work_type", length = 20)
	private String workType;
	
	@Column (name = "job_date", length = 10)
	private String jobDate;

	@Column (name = "equip_type", length = 20)
	private String equipType;

	@Column (name = "equip_cd", length = 30)
	private String equipCd;

	@Column (name = "actual_qty", length = 12)
	private Integer actualQty;

	@Column (name = "started_at", length = 20)
	private String startedAt;

	@Column (name = "finished_at", length = 30)
	private String finishedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getJobDate() {
		return jobDate;
	}

	public void setJobDate(String jobDate) {
		this.jobDate = jobDate;
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

	public Integer getActualQty() {
		return actualQty;
	}

	public void setActualQty(Integer actualQty) {
		this.actualQty = actualQty;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public String getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(String finishedAt) {
		this.finishedAt = finishedAt;
	}

}
