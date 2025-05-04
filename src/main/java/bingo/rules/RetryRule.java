package bingo.rules;

import java.io.Serial;
import java.io.Serializable;

public enum RetryRule implements Serializable {
    IMBALANCED_MATCHMAKING("Imbalanced matchmaking (rule 8a or 8b)"),
    UNFAIR_DISADVANTAGE("Unfair disadvantage (rule 8c)");

    @Serial
    private static final long serialVersionUID = -2975621168644240225L;

    private final String displayText;

    RetryRule(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
