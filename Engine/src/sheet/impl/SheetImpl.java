package sheet.impl;

import DTO.CoordinateDTO;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.CellImpl;
import sheet.cell.impl.EffectiveValueImp;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateImpl;
import sheet.range.Range;
import sheet.range.RangeFactory;
import sheet.range.RangeImpl;

import java.io.Serializable;
import java.util.*;

public class SheetImpl implements Sheet, Serializable {
    private final Map<Coordinate, Cell> cells;
    private List<Edge> edges = new ArrayList<>();
    private final String sheetName;
    private int version;
    private final int rowSize;
    private final int columnSize;
    private final int columnWidthUnits;
    private final int rowsHeightUnits;
    private int counterChangedCells = 0;
    private Map<String, Range> ranges;


    public SheetImpl(String sheetName, int rowSize, int columnSize, int columnWidthUnits, int rowsHeightUnits, int version) {
        this.sheetName = sheetName;
        this.cells = new HashMap<>();
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.columnWidthUnits = columnWidthUnits;
        this.rowsHeightUnits = rowsHeightUnits;
        this.version = version++;
    }

    @Override
    public int getCounterChangedCells() {
        return counterChangedCells;
    }

    @Override
    public void setCounter(int counter) {
        this.counterChangedCells = counter;
    }

    @Override
    public int getColumnWidthUnits() {
        return columnWidthUnits;
    }

    @Override
    public int getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    @Override
    public int getRowSize() {
        return rowSize;
    }

    @Override
    public int getColSize() {
        return columnSize;
    }

    @Override
    public void updateVersion() {
        this.version++;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    public void addCell(Cell newCell) {
        Coordinate cellCord = newCell.getCoordinate();
        Coordinate coordinate = CoordinateFactory.createCoordinate(this, cellCord.getRow(), cellCord.getColumn(), cellCord.getStringCord());
        cells.put(coordinate, newCell);
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        return cells.get(coordinate);
    }


    @Override
    public void onCellUpdated(String originalValue, Coordinate coordinate) {
        String previousOriginalValue = null;
        EffectiveValue previousEffectiveValue = null;
        Cell cell = cells.get(coordinate);
        if (cell != null) {
            previousOriginalValue = cell.getOriginalValue();
            previousEffectiveValue = cell.getEffectiveValue() != null ? cell.getEffectiveValue().copy() : null;
        } else {
            cell = new CellImpl(coordinate, rowsHeightUnits, columnWidthUnits);
            addCell(cell);
        }

        try {
            cell.setOriginalValue(originalValue);
            removeDependence(cell);
            if (cell.getEffectiveValue() == null) {
                cell.setEffectiveValue(new EffectiveValueImp(coordinate));
            }
            cell.getEffectiveValue().calculateValue(this, originalValue);
            if (!cell.getEffectiveValue().equals(previousEffectiveValue))
                counterChangedCells++;
            updateCells(coordinate);
            cell.setOriginalValue(originalValue);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            cell.setOriginalValue(previousOriginalValue);
            cell.setEffectiveValue(previousEffectiveValue);
            updateCells(coordinate);
            throw new IllegalArgumentException("Failed to update cell at " +
                    coordinate.getStringCord() + " because of " +
                    e.getMessage());

        }
    }


    @Override
    public void updateCells(Coordinate coordinate) {
        List<Cell> sortedCells = orderCellsForCalculation();
        for (Cell cell : sortedCells) {
            EffectiveValue previousEffectiveValue = cell.getEffectiveValue() != null ? cell.getEffectiveValue().copy() : null;
            try {
                if (cell.getEffectiveValue() != null) {
                    cell.getEffectiveValue().calculateValue(this, cell.getOriginalValue());
                    if (!cell.getEffectiveValue().equals(previousEffectiveValue)) {
                        removeDependence(cell);
                        counterChangedCells++;
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("error while updating cell at " +
                        cell.getCoordinate().getStringCord() +
                        " because " + e.getMessage());
            }
        }
    }

    private List<Cell> orderCellsForCalculation() {
        Map<Cell, List<Cell>> graph = new HashMap<>();
        Map<Cell, Integer> inDegree = new HashMap<>();
        List<Cell> orderedCells = new ArrayList<>();

        for (Cell cell : cells.values()) {
            inDegree.put(cell, 0);
        }

        for (Edge edge : edges) {
            Cell fromCell = cells.get(edge.getFrom());
            Cell toCell = cells.get(edge.getTo());

            if (fromCell != null && toCell != null) {
                graph.computeIfAbsent(fromCell, k -> new ArrayList<>()).add(toCell);
                inDegree.put(toCell, inDegree.get(toCell) + 1);
            }
        }

        Queue<Cell> queue = new LinkedList<>();

        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            orderedCells.add(current);

            List<Cell> neighbors = graph.getOrDefault(current, new ArrayList<>());
            for (Cell neighbor : neighbors) {
                int newInDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newInDegree);

                if (newInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (orderedCells.size() != inDegree.size()) {
            throw new IllegalStateException("Circular dependency detected");
        }

        return orderedCells;
    }


    @Override
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    @Override
    public Map<Coordinate, Cell> getCells() {
        return cells;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    private void removeDependence(Cell cell) {
        for (Coordinate cord : cell.getRelatedCells()) {
            Cell dependentCell = this.getCell(cord);
            dependentCell.getAffectedCells().remove(cell.getCoordinate());
            this.removeEdge(new Edge(cord, cell.getCoordinate()));
        }
        cell.getRelatedCells().clear();
    }

    @Override
    public Sheet sortSheet(Coordinate start, Coordinate end, List<String> selectedColumns) {
        if (selectedColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns selected for sorting.");
        }

        List<Integer> columnIndices = new ArrayList<>();
        for (String column : selectedColumns) {
            int columnIndex = column.charAt(0) - 'A';
            columnIndices.add(columnIndex);
        }

        if (columnIndices.isEmpty() || start.getRow() > end.getRow() || start.getColumn() > end.getColumn()) {
            throw new IllegalArgumentException("Invalid sorting range or columns.");
        }

        Map<Integer, List<Cell>> rows = new HashMap<>();
        for (int row = start.getRow(); row <= end.getRow(); row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int col = start.getColumn(); col <= end.getColumn(); col++) {
                Coordinate coordinate = new CoordinateImpl(row, col);
                Cell cell = getCell(coordinate);
                if (cell != null) {
                    rowCells.add(cell);
                }
            }
            if (!rowCells.isEmpty()) {
                rows.put(row, rowCells);
            }
        }

        if (rows.isEmpty()) {
            return this;
        }

        sortRowsByCol(columnIndices, 0, rows);

        Sheet newSheet = new SheetImpl(sheetName, rowSize, columnSize, columnWidthUnits, rowsHeightUnits, version);
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < columnSize; col++) {
                Coordinate coordinate = new CoordinateImpl(row, col);
                if (row >= start.getRow() && row <= end.getRow() && col >= start.getColumn() && col <= end.getColumn()) {
                    List<Cell> sortedRow = rows.get(row);
                    if (sortedRow != null && col - start.getColumn() < sortedRow.size()) {
                        Cell sortedCell = sortedRow.get(col - start.getColumn());
                        newSheet.addCell(sortedCell);
                    }
                } else {
                    Cell originalCell = this.getCell(coordinate);
                    if (originalCell != null)
                        newSheet.addCell(originalCell);
                }
            }
        }

        return newSheet;
    }

    private void sortRowsByCol(List<Integer> columnIndices, int index, Map<Integer, List<Cell>> rows) {
        List<Integer> rowNumbers = new ArrayList<>(rows.keySet());
        for (int i = 0; i < rowNumbers.size() - 1; i++) {
            for (int j = i + 1; j < rowNumbers.size(); j++) {
                int row1Num = rowNumbers.get(i);
                int row2Num = rowNumbers.get(j);

                Cell cell1 = null;
                Cell cell2 = null;

                List<Cell> row1 = rows.get(row1Num);
                List<Cell> row2 = rows.get(row2Num);

                for (Cell cell : row1) {
                    if (cell.getCoordinate().getColumn() == columnIndices.get(index)) {
                        cell1 = cell;
                        break;
                    }
                }

                for (Cell cell : row2) {
                    if (cell.getCoordinate().getColumn() == columnIndices.get(index)) {
                        cell2 = cell;
                        break;
                    }
                }

                if (cell1 != null && cell2 != null) {
                    double value1 = getEffectiveNumericValue(cell1);
                    double value2 = getEffectiveNumericValue(cell2);
                    if (!Double.isNaN(value1) && !Double.isNaN(value2)) {
                        if (value1 > value2) {
                            swapRows(row1Num, row2Num, rows);
                        } else if (value1 == value2) {
                            if (index + 1 < columnIndices.size()) {
                                sortRowsByCol(columnIndices, index + 1, rows);
                            }
                        }
                    }
                }
            }
        }
    }

    private void swapRows(int row1Num, int row2Num, Map<Integer, List<Cell>> rows) {
        List<Cell> rowOfCell1 = rows.get(row1Num);
        List<Cell> rowOfCell2 = rows.get(row2Num);

        List<Cell> newRow1 = new ArrayList<>();
        List<Cell> newRow2 = new ArrayList<>();

        for (Cell cell : rowOfCell1) {
            Coordinate oldCoordinate = cell.getCoordinate();
            Coordinate newCoordinate = new CoordinateImpl(row2Num, oldCoordinate.getColumn());
            Cell newCell = new CellImpl(cell);
            newCell.setCoordinate(newCoordinate);
            newRow1.add(newCell);
        }

        for (Cell cell : rowOfCell2) {
            Coordinate oldCoordinate = cell.getCoordinate();
            Coordinate newCoordinate = new CoordinateImpl(row1Num, oldCoordinate.getColumn());
            Cell newCell = new CellImpl(cell);
            newCell.setCoordinate(newCoordinate);
            newRow2.add(newCell);
        }

        rows.put(row1Num, newRow2);
        rows.put(row2Num, newRow1);
    }


    private double getEffectiveNumericValue(Cell cell) {
        if (cell != null && cell.getEffectiveValue() != null) {
            try {
                return Double.parseDouble(cell.getEffectiveValue().toString());
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
        return Double.NaN;
    }

    @Override
    public Sheet applyFilter(Coordinate startCell, Coordinate endCell, char selectedColumn, List<String> selectedValues) {
        Sheet filteredSheet = new SheetImpl(sheetName + " - Filtered", rowSize, columnSize, columnWidthUnits, rowsHeightUnits, version);
        int columnIndex = selectedColumn - 'A';
        for (int row = startCell.getRow(); row <= endCell.getRow(); row++) {
            Coordinate currentCoordinate = new CoordinateImpl(row, columnIndex);
            Cell cell = getCell(currentCoordinate);
            if (cell != null) {
                String cellValue = cell.getEffectiveValue() != null ? cell.getEffectiveValue().toString() : "";
                if (selectedValues.contains(cellValue)) {
                    for (int col = 0; col < columnSize; col++) {
                        Coordinate newCoordinate = new CoordinateImpl(row, col);
                        Cell originalCell = getCell(newCoordinate);
                        if (originalCell != null) {
                            filteredSheet.addCell(originalCell);
                        }
                    }
                }
            }
        }
        return filteredSheet;
    }

    @Override
    public void addRange(String rangeCells, String name) throws Exception {
        RangeFactory.addRange(rowSize, columnSize,name,rangeCells);
    }

    @Override
    public void deleteRange(String name)  throws Exception{
        RangeFactory.removeRange(name);
    }

    @Override
    public Map<String, Range> getRanges()
    {
        return ranges;
    }
}