package mainContoroller;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.Logic;
import sheet.SheetController;
import xmlParse.XmlSheetLoader;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppController extends Application {

    @FXML private BorderPane headerComponent;
    @FXML private ScrollPane actionLineComponent;
    @FXML private AnchorPane sheetComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private ActionLineController actionLineComponentController;
    private SheetController sheetComponentController;

    private Logic logic = new Logic();

    @FXML
    public void initialize() {
        if (headerComponentController != null && actionLineComponentController != null) {
            headerComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            sheetComponentController = new SheetController();
            sheetComponentController.setMainController(this);
        }
    }
    public List<String> getAllCellNames() {
       return sheetComponentController.getAllCellNames();
    }
    public void setSheet() {

        String xmlFilePath = headerComponentController.getXmlFilePath();
        if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
            logic.addSheet(XmlSheetLoader.fromXmlFileToObject(xmlFilePath));
        }
        actionLineComponentController.clearUIComponents();
        sheetComponentController.clearGrid();
        SheetDTO latestSheet = logic.getLatestSheet();
        sheetComponentController.setSheetDTO(latestSheet);
        sheetComponentController.createGridFromSheetDTO();
        sheetComponent.getChildren().clear();
        sheetComponent.getChildren().add(sheetComponentController.getGridPane());

        AnchorPane.setTopAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setBottomAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setLeftAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setRightAnchor(sheetComponentController.getGridPane(), 0.0);


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

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("app.fxml"));
            Parent root = fxmlLoader.load();
            AppController controller = fxmlLoader.getController();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sheet Cell Application");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Application Error", "Failed to Load", "An error occurred while loading the application.");
        }
    }

    public void updateActionLineFields(CoordinateDTO coordinate) {
        CellDTO cell = logic.getLatestSheet().getCell(coordinate.toString());
            actionLineComponentController.updateFields(coordinate,cell);
    }

    public CellDTO setCell(CoordinateDTO coordinate, String value) {
        logic.setCellValue(coordinate.toString(),value);
        actionLineComponentController.clearUIComponents();
        sheetComponentController.clearGrid();
        SheetDTO latestSheet = logic.getLatestSheet();
        sheetComponentController.setSheetDTO(latestSheet);
        sheetComponentController.createGridFromSheetDTO();
        sheetComponent.getChildren().clear();
        sheetComponent.getChildren().add(sheetComponentController.getGridPane());

        AnchorPane.setTopAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setBottomAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setLeftAnchor(sheetComponentController.getGridPane(), 0.0);
        AnchorPane.setRightAnchor(sheetComponentController.getGridPane(), 0.0);
        return logic.getLatestSheet().getCell(coordinate.toString());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
