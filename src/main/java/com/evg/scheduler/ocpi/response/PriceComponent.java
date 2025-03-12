package com.evg.scheduler.ocpi.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PriceComponent {

	private String type;

	private double price;

	@JsonIgnore
	private double vat;

	private int step_size;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStep_size() {
		return step_size;
	}

	public void setStep_size(int step_size) {
		this.step_size = step_size;
	}

	public double getVat() {
		return vat;
	}

	public void setVat(double vat) {
		this.vat = vat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "PriceComponent [type=" + type + ", price=" + price + ", vat=" + vat + ", step_size=" + step_size + "]";
	}

}
