package bingo.game;

import java.io.Serial;
import java.io.Serializable;

public enum BingoGameState implements Serializable {
    LEVEL_INITIALIZED(false),
    PREREQUISITE_SETUP_DONE(false),
    PARTIAL_RESULT_SUBMITTED(false),
    UNCONFIRMED_VOLUNTARY_END(false),
    UNCONFIRMED_SUCCESSFUL_MATCH(false),
    UNCONFIRMED_UNSUCCESSFUL_MATCH(false),
    CHALLENGE_ENDED_VOLUNTARILY(true),
    CHALLENGE_ENDED_SUCCESSFULLY(true),
    CHALLENGE_ENDED_UNSUCCESSFULLY(true);

    @Serial
    private static final long serialVersionUID = -1793552819069605261L;

    private final boolean isFinal;

    BingoGameState(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
