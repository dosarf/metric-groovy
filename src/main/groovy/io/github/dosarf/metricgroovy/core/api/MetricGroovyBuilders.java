package io.github.dosarf.metricgroovy.core.api;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.dosarf.metricgroovy.core.api.inspection.MetricGroovyScriptInspector;
import io.github.dosarf.metricgroovy.core.api.script.MetricGroovyScript;
import io.github.dosarf.metricgroovy.core.api.unit.SupportedUnits;
import io.github.dosarf.metricgroovy.core.impl.ExpressionHandler;
import io.github.dosarf.metricgroovy.core.impl.inspection.MetricGroovyScriptInspectorImpl;
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExpressionHandler;
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExtension;
import io.github.dosarf.metricgroovy.core.impl.script.MeasureSourceWrapper;
import io.github.dosarf.metricgroovy.core.impl.script.MetricGroovyScriptImpl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MetricGroovyBuilders {

	public static EvaluatorBuilder evaluatorBuilder(String scriptText) {
		return new EvaluatorBuilder(scriptText);
	}

	public static InspectorBuilder inspectorBuilder(String scriptText) {
		return new InspectorBuilder(scriptText);
	}

	public static class EvaluatorBuilder {

		private final String scriptText;
		private ExpressionHandler expressionHandler;
		private Map<String, Unit<? extends Quantity>> supportedUnits;
		private Map<String, Object> customVariables;
		private List<MeasureSource> measureSources;

		public EvaluatorBuilder(String scriptText) {
			this.scriptText = scriptText;
			expressionHandler = new EvaluatorExpressionHandler();
			supportedUnits = SupportedUnits.UNITS;
			customVariables = Maps.newHashMap();
			measureSources = Lists.newLinkedList();
		}

		public EvaluatorBuilder expressionHandler(
				ExpressionHandler expressionHandler) {
			this.expressionHandler = expressionHandler;
			return this;
		}

		public EvaluatorBuilder supportedUnits(Map<String, Unit<? extends Quantity>> supportedUnits) {
			this.supportedUnits = supportedUnits;
			return this;
		}

		public EvaluatorBuilder customVariables(
				Map<String, Object> customVariables) {
			this.customVariables = customVariables;
			return this;
		}

		public EvaluatorBuilder measureSource(MeasureSource measureSource) {
			measureSources.add(measureSource);
			return this;
		}

		public MetricGroovyScript build() {
			GroovyShell shell = new GroovyShell();
			Script script = shell.parse(getModifiedScriptText(scriptText));
			script.setBinding(createBinding());
			return new MetricGroovyScriptImpl(script);
		}

		private String getModifiedScriptText(String scriptText) {
			StringBuilder modifiedScriptTextBuilder = new StringBuilder();
			modifiedScriptTextBuilder.append(String.format("import %s%n",
					EvaluatorExtension.class.getName()));
			modifiedScriptTextBuilder
					.append(String
							.format("EvaluatorExtension._exp_handler = _exp_handler%n"));
			modifiedScriptTextBuilder.append(scriptText);
			return modifiedScriptTextBuilder.toString();
		}

		private Binding createBinding() {
			Binding binding = new Binding();
			binding.setVariable("_exp_handler", expressionHandler);
			for (Entry<String, Unit<? extends Quantity>> supportedUnit : supportedUnits.entrySet()) {
				binding.setVariable(supportedUnit.getKey(),
						supportedUnit.getValue());
			}
			for (Entry<String, Object> customVariable : customVariables
					.entrySet()) {
				binding.setVariable(customVariable.getKey(),
						customVariable.getValue());
			}
			for (MeasureSource measureSource : measureSources) {
				binding.setVariable(measureSource.getName(),
						new MeasureSourceWrapper(measureSource));
			}
			return binding;
		}
	}

	public static class InspectorBuilder {
		private final String scriptText;
		private Map<String, Unit<? extends Quantity>> supportedUnits;
		private Map<String, Object> customVariables;
		private List<String> measureSourceNames;

		public InspectorBuilder(String scriptText) {
			this.scriptText = scriptText;
			supportedUnits = SupportedUnits.UNITS;
			customVariables = Maps.newHashMap();
			measureSourceNames = Lists.newLinkedList();
		}

		public InspectorBuilder supportedUnits(Map<String, Unit<? extends Quantity>> supportedUnits) {
			this.supportedUnits = supportedUnits;
			return this;
		}

		public InspectorBuilder customVariables(
				Map<String, Object> customVariables) {
			this.customVariables = customVariables;
			return this;
		}

		public InspectorBuilder useMeasureSourceName(String name) {
			measureSourceNames.add(name);
			return this;
		}

		public MetricGroovyScriptInspector build() {
			return new MetricGroovyScriptInspectorImpl(scriptText,
					supportedUnits, customVariables, measureSourceNames);
		}
	}

}
