package io.github.dosarf;

import static org.assertj.core.api.Assertions.assertThat;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.METRE_PER_SECOND;
import static tec.units.ri.unit.Units.RADIAN;
import static tec.units.ri.unit.Units.SECOND;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import org.junit.Test;

import tec.units.ri.AbstractUnit;
import tec.units.ri.format.SimpleUnitFormat;
import tec.units.ri.quantity.Quantities;

public class UnitApiTest {

	@Test
	public void quantityScaling() {
		Quantity<Length> length = Quantities.getQuantity(12.0, METRE);

		assertThat(Quantities.getQuantity(24.0, METRE)).isEqualTo(
				length.multiply(2));
	}

	@Test
	public void quantityConversion() {
		Quantity<Length> length = Quantities.getQuantity(12.0, METRE);
		Quantity<Length> length2 = Quantities.getQuantity(1200.0, CENTI(METRE));

		assertThat(length2.to(METRE)).isEqualTo(length);
	}

	@Test
	public void quantityDivision() {
		Quantity<Speed> speed = Quantities.getQuantity(4.0, METRE_PER_SECOND);
		Quantity<Length> length = Quantities.getQuantity(8.0, METRE);
		Quantity<Time> time = Quantities.getQuantity(2.0, SECOND);

		assertThat(length.divide(time)).isEqualTo(speed);
	}

	@Test
	public void unitConversion() {
		Unit<?> speedUnit = METRE.divide(SECOND);

		assertThat(speedUnit).isEqualTo(METRE_PER_SECOND);
		assertThat(speedUnit).isNotEqualTo(METRE);
	}

	@Test
	public void parseUnit() {
		Unit<?> speedUnit = AbstractUnit.parse("m/s");

		assertThat(speedUnit).isEqualTo(METRE_PER_SECOND);
		assertThat(speedUnit).isNotEqualTo(METRE);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void parseLocalScopedCustomUnit() {
		UnitFormat unitFormat = SimpleUnitFormat.getInstance();
		unitFormat.label(METRE.multiply(0.3048), "ft");

		Unit<Length> feetUnit = (Unit<Length>) unitFormat.parse("ft");
		Quantity<Length> length = Quantities.getQuantity(1.0, feetUnit);

		assertThat(length.to(METRE)).isEqualTo(
				Quantities.getQuantity(0.3048, METRE));

		Unit<Speed> speedUnit = (Unit<Speed>) unitFormat.parse("ft/s");
		Quantity<Speed> speed = Quantities.getQuantity(1.0, speedUnit);

		assertThat(speed.to(METRE_PER_SECOND)).isEqualTo(
				Quantities.getQuantity(0.3048, METRE_PER_SECOND));
	}

	@Test
	public void angleUnits() {
		UnitFormat unitFormat = SimpleUnitFormat.getInstance();
		unitFormat.label(RADIAN.multiply(Math.PI / 180.0), "deg");

		@SuppressWarnings("unchecked")
		Unit<Angle> degreeUnit = (Unit<Angle>) unitFormat.parse("deg");
		Quantity<Angle> PI_2 = Quantities.getQuantity(90.0, degreeUnit);

		assertThat(PI_2.to(RADIAN)).isEqualTo(
				Quantities.getQuantity(Math.PI / 2.0, RADIAN));
	}

}
