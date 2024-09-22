package sheet.range;

import DTO.CoordinateDTO;
import sheet.coordinate.Coordinate;

import java.util.List;

public interface Range {
    String getName();
    String getRange();
    List<Coordinate> getCells();
    boolean overlaps(Range other);
    boolean getIsUsedInFunction();
    void setIsUsedInFunction(boolean isUsedInFunction);


}
