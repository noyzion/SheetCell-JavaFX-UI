package filter;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterController {

    private AppController mainController;

    @FXML
    private Button filterButton;
    @FXML
    private Button cancelFilterButton;
    @FXML
    private ComboBox<String> chooseColumn;
    @FXML
    private ComboBox<String> startCell;
    @FXML
    private ComboBox<String> endCell;
    private ListView<CheckBox> valueListView = new ListView<>();
    private boolean filter;
    private boolean isProcessing = false;
    private String selectedColumn;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        startCell.setDisable(true);
        endCell.setDisable(true);
        chooseColumn.setDisable(true);
        disableFilter();
        addInputListeners();
    }

    public void clearUIComponents() {
        chooseColumn.getItems().clear();
        chooseColumn.getSelectionModel().clearSelection();
        startCell.getItems().clear();
        endCell.getItems().clear();
        startCell.getSelectionModel().clearSelection();
        endCell.getSelectionModel().clearSelection();
        valueListView.getItems().clear();
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
        populateComboBoxWithColumns();
        populateComboBoxWithCells();
        startCell.setDisable(false);
        endCell.setDisable(false);
        chooseColumn.setDisable(false);
    }

    private void populateEndCell(String startCellValue) {
        if (startCellValue != null) {
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellValue);
            List<String> endCells = new ArrayList<>();
            int row = startCoordinate.getRow() + 2; // Starting 2 rows down
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

    private void populateComboBoxWithColumns() {
        chooseColumn.getItems().clear();
        for (char column = 0; column < mainController.getLatestSheet().getColumnSize(); column++) {
            String ch = CoordinateFactory.convertIndexToColumnLetter(column);
            chooseColumn.getItems().add(ch);
        }
    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
        });
        chooseColumn.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedColumn = newValue;
            validateInputs();
            validateAndPopulateValues();
        });
    }

    private void validateInputs() {
        boolean isColumnSelected = chooseColumn.getSelectionModel().getSelectedItem() != null;
        boolean isStartCellSelected = startCell.getSelectionModel().getSelectedItem() != null;
        boolean isEndCellSelected = endCell.getSelectionModel().getSelectedItem() != null;
        filterButton.setDisable(!(isColumnSelected && isStartCellSelected && isEndCellSelected));
    }

    private void validateAndPopulateValues() {
        if (isProcessing) return;
        isProcessing = true;
        try {
            boolean isColumnSelected = chooseColumn.getSelectionModel().getSelectedItem() != null;
            boolean isStartCellSelected = startCell.getSelectionModel().getSelectedItem() != null;
            boolean isEndCellSelected = endCell.getSelectionModel().getSelectedItem() != null;

            if (isColumnSelected && isStartCellSelected && isEndCellSelected) {
                String startCellText = startCell.getValue();
                String endCellText = endCell.getValue();
                String selectedColumn = chooseColumn.getValue();

                if (selectedColumn != null && !selectedColumn.isEmpty()) {
                    populateUniqueValuesAndShowPopup(startCellText, endCellText);
                }
            }
        } finally {
            isProcessing = false;
        }
    }

    private void populateUniqueValuesAndShowPopup(String startCellText, String endCellText) {
        CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
        CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);

        List<String> uniqueValues = extractUniqueValues(startCoordinate, endCoordinate);
        if (uniqueValues.isEmpty()) {
            mainController.showErrorDialog("No Unique Values", "No unique values found", "Please check the selected column.");
            return;
        }

        populateValueListView(uniqueValues);
    }

    private List<String> extractUniqueValues(CoordinateDTO start, CoordinateDTO end) {
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

    private void populateValueListView(List<String> uniqueValues) {
        valueListView.getItems().clear();
        for (String value : uniqueValues) {
            CheckBox checkBox = new CheckBox(value);
            valueListView.getItems().add(checkBox);
        }
    }

    private void showFilterPopup() {
        Alert dialog = new Alert(Alert.AlertType.NONE, "Select values to filter:");
        dialog.initModality(Modality.APPLICATION_MODAL); // Make the dialog modal

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.getChildren().add(valueListView);

        ButtonType okButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);


        dialog.getDialogPane().setContent(dialogPaneContent);
        dialog.showAndWait();

    }


    @FXML
    private void handleFilterButtonAction() {
        filter = true;
        String startCellText = startCell.getValue();
        String endCellText = endCell.getValue();
        try {
            if (startCellText == null || endCellText == null || selectedColumn == null) {
                mainController.showErrorDialog("Input Error", "Invalid input", "Please specify a range and a column.");
                return;
            }
            showFilterPopup();

            List<String> selectedValues = new ArrayList<>();
            for (CheckBox checkBox : valueListView.getItems()) {
                if (checkBox.isSelected()) {
                    selectedValues.add(checkBox.getText());
                }
            }

            if (selectedValues.isEmpty()) {
                mainController.showErrorDialog("Filter Error", "No values selected", "Please select at least one value to filter.");
                return;
            }

            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);
            mainController.filterSheet(startCoordinate, endCoordinate, selectedColumn.charAt(0), selectedValues);
            cancelFilterButton.setDisable(false);
        } catch (Exception e) {
            String errorTitle = "Error";
            String errorHeader = "Failed to filter the selected cells";
            String errorMessage = "An error occurred during the filter: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
    }
}
