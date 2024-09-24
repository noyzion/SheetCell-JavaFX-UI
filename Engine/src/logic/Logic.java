package logic;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;
import sheet.coordinate.CoordinateParser;
import sheet.impl.Edge;
import sheet.impl.SheetImpl;
import sheet.range.Range;
import sheet.range.RangeImpl;

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

    public void setCellValue(String cellId, String value) throws Exception {
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

    private Sheet createNewSheetFrom(Sheet oldSheet) throws Exception {
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

        for (Map.Entry<String, Range> entry : oldSheet.getRanges().entrySet()) {
            Range newRange = new RangeImpl(oldSheet.getRowSize(),oldSheet.getColSize(),entry.getValue().getName(), entry.getValue().getRange());
            newSheet.addRange(newRange.getRange(), newRange.getName());
        }
        return newSheet;
    }

    public SheetDTO sortSheet(CoordinateDTO start, CoordinateDTO end, List<String> selectedColumns)
    {
        Coordinate startCord = new CoordinateImpl(start.getRow(), start.getColumn());
        Coordinate endCord = new CoordinateImpl(end.getRow(), end.getColumn());
        Sheet sortedSheet = versionManager.getVersionedSheets().getLast().sortSheet(startCord, endCord, selectedColumns);
        return new ConverterUtil().toSheetDTO(sortedSheet);
    }
    public SheetDTO filterSheet(CoordinateDTO start, CoordinateDTO end, char col, List<String> values)
    {
        Coordinate startCord = new CoordinateImpl(start.getRow(), start.getColumn());
        Coordinate endCord = new CoordinateImpl(end.getRow(), end.getColumn());
        Sheet filteredSheet = versionManager.getVersionedSheets().getLast().applyFilter(startCord, endCord, col, values);
        return new ConverterUtil().toSheetDTO(filteredSheet);
    }

    public void addRange( String name, String range) throws Exception {
        versionManager.getVersionedSheets().getLast().addRange(range,name);
    }
    public void deleteRange(String name) throws Exception {
        versionManager.getVersionedSheets().getLast().deleteRange(name);
    }
}
