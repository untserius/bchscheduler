package com.evg.scheduler.model.ocpp;

public class AutoReload {

	private double amount;

	private double lowBalance;

	private String cardNo;
	
	private String paymentId;
	
	private long accountId;
	
	private long userId;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getLowBalance() {
		return lowBalance;
	}

	public void setLowBalance(double lowBalance) {
		this.lowBalance = lowBalance;
	}

	
	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AutoReload [amount=" + amount + ", lowBalance=" + lowBalance + ", cardNo=" + cardNo + ", paymentId="
				+ paymentId + ", accountId=" + accountId + ", userId=" + userId + "]";
	}

}