package DTO;

import javafx.scene.paint.Color;

public class CellStyle {
    private Color backgroundColor;
    private Color textColor;

    public CellStyle(Color backgroundColor, Color textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }
}
