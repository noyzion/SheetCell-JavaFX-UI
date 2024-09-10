package header;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainContoroller.AppController;

import java.io.File;

public class HeaderController {

    @FXML private TextField currentFile;
    @FXML private TextField fileName;
    @FXML private Button loadFileButton;

    private AppController mainController;
    private String xmlFilePath;
    private String previousFilePath;
    private String previousFileName;

    @FXML
    private void initialize() {
        clearUIComponents();
        loadFileButton.setOnAction(event -> handleLoadFileButtonAction());
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
        } else {
            currentFile.setText(previousFilePath);
            fileName.setText(previousFileName);
            mainController.showErrorDialog("File Selection Error", "No file was selected.", "Please select a valid XML file.");
        }
    }

    public void clearUIComponents() {
        currentFile.clear();
        fileName.clear();
    }

    private void loadFileWithProgressPopup(File file) {
        // Create a new Stage for the progress popup
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
        progressStage.show();

        // Create a task for loading the file
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    for (int i = 0; i <= 100; i += 10) {
                        Thread.sleep(200); // Simulate a time-consuming task
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

        // Bind progress bar to task's progress
        progressBar.progressProperty().bind(task.progressProperty());

        // Set up actions on task completion
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                progressBar.progressProperty().unbind();
                progressStage.close(); // Close the progress popup
                currentFile.setText(xmlFilePath);
                fileName.setText(file.getName());
                mainController.setSheet(); // Update sheet data after loading
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                progressBar.progressProperty().unbind();
                progressStage.close(); // Close the progress popup
                currentFile.setText(previousFilePath);
                fileName.setText(previousFileName);
                mainController.showErrorDialog("File Load Error", "An error occurred while loading the file.", e.getSource().getException().getMessage());
            });
        });

        // Start the task in a new thread
        new Thread(task).start();
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
