package bingo.achievements.division;

import bingo.math.terms.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisionAchievementResultTest {
    private final DivisionAchievementResult oneGeneralOffensiveAchievement =
            new DivisionAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 1);
    private final DivisionAchievementResult twoGeneralOffensiveAchievements =
            new DivisionAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
    private final DivisionAchievementResult oneBrothersInArmsAchievement =
            new DivisionAchievementResult(DivisionAchievement.BROTHERS_IN_ARMS, 1);
    private final DivisionAchievementResult oneCoordinatedAttackAchievement =
            new DivisionAchievementResult(DivisionAchievement.COORDINATED_ATTACK, 1);

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneGeneralOffensiveAchievementWithTwoPlayers() {
        Term term = oneGeneralOffensiveAchievement.getAsTerm(2);
        assertEquals(150, term.getValue());
        assertEquals("General Offensive: 150 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneGeneralOffensiveAchievementWithThreePlayers() {
        Term term = oneGeneralOffensiveAchievement.getAsTerm(3);
        assertEquals(100, term.getValue());
        assertEquals("General Offensive: 100 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoGeneralOffensiveAchievementsWithTwoPlayers() {
        Term term = twoGeneralOffensiveAchievements.getAsTerm(2);
        assertEquals(300, term.getValue());
        assertEquals("General Offensive: 2 * 150 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoGeneralOffensiveAchievementsWithThreePlayers() {
        Term term = twoGeneralOffensiveAchievements.getAsTerm(3);
        assertEquals(200, term.getValue());
        assertEquals("General Offensive: 2 * 100 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneBrothersInArmsAchievementWithTwoPlayers() {
        Term term = oneBrothersInArmsAchievement.getAsTerm(2);
        assertEquals(150, term.getValue());
        assertEquals("Brothers-in-Arms: 150 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneBrothersInArmsAchievementWithThreePlayers() {
        Term term = oneBrothersInArmsAchievement.getAsTerm(3);
        assertEquals(150, term.getValue());
        assertEquals("Brothers-in-Arms: 150 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCoordinatedAttackAchievementWithTwoPlayers() {
        Term term = oneCoordinatedAttackAchievement.getAsTerm(2);
        assertEquals(225, term.getValue());
        assertEquals("Coordinated Attack: 225 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCoordinatedAttackAchievementWithThreePlayers() {
        Term term = oneCoordinatedAttackAchievement.getAsTerm(3);
        assertEquals(150, term.getValue());
        assertEquals("Coordinated Attack: 150 points", term.getAsString());
    }
}
