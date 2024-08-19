package shticell.engine.sheet.coordinate;


public class CoordinateImpl implements Coordinate {

    private final int row;
    private final int column;

    public CoordinateImpl(int row, int column) {
        this.row = row;
        this.column = column;
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
        return String.format(CoordinateFactory.getKey(this));
    }

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


