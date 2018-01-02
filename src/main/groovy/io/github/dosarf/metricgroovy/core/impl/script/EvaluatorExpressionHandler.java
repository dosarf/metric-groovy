package io.github.dosarf.metricgroovy.core.impl.script;

import io.github.dosarf.metricgroovy.core.impl.ExpressionHandler;

import java.math.BigDecimal;

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.unit.Unit;


@SuppressWarnings({"rawtypes", "unchecked" })
public class EvaluatorExpressionHandler implements ExpressionHandler {

	@Override
	public Unit multiply(Unit first, Unit second) {
		return first.times(second);
	}

	@Override
	public Unit div(Unit first, Unit second) {
		return first.divide(second);
	}

	@Override
	public Unit power(Unit base, Integer exponent) {
		return base.pow(exponent);
	}

	@Override
	public Measure call(Number number, Unit unit) {
		return createTypeCorrectMeasure(number, unit);
	}

	@Override
	public Measure call(Number number) {
		return createTypeCorrectMeasure(number, DIMENSIONLESS_UNIT);
	}

	@Override
	public Measure multiply(Measure first, Measure second) {
		Number firstValue = (Number)first.getValue();
		Number secondValue = (Number)second.getValue();
		Calculator arithmetics = Calculator.getArithmeticsFor(firstValue, secondValue);
		Number resultValue = arithmetics.multiply(firstValue, secondValue);
		return createTypeCorrectMeasure(resultValue, first.getUnit().times(second.getUnit()));
	}

	@Override
	public Measure div(Measure first, Measure second) {
		Number firstValue = (Number)first.getValue();
		Number secondValue = (Number)second.getValue();
		Calculator arithmetics = Calculator.getArithmeticsFor(firstValue, secondValue);
		Number resultValue = arithmetics.div(firstValue, secondValue);
		return createTypeCorrectMeasure(resultValue, first.getUnit().divide(second.getUnit()));
	}

	public static Measure createTypeCorrectMeasure(Number number, Unit unit) {
		if (number instanceof BigDecimal) {
			return DecimalMeasure.valueOf((BigDecimal)number, unit);
		} else if (number instanceof Long) {
			return Measure.valueOf((Long)number, unit);
		} else if (number instanceof Integer) {
			return Measure.valueOf((Integer)number, unit);
		} else if (number instanceof Double) {
			return Measure.valueOf((Double)number, unit);
		} else if (number instanceof Float) {
			return Measure.valueOf((Float)number, unit);
		} else {
			throw new IllegalArgumentException("Don't know how to handle nubmer of type " + number.getClass());
		}
	}
	
}
