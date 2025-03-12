package com.evg.scheduler.ocpi.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TariffRestrictions {

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String start_time;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String end_time;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String start_date;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String end_date;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double min_kwh;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double max_kwh;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double min_current;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double min_power;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private double max_power;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private long min_duration;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private long max_duration;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String day_of_week;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String reservation;

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public double getMin_kwh() {
		return min_kwh;
	}

	public void setMin_kwh(double min_kwh) {
		this.min_kwh = min_kwh;
	}

	public double getMax_kwh() {
		return max_kwh;
	}

	public void setMax_kwh(double max_kwh) {
		this.max_kwh = max_kwh;
	}

	public double getMin_current() {
		return min_current;
	}

	public void setMin_current(double min_current) {
		this.min_current = min_current;
	}

	public double getMin_power() {
		return min_power;
	}

	public void setMin_power(double min_power) {
		this.min_power = min_power;
	}

	public double getMax_power() {
		return max_power;
	}

	public void setMax_power(double max_power) {
		this.max_power = max_power;
	}

	public long getMin_duration() {
		return min_duration;
	}

	public void setMin_duration(long min_duration) {
		this.min_duration = min_duration;
	}

	public long getMax_duration() {
		return max_duration;
	}

	public void setMax_duration(long max_duration) {
		this.max_duration = max_duration;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getReservation() {
		return reservation;
	}

	public void setReservation(String reservation) {
		this.reservation = reservation;
	}

	@Override
	public String toString() {
		return "TariffRestrictions [start_time=" + start_time + ", end_time=" + end_time + ", start_date=" + start_date
				+ ", end_date=" + end_date + ", min_kwh=" + min_kwh + ", max_kwh=" + max_kwh + ", min_current="
				+ min_current + ", min_power=" + min_power + ", max_power=" + max_power + ", min_duration="
				+ min_duration + ", max_duration=" + max_duration + ", day_of_week=" + day_of_week + ", reservation="
				+ reservation + "]";
	}

}
