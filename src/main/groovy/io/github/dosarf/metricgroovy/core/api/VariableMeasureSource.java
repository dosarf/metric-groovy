package io.github.dosarf.metricgroovy.core.api;

import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;

import java.util.Optional;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

@SuppressWarnings("rawtypes")
public interface VariableMeasureSource extends MeasureSource {

	/**
	 * @param name
	 *            the name of the measure requested
	 * @param requestedUnit
	 *            mostly for diagnostic purposes. Absent value should indicate
	 *            that the variable measure will be used with the dimensionless
	 *            unit.
	 * @return The measure for the given variable name, not necessarily in the
	 *         requested unit. Throws if measure with given name does not exist.
	 */
	Measure getMeasure(String name, Optional<Unit<? extends Quantity>> requestedUnit)
			throws EvaluatorException;
}
