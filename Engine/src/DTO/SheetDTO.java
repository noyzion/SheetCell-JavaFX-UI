package DTO;

import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateParser;
import sheet.impl.Edge;
import sheet.range.Range;

import java.util.List;
import java.util.Map;

public class SheetDTO {

    private final String sheetName;
    private final int version;
    private final int rowSize;
    private final int columnSize;
    private final int columnWidthUnits;
    private final int rowsHeightUnits;
    private final Map<CoordinateDTO, CellDTO> cells;
    private final List<Edge> edges;
    private final int counterChangedCells;
    private final Map<String, Range> ranges;

    public SheetDTO(String sheetName, int version, int rowSize, int columnSize,
                    int columnWidthUnits, int rowsHeightUnits,
                    Map<CoordinateDTO, CellDTO> cells, List<Edge> edges,int counterChangedCells,
                    Map<String, Range> ranges) {
        this.sheetName = sheetName;
        this.version = version;
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.columnWidthUnits = columnWidthUnits;
        this.rowsHeightUnits = rowsHeightUnits;
        this.cells = cells;
        this.edges = edges;
        this.counterChangedCells = counterChangedCells;
        this.ranges = ranges;
    }

    public Map<CoordinateDTO, CellDTO> getCells() { return cells; }

    public int getCounterChangedCells() {
        return counterChangedCells;
    }

    public int getVersion() {
        return version;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getColumnWidthUnits() { return columnWidthUnits; }

    public int getRowsHeightUnits() { return rowsHeightUnits; }

    public Map<String, Range> getRanges()
    {
        return ranges;
    }


    public CellDTO getCell(String coordinate){
        Coordinate cord = CoordinateParser.parse(coordinate);
        return cells.get(new CoordinateDTO(cord.getRow(), cord.getColumn(), cord.getStringCord()));
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder();
        outputString.append("Sheet name: ").append(sheetName).append("\n");
        outputString.append("Version: ").append(version).append("\n");
        outputString.append("   | ");
        for (int col = 0; col < columnSize; col++) {
            String colHeader = String.format("%-" + columnWidthUnits + "s", (char) ('A' + col));
            outputString.append(colHeader).append("| ");
        }
        outputString.append("\n");
        for (int row = 0; row < rowSize; row++) {
            String rowHeader = String.format("%02d | ", row + 1);
            outputString.append(rowHeader);
            for (int col = 0; col < columnSize; col++) {
                CoordinateDTO cellCoordinate = new CoordinateDTO(row, col);
                String cellValue = cells.containsKey(cellCoordinate)
                        ? (cells.get(cellCoordinate).getEffectiveValue().getValue() != null
                        ? String.valueOf(cells.get(cellCoordinate).getEffectiveValue().getValue())
                        : " ")
                        : " ";
                if (cellValue.length() > columnWidthUnits) {
                    cellValue = cellValue.substring(0, columnWidthUnits);
                }
                String formattedCellValue = centerText(cellValue, columnWidthUnits);
                outputString.append(formattedCellValue).append("| ");
            }
            for (int i = 1; i < rowsHeightUnits; i++) {
                outputString.append("\n");
                outputString.append("   | ");

                for (int col = 0; col < columnSize; col++) {
                    outputString.append(String.format("%-" + columnWidthUnits + "s", "")).append("| ");
                }
            }
            outputString.append("\n");
        }
        return outputString.toString();
    }


    private String centerText(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (width <= text.length()) {
            return text.substring(0, width);
        }

        int padding = (width - text.length()) / 2;
        String leftPadding = " ".repeat(padding);
        String rightPadding = " ".repeat(width - text.length() - padding);

        return leftPadding + text + rightPadding;
    }
}