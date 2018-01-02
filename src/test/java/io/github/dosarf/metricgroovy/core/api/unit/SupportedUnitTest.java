package io.github.dosarf.metricgroovy.core.api.unit;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.dosarf.metricgroovy.core.api.unit.SupportedUnits;

import javax.measure.unit.Unit;

import org.junit.Test;

public class SupportedUnitTest {

	@Test
	public void bindableSupportedUnits() {
		assertThat(Unit.valueOf("m")).isEqualTo(SupportedUnits.UNITS.get("m"));
	}

}
