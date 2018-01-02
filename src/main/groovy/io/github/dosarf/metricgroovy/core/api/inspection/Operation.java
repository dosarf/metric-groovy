package io.github.dosarf.metricgroovy.core.api.inspection;

import java.util.List;

import javax.measure.unit.Unit;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public class Operation {
	private final OperationType type;
	private final List<Unit> operandUnits;

	public Operation(OperationType type, Unit... operandUnits) {
		this.type = type;
		this.operandUnits = Lists.newArrayList(operandUnits);
	}

	public OperationType getType() {
		return type;
	}

	public List<Unit> getInvolvedUnits() {
		return operandUnits;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("type", type)
				.add("operandUnits", operandUnits).toString();
	}
}
