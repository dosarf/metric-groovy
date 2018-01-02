package io.github.dosarf.metricgroovy.core.impl.inspection;

import io.github.dosarf.metricgroovy.core.api.inspection.Operation;
import io.github.dosarf.metricgroovy.core.api.inspection.OperationType;
import io.github.dosarf.metricgroovy.core.impl.ExpressionHandler;
import io.github.dosarf.metricgroovy.core.impl.script.EvaluatorExpressionHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.measure.Measure;
import javax.measure.unit.Unit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@SuppressWarnings({"rawtypes", "unchecked" })
public class InspectorExpressionHandler implements ExpressionHandler {

	private Multimap<Unit, Unit> unitCompositions;
	private List<Measure> literals;
	private List<Operation> operations;
	
	public InspectorExpressionHandler() {
		unitCompositions = LinkedHashMultimap.create();
		literals = Lists.newLinkedList();
		operations = Lists.newLinkedList();
	}
	
	@Override
	public Unit multiply(Unit first, Unit second) {
		Unit result = first.times(second);
		recordUnitComposition(result, first, second);
		return result;
	}
	
	@Override
	public Unit div(Unit first, Unit second) {
		Unit result = first.divide(second);
		recordUnitComposition(result, first, second);
		return result;
	}

	@Override
	public Unit power(Unit base, Integer exponent) {
		Unit result = base.pow(exponent);
		recordUnitComposition(result, base);
		return result;
	}

	@Override
	public Measure call(Number number, Unit unit) {
		Measure result = EvaluatorExpressionHandler.createTypeCorrectMeasure(number, unit);
		literals.add(result);
		return result;
	}

	@Override
	public Measure call(Number number) {
		Measure result = EvaluatorExpressionHandler.createTypeCorrectMeasure(number, EvaluatorExpressionHandler.DIMENSIONLESS_UNIT);
		literals.add(result);
		return result;
	}

	@Override
	public Measure multiply(Measure first, Measure second) {
		Unit firstUnit = first.getUnit();
		Unit secondUnit = second.getUnit();
		Unit resultUnit = firstUnit.times(secondUnit);
		// ? recordUnitComposition(resultUnit, firstUnit, secondUnit);
		operations.add(new Operation(OperationType.MULTIPLICATION, firstUnit, secondUnit));
		return Measure.valueOf(1, resultUnit);
	}

	@Override
	public Measure div(Measure first, Measure second) {
		Unit firstUnit = first.getUnit();
		Unit secondUnit = second.getUnit();
		Unit resultUnit = firstUnit.divide(secondUnit);
		// ? recordUnitComposition(resultUnit, firstUnit, secondUnit);
		operations.add(new Operation(OperationType.DIVISION, firstUnit, secondUnit));
		return Measure.valueOf(1, resultUnit);
	}
	
	// TODO OBSOLETE
	@Deprecated
	public void report(PrintStream out) throws IOException {
		reportUnitCompositions(out);
		reportLiterals(out);
		reportOperations(out);
	}
	
	public Multimap<Unit, Unit> getUnitCompositions() {
		return ImmutableMultimap.copyOf(unitCompositions);
	}
	
	public List<Measure> getLiterals() {
		return ImmutableList.copyOf(literals);
	}
	
	public List<Operation> getOperations() {
		return ImmutableList.copyOf(operations);
	}
	
	private void reportUnitCompositions(PrintStream out) {
		out.println("Unit compisitions:");
		for (Unit composite : unitCompositions.keySet()) {
			out.println(String.format("  unit %s is a compisite of:", composite.toString()));
			for (Unit unit : unitCompositions.get(composite)) {
				out.println(String.format("    unit %s", unit.toString()));
			}
		}
	}

	private void reportLiterals(PrintStream out) {
		out.println("Literals:");
		for (Measure literal: literals) {
			if (literal.getUnit().equals(EvaluatorExpressionHandler.DIMENSIONLESS_UNIT)) {
				out.println(String.format("  literal %s is left dimensionless", literal.getValue().toString()));
			} else {
				out.println(String.format("  literal %s as %s", literal.getValue(), literal.getUnit()));
			}
		}
		
	}

	private void reportOperations(PrintStream out) {
		out.println("Operations:");
		for (Operation operation: operations) {
			out.println(String.format("  %s", operation.toString()));
		}
	}
	
	private void recordUnitComposition(Unit result, Unit ... operandUnits) {
		for (Unit operandUnit: operandUnits) {
			if (!unitCompositions.containsEntry(result, operandUnit)) {
				unitCompositions.put(result, operandUnit);
			}
		}
	}

}
