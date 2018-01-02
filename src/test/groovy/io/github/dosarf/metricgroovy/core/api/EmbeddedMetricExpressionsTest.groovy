package io.github.dosarf.metricgroovy.core.api

import static org.assertj.core.api.Assertions.*

import static io.github.dosarf.metricgroovy.core.api.unit.SupportedUnits.*
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExpressionHandler
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExtension

import javax.measure.Measure
import javax.measure.unit.Unit

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EmbeddedMetricExpressionsTest {

	@Before
	public void setUp() {
		EvaluatorExtension._exp_handler = new EvaluatorExpressionHandler()
	}

	@Test
	public void measureCasting() {
		Measure speed = 5.1 (m/s)
		assertThat(speed.toString()).isEqualTo('5.1 m/s')

		Measure duration = 2.3 (s)
		assertThat(duration.toString()).isEqualTo('2.3 s')

		Measure distance = speed * duration
		assertThat(distance.toString()).isEqualTo('11.73 m')
	}

	@Test
	public void dimensionlessUnit() {
		Measure captains = 3 ()
		assertThat(captains.getValue()).isEqualTo(3)
		assertThat(captains.getUnit()).isEqualTo(Unit.valueOf(''))
	}

	@Test
	public void measureMultiplication() {
		Measure distance = 5.1 (m/s) * 2.3 (s)
		assertThat(distance.toString()).isEqualTo('11.73 m')
	}

	@Test
	public void measureDivision() {
		Measure integerDuration = 10 (km) / 4 (km/h)
		assertThat(integerDuration.toString()).isEqualTo('2 h')

		Measure doubleDuration = 10.0 (km) / 4 (km/h)
		assertThat(doubleDuration.toString()).isEqualTo('2.5 h')
	}
}
