package sheet.cell.api;

import sheet.coordinate.Coordinate;

import java.util.List;

public interface Cell {

    Coordinate getCoordinate();

    String getOriginalValue();

    void setOriginalValue(String value);

    EffectiveValue getEffectiveValue();

    void setEffectiveValue(EffectiveValue value);

    int getVersion();

    List<Coordinate> getRelatedCells();

    List<Coordinate> getAffectedCells();

    void addCellToAffectedCells(Coordinate coordinate);

    void addCellToRelatedCells(Coordinate coordinate);

    int getRowsHeightUnits();

    int getColumnWidthUnits();

    void setVersion(int version);

}