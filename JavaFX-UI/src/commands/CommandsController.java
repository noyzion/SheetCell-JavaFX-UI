package commands;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import mainContoroller.AppController;
import sheet.SheetController;

public class CommandsController {

    private AppController mainController;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    private void initialize() {
        themeComboBox.getItems().addAll("Basic", "Pink", "Blue", "Green");
        themeComboBox.setValue("Basic");
        String currentTheme = themeComboBox.getValue();

        applyTheme(currentTheme);


        themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyTheme(newValue);
            }
        });
    }


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void applyTheme(String themeName) {
       // SheetController sheetController = mainController.getSheetComponentController();
      //  sheetController.setSheetStyle(themeName);
    }
}