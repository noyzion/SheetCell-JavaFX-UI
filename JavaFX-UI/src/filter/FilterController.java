package filter;

import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterController {

    private AppController mainController;

    @FXML private Button filterButton;
    @FXML private Button cancelFilterButton;
    @FXML private ComboBox<String> chooseColumn;
    @FXML private ComboBox<String> startCell;
    @FXML private ComboBox<String> endCell;
    @FXML private ListView<CheckBox> valueListView;
    private boolean filter;

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
    }

    @FXML
    private void handleFilterButtonAction() {
        filter = true;
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
                endCell.getItems().add(cell);
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

    private void populateValuesList() {

    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
            validateInputs();
        });

        endCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateInputs();
        });

        chooseColumn.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateInputs();
        });

//        valueListView.getItems().forEach(checkBox ->
//                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//                    validateInputs();
//                })
//        );
    }

    private void validateInputs() {
        boolean isColumnSelected = chooseColumn.getSelectionModel().getSelectedItem() != null;
        boolean isStartCellSelected = startCell.getSelectionModel().getSelectedItem() != null;
        boolean isEndCellSelected = endCell.getSelectionModel().getSelectedItem() != null;
     //   boolean isValueSelected = valueListView.getItems().stream().anyMatch(CheckBox::isSelected);
//&& isValueSelected)
        filterButton.setDisable(!(isColumnSelected && isStartCellSelected && isEndCellSelected ));
    }

}
