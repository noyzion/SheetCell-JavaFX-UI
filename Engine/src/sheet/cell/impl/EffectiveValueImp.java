package sheet.cell.impl;

import expression.api.Expression;
import expression.api.ExpressionFactory;
import expression.impl.BooleanExpression;
import expression.impl.ExpressionFactoryImpl;
import expression.impl.NumberExpression;
import expression.impl.Operations.Ref;
import expression.impl.StringExpression;
import sheet.api.Sheet;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import sheet.impl.Edge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EffectiveValueImp implements EffectiveValue, Serializable {
    private CellType cellType;
    private Object value;
    private final Coordinate coordinate;
    private final ExpressionFactory expressionFactory = new ExpressionFactoryImpl();

    public EffectiveValueImp(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }


    @Override
    public EffectiveValue copy() {
        EffectiveValueImp copy = new EffectiveValueImp(this.coordinate);
        copy.cellType = this.cellType;
        copy.value = this.value;
        return copy;
    }

    @Override
    public void calculateValue(Sheet sheet, String originalValue) {
        if (originalValue == null) {
         this.value = null;
        } else if (originalValue.startsWith("{") && originalValue.endsWith("}")) {
            Expression expr = stringToExpression(sheet, originalValue);
            if (this.value == null)
                this.value = expr.evaluate();
        } else {
            numOrStringOrBoolean(originalValue);
        }
    }

    private String[] stringTrimer(String input) {
        if (input.startsWith("{") && input.endsWith("}")) {
            input = input.substring(1, input.length() - 1).trim();
        }

        List<String> result = new ArrayList<>();
        StringBuilder currentElement = new StringBuilder();
        boolean insideBraces = false;
        int openBrackets = 0;

        for (char c : input.toCharArray()) {
            if (c == '{') {
                insideBraces = true;
                openBrackets++;
            } else if (c == '}') {
                openBrackets--;
                if (openBrackets == 0) {
                    insideBraces = false;
                }
            }

            if (c == ',' && !insideBraces) {
                result.add(currentElement.toString().trim());
                currentElement.setLength(0);
            } else {
                currentElement.append(c);
            }
        }
        result.add(currentElement.toString().trim());

        String operator = result.remove(0);  // Use remove(0) instead of removeFirst()
        String[] operatorAndArgs = new String[result.size() + 1];
        operatorAndArgs[0] = operator;
        for (int i = 0; i < result.size(); i++) {
            operatorAndArgs[i + 1] = result.get(i);
        }

        return operatorAndArgs;
    }

    private Expression createExpression(Sheet sheet, String[] expression) throws IllegalArgumentException {
        String operator = expression[0];
        List<Expression> args = new ArrayList<>();
        for (int i = 1; i < expression.length; i++) {
            args.add(stringToExpression(sheet, expression[i]));
        }

        Expression res;
            res = getExpression(sheet, operator, args);
            this.value = res.evaluate();

            if (res instanceof Ref refExpr) {
                Coordinate ref = refExpr.getRefCoordinate();
                sheet.getCell(coordinate).addCellToRelatedCells(ref);
                sheet.getCell(ref).addCellToAffectedCells(coordinate);
                Edge edge = new Edge(ref, coordinate);
                if (!sheet.getEdges().contains(edge)) {
                    sheet.addEdge(edge);
                }
                cellType = CellType.EXPRESSION;
            }

            if (res == null) {
                throw new IllegalStateException("Expression could not be created or evaluated.");
            }

            return res;

    }

    private Expression getExpression(Sheet sheet, String operator, List<Expression> args) {
        this.cellType = expressionFactory.validateArguments(operator, args,coordinate);
        return expressionFactory.createExpression(sheet, operator, args);

    }

    private Expression stringToExpression(Sheet sheet, String input) {
        if (input.startsWith("{") && input.endsWith("}")) {
            return createExpression(sheet, stringTrimer(input));
        } else {
            try {
                return new NumberExpression(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                if ("true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input)) {
                    return new BooleanExpression(Boolean.parseBoolean(input));
                } else {
                    return new StringExpression(input);
                }}
        }
    }

    private void numOrStringOrBoolean(String value) {
        try {
            this.value = Double.parseDouble(value);
            this.cellType = CellType.NUMERIC;
        } catch (NumberFormatException e) {
            if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                this.cellType = CellType.BOOLEAN;
                this.value = value.toUpperCase();
            }
            else{
                this.cellType = CellType.STRING;
                this.value = value;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(obj == null & this.value == null)
            return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EffectiveValueImp other = (EffectiveValueImp) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }

    @Override
    public String toString()
    {
        if(value.getClass() == Boolean.class)
            return value.toString().toUpperCase();
        return value.toString();
    }

}
