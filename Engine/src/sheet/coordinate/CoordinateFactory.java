package sheet.coordinate;

import sheet.api.Sheet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory implements Serializable {

    private static final Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static String getKey(Coordinate coordinate) {
        return coordinate.getRow() + ":" + coordinate.getColumn();
    }

    public static Coordinate createCoordinate(Sheet sheet, int row, int column, String cord) throws IndexOutOfBoundsException {
        if (row < 0 || row >= sheet.getRowSize() || column < 0 || column >= sheet.getColSize()) {
            throw new IndexOutOfBoundsException("Please enter a valid row/column number, between 1 to " +
                    (sheet.getRowSize()) + " for rows and between A to " +
                    convertIndexToColumnLetter(sheet.getColSize()) + " for columns.");
        }

        String key = row + ":" + column;

        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column, cord);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

//    public static void isValidCoordinateFormat(String input) throws IllegalArgumentException {
//        if (!input.matches("[A-Z]+\\d+")) {
//            throw new IllegalArgumentException("Invalid format. Coordinate should be in the format (e.g., A5).");
//        }
//    }


    public static String convertIndexToColumnLetter(int columnIndex) throws IllegalArgumentException {
        StringBuilder columnLetter = new StringBuilder();
        while (columnIndex >= 0) {
            columnLetter.insert(0, (char) ('A' + (columnIndex % 26)));
            columnIndex = (columnIndex / 26) - 1;
        }
        return columnLetter.toString();
    }

//    public static boolean isCoordinateWithinBounds(int rowSize, int colSize, String coordinate) throws ParseException, IllegalArgumentException {
//        Coordinate cord = CoordinateParser.parse(coordinate);
//        StringBuilder errorMessages = new StringBuilder();
//        boolean both = false;
//        if (cord.getRow() < 0 || cord.getRow() >= rowSize) {
//            errorMessages.append("row index out of bounds. Valid row indices are from 1 to ").append(rowSize).append(". ");
//            both = true;
//        }
//        if (cord.getColumn() < 0 || cord.getColumn() >= colSize) {
//            if (both)
//                errorMessages.append(" and ");
//            errorMessages.append("column index out of bounds. Valid column indices are from 1 to ").append(convertIndexToColumnLetter(colSize)).append(". ");
//        }
//        if (!errorMessages.isEmpty()) {
//            throw new IllegalArgumentException(errorMessages.toString().trim());
//        }
//
//        return true;
//    }

}