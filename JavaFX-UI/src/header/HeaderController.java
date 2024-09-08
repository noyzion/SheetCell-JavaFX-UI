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
        clearUIComponents();
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
                mainController.showErrorDialog("Sheet Error", "An error occurred while setting the sheet.", e.getMessage());
                return;
            }
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

    private void loadFileWithProgress(File file) {
        clearUIComponents();
        progressBar.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(200);
                    updateProgress(i / 100.0, 1.0);
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
            mainController.showErrorDialog("File Load Error", "An error occurred while loading the file.", e.getSource().getException().getMessage());
        });

        new Thread(task).start();
    }



    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
