package actionLine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionLineController  {

    @FXML private ComboBox<String> cellIdSelection;
    @FXML private TextField newOriginalValueBox;
    @FXML private Button updateValue;
    @FXML private TextField showLastVersion;
    @FXML private Button versionSelector;
    private AppController mainController;

    @FXML
    private void initialize() {
    }

    @FXML
    public void handleCellIdSelctionAction() {
        // Retrieve the number of rows and columns from the main controller
        int rows = mainController.getSheetRowSize();
        int cols = mainController.getSheetColumnSize();

        // Create a list to hold cell IDs temporarily
        List<String> cellIds = new ArrayList<>();

        // Generate cell IDs for all rows and columns
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                String cellId = getColumnName(col) + row;
                cellIds.add(cellId);
            }
        }

        // Sort the list of cell IDs
        cellIds.sort(String::compareTo);

        // Ensure the ComboBox is updated on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Clear existing items and add sorted cell IDs to the ComboBox
            cellIdSelection.getItems().setAll(cellIds);
        });
    }

    // Helper method to convert column number to column name (e.g., 1 -> "A", 2 -> "B")
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
    private void handleNewOriginalValueAction() {
        // Your code here
    }

    @FXML
    private void handleUpdateValueAction() {
        // Your code here
    }

    @FXML
    private void handleShowLastVersion() {
        // Your code here
    }

    @FXML
    private void handleVersionSelectorAction() {
        // Your code here
    }
    // Method to allow AppController to inject itself
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}
