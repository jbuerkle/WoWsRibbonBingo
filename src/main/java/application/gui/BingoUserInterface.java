package application.gui;

import bingo.BingoGame;
import bingo.BingoResult;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ribbons.Ribbon;

import java.util.HashMap;
import java.util.Map;

public class BingoUserInterface extends Application {
    private final Map<Ribbon, TextField> textFieldsByRibbon;
    private final CheckBox battleshipCheckBox;
    private final BingoGame bingoGame;
    private final TextArea textArea;
    private final GridPane mainGrid;
    private int mainGridRow;

    public BingoUserInterface() {
        this.textFieldsByRibbon = new HashMap<>();
        this.battleshipCheckBox = new CheckBox("Use battleship modifier");
        this.bingoGame = new BingoGame();
        this.textArea = new TextArea();
        this.mainGrid = new GridPane();
        this.mainGridRow = 0;
        setUpGridWithFiveInputFieldsPerRow();
        setUpGridWithCheckBoxAndButtons();
        setUpGridWithLargeTextArea();
        resetInputFields();
    }

    private void setUpGridWithFiveInputFieldsPerRow() {
        GridPane gridPane = createNewGridPane();
        int column = 0;
        for (Ribbon ribbon : Ribbon.values()) {
            if (column > 4) {
                mainGridRow++;
                gridPane = createNewGridPane();
                column = 0;
            }
            createInputFieldForRibbon(ribbon, gridPane, column);
            column++;
        }
        mainGridRow++;
    }

    private void setUpGridWithCheckBoxAndButtons() {
        Button submitButton = new Button("Submit result");
        Button goNextButton = new Button("Go to next level");
        Button resetButton = new Button("Reset input fields");
        submitButton.setOnMouseClicked(this::submitResult);
        goNextButton.setOnMouseClicked(this::goToNextLevel);
        resetButton.setOnMouseClicked(this::resetInputFields);
        GridPane gridPane = createNewGridPane();
        gridPane.add(battleshipCheckBox, 0, 0);
        gridPane.add(submitButton, 1, 0);
        gridPane.add(goNextButton, 2, 0);
        gridPane.add(resetButton, 3, 0);
        mainGridRow++;
    }

    private void setUpGridWithLargeTextArea() {
        textArea.setEditable(false);
        textArea.setWrapText(true);
        setTextInTextArea();
        GridPane gridPane = createNewGridPane();
        gridPane.add(textArea, 0, 0);
        mainGridRow++;
    }

    private void setTextInTextArea() {
        textArea.setText(bingoGame.toString());
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

    private void submitResult(MouseEvent event) {
        BingoResult bingoResult = new BingoResult(battleshipCheckBox.isSelected());
        for (Map.Entry<Ribbon, TextField> entry : textFieldsByRibbon.entrySet()) {
            Ribbon ribbon = entry.getKey();
            String userInput = entry.getValue().getText();
            if (!userInput.isBlank()) {
                try {
                    int amount = Integer.parseInt(userInput);
                    bingoResult.addRibbonResult(ribbon, amount);
                } catch (NumberFormatException e) {
                    textArea.setText("Input field for ribbon '%s' does not contain an integer: %s".formatted(
                            ribbon.getDisplayText(), userInput));
                    return;
                }
            }
        }
        bingoGame.submitBingoResult(bingoResult);
        setTextInTextArea();
    }

    private void goToNextLevel(MouseEvent event) {
        if (bingoGame.playerCanGoToNextLevel()) {
            bingoGame.goToNextLevel();
            resetInputFields();
            setTextInTextArea();
        }
    }

    private void resetInputFields(MouseEvent event) {
        resetInputFields();
    }

    private void resetInputFields() {
        textFieldsByRibbon.values().forEach(textField -> textField.setText(""));
        battleshipCheckBox.setSelected(false);
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
