package io.github.dosarf.metricgroovy.core.impl.script;

import groovy.lang.Script;
import io.github.dosarf.metricgroovy.core.api.script.MetricGroovyScript;

public class MetricGroovyScriptImpl implements MetricGroovyScript {

	private final Script script;

	public MetricGroovyScriptImpl(Script script) {
		this.script = script;
	}
	
	@Override
	public Object evaluate() {
		return script.run();
	}

	@Override
	public Object getVariable(String name) {
		return script.getBinding().getVariable(name);
	}

}
