package io.github.dosarf.metricgroovy.core.api.unit

import static org.assertj.core.api.Assertions.*

import static io.github.dosarf.metricgroovy.core.api.unit.SupportedUnits.*
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExpressionHandler
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExtension

import javax.measure.unit.Unit
import javax.measure.unit.UnitFormat

import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JScienceUnitOperationsTest {

	@Before
	public void setUp() {
		EvaluatorExtension._exp_handler = new EvaluatorExpressionHandler()
	}

	@Test
	void unitDivision() {
		Unit speedUnit = m/s
		assertThat(speedUnit.toString()).isEqualTo('m/s')
	}
	
	@Test
	public void unitMultiplication() {
		Unit speedUnit = m/s
		Unit distanceUnit = speedUnit*s
		assertThat(distanceUnit.toString()).isEqualTo('m')
	}
	
	@Test
	public void unitPositivePower() {
		Unit seconds2 = s**2
		StringBuilder sb = new StringBuilder()
		UnitFormat.getUCUMInstance().format(seconds2, sb)
		assertThat(sb.toString()).isEqualTo('s^2')
		
		Unit acceleration = m/s**2
		
		Unit speed = acceleration * s
		assertThat(speed.toString()).isEqualTo('m/s')
	}
	
	@Test
	public void unitNegativePower() {
		Unit frequency = s ** -1
		assertThat(frequency.toString()).isEqualTo('1/s')
		
		Unit pressure = N * m**-2
		
		Unit force = pressure * m**2
		assertThat(force.toString()).isEqualTo('N')
	}
	

}
