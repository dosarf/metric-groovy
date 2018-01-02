package io.github.dosarf.metricgroovy.core.api.error;

public class UndefinedMeasureException extends EvaluatorException {
	private static final long serialVersionUID = 1L;

	private final String measureName;

	public UndefinedMeasureException(String measureName) {
		super(String.format("Measure '%s' is undefined.", measureName));
		this.measureName = measureName;
	}

	public String getMeasureName() {
		return measureName;
	}

}
