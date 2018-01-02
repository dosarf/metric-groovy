package io.github.dosarf.metricgroovy.core.api.inspection;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.dosarf.metricgroovy.core.api.MetricGroovyBuilders;
import io.github.dosarf.metricgroovy.core.api.MetricGroovyBuilders.InspectorBuilder;
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;

import java.util.List;
import java.util.Map;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@SuppressWarnings("rawtypes")
public class MetricGroovyScriptInspectorTest {

	@Test
	public void vanillaInspection() throws EvaluatorException {
		final Unit<? extends Quantity> metersPerSeconds = Unit.valueOf("m/s");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("1.0 (m/s)");
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(metersPerSeconds).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).hasSize(1);
		verifyMeasure(Measure.valueOf(1.0, metersPerSeconds), literals.get(0));

		assertThat(report.getOperations()).isEmpty();

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.size()).isEqualTo(2);
		List<Unit> unitComponents = Lists.newArrayList(unitCompositions
				.get(metersPerSeconds));
		assertThat(unitComponents).containsExactly(Unit.valueOf("m"),
				Unit.valueOf("s"));

		assertThat(report.getMeasureSourceNames()).isEmpty();
	}

	@Test
	public void customVariable() throws EvaluatorException {
		final Unit<? extends Quantity> metersPerSeconds = Unit.valueOf("m/s");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("one (m/s)");
		builder.customVariables(ImmutableMap.<String, Object> of("one",
				Double.valueOf(1.0)));
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(metersPerSeconds).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).hasSize(1);
		verifyMeasure(Measure.valueOf(1.0, metersPerSeconds), literals.get(0));

		assertThat(report.getOperations()).isEmpty();

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.size()).isEqualTo(2);
		List<Unit> unitComponents = Lists.newArrayList(unitCompositions
				.get(metersPerSeconds));
		assertThat(unitComponents).containsExactly(Unit.valueOf("m"),
				Unit.valueOf("s"));

		assertThat(report.getMeasureSourceNames()).isEmpty();
	}

	@Test
	public void supportedUnits() throws EvaluatorException {
		Map<String, Unit<? extends Quantity>> supportedUnits = ImmutableMap.of(
				"METER", (Unit<? extends Quantity>) Unit.valueOf("m"),
				"SECOND", (Unit<? extends Quantity>) Unit.valueOf("s"));

		final Unit<? extends Quantity> metersPerSeconds = Unit.valueOf("m/s");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("1.0 (METER/SECOND)");
		builder.supportedUnits(supportedUnits);
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(metersPerSeconds).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).hasSize(1);
		verifyMeasure(Measure.valueOf(1.0, metersPerSeconds), literals.get(0));

		assertThat(report.getOperations()).isEmpty();

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.size()).isEqualTo(2);
		List<Unit> unitComponents = Lists.newArrayList(unitCompositions
				.get(metersPerSeconds));
		assertThat(unitComponents).containsExactly(Unit.valueOf("m"),
				Unit.valueOf("s"));

		assertThat(report.getMeasureSourceNames()).isEmpty();
	}

	@Test
	public void measureSource() throws EvaluatorException {
		final Unit<? extends Quantity> metersPerSeconds = Unit.valueOf("m/s");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("records.speedOverGround (m/s)");
		builder.useMeasureSourceName("records");
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(metersPerSeconds).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).isEmpty();

		assertThat(report.getOperations()).isEmpty();

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.size()).isEqualTo(2);
		List<Unit> unitComponents = Lists.newArrayList(unitCompositions
				.get(metersPerSeconds));
		assertThat(unitComponents).containsExactly(Unit.valueOf("m"),
				Unit.valueOf("s"));

		assertThat(report.getMeasureSourceNames()).containsExactly("records");

		List<MeasureAccess> measureAccesses = report
				.getMeasureAccesses("records");
		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("speedOverGround", metersPerSeconds,
						ImmutableList.of()));
	}

	@Test
	public void dimensionlessCast() throws EvaluatorException {
		final Unit<? extends Quantity> dimensionless = Unit.valueOf("");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("records.speedOverGround () * 2.0 ()");
		builder.useMeasureSourceName("records");
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(dimensionless).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).hasSize(1);

		assertThat(report.getOperations()).hasSize(1);

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.isEmpty()).isTrue();

		assertThat(report.getMeasureSourceNames()).containsExactly("records");

		List<MeasureAccess> measureAccesses = report
				.getMeasureAccesses("records");
		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("speedOverGround", dimensionless,
						ImmutableList.of()));
	}

	@Test
	public void multipleMeasureSourceAndOperations() throws EvaluatorException {
		final Unit<? extends Quantity> metersPerSeconds = Unit.valueOf("m/s");
		final Unit<? extends Quantity> meters = Unit.valueOf("m");
		final Unit<? extends Quantity> seconds = Unit.valueOf("s");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("records1.distance (m) / records2.duration (s)");
		builder.useMeasureSourceName("records1");
		builder.useMeasureSourceName("records2");
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(metersPerSeconds).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).isEmpty();

		List<Operation> operations = report.getOperations();
		assertThat(operations).hasSize(1);
		assertThat(operations.get(0).getType()).isEqualTo(
				OperationType.DIVISION);

		List<Unit> involvedUnits = operations.get(0).getInvolvedUnits();
		assertThat(involvedUnits).containsOnly(meters, seconds);

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.isEmpty()).isTrue();

		assertThat(report.getMeasureSourceNames()).containsExactly("records1",
				"records2");

		List<MeasureAccess> measureAccesses = report
				.getMeasureAccesses("records1");
		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("distance", meters, ImmutableList.of()));

		measureAccesses = report.getMeasureAccesses("records2");
		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("duration", seconds, ImmutableList.of()));
	}

	@Test
	public void measureSourceProvider() throws EvaluatorException {
		final Unit<? extends Quantity> kilowattHours = Unit.valueOf("kW*h");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("records.period(year: 2014, month: 'June').consumption (kW*h)");
		builder.useMeasureSourceName("records");
		builder.supportedUnits(ImmutableMap.of("kW", Unit.valueOf("kW"), "h",
				Unit.valueOf("h")));
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(Measure.class.isAssignableFrom(report.getResultType()))
				.isTrue();
		assertThat(kilowattHours).isEqualTo(report.getResultUnit());

		List<Measure> literals = report.getLiterals();
		assertThat(literals).isEmpty();

		assertThat(report.getOperations()).isEmpty();

		Multimap<Unit, Unit> unitCompositions = report.getUnitCompositions();
		assertThat(unitCompositions.size()).isEqualTo(2);
		List<Unit> unitComponents = Lists.newArrayList(unitCompositions
				.get(kilowattHours));
		assertThat(unitComponents).containsExactly(Unit.valueOf("kW"),
				Unit.valueOf("h"));

		assertThat(report.getMeasureSourceNames()).containsExactly("records");

		List<MeasureAccess> measureAccesses = report
				.getMeasureAccesses("records");
		MeasureAccessStep expectedMeasureAccessStep = new MeasureAccessStep(
				"period", ImmutableMap.of("year", 2014, "month", "June"));
		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("consumption", kilowattHours, ImmutableList
						.of(expectedMeasureAccessStep)));
	}

	@Test
	public void measureSourceProviderMultipleAccessSteps()
			throws EvaluatorException {
		final Unit<? extends Quantity> kilowattHours = Unit.valueOf("kW*h");

		InspectorBuilder builder = MetricGroovyBuilders
				.inspectorBuilder("records.area(city: 'Espoo').period(year: 2014, month: 'June').consumption (kW*h)");
		builder.useMeasureSourceName("records");
		builder.supportedUnits(ImmutableMap.of("kW", Unit.valueOf("kW"), "h",
				Unit.valueOf("h")));
		MetricGroovyScriptInspector inspector = builder.build();
		Report report = inspector.inspect();

		assertThat(report.getMeasureSourceNames()).containsExactly("records");

		List<MeasureAccess> measureAccesses = report
				.getMeasureAccesses("records");

		MeasureAccessStep expectedMeasureAccessStep1 = new MeasureAccessStep(
				"area", ImmutableMap.of("city", "Espoo"));
		MeasureAccessStep expectedMeasureAccessStep2 = new MeasureAccessStep(
				"period", ImmutableMap.of("year", 2014, "month", "June"));

		assertThat(measureAccesses).containsExactly(
				new MeasureAccess("consumption", kilowattHours, ImmutableList
						.of(expectedMeasureAccessStep1,
								expectedMeasureAccessStep2)));
	}

	private void verifyMeasure(Measure expected, Measure actual) {
		assertThat(expected.getValue().toString()).isEqualTo(
				actual.getValue().toString());
		assertThat(expected.getUnit()).isEqualTo(actual.getUnit());
	}

}
