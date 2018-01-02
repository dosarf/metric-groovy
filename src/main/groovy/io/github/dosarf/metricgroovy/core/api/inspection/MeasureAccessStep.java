package io.github.dosarf.metricgroovy.core.api.inspection;

import java.util.Map;

import com.google.common.base.MoreObjects;

public class MeasureAccessStep {
	private final String name;
	private final Map<String, Object> parameters;

	public MeasureAccessStep(String name, Map<String, Object> parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
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
		MeasureAccessStep other = (MeasureAccessStep) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", name)
				.add("parameters", parameters).toString();
	}
}