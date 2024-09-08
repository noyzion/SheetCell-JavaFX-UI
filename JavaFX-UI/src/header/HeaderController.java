package header;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.io.File;

public class HeaderController {

    @FXML private TextField currentFile;
    @FXML private TextField fileName;
    @FXML private Button loadFileButton;
    @FXML private ProgressBar progressBar;

    private AppController mainController;
    private String xmlFilePath;
    private String previousFilePath;
    private String previousFileName;

    @FXML
    private void initialize() {
        loadFileButton.setOnAction(event -> handleLoadFileButtonAction());
        progressBar.setVisible(false);
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
            try {
                loadFileWithProgress(selectedFile);
                mainController.setSheet();
                currentFile.setText(xmlFilePath);
                fileName.setText(selectedFile.getName());
            } catch (Exception e) {
                currentFile.setText(previousFilePath);
                fileName.setText(previousFileName);
                showErrorDialog("Sheet Error", "An error occurred while setting the sheet.", e.getMessage());
                return;
            }
        } else {
            currentFile.setText(previousFilePath);
            fileName.setText(previousFileName);
            showErrorDialog("File Selection Error", "No file was selected.", "Please select a valid XML file.");
        }
    }

    private void clearUIComponents() {
        currentFile.clear();
        fileName.clear();
        progressBar.setProgress(0); // Reset progress to 0
        progressBar.setVisible(false); // Hide progress bar
    }

    private void loadFileWithProgress(File file) {
        clearUIComponents();
        progressBar.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(200); // Simulate loading time
                    updateProgress(i / 100.0, 1.0); // Update progress between 0.0 and 1.0
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
        });

        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
            currentFile.setText(previousFilePath);
            fileName.setText(previousFileName);
            showErrorDialog("File Load Error", "An error occurred while loading the file.", e.getSource().getException().getMessage());
        });

        new Thread(task).start();
    }

    private void showErrorDialog(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
