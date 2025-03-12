package com.evg.scheduler.dao;

import java.util.List;
import java.util.Map;

import com.evg.scheduler.model.ocpp.BaseEntity;

public interface GeneralDao<T extends BaseEntity, I> {

	<T> T saveOrupdate(T newsEntry);

	<T> List<T> findAll(String hql, T newsEntry);

	int updateSqlQuiries(String hql);

	public String listOfStringData(String hql);
	
	public List<Map<String, Object>> getMapData(String hql);

	<T> T update(T newsEntry);

	<T> T save(T type);

	<T> T findById(T newsEntry, long id);

	<T> T findOne(String hql, T newsEntry);

	<T> T findOneBySQLQuery(String hql, T newsEntry);

	String getSingleRecord(String hql);

	public String getRecordBySql(String hql);

	Boolean getFlag(String hql);

	// BigDecimal getQueryForPrimaryKeyId(String hql);

	public String updateHqlQuiries(String hql);

	Boolean getStationMaxSessionFlag(String hql);

	<T> List<T> findAllSQLQuery(T newsEntry, String query);

	<T> List<T> findAllHQLQuery(T newsEntry, String query);

	<T> T findOneHQLQuery(T newsEntry, String query);
	
	List findSQLQuery(String query);

}
