package bingo.game.results.division;

import bingo.game.achievements.division.DivisionAchievement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SharedDivisionAchievementsTest {

    @Test
    void getPointValueShouldReturnCorrectValueForOnePlayer() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(1);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        assertEquals(0, divisionAchievements.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForTwoPlayers() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(2);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        assertEquals(300, divisionAchievements.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForThreePlayers() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        assertEquals(200, divisionAchievements.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOverwrittenAchievementResult() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 1);
        assertEquals(100, divisionAchievements.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForMultipleAchievementResults() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 1);
        divisionAchievements.addAchievementResult(DivisionAchievement.BROTHERS_IN_ARMS, 1);
        divisionAchievements.addAchievementResult(DivisionAchievement.COORDINATED_ATTACK, 1);
        assertEquals(400, divisionAchievements.getPointValue());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextWhenAddingResultsWithAmountZero() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        divisionAchievements.addAchievementResult(DivisionAchievement.BROTHERS_IN_ARMS, 0);
        divisionAchievements.addAchievementResult(DivisionAchievement.COORDINATED_ATTACK, 0);
        assertEquals("Shared division achievements: 0 points", divisionAchievements.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForZeroAchievementResults() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        assertEquals("Shared division achievements: 0 points", divisionAchievements.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForMultipleAchievementResults() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(2);
        divisionAchievements.addAchievementResult(DivisionAchievement.BROTHERS_IN_ARMS, 1);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        divisionAchievements.addAchievementResult(DivisionAchievement.COORDINATED_ATTACK, 1);
        assertEquals(
                "Shared division achievements: General Offensive: 2 * 150 points + Coordinated Attack: 225 points + Brothers-in-Arms: 150 points = 675 points",
                divisionAchievements.toString());
    }
}
