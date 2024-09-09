package sheet;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.util.Map;

public class SheetController {

    private SheetDTO sheetDTO;
    private GridPane gridPane = new GridPane();
    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void createGridFromSheetDTO() {
        gridPane.getChildren().clear(); // Clear existing content

        Map<CoordinateDTO, CellDTO> cells = sheetDTO.getCells();
        int rows = sheetDTO.getRowSize();
        int columns = sheetDTO.getColumnSize();

        // Create column headers (A, B, C...)
        for (int col = 0; col < columns; col++) {
            String colHeader = String.valueOf((char) ('A' + col));
            Label colLabel = createCellLabel(colHeader, "header");
            gridPane.add(colLabel, col + 1, 0);
        }

        // Create row headers (1, 2, 3...)
        for (int row = 0; row < rows; row++) {
            String rowHeader = String.valueOf(row + 1);
            Label rowLabel = createCellLabel(rowHeader, "header");
            gridPane.add(rowLabel, 0, row + 1);
        }

        // Create actual cells
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
        gridPane.getChildren().clear(); // Clear all cells and headers
    }
    private Label createCellLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setPrefWidth(80);
        label.setPrefHeight(40);
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
