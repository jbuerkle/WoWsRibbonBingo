package bingo.game;

import java.io.Serial;
import java.io.Serializable;

public enum BingoGameState implements Serializable {
    LEVEL_INITIALIZED,
    UNCONFIRMED_RESULT,
    UNCONFIRMED_VOLUNTARY_END,
    CHALLENGE_ENDED;

    @Serial
    private static final long serialVersionUID = -1793552819069605261L;
}
