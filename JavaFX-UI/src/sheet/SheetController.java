package sheet;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import mainContoroller.AppController;

import java.net.URL;
import java.util.Map;

public class SheetController {

    private SheetDTO sheetDTO;
    private GridPane gridPane = new GridPane();
    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void createGridFromSheetDTO() {
        gridPane.getChildren().clear();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Map<CoordinateDTO, CellDTO> cells = sheetDTO.getCells();
        int rows = sheetDTO.getRowSize();
        int columns = sheetDTO.getColumnSize();

        URL cssUrl = getClass().getResource("/sheet/styles/basicStyle.css");
        gridPane.getStylesheets().add(cssUrl.toExternalForm());

        for (int col = 0; col < columns; col++) {
            String colHeader = String.valueOf((char) ('A' + col));
            Label colLabel = createCellLabel(colHeader, "header");
            gridPane.add(colLabel, col + 1, 0);
        }

        for (int row = 0; row < rows; row++) {
            String rowHeader = String.valueOf(row + 1);
            Label rowLabel = createCellLabel(rowHeader, "header");
            gridPane.add(rowLabel, 0, row + 1);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                CoordinateDTO coordinate = new CoordinateDTO(row, col + 1);
                String cellValue = " "; // Default empty cell

                if (cells.containsKey(coordinate)) {
                    cellValue = cells.get(coordinate).getEffectiveValue().getValue().toString();
                }

                Label cellLabel = createCellLabel(cellValue, "label");
                gridPane.add(cellLabel, col + 1, row + 1);
            }
        }
    }
    public void clearGrid() {
        gridPane.getChildren().clear();
    }
    private Label createCellLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setPrefWidth(sheetDTO.getColumnWidthUnits());
        label.setPrefHeight(sheetDTO.getRowsHeightUnits());
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add(styleClass);
        return label;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setSheetDTO(SheetDTO sheetDTO) {
        this.sheetDTO = sheetDTO;
    }
}
