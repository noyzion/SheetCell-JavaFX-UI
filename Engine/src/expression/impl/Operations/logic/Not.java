package expression.impl.Operations.logic;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import sheet.cell.api.CellType;

public class Not extends UnaryExpression {

    public Not(Expression ex1) {
        super(ex1);
    }

    @Override
    public String getOperationName() {
        return "NOT";
    }

    @Override
    public CellType getCellType() {
        return CellType.BOOLEAN;
    }
    @Override
    protected Object evaluate(Object e1) {
        if (e1 == null) {
            throw new IllegalArgumentException("Argument cannot be empty.");
        }

        if (!(e1 instanceof Boolean)) {
            return "UNKNOWN";
        }
       return !(Boolean) e1;
    }
}
