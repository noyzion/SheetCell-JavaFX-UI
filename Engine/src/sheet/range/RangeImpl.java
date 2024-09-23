package sheet.range;
import DTO.CoordinateDTO;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.util.ArrayList;
import java.util.List;

public class RangeImpl implements Range {

    private String name;
    private String range;
    private Coordinate start;
    private Coordinate end;
    private List<Coordinate> cells;
    private boolean isUsedInFunction;

    public RangeImpl(String name, String range) {
        this.name = name;
        this.range = range;
        this.cells = parseRange(range);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStart()
    {
        return start.toString();
    }

    @Override
    public String getEnd()
    {
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
    public boolean getIsUsedInFunction(){
        return this.isUsedInFunction;
    }

    @Override
    public void setIsUsedInFunction(boolean isUsedInFunction){
        this.isUsedInFunction = isUsedInFunction;
    }

    private List<Coordinate> parseRange(String range) {
        List<Coordinate> cellList = new ArrayList<>();

        String[] parts = range.split("\\.\\.");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

         start = parseCell(parts[0].trim());
         end = parseCell(parts[1].trim());

        for (int row = start.getRow(); row <= end.getRow(); row++) {
            for (int column = start.getColumn(); column <= end.getColumn(); column++) {
                cellList.add(new CoordinateImpl(row, column));
            }
        }

        return cellList;
    }

    private Coordinate parseCell(String cell) {
        String columnPart = cell.replaceAll("[^A-Z]", "");
        String rowPart = cell.replaceAll("[^1-9]", "");

        int columnIndex =columnPart.charAt(0) - 'A';
        int rowIndex = Integer.parseInt(rowPart);

        return new CoordinateImpl(rowIndex, columnIndex);
    }

}
