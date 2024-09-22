package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.impl.Edge;

import java.util.List;

public interface SheetUpdateActions {
    void addCell(Cell newCell);

    void onCellUpdated(String originalValue, Coordinate coordinate);

    void addEdge(Edge edge);

    void updateCells(Coordinate coordinate);

    void updateVersion();

    void setCounter(int counter);

    Sheet sortSheet(Coordinate start, Coordinate end, List<String> selectedColumns);

    Sheet applyFilter(Coordinate startCell, Coordinate endCell, char selectedColumn, List<String> selectedValues);
}