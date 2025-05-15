package bingo.game.input;

import java.io.Serial;

public class UserInputException extends Exception {
    @Serial
    private static final long serialVersionUID = -3891620162498187015L;

    public UserInputException(String message, Exception cause) {
        super(message, cause);
    }

    public UserInputException(String message) {
        super(message);
    }
}
