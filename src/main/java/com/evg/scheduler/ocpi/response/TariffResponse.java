package com.evg.scheduler.ocpi.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TariffResponse {

	private String id;

	private String currency;

	@JsonIgnore
	private String type;

	private List<DisplayText> tariff_alt_text = new ArrayList<DisplayText>();

	@JsonIgnore
	private String tariff_alt_url;

	@JsonIgnore
	private TariffPrice min_price;

	@JsonIgnore
	private TariffPrice max_price;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private Set<TariffElement> elements;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@JsonIgnore
	private Date start_date_time;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@JsonIgnore
	private Date end_date_time;

	private EnergyMix energy_mix;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date last_updated;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTariff_alt_url() {
		return tariff_alt_url;
	}

	public void setTariff_alt_url(String tariff_alt_url) {
		this.tariff_alt_url = tariff_alt_url;
	}

	public TariffPrice getMin_price() {
		return min_price;
	}

	public void setMin_price(TariffPrice min_price) {
		this.min_price = min_price;
	}

	public TariffPrice getMax_price() {
		return max_price;
	}

	public void setMax_price(TariffPrice max_price) {
		this.max_price = max_price;
	}

	public Set<TariffElement> getElements() {
		return elements;
	}

	public void setElements(Set<TariffElement> elements) {
		this.elements = elements;
	}

	@JsonIgnore
	public Date getStart_date_time() {
		return start_date_time;
	}

	public void setStart_date_time(Date start_date_time) {
		this.start_date_time = start_date_time;
	}

	@JsonIgnore
	public Date getEnd_date_time() {
		return end_date_time;
	}

	public void setEnd_date_time(Date end_date_time) {
		this.end_date_time = end_date_time;
	}

	public Date getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(Date last_updated) {
		this.last_updated = last_updated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DisplayText> getTariff_alt_text() {
		return tariff_alt_text;
	}

	public void setTariff_alt_text(List<DisplayText> tariff_alt_text) {
		this.tariff_alt_text = tariff_alt_text;
	}

	public EnergyMix getEnergy_mix() {
		return energy_mix;
	}

	public void setEnergy_mix(EnergyMix energy_mix) {
		this.energy_mix = energy_mix;
	}

	@Override
	public String toString() {
		return "TariffResponse [id=" + id + ", currency=" + currency + ", type=" + type + ", tariff_alt_text="
				+ tariff_alt_text + ", tariff_alt_url=" + tariff_alt_url + ", min_price=" + min_price + ", max_price="
				+ max_price + ", elements=" + elements + ", start_date_time=" + start_date_time + ", end_date_time="
				+ end_date_time + ", energy_mix=" + energy_mix + ", last_updated=" + last_updated + "]";
	}

}
