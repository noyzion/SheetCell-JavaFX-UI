package actionLine;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import expression.FunctionArgument;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mainController.AppController;

import java.util.List;

public class ActionLineController {

    @FXML private TextField cellIdSelection;
    @FXML private TextField originalValueBox;
    @FXML private Button updateValue;
    @FXML private TextField showLastVersion;
    @FXML private Button versionSelector;
    @FXML private Button lastVersionButton;
    private AppController mainController;
    private boolean versionSelected = false;
    private BooleanProperty cancelAnimation = new SimpleBooleanProperty(false);

    private CellDTO selectedCell;

    @FXML
    private void initialize() {
        updateValue.setDisable(true);
        versionSelector.setDisable(true);
        lastVersionButton.setDisable(true);
        originalValueBox.setEditable(false);
    }

    public void clearUIComponents() {
        cellIdSelection.clear();
        originalValueBox.clear();
        showLastVersion.clear();
        updateValue.setDisable(true);
        originalValueBox.setEditable(false);
    }

    @FXML
    private void handleUpdateValueTextFieldAction() {
        String newValue = originalValueBox.getText();
        try {
            CellDTO cell = mainController.setCell(cellIdSelection.getText(), newValue);
            Node cellLabel = mainController.getSheetComponentController().getCellNode(cell.getCoordinateDTO());
            if (cellLabel != null) {
                applyCellUpdateAnimation(cellLabel);
            }

        } catch (Exception e) {
            mainController.showErrorDialog("Error", "Failed to update cell", e.getMessage());
        }

    }

    private void applyCellUpdateAnimation(Node node) {
        if (cancelAnimation.get()) {
            return; // Exit if the animation is canceled
        }
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.LIGHTBLUE);
        shadow.setRadius(20);
        node.setEffect(shadow);

        scaleTransition.setOnFinished(event -> node.setEffect(null));
        if (node instanceof Label) {
            Label cellLabel = (Label) node;

            String originalStyle = cellLabel.getStyle();
            Timeline colorAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(cellLabel.styleProperty(), "-fx-background-color: lightyellow;")),  // Initial background
                    new KeyFrame(Duration.millis(200), new KeyValue(cellLabel.styleProperty(), "-fx-background-color: lightpink;")),  // Flash to light pink
                    new KeyFrame(Duration.millis(400), new KeyValue(cellLabel.styleProperty(), "-fx-background-color: lightteal;"))   // Flash to light teal
            );

            colorAnimation.setCycleCount(2);
            colorAnimation.setAutoReverse(true);
            colorAnimation.setOnFinished(event -> cellLabel.setStyle(originalStyle));
            colorAnimation.play();
        }
        scaleTransition.play();
    }

    public void updateFields(CoordinateDTO cord, CellDTO cell) {
        cellIdSelection.setText(cord.toString());
        selectedCell = cell;
        updateValue.setDisable(false);
        originalValueBox.setEditable(true);
        if (cell == null) {
            originalValueBox.setText("empty cell");
            showLastVersion.setText("1");
        } else {
            originalValueBox.setText(cell.getOriginalValue().toString());
            showLastVersion.setText(Integer.toString(cell.getLastVersionUpdate()));
        }
    }

    public void enableLastVersionButton() {
        lastVersionButton.setDisable(false);
        versionSelected = true;
    }

    public void disableLastVersionButton() {
        lastVersionButton.setDisable(true);
        versionSelected = false;
    }
    public void cancelCellAnimation() {
        cancelAnimation.set(true);
    }

    public void applyCellAnimation() {
        cancelAnimation.set(false);
    }
    @FXML
    private void handleLastVersionButton() {
        if (versionSelected) {
            mainController.getSheetComponentController().setSheetDTO(mainController.getLatestSheet());
            mainController.showSheet(mainController.getLatestSheet(), false);
            disableLastVersionButton();
        }
    }

    @FXML
    private void handleUpdateValueAction() {
        openUpdateValueDialog(selectedCell);
    }

    public void openUpdateValueDialog(CellDTO cell) {
        boolean validInput = false;
        while (!validInput) {
            try {
                UpdateValueController updateDialog = new UpdateValueController(cell, cellIdSelection.getText(), mainController.getAllCellNames());
                updateDialog.display();

                if (updateDialog.isConfirmed()) {
                    String inputType = updateDialog.getInputType();
                    var selectedOperation = updateDialog.getSelectedOperation();
                    List<FunctionArgument> functionArgs = updateDialog.getOperationArguments();
                    String updatedValue = updateDialog.getGeneratedString();
                    cell = mainController.setCell(cellIdSelection.getText(), updatedValue);
                    Node cellLabel = mainController.getSheetComponentController().getCellNode(cell.getCoordinateDTO());
                    if (cellLabel != null) {
                        applyCellUpdateAnimation(cellLabel);
                    }
                    validInput = true;
                } else {
                    break;
                }
            } catch (Exception e) {
                mainController.showErrorDialog("Error", "Failed to update cell", e.getMessage());
            }
        }
    }

    @FXML
    private void handleVersionSelectorAction() {
        openVersionSelectorDialog();
    }

    public void openVersionSelectorDialog() {
        VersionSelectorController cellValueWindow = new VersionSelectorController(mainController.getSheetVersion(), this);
        cellValueWindow.setMainController(mainController);
        cellValueWindow.display();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        mainController.getHeaderController().setSheetLoadedListener(event -> versionSelector.setDisable(false));
    }

    public void enableVersionSelector() {
        versionSelector.setDisable(false);
    }

}