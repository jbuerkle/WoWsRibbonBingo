package bingo.rules;

public enum RetryRule {
    IMBALANCED_MATCHMAKING("imbalanced matchmaking (rule 8a or 8b)"),
    UNFAIR_DISADVANTAGE("an unfair disadvantage (rule 8c)");

    private final String displayText;

    RetryRule(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
