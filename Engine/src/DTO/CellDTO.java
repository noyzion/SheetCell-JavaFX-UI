package DTO;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import java.util.List;

public class CellDTO {
    private final CoordinateDTO coordinate;
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final int lastVersionUpdate;
    private final List<CoordinateDTO> relatedCells;
    private final List<CoordinateDTO> affectedCells;

    public CellDTO(CoordinateDTO coordinate, String originalValue, EffectiveValue effectiveValue, int lastVersionUpdate, List<CoordinateDTO> relatedCells, List<CoordinateDTO> affectedCells) {
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
        if (originalValue == null)
            return "empty cell";
        else
            return originalValue;

    }
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastVersionUpdate() {
        return lastVersionUpdate;
    }

    public List<CoordinateDTO> getRelatedCells() {
        return relatedCells;
    }

    public List<CoordinateDTO> getAffectedCells() {
        return affectedCells;
    }

    @Override
    public String toString() {
        String originalValueString = (originalValue == null) ? "cell is empty" : originalValue;
        String effectiveValueString = (effectiveValue.getValue() == null) ? "cell is empty" : effectiveValue.getValue().toString();

        return "\n Coordinate: " + coordinate.toString() +
                "\n Original Value: " + originalValueString +
                "\n Effective Value: " + effectiveValueString +
                "\n Last Version Update: " + lastVersionUpdate +
                "\n Related Cells: " + relatedCells +
                "\n Affected Cells: " + affectedCells +
                "\n}";
    }

}