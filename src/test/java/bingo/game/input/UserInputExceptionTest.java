package bingo.game.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserInputExceptionTest {
    private static final String DUMMY_MESSAGE = "Dummy message";

    @Test
    void shouldReturnTheMessageSetViaConstructor() {
        Exception exception = new UserInputException(DUMMY_MESSAGE);
        assertEquals(DUMMY_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldReturnTheMessageAndCauseSetViaConstructor() {
        Exception cause = new IllegalArgumentException();
        Exception exception = new UserInputException(DUMMY_MESSAGE, cause);
        assertEquals(DUMMY_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
