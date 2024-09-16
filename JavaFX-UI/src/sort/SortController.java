package sort;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import mainController.AppController;

import java.awt.*;

public class SortController {

    AppController mainController;
    @FXML Button cancelSortButton;
    @FXML TextField startCol;
    @FXML TextField endCol;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void handleCancelSortAction()
    {

    }

}
