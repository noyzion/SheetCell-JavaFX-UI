package expression.impl.Operations.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Bigger extends BinaryExpression {
    public Bigger(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "BIGGER";
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
        if (!(e1 instanceof Double) || !(e2 instanceof Double)) {
            String actualType = e1 == null ? "null" : e1.getClass().getSimpleName();
            return "UNKOWN";
        }

        Double d1 = (Double) e1;
        Double d2 = (Double) e2;

        return d1 >= d2;
    }
}

