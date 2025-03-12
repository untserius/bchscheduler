package com.evg.scheduler.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JDBCExecuteRepository implements ExecuteRepository {
	
	@Qualifier("primary")
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private int maxTimeInSec=300;
	
	@Override
	public List<Map<String, Object>> findAll(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec);
		return jdbcTemplate.queryForList(query);
	}

	@Override
	public String findString(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		return jdbcTemplate.queryForObject(query, String.class);
	}

	@Override
	public <T> List<T> findAllSingalObject(String query,T newEntry) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		return (List<T>) jdbcTemplate.queryForList(query, newEntry.getClass());

	}
	
	@Override
	public String getRecordBySqlStr(String query,String removal) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		String r = jdbcTemplate.queryForList(query).toString().replace("[", "").replace("]", "").replace("{", "").replace(removal+"=", "").replace("}", "");
		if(r == null || r.equalsIgnoreCase("null") || r.equalsIgnoreCase(" ") || r.equalsIgnoreCase("")) {
			r = "0";
		}
		return r;
	}

	@Override
	public List<String> findAllSingalString(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		return jdbcTemplate.queryForList(query, String.class);
	}

	@Override
	public int update(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		return jdbcTemplate.update(query);
	}

	@Override
	public String getRecordBySql(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		String data = "0";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
		if(queryForList.size() > 0) {
			data = String.valueOf(queryForList.get(0).get("0"));
		}
		return data;
	}
	@Override
	public void execute(String query) {
		jdbcTemplate.setQueryTimeout(maxTimeInSec); 
		jdbcTemplate.execute(query);
	}

}
