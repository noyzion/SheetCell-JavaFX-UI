package actionLine;

import DTO.CellDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mainContoroller.AppController;

import java.util.ArrayList;
import java.util.List;

public class ActionLineController  {

    @FXML private ComboBox<String> cellIdSelection;
    @FXML private TextField originalValueBox;
    @FXML private Button updateValue;
    @FXML private TextField showLastVersion;
    @FXML private Button versionSelector;
    private AppController mainController;

    @FXML
    private void initialize() {
        cellIdSelection.setOnAction(event -> handleCellIdSelctionAction());

    }
    public void clearUIComponents() {
        cellIdSelection.cancelEdit();
        originalValueBox.clear();
        showLastVersion.clear();
    }
    @FXML
    private void handleCellIdSelctionAction() {
        String selectedCellId = cellIdSelection.getSelectionModel().getSelectedItem();
        CellDTO selectedCell = mainController.getCell(selectedCellId);

        if (selectedCellId != null) {
            String cellOriginalValue = getCellOriginalValue(selectedCell);
            String cellVersion = getCellVersion(selectedCell);
            originalValueBox.setText(cellOriginalValue);
            showLastVersion.setText(cellVersion);

        } else {
            originalValueBox.setText("No cell selected.");
        }


    }

    private String getCellVersion(CellDTO cell) {
        if (cell == null) {
            return "1";
        } else {
            return String.valueOf(cell.getLastVersionUpdate());
        }
    }

    private String getCellOriginalValue(CellDTO cell) {
        if(cell == null)
            return "empty cell";
        else if(cell.getOriginalValue() == null)
            return "empty cell";
        else
            return cell.getOriginalValue();
    }

    public void fillCellsOptions() {
        int rows = mainController.getSheetRowSize();
        int cols = mainController.getSheetColumnSize();
        List<String> cellIds = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                String cellId = getColumnName(col) + row;
                cellIds.add(cellId);
            }
        }
        cellIds.sort(String::compareTo);
        Platform.runLater(() -> {
            cellIdSelection.getItems().setAll(cellIds);
        });
    }

    private String getColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }


    @FXML
    private void handleUpdateValueAction() {
        // Your code here
    }

    @FXML
    private void handleVersionSelectorAction() {
        // Your code here
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}
