package expression.impl.Operations.numeric;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Pow extends BinaryExpression {
    public Pow(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "POW";
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

        double base = (Double) e1;
        double exponent = (Double) e2;

        if (base == 0 && exponent < 0) {
            return Double.NaN;
        }

        return Math.pow(base, exponent);
    }
}