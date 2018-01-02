package io.github.dosarf.metricgroovy.core.api.error;

import io.github.dosarf.metricgroovy.core.api.MeasureSource;
import io.github.dosarf.metricgroovy.core.api.MeasureSourceProvider;
import io.github.dosarf.metricgroovy.core.api.VariableMeasureSource;

public class IncompatibleMeasureSourceException extends EvaluatorException {

	private static final long serialVersionUID = 1L;

	private final String measureSourceName;
	private final Class<? extends MeasureSource> expectedInterface;
	private final Object argument;

	public IncompatibleMeasureSourceException(String measureSourceName,
			Class<? extends MeasureSource> expectedInterface, Object argument) {
		super(createMessage(measureSourceName, expectedInterface, argument));
		this.measureSourceName = measureSourceName;
		this.expectedInterface = expectedInterface;
		this.argument = argument;
	}

	public String getMeasureSourceName() {
		return measureSourceName;
	}

	public Class<? extends MeasureSource> getExpectedInterface() {
		return expectedInterface;
	}

	public Object getArgument() {
		return argument;
	}
	
	public static IncompatibleMeasureSourceException expectedVariableMeasureSource(String measureSourceName) {
		return new IncompatibleMeasureSourceException(measureSourceName, VariableMeasureSource.class, "[unit]");
	}

	public static IncompatibleMeasureSourceException expectedMeasureSourceProvider(String measureSourceName, Object argument) {
		return new IncompatibleMeasureSourceException(measureSourceName, MeasureSourceProvider.class, argument);
	}

	private static String createMessage(String measureSourceName,
			Class<? extends MeasureSource> expectedInterface, Object arguments) {
		return String
				.format("Syntax '%s(%s)' needs %s instance at this point.",
						measureSourceName, arguments,
						expectedInterface.getSimpleName());
	}
}
