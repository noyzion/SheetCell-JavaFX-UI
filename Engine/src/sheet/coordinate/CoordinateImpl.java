package sheet.coordinate;


import java.io.Serializable;

public class CoordinateImpl implements Coordinate, Serializable {

    private final int row;
    private final int column;
    private String stringCord;

    public CoordinateImpl(int row, int column, String stringCord) {
        this.row = row;
        this.column = column;
        this.stringCord = stringCord;
    }

    public CoordinateImpl(int row, int column) {
        String col = CoordinateFactory.convertIndexToColumnLetter(column);
        String rowC = Integer.toString(row+1);
        String stringCord = col+rowC;
        this.row = row;
        this.column = column;
        this.stringCord = stringCord;
    }

    @Override
    public String getStringCord() {
        return stringCord;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
return stringCord;    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate c) {
            return c.getRow() == this.getRow() && c.getColumn() == this.getColumn();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }

}


