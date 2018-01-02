package io.github.dosarf.metricgroovy.core.api.inspection;

import io.github.dosarf.metricgroovy.core.impl.inspection.InspectorExpressionHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.unit.Unit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

@SuppressWarnings("rawtypes")
public class Report {
	private final Class resultType;
	private final Unit resultUnit;
	private final Multimap<Unit, Unit> unitCompositions;
	private final List<Measure> literals;
	private final List<Operation> operations;
	private final Map<String, List<MeasureAccess>> measureAccessesMap;

	public Report(Class resultType, Unit resultUnit,
			InspectorExpressionHandler expressionHandler,
			Map<String, List<MeasureAccess>> measureAccessesMap) {
		this.resultType = resultType;
		this.resultUnit = resultUnit;
		unitCompositions = expressionHandler.getUnitCompositions();
		literals = expressionHandler.getLiterals();
		operations = expressionHandler.getOperations();
		this.measureAccessesMap = ImmutableMap.copyOf(measureAccessesMap);
	}

	public Class getResultType() {
		return resultType;
	}

	/**
	 * @return the resulting unit of the measure, if the resulting type is an
	 *         instance of javax.measure.Measure, null otherwise.
	 */
	public Unit getResultUnit() {
		return resultUnit;
	}

	public Multimap<Unit, Unit> getUnitCompositions() {
		return unitCompositions;
	}

	public List<Measure> getLiterals() {
		return literals;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public Set<String> getMeasureSourceNames() {
		return measureAccessesMap.keySet();
	}

	/**
	 * @param measureSourceName
	 * @return the list of measure accesses made by the inspected script through
	 *         the given measure access name.
	 */
	public List<MeasureAccess> getMeasureAccesses(String measureSourceName) {
		return measureAccessesMap.get(measureSourceName);
	}

}
