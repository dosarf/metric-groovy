package io.github.dosarf.metricgroovy.core.api.script;

import static io.github.dosarf.metricgroovy.core.api.MetricGroovyBuilders.evaluatorBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.dosarf.metricgroovy.core.api.MeasureSource;
import io.github.dosarf.metricgroovy.core.api.MeasureSourceProvider;
import io.github.dosarf.metricgroovy.core.api.MetricGroovyBuilders;
import io.github.dosarf.metricgroovy.core.api.VariableMeasureSource;
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorException;
import io.github.dosarf.metricgroovy.core.api.error.EvaluatorSyntaxException;
import io.github.dosarf.metricgroovy.core.api.error.IncompatibleUnitException;
import io.github.dosarf.metricgroovy.core.api.error.UndefinedMeasureException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.assertj.core.data.Offset;
import org.assertj.core.util.Objects;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.And;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("rawtypes")
public class MetricGroovyScriptTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private TestMeasureSource measureSource;

	@Before
	public void setUp() {
		measureSource = new TestMeasureSource();
		measureSource.put("SOG", Measure.valueOf(1.0, Unit.valueOf("m/s")));
		measureSource.put("consumption",
				Measure.valueOf(12340, Unit.valueOf("W*h")));
		measureSource.put("averagePower",
				Measure.valueOf(1234, Unit.valueOf("W")));
	}

	@Test
	public void simpleMeasure() throws EvaluatorException {
		Measure measure = (Measure) script("6.28 (m)").evaluate();
		assertMeasure(6.28, "m", measure);
	}

	@Test
	public void simpleDimensionlessMeasure() throws EvaluatorException {
		Measure measure = (Measure) script("6.28 ()").evaluate();
		assertMeasure(6.28, "", measure);
	}

	@Test
	public void simpleExpression() throws EvaluatorException {
		Measure measure = (Measure) script("6.28 (m) / 2.0 (s)").evaluate();
		assertMeasure(3.14, "m/s", measure);
	}

	@Test
	public void variableMeasureInOwnUnit() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG (m/s)").evaluate();
		assertMeasure(1.0, "m/s", measure);
	}

	@Test
	public void variableMeasureInDifferentUnit() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG (kn)").evaluate();
		assertMeasure(1.9438, "kn", measure);
	}

	@Test
	public void variableMeasureExpression() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG (kn) * 0.5 (h)")
				.evaluate();
		assertMeasure(0.9719, "nmi", measure);
	}

	// TODO - figure out a way to detect senseless operations like m/s*h
	@Ignore
	@Test
	public void weirdMultiplication() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG (m/s) * 1.0 (h)")
				.evaluate();
		assertMeasure(3600.0, "m", measure);
	}

	@Test
	public void dimensionlessVariableMeasure() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG ()").evaluate();
		assertMeasure(1.0, "", measure);
	}

	@Test
	public void dimensionlessFloatingPointMultiplication()
			throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG () * 1.0 ()")
				.evaluate();
		assertMeasure(1.0, "", measure);
	}

	@Test
	public void incompatibleUnits() throws EvaluatorException {
		thrown.expect(IncompatibleUnitException.class);
		thrown.expect(new And(Arrays.asList(ExceptionFieldMatcher.of(
				IncompatibleUnitException.class, IncompatibleUnitException::getRequestedUnit,
				Unit.valueOf("m"), "requestedUnit"), ExceptionFieldMatcher.of(
				IncompatibleUnitException.class, IncompatibleUnitException::getProvidedUnit,
				Unit.valueOf("m/s"), "providedUnit"))));

		script("measure.SOG (m)").evaluate();
	}

	@Test
	public void undefinedMeasure() throws EvaluatorException {
		thrown.expect(UndefinedMeasureException.class);
		thrown.expect(ExceptionFieldMatcher.of(
				UndefinedMeasureException.class, UndefinedMeasureException::getMeasureName,
				"COG", "measureName"));

		script("measure.COG (m)").evaluate();
	}

	@Test
	public void wrongNumberOfUnitCastArgs() throws EvaluatorException {
		thrown.expect(EvaluatorSyntaxException.class);
		thrown.expect(ExceptionFieldMatcher.of(
				EvaluatorSyntaxException.class, EvaluatorSyntaxException::getInvocationArgs,
				objectArray(Unit.valueOf("kn"), Unit.valueOf("m/s")), "invocationArgs"));
		
		script("measure.SOG (kn, m/s)").evaluate();
	}
	
	@Test
	public void wrongTypeOfUnitCastArg() throws EvaluatorException {
		thrown.expect(EvaluatorSyntaxException.class);
		thrown.expect(ExceptionFieldMatcher.of(
				EvaluatorSyntaxException.class, EvaluatorSyntaxException::getInvocationArgs,
				objectArray(12), "invocationArgs"));
		
		script("measure.SOG (12)").evaluate();
	}

	@Test
	public void dimensionlessIntegerMultiplication() throws EvaluatorException {
		Measure measure = (Measure) script("measure.SOG () * 1 ()").evaluate();
		assertMeasure(1.0, "", measure);
	}

	@Test
	public void multipleVariableProviders() throws EvaluatorException {
		TestMeasureSource measureSource1 = new TestMeasureSource("m1");
		measureSource1.put("SOG", Measure.valueOf(1.0, Unit.valueOf("m/s")));

		TestMeasureSource measureSource2 = new TestMeasureSource("m2");
		measureSource2.put("duration",
				Measure.valueOf(3600.0, Unit.valueOf("s")));

		Measure measure = (Measure) script("m1.SOG (km/h) * m2.duration (h)",
				measureSource1, measureSource2).evaluate();
		assertMeasure(3.6, "km", measure);
	}

	@Test
	public void measureSourceProvider() throws EvaluatorException {
		Measure measure = (Measure) script(
				"measure.records(year: 2014, month: 1).consumption (W*h)")
				.evaluate();
		assertMeasure(12340, "W*h", measure);
	}

	@Test
	public void measureSourceProviderDimensionless() throws EvaluatorException {
		Measure measure = (Measure) script(
				"measure.records(year: 2014, month: 1).consumption ()")
				.evaluate();
		assertMeasure(12340, "", measure);
	}

	@Test
	public void compoundMeasureSourceProvider() throws EvaluatorException {
		Measure measure = (Measure) script(
				"measure.records(year: 2014, month: 1).location(city: 'Espoo', suburb: 'Espoonlahti').averagePower (W)")
				.evaluate();
		assertMeasure(1234, "W", measure);
	}

	@Test
	public void customVariable() throws EvaluatorException {
		Map<String, Object> customVariables = ImmutableMap.<String, Object> of(
				"foo", "bar");
		MetricGroovyScript script = script("'bar' + foo", customVariables);

		assertThat((String) script.evaluate()).isEqualTo("barbar");
	}

	@Test
	public void returnValue() throws EvaluatorException {
		Map<String, Object> customVariables = ImmutableMap.<String, Object> of(
				"foo", "bar");
		MetricGroovyScript script = script(
				"myVariable = 'bar' + foo; return 5", customVariables);

		assertThat((Integer) script.evaluate()).isEqualTo(Integer.valueOf(5));
	}

	@Test
	public void getVariable() throws EvaluatorException {
		Map<String, Object> customVariables = ImmutableMap.<String, Object> of(
				"foo", "bar");
		MetricGroovyScript script = evaluatorBuilder("myVariable = 'bar' + foo")
				.customVariables(customVariables).build();

		script.evaluate();
		assertThat(script.getVariable("myVariable")).isEqualTo("barbar");
	}

	@Test
	public void supportedUnits() throws EvaluatorException {
		Map<String, Unit<? extends Quantity>> supportedUnits = ImmutableMap.of(
				"METER", (Unit<? extends Quantity>) Unit.valueOf("m"),
				"SECOND", (Unit<? extends Quantity>) Unit.valueOf("s"));
		MetricGroovyScript script = evaluatorBuilder("3.0 (METER/SECOND)")
				.supportedUnits(supportedUnits).build();

		assertMeasure(3.0, "m/s", (Measure) script.evaluate());
	}

	private void assertMeasure(double expectedValue, String expectedUnit,
			Measure measure) {
		Offset<Double> precision = Offset.offset(0.001);

		Object value = measure.getValue();
		if (value instanceof Double) {
			assertThat(expectedValue).isEqualTo((double) value, precision);
		} else if (value instanceof BigDecimal) {
			assertThat(expectedValue).isEqualTo(
					((BigDecimal) value).doubleValue(), precision);
		}

		assertThat(Unit.valueOf(expectedUnit)).isEqualTo(measure.getUnit());
	}

	private MetricGroovyScript script(String expression) {
		MetricGroovyBuilders.EvaluatorBuilder builder = MetricGroovyBuilders
				.evaluatorBuilder(expression);
		builder.measureSource(measureSource);
		return builder.build();
	}

	private MetricGroovyScript script(String expression,
			MeasureSource... measureSources) {
		MetricGroovyBuilders.EvaluatorBuilder builder = MetricGroovyBuilders
				.evaluatorBuilder(expression);
		for (MeasureSource measureSource : measureSources) {
			builder.measureSource(measureSource);
		}
		return builder.build();
	}

	private MetricGroovyScript script(String expression,
			Map<String, Object> customVariables) {
		MetricGroovyBuilders.EvaluatorBuilder builder = MetricGroovyBuilders
				.evaluatorBuilder(expression);
		builder.customVariables(customVariables);
		return builder.build();
	}

	public static class TestMeasureSource extends HashMap<String, Measure>
			implements VariableMeasureSource, MeasureSourceProvider {

		private static final long serialVersionUID = 1L;
		private final String name;

		TestMeasureSource() {
			this("measure");
		}

		TestMeasureSource(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Measure getMeasure(String name,
				Optional<Unit<? extends Quantity>> requestedUnit)
				throws EvaluatorException {
			if (!containsKey(name)) {
				throw new UndefinedMeasureException(name);
			}
			Measure measure = get(name);
			return measure;
		}

		@Override
		public MeasureSource getMeasureSource(String name,
				Map<String, Object> parameters) throws EvaluatorException {
			if (name.equals("records")
					&& parameters.equals(ImmutableMap.of("year", 2014, "month",
							1))) {
				return this;
			} else if (name.equals("location")
					&& parameters.equals(ImmutableMap.of("city", "Espoo",
							"suburb", "Espoonlahti"))) {
				return this;
			}
			throw new EvaluatorException("Query failed for " + name);
		}
	}

	private static class ExceptionFieldMatcher<T extends Exception, U> extends
			BaseMatcher {

		private final Class<T> clazz;
		private final Function<T, U> valueExtractor;
		private final U expectedValue;
		private final String fieldName;

		private ExceptionFieldMatcher(Class<T> clazz,
				Function<T, U> valueExtractor, U expectedValue, String fieldName) {
			this.clazz = clazz;
			this.valueExtractor = valueExtractor;
			this.expectedValue = expectedValue;
			this.fieldName = fieldName;
		}

		public static <T extends Exception, U> ExceptionFieldMatcher<T, U> of(
				Class<T> clazz, Function<T, U> valueExtractor, U expectedValue,
				String fieldName) {
			return new ExceptionFieldMatcher<>(clazz, valueExtractor,
					expectedValue, fieldName);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(Object item) {
			if (clazz.isAssignableFrom(item.getClass()))
				;

			U actualValue = valueExtractor.apply((T) item);

			return Objects.areEqual(expectedValue, actualValue);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(String
					.format("Matching field %s", fieldName));
		}
	}

	private static Object[] objectArray(Object ... items) {
		return items;
	}	
}
