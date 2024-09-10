package header;

import DTO.CellDTO;
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
    private List<String> functionArguments;
    private CellDTO selectedCell;

    public UpdateValueController(CellDTO cellDTO) {
        this.selectedCell = cellDTO;
    }

    public void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update Value");
        window.setMinWidth(400);
        Label originalValueLabel;
        Label effectiveValueLabel;
        if(selectedCell != null ) {
             originalValueLabel = new Label("Original Value: " + selectedCell.getOriginalValue());
             effectiveValueLabel = new Label("Effective Value: " + selectedCell.getEffectiveValue().getValue().toString());
        }
        else
        {
             originalValueLabel = new Label("Original Value: cell is empty");
             effectiveValueLabel = new Label("Effective Value: cell is empty");
        }

        Label chooseTypeLabel = new Label("Choose input type:");
        ComboBox<String> inputTypeComboBox = new ComboBox<>();
        inputTypeComboBox.getItems().addAll("Number", "Text", "Function");
        inputTypeComboBox.setValue("Number");

        VBox dynamicContentArea = new VBox();
        dynamicContentArea.setSpacing(10);
        dynamicContentArea.setPadding(new Insets(10));

        ComboBox<String> functionChoiceBox = new ComboBox<>();
        functionChoiceBox.getItems().addAll(Arrays.stream(Operation.values())
                .map(Operation::name)
                .toList()); // Add function names from enum
        functionChoiceBox.setVisible(false);

        VBox functionArgumentsContainer = new VBox();
        functionArgumentsContainer.setSpacing(5);
        functionArgumentsContainer.setVisible(false);

        TextField valueInputField = new TextField();
        valueInputField.setPromptText("Enter a value");

        inputTypeComboBox.setOnAction(e -> {
            String selectedType = inputTypeComboBox.getValue();
            dynamicContentArea.getChildren().clear();

            switch (selectedType) {
                case "Number":
                    dynamicContentArea.getChildren().add(valueInputField);
                    valueInputField.setPromptText("Enter a number");
                    break;
                case "Text":
                    dynamicContentArea.getChildren().add(valueInputField);
                    valueInputField.setPromptText("Enter text");
                    break;
                case "Function":
                    dynamicContentArea.getChildren().add(functionChoiceBox);
                    dynamicContentArea.getChildren().add(functionArgumentsContainer);
                    functionChoiceBox.setVisible(true);
                    functionArgumentsContainer.setVisible(true);
                    break;
            }
        });

        functionChoiceBox.setOnAction(e -> {
            functionArgumentsContainer.getChildren().clear();
            String selectedFunctionName = functionChoiceBox.getValue();

            try {
                // Convert the selected function name to an Operation enum
                selectedOperation = Operation.valueOf(selectedFunctionName);
                int numberOfArguments = selectedOperation.getNumArgs();

                // Create and add argument fields based on the number of arguments
                for (int i = 0; i < numberOfArguments; i++) {
                    TextField argumentField = new TextField();
                    argumentField.setPromptText("Argument " + (i + 1));
                    functionArgumentsContainer.getChildren().add(argumentField);
                }
            } catch (IllegalArgumentException ex) {
                // Handle invalid function names
                System.err.println("Invalid function selected: " + selectedFunctionName);
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            inputType = inputTypeComboBox.getValue();
            if ("Function".equals(inputType) && selectedOperation != null) {
                functionArguments = new ArrayList<>();
                for (var child : functionArgumentsContainer.getChildren()) {
                    if (child instanceof TextField) {
                        functionArguments.add(((TextField) child).getText());
                    }
                }
            } else {
                selectedOperation = null;
                functionArguments = null;
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

    public String getInputType() {
        return inputType;
    }

    public Operation getSelectedOperation() {
        return selectedOperation;
    }

    public List<String> getOperationArguments() {
        return functionArguments;
    }
}
