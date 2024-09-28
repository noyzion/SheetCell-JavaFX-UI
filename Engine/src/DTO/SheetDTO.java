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
    private final double[][] columnWidthUnits;
    private final double[][] rowsHeightUnits;
    private final Map<CoordinateDTO, CellDTO> cells;
    private final List<Edge> edges;
    private final int counterChangedCells;
    private final Map<String, Range> ranges;

    public SheetDTO(String sheetName, int version, int rowSize, int columnSize,
                    double[][] columnWidthUnits, double[][] rowsHeightUnits,
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

    public double[][] getColumnWidthUnits() {
        return columnWidthUnits;
    }
    public double[][] getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    public double getCellColumnWidthUnits(String id) {
        CoordinateDTO cord = CoordinateParser.parseDTO(id);
        return columnWidthUnits[cord.getColumn()][cord.getRow()];
    }

    public double getCellRowsHeightUnits(String id) {
        CoordinateDTO cord = CoordinateParser.parseDTO(id);
        return rowsHeightUnits[cord.getColumn()][cord.getRow()];}

    public Map<String, Range> getRanges()
    {
        return ranges;
    }


    public CellDTO getCell(String coordinate){
        Coordinate cord = CoordinateParser.parse(coordinate);
        return cells.get(new CoordinateDTO(cord.getRow(), cord.getColumn(), cord.getStringCord()));
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