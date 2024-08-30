package shticell.engine.sheet.cell.impl;

import shticell.engine.expression.api.Expression;
import shticell.engine.expression.impl.NumberExpression;
import shticell.engine.expression.impl.Operations.*;
import shticell.engine.expression.impl.StringExpression;
import shticell.engine.sheet.api.Sheet;
import shticell.engine.sheet.cell.api.Cell;
import shticell.engine.sheet.cell.api.CellType;
import shticell.engine.sheet.cell.api.EffectiveValue;
import shticell.engine.sheet.coordinate.Coordinate;
import shticell.engine.sheet.impl.Edge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EffectiveValueImp implements EffectiveValue, Serializable {
    private CellType cellType;
    private Object value;
    private final Coordinate coordinate;

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
            numOrString(originalValue);
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

        Expression res = null;
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
       this.cellType = validateArguments(operator, args);
        return createExpression(sheet, operator, args);
    }

    private CellType validateArguments(String operator, List<Expression> args) {
        switch (operator) {
            case "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "POW" -> {
                if (args.size() != 2) {
                    throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
                }
                if (args.get(0).getCellType() == CellType.STRING) {
                    Expression e1 = args.getFirst();
                    throw new IllegalArgumentException("First argument must be of type Double. Received: " + (e1 != null ?  e1.getCellType().getType().getSimpleName() : "null"));
                }
                if (args.get(1).getCellType() == CellType.STRING) {
                    Expression e1 = args.get(1);
                    throw new IllegalArgumentException("Second argument must be of type Double. Received: " + (e1 != null ?  e1.getCellType().getType().getSimpleName() : "null"));
                }

                return CellType.NUMERIC;
            }
            case  "CONCAT" -> {
                if (args.size() != 2) {
                    throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
                }
                if (args.get(0).getCellType() == CellType.NUMERIC) {
                    Expression e1 = args.getFirst();
                    throw new IllegalArgumentException("First argument must be of type String. Received: " + (e1 != null ? e1.getCellType().getType().getSimpleName() : "null"));
                }
                if (args.get(1).getCellType() == CellType.NUMERIC) {
                    Expression e1 = args.get(1);
                    throw new IllegalArgumentException("Second argument must be of type String. Received: " + (e1 != null ? e1.getCellType().getType().getSimpleName() : "null"));
                }
                return CellType.STRING;
            }
            case "ABS" -> {
                if (args.size() != 1) {
                    throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
                }
                if (args.get(0).getCellType() == CellType.STRING) {
                    Expression e1 = args.getFirst();
                    throw new IllegalArgumentException("Invalid argument type: Expected Double, but received " + e1.getCellType().getType().getSimpleName() + ".");
                }
                return CellType.NUMERIC;
            }
            case "SUB" -> {
                if (args.size() != 3) {
                    throw new IllegalArgumentException(operator + " requires exactly 3 arguments, but got " + args.size());
                }
                if (args.getFirst().getCellType() == CellType.NUMERIC) {
                    Expression e1 = args.getFirst();
                    throw new IllegalArgumentException("First argument must be a String. Received: "
                            + (e1 != null ? e1.getCellType().getType().getSimpleName() : "null"));
                }

                if (args.get(1).getCellType() == CellType.STRING) {
                    Expression e1 = args.get(1);
                    throw new IllegalArgumentException("Second argument must be numeric. Received: "
                            + (e1 != null ? e1.getCellType().getType().getSimpleName() : "null"));
                }

                if (args.get(2).getCellType() == CellType.STRING) {
                    Expression e1 = args.get(1);
                    throw new IllegalArgumentException("Third argument must be numeric. Received: "
                            + (e1 != null ?  e1.getCellType().getType().getSimpleName() : "null"));
                }
                return CellType.STRING;
            }
            case "REF" -> {
                if (args.size() != 1) {
                    throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
                }
                return CellType.EXPRESSION;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private Expression createExpression(Sheet sheet, String operator, List<Expression> args) {
        return switch (operator) {
            case "PLUS" -> new Plus(args.get(0), args.get(1));
            case "MINUS" -> new Minus(args.get(0), args.get(1));
            case "TIMES" -> new Times(args.get(0), args.get(1));
            case "DIVIDE" -> new Divide(args.get(0), args.get(1));
            case "MOD" -> new Mod(args.get(0), args.get(1));
            case "POW" -> new Pow(args.get(0), args.get(1));
            case "ABS" -> new Abs(args.get(0));
            case "CONCAT" -> new Concat(args.get(0), args.get(1));
            case "SUB" -> new Sub(args.get(0), args.get(1), args.get(2));
            case "REF" -> new Ref(args.get(0), sheet);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private Expression stringToExpression(Sheet sheet, String input) {
        if (input.startsWith("{") && input.endsWith("}")) {
            return createExpression(sheet, stringTrimer(input));
        } else {
            try {
                return new NumberExpression(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                return new StringExpression(input);
            }
        }
    }

    private void numOrString(String value) {
        try {
            this.value = Double.parseDouble(value);
            this.cellType = CellType.NUMERIC;
        } catch (NumberFormatException e) {
            this.value = value;
            this.cellType = CellType.STRING;
        }
    }
}
