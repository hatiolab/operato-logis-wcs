select
	x.*,
	m10_result as m10_result_str,
	m20_result as m20_result_str,
	m30_result as m30_result_str,
	m40_result as m40_result_str,
	m50_result as m50_result_str,
	m60_result as m60_result_str
from
	productivity x
where
	domain_id = :domainId
	and batch_id = :batchId
	and job_date = :jobDate
	and job_hour = :jobHour