package expression.impl.Operations.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class And extends BinaryExpression {


    public And(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "AND";
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
        if (!(e1 instanceof Boolean) || !(e2 instanceof Boolean)) {
            String actualType = e1 == null ? "null" : e1.getClass().getSimpleName();
            return "UNKNOWN";        }

        return (Boolean)e1&& (Boolean)e2;
    }
}
