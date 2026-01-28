package bingo.game.achievements;

import bingo.game.achievements.modifiers.PointValueModifier;
import bingo.game.ribbons.Ribbon;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AchievementTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("It's Just a Flesh Wound", Achievement.ITS_JUST_A_FLESH_WOUND.getDisplayText());
        assertEquals("Kraken Unleashed", Achievement.KRAKEN_UNLEASHED.getDisplayText());
        assertEquals("Die-Hard", Achievement.DIE_HARD.getDisplayText());
    }

    @Test
    void getFlatPointValueShouldReturnCorrectValue() {
        assertEquals(25, Achievement.CLOSE_QUARTERS_EXPERT.getFlatPointValue());
        assertEquals(50, Achievement.DEVASTATING_STRIKE.getFlatPointValue());
        assertEquals(75, Achievement.DOUBLE_STRIKE.getFlatPointValue());
        assertEquals(150, Achievement.HIGH_CALIBER.getFlatPointValue());
        assertEquals(300, Achievement.SOLO_WARRIOR.getFlatPointValue());
    }

    @Test
    void getPointValueModifiersShouldReturnEmptyList() {
        assertTrue(Achievement.CLOSE_QUARTERS_EXPERT.getPointValueModifiers().isEmpty());
        assertTrue(Achievement.DEVASTATING_STRIKE.getPointValueModifiers().isEmpty());
        assertTrue(Achievement.DOUBLE_STRIKE.getPointValueModifiers().isEmpty());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForArsonist() {
        List<PointValueModifier> arsonistModifiers = Achievement.ARSONIST.getPointValueModifiers();
        assertEquals(1, arsonistModifiers.size());
        PointValueModifier setOnFireModifier = arsonistModifiers.getFirst();
        assertEquals(Ribbon.SET_ON_FIRE, setOnFireModifier.ribbon());
        assertEquals(0.1, setOnFireModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForKrakenUnleashed() {
        List<PointValueModifier> krakenUnleashedModifiers = Achievement.KRAKEN_UNLEASHED.getPointValueModifiers();
        assertEquals(1, krakenUnleashedModifiers.size());
        PointValueModifier destroyedModifier = krakenUnleashedModifiers.getFirst();
        assertEquals(Ribbon.DESTROYED, destroyedModifier.ribbon());
        assertEquals(0.2, destroyedModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForCombatScout() {
        List<PointValueModifier> combatScoutModifiers = Achievement.COMBAT_SCOUT.getPointValueModifiers();
        assertEquals(1, combatScoutModifiers.size());
        PointValueModifier spottedModifier = combatScoutModifiers.getFirst();
        assertEquals(Ribbon.SPOTTED, spottedModifier.ribbon());
        assertEquals(0.6, spottedModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForAntiAirDefenseExpert() {
        List<PointValueModifier> antiAirDefenseExpertModifiers = Achievement.AA_DEFENSE_EXPERT.getPointValueModifiers();
        assertEquals(2, antiAirDefenseExpertModifiers.size());
        Iterator<PointValueModifier> iterator = antiAirDefenseExpertModifiers.iterator();
        PointValueModifier aircraftShotDownModifier = iterator.next();
        assertEquals(Ribbon.AIRCRAFT_SHOT_DOWN, aircraftShotDownModifier.ribbon());
        assertEquals(0.3, aircraftShotDownModifier.bonusModifier());
        PointValueModifier shotDownByFighterModifier = iterator.next();
        assertEquals(Ribbon.SHOT_DOWN_BY_FIGHTER, shotDownByFighterModifier.ribbon());
        assertEquals(0.3, shotDownByFighterModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForWitherer() {
        List<PointValueModifier> withererModifiers = Achievement.WITHERER.getPointValueModifiers();
        assertEquals(2, withererModifiers.size());
        Iterator<PointValueModifier> iterator = withererModifiers.iterator();
        PointValueModifier setOnFireModifier = iterator.next();
        assertEquals(Ribbon.SET_ON_FIRE, setOnFireModifier.ribbon());
        assertEquals(0.3, setOnFireModifier.bonusModifier());
        PointValueModifier causedFloodingModifier = iterator.next();
        assertEquals(Ribbon.CAUSED_FLOODING, causedFloodingModifier.ribbon());
        assertEquals(0.3, causedFloodingModifier.bonusModifier());
    }

    @Test
    void getAllAchievementsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Arsonist: 30 points + 10% bonus points for all 'Set on fire' ribbons
                - AA Defense Expert: 45 points + 30% bonus points for all 'Aircraft shot down' ribbons + 30% bonus points for all 'Shot down by fighter' ribbons
                - Close Quarters Expert: 25 points
                - Devastating Strike: 50 points
                - Double Strike: 75 points
                - Die-Hard: 50 points
                - First Blood: 50 points
                - It's Just a Flesh Wound: 50 points
                - Fireproof: 50 points
                - Unsinkable: 50 points
                - Dreadnought: 50 points
                - Combat Scout: 60 points + 60% bonus points for all 'Spotted' ribbons
                - Confederate: 150 points
                - High Caliber: 150 points
                - Kraken Unleashed: 30 points + 20% bonus points for all 'Destroyed' ribbons
                - Solo Warrior: 300 points
                - Witherer: 60 points + 30% bonus points for all 'Set on fire' ribbons + 30% bonus points for all 'Caused flooding' ribbons
                """;
        assertEquals(expectedString, Achievement.getAllAchievementsListedAsString());
    }
}
