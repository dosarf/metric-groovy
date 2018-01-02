package io.github.dosarf.metricgroovy.core.impl.script

import javax.measure.Measure
import javax.measure.unit.Unit

import io.github.dosarf.metricgroovy.core.impl.ExpressionHandler

// TODO Does this class need to be .groovy? Can't it be .java?
class EvaluatorExtension {

	// TODO figure out how to switch META-INF/services/org.codehaus.groovy.runtime.ExtensionModule
	// using class loaders OR, figure out
	//      (a) how to disable this for other groovy evaluations within the same process
	//      (a) and how to enable different ExtensionClass for different groovy evaluations

	static ExpressionHandler _exp_handler;

	static Unit multiply(Unit first, Unit second) {
		return _exp_handler.multiply(first, second)
	}

	static Unit div(Unit first, Unit second) {
		return _exp_handler.div(first, second)
	}

	static Unit power(Unit base, Integer exponent) {
		return _exp_handler.power(base, exponent)
	}

	static Measure call(Number number, Unit unit) {
		return _exp_handler.call(number, unit)
	}

	static Measure call(Number number) {
		return _exp_handler.call(number)
	}

	static Measure multiply(Measure first, Measure second) {
		return _exp_handler.multiply(first, second)
	}

	static Measure div(Measure first, Measure second) {
		return _exp_handler.div(first, second)
	}
}
