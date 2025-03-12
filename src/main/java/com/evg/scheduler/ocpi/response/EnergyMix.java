package com.evg.scheduler.ocpi.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnergyMix {

	private boolean is_green_energy;

	private List<EnergySource> energy_sources;

	private List<EnvironmentalImpact> environ_impact;

	private String supplier_name;

	private String energy_product_name;

	public boolean isIs_green_energy() {
		return is_green_energy;
	}

	public void setIs_green_energy(boolean is_green_energy) {
		this.is_green_energy = is_green_energy;
	}

	public List<EnergySource> getEnergy_sources() {
		return energy_sources;
	}

	public void setEnergy_sources(List<EnergySource> energy_sources) {
		this.energy_sources = energy_sources;
	}

	public List<EnvironmentalImpact> getEnviron_impact() {
		return environ_impact;
	}

	public void setEnviron_impact(List<EnvironmentalImpact> environ_impact) {
		this.environ_impact = environ_impact;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getEnergy_product_name() {
		return energy_product_name;
	}

	public void setEnergy_product_name(String energy_product_name) {
		this.energy_product_name = energy_product_name;
	}

	@Override
	public String toString() {
		return "EnergyMix [is_green_energy=" + is_green_energy + ", energy_sources=" + energy_sources
				+ ", environ_impact=" + environ_impact + ", supplier_name=" + supplier_name + ", energy_product_name="
				+ energy_product_name + "]";
	}

}
