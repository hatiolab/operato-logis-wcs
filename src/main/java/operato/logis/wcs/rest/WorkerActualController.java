package operato.logis.wcs.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import operato.logis.wcs.entity.WorkerActual;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.DateUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.sys.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/worker_actuals")
@ServiceDesc(description = "WorkerActual Service API")
public class WorkerActualController extends AbstractRestService {

	@Override
	protected Class<?> entityClass() {
		return WorkerActual.class;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search (Pagination) By Search Conditions")
	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one by ID")
	public WorkerActual findOne(@PathVariable("id") String id) {
		return this.getOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create")
	public WorkerActual create(@RequestBody WorkerActual input) {
		return this.createOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update")
	public WorkerActual update(@PathVariable("id") String id, @RequestBody WorkerActual input) {
		return this.updateOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple at one time")
	public Boolean multipleUpdate(@RequestBody List<WorkerActual> list) {
		return this.cudMultipleData(this.entityClass(), list);
	}

	@RequestMapping(value = "/search/input_workers/{equip_type}/{equip_cd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one by ID")
	public List<WorkerActual> searchWorkers(@PathVariable("equip_type") String equipType, @PathVariable("equip_cd") String equipCd) {
		Map<String, Object> condition = ValueUtil.newMap("equipType,equipCd", equipType, equipCd);
		return this.queryManager.selectList(WorkerActual.class, condition);
	}
	
	@RequestMapping(value = "/input_worker", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Input worker to equipment")
	public WorkerActual inputWorker(@RequestBody WorkerActual input) {
		if(ValueUtil.isEmpty(input.getWorkerId())) {
			throw ThrowUtil.newNotAllowedEmptyInfo("terms.label.worker_id");
		}
		
		if(ValueUtil.isEmpty(input.getWorkerName())) {
			throw ThrowUtil.newNotAllowedEmptyInfo("terms.label.worker_name");
		}
		
		// 기본값은 'A' 분류 작업
		if(ValueUtil.isEmpty(input.getWorkType())) {
			input.setWorkType("A");
		}
		
		if(ValueUtil.isEmpty(input.getEquipType())) {
			throw ThrowUtil.newNotAllowedEmptyInfo("terms.label.equip_type");
		}
		
		if(ValueUtil.isEmpty(input.getEquipCd())) {
			throw ThrowUtil.newNotAllowedEmptyInfo("terms.label.equip_cd");
		}
		
		// 해당일, 해당 설비에 이미 투입되었는지 확인
		Map<String, Object> condition = ValueUtil.newMap("workerId,jobDate,equipType,equipCd", input.getWorkerId(), DateUtil.todayStr(), input.getEquipType(), input.getEquipCd());
		int count = this.queryManager.selectSize(WorkerActual.class, condition);
		
		if(count > 0) {
			throw ThrowUtil.newValidationErrorWithNoLog("이미 투입된 작업자 입니다.");
		}
		
		input.setJobDate(DateUtil.todayStr());
		input.setStartedAt(DateUtil.currentTimeStr());
		
		return this.queryManager.insert(WorkerActual.class, input);
	}
	
	@RequestMapping(value = "/out_worker/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Out worker from equipment")
	public WorkerActual outWorker(@PathVariable("id") String id) {
		WorkerActual worker = this.queryManager.select(WorkerActual.class, id);
		// 해당일, 해당 설비에 이미 투입되었는지 확인
		worker.setFinishedAt(DateUtil.currentTimeStr());
		this.queryManager.update(worker);
		return worker;
	}

}