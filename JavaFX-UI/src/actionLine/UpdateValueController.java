package actionLine;

import DTO.CellDTO;
import expression.FunctionArgument;
import expression.Operation;
import javafx.geometry.Insets;
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
        inputTypeComboBox.getStyleClass().add("combo-box");

        VBox dynamicContentArea = new VBox(10);
        dynamicContentArea.setPadding(new Insets(10));
        dynamicContentArea.getStyleClass().add("vbox");

        inputTypeComboBox.setOnAction(e -> updateDynamicContent(dynamicContentArea, inputTypeComboBox.getValue()));

        updateDynamicContent(dynamicContentArea, "Number"); // Initialize with Number selected

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
                dynamicContentArea.getChildren().add(createTextField("Enter a value"));
                break;
            case "Function":
                ComboBox<String> functionChoiceBox = createFunctionChoiceBox();
                VBox functionArgumentsContainer = createFunctionArgumentsContainer();
                dynamicContentArea.getChildren().addAll(functionChoiceBox, functionArgumentsContainer);
                break;
        }
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private ComboBox<String> createFunctionChoiceBox() {
        ComboBox<String> functionChoiceBox = new ComboBox<>();
        functionChoiceBox.getItems().addAll(Arrays.stream(Operation.values()).map(Operation::name).toList());

        functionChoiceBox.setOnAction(e -> {
            VBox parentVBox = (VBox) functionChoiceBox.getParent();
            if (parentVBox != null && parentVBox.getChildren().size() > 1 && parentVBox.getChildren().get(1) instanceof VBox) {
                VBox functionArgumentsContainer = (VBox) parentVBox.getChildren().get(1);
                updateFunctionArgumentsContainer(functionArgumentsContainer, functionChoiceBox.getValue());
            }
        });

        return functionChoiceBox;
    }

    private VBox createFunctionArgumentsContainer() {
        VBox functionArgumentsContainer = new VBox(5);
        return functionArgumentsContainer; // Will be updated later
    }

    private void updateFunctionArgumentsContainer(VBox functionArgumentsContainer, String functionName) {
        functionArgumentsContainer.getChildren().clear();
        if (functionName != null) {
            Operation operation = Operation.valueOf(functionName);
            for (int i = 0; i < operation.getNumArgs(); i++) {
                functionArgumentsContainer.getChildren().add(createArgumentBox("Argument " + (i + 1)));
            }
        }
    }

    private VBox createArgumentBox(String promptText) {
        VBox argumentBox = new VBox(5);

        ComboBox<String> argumentTypeComboBox = new ComboBox<>();
        argumentTypeComboBox.getItems().addAll("Number", "Text", "Function");
        argumentTypeComboBox.setValue("Number");

        TextField argumentField = createTextField(promptText);
        VBox nestedFunctionContainer = new VBox(5);

        argumentTypeComboBox.setOnAction(e -> {
            updateArgumentBox(argumentBox, argumentTypeComboBox, argumentField, nestedFunctionContainer);
        });

        argumentBox.getChildren().addAll(argumentTypeComboBox, argumentField);
        return argumentBox;
    }

    private void updateArgumentBox(VBox argumentBox, ComboBox<String> argumentTypeComboBox, TextField argumentField, VBox nestedFunctionContainer) {
        argumentBox.getChildren().clear();
        switch (argumentTypeComboBox.getValue()) {
            case "Number":
            case "Text":
                argumentBox.getChildren().addAll(argumentTypeComboBox, argumentField);
                break;
            case "Function":
                ComboBox<String> nestedFunctionChoiceBox = createFunctionChoiceBox();
                argumentBox.getChildren().addAll(argumentTypeComboBox, nestedFunctionChoiceBox, nestedFunctionContainer);
                nestedFunctionChoiceBox.setOnAction(e -> {
                    updateFunctionArgumentsContainer(nestedFunctionContainer, nestedFunctionChoiceBox.getValue());
                });
                updateFunctionArgumentsContainer(nestedFunctionContainer, nestedFunctionChoiceBox.getValue());
                break;
        }
    }

    private void handleSubmit(String inputType, VBox dynamicContentArea, Stage window) {
        this.inputType = inputType;

        if ("Function".equals(inputType)) {
            functionArguments = new ArrayList<>();
            for (var child : dynamicContentArea.getChildren()) {
                if (child instanceof VBox) {
                    functionArguments.add(createFunctionArgument((VBox) child));
                }
            }
        } else {
            selectedOperation = null;
            functionArguments = null;
        }

        window.close();
    }

    private FunctionArgument createFunctionArgument(VBox argumentBox) {
        ComboBox<String> argumentTypeComboBox = (ComboBox<String>) argumentBox.getChildren().get(0);
        String argumentType = argumentTypeComboBox.getValue();

        if ("Function".equals(argumentType)) {
            ComboBox<String> nestedFunctionChoiceBox = (ComboBox<String>) argumentBox.getChildren().get(1);
            Operation nestedOperation = Operation.valueOf(nestedFunctionChoiceBox.getValue());
            List<FunctionArgument> nestedArgs = new ArrayList<>();

            VBox nestedFunctionArgumentsContainer = (VBox) argumentBox.getChildren().get(2);
            for (var nestedChild : nestedFunctionArgumentsContainer.getChildren()) {
                if (nestedChild instanceof VBox) {
                    nestedArgs.add(createFunctionArgument((VBox) nestedChild));
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
