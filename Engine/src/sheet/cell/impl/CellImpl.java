package sheet.cell.impl;

import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell, Serializable {
    private EffectiveValue effectiveValue;
    private String originalValue;
    private Coordinate coordinate;
    private List<Coordinate> relatedCells = new ArrayList<>();
    private List<Coordinate> affectedCells = new ArrayList<>();
    private int lastVersionUpdate = 1;
    private double rowsHeightUnits;
    private double columnWidthUnits;


    public CellImpl(Coordinate coordinate, double rowsHeightUnits, double columnWidthUnits) {
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
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    @Override
    public void setVersion(int version) {
        this.lastVersionUpdate = version;
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
    public List<Coordinate> getAffectedCells() {
        return affectedCells;
    }


    @Override
    public double getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    @Override
    public double getColumnWidthUnits() {
        return columnWidthUnits;
    }
}