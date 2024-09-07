package expression.impl.Operations.string;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Concat extends BinaryExpression {
    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "CONCAT";
    }

    @Override
    public CellType getCellType() {
        return CellType.STRING;
    }

    @Override
    protected Object evaluate(Object e1, Object e2) {
        if (e1 == null) {
            throw new IllegalArgumentException("First argument cannot be empty.");
        }
        if (e2 == null) {
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }
        if (!(e1 instanceof String str1)) {
            return "!UNDEFINED!";
        }

        if (!(e2 instanceof String str2)) {
            return "!UNDEFINED!";
        }

        return str1 + str2;
    }
}
