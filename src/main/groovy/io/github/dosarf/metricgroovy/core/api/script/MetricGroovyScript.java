package io.github.dosarf.metricgroovy.core.api.script;

import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;

/**
 * Script object that can be cached and evaluated multiple times with different
 * input.
 * */
public interface MetricGroovyScript {

	/**
	 * @param variables
	 *            read-only custom variables provided for this evaluation
	 * @return the result of the evaluation
	 * */
	public Object evaluate() throws EvaluatorException;

	/**
	 * @return the value of the variable as it is after the last execution of
	 *         evaluate(). Null, if variable does not exist.
	 */
	public Object getVariable(String name);
}
