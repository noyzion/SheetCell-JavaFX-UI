package ranges;

import DTO.CoordinateDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import sheet.range.Range;
import sheet.range.RangeFactory;
import sheet.range.RangeImpl;

import java.util.ArrayList;
import java.util.List;

public class RangeController {

    @FXML private ComboBox<String> startCell;
    @FXML private ComboBox<String> endCell;
    @FXML private TextField rangeName;
    @FXML private Button addRangeButton;
    @FXML private Button deleteRangeButton;
    @FXML private TableView<Range> rangesTable;
    @FXML private TableColumn<Range, String> nameColumn;
    @FXML private TableColumn<Range, String> startColumn;
    @FXML private TableColumn<Range, String> endColumn;

    private AppController mainController;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        startColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart()));
        endColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd()));

        setupTableCellClickListener();
        disableRange();
        addInputListeners();
    }

    private void setupTableCellClickListener() {
        nameColumn.setCellFactory(col -> {
            TableCell<Range, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    Range selectedRange = cell.getTableRow().getItem();
                    handleRangeClick(selectedRange);
                }
            });

            return cell;
        });
    }

    private void handleRangeClick(Range selectedRange) {
        String startCellValue = selectedRange.getStart();
        String endCellValue = selectedRange.getEnd();
        CoordinateDTO startCoord = CoordinateParser.parseDTO(startCellValue);
        CoordinateDTO endCoord = CoordinateParser.parseDTO(endCellValue);

        mainController.getSheetComponentController().clearHighlightedCells();
        mainController.getSheetComponentController().highlightRange(startCoord, endCoord);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleAddRangeAction() {
        String start = startCell.getValue();
        String end = endCell.getValue();
        String name = rangeName.getText();

        if (start == null || end == null || name.isEmpty()) {
            mainController.showErrorDialog("Please fill in all fields.", "", "Please ensure all inputs are provided.");
            return;
        }

        try {
            String range = start + ".." + end;
            mainController.addRangeForSheet(name, range);
            rangesTable.getItems().add(RangeFactory.getRange(name)); // Ensure this gets the correct range
            clearUIComponents();
        } catch (Exception e) {
            mainController.showErrorDialog("An error occurred while adding the range.", e.getMessage(), "Please try again or check your input.");
        }
    }

    @FXML
    private void handleDeleteRangeAction() {
        Range selectedRange = rangesTable.getSelectionModel().getSelectedItem();
        if (selectedRange == null) {
            mainController.showErrorDialog("No Range Selected", "", "Please select a range to delete.");
            return;
        }

        try {
            mainController.deleteRangeForSheet(selectedRange.getName());
            rangesTable.getItems().remove(selectedRange);
        } catch (Exception e) {
            mainController.showErrorDialog("Failed to delete the range.", e.getMessage(), "Please try again.");
        }
    }

    public void clearUIComponents() {
        startCell.getSelectionModel().clearSelection();
        endCell.getSelectionModel().clearSelection();
        rangeName.clear();
    }

    private void addInputListeners() {
        startCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateEndCell(newValue);
            checkIfAddRangeCanBeEnabled(); // Ensure this is checked after updating the end cell
        });

        endCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkIfAddRangeCanBeEnabled();
        });

        rangeName.textProperty().addListener((observable, oldValue, newValue) -> {
            checkIfAddRangeCanBeEnabled();
        });
    }

    private void checkIfAddRangeCanBeEnabled() {
        boolean canEnable = startCell.getValue() != null && endCell.getValue() != null && !rangeName.getText().isEmpty();
        addRangeButton.setDisable(!canEnable);
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

    private void populateComboBoxWithCells() {
        startCell.getItems().clear();
        endCell.getItems().clear();

        for (char column = 0; column < mainController.getLatestSheet().getColumnSize(); column++) {
            for (int row = 1; row <= mainController.getLatestSheet().getRowSize(); row++) {
                String cell = CoordinateFactory.convertIndexToColumnLetter(column) + row;
                startCell.getItems().add(cell);
                endCell.getItems().add(cell);
            }
        }
    }
}
