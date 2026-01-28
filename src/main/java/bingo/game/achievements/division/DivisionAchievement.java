package bingo.game.achievements.division;

import bingo.game.math.terms.impl.LabeledTerm;
import bingo.game.math.terms.impl.Literal;
import bingo.game.math.terms.impl.TermWithPoints;
import bingo.game.text.TextUtility;

import java.io.Serial;
import java.io.Serializable;

public enum DivisionAchievement implements Serializable {
    GENERAL_OFFENSIVE("General Offensive", 100, 0.5),
    BROTHERS_IN_ARMS("Brothers-in-Arms", 150, 0),
    STRIKE_TEAM("Strike Team", 150, 0.5),
    COORDINATED_ATTACK("Coordinated Attack", 150, 0.5),
    SHOULDER_TO_SHOULDER("Shoulder to Shoulder", 150, 0.5);

    @Serial
    private static final long serialVersionUID = -2677701939598808090L;

    private final String displayText;
    private final int pointValue;
    private final double bonusModifierForDuos;

    DivisionAchievement(String displayText, int pointValue, double bonusModifierForDuos) {
        this.displayText = displayText;
        this.pointValue = pointValue;
        this.bonusModifierForDuos = bonusModifierForDuos;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getPointValue(int numberOfPlayers) {
        if (numberOfPlayers < 2) {
            return 0;
        } else if (numberOfPlayers == 2) {
            return (int) Math.round(pointValue * (1 + bonusModifierForDuos));
        } else {
            return pointValue;
        }
    }

    public double getBonusModifierForDuos() {
        return bonusModifierForDuos;
    }

    @Override
    public String toString() {
        String achievementAsString =
                new LabeledTerm(displayText, new TermWithPoints(new Literal(pointValue))).getAsString();
        if (bonusModifierForDuos != 0) {
            achievementAsString =
                    achievementAsString.concat(" (+%s bonus points for duos)".formatted(TextUtility.getAsPercentage(
                            bonusModifierForDuos)));
        }
        return achievementAsString;
    }

    public static String getAllAchievementsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (DivisionAchievement achievement : DivisionAchievement.values()) {
            stringBuilder.append("- ").append(achievement.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
