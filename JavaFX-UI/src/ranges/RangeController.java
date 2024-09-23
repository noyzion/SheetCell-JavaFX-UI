package ranges;

import DTO.CoordinateDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import sheet.range.Range;
import sheet.range.RangeImpl;

import java.lang.runtime.TemplateRuntime;
import java.util.ArrayList;
import java.util.List;

public class RangeController {

    @FXML
    ComboBox<String> startCell;
    @FXML
    ComboBox<String> endCell;
    @FXML
    TextField rangeName;
    @FXML
    Button addRangeButton;
    @FXML
    TableView rangesTable;
    @FXML
    private TableColumn<Range, String> nameColumn;
    @FXML
    private TableColumn<Range, String> startColumn;
    @FXML
    private TableColumn<Range, String> endColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        startColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart()));
        endColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd()));
        startCell.setDisable(true);
        endCell.setDisable(true);
        disableRange();
        addInputListeners();

    }

    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleAddRangeAction() {
        String start = startCell.getValue();
        String end = endCell.getValue();
        String name = rangeName.getText();

        if (start == null || end == null || name.isEmpty()) {
            mainController.showErrorDialog("Please fill in all fields.", "", "Please ensure all inputs are provided.");
            return;
        }

        try {
            String range = end + ".." + start;
            mainController.addRangeForSheet(name, range);
            Range newRange = new RangeImpl(name, range);
            rangesTable.getItems().add(newRange);
        } catch (Exception e) {
            mainController.showErrorDialog("An error occurred while adding the range.", e.getMessage(), "Please try again or check your input.");
        }
    }


    public void clearUIComponents() {
        startCell.getItems().clear();
        endCell.getItems().clear();
        startCell.getSelectionModel().clearSelection();
        endCell.getSelectionModel().clearSelection();
        rangeName.clear();
    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
        });
        endCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkIfAddRangeCanBeEnabled();
        });
        rangeName.textProperty().addListener((observable, oldValue, newValue) -> {
            checkIfAddRangeCanBeEnabled();
        });
    }
    private void checkIfAddRangeCanBeEnabled() {
        boolean canEnable = !startCell.getValue().isEmpty() && !endCell.getValue().isEmpty() && !rangeName.getText().isEmpty();
        addRangeButton.setDisable(!(canEnable));
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

    public void disableRange() {
        addRangeButton.setDisable(true);
        endCell.setDisable(true);
        rangeName.setDisable(true);
        startCell.setDisable(true);

    }

    public void enableRange() {
        populateComboBoxWithCells();
        startCell.setDisable(false);
        endCell.setDisable(false);
        rangeName.setDisable(false);
    }
}
