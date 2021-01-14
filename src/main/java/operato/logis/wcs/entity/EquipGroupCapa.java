package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.Table;

/**
 * 설비 그룹 Capacity
 * 
 * @author shortstop
 */
@Table(name = "equip_group_capa", idStrategy = GenerationRule.UUID, uniqueFields="domainId,equipGroupId,workerCount", indexes = {
	@Index(name = "ix_equip_group_capa_0", columnList = "domain_id,equip_group_id,worker_count", unique = true)
})
public class EquipGroupCapa extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 175517288720818958L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "equip_group_id", nullable = false, length = 40)
	private String equipGroupId;

	@Column (name = "worker_count", length = 12)
	private Integer workerCount;

	@Column (name = "man_uph", length = 19)
	private Float manUph;

	@Column (name = "daily_capa", length = 19)
	private Float dailyCapa;
  
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEquipGroupId() {
		return equipGroupId;
	}

	public void setEquipGroupId(String equipGroupId) {
		this.equipGroupId = equipGroupId;
	}

	public Integer getWorkerCount() {
		return workerCount;
	}

	public void setWorkerCount(Integer workerCount) {
		this.workerCount = workerCount;
	}

	public Float getManUph() {
		return manUph;
	}

	public void setManUph(Float manUph) {
		this.manUph = manUph;
	}

	public Float getDailyCapa() {
		return dailyCapa;
	}

	public void setDailyCapa(Float dailyCapa) {
		this.dailyCapa = dailyCapa;
	}	
}
