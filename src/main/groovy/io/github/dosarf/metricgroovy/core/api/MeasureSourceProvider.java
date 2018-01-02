package io.github.dosarf.metricgroovy.core.api;

import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;

import java.util.Map;

public interface MeasureSourceProvider extends MeasureSource {

	/**
	 * @param name think of it as a function name
	 * @param parameters think of it as arguments to a function call
	 * @return A MeasureSource instance for the given requestName
	 * and the request parameters.
	 */
	MeasureSource getMeasureSource(String name, Map<String, Object> parameters) throws EvaluatorException;

}
