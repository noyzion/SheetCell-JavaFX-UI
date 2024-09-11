package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import expression.FunctionArgument;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
        // Initialize the "Update Value" button as disabled
        updateValue.setDisable(true);
    }

    public void clearUIComponents() {
        cellIdSelection.clear();
        originalValueBox.clear();
        showLastVersion.clear();
        // Disable the "Update Value" button
        updateValue.setDisable(true);
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
        selectedCell = cell;
        updateValue.setDisable(false);
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
        if (selectedCell != null) {
            openUpdateValueDialog(selectedCell);
        }
    }

    public void openUpdateValueDialog(CellDTO cell) {
        UpdateValueController updateDialog = new UpdateValueController(cell);
        updateDialog.display();

        String inputType = updateDialog.getInputType();
        var selectedOperation = updateDialog.getSelectedOperation();
        List<FunctionArgument> functionArgs = updateDialog.getOperationArguments();
      //  mainController.setCell(cell.getCoordinateDTO(),updateDialog.getGeneratedString());
    }

    @FXML
    private void handleVersionSelectorAction() {
        // Your code here
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
