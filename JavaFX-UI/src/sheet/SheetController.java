package sheet;
import DTO.CellDTO;
import DTO.CellStyle;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import commands.CommandsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import mainController.AppController;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SheetController {

    private SheetDTO sheetDTO;
    private GridPane gridPane = new GridPane();
    private AppController mainController;
    private boolean readOnly = false;
    private String sheetStyle = "/sheet/styles/basicStyle.css";
    private double startX, startY;
    private int resizingColumn, resizingRow;
    private Map<CoordinateDTO, CellStyle> cellStyles = new HashMap<>();
    private Map<Integer, Pos> columnAlignments = new HashMap<>();

    public SheetController() {
    }

    public SheetController(SheetDTO sheetDTO) {
        this.sheetDTO = sheetDTO;
    }

    private void applyStyle() {
        gridPane.getStylesheets().clear();
        URL cssUrl = getClass().getResource(sheetStyle);
        gridPane.getStylesheets().add(cssUrl.toExternalForm());
    }

    public void setSheetStyle(String styleName) {
        String newStyle = switch (styleName) {
            case "Basic" ->  "/sheet/styles/basicStyle.css";
            case "Pink" -> "/sheet/styles/PinkStyle.css";
            case "Blue" -> "/sheet/styles/BlueStyle.css";
            case "Green" -> "/sheet/styles/GreenStyle.css";
            default -> "/sheet/styles/basicStyle.css";
        };
        sheetStyle = newStyle;
        applyStyle();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setColumnAlignment(int columnIndex, Pos alignment) {
        if(columnAlignments.containsKey(columnIndex)) {
            columnAlignments.remove(columnIndex);
        }
        columnAlignments.put(columnIndex, alignment);
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                Integer colIndex = GridPane.getColumnIndex(label);

                if (colIndex != null && colIndex == columnIndex && label.getStyleClass().contains("cell")) {
                    label.setAlignment(alignment);
                }
            }
        }
    }


    public void createGridFromSheetDTO() {
        gridPane.getChildren().clear();
        gridPane.setHgap(10);
        gridPane.setVgap(30);

        Map<CoordinateDTO, CellDTO> cells = sheetDTO.getCells();
        int rows = sheetDTO.getRowSize();
        int columns = sheetDTO.getColumnSize();

        initializeColumnConstraints(columns);
        initializeRowConstraints(rows);

        applyStyle();

        for (int col = 0; col < columns; col++) {
            String colHeader = String.valueOf((char) ('A' + col));
            Label colLabel = createCellLabel(colHeader, "header-col", null);
            gridPane.add(colLabel, col + 1, 0);
            addColumnResizeHandle(col + 1);
        }

        for (int row = 0; row < rows; row++) {
            String rowHeader = String.valueOf(row + 1);
            Label rowLabel = createCellLabel(rowHeader, "header-row", null);
            gridPane.add(rowLabel, 0, row + 1);
            addRowResizeHandle(row + 1);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                CoordinateDTO coordinate = new CoordinateDTO(row, col);
                String cellValue = cells.containsKey(coordinate) && cells.get(coordinate).getEffectiveValue().getValue() != null
                        ? cells.get(coordinate).getEffectiveValue().getValue().toString()
                        : " ";

                String styleClass = readOnly ? "read-only-label" : "cell";
                Label cellLabel = createCellLabel(cellValue, styleClass, coordinate);

                if (cellStyles.containsKey(coordinate)) {
                    CellStyle cellStyle = cellStyles.get(coordinate);
                    cellLabel.setTextFill(cellStyle.getTextColor());
                    cellLabel.setStyle("-fx-background-color: " + toRgbString(cellStyle.getBackgroundColor()));
                }

                gridPane.add(cellLabel, col + 1, row + 1);
            }
        }

        if (readOnly) {
            makeGridReadOnly();
        }
    }
    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void updateCellStyle(CoordinateDTO coordinate, Color backgroundColor, Color textColor) {
        CellStyle style = new CellStyle(backgroundColor,textColor);
        cellStyles.put(coordinate, style);
        createGridFromSheetDTO();
    }

    public void resetCellStyle(CoordinateDTO coordinate) {
            cellStyles.remove(coordinate);
            createGridFromSheetDTO();
    }

    public void clearGrid() {
        gridPane.getChildren().clear();
    }

    private Label createCellLabel(String text, String styleClass, CoordinateDTO coordinate) {
        Label label = new Label(text);
        double columnWidth = sheetDTO.getColumnWidthUnits();
        double rowHeight = sheetDTO.getRowsHeightUnits();

        if (!readOnly && "cell".equals(styleClass)) {
            label.setOnMouseClicked(event -> handleCellClick(coordinate));
        } else if (!readOnly && "header-col".equals(styleClass)) {
            label.setOnMouseClicked(event -> handleColumnHeaderClick(GridPane.getColumnIndex(label)));
        }

        if ("header-col".equals(styleClass) || "header-row".equals(styleClass)) {
            styleClass = "header";
        }

        if(coordinate != null) {
            Pos alignment = columnAlignments.getOrDefault((coordinate.getColumn()+1), Pos.CENTER);
            label.setAlignment(alignment);
        }

        label.setPrefWidth(columnWidth);
        label.setPrefHeight(rowHeight);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setPadding(new Insets(5));
        label.getStyleClass().add(styleClass);

        return label;
    }

    private void handleColumnHeaderClick(int columnIndex) {
        if(!readOnly) {
            CommandsController commandsController = mainController.getCommandsController();
            commandsController.setAligmentButtonsDisable(false);
            commandsController.updateColumn(columnIndex);
        }

    }

    private void handleCellClick(CoordinateDTO coordinate) {
        if (!readOnly) {
            mainController.updateActionLineFields(coordinate);
            CommandsController commandsController = mainController.getCommandsController();
                commandsController.updateCellCoordinate(coordinate);
                commandsController.setEditCellDisable(false);
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
                label.getStyleClass().add("read-only-label");
                label.setOnMouseClicked(null);
            } else if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setEditable(false);
            }
        });
    }

    private void restoreEditable() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getUserData() instanceof CoordinateDTO) {
                    label.setOnMouseClicked(event -> handleCellClick((CoordinateDTO) label.getUserData()));
                }
            } else if (node instanceof TextField) {
                ((TextField) node).setEditable(true);
            }
        });
    }

    private void addColumnResizeHandle(int colIndex) {
        Line resizeLine = new Line();
        resizeLine.setStartX(0);
        resizeLine.setEndX(0);
        resizeLine.setStartY(0);
        resizeLine.setEndY(0);
        resizeLine.setStroke(javafx.scene.paint.Color.GRAY);
        resizeLine.setStrokeWidth(5);
        resizeLine.setCursor(javafx.scene.Cursor.H_RESIZE);

        resizeLine.setOnMousePressed(event -> {
            startColumnResize(colIndex, event.getX());
        });
        resizeLine.setOnMouseDragged(event -> {
            resizeColumn(colIndex, event.getX());
        });
        resizeLine.setOnMouseReleased(event -> {
            endColumnResize();
        });

        gridPane.add(resizeLine, colIndex, 0);
    }

    private void addRowResizeHandle(int rowIndex) {
        Line resizeLine = new Line();
        resizeLine.setStartX(0);
        resizeLine.setEndX(0);
        resizeLine.setStartY(0);
        resizeLine.setEndY(0);
        resizeLine.setStroke(javafx.scene.paint.Color.GRAY);
        resizeLine.setStrokeWidth(5);
        resizeLine.setCursor(javafx.scene.Cursor.V_RESIZE);

        resizeLine.setOnMousePressed(event -> {
            startRowResize(rowIndex, event.getY());
        });
        resizeLine.setOnMouseDragged(event -> {
            resizeRow(rowIndex, event.getY());
        });
        resizeLine.setOnMouseReleased(event -> {
            endRowResize();
        });

        gridPane.add(resizeLine, 0, rowIndex); // Adjust position as needed
    }

    private void startColumnResize(int colIndex, double startX) {
        this.startX = startX;
        this.resizingColumn = colIndex;
    }

    private void resizeColumn(int colIndex, double currentX) {
        List<ColumnConstraints> columnConstraintsList = gridPane.getColumnConstraints();

        if (colIndex >= 0 && colIndex < columnConstraintsList.size()) {
            double deltaX = currentX - startX;
            ColumnConstraints constraints = columnConstraintsList.get(colIndex);
            double newWidth = constraints.getPrefWidth() + deltaX;

            if (newWidth < 0) {
                newWidth = 0;
            }

            constraints.setPrefWidth(newWidth);
            startX = currentX;
        } else {
            System.err.println("Column index " + colIndex + " is out of bounds. Column constraints size: " + columnConstraintsList.size());
        }
    }

    private void endColumnResize() {
        resizingColumn = -1;
    }

    private void startRowResize(int rowIndex, double startY) {
        this.startY = startY;
        this.resizingRow = rowIndex;
    }

    private void resizeRow(int rowIndex, double currentY) {
        List<RowConstraints> rowConstraintsList = gridPane.getRowConstraints();
        if (rowIndex >= 0 && rowIndex < rowConstraintsList.size()) {
            double deltaY = currentY - startY;
            RowConstraints constraints = rowConstraintsList.get(rowIndex);
            double newHeight = constraints.getPrefHeight() + deltaY;
            constraints.setPrefHeight(Math.max(newHeight, 0)); // Ensure newHeight is not negative
            startY = currentY;
        } else {
            System.err.println("Row index " + rowIndex + " is out of bounds. Row constraints size: " + rowConstraintsList.size());
        }
    }

    private void endRowResize() {
        resizingRow = -1;
    }

    private void initializeColumnConstraints(int numColumns) {
        gridPane.getColumnConstraints().clear();
        for (int i = 0; i < numColumns + 1; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(sheetDTO.getColumnWidthUnits()); // Ensure this returns a valid value
            gridPane.getColumnConstraints().add(columnConstraints);
        }
    }

    private void initializeRowConstraints(int numRows) {
        gridPane.getRowConstraints().clear();
        for (int i = 0; i < numRows + 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(sheetDTO.getRowsHeightUnits()); // Ensure this returns a valid value
            gridPane.getRowConstraints().add(rowConstraints);
        }
    }

}
