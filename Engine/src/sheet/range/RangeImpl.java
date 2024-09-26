package sheet.range;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import sheet.api.Sheet;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;
import sheet.coordinate.CoordinateParser;

import java.util.ArrayList;
import java.util.List;

public class RangeImpl implements Range {

    private String name;
    private String range;
    private Coordinate start;
    private Coordinate end;
    private List<Coordinate> cells;
    private boolean isUsedInFunction;

    public RangeImpl(int rowSize, int colSize, String name, String range) {
        this.name = name;
        this.range = range;
        String[] parts = range.split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        start = new CoordinateImpl(RangeFactory.parseCell(parts[0].trim()).getRow() - 1, RangeFactory.parseCell(parts[0].trim()).getColumn());
        end = new CoordinateImpl(RangeFactory.parseCell(parts[1].trim()).getRow() - 1, RangeFactory.parseCell(parts[1].trim()).getColumn());
        cells = new ArrayList<>();
        for (Coordinate cord : RangeFactory.parseRange(rowSize, colSize, start, end)) {
            System.out.println(cord.getStringCord());
            this.cells.add(new CoordinateImpl(cord.getRow(), cord.getColumn()));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStart() {
        return start.toString();
    }

    @Override
    public String getEnd() {
        return end.toString();
    }

    @Override
    public String getRange() {
        return range;
    }

    @Override
    public List<Coordinate> getCells() {
        return cells;
    }

    @Override
    public boolean overlaps(Range other) {
        for (Coordinate cell : cells) {
            if (other.getCells().contains(cell)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getIsUsedInFunction() {
        return this.isUsedInFunction;
    }

    @Override
    public void setIsUsedInFunction(boolean isUsedInFunction) {
        this.isUsedInFunction = isUsedInFunction;
    }

}