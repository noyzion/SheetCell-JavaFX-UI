package expression.impl.Operations.numeric;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import sheet.cell.api.CellType;

public class Mod extends BinaryExpression {

    public Mod(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationName() {
        return "MOD";
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
            double numerator = (Double) e1;
            double denominator = (Double) e2;

            if (denominator == 0) {
                return Double.NaN; // Return NaN for modulus by zero
            }

            return numerator % denominator;
        }
    }
