package shticell.engine.sheet.cell.impl;

import shticell.engine.sheet.cell.api.Cell;
import shticell.engine.sheet.cell.api.EffectiveValue;
import shticell.engine.sheet.coordinate.Coordinate;
import shticell.engine.sheet.coordinate.CoordinateImpl;

import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell {
    private EffectiveValue effectiveValue;
    private String originalValue;
    private final Coordinate coordinate;
    private List<Coordinate> relatedCells = new ArrayList<>();
    private List<Coordinate> affectedCells = new ArrayList<>();
    private int lastVersionUpdate = 1;
    private final int rowsHeightUnits;
    private final int columnWidthUnits;


    public CellImpl(Coordinate coordinate, int rowsHeightUnits, int columnWidthUnits) {
        this.coordinate = coordinate;
        this.rowsHeightUnits = rowsHeightUnits;
        this.columnWidthUnits = columnWidthUnits;
    }

    public CellImpl(Cell other) {
        this.coordinate = new CoordinateImpl(
                other.getCoordinate().getRow(),
                other.getCoordinate().getColumn(),
                other.getCoordinate().getStringCord()
        );
        this.originalValue = other.getOriginalValue();
        this.effectiveValue = other.getEffectiveValue() != null ? other.getEffectiveValue().copy() : null;
        this.lastVersionUpdate = other.getVersion();
        this.relatedCells = new ArrayList<>(other.getRelatedCells());
        this.affectedCells = new ArrayList<>(other.getAffectedCells());
        this.rowsHeightUnits = other.getRowsHeightUnits();
        this.columnWidthUnits = other.getColumnWidthUnits();
    }


    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    @Override
    public boolean isInBounds() {
        int widthLength = this.effectiveValue.getValue().toString().length();
        return widthLength >= this.columnWidthUnits;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    @Override
    public void setEffectiveValue(EffectiveValue value) {
        this.effectiveValue = value;
    }

    @Override
    public void addCellToRelatedCells(Coordinate coordinate) {
        if (!relatedCells.contains(coordinate)) {
            relatedCells.add(coordinate);
        }
    }

    @Override
    public void addCellToAffectedCells(Coordinate coordinate) {
        if (!affectedCells.contains(coordinate)) {
            affectedCells.add(coordinate);
        }
    }


    @Override
    public List<Coordinate> getRelatedCells() {
        return relatedCells;
    }

    @Override
    public int getVersion() {
        return lastVersionUpdate;
    }

    @Override
    public void updateVersion() {
        this.lastVersionUpdate++;
    }

    @Override
    public List<Coordinate> getAffectedCells() {
        return affectedCells;
    }


    @Override
    public int getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    @Override
    public int getColumnWidthUnits() {
        return columnWidthUnits;
    }
}