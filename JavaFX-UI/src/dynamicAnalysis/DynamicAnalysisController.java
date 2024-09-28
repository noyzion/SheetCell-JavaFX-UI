package dynamicAnalysis;

import DTO.CoordinateDTO;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import mainController.AppController;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateParser;

public class DynamicAnalysisController {

    private AppController mainController;

    @FXML private Label selectedCell;
    @FXML private TextField minValue;
    @FXML private TextField maxValue;
    @FXML private TextField stepSize;
    @FXML private Button cancelButton;
    @FXML private Slider slider;
boolean dynamicAnlysis = false;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        disableInputFields();
        addInputListeners();
    }

    public void clearUIComponents() {
      minValue.clear();
      maxValue.clear();
      stepSize.clear();
      slider.setValue(0);
    }

    @FXML
    private void handleCancelAction()
    {
        if (dynamicAnlysis) {
            mainController.getSheetComponentController().setSheetDTO(mainController.getLatestSheet());
            mainController.showSheet(mainController.getLatestSheet(), false);
            disableInputFields();
        }
    }
    public void disableInputFields() {
        minValue.setDisable(true);
        maxValue.setDisable(true);
        stepSize.setDisable(true);
        slider.setDisable(true);
        dynamicAnlysis = false;
    }

    public void enableInputFields() {
        minValue.setDisable(false);
        maxValue.setDisable(false);
        stepSize.setDisable(false);
        dynamicAnlysis = true;
    }

    public void updateCellCoordinate(CoordinateDTO coordinate) {
        selectedCell.setText(coordinate.toString());
        enableInputFields();
    }

    public void enableDynamicAnalysis() {
        dynamicAnlysis = true;
        try {
            double min = Double.parseDouble(minValue.getText());
            double max = Double.parseDouble(maxValue.getText());

            if (min >= max) {
                throw new IllegalArgumentException("Invalid range.");
            }

            slider.setMin(min);
            slider.setMax(max);
            slider.setValue(min);
            slider.setDisable(false);

            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.setMajorTickUnit((max - min) / 10);
            slider.setMinorTickCount(5);
cancelButton.setDisable(false);
            slider.valueProperty().addListener((obs, oldValue, newValue) -> {
                try {
                    performDynamicAnalysis(newValue.doubleValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                updateStepSize();
            });

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numerical values.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addInputListeners() {
        ChangeListener<String> inputChangeListener = (observable, oldValue, newValue) -> {
            if (!minValue.getText().isEmpty() && !maxValue.getText().isEmpty()) {
                enableDynamicAnalysis();
            }
        };

        minValue.textProperty().addListener(inputChangeListener);
        maxValue.textProperty().addListener(inputChangeListener);
    }

    private void performDynamicAnalysis(double newValue) throws Exception {
        String cellCoordinate = selectedCell.getText();
        Coordinate coordinate = CoordinateParser.parse(cellCoordinate);

      mainController.setCellDemo(cellCoordinate,Double.toString(newValue));
    }

    private void updateStepSize() {
        double min = Double.parseDouble(minValue.getText());
        double max = Double.parseDouble(maxValue.getText());
        double currentValue = slider.getValue();

        double range = max - min;
        double steps = Math.ceil(range / 10);
        double step = range / steps;

        stepSize.setText(String.format("%.2f", step));
    }
}
