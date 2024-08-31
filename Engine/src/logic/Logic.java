package logic;

import DTO.SheetDTO;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateParser;
import sheet.coordinate.ParseException;
import sheet.impl.Edge;
import sheet.impl.SheetImpl;

import java.io.Serializable;
import java.util.*;

public class Logic implements Serializable {

    private final VersionManager versionManager = new VersionManager();

    public void addSheet(Sheet sheet) {
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet cannot be null.");
        }
        versionManager.addSheet(sheet);
    }

    public SheetDTO getLatestSheet() {
        return versionManager.getLatestSheet();
    }

    public SheetDTO getSheetByVersion(int version) {
        return versionManager.getSheetByVersion(version);
    }

    public void setCellValue(String cellId, String value) throws ParseException {
        if (cellId == null) {
            throw new IllegalArgumentException("Cell ID cannot be null.");
        }
        Sheet currentSheet = versionManager.getVersionedSheets().getLast();
        Sheet newSheet = createNewSheetFrom(currentSheet);
        newSheet.updateVersion();

        Coordinate coordinate = CoordinateParser.parse(cellId);
        newSheet.onCellUpdated(value, coordinate);
        newSheet.getCell(coordinate).setVersion(newSheet.getVersion());

        for (Coordinate cord : newSheet.getCell(coordinate).getAffectedCells()) {
            newSheet.getCell(cord).setVersion(newSheet.getVersion());
        }

        versionManager.addSheet(newSheet);
    }

    private Sheet createNewSheetFrom(Sheet oldSheet) {
        SheetImpl newSheet = new SheetImpl(
                oldSheet.getSheetName(),
                oldSheet.getRowSize(),
                oldSheet.getColSize(),
                oldSheet.getColumnWidthUnits(),
                oldSheet.getRowsHeightUnits(),
                oldSheet.getVersion()
        );

        for (Map.Entry<Coordinate, Cell> entry : oldSheet.getCells().entrySet()) {
            Cell oldCell = entry.getValue();
            Cell newCell = new CellImpl(oldCell);
            newSheet.addCell(newCell);
        }

        for (Edge edge : oldSheet.getEdges()) {
            Edge newEdge = new Edge(edge.getFrom(), edge.getTo());
            newSheet.addEdge(newEdge);
        }
        return newSheet;
    }

}
