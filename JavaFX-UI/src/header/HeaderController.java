package header;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainController.AppController;

import java.io.File;
import java.util.function.Consumer;

public class HeaderController {

    @FXML
    private TextField currentFile;
    @FXML
    private TextField fileName;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button cancelAnimation;
    @FXML
    private Button applyAnimation;
    private Consumer<Void> sheetLoadedListener;
    private AppController mainController;
    private String xmlFilePath;
    private String previousFilePath;
    private String previousFileName;

    @FXML
    private void initialize() {
        clearUIComponents();
        loadFileButton.setOnAction(event -> handleLoadFileButtonAction());
        cancelAnimation.setOnAction(event -> handleCancelAnimationAction());
    }

    public void setSheetLoadedListener(Consumer<Void> listener) {
        this.sheetLoadedListener = listener;
    }

    @FXML
    private void handleLoadFileButtonAction() {
        previousFilePath = currentFile.getText();
        previousFileName = fileName.getText();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        Stage stage = (Stage) loadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            xmlFilePath = selectedFile.getAbsolutePath();
            loadFileWithProgressPopup(selectedFile);
        }
    }

    public void clearUIComponents() {
        currentFile.clear();
        fileName.clear();
    }

    @FXML
    private void loadFileWithProgressPopup(File file) {
        Stage progressStage = new Stage();
        progressStage.initStyle(StageStyle.UTILITY);
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setTitle("Loading...");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        VBox vbox = new VBox(progressBar);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox);
        progressStage.setScene(scene);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    for (int i = 0; i <= 100; i += 10) {
                        Thread.sleep(200);
                        updateProgress(i, 100);
                    }
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        return null;
                    }
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        progressStage.setOnCloseRequest(event -> {
            if (task.isRunning()) {
                task.cancel();
            }
        });

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            progressBar.progressProperty().unbind();
            progressStage.close();

            try {
                mainController.setSheetByXML();
                currentFile.setText(xmlFilePath);
                fileName.setText(file.getName());
                if (sheetLoadedListener != null) {
                    sheetLoadedListener.accept(null);
                }
            } catch (Exception error) {
                currentFile.setText(previousFilePath);
                fileName.setText(previousFileName);
                mainController.showErrorDialog("File Load Error", "An error occurred while loading the file.", error.getMessage());
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            progressBar.progressProperty().unbind();
            progressStage.close();
            currentFile.setText(previousFilePath);
            fileName.setText(previousFileName);
            mainController.showErrorDialog("File Load Error", "An error occurred while loading the file.", e.getSource().getException().getMessage());
        }));

        progressStage.show();
        new Thread(task).start();
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleCancelAnimationAction() {
        mainController.getActionLineController().cancelCellAnimation();
    }

    @FXML
    public void handleApplyAnimationAction() {
        mainController.getActionLineController().applyCellAnimation();
    }
}
