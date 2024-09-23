package sheet.range;
import DTO.CoordinateDTO;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.util.ArrayList;
import java.util.List;

public class RangeImpl implements Range {

    private String name;
    private String range;
    private Coordinate start;
    private Coordinate end;
    private List<Coordinate> cells;
    private boolean isUsedInFunction;

    public RangeImpl(String name, String range) {
        this.name = name;
        this.range = range;
        for(CoordinateDTO cord : RangeFactory.parseRange(start,end,range))
        {
            this.cells.add(new CoordinateImpl(cord.getRow(),cord.getColumn()));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStart()
    {
        return start.toString();
    }

    @Override
    public String getEnd()
    {
        return end.toString();
    }
    @Override
    public String getRange() {
        return range;
    }

    @Override
    public List<Coordinate> getCells() {
        return cells;
    }

    @Override
    public boolean overlaps(Range other) {
        for (Coordinate cell : cells) {
            if (other.getCells().contains(cell)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getIsUsedInFunction(){
        return this.isUsedInFunction;
    }

    @Override
    public void setIsUsedInFunction(boolean isUsedInFunction){
        this.isUsedInFunction = isUsedInFunction;
    }

}
