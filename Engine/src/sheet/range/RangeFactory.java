package sheet.range;
import DTO.CoordinateDTO;

import java.util.HashMap;
import java.util.Map;

public class RangeFactory {
    private static Map<String, Range> ranges = ranges = new HashMap<>();

    public static boolean addRange(String name, String range) {
        if (ranges.containsKey(name)) {
            System.out.println("Error: Range name already exists.");
            return false;
        }

       Range newRange = new RangeImpl(name, range);
        ranges.put(name, newRange);
        return true;
    }

    public static boolean removeRange(String name) {
        if (!ranges.containsKey(name)) {
            System.out.println("Error: Range not found.");
            return false;
        }

        if (ranges.get(name).getIsUsedInFunction()) {
             System.out.println("Error: Range is in use.");
             return false;
         }

        ranges.remove(name);
        return true;
    }

    public static Range getRange(String name) {
        return ranges.get(name);
    }

    public static Boolean isRangeExist(String key) {
        return ranges.containsKey(key);
    }

}
