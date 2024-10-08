package expression.impl;

import expression.api.Expression;
import sheet.cell.api.CellType;


public class NumberExpression implements Expression {
    private final double value;

    public NumberExpression(double value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String getOperationName()
    {
        return "Number";
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public CellType getCellType() {
        return CellType.NUMERIC;
    }

}