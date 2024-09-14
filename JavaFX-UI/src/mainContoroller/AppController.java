package mainContoroller;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import actionLine.ActionLineController;
import commands.CommandsController;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.Logic;
import sheet.SheetController;
import xmlParse.XmlSheetLoader;

import java.util.List;

public class AppController extends Application {

    @FXML private BorderPane headerComponent;
    @FXML private ScrollPane actionLineComponent;
    @FXML private AnchorPane sheetComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandComponent;
    @FXML private CommandsController commandComponentController;
    private SheetController sheetComponentController;

    private Logic logic = new Logic();

    @FXML
    public void initialize() {
        if (headerComponentController != null && actionLineComponentController != null && commandComponentController != null) {
            headerComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            sheetComponentController = new SheetController();
            sheetComponentController.setMainController(this);
            commandComponentController.setMainController(this);
            headerComponentController.setSheetLoadedListener(event -> actionLineComponentController.enableVersionSelector());
        }
    }
    public List<String> getAllCellNames() {
       return sheetComponentController.getAllCellNames();
    }


    public void setSheetByXML() {
        String xmlFilePath = headerComponentController.getXmlFilePath();
        if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
            logic.addSheet(XmlSheetLoader.fromXmlFileToObject(xmlFilePath));
        }
       showSheet(logic.getLatestSheet());
    }

    public void showSheet(SheetDTO sheet) {
        actionLineComponentController.clearUIComponents();
        sheetComponentController.clearGrid();
        sheetComponentController.setSheetDTO(sheet);
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

    public int getSheetVersion()
    {
        return logic.getLatestSheet().getVersion();
    }

    public SheetDTO getLatestSheet() {
        return logic.getLatestSheet();
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

    public CellDTO setCell(String coordinate, String value) {
        logic.setCellValue(coordinate.toString(),value);
        showSheet(logic.getLatestSheet());
        return logic.getLatestSheet().getCell(coordinate.toString());
    }

    public SheetDTO getSheetByVersion(int version) {
        return logic.getSheetByVersion(version);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public HeaderController getHeaderController() {
        return headerComponentController;
    }

    public ActionLineController getActionLineController() {
        return actionLineComponentController;
    }

    public SheetController getSheetComponentController()
    {
        return sheetComponentController;
    }

    public CommandsController getCommandsController() {
        return commandComponentController;
    }
}
