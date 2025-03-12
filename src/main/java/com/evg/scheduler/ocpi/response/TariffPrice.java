package com.evg.scheduler.ocpi.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TariffPrice {

	private double excl_vat;

	private double incl_vat;

	public double getExcl_vat() {
		return excl_vat;
	}

	public void setExcl_vat(double excl_vat) {
		this.excl_vat = excl_vat;
	}

	public double getIncl_vat() {
		return incl_vat;
	}

	public void setIncl_vat(double incl_vat) {
		this.incl_vat = incl_vat;
	}

	@Override
	public String toString() {
		return "Price [excl_vat=" + excl_vat + ", incl_vat=" + incl_vat + "]";
	}

}
