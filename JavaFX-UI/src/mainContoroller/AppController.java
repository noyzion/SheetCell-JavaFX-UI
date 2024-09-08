package mainContoroller;

import DTO.CellDTO;
import actionLine.ActionLineController;
import header.HeaderController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.Logic;
import xmlParse.XmlSheetLoader;

public class AppController extends Application {

    @FXML private BorderPane headerComponent;
    @FXML private ScrollPane actionLineComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private ActionLineController actionLineComponentController;

    private Logic logic = new Logic();

    @FXML
    public void initialize() {
        if (headerComponentController != null && actionLineComponentController != null) {
            headerComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
        }
    }



    public void setSheet() {
       headerComponentController.clearUIComponents();
        actionLineComponentController.clearUIComponents();
        String xmlFilePath = headerComponentController.getXmlFilePath();
        if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
            logic.addSheet(XmlSheetLoader.fromXmlFileToObject(xmlFilePath));
            actionLineComponentController.fillCellsOptions();

        }
    }
    public void showErrorDialog(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    public CellDTO getCell(String coordinate) {
        return logic.getLatestSheet().getCell(coordinate);
    }

    public int getSheetRowSize() {
        return logic.getLatestSheet().getRowSize();
    }

    public int getSheetColumnSize() {
        return logic.getLatestSheet().getColumnSize();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sheet Cell Application");
        primaryStage.show();
    }


    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }

}