package io.github.dosarf.metricgroovy.core.api.error;

public class EvaluatorException extends Exception {

	private static final long serialVersionUID = 1L;

	public EvaluatorException() {
	}

	public EvaluatorException(String message) {
		super(message);
	}

	public EvaluatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
