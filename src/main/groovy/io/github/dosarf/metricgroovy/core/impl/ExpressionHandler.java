package io.github.dosarf.metricgroovy.core.impl;

import javax.measure.Measure;
import javax.measure.unit.Unit;

@SuppressWarnings("rawtypes")
public interface ExpressionHandler {

	static final Unit DIMENSIONLESS_UNIT = Unit.valueOf("");

	// Operator implementations for creating compound units in groovy
	// like 'm / s' or 'm / s**2'
	Unit multiply(Unit first, Unit second);

	Unit div(Unit first, Unit second);

	Unit power(Unit base, Integer exponent);

	//
	// Operator implementations for creating measure literals in groovy like
	// '5.2 (m)' (for meter dimension) and '5.2 ()' (for dimensionless unit)
	//
	//
	// TODO include scalar unit operations also supported by JScience and
	// enable them through EvaluatorExtension
	//
	Measure call(Number number, Unit unit);

	Measure call(Number number);

	//
	// Operator implementations for performing operations on measurements
	//
	//
	// TODO support all operators for measures that can be possible used (+, -,
	// ...)
	// TODO support some notable Math function calls for measures where it makes
	// sense (e.g. trig functions for degrees, min/max for compatible units,
	// comparision operators (<, <=, ...), ...)
	//
	Measure multiply(Measure first, Measure second);

	Measure div(Measure first, Measure second);
}
