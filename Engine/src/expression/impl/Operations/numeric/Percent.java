package expression.impl.Operations.numeric;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Percent extends BinaryExpression {
    public Percent(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "PERCENT";
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }

    @Override
    protected Object evaluate(Object part, Object whole) {
        if (part == null) {
            throw new IllegalArgumentException("First argument cannot be empty.");
        }
        if (whole == null) {
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }
        if (!(part instanceof Double) || !(whole instanceof Double)) {
            String actualType = part == null ? "null" : part.getClass().getSimpleName();
            return Double.NaN;
        }

        Double d1 = (Double) part;
        Double d2 = (Double) whole;

        return d1 * d2 / 100;
    }
}
