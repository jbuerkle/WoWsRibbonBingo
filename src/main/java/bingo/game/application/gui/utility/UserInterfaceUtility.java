package bingo.game.application.gui.utility;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;

public class UserInterfaceUtility {

    public void setStyleSheetsFor(Scene scene) {
        URL resourceUrl = getClass().getResource("/stylesheets/dark-mode.css");
        if (resourceUrl != null) {
            scene.getStylesheets().add(resourceUrl.toExternalForm());
        }
    }

    public void setEventHandlers(Button button, EventHandler<InputEvent> eventHandler) {
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
}
