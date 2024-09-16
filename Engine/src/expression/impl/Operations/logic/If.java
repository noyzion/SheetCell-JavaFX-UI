package expression.impl.Operations.logic;

import expression.api.Expression;
import expression.impl.TrinaryExpression;
import sheet.cell.api.CellType;

public class If extends TrinaryExpression {
    public If(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }


    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }

    @Override
    public String getOperationName() {
        return "IF";
    }

    @Override
    protected Object evaluate(Object evaluate1, Object evaluate2, Object evaluate3) {
        if (evaluate1 == null) {
            throw new IllegalArgumentException("First argument cannot be empty.");
        }
        if (evaluate2 == null) {
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }
        if (evaluate3 == null) {
            throw new IllegalArgumentException("Third argument cannot be empty.");
        }
        if (!(evaluate1 instanceof Boolean b1)) {
            return "UNKNOWN";
        }
        if (evaluate2.getClass() != evaluate3.getClass()) {
            throw new IllegalArgumentException("IF operation requires `then` and `else` arguments to be of the same type. but then is " + evaluate2.getClass().getSimpleName() +
                    "and else is " + evaluate3.getClass().getSimpleName());
        }

        Boolean condition = (Boolean) evaluate1;
        return condition ? evaluate2 : evaluate3;
    }
}
