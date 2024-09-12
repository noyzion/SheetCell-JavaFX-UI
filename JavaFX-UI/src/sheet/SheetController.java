package sheet;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import mainContoroller.AppController;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        URL cssUrl = getClass().getResource("/sheet/styles/BasicStyle.css");
        gridPane.getStylesheets().add(cssUrl.toExternalForm());

        for (int col = 0; col < columns; col++) {
            String colHeader = String.valueOf((char) ('A' + col));
            Label colLabel = createCellLabel(colHeader, "header",null);
            gridPane.add(colLabel, col + 1, 0);
        }

        for (int row = 0; row < rows; row++) {
            String rowHeader = String.valueOf(row + 1);
            Label rowLabel = createCellLabel(rowHeader, "header",null);
            gridPane.add(rowLabel, 0, row + 1);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                CoordinateDTO coordinate = new CoordinateDTO(row, col);
                String cellValue = " ";

                if (cells.containsKey(coordinate)) {
                    if(cells.get(coordinate).getEffectiveValue().getValue()== null)
                        cellValue = " ";
                    else
                    cellValue = cells.get(coordinate).getEffectiveValue().getValue().toString();
                }

                Label cellLabel = createCellLabel(cellValue, "label",coordinate);
                gridPane.add(cellLabel, col + 1, row + 1);
            }
        }
    }

    public void clearGrid() {
        gridPane.getChildren().clear();
    }
    private Label createCellLabel(String text, String styleClass, CoordinateDTO coordinate) {
        Label label = new Label(text);
        label.setPrefWidth(sheetDTO.getColumnWidthUnits());
        label.setPrefHeight(sheetDTO.getRowsHeightUnits());
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add(styleClass);

        if(styleClass == "label") {
            label.setOnMouseClicked(event -> handleCellClick(coordinate));
        }

        return label;
    }
    private void handleCellClick(CoordinateDTO coordinate) {

        mainController.updateActionLineFields(coordinate);
    }

    public List<String> getAllCellNames() {
        int numRows = sheetDTO.getRowSize();
        int numCols = sheetDTO.getColumnSize();

        return IntStream.range(0, numCols)
                .mapToObj(col -> (char) ('A' + col))
                .flatMap(colLetter -> IntStream.range(1, numRows + 1)
                        .mapToObj(row -> colLetter + String.valueOf(row)))
                .sorted()
                .collect(Collectors.toList());
    }
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setSheetDTO(SheetDTO sheetDTO) {
        this.sheetDTO = sheetDTO;
    }
}
