package application.gui;

import bingo.BingoGame;
import bingo.BingoResult;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ribbons.Ribbon;
import ships.Ship;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BingoUserInterface extends Application {
    private static final String SHIP_ALREADY_USED = "This ship was already used!";
    private final Map<Ribbon, TextField> textFieldsByRibbon;
    private final ObservableList<Ship> shipsUsed;
    private final TableView<Ship> tableView;
    private final TableColumn<Ship, String> shipNameColumn;
    private final TextField shipInputField;
    private final CheckBox battleshipCheckBox;
    private final BingoGame bingoGame;
    private final TextArea textArea;
    private final GridPane mainGrid;
    private int mainGridRow;

    public BingoUserInterface() {
        this.textFieldsByRibbon = new HashMap<>();
        this.shipsUsed = FXCollections.observableList(new LinkedList<>());
        this.tableView = new TableView<>(shipsUsed);
        this.shipNameColumn = new TableColumn<>("Ships used");
        this.shipInputField = new TextField();
        this.battleshipCheckBox = new CheckBox("Use battleship modifier");
        this.bingoGame = new BingoGame();
        this.textArea = new TextArea();
        this.mainGrid = new GridPane();
        this.mainGridRow = 0;
        setUpGridWithFiveInputFieldsPerRow();
        setUpGridWithCheckBoxAndButtons();
        setUpGridWithLargeTextAreaAndTableView();
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
        setEventHandlers(submitButton, this::submitResult);
        setEventHandlers(goNextButton, this::goToNextLevel);
        setEventHandlers(resetButton, this::resetInputFields);
        GridPane gridPane = createNewGridPane();
        gridPane.add(battleshipCheckBox, 0, 0);
        gridPane.add(submitButton, 1, 0);
        gridPane.add(goNextButton, 2, 0);
        gridPane.add(resetButton, 3, 0);
        mainGridRow++;
    }

    private void setUpGridWithLargeTextAreaAndTableView() {
        textArea.setEditable(false);
        textArea.setWrapText(true);
        shipNameColumn.setCellValueFactory(ship -> ship.getValue().nameProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getColumns().add(shipNameColumn);
        setTextInTextArea();
        GridPane tableInputGrid = createGridPaneForTableInputFieldAndButtons();
        GridPane gridPane = createNewGridPane();
        gridPane.add(textArea, 0, 0);
        gridPane.add(tableView, 1, 0);
        gridPane.add(tableInputGrid, 2, 0);
        mainGridRow++;
    }

    private GridPane createGridPaneForTableInputFieldAndButtons() {
        Label shipInputFieldLabel = new Label("Name of ship used");
        Button addShipButton = new Button("Add ship from input field");
        Button removeShipButton = new Button("Remove ship selected in table");
        setEventHandlers(addShipButton, this::addShip);
        setEventHandlers(removeShipButton, this::removeShip);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.add(shipInputFieldLabel, 0, 0);
        gridPane.add(shipInputField, 0, 1);
        gridPane.add(addShipButton, 0, 2);
        gridPane.add(removeShipButton, 0, 3);
        return gridPane;
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

    private void submitResult(InputEvent event) {
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

    private void goToNextLevel(InputEvent event) {
        if (bingoGame.playerCanGoToNextLevel()) {
            bingoGame.goToNextLevel();
            resetInputFields();
            setTextInTextArea();
        }
    }

    private void resetInputFields(InputEvent event) {
        resetInputFields();
    }

    private void resetInputFields() {
        textFieldsByRibbon.values().forEach(this::clearInput);
        battleshipCheckBox.setSelected(false);
    }

    private void clearInput(TextField textField) {
        textField.setText("");
    }

    private void addShip(InputEvent event) {
        String trimmedUserInput = shipInputField.getText().trim();
        if (containsUsableInput(trimmedUserInput)) {
            for (Ship ship : shipsUsed) {
                if (trimmedUserInput.equalsIgnoreCase(ship.name())) {
                    shipInputField.setText(SHIP_ALREADY_USED);
                    return;
                }
            }
            shipsUsed.add(new Ship(trimmedUserInput));
            clearInput(shipInputField);
        }
    }

    private boolean containsUsableInput(String userInput) {
        return !userInput.isBlank() && !userInput.equals(SHIP_ALREADY_USED);
    }

    private void removeShip(InputEvent event) {
        Ship ship = tableView.getSelectionModel().getSelectedItem();
        shipsUsed.remove(ship);
    }

    private void setEventHandlers(Button button, EventHandler<InputEvent> eventHandler) {
        button.setOnMouseClicked(eventHandler);
        button.setOnKeyPressed(onPressEnterOrSpacePerform(eventHandler));
    }

    private EventHandler<KeyEvent> onPressEnterOrSpacePerform(EventHandler<InputEvent> eventHandler) {
        return event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode.equals(KeyCode.ENTER) || keyCode.equals(KeyCode.SPACE)) {
                eventHandler.handle(event);
            }
        };
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
