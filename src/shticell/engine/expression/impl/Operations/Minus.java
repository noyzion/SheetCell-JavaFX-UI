package shticell.engine.expression.impl.Operations;

import shticell.engine.expression.api.Expression;
import shticell.engine.expression.impl.BinaryExpression;
import shticell.engine.sheet.cell.api.CellType;

public class Minus extends BinaryExpression {

    public Minus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "MINUS";
    }


    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    protected Object evaluate(Object e1, Object e2) {

        if (e1 == null) {
            throw new IllegalArgumentException("First argument cannot be empty.");
        }
        if (e2 == null) {
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }

        if (!(e1 instanceof Double) || !(e2 instanceof Double)) {
          return Double.NaN;
        }

        double num1 = (Double) e1;
        double num2 = (Double) e2;

        return num1 - num2;
    }
}