package DTO;

import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;

public class CoordinateDTO {
    private final int row;
    private final int column;
    private final String stringCord;

    public CoordinateDTO(int row, int column, String stringCord) {
        this.row = row;
        this.column = column;
        this.stringCord = stringCord;
    }

    public CoordinateDTO(int row, int column) {
        String col = CoordinateFactory.convertIndexToColumnLetter(column);
        String rowC = Integer.toString(row);
        String stringCord = col+rowC;
        this.row = row;
        this.column = column;
        this.stringCord = stringCord;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || this.getClass() != object.getClass())
            return false;
        CoordinateDTO current = (CoordinateDTO) object;
        return row == current.getRow() && column == current.getColumn();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return stringCord;
    }
}
