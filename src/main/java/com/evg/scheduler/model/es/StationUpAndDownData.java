package com.evg.scheduler.model.es;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(indexName = "#{@environment.getProperty('es.stationupanddowndata')}")
public class StationUpAndDownData {

	@Id
    private String id;
	
	@Field(type = FieldType.Keyword, name = "stationId")
	private long stationId;
	
	@Field(type = FieldType.Keyword, name = "stnRefNum")
	private String stnRefNum;
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "startTimeStamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date startTimeStamp = new Date();
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "endTimeStamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date endTimeStamp = new Date();
	
	@Field(type = FieldType.Keyword, name = "activity")
	private boolean activity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public String getStnRefNum() {
		return stnRefNum;
	}

	public void setStnRefNum(String stnRefNum) {
		this.stnRefNum = stnRefNum;
	}

	public Date getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(Date startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	public Date getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(Date endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}

	public boolean isActivity() {
		return activity;
	}

	public void setActivity(boolean activity) {
		this.activity = activity;
	}

	@Override
	public String toString() {
		return "StationUpAndDownData [id=" + id + ", stationId=" + stationId + ", stnRefNum=" + stnRefNum
				+ ", startTimeStamp=" + startTimeStamp + ", endTimeStamp=" + endTimeStamp + ", activity=" + activity
				+ "]";
	}
	
}
