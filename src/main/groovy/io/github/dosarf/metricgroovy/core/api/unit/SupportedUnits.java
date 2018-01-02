package io.github.dosarf.metricgroovy.core.api.unit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Force;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import com.google.common.collect.ImmutableMap;


@SuppressWarnings("unchecked")
public class SupportedUnits {
	// TODO add more units (all relevant members from SI and NonSI classes)

	public static final Unit<Length> m = (Unit<Length>) Unit.valueOf("m");
	public static final Unit<Length> km = (Unit<Length>) Unit.valueOf("km");
	public static final Unit<Length> nmi = (Unit<Length>) Unit.valueOf("nmi");
	public static final Unit<Duration> h = (Unit<Duration>) Unit.valueOf("h");
	public static final Unit<Duration> s = (Unit<Duration>) Unit.valueOf("s");
	public static final Unit<Force> N = (Unit<Force>) Unit.valueOf("N");
	public static final Unit<Velocity> kn = (Unit<Velocity>) Unit.valueOf("kn");
	public static final Unit<Power> W = (Unit<Power>) Unit.valueOf("W");

	public static final Unit<Angle> rad = (Unit<Angle>) Unit.valueOf("rad");

	public static final Unit<Angle> deg = NonSI.DEGREE_ANGLE;

	public static final Map<String, Unit<? extends Quantity>> UNITS;

	static {
		Map<String, Unit<? extends Quantity>> units = new HashMap<>();
		for (Field field : SupportedUnits.class.getFields()) {
			if (isPublicStaticFinal(field) && Unit.class.isAssignableFrom(field.getType())) {
				Unit<? extends Quantity> value;
				try {
					value = (Unit<? extends Quantity>)field.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				units.put(field.getName(), value);
			}
		}
		UNITS = ImmutableMap.copyOf(units);
	}

	private static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
	}
}
