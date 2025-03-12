package com.evg.scheduler.message;

import java.util.Date;

public class ResponseMessage {
	private String status="Rejected";
	private String message="";
	private Date timestamp=new Date();
	private String transactionId="0";
	private String clientId;
	private String sessionId;
	private int statusCode;
	private long connectorId;
	private long reservationId;
	private String requestType;
	private String apiRequestId;

	public ResponseMessage(String message) {
		this.message = message;
	}

	public ResponseMessage() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public long getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(long connectorId) {
		this.connectorId = connectorId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}
	
	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getApiRequestId() {
		return apiRequestId;
	}

	public void setApiRequestId(String apiRequestId) {
		this.apiRequestId = apiRequestId;
	}

	@Override
	public String toString() {
		return "ResponseMessage [status=" + status + ", message=" + message + ", timestamp=" + timestamp
				+ ", transactionId=" + transactionId + ", clientId=" + clientId + ", sessionId=" + sessionId
				+ ", statusCode=" + statusCode + ", connectorId=" + connectorId + ", reservationId=" + reservationId
				+ ", requestType=" + requestType + ", apiRequestId=" + apiRequestId + "]";
	}
	
}
