package expression;

import expression.Operation;
import java.util.List;

public class FunctionArgument {
    private String inputType;
    private String value;
    private Operation operation;
    private List<FunctionArgument> nestedArguments;

    public FunctionArgument(String inputType, String value) {
        this.inputType = inputType;
        this.value = value;
    }

    public FunctionArgument(Operation operation, List<FunctionArgument> nestedArguments) {
        this.inputType = "Function";
        this.operation = operation;
        this.nestedArguments = nestedArguments;
    }

    public String getInputType() {
        return inputType;
    }

    public String getValue() {
        return value;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<FunctionArgument> getNestedArguments() {
        return nestedArguments;
    }
}
