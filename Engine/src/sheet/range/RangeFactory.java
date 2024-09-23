package sheet.range;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangeFactory {
    private static Map<String, Range> ranges = ranges = new HashMap<>();

    public static boolean addRange(int rowSize, int colSize, String name, String range) {
        if (ranges.containsKey(name)) {
           throw new IllegalArgumentException("range already exists");
        }

       Range newRange = new RangeImpl(rowSize, colSize,name, range);
        ranges.put(name, newRange);
        return true;
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



    public static List<CoordinateDTO> parseRange(int rowSize, int colSize, CoordinateDTO start, CoordinateDTO end) {
        List<CoordinateDTO> cellList = new ArrayList<>();

        for (int column = start.getColumn(); column <= end.getColumn(); column++) {
            if (column == start.getColumn()) {
                for (int row = start.getRow(); row <= rowSize; row++) {
                    cellList.add(new CoordinateDTO(row, column));
                }
            }
            else if (column < end.getColumn()) {
                for (int row = 0; row <= rowSize; row++) {
                    cellList.add(new CoordinateDTO(row, column));
                }
            }
            else if (column == end.getColumn()) {
                for (int row = 0; row <= end.getRow(); row++) {
                    cellList.add(new CoordinateDTO(row, column));
                }
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
