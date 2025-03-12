package com.evg.scheduler.message;

import java.util.Date;
import java.util.List;

public class MailForm {

	private String mailFrom;

	private String mailTo;

	private String mailCc;

	private String mailBcc;

	private String mailSubject;
	
	private String host;
	
	private String port;
	
	private String password;

	private String mailContent;

	private String contentType;
	
	private long stationId;
	
	private String stationRefNum;
	
	private Object model;

	private List<Object> attachments;
	
	private String imgPath;

	public MailForm(String mailTo, String mailSubject, String mailContent) {
		super();
		this.mailTo = mailTo;
		this.mailSubject = mailSubject;
		this.mailContent = mailContent;
	}
	
	public MailForm(String mailTo, String mailSubject, String mailContent,long stationId,String stationRefNum) {
		super();
		this.mailTo = mailTo;
		this.mailSubject = mailSubject;
		this.mailContent = mailContent;
		this.stationId=stationId;
		this.stationRefNum=stationRefNum;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMailBcc() {
		return mailBcc;
	}

	public void setMailBcc(String mailBcc) {
		this.mailBcc = mailBcc;
	}

	public String getMailCc() {
		return mailCc;
	}

	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public Date getMailSendDate() {
		return new Date();
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public List<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object> attachments) {
		this.attachments = attachments;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public String getStationRefNum() {
		return stationRefNum;
	}

	public void setStationRefNum(String stationRefNum) {
		this.stationRefNum = stationRefNum;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "MailForm [mailFrom=" + mailFrom + ", mailTo=" + mailTo + ", mailCc=" + mailCc + ", mailBcc=" + mailBcc
				+ ", mailSubject=" + mailSubject + ", host=" + host + ", port=" + port + ", password=" + password
				+ ", mailContent=" + mailContent + ", contentType=" + contentType + ", stationId=" + stationId
				+ ", stationRefNum=" + stationRefNum + ", model=" + model + ", attachments=" + attachments
				+ ", imgPath=" + imgPath + "]";
	}
	
}
