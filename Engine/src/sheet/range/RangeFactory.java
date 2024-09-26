package sheet.range;
import DTO.CoordinateDTO;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangeFactory {
    private static Map<String, Range> ranges = ranges = new HashMap<>();

    public static Range createRange(int rowSize, int colSize, String name, String range) {
        if (ranges.containsKey(name)) {
            return ranges.get(name);
        }

        Range newRange = new RangeImpl(rowSize, colSize, name, range);
        ranges.put(name, newRange);
        return newRange;
    }

    public static boolean removeRange(String name) {
        if (!ranges.containsKey(name)) {
            throw new IllegalArgumentException("range does not exist");
        }

        if (ranges.get(name).getIsUsedInFunction()) {
            throw new IllegalArgumentException("range is used in function");
         }

        ranges.remove(name);
        return true;
    }

    public static Range getRange(String name) {
        return ranges.get(name);
    }

    public static Boolean isRangeExist(String key) {
        return ranges.containsKey(key);
    }



    public static List<Coordinate> parseRange(int rowSize, int colSize, Coordinate start, Coordinate end) {
        List<Coordinate> cellList = new ArrayList<>();

        if (start.getRow() < 0 || start.getRow() >= rowSize ||
                start.getColumn() < 0 || start.getColumn() >= colSize ||
                end.getRow() < 0 || end.getRow() >= rowSize ||
                end.getColumn() < 0 || end.getColumn() >= colSize) {
            throw new IllegalArgumentException("Start or end coordinates are out of bounds.");
        }


        for (int column = start.getColumn(); column <= end.getColumn(); column++) {
            for (int row = (column == start.getColumn() ? start.getRow() : 0); row <= (column == end.getColumn() ? end.getRow() : rowSize -1); row++) {
                cellList.add(new CoordinateImpl(row, column));
            }
        }
        return cellList;
    }

    public static List<CoordinateDTO> parseRangeDTO(int rowSize, int colSize, CoordinateDTO start, CoordinateDTO end) {
        List<CoordinateDTO> cellList = new ArrayList<>();

        for (int column = start.getColumn(); column <= end.getColumn(); column++) {
            for (int row = (column == start.getColumn() ? start.getRow() : 0); row <= (column == end.getColumn() ? end.getRow() : rowSize -1 ); row++) {
                cellList.add(new CoordinateDTO(row, column));
            }
        }
        return cellList;
    }


    public static CoordinateDTO parseCell(String cell) {
        String columnPart = cell.replaceAll("[^A-Z]", "");
        String rowPart = cell.replaceAll("[^1-9]", "");

        int columnIndex =columnPart.charAt(0) - 'A';
        int rowIndex = Integer.parseInt(rowPart);

        return new CoordinateDTO(rowIndex, columnIndex);
    }
}
