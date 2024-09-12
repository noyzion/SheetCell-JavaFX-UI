package sheet;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private boolean readOnly = false;  // Flag to indicate read-only state

    public SheetController() {
    }

    public SheetController(SheetDTO sheetDTO) {
        this.sheetDTO = sheetDTO;
    }

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

        // Create column headers
        for (int col = 0; col < columns; col++) {
            String colHeader = String.valueOf((char) ('A' + col));
            Label colLabel = createCellLabel(colHeader, "header", null);
            gridPane.add(colLabel, col + 1, 0);
        }

        // Create row headers
        for (int row = 0; row < rows; row++) {
            String rowHeader = String.valueOf(row + 1);
            Label rowLabel = createCellLabel(rowHeader, "header", null);
            gridPane.add(rowLabel, 0, row + 1);
        }

        // Create cell labels
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                CoordinateDTO coordinate = new CoordinateDTO(row, col);
                String cellValue = " ";

                if (cells.containsKey(coordinate)) {
                    if (cells.get(coordinate).getEffectiveValue().getValue() == null) {
                        cellValue = " ";
                    } else {
                        cellValue = cells.get(coordinate).getEffectiveValue().getValue().toString();
                    }
                }

                // Apply different style based on read-only state
                String styleClass = readOnly ? "read-only-label" : "label";
                Label cellLabel = createCellLabel(cellValue, styleClass, coordinate);

                gridPane.add(cellLabel, col + 1, row + 1);
            }
        }

        if (readOnly) {
            makeGridReadOnly();
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

        if (!readOnly && "label".equals(styleClass)) {
            label.setOnMouseClicked(event -> handleCellClick(coordinate));
        }

        return label;
    }

    private void handleCellClick(CoordinateDTO coordinate) {
        if (!readOnly) {
            mainController.updateActionLineFields(coordinate);
        }
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

    // Method to set the read-only mode
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if (readOnly) {
            makeGridReadOnly();
        } else {
            restoreEditable();
        }
    }

    private void makeGridReadOnly() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.getStyleClass().add("read-only-label");  // Add the read-only style
                label.setOnMouseClicked(null);  // Remove click events
            } else if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setEditable(false);  // Disable TextField if present
            }
            // Add other conditions if needed for different types of editable components
        });
    }


    // Method to restore editability of the grid
    private void restoreEditable() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                // Restore click events for labels with coordinates stored in their user data
                if (label.getUserData() instanceof CoordinateDTO) {
                    label.setOnMouseClicked(event -> handleCellClick((CoordinateDTO) label.getUserData()));
                }
            } else if (node instanceof TextField) {
                ((TextField) node).setEditable(true);  // Enable TextField if present
            }
            // Add other conditions if needed for different types of editable components
        });
    }
}
