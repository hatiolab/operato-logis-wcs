select 
	count(a.norun_count) * 10 as norun_time
from ( 
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m10_result = 0
		
	union all
		
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m20_result = 0
		
	union all
		
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m30_result = 0
		
	union all
		
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m40_result = 0
		
	union all
		
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m50_result = 0
		
	union all
		
	select
		count(1) as norun_count
	from
		productivity
	where
		domain_id = :domainId
		and batch_id = :batchId
		and m60_result = 0
) a