package expression.impl.Operations.numeric;

import expression.api.Expression;
import expression.impl.TrinaryExpression;
import sheet.cell.api.CellType;

public class Sub extends TrinaryExpression {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }


    @Override
    public CellType getCellType() {
        return CellType.STRING;
    }

    @Override
    public String getOperationName() {
        return "SUB";
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
            throw new IllegalArgumentException("Second argument cannot be empty.");
        }
        if (!(evaluate1 instanceof String str1) || !(evaluate2 instanceof Double num2) || !(evaluate3 instanceof Double num3) ) {
            return "!UNDEFINED!";
        }

        if (isWholeNumber(num2)) {
            throw new IllegalArgumentException("Second argument must be a whole number. Received: " + num2);
        }

        if (isWholeNumber(num3)) {
            throw new IllegalArgumentException("Third argument must be a whole number. Received: " + num3);
        }

        int startIndex = num2.intValue();
        int endIndex = num3.intValue();

        if (startIndex < 0 || startIndex > str1.length() || endIndex < startIndex || endIndex > str1.length()) {
            throw new IllegalArgumentException("Invalid indices: startIndex = " + startIndex + ", endIndex = " + endIndex);
        }

        try {
            return str1.substring(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Error extracting substring with indices: startIndex = " + startIndex + ", endIndex = " + endIndex, e);
        }
    }

    private boolean isWholeNumber(Double number) {
        return number % 1 != 0;
    }
}