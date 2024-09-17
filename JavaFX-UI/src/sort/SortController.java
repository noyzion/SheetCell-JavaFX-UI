package sort;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    @FXML private TextField startCell;
    @FXML private TextField endCell;
    @FXML private VBox checkboxContainer;

    private boolean sort;
    private List<CheckBox> columnCheckBoxes = new ArrayList<>(); // Store checkboxes

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        startCell.setDisable(true);
        endCell.setDisable(true);
        disableSort();
        addInputListeners();
    }

    @FXML
    private void handleSortAction() {
        sort = true;
        String startCellText = startCell.getText();
        String endCellText = endCell.getText();

        try {
            CoordinateDTO startCoordinate = CoordinateParser.parseDTO(startCellText);
            CoordinateDTO endCoordinate = CoordinateParser.parseDTO(endCellText);

            // Collect selected columns from checkboxes
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

            // Perform sorting with the selected columns
            SheetDTO sheet = mainController.getLatestSheet();
            mainController.sortSheet(startCoordinate, endCoordinate, selectedColumns);

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
        cancelSortButton.setDisable(true);
        sortButton.setDisable(true);
        sort = false;
    }

    public void enableSort() {
        cancelSortButton.setDisable(false);
        startCell.setDisable(false);
        endCell.setDisable(false);
        sort = true;
    }

    private void addInputListeners() {
        startCell.textProperty().addListener((observable, oldValue, newValue) -> {
            generateColumnCheckBoxes(newValue, endCell.getText());
        });
        endCell.textProperty().addListener((observable, oldValue, newValue) -> {
            generateColumnCheckBoxes(startCell.getText(), newValue);
        });
    }

    private void generateColumnCheckBoxes(String start, String end) {
        checkboxContainer.getChildren().clear();
        columnCheckBoxes.clear();

        if (start.isEmpty() || end.isEmpty()) {
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
                checkBox.setOnAction(event -> checkIfSortCanBeEnabled()); // Add listener for each checkbox
                columnCheckBoxes.add(checkBox);
                checkboxContainer.getChildren().add(checkBox);
            }
        } catch (Exception e) {
            String errorTitle = "Checkbox Generation Error";
            String errorHeader = "Failed to generate column checkboxes";
            String errorMessage = "An error occurred while generating checkboxes: " + e.getMessage();

            mainController.showErrorDialog(errorTitle, errorHeader, errorMessage);
        }
        checkIfSortCanBeEnabled(); // Initial check
    }

    /**
     * Check if the sort button should be enabled
     * The sort button is enabled only if both startCell and endCell are non-empty
     * and at least one checkbox is selected
     */
    private void checkIfSortCanBeEnabled() {
        boolean canEnable = !startCell.getText().isEmpty() && !endCell.getText().isEmpty();

        // Check if at least one checkbox is selected
        boolean anyColumnSelected = columnCheckBoxes.stream().anyMatch(CheckBox::isSelected);

        // Enable sort button only if both cells are non-empty and at least one checkbox is selected
        sortButton.setDisable(!(canEnable && anyColumnSelected));
    }
}
