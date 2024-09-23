package expression.impl;

import expression.api.Expression;
import expression.api.ExpressionFactory;
import expression.impl.Operations.*;
import expression.impl.Operations.logic.*;
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
            case "IF" -> new If(args.get(0), args.get(1), args.get(2));
            case "NOT" -> new Not(args.get(0));
            case "OR" -> new Or(args.get(0), args.get(1));
            case "AND" -> new And(args.get(0), args.get(1));
            case "BIGGER" -> new Bigger(args.get(0), args.get(1));
            case "LESS" -> new Less(args.get(0), args.get(1));
            case "EQUAL" -> new Equal(args.get(0), args.get(1));
            case "PERCENT" -> new Percent(args.get(0), args.get(1));
            case "AVERAGE" -> new Average(args.get(0), sheet);
            case "SUM" -> new Sum(args.get(0), sheet);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    @Override
    public CellType validateArguments(String operator, List<Expression> args, Coordinate coordinate) {
        if (args == null) {
            throw new IllegalArgumentException("Arguments list cannot be null.");
        }

        return switch (operator) {
            case "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "POW", "PERCENT", "BIGGER", "LESS" ->
                    validateBinaryOperation(operator, args, coordinate);
            case "CONCAT" -> validateConcatOperation(operator, args, coordinate);
            case "ABS" -> validateAbsOperation(operator, args, coordinate);
            case "SUB" -> validateSubOperation(operator, args, coordinate);
            case "REF" -> validateRefOperation(operator, args, coordinate);
            case "NOT" -> validateUnaryBooleanOperation(operator, args, coordinate);
            case "OR", "AND" -> validateBinaryBooleanOperation(operator, args, coordinate);
            case "IF" -> validateIfOperation(operator, args, coordinate);
            case "EQUAL" -> CellType.BOOLEAN;
            case "SUM" -> validateRangeOperation(operator, args, coordinate);
            case "AVERAGE" -> validateRangeOperation(operator, args, coordinate);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private CellType validateRangeOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.STRING, operator, "Range", coordinate);
        return CellType.NUMERIC;
    }

    private CellType validateBinaryOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 2) {
            throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.NUMERIC, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.NUMERIC, operator, "Second", coordinate);
        return CellType.NUMERIC;
    }

    private CellType validateBinaryBooleanOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 2) {
            throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.BOOLEAN, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.BOOLEAN, operator, "Second", coordinate);
        return CellType.BOOLEAN;
    }

    private CellType validateUnaryBooleanOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.BOOLEAN, operator, "Argument", coordinate);
        return CellType.BOOLEAN;
    }

    private CellType validateConcatOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 2) {
            throw new IllegalArgumentException(operator + " requires exactly 2 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.STRING, operator, "First", coordinate);
        validateArgumentType(args.get(1), CellType.STRING, operator, "Second", coordinate);
        return CellType.STRING;
    }

    private CellType validateAbsOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.NUMERIC, operator, "Argument", coordinate);
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

    private CellType validateIfOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 3) {
            throw new IllegalArgumentException(operator + " requires exactly 3 arguments, but got " + args.size());
        }
        validateArgumentType(args.get(0), CellType.BOOLEAN, operator, "Condition", coordinate);
        CellType thenType = args.get(1).getCellType();
        CellType elseType = args.get(2).getCellType();

        if (!thenType.equals(elseType)) {
            throw new IllegalArgumentException("IF operation requires `then` and `else` arguments to be of the same type. but then is " + args.get(1).getCellType() +
                    "and else is " + args.get(2).getCellType());
        }
        return thenType;
    }

    private CellType validateRefOperation(String operator, List<Expression> args, Coordinate coordinate) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(operator + " requires exactly 1 argument, but got " + args.size());
        }
        return CellType.EXPRESSION;
    }

    private void validateArgumentType(Expression arg, CellType expectedType, String operator, String position, Coordinate coordinate) {
        if (arg.getCellType() != expectedType && arg.getCellType() != CellType.EXPRESSION) {
            throw new IllegalArgumentException("Cell at coordinate " + coordinate.getStringCord() +
                    " " + position + " argument must be of type " + expectedType +
                    ". Received: " + arg.getCellType().getType().getSimpleName());
        }
    }
}

