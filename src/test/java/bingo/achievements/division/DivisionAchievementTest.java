package bingo.achievements.division;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisionAchievementTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("General Offensive", DivisionAchievement.GENERAL_OFFENSIVE.getDisplayText());
        assertEquals("Brothers-in-Arms", DivisionAchievement.BROTHERS_IN_ARMS.getDisplayText());
        assertEquals("Strike Team", DivisionAchievement.STRIKE_TEAM.getDisplayText());
        assertEquals("Coordinated Attack", DivisionAchievement.COORDINATED_ATTACK.getDisplayText());
        assertEquals("Shoulder to Shoulder", DivisionAchievement.SHOULDER_TO_SHOULDER.getDisplayText());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOnePlayer() {
        final int numberOfPlayers = 1;
        assertEquals(0, DivisionAchievement.GENERAL_OFFENSIVE.getPointValue(numberOfPlayers));
        assertEquals(0, DivisionAchievement.BROTHERS_IN_ARMS.getPointValue(numberOfPlayers));
        assertEquals(0, DivisionAchievement.STRIKE_TEAM.getPointValue(numberOfPlayers));
        assertEquals(0, DivisionAchievement.COORDINATED_ATTACK.getPointValue(numberOfPlayers));
        assertEquals(0, DivisionAchievement.SHOULDER_TO_SHOULDER.getPointValue(numberOfPlayers));
    }

    @Test
    void getPointValueShouldReturnCorrectValueForTwoPlayers() {
        final int numberOfPlayers = 2;
        assertEquals(150, DivisionAchievement.GENERAL_OFFENSIVE.getPointValue(numberOfPlayers));
        assertEquals(150, DivisionAchievement.BROTHERS_IN_ARMS.getPointValue(numberOfPlayers));
        assertEquals(375, DivisionAchievement.STRIKE_TEAM.getPointValue(numberOfPlayers));
        assertEquals(300, DivisionAchievement.COORDINATED_ATTACK.getPointValue(numberOfPlayers));
        assertEquals(375, DivisionAchievement.SHOULDER_TO_SHOULDER.getPointValue(numberOfPlayers));
    }

    @Test
    void getPointValueShouldReturnCorrectValueForThreePlayers() {
        final int numberOfPlayers = 3;
        assertEquals(100, DivisionAchievement.GENERAL_OFFENSIVE.getPointValue(numberOfPlayers));
        assertEquals(150, DivisionAchievement.BROTHERS_IN_ARMS.getPointValue(numberOfPlayers));
        assertEquals(250, DivisionAchievement.STRIKE_TEAM.getPointValue(numberOfPlayers));
        assertEquals(200, DivisionAchievement.COORDINATED_ATTACK.getPointValue(numberOfPlayers));
        assertEquals(250, DivisionAchievement.SHOULDER_TO_SHOULDER.getPointValue(numberOfPlayers));
    }

    @Test
    void getBonusModifierForDuosShouldReturnCorrectValue() {
        assertEquals(0.5, DivisionAchievement.GENERAL_OFFENSIVE.getBonusModifierForDuos());
        assertEquals(0, DivisionAchievement.BROTHERS_IN_ARMS.getBonusModifierForDuos());
        assertEquals(0.5, DivisionAchievement.STRIKE_TEAM.getBonusModifierForDuos());
        assertEquals(0.5, DivisionAchievement.COORDINATED_ATTACK.getBonusModifierForDuos());
        assertEquals(0.5, DivisionAchievement.SHOULDER_TO_SHOULDER.getBonusModifierForDuos());
    }

    @Test
    void getAllAchievementsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - General Offensive: 100 points (+50% bonus points for duos)
                - Brothers-in-Arms: 150 points
                - Strike Team: 250 points (+50% bonus points for duos)
                - Coordinated Attack: 200 points (+50% bonus points for duos)
                - Shoulder to Shoulder: 250 points (+50% bonus points for duos)
                """;
        assertEquals(expectedString, DivisionAchievement.getAllAchievementsListedAsString());
    }
}
