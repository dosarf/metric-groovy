package io.github.dosarf.metricgroovy.core.api.error;

import javax.measure.unit.Unit;

@SuppressWarnings("rawtypes")
public class IncompatibleUnitException extends EvaluatorException {

	private static final long serialVersionUID = 1L;
	private final Unit requestedUnit;
	private final Unit providedUnit;

	public IncompatibleUnitException(Unit requestedUnit, Unit providedUnit) {
		super(String.format(
				"Requested %s and provided %s units are incompatible",
				requestedUnit, providedUnit));
		this.requestedUnit = requestedUnit;
		this.providedUnit = providedUnit;
	}

	public Unit getRequestedUnit() {
		return requestedUnit;
	}

	public Unit getProvidedUnit() {
		return providedUnit;
	}

}
