package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import expression.FunctionArgument;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.util.List;

public class ActionLineController {

    @FXML private TextField cellIdSelection;
    @FXML private TextField originalValueBox;
    @FXML private Button updateValue;
    @FXML private TextField showLastVersion;
    @FXML private Button versionSelector;
    private AppController mainController;

    private CellDTO selectedCell;

    @FXML
    private void initialize() {
        updateValue.setDisable(true);
        versionSelector.setDisable(true);
    }

    public void clearUIComponents() {
        cellIdSelection.clear();
        originalValueBox.clear();
        showLastVersion.clear();
        updateValue.setDisable(true);
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
        openUpdateValueDialog(selectedCell);
    }

    public void openUpdateValueDialog(CellDTO cell) {
        boolean validInput = false;
        CellValueWindow cellValueWindow = new CellValueWindow();
        while (!validInput) {
            try {
                UpdateValueController updateDialog = new UpdateValueController(cell, mainController.getAllCellNames());
                updateDialog.display();

                if (updateDialog.isConfirmed()) {
                    String inputType = updateDialog.getInputType();
                    var selectedOperation = updateDialog.getSelectedOperation();
                    List<FunctionArgument> functionArgs = updateDialog.getOperationArguments();
                    String updatedValue = updateDialog.getGeneratedString();
                    cell = mainController.setCell(cellIdSelection.getText(), updatedValue);
                    if (cell.getEffectiveValue().getValue() == null)
                        cellValueWindow.show("empty cell", cell.getCoordinateDTO().toString());
                    else
                        cellValueWindow.show(cell.getEffectiveValue().getValue().toString(), cell.getCoordinateDTO().toString());

                    validInput = true;
                } else {
                    break;
                }
            } catch (Exception e) {
                mainController.showErrorDialog("Error", "Failed to update cell", e.getMessage());
            }
        }
    }

    @FXML
    private void handleVersionSelectorAction() {
        openVersionSelectorDialog();
    }

    public void openVersionSelectorDialog() {
        VersionSelectorController cellValueWindow = new VersionSelectorController(mainController.getSheetVersion());
        cellValueWindow.setMainController(mainController);
        cellValueWindow.display();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;

        // Setting the listener in the HeaderController to enable the versionSelector button when the sheet is loaded
        mainController.getHeaderController().setSheetLoadedListener(event -> versionSelector.setDisable(false));
    }
    public void enableVersionSelector() {
        versionSelector.setDisable(false);
    }

}
