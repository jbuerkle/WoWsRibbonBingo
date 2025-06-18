package bingo.application.gui;

import bingo.achievements.Achievement;
import bingo.game.BingoGame;
import bingo.game.input.UserInputException;
import bingo.game.results.BingoResult;
import bingo.game.util.BingoGameOutputSplitter;
import bingo.game.util.BingoGameSerializer;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.generator.RandomShipRestrictionGenerator;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.restrictions.impl.ForcedMainArmamentType;
import bingo.ribbons.Ribbon;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class BingoUserInterface extends Application {
    private static final String SHIP_RESTRICTION_ALREADY_SET = "A ship restriction is already set!";
    private static final String SHIP_ALREADY_USED = "This ship was already used!";
    private static final String AUTOSAVE_DIRECTORY = "autosave";
    private static final String EMPTY_STRING = "";

    private Stage primaryStage;
    private BingoGame bingoGame;
    private final RandomShipRestrictionGenerator randomShipRestrictionGenerator;
    private final ComboBox<MainArmamentType> mainArmamentTypeComboBox;
    private final BingoGameOutputSplitter bingoGameOutputSplitter;
    private final BingoGameSerializer bingoGameSerializer;
    private final Map<Ribbon, TextField> textFieldsByRibbon;
    private final Map<Achievement, TextField> textFieldsByAchievement;
    private final Map<RetryRule, CheckBox> checkBoxesByRetryRule;
    private final TableView<Ship> tableView;
    private final TableColumn<Ship, String> shipNameColumn;
    private final TextField shipInputField;
    private final TextField numberInputField;
    private final TextField playerNameInputField;
    private final TextArea textArea;
    private final GridPane mainGrid;
    private int mainGridRow;

    public BingoUserInterface() {
        this.bingoGame = new BingoGame();
        this.randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();
        this.mainArmamentTypeComboBox = new ComboBox<>();
        this.bingoGameOutputSplitter = new BingoGameOutputSplitter();
        this.bingoGameSerializer = new BingoGameSerializer();
        this.textFieldsByRibbon = new HashMap<>();
        this.textFieldsByAchievement = new HashMap<>();
        this.checkBoxesByRetryRule = new HashMap<>();
        this.tableView = new TableView<>();
        this.shipNameColumn = new TableColumn<>("Ships used");
        this.shipInputField = new TextField();
        this.numberInputField = new TextField();
        this.playerNameInputField = new TextField();
        this.textArea = new TextArea();
        this.mainGrid = new GridPane();
        this.mainGrid.setPadding(new Insets(5));
        this.mainGridRow = 0;
        setUpGridWithSevenInputFieldsPerRow();
        setUpGridWithComboBoxAndCheckBoxes();
        setUpGridWithButtons();
        setUpGridWithLargeTextAreaAndTableView();
        resetInputFields();
        createAutosaveDirectory();
    }

    private void setUpGridWithSevenInputFieldsPerRow() {
        setUpGridWithSevenInputFieldsPerRow(Ribbon.values(), Ribbon::getDisplayText, textFieldsByRibbon);
        setUpGridWithSevenInputFieldsPerRow(Achievement.values(), Achievement::getDisplayText, textFieldsByAchievement);
    }

    private <T> void setUpGridWithSevenInputFieldsPerRow(
            T[] obtainableValues, Function<T, String> displayTextGetter, Map<T, TextField> textFieldsByObtainable) {
        GridPane gridPane = createNewGridPane();
        int column = 0;
        for (T obtainable : obtainableValues) {
            if (column > 6) {
                mainGridRow++;
                gridPane = createNewGridPane();
                column = 0;
            }
            TextField textField = createInputFieldWithLabel(displayTextGetter.apply(obtainable), gridPane, column);
            textFieldsByObtainable.put(obtainable, textField);
            column++;
        }
        mainGridRow++;
    }

    private void setUpGridWithComboBoxAndCheckBoxes() {
        Label mainArmamentTypeLabel = new Label("Main armament of ship used");
        setUpMainArmamentTypeComboBox();
        GridPane gridPane = createNewGridPane();
        gridPane.add(mainArmamentTypeLabel, 0, 0);
        gridPane.add(mainArmamentTypeComboBox, 0, 1);
        setUpCheckBoxesForRetryRules(gridPane);
        mainGridRow++;
    }

    private void setUpGridWithButtons() {
        Button submitButton = new Button("Submit result");
        Button confirmButton = new Button("Confirm result");
        Button endChallengeButton = new Button("End challenge");
        Button resetButton = new Button("Reset input fields");
        setEventHandlers(submitButton, this::submitResult);
        setEventHandlers(confirmButton, this::confirmResult);
        setEventHandlers(endChallengeButton, this::endChallenge);
        setEventHandlers(resetButton, this::resetInputFieldsAndBingoGame);
        GridPane gridPane = createNewGridPane();
        gridPane.add(submitButton, 0, 0);
        gridPane.add(confirmButton, 1, 0);
        gridPane.add(endChallengeButton, 2, 0);
        gridPane.add(resetButton, 3, 0);
        mainGridRow++;
    }

    private void setUpGridWithLargeTextAreaAndTableView() {
        textArea.setEditable(false);
        textArea.setWrapText(true);
        shipNameColumn.setCellValueFactory(ship -> ship.getValue().nameProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getColumns().add(shipNameColumn);
        GridPane tableInputGrid = createGridPaneForTableInputFieldAndButtons();
        GridPane autosaveGrid = createGridPaneForAutosaveInputFieldAndButton();
        GridPane gridPane = createNewGridPane();
        gridPane.add(textArea, 0, 0);
        gridPane.add(tableView, 1, 0);
        gridPane.add(tableInputGrid, 2, 0);
        gridPane.add(autosaveGrid, 3, 0);
        mainGridRow++;
    }

    private void setUpMainArmamentTypeComboBox() {
        mainArmamentTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MainArmamentType mainArmamentType) {
                if (mainArmamentType == null) {
                    return EMPTY_STRING;
                }
                return mainArmamentType.getDisplayText();
            }

            @Override
            public MainArmamentType fromString(String string) {
                for (MainArmamentType mainArmamentType : MainArmamentType.values()) {
                    if (mainArmamentType.getDisplayText().equals(string)) {
                        return mainArmamentType;
                    }
                }
                return null;
            }
        });
        updateComboBoxWithAllowedMainArmamentTypes();
    }

    private void updateComboBoxWithAllowedMainArmamentTypes() {
        ShipRestriction shipRestriction = bingoGame.getShipRestriction();
        final List<MainArmamentType> allowedMainArmamentTypes;
        if (shipRestriction instanceof BannedMainArmamentType(MainArmamentType bannedMainArmamentType)) {
            allowedMainArmamentTypes = Stream.of(MainArmamentType.values())
                    .filter(mainArmamentType -> !mainArmamentType.equals(bannedMainArmamentType))
                    .toList();
        } else if (shipRestriction instanceof ForcedMainArmamentType(MainArmamentType forcedMainArmamentType)) {
            allowedMainArmamentTypes = List.of(forcedMainArmamentType);
        } else {
            allowedMainArmamentTypes = List.of(MainArmamentType.values());
        }
        mainArmamentTypeComboBox.getItems().clear();
        mainArmamentTypeComboBox.getItems().addAll(allowedMainArmamentTypes);
        mainArmamentTypeComboBox.setValue(allowedMainArmamentTypes.getFirst());
    }

    private void setUpCheckBoxesForRetryRules(GridPane gridPane) {
        int column = 1;
        for (RetryRule retryRule : RetryRule.values()) {
            CheckBox checkBox = new CheckBox(retryRule.getDisplayText());
            checkBoxesByRetryRule.put(retryRule, checkBox);
            gridPane.add(checkBox, column, 1);
            column++;
        }
    }

    private GridPane createGridPaneForTableInputFieldAndButtons() {
        Label shipInputFieldLabel = new Label("Name of ship used");
        Label numberInputFieldLabel = new Label("Any positive integer chosen by player");
        Button addShipButton = new Button("Add ship from input field");
        Button removeShipButton = new Button("Remove ship selected in table");
        Button setRestrictionButton = new Button("Get ship restriction for chosen number");
        Button removeRestrictionButton = new Button("Remove current ship restriction");
        setEventHandlers(addShipButton, this::addShip);
        setEventHandlers(removeShipButton, this::removeShip);
        setEventHandlers(setRestrictionButton, this::setRestriction);
        setEventHandlers(removeRestrictionButton, this::removeRestriction);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.add(shipInputFieldLabel, 0, 0);
        gridPane.add(shipInputField, 0, 1);
        gridPane.add(addShipButton, 0, 2);
        gridPane.add(removeShipButton, 0, 3);
        gridPane.add(numberInputFieldLabel, 0, 4);
        gridPane.add(numberInputField, 0, 5);
        gridPane.add(setRestrictionButton, 0, 6);
        gridPane.add(removeRestrictionButton, 0, 7);
        return gridPane;
    }

    private GridPane createGridPaneForAutosaveInputFieldAndButton() {
        Label playerNameLabel = new Label("Name of player for autosave");
        Button loadAutosaveButton = new Button("Load game from autosave");
        setEventHandlers(loadAutosaveButton, this::loadFromSaveFile);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.add(playerNameLabel, 0, 0);
        gridPane.add(playerNameInputField, 0, 1);
        gridPane.add(loadAutosaveButton, 0, 2);
        return gridPane;
    }

    private void setTextInTextArea() {
        String bingoGameOutput = bingoGame.toString();
        String splitOutput = bingoGameOutputSplitter.process(bingoGameOutput);
        textArea.setText(splitOutput);
    }

    private GridPane createNewGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(40);
        gridPane.setPadding(new Insets(10));
        mainGrid.add(gridPane, 0, mainGridRow);
        return gridPane;
    }

    private TextField createInputFieldWithLabel(String displayText, GridPane gridPane, int column) {
        Label label = new Label(displayText);
        TextField textField = new TextField();
        gridPane.add(label, column, 0);
        gridPane.add(textField, column, 1);
        gridPane.getColumnConstraints().add(new ColumnConstraints());
        return textField;
    }

    private void submitResult(@SuppressWarnings("unused") InputEvent event) {
        BingoResult bingoResult = new BingoResult(mainArmamentTypeComboBox.getValue());
        try {
            for (Map.Entry<Ribbon, TextField> entry : textFieldsByRibbon.entrySet()) {
                Ribbon ribbon = entry.getKey();
                int amount = getAmountFromUserInput(entry.getValue(), ribbon.getDisplayText());
                bingoResult.addRibbonResult(ribbon, amount);
            }
            for (Map.Entry<Achievement, TextField> entry : textFieldsByAchievement.entrySet()) {
                Achievement achievement = entry.getKey();
                int amount = getAmountFromUserInput(entry.getValue(), achievement.getDisplayText());
                bingoResult.addAchievementResult(achievement, amount);
            }
            List<RetryRule> activeRetryRules = checkBoxesByRetryRule.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().isSelected())
                    .map(Map.Entry::getKey)
                    .toList();
            boolean stateChangeSuccessful = bingoGame.submitBingoResult(bingoResult, activeRetryRules);
            if (stateChangeSuccessful) {
                setTextInTextArea();
            }
        } catch (UserInputException exception) {
            textArea.setText(exception.getMessage());
        }
    }

    private int getAmountFromUserInput(TextField textField, String displayText) throws UserInputException {
        String trimmedUserInput = textField.getText().trim();
        if (userInputIsNotBlank(trimmedUserInput)) {
            try {
                return Integer.parseInt(trimmedUserInput);
            } catch (NumberFormatException exception) {
                String message =
                        "Input field for '%s' does not contain an integer: %s".formatted(displayText, trimmedUserInput);
                throw new UserInputException(message, exception);
            }
        }
        return 0;
    }

    private void confirmResult(@SuppressWarnings("unused") InputEvent event) {
        boolean stateChangeSuccessful = bingoGame.confirmCurrentResult();
        if (stateChangeSuccessful) {
            updateComboBoxWithAllowedMainArmamentTypes();
            resetInputFields();
            createAutosaveFile();
        }
    }

    private void createAutosaveDirectory() {
        try {
            Files.createDirectories(Paths.get(AUTOSAVE_DIRECTORY));
        } catch (IOException exception) {
            textArea.setText("Failed to create autosave directory: " + exception.getMessage());
        }
    }

    private void createAutosaveFile() {
        String trimmedUserInput = playerNameInputField.getText().trim();
        if (userInputIsNotBlank(trimmedUserInput)) {
            String fileName = generateFileNameForAutosave(trimmedUserInput);
            String filePath = "%s/%s".formatted(AUTOSAVE_DIRECTORY, fileName);
            try {
                bingoGameSerializer.saveGame(bingoGame, filePath);
            } catch (IOException exception) {
                textArea.setText("Failed to create autosave file: " + exception.getMessage());
            }
        }
    }

    private String generateFileNameForAutosave(String playerName) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String dateString = today.format(formatter);
        return "%s_%s.wrb".formatted(playerName, dateString);
    }

    private void loadFromSaveFile(@SuppressWarnings("unused") InputEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter wrbFilter = new FileChooser.ExtensionFilter("WRB Files (*.wrb)", "*.wrb");
        fileChooser.getExtensionFilters().add(wrbFilter);
        fileChooser.setInitialDirectory(new File(AUTOSAVE_DIRECTORY));
        fileChooser.setTitle("Open WRB File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            loadFromSaveFile(selectedFile.getPath());
        }
    }

    private void loadFromSaveFile(String filePath) {
        try {
            bingoGame = bingoGameSerializer.loadGame(filePath);
            tableView.getItems().clear();
            tableView.getItems().addAll(bingoGame.getShipsUsed());
            updateComboBoxWithAllowedMainArmamentTypes();
            resetInputFields();
        } catch (IOException | ClassNotFoundException exception) {
            textArea.setText("Failed to load from save file: " + exception.getMessage());
        }
    }

    private void resetInputFieldsAndBingoGame(@SuppressWarnings("unused") InputEvent event) {
        resetInputFieldsAndBingoGame();
    }

    private void resetInputFieldsAndBingoGame() {
        boolean stateChangeSuccessful = bingoGame.doResetForCurrentLevel();
        if (stateChangeSuccessful) {
            resetInputFields();
        }
    }

    private void resetInputFields() {
        textFieldsByRibbon.values().forEach(this::clearInput);
        textFieldsByAchievement.values().forEach(this::clearInput);
        checkBoxesByRetryRule.values().forEach(this::clearInput);
        setTextInTextArea();
    }

    private void clearInput(TextField textField) {
        textField.setText(EMPTY_STRING);
    }

    private void clearInput(CheckBox checkBox) {
        checkBox.setSelected(false);
    }

    private void endChallenge(@SuppressWarnings("unused") InputEvent event) {
        boolean stateChangeSuccessful = bingoGame.endChallenge();
        if (stateChangeSuccessful) {
            setTextInTextArea();
        }
    }

    private void addShip(@SuppressWarnings("unused") InputEvent event) {
        String trimmedUserInput = shipInputField.getText().trim();
        if (userInputIsNotBlank(trimmedUserInput)) {
            Ship ship = new Ship(trimmedUserInput);
            boolean shipSuccessfullyAdded = bingoGame.addShipUsed(ship);
            if (shipSuccessfullyAdded) {
                tableView.getItems().add(ship);
                clearInput(shipInputField);
            } else {
                textArea.setText(SHIP_ALREADY_USED);
            }
        }
    }

    private boolean userInputIsNotBlank(String userInput) {
        return !userInput.isBlank();
    }

    private void removeShip(@SuppressWarnings("unused") InputEvent event) {
        Ship ship = tableView.getSelectionModel().getSelectedItem();
        bingoGame.getShipsUsed().remove(ship);
        tableView.getItems().remove(ship);
    }

    private void setRestriction(@SuppressWarnings("unused") InputEvent event) {
        try {
            int number = getAmountFromUserInput(numberInputField, "Number chosen by player");
            ShipRestriction shipRestriction = randomShipRestrictionGenerator.getForNumber(number);
            boolean restrictionSuccessfullySet = bingoGame.setShipRestriction(shipRestriction);
            if (restrictionSuccessfullySet) {
                updateComboBoxWithAllowedMainArmamentTypes();
                clearInput(numberInputField);
                setTextInTextArea();
            } else {
                textArea.setText(SHIP_RESTRICTION_ALREADY_SET);
            }
        } catch (UserInputException exception) {
            textArea.setText(exception.getMessage());
        }
    }

    private void removeRestriction(@SuppressWarnings("unused") InputEvent event) {
        bingoGame.removeShipRestriction();
        updateComboBoxWithAllowedMainArmamentTypes();
        setTextInTextArea();
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
        this.primaryStage = primaryStage;
        Scene scene = new Scene(mainGrid);
        primaryStage.setTitle("World of Warships Ribbon Bingo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
