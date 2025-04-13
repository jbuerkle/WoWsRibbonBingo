package bingo.achievements;

import bingo.math.terms.Term;
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
    private final AchievementResult oneCombatScoutAchievement = new AchievementResult(Achievement.COMBAT_SCOUT, 1);
    private final AchievementResult oneKrakenUnleashedAchievement =
            new AchievementResult(Achievement.KRAKEN_UNLEASHED, 1);
    private final AchievementResult oneAntiAirDefenseExpertAchievement =
            new AchievementResult(Achievement.AA_DEFENSE_EXPERT, 1);
    private Set<RibbonResult> ribbonResultSet;

    @BeforeEach
    void setup() {
        ribbonResultSet = new HashSet<>();
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneArsonistAchievement() {
        Term term = oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(30, term.getValue());
        assertEquals("Arsonist: 30 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneArsonistAchievementWithTenSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        Term term = oneArsonistAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(50, term.getValue());
        assertEquals("Arsonist: 30 points + (Set on fire: 10 * 20 points) * 0.1", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoArsonistAchievements() {
        Term term = twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(60, term.getValue());
        assertEquals("Arsonist: 2 * 30 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoArsonistAchievementsWithTwentySetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        Term term = twoArsonistAchievements.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(140, term.getValue());
        assertEquals("Arsonist: 2 * (30 points + (Set on fire: 20 * 20 points) * 0.1)", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievement() {
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultSet.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultSet.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(30, term.getValue());
        assertEquals("Witherer: 30 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithFifteenSetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(120, term.getValue());
        assertEquals("Witherer: 30 points + (Set on fire: 15 * 20 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithFifteenSetOnFireRibbonsAndFiveCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(180, term.getValue());
        assertEquals(
                "Witherer: 30 points + (Set on fire: 15 * 20 points) * 0.3 + (Caused flooding: 5 * 40 points) * 0.3",
                term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithThirtySetOnFireRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SET_ON_FIRE, 30));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(210, term.getValue());
        assertEquals("Witherer: 30 points + (Set on fire: 30 * 20 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithTenCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 10));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Witherer: 30 points + (Caused flooding: 10 * 40 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithTwentyCausedFloodingRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 20));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultSet, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(270, term.getValue());
        assertEquals("Witherer: 30 points + (Caused flooding: 20 * 40 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithSmallCaliberGunsAndFiveSpottedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SPOTTED, 5));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultSet, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 5 * 30 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithSmallCaliberGunsAndTenSpottedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SPOTTED, 10));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultSet, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(240, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 10 * 30 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithAircraftAndFiveSpottedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SPOTTED, 5));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultSet, MainArmamentType.AIRCRAFT);
        assertEquals(90, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 5 * 10 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithAircraftAndFifteenSpottedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SPOTTED, 15));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultSet, MainArmamentType.AIRCRAFT);
        assertEquals(150, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 15 * 10 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneKrakenUnleashedAchievementWithFiveDestroyedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.DESTROYED, 5));
        Term term = oneKrakenUnleashedAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Kraken Unleashed: 30 points + (Destroyed: 5 * 120 points) * 0.2", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneKrakenUnleashedAchievementWithTenDestroyedRibbons() {
        ribbonResultSet.add(new RibbonResult(Ribbon.DESTROYED, 10));
        Term term = oneKrakenUnleashedAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(270, term.getValue());
        assertEquals("Kraken Unleashed: 30 points + (Destroyed: 10 * 120 points) * 0.2", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneAntiAirDefenseExpertAchievementWithThirtyFivePlanesShotDown() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SHOT_DOWN_BY_FIGHTER, 10));
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 25));
        Term term = oneAntiAirDefenseExpertAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals(
                "AA Defense Expert: 45 points + (Aircraft shot down: 25 * 10 points) * 0.3 + (Shot down by fighter: 10 * 10 points) * 0.3",
                term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneAntiAirDefenseExpertAchievementWithSeventyPlanesShotDown() {
        ribbonResultSet.add(new RibbonResult(Ribbon.SHOT_DOWN_BY_FIGHTER, 20));
        ribbonResultSet.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 50));
        Term term = oneAntiAirDefenseExpertAchievement.getAsTerm(ribbonResultSet, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(255, term.getValue());
        assertEquals(
                "AA Defense Expert: 45 points + (Aircraft shot down: 50 * 10 points) * 0.3 + (Shot down by fighter: 20 * 10 points) * 0.3",
                term.getAsString());
    }
}
