package com.evg.scheduler.model.ocpp;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class BaseEntity implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("%s(id=%d)", this.getClass().getSimpleName(), this.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;

		if (o instanceof BaseEntity) {
			final BaseEntity other = (BaseEntity) o;
			return equal(getId(), other.getId());
			// return getId() == other.getId() || (getId() != null &&
			// getId().equals(other.getId()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	public static int hashCode(Object... objects) {
		return Arrays.hashCode(objects);
	}
}
