package xmlParse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateParser;
import sheet.impl.SheetImpl;
import xmlParse.jaxb.*;

import java.io.File;


public class XmlSheetLoader {


    public static Sheet fromXmlFileToObject(String filePath) {

        try {
            XmlSheetValidator.validateXmlPath(filePath);
            File file = new File(filePath);
            XmlSheetValidator.isXmlFileExists(file);
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            STLSheet sheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);

            XmlSheetValidator.validateSheetSize(sheet);
            XmlSheetValidator.validateCellsWithinBounds(sheet);
            return convert(sheet);

        } catch (JAXBException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Sheet convert(STLSheet stlSheet) throws Exception {
        if (stlSheet == null) {
            return null;
        }

        double colWidthUnits = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        double rowHeightUnits = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        String name = stlSheet.getName();
        int rowSize = stlSheet.getSTLLayout().getRows();
        int columnSize = stlSheet.getSTLLayout().getColumns();
        STLCells stlCells = stlSheet.getSTLCells();
        STLRanges ranges = stlSheet.getSTLRanges();

        double[][] colsWidth = new double[rowSize][columnSize];
        double[][] rowsHeight = new double[rowSize][columnSize];

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                colsWidth[i][j] = colWidthUnits;
                rowsHeight[i][j] = rowHeightUnits;
            }
        }
        Sheet sheet = new SheetImpl(name, rowSize, columnSize, colsWidth, rowsHeight, 1);
        for (STLRange range : ranges.getSTLRange()){
            STLBoundaries boundaries = range.getSTLBoundaries();
            String toCoordinate = boundaries.getTo();
            String fromCoordinate = boundaries.getFrom();
            sheet.addRange(fromCoordinate + ".." + toCoordinate,range.getName());
        }
        for (STLCell stlCell : stlCells.getSTLCell()) {
            String stringCord = stlCell.getColumn() + stlCell.getRow();
            Coordinate coordinate = CoordinateParser.parse(stringCord);
            Cell newCell = new CellImpl(coordinate, sheet.getCellRowHeightUnits(stringCord), sheet.getCellColWidthUnits(stringCord));
            sheet.addCell(newCell);
            String originalValue = stlCell.getSTLOriginalValue();
            sheet.onCellUpdated(originalValue, coordinate);
        }
        sheet.setCounter(sheet.getCells().size());
        return sheet;
    }
}