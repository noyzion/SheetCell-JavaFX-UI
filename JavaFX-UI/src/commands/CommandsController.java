package commands;

import DTO.CoordinateDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import mainContoroller.AppController;
import sheet.SheetController;

public class CommandsController {

    private AppController mainController;

    @FXML private ComboBox<String> themeComboBox;
    @FXML private Label selectedCellLabel;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private Button applyStylesButton;
    @FXML private Button resetStylesButton;

    @FXML
    private void initialize() {
        themeComboBox.getItems().addAll("Basic", "Pink", "Blue", "Green");
        String currentTheme = themeComboBox.getValue();

        themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyTheme(newValue);
            }
        });
       setEditCellDisable(true);

    }

    public void setEditCellDisable(boolean disable)
    {
        backgroundColorPicker.setDisable(disable);
        textColorPicker.setDisable(disable);
        applyStylesButton.setDisable(disable);
        resetStylesButton.setDisable(disable);
    }
    public void updateCellCoordinate(CoordinateDTO coordinate) {
        selectedCellLabel.setText(coordinate.toString());
    }
    @FXML
    private void handleApplyStylesButtonHandle() {

    }

    @FXML
    private void handleResetStylesButtonHandle() {

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void applyTheme(String themeName) {
        SheetController sheetController = mainController.getSheetComponentController();
        sheetController.setSheetStyle(themeName);
    }
}