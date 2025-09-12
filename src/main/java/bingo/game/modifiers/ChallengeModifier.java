package bingo.game.modifiers;

import bingo.text.TextUtility;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Predicate;

public enum ChallengeModifier implements Serializable {
    RANDOM_SHIP_RESTRICTIONS(
            "Random ship restrictions",
            0.4,
            0,
            "All participating streamers get random ship restrictions, as described in [the section below](#optional-ship-restrictions), %reward%.",
            ChallengeModifier::alwaysAllowed),
    INCREASED_DIFFICULTY(
            "Increased difficulty",
            0.2,
            0.2,
            "The point requirements for each level increase by 20%, %reward%.",
            ChallengeModifier::alwaysAllowed),
    DOUBLE_DIFFICULTY_INCREASE(
            "Double difficulty increase",
            0.2,
            0.2,
            "The point requirements for each level increase by another 20%, %reward%. Duo/trio streamer challenge only.",
            ChallengeModifier::onlyAllowedInDuoTrioStreamerChallenge),
    NO_HELP(
            "No help",
            0.2,
            0,
            "Supporters cannot join your division, %reward%. Solo streamer challenge only.",
            ChallengeModifier::onlyAllowedInSoloStreamerChallenge),
    NO_GIVING_UP(
            "No giving up",
            0.2,
            0,
            "You cannot end the challenge early, %reward%. This does not affect your ability to pause the challenge.",
            ChallengeModifier::alwaysAllowed),
    NO_SAFETY_NET(
            "No safety net",
            0.5,
            0,
            "You do not gain any extra lives, %reward%.",
            ChallengeModifier::alwaysAllowed);

    @Serial
    private static final long serialVersionUID = -4027175656426943041L;

    private final String displayName;
    private final double bonusModifier;
    private final double pointRequirementModifier;
    private final String descriptionWithPlaceholder;
    private final Predicate<Integer> restrictionOnNumberOfPlayers;

    ChallengeModifier(
            String displayName, double bonusModifier, double pointRequirementModifier,
            String descriptionWithPlaceholder, Predicate<Integer> restrictionOnNumberOfPlayers) {
        this.displayName = displayName;
        this.bonusModifier = bonusModifier;
        this.pointRequirementModifier = pointRequirementModifier;
        this.descriptionWithPlaceholder = descriptionWithPlaceholder;
        this.restrictionOnNumberOfPlayers = restrictionOnNumberOfPlayers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBonusModifier() {
        return bonusModifier;
    }

    public double getPointRequirementModifier() {
        return pointRequirementModifier;
    }

    public boolean allowsNumberOfPlayers(int numberOfPlayers) {
        return restrictionOnNumberOfPlayers.test(numberOfPlayers);
    }

    @Override
    public String toString() {
        return displayName.concat(": ")
                .concat(descriptionWithPlaceholder.replace("%reward%", getTextForBonusModifier()));
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

    private static boolean onlyAllowedInDuoTrioStreamerChallenge(int numberOfPlayers) {
        return numberOfPlayers > 1;
    }

    private static boolean onlyAllowedInSoloStreamerChallenge(int numberOfPlayers) {
        return numberOfPlayers == 1;
    }

    private static boolean alwaysAllowed(int numberOfPlayers) {
        return numberOfPlayers > 0;
    }
}
