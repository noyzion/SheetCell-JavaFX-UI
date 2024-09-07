package header;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainContoroller.AppController;

import java.io.File;
import java.io.IOException;

public class HeaderController {

    @FXML
    private TextField currentFile;
    @FXML
    private Button loadFileButton;
    @FXML
    private ProgressBar progressBar;
    private AppController mainController;
    private String xmlFilePath;

    @FXML
    private void initialize() {
        loadFileButton.setOnAction(event -> handleLoadFileButtonAction());
    }

    @FXML
    private void handleLoadFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        Stage stage = (Stage) loadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            xmlFilePath = selectedFile.getAbsolutePath();
            currentFile.setText(xmlFilePath);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            loadFileWithProgress(selectedFile);
        }
        mainController.setSheet();
    }

    private void loadFileWithProgress(File file) {
        progressBar.setVisible(true);
        progressBar.progressProperty().unbind();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(200);
                    updateProgress(i, 100);
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            currentFile.setText(file.getAbsolutePath());
        });
        task.setOnFailed(e -> {
            progressBar.setVisible(false);
            e.getSource().getException().printStackTrace();
        });
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    // Method to allow AppController to inject itself
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }


}