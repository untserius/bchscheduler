package com.evg.scheduler.model.es;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
@Document(indexName = "#{@environment.getProperty('es.portstatuslogs')}")
public class portstatusindex {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Integer, name = "stationId")
	private long stationId;
	
	@Field(type = FieldType.Integer, name = "portId")
	private long portId;
	
	@Field(type = FieldType.Keyword, name = "status")
	private String status;
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "timeStamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date timeStamp;
	
	@Field(type = FieldType.Keyword, name = "vendorErrorCode")
	private String vendorErrorCode;
	
	@Field(type = FieldType.Keyword, name = "errorCode")
	private String errorCode;
	
	@Field(type = FieldType.Keyword, name = "info")
	private String info;
	
	@Field(type = FieldType.Keyword, name = "vendorId")
	private String vendorId;
	
	@Field(type = FieldType.Keyword, name = "source")
	private String source;
	
	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "CreateDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date CreateDate;
	

	@Field(type =FieldType.Date, format = DateFormat.date_optional_time , name = "ToTimeStamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date ToTimeStamp;
	
	@Field(type = FieldType.Keyword, name = "maintenance")
	private boolean maintenance;


	public long getStationId() {
		return stationId;
	}


	public void setStationId(long stationId) {
		this.stationId = stationId;
	}


	public long getPortId() {
		return portId;
	}


	public void setPortId(long portId) {
		this.portId = portId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}


	public String getVendorErrorCode() {
		return vendorErrorCode;
	}


	public void setVendorErrorCode(String vendorErrorCode) {
		this.vendorErrorCode = vendorErrorCode;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public String getVendorId() {
		return vendorId;
	}


	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}


	public Date getCreateDate() {
		return CreateDate;
	}


	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}


	public Date getToTimeStamp() {
		return ToTimeStamp;
	}


	public void setToTimeStamp(Date toTimeStamp) {
		ToTimeStamp = toTimeStamp;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}
	
	public boolean isMaintenance() {
		return maintenance;
	}


	public void setMaintenance(boolean maintenance) {
		this.maintenance = maintenance;
	}

	@Override
	public String toString() {
		return "portstatusindex [id=" + id + ", stationId=" + stationId + ", portId=" + portId + ", status=" + status
				+ ", timeStamp=" + timeStamp + ", vendorErrorCode=" + vendorErrorCode + ", errorCode=" + errorCode
				+ ", info=" + info + ", vendorId=" + vendorId + ", source=" + source + ", CreateDate=" + CreateDate
				+ ", ToTimeStamp=" + ToTimeStamp + ", maintenance=" + maintenance + "]";
	}


}
