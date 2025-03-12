package com.evg.scheduler.repository.jdbc;

import java.util.List;
import java.util.Map;

public interface ExecuteRepository {

	List<Map<String, Object>> findAll(String query);

	String findString(String query);

	<T> List<T> findAllSingalObject(String query,T newsEntry);

	List<String> findAllSingalString(String query);
	
	String getRecordBySqlStr(String query,String removal);

	int update(String updateStationQuery);

	String getRecordBySql(String uniId);

	void execute(String query);
	
}
