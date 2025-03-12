package com.evg.scheduler.ocpi.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TariffElement {

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<PriceComponent> price_components;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private TariffRestrictions restrictions;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public List<PriceComponent> getPrice_components() {
		return price_components;
	}

	public void setPrice_components(List<PriceComponent> price_components) {
		this.price_components = price_components;
	}

	public TariffRestrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(TariffRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	@Override
	public String toString() {
		return "TariffElement [price_components=" + price_components + ", restrictions=" + restrictions + "]";
	}

}
