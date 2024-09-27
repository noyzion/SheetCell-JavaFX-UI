package graphs;

import DTO.CellDTO;
import DTO.CoordinateDTO;
import DTO.SheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import mainController.AppController;
import sheet.coordinate.CoordinateFactory;

import java.util.ArrayList;
import java.util.List;

public class GraphController {

    @FXML private VBox graphComponent;
    @FXML private ComboBox<String> xColumnComboBox;
    @FXML private ComboBox<String> xStartRowComboBox;
    @FXML private ComboBox<String> xEndRowComboBox;
    @FXML private ComboBox<String> yColumnComboBox;
    @FXML private ComboBox<String> yStartRowComboBox;
    @FXML private ComboBox<String> yEndRowComboBox;
    @FXML private Button generateGraph;
    @FXML private ComboBox<String> graphTypeComboBox;
    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;

        xStartRowComboBox.setDisable(true);
        yStartRowComboBox.setDisable(true);
        xEndRowComboBox.setDisable(true);
        yEndRowComboBox.setDisable(true);
        xColumnComboBox.setDisable(true);
        yColumnComboBox.setDisable(true);
        disableGraph();
        populateGraphTypeComboBox();
        addInputListeners();
    }

    private void populateGraphTypeComboBox() {
        graphTypeComboBox.getItems().addAll("Line Chart", "Bar Chart");
        graphTypeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onGoBack() {
        onGenerateGraph();
    }

    public void clearUIComponents() {
        xColumnComboBox.getItems().clear();
        xStartRowComboBox.getItems().clear();
        xEndRowComboBox.getItems().clear();
        yColumnComboBox.getItems().clear();
        yStartRowComboBox.getItems().clear();
        yEndRowComboBox.getItems().clear();
        xColumnComboBox.getSelectionModel().clearSelection();
        xStartRowComboBox.getSelectionModel().clearSelection();
        xEndRowComboBox.getSelectionModel().clearSelection();
        yColumnComboBox.getSelectionModel().clearSelection();
        yStartRowComboBox.getSelectionModel().clearSelection();
        yEndRowComboBox.getSelectionModel().clearSelection();
    }

    public void enableGraph() {
        xColumnComboBox.setDisable(false);
        yColumnComboBox.setDisable(false);
        populateColumnComboBoxes();
    }

    public void disableGraph() {
        generateGraph.setDisable(true);
    }

    private void populateColumnComboBoxes() {
        for (int column = 0 ; column < mainController.getLatestSheet().getColumnSize(); column++) {
                xColumnComboBox.getItems().add(CoordinateFactory.convertIndexToColumnLetter(column));
            yColumnComboBox.getItems().add(CoordinateFactory.convertIndexToColumnLetter(column));
        }
    }

    private void populateStartComboBox(ComboBox<String> rowComboBox)
    {
        rowComboBox.setDisable(false);
        for (int row = 1; row <= mainController.getLatestSheet().getRowSize(); row++) {
            rowComboBox.getItems().add(String.valueOf(row));
        }
    }
    private void populateEndComboBox(ComboBox<String> rowStartComboBox,ComboBox<String> rowEndComboBox) {
        rowEndComboBox.setDisable(false);

        for (int row = Integer.parseInt(rowStartComboBox.getValue()); row <= mainController.getLatestSheet().getRowSize(); row++) {
            rowEndComboBox.getItems().add(String.valueOf(row));
        }
    }


    @FXML
    private void onGenerateGraph() {
        String selectedXColumn = xColumnComboBox.getValue();
        String selectedYColumn = yColumnComboBox.getValue();
        String selectedXStartRow = xStartRowComboBox.getValue();
        String selectedXEndRow = xEndRowComboBox.getValue();
        String selectedYStartRow = yStartRowComboBox.getValue();
        String selectedYEndRow = yEndRowComboBox.getValue();

        if (selectedXColumn == null || selectedYColumn == null || selectedXStartRow == null || selectedXEndRow == null ||
            selectedYStartRow == null || selectedYEndRow == null) {
            return;
        }

        List<Double> xAxisData = getColumnData(selectedXColumn, Integer.parseInt(selectedXStartRow), Integer.parseInt(selectedXEndRow));
        List<Double> yAxisData = getColumnData(selectedYColumn, Integer.parseInt(selectedYStartRow), Integer.parseInt(selectedYEndRow));

        String graphType = graphTypeComboBox.getValue();

        if ("Line Chart".equals(graphType)) {
            generateLineChart(xAxisData, yAxisData);
        } else if ("Bar Chart".equals(graphType)) {
            generateBarChart(xAxisData, yAxisData);
        }
    }

    private List<Double> getColumnData(String column, int startRow, int endRow) {
        SheetDTO sheet = mainController.getLatestSheet();
        List<Double> columnData = new ArrayList<>();

        int columnIndex = column.charAt(0) - 'A';

        for (int row = startRow; row <= endRow; row++) {
            CellDTO cell = sheet.getCell(new CoordinateDTO(row, columnIndex).toString());
            if (cell != null) {
                Object cellValue = cell.getEffectiveValue().getValue();
                if (cellValue instanceof Number) {
                    columnData.add(((Number) cellValue).doubleValue());
                }
            }
        }

        return columnData;
    }

    private void generateLineChart(List<Double> xAxisData, List<Double> yAxisData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis");
        yAxis.setLabel("Y Axis");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Line Chart");
        lineChart.setPrefSize(200, 200);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            series.getData().add(new XYChart.Data<>(xAxisData.get(i), yAxisData.get(i)));
        }
        lineChart.getData().add(series);

        graphComponent.getChildren().clear();
        graphComponent.getChildren().add(lineChart);
    }

    private void generateBarChart(List<Double> xAxisData, List<Double> yAxisData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis");
        yAxis.setLabel("Y Axis");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Bar Chart");
        barChart.setPrefSize(200, 200);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(xAxisData.get(i)), yAxisData.get(i)));
        }
        barChart.getData().add(series);

        graphComponent.getChildren().clear();
        graphComponent.getChildren().add(barChart);
    }

    private void addInputListeners() {
        xColumnComboBox.valueProperty().addListener((observable, oldValue, newValue) ->{ checkIfGraphCanBeEnabled();
            populateStartComboBox(xStartRowComboBox); });

        xStartRowComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {checkIfGraphCanBeEnabled();
            populateEndComboBox(xStartRowComboBox,xEndRowComboBox);
        });

        xEndRowComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkIfGraphCanBeEnabled());

        yColumnComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {checkIfGraphCanBeEnabled();
        populateStartComboBox(yStartRowComboBox);
        });
        yStartRowComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {checkIfGraphCanBeEnabled();
            populateEndComboBox(yStartRowComboBox,yEndRowComboBox);});
        yEndRowComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkIfGraphCanBeEnabled());
    }

    private void checkIfGraphCanBeEnabled() {
        boolean canEnable = xColumnComboBox.getValue() != null && xStartRowComboBox.getValue() != null &&
                            xEndRowComboBox.getValue() != null && yColumnComboBox.getValue() != null &&
                            yStartRowComboBox.getValue() != null && yEndRowComboBox.getValue() != null;
        generateGraph.setDisable(!canEnable);
    }
}
