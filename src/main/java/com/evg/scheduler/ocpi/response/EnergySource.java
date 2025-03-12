package com.evg.scheduler.ocpi.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnergySource {

	private EnergySourceCategory source;

	private double percentage;

	public EnergySourceCategory getSource() {
		return source;
	}

	public void setSource(EnergySourceCategory source) {
		this.source = source;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public String toString() {
		return "EnergySource [source=" + source + ", percentage=" + percentage + "]";
	}

}
