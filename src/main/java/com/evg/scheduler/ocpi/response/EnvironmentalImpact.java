package com.evg.scheduler.ocpi.response;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvironmentalImpact {

	private EnvironmentalImpactCategory source;

	private double number;

	public EnvironmentalImpactCategory getSource() {
		return source;
	}

	public void setSource(EnvironmentalImpactCategory source) {
		this.source = source;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "EnvironmentalImpact [source=" + source + ", number=" + number + "]";
	}

}
