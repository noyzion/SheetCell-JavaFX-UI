package actionLine;

import DTO.SheetDTO;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainController.AppController;
import sheet.SheetController;

import java.util.stream.IntStream;

public class VersionSelectorController {

    private final int sheetVersions;
    private AppController mainController;
    private ActionLineController actionLineController;

    public VersionSelectorController(int sheetVersions, ActionLineController actionLineController) {
        this.sheetVersions = sheetVersions;
        this.actionLineController = actionLineController;
    }

    private void showSheetVersion(int versionNumber, Stage stage) {
        SheetDTO selectedSheet = mainController.getSheetByVersion(versionNumber);
        mainController.showSheet(selectedSheet,true);
        actionLineController.enableLastVersionButton();

        stage.close();
    }

    // Set the mainController
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void display() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Select Sheet Version");

        VBox layout = createLayout(primaryStage);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLayout(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        Label instructionLabel = new Label("Select a version to view:");
        ListView<Integer> versionListView = createVersionListView(stage);

        layout.getChildren().addAll(instructionLabel, versionListView);

        return layout;
    }

    private ListView<Integer> createVersionListView(Stage stage) {
        ListView<Integer> versionListView = new ListView<>();

        IntStream.rangeClosed(1, sheetVersions).forEach(versionListView.getItems()::add);

        versionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showSheetVersion(newValue, stage);
            }
        });

        return versionListView;
    }

}
