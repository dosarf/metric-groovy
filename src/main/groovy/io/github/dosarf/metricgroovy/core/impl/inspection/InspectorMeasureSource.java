package io.github.dosarf.metricgroovy.core.impl.inspection;

import io.github.dosarf.metricgroovy.core.api.MeasureSource;
import io.github.dosarf.metricgroovy.core.api.MeasureSourceProvider;
import io.github.dosarf.metricgroovy.core.api.VariableMeasureSource;
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;
import io.github.dosarf.metricgroovy.core.api.inspection.MeasureAccess;
import io.github.dosarf.metricgroovy.core.api.inspection.MeasureAccessStep;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class InspectorMeasureSource implements VariableMeasureSource,
		MeasureSourceProvider {

	private final String name;
	private List<MeasureAccess> measureAccesses;

	public InspectorMeasureSource(String name) {
		this.name = name;
		measureAccesses = Lists.newLinkedList();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Measure getMeasure(String name,
			Optional<Unit<? extends Quantity>> requestedUnitOptional) {
		Unit requestedUnit = getRequestedUnit(requestedUnitOptional);
		measureAccesses.add(new MeasureAccess(name, requestedUnit));
		return Measure.valueOf(1, requestedUnit);
	}

	@Override
	public MeasureSource getMeasureSource(String name,
			Map<String, Object> parameters) throws EvaluatorException {
		return new ComplexMeasureAccessCollector(this).getMeasureSource(name,
				parameters);
	}

	public List<MeasureAccess> getMeasureAccesses() {
		return ImmutableList.copyOf(measureAccesses);
	}

	private void add(MeasureAccess measureAccess) {
		measureAccesses.add(measureAccess);
	}

	private static Unit getRequestedUnit(
			Optional<Unit<? extends Quantity>> requestedUnit) {
		return requestedUnit.isPresent() ? requestedUnit.get() : null;
	}

	private static class ComplexMeasureAccessCollector implements
			MeasureSourceProvider, VariableMeasureSource {

		private final InspectorMeasureSource parent;
		private List<MeasureAccessStep> accessPath;

		private ComplexMeasureAccessCollector(InspectorMeasureSource parent) {
			this.parent = parent;
			accessPath = Lists.newLinkedList();
		}

		@Override
		public String getName() {
			return "inspector";
		}

		@Override
		public MeasureSource getMeasureSource(String name,
				Map<String, Object> parameters) throws EvaluatorException {
			accessPath.add(new MeasureAccessStep(name, parameters));
			return this;
		}

		@Override
		public Measure getMeasure(String name,
				Optional<Unit<? extends Quantity>> requestedUnitOptional)
				throws EvaluatorException {
			Unit requestedUnit = getRequestedUnit(requestedUnitOptional);
			MeasureAccess measureAccess = new MeasureAccess(name,
					requestedUnit, accessPath);
			parent.add(measureAccess);
			return Measure.valueOf(1, requestedUnit);
		}

	}

}
