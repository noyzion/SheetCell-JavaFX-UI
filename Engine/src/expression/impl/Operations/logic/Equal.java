package expression.impl.Operations.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Equal extends BinaryExpression {


    public Equal(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "EQUAL";
    }

    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

    @Override
    protected Object evaluate(Object e1, Object e2) {
        if (e1 == null) {
            throw new IllegalArgumentException("First argument cannot be empty.");
        }
        if (e2 == null) {
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }

        if (e1.getClass() != e2.getClass()) {
            return false;
        }

        if (e1 instanceof Number && e2 instanceof Number) {
            return ((Number) e1).doubleValue() == ((Number) e2).doubleValue();
        } else if (e1 instanceof String && e2 instanceof String) {
            return e1.equals(e2);
        } else if (e1 instanceof Boolean && e2 instanceof Boolean) {
            return e1.equals(e2);
        }
        return false;
    }
}
