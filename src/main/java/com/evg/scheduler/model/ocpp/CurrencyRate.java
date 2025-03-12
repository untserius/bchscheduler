package com.evg.scheduler.model.ocpp;

import java.util.Date;

public class CurrencyRate {

	private static final long serialVersionUID = 1L;

	private String currency_code;
	private float currency_rate;
	private String currencyName;
	private Date lastUpdated;

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public float getCurrency_rate() {
		return currency_rate;
	}

	public void setCurrency_rate(float currency_rate) {
		this.currency_rate = currency_rate;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		return "CurrencyRate [currency_code=" + currency_code + ", currency_rate=" + currency_rate + ", currencyName="
				+ currencyName + ", lastUpdated=" + lastUpdated + "]";
	}
}