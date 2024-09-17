package sheet.coordinate;

import DTO.CoordinateDTO;

import java.io.Serializable;

public class CoordinateParser implements Serializable {

        public static Coordinate parse(String coordinateString)  {

            coordinateString = coordinateString.toUpperCase().trim();
            int row = 0;
            int col = 0;

            int i = 0;
            while (i < coordinateString.length() && Character.isLetter(coordinateString.charAt(i))) {
                col = col * 26 + (coordinateString.charAt(i) - 'A' + 1);
                i++;
            }

            while (i < coordinateString.length() && Character.isDigit(coordinateString.charAt(i))) {
                row = row * 10 + (coordinateString.charAt(i) - '0');
                i++;
            }

            return new CoordinateImpl(row-1 , col-1 ,coordinateString);
        }

    public static CoordinateDTO parseDTO(String coordinateString)  {

        coordinateString = coordinateString.toUpperCase().trim();
        int row = 0;
        int col = 0;

        int i = 0;
        while (i < coordinateString.length() && Character.isLetter(coordinateString.charAt(i))) {
            col = col * 26 + (coordinateString.charAt(i) - 'A' + 1);
            i++;
        }

        while (i < coordinateString.length() && Character.isDigit(coordinateString.charAt(i))) {
            row = row * 10 + (coordinateString.charAt(i) - '0');
            i++;
        }

        return new CoordinateDTO(row-1 , col-1 ,coordinateString);
    }
    }


