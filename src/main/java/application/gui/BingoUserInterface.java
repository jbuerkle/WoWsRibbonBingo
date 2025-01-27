package application.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ribbons.Ribbon;

import java.util.HashMap;
import java.util.Map;

public class BingoUserInterface extends Application {
    private final Map<Ribbon, TextField> textFieldsByRibbon;
    private final GridPane mainGrid;
    private int mainGridRow;

    public BingoUserInterface() {
        textFieldsByRibbon = new HashMap<>();
        mainGrid = new GridPane();
        mainGridRow = 0;
        setUpGridWithFourInputFieldsPerRow();
    }

    private void setUpGridWithFourInputFieldsPerRow() {
        GridPane gridPane = createNewGridPane();
        int column = 0;
        for (Ribbon ribbon : Ribbon.values()) {
            if (column > 3) {
                mainGridRow++;
                gridPane = createNewGridPane();
                column = 0;
            }
            createInputFieldForRibbon(ribbon, gridPane, column);
            column++;
        }
        mainGridRow++;
    }

    private GridPane createNewGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(40);
        gridPane.setPadding(new Insets(15));
        mainGrid.add(gridPane, 0, mainGridRow);
        return gridPane;
    }

    private void createInputFieldForRibbon(Ribbon ribbon, GridPane gridPane, int column) {
        Label label = new Label(ribbon.getDisplayText());
        TextField textField = new TextField();
        gridPane.add(label, column, 0);
        gridPane.add(textField, column, 1);
        gridPane.getColumnConstraints().add(new ColumnConstraints());
        textFieldsByRibbon.put(ribbon, textField);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(mainGrid);
        primaryStage.setTitle("World of Warships Ribbon Bingo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
