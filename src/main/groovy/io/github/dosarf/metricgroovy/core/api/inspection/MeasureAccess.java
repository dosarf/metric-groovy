package io.github.dosarf.metricgroovy.core.api.inspection;

import java.util.List;

import javax.measure.unit.Unit;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("rawtypes")
public class MeasureAccess {
	private final List<MeasureAccessStep> measureAccessPath;
	private final String measureName;
	private final Unit requestedUnit;

	public MeasureAccess(String measureName, Unit requestedUnit) {
		this(measureName, requestedUnit, ImmutableList.<MeasureAccessStep> of());
	}

	public MeasureAccess(String measureName, Unit requestedUnit,
			List<MeasureAccessStep> measureAccessPath) {
		this.measureName = measureName;
		this.requestedUnit = requestedUnit;
		this.measureAccessPath = ImmutableList.copyOf(measureAccessPath);
	}

	public String getMeasureName() {
		return measureName;
	}

	public Unit getRequestedUnit() {
		return requestedUnit;
	}

	public boolean hasMeasureAccessPath() {
		return !measureAccessPath.isEmpty();
	}

	public List<MeasureAccessStep> getMeasureAccessPath() {
		return measureAccessPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((measureAccessPath == null) ? 0 : measureAccessPath
						.hashCode());
		result = prime * result
				+ ((measureName == null) ? 0 : measureName.hashCode());
		result = prime * result
				+ ((requestedUnit == null) ? 0 : requestedUnit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasureAccess other = (MeasureAccess) obj;
		if (measureAccessPath == null) {
			if (other.measureAccessPath != null)
				return false;
		} else if (!measureAccessPath.equals(other.measureAccessPath))
			return false;
		if (measureName == null) {
			if (other.measureName != null)
				return false;
		} else if (!measureName.equals(other.measureName))
			return false;
		if (requestedUnit == null) {
			if (other.requestedUnit != null)
				return false;
		} else if (!requestedUnit.equals(other.requestedUnit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this)
				.add("measureName", measureName)
				.add("requestedUnit", requestedUnit);
		if (hasMeasureAccessPath()) {
			toStringHelper.add("accessPath", measureAccessPath);
		}
		return toStringHelper.toString();
	}

}