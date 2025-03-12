package com.evg.scheduler.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="VantivAuthComplete")
@XmlAccessorType(XmlAccessType.FIELD)
public class VantivAuthComplete {

	@XmlElement(name="ErrorCode")
	private String ErrorCode;
	@XmlElement(name="ErrorMessage")
	private String ErrorMessage;
	@XmlElement(name="TerminalId")
	private String TerminalId;
	@XmlElement(name="DateTime")
	private String DateTime;
	@XmlElement(name="PGTransactionId")
	private String PGTransactionId;
	@XmlElement(name="ApprovalNumber")
	private String ApprovalNumber;
	
	public VantivAuthComplete() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public String getTerminalId() {
		return TerminalId;
	}

	public void setTerminalId(String terminalId) {
		TerminalId = terminalId;
	}

	public String getDateTime() {
		return DateTime;
	}

	public void setDateTime(String dateTime) {
		DateTime = dateTime;
	}

	public String getPGTransactionId() {
		return PGTransactionId;
	}

	public void setPGTransactionId(String pGTransactionId) {
		PGTransactionId = pGTransactionId;
	}

	public String getApprovalNumber() {
		return ApprovalNumber;
	}

	public void setApprovalNumber(String approvalNumber) {
		ApprovalNumber = approvalNumber;
	}

	@Override
	public String toString() {
		return "VantivAuthComplete [ErrorCode=" + ErrorCode + ", ErrorMessage="
				+ ErrorMessage + ", TerminalId=" + TerminalId + ", DateTime="
				+ DateTime + ", PGTransactionId=" + PGTransactionId
				+ ", ApprovalNumber=" + ApprovalNumber + "]";
	}
	
}
