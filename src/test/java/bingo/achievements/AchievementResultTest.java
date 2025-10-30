package bingo.achievements;

import bingo.math.terms.Term;
import bingo.ribbons.Ribbon;
import bingo.ribbons.RibbonResult;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

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
    private List<RibbonResult> ribbonResultList;

    @BeforeEach
    void setup() {
        ribbonResultList = new LinkedList<>();
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneArsonistAchievement() {
        Term term = oneArsonistAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(30, term.getValue());
        assertEquals("Arsonist: 30 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneArsonistAchievementWithTenSetOnFireRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SET_ON_FIRE, 10));
        Term term = oneArsonistAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(50, term.getValue());
        assertEquals("Arsonist: 30 points + (Set on fire: 10 * 20 points) * 0.1", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoArsonistAchievements() {
        Term term = twoArsonistAchievements.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(60, term.getValue());
        assertEquals("Arsonist: 2 * 30 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForTwoArsonistAchievementsWithTwentySetOnFireRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SET_ON_FIRE, 20));
        Term term = twoArsonistAchievements.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(140, term.getValue());
        assertEquals("Arsonist: 2 * (30 points + (Set on fire: 20 * 20 points) * 0.1)", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievement() {
        ribbonResultList.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 30));
        ribbonResultList.add(new RibbonResult(Ribbon.MAIN_GUN_HIT, 300));
        ribbonResultList.add(new RibbonResult(Ribbon.TORPEDO_HIT, 10));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(60, term.getValue());
        assertEquals("Witherer: 60 points", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithFifteenSetOnFireRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Witherer: 60 points + (Set on fire: 15 * 20 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithFifteenSetOnFireRibbonsAndFiveCausedFloodingRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SET_ON_FIRE, 15));
        ribbonResultList.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 5));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(210, term.getValue());
        assertEquals(
                "Witherer: 60 points + (Set on fire: 15 * 20 points) * 0.3 + (Caused flooding: 5 * 40 points) * 0.3",
                term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithThirtySetOnFireRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SET_ON_FIRE, 30));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(240, term.getValue());
        assertEquals("Witherer: 60 points + (Set on fire: 30 * 20 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithTenCausedFloodingRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 10));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(180, term.getValue());
        assertEquals("Witherer: 60 points + (Caused flooding: 10 * 40 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneWithererAchievementWithTwentyCausedFloodingRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.CAUSED_FLOODING, 20));
        Term term = oneWithererAchievement.getAsTerm(ribbonResultList, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(300, term.getValue());
        assertEquals("Witherer: 60 points + (Caused flooding: 20 * 40 points) * 0.3", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithSmallCaliberGunsAndFiveSpottedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SPOTTED, 5));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultList, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 5 * 30 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithSmallCaliberGunsAndTenSpottedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SPOTTED, 10));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultList, MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals(240, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 10 * 30 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithAircraftAndFiveSpottedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SPOTTED, 5));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultList, MainArmamentType.AIRCRAFT);
        assertEquals(90, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 5 * 10 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneCombatScoutAchievementWithAircraftAndFifteenSpottedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.SPOTTED, 15));
        Term term = oneCombatScoutAchievement.getAsTerm(ribbonResultList, MainArmamentType.AIRCRAFT);
        assertEquals(150, term.getValue());
        assertEquals("Combat Scout: 60 points + (Spotted: 15 * 10 points) * 0.6", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneKrakenUnleashedAchievementWithFiveDestroyedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.DESTROYED, 5));
        Term term = oneKrakenUnleashedAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals("Kraken Unleashed: 30 points + (Destroyed: 5 * 120 points) * 0.2", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneKrakenUnleashedAchievementWithTenDestroyedRibbons() {
        ribbonResultList.add(new RibbonResult(Ribbon.DESTROYED, 10));
        Term term = oneKrakenUnleashedAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(270, term.getValue());
        assertEquals("Kraken Unleashed: 30 points + (Destroyed: 10 * 120 points) * 0.2", term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneAntiAirDefenseExpertAchievementWithThirtyFivePlanesShotDown() {
        ribbonResultList.add(new RibbonResult(Ribbon.SHOT_DOWN_BY_FIGHTER, 10));
        ribbonResultList.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 25));
        Term term = oneAntiAirDefenseExpertAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(150, term.getValue());
        assertEquals(
                "AA Defense Expert: 45 points + (Aircraft shot down: 25 * 10 points) * 0.3 + (Shot down by fighter: 10 * 10 points) * 0.3",
                term.getAsString());
    }

    @Test
    void shouldReturnCorrectValueAndDisplayTextForOneAntiAirDefenseExpertAchievementWithSeventyPlanesShotDown() {
        ribbonResultList.add(new RibbonResult(Ribbon.SHOT_DOWN_BY_FIGHTER, 20));
        ribbonResultList.add(new RibbonResult(Ribbon.AIRCRAFT_SHOT_DOWN, 50));
        Term term = oneAntiAirDefenseExpertAchievement.getAsTerm(ribbonResultList, MainArmamentType.LARGE_CALIBER_GUNS);
        assertEquals(255, term.getValue());
        assertEquals(
                "AA Defense Expert: 45 points + (Aircraft shot down: 50 * 10 points) * 0.3 + (Shot down by fighter: 20 * 10 points) * 0.3",
                term.getAsString());
    }
}
