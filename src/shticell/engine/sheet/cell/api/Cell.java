package shticell.engine.sheet.cell.api;

import shticell.engine.sheet.coordinate.Coordinate;

import java.util.List;

public interface Cell {

    Coordinate getCoordinate();

    boolean isInBounds();

    String getOriginalValue();

    void setOriginalValue(String value);

    EffectiveValue getEffectiveValue();

    void setEffectiveValue(EffectiveValue value);

    int getVersion();

    List<Coordinate> getRelatedCells();

    List<Coordinate> getAffectedCells();

    void addCellToAffectedCells(Coordinate coordinate);

    void addCellToRelatedCells(Coordinate coordinate);

    void updateVersion();


    int getRowsHeightUnits();

    int getColumnWidthUnits();
}