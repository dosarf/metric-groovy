package io.github.dosarf.metricgroovy.core.impl.script;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("rawtypes")
public abstract class Calculator<T extends Number> {
	
	public abstract Number multiply(Number first, Number second);

	public abstract Number div(Number first, Number second);

	public abstract T convert(Number value);
	
	@SuppressWarnings("unchecked")
	public static Calculator<? extends Number> getArithmeticsFor(Number first,
			Number second) {
		Integer firstRank = RANKS.get(first.getClass());
		Integer secondRank = RANKS.get(second.getClass());
		Integer higherRank = Math.max(firstRank, secondRank);
		return CALCULATORS.get(higherRank);
	}

	private static Map<Class<? extends Number>, Integer> RANKS;
	private static Map<Integer, Calculator> CALCULATORS;

	static {
		RANKS = ImmutableMap.<Class<? extends Number>, Integer> of(
				Integer.class, 1, Long.class, 2, BigDecimal.class, 3,
				Float.class, 4, Double.class, 5);
		CALCULATORS = ImmutableMap.<Integer, Calculator> of(1,
				new IntegerCalculator(), 2, new LongCalculator(), 3,
				new BigDecimalCalculator(), 4, new FloatCalculator(), 5,
				new DoubleCalculator());
	}

	private static class IntegerCalculator extends Calculator<Integer> {

		@Override
		public Number multiply(Number first, Number second) {
			return convert(first) * convert(second);
		}

		@Override
		public Number div(Number first, Number second) {
			return convert(first) / convert(second);
		}

		@Override
		public Integer convert(Number value) {
			return (value instanceof Integer) ? (Integer)value : value.intValue();
		}

	}

	private static class LongCalculator extends Calculator<Long> {

		@Override
		public Number multiply(Number first, Number second) {
			return convert(first) * convert(second);
		}

		@Override
		public Number div(Number first, Number second) {
			return convert(first) / convert(second);
		}

		@Override
		public Long convert(Number value) {
			return (value instanceof Long) ? (Long)value : value.longValue();
		}

	}

	private static class BigDecimalCalculator extends Calculator<BigDecimal> {

		@Override
		public Number multiply(Number first, Number second) {
			return convert(first).multiply(convert(second));
		}

		@Override
		public Number div(Number first, Number second) {
			return convert(first).divide(convert(second));
		}

		@Override
		public BigDecimal convert(Number value) {
			return (value instanceof BigDecimal) ? (BigDecimal)value : BigDecimal.valueOf(value.doubleValue());
		}

	}

	private static class FloatCalculator extends Calculator<Float> {

		@Override
		public Number multiply(Number first, Number second) {
			return convert(first) * convert(second);
		}

		@Override
		public Number div(Number first, Number second) {
			return convert(first) / convert(second);
		}

		@Override
		public Float convert(Number value) {
			return (value instanceof Float) ? (Float)value : value.floatValue();
		}

	}

	private static class DoubleCalculator extends Calculator<Double> {

		@Override
		public Number multiply(Number first, Number second) {
			return convert(first) * convert(second);
		}

		@Override
		public Number div(Number first, Number second) {
			return convert(first) / convert(second);
		}

		@Override
		public Double convert(Number value) {
			return (value instanceof Double) ? (Double)value : value.doubleValue();
		}

	}

}
