package sort;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;

import java.util.ArrayList;
import java.util.List;

public class SortController {

    private AppController mainController;

    @FXML private Button sortButton;
    @FXML private Button cancelSortButton;
    @FXML private ComboBox<String> startCell;
    @FXML private ComboBox<String> endCell;
    @FXML private VBox checkboxContainer;
    private boolean sort;
    private List<CheckBox> columnCheckBoxes = new ArrayList<>();


    public void setMainController(AppController mainController) {
        this.mainController = mainController;

        startCell.setDisable(true);
        endCell.setDisable(true);
        disableSort();
        addInputListeners();
    }

    public void clearUIComponents() {
        startCell.getItems().clear();
        endCell.getItems().clear();
        startCell.getSelectionModel().clearSelection();
        endCell.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSortAction() {
        sort = true;
        String startCellText = startCell.getValue();
        String endCellText = endCell.getValue();

        try {
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);

            List<String> selectedColumns = new ArrayList<>();
            for (CheckBox checkBox : columnCheckBoxes) {
                if (checkBox.isSelected()) {
                    selectedColumns.add(checkBox.getText());
                }
            }

            if (selectedColumns.isEmpty()) {
                mainController.showErrorDialog("No Columns Selected", "Column Selection Error", "You must select at least one column for sorting.");
                return;
            }

            SheetDTO sheet = mainController.getLatestSheet();
            mainController.sortSheet(startCoordinate, endCoordinate, selectedColumns);
            cancelSortButton.setDisable(false);

        } catch (Exception e) {
            String errorTitle = "Sorting Error";
            String errorHeader = "Failed to sort the selected cells";
            String errorMessage = "An error occurred during sorting: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
    }

    @FXML
    private void handleCancelSortAction() {
        if (sort) {
            mainController.getSheetComponentController().setSheetDTO(mainController.getLatestSheet());
            mainController.showSheet(mainController.getLatestSheet(), false);
            disableSort();
        }
    }

    public void disableSort() {
        endCell.setDisable(true);
        cancelSortButton.setDisable(true);
        sortButton.setDisable(true);
        sort = false;
    }

    public void enableSort() {
        populateComboBoxWithCells();
        startCell.setDisable(false);
    }

    private void generateColumnCheckBoxes(String start, String end) {
        checkboxContainer.getChildren().clear();
        columnCheckBoxes.clear();

        if ( start == null || end == null) {
            return;
        }
        else if(start.isEmpty() || end.isEmpty()) {
            return;
        }

        try {
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(start);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(end);

            String startColumn = CoordinateFactory.convertIndexToColumnLetter(startCoordinate.getColumn());
            String endColumn = CoordinateFactory.convertIndexToColumnLetter(endCoordinate.getColumn());

            List<String> columns = new ArrayList<>();
            for (char column = startColumn.charAt(0); column <= endColumn.charAt(0); column++) {
                columns.add(String.valueOf(column));
            }

            for (String column : columns) {
                CheckBox checkBox = new CheckBox(column);
                checkBox.setOnAction(event -> checkIfSortCanBeEnabled());
                columnCheckBoxes.add(checkBox);
                checkboxContainer.getChildren().add(checkBox);
            }
        } catch (Exception e) {
            String errorTitle = "Checkbox Generation Error";
            String errorHeader = "Failed to generate column checkboxes";
            String errorMessage = "An error occurred while generating checkboxes: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
        checkIfSortCanBeEnabled();
    }

    private void checkIfSortCanBeEnabled() {
        boolean canEnable = !startCell.getValue().isEmpty() && !endCell.getValue().isEmpty();
        boolean anyColumnSelected = columnCheckBoxes.stream().anyMatch(CheckBox::isSelected);
        sortButton.setDisable(!(canEnable && anyColumnSelected));
    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
            generateColumnCheckBoxes(newValue, endCell.getValue());
        });

        endCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            generateColumnCheckBoxes(startCell.getValue(), newValue);
        });
    }

    private void populateEndCell(String startCellValue) {
        if (startCellValue != null) {
            if(!startCellValue.isEmpty()) {
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
}