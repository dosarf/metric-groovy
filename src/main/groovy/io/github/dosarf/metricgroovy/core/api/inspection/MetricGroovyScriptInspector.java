package io.github.dosarf.metricgroovy.core.api.inspection;

import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;

public interface MetricGroovyScriptInspector {
	Report inspect() throws EvaluatorException;
}
