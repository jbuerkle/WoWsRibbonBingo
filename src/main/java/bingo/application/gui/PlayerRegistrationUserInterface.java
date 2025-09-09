package bingo.application.gui;

import bingo.application.gui.constants.UserInterfaceConstants;
import bingo.application.gui.utility.UserInterfaceUtility;
import bingo.game.BingoGame;
import bingo.game.input.UserInputException;
import bingo.game.modifiers.ChallengeModifier;
import bingo.game.utility.BingoGameSerializer;
import bingo.players.Player;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerRegistrationUserInterface {
    private static final String CREATE_AUTOSAVE_FAILURE_HEADER = "Failed to create autosave directory";
    private static final String CREATE_AUTOSAVE_FAILURE_MESSAGE =
            "Please make sure that this application is running in a directory which does not require administrative rights to modify, then restart the application.";
    private static final String LOAD_AUTOSAVE_FAILURE_HEADER = "Failed to load from save file";
    private static final String LOAD_AUTOSAVE_FAILURE_MESSAGE =
            "The save file may be from a previous version of the game. Either start a new game, or try loading the save file with a previous version of the application.";

    private final Stage primaryStage;
    private final Map<ChallengeModifier, CheckBox> checkBoxesByChallengeModifier;
    private final UserInterfaceUtility userInterfaceUtility;
    private final BingoGameSerializer bingoGameSerializer;
    private final TableView<Player> tableView;
    private final TextField playerInputField;
    private final Button addPlayerButton;
    private final Button removePlayerButton;
    private final Button startNewGameButton;
    private final Button loadAutosaveButton;
    private final GridPane mainGrid;

    public PlayerRegistrationUserInterface(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.checkBoxesByChallengeModifier = new LinkedHashMap<>();
        this.userInterfaceUtility = new UserInterfaceUtility();
        this.bingoGameSerializer = new BingoGameSerializer();
        this.tableView = new TableView<>();
        this.playerInputField = new TextField();
        this.addPlayerButton = new Button("Add player");
        this.removePlayerButton = new Button("Remove selected player");
        this.startNewGameButton = new Button("Start new game");
        this.loadAutosaveButton = new Button("Load game from autosave");
        this.mainGrid = createMainGridPane();
        setUpTableView();
        setUpInputFieldAndButtons();
        setUpCheckBoxesForChallengeModifiers();
        updateButtonVisibility();
    }

    public void setScene() {
        Scene scene = new Scene(mainGrid);
        userInterfaceUtility.setStyleSheetsFor(scene);
        primaryStage.setScene(scene);
        createAutosaveDirectory();
    }

    private void setUpTableView() {
        TableColumn<Player, String> playerNameColumn = new TableColumn<>("Player names");
        playerNameColumn.setCellValueFactory(player -> player.getValue().nameProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getColumns().add(playerNameColumn);
        mainGrid.add(tableView, 0, 0);
    }

    private void setUpInputFieldAndButtons() {
        Label playerInputFieldLabel = new Label("Player name");
        userInterfaceUtility.setEventHandlers(addPlayerButton, this::addPlayer);
        userInterfaceUtility.setEventHandlers(removePlayerButton, this::removePlayer);
        userInterfaceUtility.setEventHandlers(startNewGameButton, this::startNewGame);
        userInterfaceUtility.setEventHandlers(loadAutosaveButton, this::loadFromSaveFile);
        GridPane gridPane = createGridPaneWithVerticalGap();
        gridPane.add(playerInputFieldLabel, 0, 0);
        gridPane.add(playerInputField, 0, 1);
        gridPane.add(addPlayerButton, 0, 2);
        gridPane.add(removePlayerButton, 0, 3);
        gridPane.add(startNewGameButton, 0, 4);
        gridPane.add(loadAutosaveButton, 0, 5);
        mainGrid.add(gridPane, 1, 0);
    }

    private void setUpCheckBoxesForChallengeModifiers() {
        Label challengeModifierLabel = new Label("Challenge modifiers");
        GridPane gridPane = createGridPaneWithVerticalGap();
        gridPane.add(challengeModifierLabel, 0, 0);
        int row = 1;
        for (ChallengeModifier challengeModifier : ChallengeModifier.values()) {
            CheckBox checkBox = new CheckBox(challengeModifier.getDisplayName());
            checkBoxesByChallengeModifier.put(challengeModifier, checkBox);
            gridPane.add(checkBox, 0, row);
            row++;
        }
        mainGrid.add(gridPane, 2, 0);
    }

    private GridPane createGridPaneWithVerticalGap() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(15);
        return gridPane;
    }

    private GridPane createMainGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(20);
        return gridPane;
    }

    private void addPlayer(InputEvent ignoredEvent) {
        Optional<Player> player = createPlayerFromUserInput(playerInputField.getText());
        if (player.isPresent()) {
            tableView.getItems().add(player.get());
            clearInput(playerInputField);
            updateButtonVisibility();
        }
    }

    private Optional<Player> createPlayerFromUserInput(String userInput) {
        String trimmedUserInput = userInput.trim();
        if (userInputContainsUniquePlayerName(trimmedUserInput)) {
            return Optional.of(new Player(trimmedUserInput));
        }
        return Optional.empty();
    }

    private boolean userInputContainsUniquePlayerName(String trimmedUserInput) {
        return !trimmedUserInput.isBlank() &&
                tableView.getItems().stream().noneMatch(player -> trimmedUserInput.equalsIgnoreCase(player.name()));
    }

    private void removePlayer(InputEvent ignoredEvent) {
        Player player = tableView.getSelectionModel().getSelectedItem();
        tableView.getItems().remove(player);
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        int numberOfPlayers = tableView.getItems().size();
        addPlayerButton.setDisable(numberOfPlayers >= 3);
        removePlayerButton.setDisable(numberOfPlayers == 0);
        startNewGameButton.setDisable(numberOfPlayers == 0);
    }

    private void createAutosaveDirectory() {
        try {
            Files.createDirectories(Paths.get(UserInterfaceConstants.AUTOSAVE_DIRECTORY));
        } catch (IOException exception) {
            showWarning(CREATE_AUTOSAVE_FAILURE_HEADER, CREATE_AUTOSAVE_FAILURE_MESSAGE);
        }
    }

    private void startNewGame(InputEvent ignoredEvent) {
        List<Player> players = tableView.getItems();
        List<ChallengeModifier> challengeModifiers = getSelectedChallengeModifiers();
        try {
            BingoGame bingoGame = new BingoGame(players, challengeModifiers);
            transitionToMainScene(bingoGame);
        } catch (UserInputException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private List<ChallengeModifier> getSelectedChallengeModifiers() {
        return checkBoxesByChallengeModifier.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .toList();
    }

    private void loadFromSaveFile(InputEvent ignoredEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter wrbFilter = new FileChooser.ExtensionFilter("WRB Files (*.wrb)", "*.wrb");
        fileChooser.getExtensionFilters().add(wrbFilter);
        fileChooser.setInitialDirectory(new File(UserInterfaceConstants.AUTOSAVE_DIRECTORY));
        fileChooser.setTitle("Open WRB File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            loadFromSaveFile(selectedFile.getPath());
        }
    }

    private void loadFromSaveFile(String filePath) {
        try {
            BingoGame bingoGame = bingoGameSerializer.loadGame(filePath);
            transitionToMainScene(bingoGame);
        } catch (IOException | ClassNotFoundException exception) {
            showWarning(LOAD_AUTOSAVE_FAILURE_HEADER, LOAD_AUTOSAVE_FAILURE_MESSAGE);
        }
    }

    private void transitionToMainScene(BingoGame bingoGame) {
        primaryStage.hide();
        BingoGameUserInterface bingoGameUserInterface = new BingoGameUserInterface(bingoGame, primaryStage);
        bingoGameUserInterface.setScene();
        primaryStage.show();
    }

    private void showWarning(String headerText, String messageText) {
        Alert alert = new Alert(AlertType.WARNING, messageText, ButtonType.OK);
        alert.setTitle(UserInterfaceConstants.APPLICATION_TITLE);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private void clearInput(TextField textField) {
        textField.setText(UserInterfaceConstants.EMPTY_STRING);
    }
}
