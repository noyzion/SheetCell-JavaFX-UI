package actionLine;

import DTO.CellDTO;
import expression.FunctionArgument;
import expression.Operation;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateValueController {

    private String inputType;
    private Operation selectedOperation;
    private List<FunctionArgument> functionArguments;
    private CellDTO selectedCell;

    public UpdateValueController(CellDTO cellDTO) {
        this.selectedCell = cellDTO;
    }

    public void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update Value");
        window.setWidth(600);
        window.setHeight(800);

        Label originalValueLabel = createLabel("Original Value: ", selectedCell != null ? selectedCell.getOriginalValue() : "cell is empty");
        Label effectiveValueLabel = createLabel("Effective Value: ", selectedCell != null ? selectedCell.getEffectiveValue().getValue().toString() : "cell is empty");

        ComboBox<String> inputTypeComboBox = new ComboBox<>();
        inputTypeComboBox.getItems().addAll("Number", "Text", "Function");
        inputTypeComboBox.setValue("Number");

        VBox dynamicContentArea = new VBox(10);
        dynamicContentArea.setPadding(new Insets(10));
        dynamicContentArea.getStyleClass().add("vbox");

        // Initial update of dynamic content
        updateDynamicContent(dynamicContentArea, inputTypeComboBox.getValue());

        // Update dynamic content on input type change
        inputTypeComboBox.setOnAction(e -> updateDynamicContent(dynamicContentArea, inputTypeComboBox.getValue()));

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button");
        submitButton.setOnAction(e -> handleSubmit(inputTypeComboBox.getValue(), dynamicContentArea, window));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("window");
        layout.getChildren().addAll(originalValueLabel, effectiveValueLabel, new Label("Choose input type:"), inputTypeComboBox, dynamicContentArea, submitButton);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("UpdateValueStyle.css").toExternalForm()); // Load CSS file
        window.setScene(scene);
        window.showAndWait();
    }

    private Label createLabel(String prefix, String value) {
        return new Label(prefix + value);
    }

    private void updateDynamicContent(VBox dynamicContentArea, String inputType) {
        dynamicContentArea.getChildren().clear();

        switch (inputType) {
            case "Number":
            case "Text":
                TextField textField = new TextField();
                textField.setPromptText("Enter a value");
                dynamicContentArea.getChildren().add(textField);
                break;
            case "Function":
                ComboBox<String> functionChoiceBox = new ComboBox<>();
                functionChoiceBox.getItems().addAll(Arrays.stream(Operation.values()).map(Operation::name).toList());
                functionChoiceBox.setValue(Operation.values()[0].name());

                VBox functionArgumentsContainer = new VBox(5);
                functionChoiceBox.setOnAction(e -> updateFunctionArgumentsContainer(functionArgumentsContainer, functionChoiceBox.getValue()));

                dynamicContentArea.getChildren().addAll(functionChoiceBox, functionArgumentsContainer);
                break;
        }
    }

    private void updateFunctionArgumentsContainer(VBox functionArgumentsContainer, String functionName) {
        functionArgumentsContainer.getChildren().clear();
        if (functionName != null) {
            Operation operation = Operation.valueOf(functionName);
            for (int i = 0; i < operation.getNumArgs(); i++) {
                functionArgumentsContainer.getChildren().add(createFunctionArgumentComponent("Argument " + (i + 1)));
            }
        }
    }

    private Node createFunctionArgumentComponent(String promptText) {
        HBox argumentBox = new HBox(5);

        ComboBox<String> argumentTypeComboBox = new ComboBox<>();
        argumentTypeComboBox.getItems().addAll("Number", "Text", "Function");
        argumentTypeComboBox.setValue("Number");

        TextField argumentField = new TextField();
        argumentField.setPromptText(promptText);

        argumentBox.getChildren().add(argumentTypeComboBox);

        // Default to showing only the TextField
        argumentBox.getChildren().add(argumentField);

        argumentTypeComboBox.setOnAction(e -> updateArgumentBox(argumentBox, argumentTypeComboBox, argumentField));

        return argumentBox;
    }

    private void updateArgumentBox(HBox argumentBox, ComboBox<String> argumentTypeComboBox, TextField argumentField) {
        String selectedType = argumentTypeComboBox.getValue();

        // Clear current content and add ComboBox
        argumentBox.getChildren().clear();
        argumentBox.getChildren().add(argumentTypeComboBox);

        if ("Function".equals(selectedType)) {
            ComboBox<String> nestedFunctionChoiceBox = new ComboBox<>();
            nestedFunctionChoiceBox.getItems().addAll(Arrays.stream(Operation.values()).map(Operation::name).toList());
            nestedFunctionChoiceBox.setValue(Operation.values()[0].name());

            VBox nestedFunctionContainer = new VBox(5);
            nestedFunctionChoiceBox.setOnAction(e -> updateFunctionArgumentsContainer(nestedFunctionContainer, nestedFunctionChoiceBox.getValue()));
            argumentBox.getChildren().addAll(nestedFunctionChoiceBox, nestedFunctionContainer);
        } else {
            argumentBox.getChildren().add(argumentField);
        }
    }

    private void handleSubmit(String inputType, VBox dynamicContentArea, Stage window) {
        this.inputType = inputType;

        if ("Function".equals(inputType)) {
            functionArguments = new ArrayList<>();
            for (var child : dynamicContentArea.getChildren()) {
                if (child instanceof VBox) {
                    VBox argumentBoxContainer = (VBox) child;
                    for (var boxChild : argumentBoxContainer.getChildren()) {
                        if (boxChild instanceof HBox) {
                            functionArguments.add(createFunctionArgument((HBox) boxChild));
                        }
                    }
                }
            }
        } else {
            selectedOperation = null;
            functionArguments = null;
        }

        window.close();
    }

    private FunctionArgument createFunctionArgument(HBox argumentBox) {
        ComboBox<String> argumentTypeComboBox = (ComboBox<String>) argumentBox.getChildren().get(0);
        String argumentType = argumentTypeComboBox.getValue();

        if ("Function".equals(argumentType)) {
            ComboBox<String> nestedFunctionChoiceBox = (ComboBox<String>) argumentBox.getChildren().get(1);
            Operation nestedOperation = Operation.valueOf(nestedFunctionChoiceBox.getValue());
            List<FunctionArgument> nestedArgs = new ArrayList<>();

            VBox nestedFunctionArgumentsContainer = (VBox) argumentBox.getChildren().get(2);
            for (var nestedChild : nestedFunctionArgumentsContainer.getChildren()) {
                if (nestedChild instanceof HBox) {
                    nestedArgs.add(createFunctionArgument((HBox) nestedChild));
                }
            }

            return new FunctionArgument(nestedOperation, nestedArgs);
        } else {
            TextField argumentField = (TextField) argumentBox.getChildren().get(1);
            return new FunctionArgument(argumentType, argumentField.getText());
        }
    }

    public String getInputType() {
        return inputType;
    }

    public Operation getSelectedOperation() {
        return selectedOperation;
    }

    public List<FunctionArgument> getOperationArguments() {
        return functionArguments;
    }
}
