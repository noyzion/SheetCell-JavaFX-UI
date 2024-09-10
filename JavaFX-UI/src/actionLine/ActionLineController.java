package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import header.UpdateValueController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.TextField;
import mainContoroller.AppController;

import java.util.List;

public class ActionLineController {

    @FXML
    private TextField cellIdSelection;
    @FXML
    private TextField originalValueBox;
    @FXML
    private Button updateValue;
    @FXML
    private TextField showLastVersion;
    @FXML
    private Button versionSelector;
    private AppController mainController;

    private CellDTO selectedCell;
    @FXML
    private void initialize() {

    }

    public void clearUIComponents() {
        cellIdSelection.clear();
        originalValueBox.clear();
        showLastVersion.clear();
    }

    private String getCellVersion(CellDTO cell) {
        if (cell == null) {
            return "1";
        } else {
            return String.valueOf(cell.getLastVersionUpdate());
        }
    }

    public void updateFields(CoordinateDTO cord, CellDTO cell) {
        cellIdSelection.setText(cord.toString());
        updateValue.setOnAction(event -> handleUpdateValueAction());
        selectedCell = cell;
        if (cell == null) {
            originalValueBox.setText("empty cell");
            showLastVersion.setText("1");
        } else {
            originalValueBox.setText(cell.getOriginalValue().toString());
            showLastVersion.setText(Integer.toString(cell.getLastVersionUpdate()));
        }
    }

    @FXML
    private void handleUpdateValueAction() {
        openUpdateValueDialog(selectedCell);
    }

    public void openUpdateValueDialog(CellDTO cell) {
        UpdateValueController updateDialog = new UpdateValueController(cell);
        updateDialog.display();

        String inputType = updateDialog.getInputType();
        var selectedOperation = updateDialog.getSelectedOperation();
        List<String> functionArgs = updateDialog.getOperationArguments();

    }

    @FXML
    private void handleVersionSelectorAction() {
        // Your code here
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
