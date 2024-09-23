package logic;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConverterUtil {

    public static CoordinateDTO toCoordinateDTO(Coordinate coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinate cannot be null.");
        }
        return new CoordinateDTO(coordinate.getRow(), coordinate.getColumn(), coordinate.getStringCord());
    }

    public static CellDTO toCellDTO(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell cannot be null.");
        }

        // Convert Coordinate to CoordinateDTO
        CoordinateDTO cellCoordinateDTO = toCoordinateDTO(cell.getCoordinate());

        // Convert lists of Coordinate to lists of CoordinateDTO
        List<CoordinateDTO> relatedCellsDTO = cell.getRelatedCells().stream()
                .map(ConverterUtil::toCoordinateDTO)
                .collect(Collectors.toList());

        List<CoordinateDTO> affectedCellsDTO = cell.getAffectedCells().stream()
                .map(ConverterUtil::toCoordinateDTO)
                .collect(Collectors.toList());

        return new CellDTO(
                cellCoordinateDTO,
                cell.getOriginalValue(),
                cell.getEffectiveValue() != null ? cell.getEffectiveValue() : null,
                cell.getVersion(),
                relatedCellsDTO,
                affectedCellsDTO
        );
    }


    public static SheetDTO toSheetDTO(SheetReadActions sheet) {
        Map<CoordinateDTO, CellDTO> cellDTOs = sheet.getCells().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> toCoordinateDTO(entry.getKey()),
                        entry -> toCellDTO(entry.getValue())));
        return new SheetDTO(
                sheet.getSheetName(),
                sheet.getVersion(),
                sheet.getRowSize(),
                sheet.getColSize(),
                sheet.getColumnWidthUnits(),
                sheet.getRowsHeightUnits(),
                cellDTOs,
                sheet.getEdges(),
                sheet.getCounterChangedCells(),
                sheet.getRanges());
    }

}
