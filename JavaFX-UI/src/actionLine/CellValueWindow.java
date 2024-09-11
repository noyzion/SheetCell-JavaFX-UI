package actionLine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CellValueWindow {

    private Stage stage;
    private Label cellIdLabel;
    private Label valueLabel;

    public CellValueWindow() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Updated Cell Value");

        cellIdLabel = new Label("Cell ID: ");
        valueLabel = new Label("New cell value: ");

        cellIdLabel.getStyleClass().add("cell-id-label");
        valueLabel.getStyleClass().add("cell-value-label");

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> close());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(cellIdLabel, valueLabel, closeButton);

        Scene scene = new Scene(layout, 300, 150);
        scene.getStylesheets().add(getClass().getResource("/actionLine/style/CellValueWindowStyle.css").toExternalForm()); // Load CSS file
        stage.setScene(scene);
    }

    public void show(String value, String cellId) {
        cellIdLabel.setText("Cell ID: " + cellId);
        valueLabel.setText("New cell value: " + value);
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
