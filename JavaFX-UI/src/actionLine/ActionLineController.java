package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
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

    @FXML
    private void initialize() {
        updateValue.setOnAction(event -> handleUpdateValueAction());
    }

    public void clearUIComponents() {
        cellIdSelection.cancelEdit();
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

        if (cell == null) {
            originalValueBox.setText("empty cell");
            showLastVersion.setText("1");
        } else if (cell.getOriginalValue() == null) {
            originalValueBox.setText("empty cell");
            showLastVersion.setText(Integer.toString(cell.getLastVersionUpdate()));
        } else {
            originalValueBox.setText(cell.getOriginalValue().toString());
            showLastVersion.setText(Integer.toString(cell.getLastVersionUpdate()));
        }
    }

    @FXML
    private void handleUpdateValueAction() {
        openUpdateValueDialog();
    }

    public void openUpdateValueDialog() {
        // Fetch previous values from current fields
        String originalValue = originalValueBox.getText();
        String effectiveValue = showLastVersion.getText(); // Assuming this represents the effective value

        // Instantiate the dialog with previous values
        UpdateValueController updateDialog = new UpdateValueController(originalValue, effectiveValue);
        updateDialog.display();

        // Get the user inputs after the dialog is closed
        String inputType = updateDialog.getInputType();
        String selectedValueOrFunction = updateDialog.getSelectedFunction();
        List<String> functionArgs = updateDialog.getFunctionArguments();

        // Process the retrieved inputs
        processUserInputs(inputType, selectedValueOrFunction, functionArgs);
    }

    private void processUserInputs(String inputType, String selectedValueOrFunction, List<String> functionArgs) {
        // Implement your logic based on the input type and user choices
        System.out.println("Input Type: " + inputType);
        System.out.println("Selected Value/Function: " + selectedValueOrFunction);
        if ("Function".equals(inputType)) {
            System.out.println("Function Arguments: " + functionArgs);
            // Further processing with the function arguments
        } else {
            // Handle number or string input types
        }
    }

    @FXML
    private void handleVersionSelectorAction() {
        // Implement version selection logic
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}
