package bingo.application;

import bingo.application.gui.PlayerRegistrationUserInterface;
import bingo.application.gui.constants.UserInterfaceConstants;
import javafx.application.Application;
import javafx.stage.Stage;

public class BingoGameApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(UserInterfaceConstants.APPLICATION_TITLE);
        PlayerRegistrationUserInterface playerRegistrationUserInterface =
                new PlayerRegistrationUserInterface(primaryStage);
        playerRegistrationUserInterface.setScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
