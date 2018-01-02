package io.github.dosarf.metricgroovy.core.impl.script

import static io.github.dosarf.metricgroovy.core.impl.ExpressionHandler.DIMENSIONLESS_UNIT
import io.github.dosarf.metricgroovy.core.api.MeasureSource
import io.github.dosarf.metricgroovy.core.api.MeasureSourceProvider
import io.github.dosarf.metricgroovy.core.api.VariableMeasureSource
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorSyntaxException
import io.github.dosarf.metricgroovy.core.api.error.IncompatibleMeasureSourceException
import io.github.dosarf.metricgroovy.core.api.error.IncompatibleUnitException

import javax.measure.DecimalMeasure
import javax.measure.Measure
import javax.measure.unit.Unit

// TODO Does this class need to be .groovy? Can't it be .java?
class MeasureSourceWrapper {
	private static final Optional<Unit> DIMENSIONLESS = Optional.of(DIMENSIONLESS_UNIT)

	private final VariableMeasureSource variableMeasureSource
	private final MeasureSourceProvider measureSourceProvider

	public MeasureSourceWrapper(MeasureSource measureProvider) {
		if (measureProvider instanceof VariableMeasureSource) {
			this.variableMeasureSource = measureProvider
		}
		if (measureProvider instanceof MeasureSourceProvider) {
			this.measureSourceProvider = measureProvider
		}
	}

	public Object invokeMethod(String name, Object argsObject) {
		Object[] args = (Object[])argsObject

		if (isSimpleMeasureAccess(args)) {
			if (variableMeasureSource == null) {
				throw IncompatibleMeasureSourceException.expectedVariableMeasureSource(name)
			}
			return getMeasure(variableMeasureSource, name, getRequestedUnit(args))
		} else if (isParameterizedMeasureAccess(args)) {
			if (measureSourceProvider == null) {
				throw IncompatibleMeasureSourceException.expectedMeasureSourceProvider(name, args[0])
			}
			return getMeasureSource(measureSourceProvider, name, (Map<String, Object>)args[0])
		} else {
			throw new EvaluatorSyntaxException(args)
		}
	}

	private boolean isSimpleMeasureAccess(Object[] args) {
		return (args.length == 0) || (args.length == 1 && args[0] instanceof Unit)
	}

	private boolean isParameterizedMeasureAccess(Object[] args) {
		return args.length == 1 && args[0] instanceof Map
	}

	private Optional<Unit> getRequestedUnit(Object[] args) {
		return args.length == 1 ? Optional.of((Unit)args[0]) : DIMENSIONLESS
	}

	private Measure getMeasure(VariableMeasureSource source, String name, Optional<Unit> requestedUnit) {
		Measure measure = source.getMeasure(name, requestedUnit)
		return getMeasureInUnit(measure, requestedUnit.get())
	}
	
	private MeasureSourceWrapper getMeasureSource(MeasureSourceProvider provider, String name, Map<String, Object> parameters) {
		MeasureSource measureSource = provider.getMeasureSource(name, parameters)
		return new MeasureSourceWrapper(measureSource)
	}

	private Measure getMeasureInUnit(Measure measure, Unit requestedUnit) {
		Unit providedUnit = measure.getUnit()
		if (providedUnit.equals(requestedUnit)) {
			return measure
		} else if (providedUnit.isCompatible(requestedUnit)) {
			// TODO this could have a performance hit
			double oldValue = (double) measure.getValue()
			double newValue = providedUnit.getConverterTo(requestedUnit).convert(oldValue)
			return DecimalMeasure.valueOf(newValue, requestedUnit)
		} else if (DIMENSIONLESS_UNIT.equals(requestedUnit)) {
			return Measure.valueOf(measure.value, DIMENSIONLESS_UNIT)
		} else {
			throw new IncompatibleUnitException(requestedUnit, providedUnit)
		}
	}

}
