package commands;

import DTO.CoordinateDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import mainContoroller.AppController;
import sheet.SheetController;
import javafx.scene.paint.Color;

public class CommandsController {

    private AppController mainController;

    @FXML private ComboBox<String> themeComboBox;
    @FXML private Label selectedCellLabel;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private Button applyStylesButton;
    @FXML private Button resetStylesButton;
    private CoordinateDTO selectedCellCoordinate;

    @FXML
    private void initialize() {
        themeComboBox.getItems().addAll("Basic", "Pink", "Blue", "Green");
        themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyTheme(newValue);
            }
        });
        backgroundColorPicker.setValue(Color.WHITE);
        textColorPicker.setValue(Color.BLACK);
        setEditCellDisable(true);
    }

    public void setEditCellDisable(boolean disable) {
        backgroundColorPicker.setDisable(disable);
        textColorPicker.setDisable(disable);
        applyStylesButton.setDisable(disable);
        resetStylesButton.setDisable(disable);
    }

    public void updateCellCoordinate(CoordinateDTO coordinate) {
        selectedCellLabel.setText(coordinate.toString());
        this.selectedCellCoordinate = coordinate;
    }

    @FXML
    private void handleApplyStylesButtonHandle() {
        if (selectedCellCoordinate != null) {
            SheetController sheetController = mainController.getSheetComponentController();
            Color backgroundColor = backgroundColorPicker.getValue();
            Color textColor = textColorPicker.getValue();
            sheetController.updateCellStyle(selectedCellCoordinate, backgroundColor, textColor);
            backgroundColorPicker.setValue(Color.WHITE);
            textColorPicker.setValue(Color.BLACK);
        }
    }

    @FXML
    private void handleResetStylesButtonHandle() {
        if (selectedCellCoordinate != null) {
            SheetController sheetController = mainController.getSheetComponentController();
            sheetController.resetCellStyle(selectedCellCoordinate);
            backgroundColorPicker.setValue(Color.WHITE);
            textColorPicker.setValue(Color.BLACK);
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void applyTheme(String themeName) {
        SheetController sheetController = mainController.getSheetComponentController();
        sheetController.setSheetStyle(themeName);
    }

}
