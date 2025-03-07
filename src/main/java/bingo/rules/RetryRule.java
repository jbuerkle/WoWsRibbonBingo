package bingo.rules;

public enum RetryRule {
    IMBALANCED_MATCHMAKING("Imbalanced matchmaking (rule 8a or 8b)"),
    UNFAIR_DISADVANTAGE("Unfair disadvantage (rule 8c)");

    private final String displayText;

    RetryRule(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
