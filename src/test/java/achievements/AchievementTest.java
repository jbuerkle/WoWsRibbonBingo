package achievements;

import achievements.modifiers.PointValueModifier;
import org.junit.jupiter.api.Test;
import ribbons.Ribbon;

import java.util.Iterator;
import java.util.Set;

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
        assertEquals(100, Achievement.KRAKEN_UNLEASHED.getFlatPointValue());
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
        Set<PointValueModifier> arsonistModifiers = Achievement.ARSONIST.getPointValueModifiers();
        assertEquals(1, arsonistModifiers.size());
        PointValueModifier setOnFireModifier = arsonistModifiers.iterator().next();
        assertEquals(Ribbon.SET_ON_FIRE, setOnFireModifier.ribbon());
        assertEquals(0.1, setOnFireModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForCombatScout() {
        Set<PointValueModifier> combatScoutModifiers = Achievement.COMBAT_SCOUT.getPointValueModifiers();
        assertEquals(1, combatScoutModifiers.size());
        PointValueModifier spottedModifier = combatScoutModifiers.iterator().next();
        assertEquals(Ribbon.SPOTTED, spottedModifier.ribbon());
        assertEquals(0.4, spottedModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForAntiAirDefenseExpert() {
        Set<PointValueModifier> antiAirDefenseExpertModifiers = Achievement.AA_DEFENSE_EXPERT.getPointValueModifiers();
        assertEquals(2, antiAirDefenseExpertModifiers.size());
        Iterator<PointValueModifier> iterator = antiAirDefenseExpertModifiers.iterator();
        PointValueModifier aircraftShotDownModifier = iterator.next();
        assertEquals(Ribbon.AIRCRAFT_SHOT_DOWN, aircraftShotDownModifier.ribbon());
        assertEquals(0.4, aircraftShotDownModifier.bonusModifier());
        PointValueModifier shotDownByFighterModifier = iterator.next();
        assertEquals(Ribbon.SHOT_DOWN_BY_FIGHTER, shotDownByFighterModifier.ribbon());
        assertEquals(0.4, shotDownByFighterModifier.bonusModifier());
    }

    @Test
    void getPointValueModifiersShouldReturnCorrectListForWitherer() {
        Set<PointValueModifier> withererModifiers = Achievement.WITHERER.getPointValueModifiers();
        assertEquals(2, withererModifiers.size());
        Iterator<PointValueModifier> iterator = withererModifiers.iterator();
        PointValueModifier setOnFireModifier = iterator.next();
        assertEquals(Ribbon.SET_ON_FIRE, setOnFireModifier.ribbon());
        assertEquals(0.2, setOnFireModifier.bonusModifier());
        PointValueModifier causedFloodingModifier = iterator.next();
        assertEquals(Ribbon.CAUSED_FLOODING, causedFloodingModifier.ribbon());
        assertEquals(0.1, causedFloodingModifier.bonusModifier());
    }

    @Test
    void getAllAchievementsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Arsonist: 20 points + 10% bonus points for all 'Set on fire' ribbons
                - AA Defense Expert: 5 points + 40% bonus points for all 'Aircraft shot down' ribbons + 40% bonus points for all 'Shot down by fighter' ribbons
                - Close Quarters Expert: 25 points
                - Devastating Strike: 50 points
                - Double Strike: 75 points
                - Die-Hard: 50 points
                - First Blood: 50 points
                - It's Just a Flesh Wound: 50 points
                - Fireproof: 50 points
                - Unsinkable: 50 points
                - Dreadnought: 50 points
                - Combat Scout: 90 points + 40% bonus points for all 'Spotted' ribbons
                - Confederate: 100 points
                - High Caliber: 100 points
                - Kraken Unleashed: 100 points
                - Solo Warrior: 300 points
                - Witherer: 40 points + 20% bonus points for all 'Set on fire' ribbons + 10% bonus points for all 'Caused flooding' ribbons
                """;
        assertEquals(expectedString, Achievement.getAllAchievementsListedAsString());
    }
}
