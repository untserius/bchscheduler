package com.evg.scheduler.model.ocpp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ocpp_activeTransaction")
public class OCPPActiveTransaction extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String sessionId;
	private long stationId;
	private String rfId;
	private long transactionId;
	private long connectorId;
	private String messageType;
	private String status;
	private long userId;
	private String RequestedID;
	private Date timeStamp;
	private long orgId;

	public OCPPActiveTransaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OCPPActiveTransaction(String sessionId, long stationId, String sessionStatus, String rfId,
			long transactionId, String messageType, String portalStation, String status, String statusMobile,
			long userId) {
		super();
		this.sessionId = sessionId;
		this.stationId = stationId;
		this.rfId = rfId;
		this.transactionId = transactionId;
		this.messageType = messageType;
		this.status = status;
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public String getRfId() {
		return rfId;
	}

	public void setRfId(String rfId) {
		this.rfId = rfId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getRequestedID() {
		return RequestedID;
	}

	public void setRequestedID(String requestedID) {
		RequestedID = requestedID;
	}

	public long getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(long connectorId) {
		this.connectorId = connectorId;
	}

	@Column(name = "orgId")
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "OCPPActiveTransaction [sessionId=" + sessionId + ", stationId=" + stationId + ", rfId=" + rfId
				+ ", transactionId=" + transactionId + ", connectorId=" + connectorId + ", messageType=" + messageType
				+ ", status=" + status + ", userId=" + userId + ", RequestedID=" + RequestedID + ", timeStamp="
				+ timeStamp + ", orgId=" + orgId + "]";
	}
}
