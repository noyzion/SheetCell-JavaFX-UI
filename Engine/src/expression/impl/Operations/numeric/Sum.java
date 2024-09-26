package expression.impl.Operations.numeric;

import DTO.CoordinateDTO;
import expression.api.Expression;
import expression.impl.UnaryExpression;
import sheet.api.Sheet;
import sheet.cell.api.CellType;
import sheet.coordinate.Coordinate;
import sheet.range.Range;

import java.util.List;

public class Sum extends UnaryExpression {

    private Sheet sheet;

    public Sum(Expression ex1, Sheet sheet) {
        super(ex1);
        this.sheet = sheet;
    }

    @Override
    protected Object evaluate(Object evaluate) throws NumberFormatException {
        if (evaluate == null) {
            throw new IllegalArgumentException("Argument cannot be empty.");
        }

        if (!(evaluate instanceof String)) {
            throw new IllegalArgumentException("Argument must be a string representing a range.");
        }

        Range range = sheet.getRanges().get((String) evaluate);
        if (range == null) {
            throw new IllegalArgumentException("Range does not exist.");
        }

        List<Coordinate> rangeCells = range.getCells();
        range.setIsUsedInFunction(true);
        double sum = 0.0;

        for (Coordinate c : rangeCells) {
            Object cellValue = sheet.getCell(c).getEffectiveValue().getValue();
            if (cellValue instanceof Number) {
                sum += ((Number) cellValue).doubleValue();
            }
        }

        return sum;
    }

    @Override
    public String getOperationName() {
        return "SUM";
    }

    @Override
    public CellType getCellType() {
        return CellType.NUMERIC;
    }
}
