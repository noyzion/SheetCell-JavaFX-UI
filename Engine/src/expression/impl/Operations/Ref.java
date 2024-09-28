package expression.impl.Operations;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.api.CellType;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateParser;

import java.util.Objects;


public class Ref extends UnaryExpression {

    Sheet sheet;
    Coordinate refCoordinate;

    public Ref(Expression expression1, Sheet sheet) {
        super(expression1);
        this.sheet = sheet;
    }

    @Override
    public CellType getCellType() {
        return CellType.EXPRESSION;
    }

    @Override
    protected Object evaluate(Object object) throws NumberFormatException {
        if (!(object instanceof String coordinateStr)) {
            throw new IllegalArgumentException("The argument must be a coordinate. Provided: " + object.getClass().getSimpleName());
        }

        try {
            this.refCoordinate = CoordinateParser.parse(coordinateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing coordinate: " + coordinateStr, e);
        }


        Cell cell = sheet.getCell(refCoordinate);

        if (cell == null) {
            cell = new CellImpl(refCoordinate, sheet.getCellRowHeightUnits(refCoordinate.getStringCord()), sheet.getCellColWidthUnits(refCoordinate.getStringCord()));
            sheet.addCell(cell);
            sheet.onCellUpdated(null, refCoordinate);
        } else if (Objects.equals(cell.getOriginalValue(), " ")) {
            return Double.NaN;
        }


        return cell.getEffectiveValue().getValue();
    }

    public Coordinate getRefCoordinate() {
        return refCoordinate;
    }

    @Override
    public String getOperationName() {
        return "REF";
    }
}