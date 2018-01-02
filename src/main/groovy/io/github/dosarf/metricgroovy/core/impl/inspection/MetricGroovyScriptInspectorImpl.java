package io.github.dosarf.metricgroovy.core.impl.inspection;

import io.github.dosarf.metricgroovy.core.api.MetricGroovyBuilders;
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;
import io.github.dosarf.metricgroovy.core.api.inspection.MeasureAccess;
import io.github.dosarf.metricgroovy.core.api.inspection.MetricGroovyScriptInspector;
import io.github.dosarf.metricgroovy.core.api.inspection.Report;
import io.github.dosarf.metricgroovy.core.api.script.MetricGroovyScript;

import java.util.List;
import java.util.Map;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("rawtypes")
public class MetricGroovyScriptInspectorImpl implements
		MetricGroovyScriptInspector {

	private final String scriptText;
	private final Map<String, Unit<? extends Quantity>> supportedUnits;
	private final Map<String, Object> customVariables;
	private final List<InspectorMeasureSource> measureSources;
	private final InspectorExpressionHandler expressionHandler;

	public MetricGroovyScriptInspectorImpl(String scriptText,
			Map<String, Unit<? extends Quantity>> supportedUnits,
			Map<String, Object> customVariables, List<String> measureSourceNames) {
		this.scriptText = scriptText;
		this.supportedUnits = supportedUnits;
		this.customVariables = customVariables;
		expressionHandler = new InspectorExpressionHandler();
		measureSources = ImmutableList.copyOf(Lists.transform(
				measureSourceNames,
				new Function<String, InspectorMeasureSource>() {

					@Override
					public InspectorMeasureSource apply(String measureSourceName) {
						return new InspectorMeasureSource(measureSourceName);
					}
				}));
	}

	@Override
	public Report inspect() throws EvaluatorException {
		MetricGroovyBuilders.EvaluatorBuilder scriptBuilder = MetricGroovyBuilders
				.evaluatorBuilder(scriptText).expressionHandler(
						expressionHandler);
		scriptBuilder.customVariables(customVariables);
		scriptBuilder.supportedUnits(supportedUnits);
		for (InspectorMeasureSource measureSource : measureSources) {
			scriptBuilder.measureSource(measureSource);
		}
		MetricGroovyScript script = scriptBuilder.build();

		Object result = script.evaluate();

		Class resultClass = Void.class;
		Unit resultUnit = null;
		if (result != null) {
			resultClass = result.getClass();
			if (result instanceof Measure) {
				resultUnit = ((Measure) result).getUnit();
			}
		}

		Map<String, List<MeasureAccess>> measureAccessesMap = Maps.newHashMap();
		for (InspectorMeasureSource measureSource : measureSources) {
			measureAccessesMap.put(measureSource.getName(),
					measureSource.getMeasureAccesses());
		}

		return new Report(resultClass, resultUnit, expressionHandler,
				measureAccessesMap);
	}

}
