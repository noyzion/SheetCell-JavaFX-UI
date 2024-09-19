package sheet.impl;

import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.CellImpl;
import sheet.cell.impl.EffectiveValueImp;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateImpl;

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


        List<List<Cell>> rows = new ArrayList<>();
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
                rows.add(rowCells);
            }
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No rows found in the specified range.");
        }

        sortRowsByCol(columnIndices, 0, rows);

        Sheet newSheet = new SheetImpl(sheetName, rowSize, columnSize, columnWidthUnits, rowsHeightUnits, version);
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < columnSize; col++) {
                Coordinate coordinate = new CoordinateImpl(row, col);
                if (row >= start.getRow() && row <= end.getRow() && col >= start.getColumn() && col <= end.getColumn()) {
                    List<Cell> sortedRow = rows.get(row - start.getRow());
                    if (col - start.getColumn() < sortedRow.size()) {
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

    private void sortRowsByCol(List<Integer> columnIndices, int index, List<List<Cell>> rows) {
        for (int row = 0; row < rows.size() - 1; row++) {
            for (int nextRow = row + 1; nextRow < rows.size(); nextRow++) {

                Cell cell1 = null;
                Cell cell2 = null;

                for (Cell cell : rows.get(row)) {
                    if (cell.getCoordinate().getColumn() == columnIndices.get(index)) {
                        cell1 = cell;
                        break;
                    }
                }

                for (Cell cell : rows.get(nextRow)) {
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
                            swapRows(rows.get(cell1.getCoordinate().getRow()), rows.get(cell2.getCoordinate().getRow()));
                        } else if (value1 == value2)
                            sortRowsByCol(columnIndices, index + 1, rows);
                    }
                }
            }
        }
    }

    private void swapRows(List<Cell> rowOfCell1, List<Cell> rowOfCell2) {
        int row1Index = rowOfCell1.get(0).getCoordinate().getRow();
        int row2Index = rowOfCell2.get(0).getCoordinate().getRow();

        List<Cell> newRow1 = new ArrayList<>();
        List<Cell> newRow2 = new ArrayList<>();

        for (Cell cell : rowOfCell1) {
            Coordinate oldCoordinate = cell.getCoordinate();
            Coordinate newCoordinate = new CoordinateImpl(row2Index, oldCoordinate.getColumn());
            Cell newCell = new CellImpl(cell);
            newCell.setCoordinate(newCoordinate);
            newRow1.add(newCell);
        }

        for (Cell cell : rowOfCell2) {
            Coordinate oldCoordinate = cell.getCoordinate();
            Coordinate newCoordinate = new CoordinateImpl(row1Index, oldCoordinate.getColumn());
            Cell newCell = new CellImpl(cell);
            newCell.setCoordinate(newCoordinate);
            newRow2.add(newCell);
        }
        rowOfCell1.clear();
        rowOfCell2.clear();
        rowOfCell1.addAll(newRow1);
        rowOfCell2.addAll(newRow2);

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
}