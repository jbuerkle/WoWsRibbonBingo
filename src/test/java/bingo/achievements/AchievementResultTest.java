package bingo.achievements;

import bingo.ribbons.Ribbon;
import bingo.ribbons.RibbonResult;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementResultTest {
    private final AchievementResult oneArsonistAchievement = new AchievementResult(Achievement.ARSONIST, 1);
    private final AchievementResult twoArsonistAchievements = new AchievementResult(Achievement.ARSONIST, 2);
    private final AchievementResult oneWithererAchievement = new AchievementResult(Achievement.WITHERER, 1);
    private Set<RibbonResult> ribbonResultSet;

    @BeforeEach
    void setup() {
        ribbonResultSet = new HashSet<>();
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOneArsonistAchievement() {
        int pointValue = oneArsonistAchievement.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(20, pointValue);
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOneArsonistAchievementWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        int pointValue = oneArsonistAchievement.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(40, pointValue);
    }

    @Test
    void getPointValueShouldReturnCorrectValueForTwoArsonistAchievements() {
        int pointValue = twoArsonistAchievements.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(40, pointValue);
    }

    @Test
    void getPointValueShouldReturnCorrectValueForTwoArsonistAchievementsWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        int pointValue = twoArsonistAchievements.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(120, pointValue);
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOneWithererAchievement() {
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultSet.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultSet.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        int pointValue = oneWithererAchievement.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(40, pointValue);
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOneWithererAchievementWithSetOnFireAndCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        int pointValue = oneWithererAchievement.getPointValue(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(120, pointValue);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneArsonistAchievement() {
        String resultString = oneArsonistAchievement.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Arsonist: 20 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneArsonistAchievementWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        String resultString = oneArsonistAchievement.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Arsonist: 20 + 200 * 0.1 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForTwoArsonistAchievements() {
        String resultString = twoArsonistAchievements.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Arsonist: 2 * 20 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForTwoArsonistAchievementsWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        String resultString = twoArsonistAchievements.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Arsonist: 2 * (20 + 400 * 0.1) points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneWithererAchievement() {
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultSet.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultSet.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        String resultString = oneWithererAchievement.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Witherer: 40 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneWithererAchievementWithSetOnFireAndCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        String resultString = oneWithererAchievement.getAsString(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals("Witherer: 40 + 300 * 0.2 + 200 * 0.1 points", resultString);
    }
}
