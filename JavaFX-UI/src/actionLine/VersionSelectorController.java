package actionLine;

import DTO.SheetDTO;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainContoroller.AppController;
import sheet.SheetController;

import java.util.Optional;
import java.util.stream.IntStream;

public class VersionSelectorController {

    private final int sheetVersions;
    private AppController mainController;

    public VersionSelectorController(int sheetVersions) {
        this.sheetVersions = sheetVersions;
    }

    public void display() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Select Sheet Version");

        VBox layout = createLayout();

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLayout() {
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        Label instructionLabel = new Label("Select a version to view:");
        ListView<Integer> versionListView = createVersionListView();

        layout.getChildren().addAll(instructionLabel, versionListView);

        return layout;
    }

    private ListView<Integer> createVersionListView() {
        ListView<Integer> versionListView = new ListView<>();

        // Populate the ListView with version numbers
        IntStream.rangeClosed(1, sheetVersions).forEach(versionListView.getItems()::add);

        versionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showSheetVersion(newValue);
            }
        });

        return versionListView;
    }

    private void showSheetVersion(int versionNumber) {
        Optional.ofNullable(mainController.getSheetByVersion(versionNumber))
                .ifPresentOrElse(selectedSheet -> {
                    SheetController sheetController = new SheetController(selectedSheet);
                    sheetController.createGridFromSheetDTO();
                    AnchorPane anchorPane = createAnchorPaneWithGrid(sheetController);

                    // Make the grid read-only
                    sheetController.setReadOnly(true);

                    // Create and show the pop-up window
                    Stage popupStage = new Stage();
                    popupStage.setTitle("Sheet Version: " + versionNumber);

                    Scene scene = new Scene(anchorPane, 600, 400);
                    popupStage.setScene(scene);
                    popupStage.show();
                }, () -> {
                    // Handle case where sheet is not found (optional)
                    System.out.println("Sheet version " + versionNumber + " not found.");
                });
    }

    private AnchorPane createAnchorPaneWithGrid(SheetController sheetController) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(sheetController.getGridPane());

        // Set anchors for the GridPane within the AnchorPane
        AnchorPane.setTopAnchor(sheetController.getGridPane(), 0.0);
        AnchorPane.setBottomAnchor(sheetController.getGridPane(), 0.0);
        AnchorPane.setLeftAnchor(sheetController.getGridPane(), 0.0);
        AnchorPane.setRightAnchor(sheetController.getGridPane(), 0.0);

        return anchorPane;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
