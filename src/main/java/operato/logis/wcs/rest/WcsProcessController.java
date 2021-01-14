package operato.logis.wcs.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import operato.logis.wcs.event.WaveReceiveEvent;
import operato.logis.wcs.service.impl.WcsProcessService;
import xyz.anythings.base.entity.BatchReceipt;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.event.EventConstants;
import xyz.anythings.sys.event.model.SysEvent;
import xyz.anythings.sys.util.AnyEntityUtil;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/wcs_process")
@ServiceDesc(description = "WCS Process Service API")
public class WcsProcessController {

	/**
	 * WCS 서비스
	 */
	@Autowired
	protected WcsProcessService wcsProcessService;
	
	/**
	 * Wave 수신 준비
	 * 
	 * @param areaCd
	 * @param stageCd
	 * @param comCd
	 * @param jobDate
	 * @return
	 */
	@RequestMapping(value = "/receive_waves/ready/{area_cd}/{stage_cd}/{com_cd}/{job_date}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Ready to receive waves")
	public BatchReceipt readyToReceiveWaves(
			@PathVariable("area_cd") String areaCd,
			@PathVariable("stage_cd") String stageCd,
			@PathVariable("com_cd") String comCd, 
			@PathVariable("job_date") String jobDate,
			@RequestParam(name = "job_type", required = false) String jobType) {

		Long domainId = Domain.currentDomainId();
		WaveReceiveEvent event = new WaveReceiveEvent(domainId, EventConstants.EVENT_RECEIVE_TYPE_RECEIPT, SysEvent.EVENT_STEP_BEFORE, areaCd, stageCd, jobDate, comCd);
		this.wcsProcessService.handleReadyToReceiveWave(event);
		return event.getReceiptData();
	}
	
	/**
	 * Wave 수신 시작
	 * 
	 * @param summary
	 * @return
	 */
	@RequestMapping(value = "/receive_waves/start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Start to receive waves")
	public BatchReceipt startToReceiveWaves(@RequestBody BatchReceipt summary) {

		Long domainId = Domain.currentDomainId();
		WaveReceiveEvent event = new WaveReceiveEvent(domainId, EventConstants.EVENT_RECEIVE_TYPE_RECEIVE, SysEvent.EVENT_STEP_BEFORE);
		event.setReceiptData(summary);
		this.wcsProcessService.handleStartToReceiveWave(event);
		return event.getReceiptData();
	}
	
	/**
	 * Wave 수신 취소
	 * 
	 * @param waveId
	 * @return
	 */
	@RequestMapping(value = "/cancel_wave/{wave_id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Cancel wave")
	public BatchReceipt cancelWave(@PathVariable("wave_id") String waveId) {

		Long domainId = Domain.currentDomainId();
		WaveReceiveEvent event = new WaveReceiveEvent(domainId, EventConstants.EVENT_RECEIVE_TYPE_CANCEL, SysEvent.EVENT_STEP_BEFORE);
		JobBatch batch = new JobBatch();
		batch.setId(waveId);
		event.setJobBatch(batch);
		this.wcsProcessService.handleCancelReceived(event);
		return event.getReceiptData();
	}
	
	/**
	 * Wave 대상 분류
	 * 
	 * @param waveId
	 * @param classifyCodes
	 * @return
	 */
	@RequestMapping(value = "/classify_wave/{wave_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Classify wave")
	public List<JobBatch> classifyingWaves(@PathVariable("wave_id") String waveId, @RequestBody List<String> classifyCodes) {

		// 파라미터 대상 분류 옵션
		Long domainId = Domain.currentDomainId();
		JobBatch batch = AnyEntityUtil.findEntityBy(domainId, true, JobBatch.class, "domainId,id", domainId, waveId);
		List<JobBatch> classifiedBatches = this.wcsProcessService.classifyWaves(batch, classifyCodes);
		return classifiedBatches;
	}
	
	/**
	 * Wave 작업 분할
	 * 
	 * @param waveId
	 * @param splitMethod
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/split_wave/{wave_id}/{split_method}/{count}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Split wave")
	public List<JobBatch> splitWave(@PathVariable("wave_id") String waveId, @PathVariable("split_method") String splitMethod, @PathVariable("count") Integer count) {

		Long domainId = Domain.currentDomainId();
		JobBatch batch = AnyEntityUtil.findEntityBy(domainId, true, JobBatch.class, "domainId,id", domainId, waveId);
		
		if(ValueUtil.isEqualIgnoreCase(splitMethod, "evenly")) {
			return this.wcsProcessService.splitWavesByEvenly(batch, count);
		} else {
			return this.wcsProcessService.splitWavesByOrderQty(batch, count);
		}
	}
	
	/**
	 * Wave 작업 병합
	 * 
	 * @param mainWaveId
	 * @param targetWaveId
	 * @return
	 */
	@RequestMapping(value = "/merge_wave/{main_wave_id}/{target_wave_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Merge waves")
	public JobBatch mergeWave(@PathVariable("main_wave_id") String mainWaveId, @PathVariable("target_wave_id") String targetWaveId) {

		Long domainId = Domain.currentDomainId();
		JobBatch mainBatch = AnyEntityUtil.findEntityBy(domainId, true, JobBatch.class, "domainId,id", domainId, mainWaveId);
		JobBatch targetBatch = AnyEntityUtil.findEntityBy(domainId, true, JobBatch.class, "domainId,id", domainId, targetWaveId);
		return this.wcsProcessService.mergeWave(mainBatch, targetBatch);
	}

	/**
	 * Wave 작업 확정
	 * 
	 * @param waveId
	 * @return
	 */
	@RequestMapping(value = "/confirm_wave/{wave_id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Confirm wave")
	public JobBatch confirmWave(@PathVariable("wave_id") String waveId) {

		Long domainId = Domain.currentDomainId();
		JobBatch batch = AnyEntityUtil.findEntityBy(domainId, true, JobBatch.class, "domainId,id", domainId, waveId);
		return this.wcsProcessService.confirmWave(batch);
	}

}
