package bingo.game.modifiers;

import bingo.text.TextUtility;

import java.io.Serial;
import java.io.Serializable;

public enum ChallengeModifier implements Serializable {
    RANDOM_SHIP_RESTRICTIONS(
            "Random ship restrictions",
            0.4,
            "All participating streamers get random ship restrictions, as described in [the section below](#optional-ship-restrictions), %reward%."),
    INCREASED_DIFFICULTY(
            "Increased difficulty",
            0.2,
            "The point requirements for each level increase by 20%, %reward%."),
    DOUBLE_DIFFICULTY_INCREASE(
            "Double difficulty increase",
            0.2,
            "The point requirements for each level increase by another 20%, %reward%. Duo/trio streamer challenge only."),
    NO_HELP("No help", 0.2, "Supporters cannot join your division, %reward%. Solo streamer challenge only."),
    NO_GIVING_UP(
            "No giving up",
            0.2,
            "You cannot end the challenge early, %reward%. This does not affect your ability to pause the challenge."),
    NO_SAFETY_NET("No safety net", 0.5, "You do not gain any extra lives, %reward%.");

    @Serial
    private static final long serialVersionUID = -4027175656426943041L;

    private final String displayName;
    private final double bonusModifier;
    private final String descriptionWithPlaceholder;

    ChallengeModifier(String displayName, double bonusModifier, String descriptionWithPlaceholder) {
        this.displayName = displayName;
        this.bonusModifier = bonusModifier;
        this.descriptionWithPlaceholder = descriptionWithPlaceholder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBonusModifier() {
        return bonusModifier;
    }

    @Override
    public String toString() {
        return displayName.concat(": ").concat(descriptionWithPlaceholder.replace(
                "%reward%",
                getTextForBonusModifier()));
    }

    private String getTextForBonusModifier() {
        return "in exchange for +%s additional rewards".formatted(TextUtility.getAsPercentage(bonusModifier));
    }

    public static String getAllChallengeModifiersListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ChallengeModifier challengeModifier : ChallengeModifier.values()) {
            stringBuilder.append("- ").append(challengeModifier.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
