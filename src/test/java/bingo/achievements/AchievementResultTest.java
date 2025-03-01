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
    void getValueShouldReturnCorrectValueForOneArsonistAchievement() {
        double pointValue =
                oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(20, pointValue);
    }

    @Test
    void getValueShouldReturnCorrectValueForOneArsonistAchievementWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        double pointValue =
                oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(40, pointValue);
    }

    @Test
    void getValueShouldReturnCorrectValueForTwoArsonistAchievements() {
        double pointValue =
                twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(40, pointValue);
    }

    @Test
    void getValueShouldReturnCorrectValueForTwoArsonistAchievementsWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        double pointValue =
                twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(120, pointValue);
    }

    @Test
    void getValueShouldReturnCorrectValueForOneWithererAchievement() {
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultSet.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultSet.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        double pointValue =
                oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(40, pointValue);
    }

    @Test
    void getValueShouldReturnCorrectValueForOneWithererAchievementWithSetOnFireAndCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        double pointValue =
                oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getValue();
        assertEquals(120, pointValue);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneArsonistAchievement() {
        String resultString =
                oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals("Arsonist: 20 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneArsonistAchievementWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        String resultString =
                oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals("Arsonist: 20 points + (Set on fire: 10 * 20 points) * 0.1", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForTwoArsonistAchievements() {
        String resultString =
                twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals("Arsonist: 2 * 20 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForTwoArsonistAchievementsWithSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        String resultString =
                twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals("Arsonist: 2 * (20 points + (Set on fire: 20 * 20 points) * 0.1)", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneWithererAchievement() {
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultSet.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultSet.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        String resultString =
                oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals("Witherer: 40 points", resultString);
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextForOneWithererAchievementWithSetOnFireAndCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        String resultString =
                oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS).getAsString();
        assertEquals(
                "Witherer: 40 points + (Set on fire: 15 * 20 points) * 0.2 + (Caused flooding: 5 * 40 points) * 0.1",
                resultString);
    }
}
