package com.evg.scheduler.model.es;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(indexName = "#{@environment.getProperty('es.stationActivelogs')}")
public class stationActiveRecords {
	
	private String id;
	
	private Long stationId;
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "creationDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate = new Date();
	
	@Field(type = FieldType.Keyword, name = "activity")
	private boolean activity;
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "IntervalTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date intervalTime;
	
	@Field(type = FieldType.Keyword, name = "stnRefNum")
	private String stnRefNum;
	
	
	public Long getStationId() {
		return stationId;
	}
	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public boolean isActivity() {
		return activity;
	}
	public void setActivity(boolean activity) {
		this.activity = activity;
	}
	public Date getIntervalTime() {
		return intervalTime;
	}
	public void setIntervalTime(Date intervalTime) {
		this.intervalTime = intervalTime;
	}
	public String getStnRefNum() {
		return stnRefNum;
	}
	public void setStnRefNum(String stnRefNum) {
		this.stnRefNum = stnRefNum;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "stationActiveRecords [id=" + id + ", stationId=" + stationId + ", creationDate=" + creationDate
				+ ", activity=" + activity + ", intervalTime=" + intervalTime + ", stnRefNum=" + stnRefNum + "]";
	}
}
