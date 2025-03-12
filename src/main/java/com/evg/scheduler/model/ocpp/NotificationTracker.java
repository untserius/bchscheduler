package com.evg.scheduler.model.ocpp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "notification_tracker")
@Entity
public class NotificationTracker extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long userId;

	private long account_transactionId;

	private String sessionId;

	private boolean emailflag;

	private boolean smsflag;

	private boolean pushNotificationFlag;

	@Temporal(TemporalType.DATE)
	@Column(name = "Modified_Date", length = 10)
	private Date modifiedDate;

	private boolean resend;

	private long resendCount;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getAccount_transactionId() {
		return account_transactionId;
	}

	public void setAccount_transactionId(long account_transactionId) {
		this.account_transactionId = account_transactionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "emailflag", columnDefinition = "bit Default 0 Not Null")
	public boolean isEmailflag() {
		return emailflag;
	}

	public void setEmailflag(boolean emailflag) {
		this.emailflag = emailflag;
	}

	@Column(name = "smsflag", columnDefinition = "bit Default 0 Not Null")
	public boolean isSmsflag() {
		return smsflag;
	}

	public void setSmsflag(boolean smsflag) {
		this.smsflag = smsflag;
	}

	@Column(name = "pushNotificationFlag", columnDefinition = "bit Default 0 Not Null")
	public boolean isPushNotificationFlag() {
		return pushNotificationFlag;
	}

	public void setPushNotificationFlag(boolean pushNotificationFlag) {
		this.pushNotificationFlag = pushNotificationFlag;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Column(name = "resend", columnDefinition = "bit Default 0 Not Null")
	public boolean isResend() {
		return resend;
	}

	public void setResend(boolean resend) {
		this.resend = resend;
	}

	@Column(name = "resendCount", columnDefinition = "double precision Default 0 Not Null")
	public long getResendCount() {
		return resendCount;
	}

	public void setResendCount(long resendCount) {
		this.resendCount = resendCount;
	}

}
