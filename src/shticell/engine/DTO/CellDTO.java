package shticell.engine.DTO;

import shticell.engine.sheet.cell.api.EffectiveValue;
import shticell.engine.sheet.coordinate.Coordinate;
import shticell.engine.sheet.coordinate.CoordinateParser;
import shticell.engine.sheet.coordinate.ParseException;

import java.util.List;

public class CellDTO {
    private final CoordinateDTO coordinate;
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final int lastVersionUpdate;
    private final List<Coordinate> relatedCells;
    private final List<Coordinate> affectedCells;

    public CellDTO(CoordinateDTO coordinate, String originalValue, EffectiveValue effectiveValue, int lastVersionUpdate, List<Coordinate> relatedCells, List<Coordinate> affectedCells) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastVersionUpdate = lastVersionUpdate;
        this.relatedCells = relatedCells;
        this.affectedCells = affectedCells;
    }

    public CoordinateDTO getCoordinateDTO() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastVersionUpdate() {
        return lastVersionUpdate;
    }

    public List<Coordinate> getRelatedCells() {
        return relatedCells;
    }

    public List<Coordinate> getAffectedCells() {
        return affectedCells;
    }

    @Override
    public String toString() {
        String originalValueString = (originalValue == null) ? "cell is empty" : originalValue;
        String effectiveValueString = (effectiveValue.getValue() == null) ? "cell is empty" : effectiveValue.getValue().toString();

        return "\n Coordinate: " + coordinate.getStringCord() +
                "\n Original Value: " + originalValueString +
                "\n Effective Value: " + effectiveValueString +
                "\n Last Version Update: " + lastVersionUpdate +
                "\n Related Cells: " + relatedCells +
                "\n Affected Cells: " + affectedCells +
                "\n}";
    }

}