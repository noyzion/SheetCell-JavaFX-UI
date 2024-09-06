package Header;

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

import java.io.File;
import java.io.IOException;

public class HeaderController extends Application {

    @FXML private TextField currentFile;
    @FXML private Button loadFileButton;
    @FXML private ProgressBar progressBar;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Header.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Header");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            currentFile.setText(selectedFile.getAbsolutePath());
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            loadFileWithProgress(selectedFile);
        }
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


    public static void main(String[] args) {
        launch(args);
    }
}
