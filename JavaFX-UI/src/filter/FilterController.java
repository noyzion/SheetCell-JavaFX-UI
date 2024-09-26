package filter;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;

import java.util.*;

public class FilterController {

    private AppController mainController;

    @FXML
    private Button filterButton;
    @FXML
    private Button cancelFilterButton;
    @FXML
    private ComboBox<String> startCell;
    @FXML
    private ComboBox<String> endCell;
    @FXML
    private VBox checkboxContainer;
    private ScrollPane valueScrollPane = new ScrollPane();
    private VBox valueListViewContainer = new VBox();
    private boolean filter;
    private boolean isProcessing = false;
    private List<String> selectedColumns = new ArrayList<>();

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        startCell.setDisable(true);
        endCell.setDisable(true);
        disableFilter();
        addInputListeners();

        valueScrollPane.setContent(valueListViewContainer);
        valueScrollPane.setFitToWidth(true);
        valueListViewContainer.setSpacing(10);
    }

    public void clearUIComponents() {
        startCell.getItems().clear();
        endCell.getItems().clear();
        startCell.getSelectionModel().clearSelection();
        endCell.getSelectionModel().clearSelection();
        valueListViewContainer.getChildren().clear();
        selectedColumns.clear();
        checkboxContainer.getChildren().clear();
    }

    @FXML
    private void handleCancelFilterButton() {
        if (filter) {
            mainController.getSheetComponentController().setSheetDTO(mainController.getLatestSheet());
            mainController.showSheet(mainController.getLatestSheet(), false);
            disableFilter();
        }
    }

    public void disableFilter() {
        filterButton.setDisable(true);
        cancelFilterButton.setDisable(true);
        filter = false;
    }

    public void enableFilter() {
        populateComboBoxWithCells();
        startCell.setDisable(false);
    }

    private void populateEndCell(String startCellValue) {
        if (startCellValue != null && !startCellValue.isEmpty()) {
            endCell.setDisable(false);
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellValue);
            List<String> endCells = new ArrayList<>();
            int row = startCoordinate.getRow() + 2;
            for (int column = startCoordinate.getColumn(); column < mainController.getLatestSheet().getColumnSize(); column++) {
                while (row <= mainController.getLatestSheet().getRowSize()) {
                    String endCellValue = CoordinateFactory.convertIndexToColumnLetter(column) + row;
                    endCells.add(endCellValue);
                    row++;
                }
                row = 1;
            }

            endCell.getItems().clear();
            endCell.getItems().addAll(endCells);

            if (!endCells.isEmpty()) {
                endCell.getSelectionModel().selectFirst();
            }
        }
    }

    private void populateComboBoxWithCells() {
        for (char column = 0; column < mainController.getLatestSheet().getColumnSize(); column++) {
            for (int row = 1; row <= mainController.getLatestSheet().getRowSize(); row++) {
                String ch = CoordinateFactory.convertIndexToColumnLetter(column);
                String cell = "" + ch + row;
                startCell.getItems().add(cell);
            }
        }
    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
        });

        endCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateColumnCheckBoxes();
        });
    }

    private void validateAndPopulateValues() {
        if (isProcessing) return;
        isProcessing = true;
        try {
            boolean isStartCellSelected = startCell.getSelectionModel().getSelectedItem() != null;
            boolean isEndCellSelected = endCell.getSelectionModel().getSelectedItem() != null;
            boolean isColumnSelected = !selectedColumns.isEmpty(); // Check if any columns are selected

            if (isColumnSelected && isStartCellSelected && isEndCellSelected) {
                String startCellText = startCell.getValue();
                String endCellText = endCell.getValue();

                populateUniqueValuesAndShowPopup(startCellText, endCellText);
            }
        } finally {
            isProcessing = false;
        }
    }

    private void populateUniqueValuesAndShowPopup(String startCellText, String endCellText) {
        CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
        CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);

        Map<String, List<String>> columnUniqueValues = new HashMap<>();
        for (String selectedColumn : selectedColumns) {
            List<String> uniqueValues = extractUniqueValues(startCoordinate, endCoordinate, selectedColumn);
            columnUniqueValues.put(selectedColumn, uniqueValues);
        }

        populateValueListView(columnUniqueValues);
    }

    private List<String> extractUniqueValues(CoordinateDTO start, CoordinateDTO end, String selectedColumn) {
        Set<String> uniqueValues = new HashSet<>();
        SheetDTO sheet = mainController.getLatestSheet();
        int columnIndex = selectedColumn.charAt(0) - 'A';

        for (int row = start.getRow(); row <= end.getRow(); row++) {
            CoordinateDTO cord = new CoordinateDTO(row, columnIndex);
            if (sheet.getCell(cord.toString()) != null) {
                String cellValue = sheet.getCell(cord.toString()).getEffectiveValue().toString();
                if (cellValue != null && !cellValue.isEmpty())
                    uniqueValues.add(cellValue);
            }
        }
        return new ArrayList<>(uniqueValues);
    }

    private void populateValueListView(Map<String, List<String>> columnUniqueValues) {
        valueListViewContainer.getChildren().clear();

        for (Map.Entry<String, List<String>> entry : columnUniqueValues.entrySet()) {
            String columnName = entry.getKey();
            List<String> uniqueValues = entry.getValue();

            Label columnHeader = new Label("Column " + columnName);
            columnHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            VBox columnGroup = new VBox(5);
            columnGroup.getChildren().add(columnHeader);

            for (String value : uniqueValues) {
                CheckBox checkBox = new CheckBox(value);
                columnGroup.getChildren().add(checkBox);
            }

            valueListViewContainer.getChildren().add(columnGroup);
        }
    }

    @FXML
    private void handleFilterButtonAction() {
        filter = true;
        String startCellText = startCell.getValue();
        String endCellText = endCell.getValue();
        try {
            if (startCellText == null || endCellText == null || selectedColumns.isEmpty()) {
                mainController.showErrorDialog("Input Error", "Invalid input", "Please specify a range and at least one column.");
                return;
            }
            validateAndPopulateValues();
            showFilterPopup();

            List<String> selectedValues = new ArrayList<>();

            for (javafx.scene.Node node : valueListViewContainer.getChildren()) {
                if (node instanceof VBox columnGroup) {
                    for (javafx.scene.Node childNode : columnGroup.getChildren()) {
                        if (childNode instanceof CheckBox checkBox && checkBox.isSelected()) {
                            selectedValues.add(checkBox.getText());
                        }
                    }
                }
            }

            if (selectedValues.isEmpty()) {
                mainController.showErrorDialog("Filter Error", "No values selected", "Please select at least one value to filter.");
                return;
            }

            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);
            List<Integer> cols = new ArrayList<>();
            for (String col : selectedColumns) {
                cols.add(col.charAt(0) - 'A');
            }
            mainController.filterSheet(startCoordinate, endCoordinate, selectedValues, cols);

            cancelFilterButton.setDisable(false);

        } catch (Exception e) {
            String errorTitle = "Error";
            String errorHeader = "Failed to filter the selected cells";
            String errorMessage = "An error occurred during the filter: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
    }

    private void populateColumnCheckBoxes() {
        checkboxContainer.getChildren().clear();

        String startCellValue = startCell.getValue();
        String endCellValue = endCell.getValue();

        if (startCellValue == null || endCellValue == null) {
            return;
        } else if (startCellValue.isEmpty() || endCellValue.isEmpty()) {
            return;
        }
        try {
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellValue);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellValue);

            for (int colIndex = startCoordinate.getColumn(); colIndex <= endCoordinate.getColumn(); colIndex++) {
                String columnName = CoordinateFactory.convertIndexToColumnLetter(colIndex);
                CheckBox columnCheckBox = new CheckBox(columnName);
                columnCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        selectedColumns.add(columnName);
                    } else {
                        selectedColumns.remove(columnName);
                    }
                    filterButton.setDisable(selectedColumns.isEmpty());
                });
                checkboxContainer.getChildren().add(columnCheckBox);
            }
        } catch (Exception e) {
            String errorTitle = "Checkbox Generation Error";
            String errorHeader = "Failed to generate column checkboxes";
            String errorMessage = "An error occurred while generating checkboxes: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
    }

    private void showFilterPopup() {
        Alert dialog = new Alert(Alert.AlertType.NONE, "Select values to filter:");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.getChildren().add(valueScrollPane);

        ButtonType okButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(dialogPaneContent);
        dialog.showAndWait();
    }
}
