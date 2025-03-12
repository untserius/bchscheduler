package com.evg.scheduler.repository.es;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.evg.scheduler.model.es.portstatusindex;
import com.evg.scheduler.model.es.stationActiveRecords;



public interface EVGRepository extends ElasticsearchRepository<stationActiveRecords, String> {

	Page<stationActiveRecords> findByStnRefNum(String stnRefNum, Pageable pageable);

	Page<stationActiveRecords> findAll(Pageable pageable);

	@Query("{ \"query_string\" : { \"query\": \"(?0) AND (?1)\", \"fields\": [\"stnRefNum\", \"reqType\"] } }")
	Page<stationActiveRecords> findBystnRefNumAndreqType(String stnRefNum, String reqType, Pageable pageable);

	@Query("{ \"query_string\" : { \"query\": \"(?0) AND (?1) AND (?2) AND (?3)\", \"fields\": [\"stnRefNum\", \"reqType\", \"createdTimestamp\", \"createdTimestamp\"] } }")
	Page<stationActiveRecords> findBycreatedTimestampAndFilterAndStnRefNum(String stnRefNum, String filter, String startDate,
			String endDate, Pageable pageable);

	@Query("{ \"query_string\" : { \"query\": \"(?0)\", \"fields\": [\"stnRefNum\"] } }")
	Page<stationActiveRecords> findBystnRefNum(String stnRefNum, Pageable pageable);
	
	@Query("{ \"query_string\" : { \"query\": \"(?0)\", \"fields\": [\"id\"] } }")
	Optional<stationActiveRecords> findById(String id);
	
	@Query("{ \"query_string\" : { \"query\": \"(?0)\", \"fields\": [\"id\"] } }")
	Optional<portstatusindex> findPortStatusById(String id);
	
	boolean existsById(String id);

	

}
