package expression.impl;

import expression.api.Expression;
import expression.api.ExpressionFactory;
import expression.impl.Operations.*;
import expression.impl.Operations.numeric.*;
import expression.impl.Operations.string.Concat;
import sheet.api.Sheet;
import sheet.cell.api.CellType;
import sheet.coordinate.Coordinate;

import java.io.Serializable;
import java.util.List;

public class ExpressionFactoryImpl implements ExpressionFactory, Serializable {

    @Override
    public Expression createExpression(Sheet sheet, String operator, List<Expression> args) {
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

    @Override
    public CellType validateArguments(String operator, List<Expression> args, Coordinate coordinate) {
        if (args == null) {
            throw new IllegalArgumentException("Arguments list cannot be null.");
        }

        switch (operator) {
            case "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "POW" -> {
                return validateBinaryOperation(operator, args, coordinate);
            }
            case "CONCAT" -> {
                return validateConcatOperation(operator, args, coordinate);
            }
            case "ABS" -> {
                return validateAbsOperation(operator, args, coordinate);
            }
            case "SUB" -> {
                return validateSubOperation(operator, args, coordinate);
            }
            case "REF" -> {
                return validateRefOperation(operator, args, coordinate);
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private CellType validateBinaryOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 2) {
            throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.STRING, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.STRING, operator, "Second", coordinate);
        return CellType.NUMERIC;
    }

    private CellType validateConcatOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 2) {
            throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.NUMERIC, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.NUMERIC, operator, "Second", coordinate);
        return CellType.STRING;
    }

    private CellType validateAbsOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.STRING, operator, "Argument", coordinate);
        return CellType.NUMERIC;
    }

    private CellType validateSubOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 3) {
            throw new IllegalArgumentException(operator + " requires exactly 3 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.NUMERIC, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.STRING, operator, "Second", coordinate);
        validateArgumentType(args.get(2), CellType.STRING, operator, "Third", coordinate);
        return CellType.STRING;
    }

    private CellType validateRefOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        return CellType.EXPRESSION;
    }

    private void validateArgumentType(Expression arg, CellType unexpectedType, String operator, String position, Coordinate coordinate) {
        if (arg.getCellType() == unexpectedType) {
            String expectedType;
            if (unexpectedType == CellType.NUMERIC) {
                expectedType = "String";
            } else if (unexpectedType == CellType.STRING) {
                expectedType = "Double";

                throw new IllegalArgumentException("Cell at coordinate " + coordinate.getStringCord() +
                        " " + position + " argument must be of type " + expectedType +
                        ". Received: " + arg.getCellType().getType().getSimpleName());
            }
        }
    }
}
