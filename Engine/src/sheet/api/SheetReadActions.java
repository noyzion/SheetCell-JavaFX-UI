package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.impl.Edge;
import sheet.range.Range;

import java.util.List;
import java.util.Map;

public interface SheetReadActions {

    int getVersion();

    String getSheetName();

    Cell getCell(Coordinate coordinate);

    int getRowSize();

    int getColSize();
    double[][] getRowsHeightUnits();
    double[][] getColumnWidthUnits();
    double getCellColWidthUnits(String cellid);
    double getCellRowHeightUnits(String cellid);

    Map<Coordinate, Cell> getCells();

    List<Edge> getEdges();

    int getCounterChangedCells();

    Map<String, Range> getRanges();
}