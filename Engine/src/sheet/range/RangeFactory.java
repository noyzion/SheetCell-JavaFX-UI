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

    public static boolean addRange(String name, String range) {
        if (ranges.containsKey(name)) {
            System.out.println("Error: Range name already exists.");
            return false;
        }

       Range newRange = new RangeImpl(name, range);
        ranges.put(name, newRange);
        return true;
    }

    public static boolean removeRange(String name) {
        if (!ranges.containsKey(name)) {
            System.out.println("Error: Range not found.");
            return false;
        }

        if (ranges.get(name).getIsUsedInFunction()) {
             System.out.println("Error: Range is in use.");
             return false;
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

    public static List<CoordinateDTO> parseRange(Coordinate start, Coordinate end, String range) {
        List<CoordinateDTO> cellList = new ArrayList<>();

        String[] parts = range.split("\\.\\.");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

      CoordinateDTO startC = parseCell(parts[0].trim());
        CoordinateDTO endC = parseCell(parts[1].trim());

        for (int row = startC.getRow(); row <= endC.getRow(); row++) {
            for (int column = startC.getColumn(); column <= endC.getColumn(); column++) {
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
