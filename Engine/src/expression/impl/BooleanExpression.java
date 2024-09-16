package expression.impl;

import expression.api.Expression;
import sheet.cell.api.CellType;

public class BooleanExpression  implements Expression {
    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String getOperationName()
    {
        return "Boolean";
    }

    @Override
    public String toString() {
        return String.valueOf(value).toUpperCase();
    }

    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

}