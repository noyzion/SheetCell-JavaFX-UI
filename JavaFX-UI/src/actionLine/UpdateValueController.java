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
import mainContoroller.AppController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateValueController {

    private String inputType;
    private Operation selectedOperation;
    private List<FunctionArgument> functionArguments;
    private CellDTO selectedCell;
    private String generatedString;
    private List<String> cellNames;


    public UpdateValueController(CellDTO cellDTO, List<String> cellNames) {
        this.cellNames = cellNames;
        this.selectedCell = cellDTO;
    }

    public String getGeneratedString() {
        return generatedString;
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

    private Node createFunctionArgumentComponent(String promptText, String operation) {
        HBox argumentBox = new HBox(5);

        if ("REF".equals(operation)) {
            ComboBox<String> refComboBox = new ComboBox<>();
            refComboBox.getItems().addAll(cellNames);
            refComboBox.setPromptText("Select a cell");
            argumentBox.getChildren().add(refComboBox);

        } else {
            ComboBox<String> argumentTypeComboBox = new ComboBox<>();
            argumentTypeComboBox.getItems().addAll("Number", "Text", "Function");
            argumentTypeComboBox.setValue("Number");

            TextField argumentField = new TextField();
            argumentField.setPromptText(promptText);

            argumentBox.getChildren().add(argumentTypeComboBox);
            argumentBox.getChildren().add(argumentField);

            argumentTypeComboBox.setOnAction(e -> updateArgumentBox(argumentBox, argumentTypeComboBox, argumentField));
        }

        return argumentBox;
    }

    private void updateFunctionArgumentsContainer(VBox functionArgumentsContainer, String functionName) {
        functionArgumentsContainer.getChildren().clear();
        selectedOperation = Operation.valueOf(functionName);
        if (functionName != null) {
            Operation operation = Operation.valueOf(functionName);
            for (int i = 0; i < operation.getNumArgs(); i++) {
                functionArgumentsContainer.getChildren().add(createFunctionArgumentComponent("Argument " + (i + 1), functionName));
            }
        }
    }

    private void updateArgumentBox(HBox argumentBox, ComboBox<String> argumentTypeComboBox, TextField argumentField) {
        String selectedType = argumentTypeComboBox.getValue();

        argumentBox.getChildren().clear();
        argumentBox.getChildren().add(argumentTypeComboBox);

        if ("Function".equals(selectedType) && selectedOperation != Operation.REF) {
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


    private FunctionArgument createFunctionArgument(HBox argumentBox) {

        ComboBox<String> argumentTypeComboBox = (ComboBox<String>) argumentBox.getChildren().get(0);
        String argumentType = argumentTypeComboBox.getValue();
        String argumentValue;
        if ("Function".equals(argumentType) && selectedOperation != Operation.REF) {
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
        } else if (Operation.REF == selectedOperation) {
            ComboBox<String> refComboBox = (ComboBox<String>) argumentBox.getChildren().get(0);
            argumentValue = refComboBox.getValue(); // Get the selected cell reference
            return new FunctionArgument(argumentValue);
        } else {
            TextField argumentField = (TextField) argumentBox.getChildren().get(1);
            argumentValue = argumentField.getText();
        }
        return new FunctionArgument(argumentValue);

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


    private String formatNonFunctionArgument(String value) {
        return value;
    }

    private void handleSubmit(String inputType, VBox dynamicContentArea, Stage window) {
        this.inputType = inputType;
        generatedString = "";

        if ("Function".equals(inputType)) {
            ComboBox<String> functionChoiceBox = (ComboBox<String>) dynamicContentArea.getChildren().get(0);
            selectedOperation = Operation.valueOf(functionChoiceBox.getValue());

            functionArguments = new ArrayList<>();
            VBox functionArgumentsContainer = (VBox) dynamicContentArea.getChildren().get(1);
            for (Node child : functionArgumentsContainer.getChildren()) {
                if (child instanceof HBox) {
                    functionArguments.add(createFunctionArgument((HBox) child));
                }
            }

            if (!functionArguments.isEmpty()) {
                generatedString = formatOperation(selectedOperation, functionArguments);
            }

            System.out.println("Formatted String: " + generatedString);
        } else {
            selectedOperation = null;
            functionArguments = null;
            TextField inputField = (TextField) dynamicContentArea.getChildren().get(0);
            generatedString = inputField.getText();
        }
        window.close();
    }

    private String formatOperation(Operation operation, List<FunctionArgument> functionArguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(operation.name());
        sb.append(",");
        sb.append(generateFormattedString(functionArguments));
        sb.append("}");
        return sb.toString();
    }

    private String generateFormattedString(List<FunctionArgument> functionArguments) {
        if (functionArguments == null || functionArguments.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (FunctionArgument argument : functionArguments) {
            sb.append(formatArgument(argument));
            sb.append(",");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    private String formatArgument(FunctionArgument argument) {
        if (argument.isFunction()) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");
            sb.append(argument.getOperation().name());
            List<FunctionArgument> nestedArgs = argument.getNestedArguments();
            if (nestedArgs != null && !nestedArgs.isEmpty()) {
                sb.append(",");
                sb.append(generateFormattedString(nestedArgs));
            }
            sb.append("}");

            return sb.toString();
        } else {
            return formatNonFunctionArgument(argument.getValue());
        }
    }
}

