package io.github.dosarf.metricgroovy.core.api.error;

import com.google.common.collect.ImmutableList;

public class EvaluatorSyntaxException extends EvaluatorException {

	private static final long serialVersionUID = 1L;

	private final Object[] invocationArgs;

	public EvaluatorSyntaxException(Object[] invocationArgs) {
		super(String.format("Incorrect evaluation syntax: %s",
				ImmutableList.copyOf(invocationArgs)));
		this.invocationArgs = invocationArgs;
	}

	public Object[] getInvocationArgs() {
		return invocationArgs;
	}

}
