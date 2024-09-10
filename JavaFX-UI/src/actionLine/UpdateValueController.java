package actionLine;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class UpdateValueController {

    private String inputType; // Stores the type of input (Number, String, Function)
    private String selectedFunction; // Stores the selected function name
    private List<String> functionArguments; // Stores the function arguments

    // Variables to hold previous values
    private String originalValue;
    private String effectiveValue;

    // Constructor to accept previous values
    public UpdateValueController(String originalValue, String effectiveValue) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
    }

    public void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update Value");
        window.setMinWidth(400);

        // Create UI elements for previous values
        Label originalValueLabel = new Label("Original Value: " + originalValue);
        Label effectiveValueLabel = new Label("Effective Value: " + effectiveValue);

        // Create UI elements for input type selection
        Label chooseTypeLabel = new Label("Choose input type:");
        ComboBox<String> inputTypeComboBox = new ComboBox<>();
        inputTypeComboBox.getItems().addAll("Number", "String", "Function");
        inputTypeComboBox.setValue("Number");

        // Dynamic content area to update based on input type
        VBox dynamicContentArea = new VBox();
        dynamicContentArea.setSpacing(10);
        dynamicContentArea.setPadding(new Insets(10));

        // Function choice box and input fields (initially hidden)
        ComboBox<String> functionChoiceBox = new ComboBox<>();
        functionChoiceBox.getItems().addAll("SUM", "AVG", "MIN", "MAX"); // Add function names here
        functionChoiceBox.setVisible(false);

        // Argument container for function input fields
        VBox functionArgumentsContainer = new VBox();
        functionArgumentsContainer.setSpacing(5);
        functionArgumentsContainer.setVisible(false);

        TextField valueInputField = new TextField();
        valueInputField.setPromptText("Enter a value");

        // Add input type change listener
        inputTypeComboBox.setOnAction(e -> {
            String selectedType = inputTypeComboBox.getValue();
            dynamicContentArea.getChildren().clear();

            switch (selectedType) {
                case "Number":
                    dynamicContentArea.getChildren().add(valueInputField);
                    valueInputField.setPromptText("Enter a number");
                    break;
                case "String":
                    dynamicContentArea.getChildren().add(valueInputField);
                    valueInputField.setPromptText("Enter a string");
                    break;
                case "Function":
                    dynamicContentArea.getChildren().add(functionChoiceBox);
                    dynamicContentArea.getChildren().add(functionArgumentsContainer);
                    functionChoiceBox.setVisible(true);
                    functionArgumentsContainer.setVisible(true);
                    break;
            }
        });

        // Handle function selection to create argument fields
        functionChoiceBox.setOnAction(e -> {
            functionArgumentsContainer.getChildren().clear();
            String selectedFunction = functionChoiceBox.getValue();
            int numberOfArguments = getNumberOfArgumentsForFunction(selectedFunction);

            // Dynamically create fields for each argument
            for (int i = 0; i < numberOfArguments; i++) {
                TextField argumentField = new TextField();
                argumentField.setPromptText("Argument " + (i + 1));
                functionArgumentsContainer.getChildren().add(argumentField);
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            inputType = inputTypeComboBox.getValue();
            if ("Function".equals(inputType)) {
                selectedFunction = functionChoiceBox.getValue();
                functionArguments = new ArrayList<>();
                for (var child : functionArgumentsContainer.getChildren()) {
                    if (child instanceof TextField) {
                        functionArguments.add(((TextField) child).getText());
                    }
                }
            } else {
                selectedFunction = valueInputField.getText();
            }
            window.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(originalValueLabel, effectiveValueLabel, chooseTypeLabel, inputTypeComboBox, dynamicContentArea, submitButton);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    private int getNumberOfArgumentsForFunction(String functionName) {
        // Define the number of arguments required for each function
        switch (functionName) {
            case "SUM":
                return 2; // Example: SUM requires 2 arguments
            case "AVG":
                return 3; // Example: AVG requires 3 arguments
            case "MIN":
                return 2; // Example: MIN requires 2 arguments
            case "MAX":
                return 2; // Example: MAX requires 2 arguments
            default:
                return 0; // Default case if no function is selected
        }
    }


    public String getInputType() {
        return inputType;
    }

    public String getSelectedFunction() {
        return selectedFunction;
    }

    public List<String> getFunctionArguments() {
        return functionArguments;
    }
}