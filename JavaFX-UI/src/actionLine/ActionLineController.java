package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.io.IOException;
import java.util.ArrayList;
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
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("showValues.fxml"));
        Parent root = loader.load();

        showValuesController showValuesController = loader.getController();
        Stage stage = new Stage();
        stage.setTitle("Show Values");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    } catch (IOException e) {
        mainController.showErrorDialog("Error", "Failed to open the Show Values window.", e.getMessage());
    }
}

    @FXML
    private void handleVersionSelectorAction() {
        // Your code here
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}
