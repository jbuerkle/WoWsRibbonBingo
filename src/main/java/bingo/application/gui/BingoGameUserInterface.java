package bingo.application.gui;

import bingo.achievements.Achievement;
import bingo.achievements.AchievementResult;
import bingo.application.gui.constants.UserInterfaceConstants;
import bingo.application.gui.utility.UserInterfaceUtility;
import bingo.game.BingoGame;
import bingo.game.input.UserInputException;
import bingo.game.results.BingoResult;
import bingo.game.util.BingoGameOutputSplitter;
import bingo.game.util.BingoGameSerializer;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.generator.RandomShipRestrictionGenerator;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.restrictions.impl.ForcedMainArmamentType;
import bingo.ribbons.Ribbon;
import bingo.ribbons.RibbonResult;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class BingoGameUserInterface {
    private static final String SHIP_RESTRICTION_ALREADY_SET = "A ship restriction is already set!";
    private static final String SHIP_ALREADY_USED = "This ship was already used!";

    private final BingoGame bingoGame;
    private final Stage primaryStage;
    private final RandomShipRestrictionGenerator randomShipRestrictionGenerator;
    private final ComboBox<Player> playerComboBox;
    private final ComboBox<MainArmamentType> mainArmamentTypeComboBox;
    private final BingoGameOutputSplitter bingoGameOutputSplitter;
    private final BingoGameSerializer bingoGameSerializer;
    private final UserInterfaceUtility userInterfaceUtility;
    private final Map<Ribbon, TextField> textFieldsByRibbon;
    private final Map<Achievement, TextField> textFieldsByAchievement;
    private final Map<RetryRule, CheckBox> checkBoxesByRetryRule;
    private final TableView<Ship> tableView;
    private final TableColumn<Ship, String> shipNameColumn;
    private final Label lastAutosaveLabel;
    private final TextField shipInputField;
    private final TextField numberInputField;
    private final TextArea textArea;
    private final GridPane mainGrid;
    private int mainGridRow;

    public BingoGameUserInterface(BingoGame bingoGame, Stage primaryStage) {
        this.bingoGame = bingoGame;
        this.primaryStage = primaryStage;
        this.randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();
        this.playerComboBox = new ComboBox<>();
        this.mainArmamentTypeComboBox = new ComboBox<>();
        this.bingoGameOutputSplitter = new BingoGameOutputSplitter();
        this.bingoGameSerializer = new BingoGameSerializer();
        this.userInterfaceUtility = new UserInterfaceUtility();
        this.textFieldsByRibbon = new HashMap<>();
        this.textFieldsByAchievement = new HashMap<>();
        this.checkBoxesByRetryRule = new HashMap<>();
        this.tableView = new TableView<>();
        this.shipNameColumn = new TableColumn<>("Ships used");
        this.lastAutosaveLabel = new Label("Game not autosaved yet");
        this.shipInputField = new TextField();
        this.numberInputField = new TextField();
        this.textArea = new TextArea();
        this.mainGrid = new GridPane();
        this.mainGrid.setPadding(new Insets(5));
        this.mainGridRow = 0;
        setUpGridWithSevenInputFieldsPerRow();
        setUpGridWithComboBoxesAndCheckBoxes();
        setUpGridWithButtons();
        setUpGridWithLargeTextAreaAndTableView();
        resetInputFields();
    }

    public void setScene() {
        Scene scene = new Scene(mainGrid);
        userInterfaceUtility.setStyleSheetsFor(scene);
        primaryStage.setScene(scene);
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

    private void setUpGridWithComboBoxesAndCheckBoxes() {
        Label playerLabel = new Label("Player");
        Label mainArmamentTypeLabel = new Label("Main armament of ship used");
        setUpPlayerComboBox();
        setUpMainArmamentTypeComboBox();
        GridPane gridPane = createNewGridPane();
        gridPane.add(playerLabel, 0, 0);
        gridPane.add(playerComboBox, 0, 1);
        gridPane.add(mainArmamentTypeLabel, 1, 0);
        gridPane.add(mainArmamentTypeComboBox, 1, 1);
        setUpCheckBoxesForRetryRules(gridPane);
        mainGridRow++;
    }

    private void setUpGridWithButtons() {
        Button submitButton = new Button("Submit result");
        Button confirmButton = new Button("Confirm result");
        Button endChallengeButton = new Button("End challenge");
        Button resetButton = new Button("Reset input fields");
        userInterfaceUtility.setEventHandlers(submitButton, this::submitResult);
        userInterfaceUtility.setEventHandlers(confirmButton, this::confirmResult);
        userInterfaceUtility.setEventHandlers(endChallengeButton, this::endChallenge);
        userInterfaceUtility.setEventHandlers(resetButton, this::resetInputFieldsAndBingoGame);
        GridPane gridPane = createNewGridPane();
        gridPane.add(submitButton, 0, 0);
        gridPane.add(confirmButton, 1, 0);
        gridPane.add(endChallengeButton, 2, 0);
        gridPane.add(resetButton, 3, 0);
        gridPane.add(lastAutosaveLabel, 4, 0);
        mainGridRow++;
    }

    private void setUpGridWithLargeTextAreaAndTableView() {
        textArea.setEditable(false);
        textArea.setWrapText(true);
        shipNameColumn.setCellValueFactory(ship -> ship.getValue().nameProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getColumns().add(shipNameColumn);
        tableView.getItems().addAll(bingoGame.getShipsUsed());
        GridPane tableInputGrid = createGridPaneForTableInputFieldAndButtons();
        GridPane gridPane = createNewGridPane();
        gridPane.add(textArea, 0, 0);
        gridPane.add(tableView, 1, 0);
        gridPane.add(tableInputGrid, 2, 0);
        mainGridRow++;
    }

    private void setUpPlayerComboBox() {
        List<Player> registeredPlayers = bingoGame.getPlayers();
        playerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Player player) {
                if (player == null) {
                    return UserInterfaceConstants.EMPTY_STRING;
                }
                return player.name();
            }

            @Override
            public Player fromString(String string) {
                for (Player player : registeredPlayers) {
                    if (player.name().equals(string)) {
                        return player;
                    }
                }
                return null;
            }
        });
        playerComboBox.getItems().addAll(registeredPlayers);
        playerComboBox.setOnAction(this::onPlayerSelectionChange);
        playerComboBox.setValue(registeredPlayers.getFirst());
    }

    private void setUpMainArmamentTypeComboBox() {
        mainArmamentTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MainArmamentType mainArmamentType) {
                if (mainArmamentType == null) {
                    return UserInterfaceConstants.EMPTY_STRING;
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
        ShipRestriction shipRestriction = bingoGame.getShipRestrictionForPlayer(getSelectedPlayer()).orElse(null);
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
        int column = 2;
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
        userInterfaceUtility.setEventHandlers(addShipButton, this::addShip);
        userInterfaceUtility.setEventHandlers(removeShipButton, this::removeShip);
        userInterfaceUtility.setEventHandlers(setRestrictionButton, this::setRestriction);
        userInterfaceUtility.setEventHandlers(removeRestrictionButton, this::removeRestriction);
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

    private void setTextInTextArea() {
        String bingoGameOutput = bingoGame.toString();
        List<String> splitOutput = bingoGameOutputSplitter.process(bingoGameOutput);
        String splitOutputAsString = bingoGameOutputSplitter.combineAsStringWithDoubleLineBreaks(splitOutput);
        textArea.setText(splitOutputAsString);
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

    private void submitResult(InputEvent ignoredEvent) {
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
            bingoGame.setActiveRetryRules(activeRetryRules);
            boolean stateChangeSuccessful = bingoGame.submitBingoResultForPlayer(getSelectedPlayer(), bingoResult);
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

    private void confirmResult(InputEvent ignoredEvent) {
        boolean stateChangeSuccessful = bingoGame.confirmCurrentResult();
        if (stateChangeSuccessful) {
            updateComboBoxWithAllowedMainArmamentTypes();
            resetInputFields();
            createAutosaveFile();
        }
    }

    private void createAutosaveFile() {
        String fileName = generateFileNameForAutosave();
        String filePath = "%s/%s".formatted(UserInterfaceConstants.AUTOSAVE_DIRECTORY, fileName);
        try {
            bingoGameSerializer.saveGame(bingoGame, filePath);
            updateLastAutosaveLabel();
        } catch (IOException exception) {
            textArea.setText("Failed to create autosave file: " + exception.getMessage());
        }
    }

    private String generateFileNameForAutosave() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String dateString = today.format(formatter);
        return "%s_%s.wrb".formatted(getPlayerNamesAsString(), dateString);
    }

    private String getPlayerNamesAsString() {
        return bingoGame.getPlayers()
                .stream()
                .map(Player::name)
                .reduce((playerNameA, playerNameB) -> playerNameA.concat("+").concat(playerNameB))
                .orElseThrow();
    }

    private void updateLastAutosaveLabel() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = currentTime.format(formatter);
        lastAutosaveLabel.setText("Last successful autosave at " + timeString);
    }

    private void resetInputFieldsAndBingoGame(InputEvent ignoredEvent) {
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

    private void clearAllPlayerDependantInputFields() {
        textFieldsByRibbon.values().forEach(this::clearInput);
        textFieldsByAchievement.values().forEach(this::clearInput);
    }

    private void clearInput(TextField textField) {
        textField.setText(UserInterfaceConstants.EMPTY_STRING);
    }

    private void clearInput(CheckBox checkBox) {
        checkBox.setSelected(false);
    }

    private void endChallenge(InputEvent ignoredEvent) {
        boolean stateChangeSuccessful = bingoGame.endChallenge();
        if (stateChangeSuccessful) {
            setTextInTextArea();
        }
    }

    private void addShip(InputEvent ignoredEvent) {
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

    private void removeShip(InputEvent ignoredEvent) {
        Ship ship = tableView.getSelectionModel().getSelectedItem();
        boolean shipSuccessfullyRemoved = bingoGame.removeShipUsed(ship);
        if (shipSuccessfullyRemoved) {
            tableView.getItems().remove(ship);
        }
    }

    private void setRestriction(InputEvent ignoredEvent) {
        try {
            int number = getAmountFromUserInput(numberInputField, "Number chosen by player");
            ShipRestriction shipRestriction = randomShipRestrictionGenerator.getForNumber(number);
            boolean restrictionSuccessfullySet =
                    bingoGame.setShipRestrictionForPlayer(getSelectedPlayer(), shipRestriction);
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

    private void removeRestriction(InputEvent ignoredEvent) {
        bingoGame.removeShipRestrictionForPlayer(getSelectedPlayer());
        updateComboBoxWithAllowedMainArmamentTypes();
        setTextInTextArea();
    }

    private void onPlayerSelectionChange(ActionEvent ignoredEvent) {
        Optional<BingoResult> optionalBingoResult = bingoGame.getBingoResultForPlayer(getSelectedPlayer());
        updateComboBoxWithAllowedMainArmamentTypes();
        clearAllPlayerDependantInputFields();
        if (optionalBingoResult.isPresent()) {
            BingoResult bingoResult = optionalBingoResult.get();
            mainArmamentTypeComboBox.setValue(bingoResult.getMainArmamentType());
            updateRibbonAmountsFromPlayerData(bingoResult.getRibbonResultSet());
            updateAchievementAmountsFromPlayerData(bingoResult.getAchievementResultSet());
        }
    }

    private void updateRibbonAmountsFromPlayerData(Set<RibbonResult> ribbonResultSet) {
        for (RibbonResult ribbonResult : ribbonResultSet) {
            TextField textField = textFieldsByRibbon.get(ribbonResult.ribbon());
            textField.setText(String.valueOf(ribbonResult.amount()));
        }
    }

    private void updateAchievementAmountsFromPlayerData(Set<AchievementResult> achievementResultSet) {
        for (AchievementResult achievementResult : achievementResultSet) {
            TextField textField = textFieldsByAchievement.get(achievementResult.achievement());
            textField.setText(String.valueOf(achievementResult.amount()));
        }
    }

    private Player getSelectedPlayer() {
        return playerComboBox.getSelectionModel().getSelectedItem();
    }
}
