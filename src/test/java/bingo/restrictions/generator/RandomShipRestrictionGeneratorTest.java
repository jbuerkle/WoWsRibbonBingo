package bingo.restrictions.generator;

import bingo.game.input.UserInputException;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.restrictions.impl.ForcedMainArmamentType;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomShipRestrictionGeneratorTest {
    @Mock
    private Random mockedRandom;

    @Test
    void getShipRestrictionsByNumberShouldReturnFiftyFiftyDistributionOfShipRestrictionTypes() {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        randomShipRestrictionGenerator.assignShipRestrictionsToAllNumbers();
        Map<Integer, ShipRestriction> shipRestrictionsByNumber =
                randomShipRestrictionGenerator.getShipRestrictionsByNumber();

        List<BannedMainArmamentType> bannedMainArmamentTypes = getBannedMainArmamentTypes(shipRestrictionsByNumber);
        List<ForcedMainArmamentType> forcedMainArmamentTypes = getForcedMainArmamentTypes(shipRestrictionsByNumber);
        assertEquals(100, shipRestrictionsByNumber.size());
        assertEquals(50, bannedMainArmamentTypes.size());
        assertEquals(50, forcedMainArmamentTypes.size());
    }

    @Test
    void getShipRestrictionsByNumberShouldReturnFourDifferentMainArmamentTypes() {
        RandomShipRestrictionGenerator predictableShipRestrictionGenerator = mockRandomNextIntWithPredictableValues();

        predictableShipRestrictionGenerator.assignShipRestrictionsToAllNumbers();
        Map<Integer, ShipRestriction> shipRestrictionsByNumber =
                predictableShipRestrictionGenerator.getShipRestrictionsByNumber();

        List<BannedMainArmamentType> bannedMainArmamentTypes = getBannedMainArmamentTypes(shipRestrictionsByNumber);
        assertBannedMainArmamentTypeIs(MainArmamentType.SMALL_CALIBER_GUNS, bannedMainArmamentTypes.getFirst());
        assertBannedMainArmamentTypeIs(MainArmamentType.MEDIUM_CALIBER_GUNS, bannedMainArmamentTypes.get(1));
        assertBannedMainArmamentTypeIs(MainArmamentType.LARGE_CALIBER_GUNS, bannedMainArmamentTypes.get(2));
        assertBannedMainArmamentTypeIs(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS, bannedMainArmamentTypes.get(3));
        List<ForcedMainArmamentType> forcedMainArmamentTypes = getForcedMainArmamentTypes(shipRestrictionsByNumber);
        assertForcedMainArmamentTypeIs(MainArmamentType.SMALL_CALIBER_GUNS, forcedMainArmamentTypes.get(2));
        assertForcedMainArmamentTypeIs(MainArmamentType.MEDIUM_CALIBER_GUNS, forcedMainArmamentTypes.get(3));
        assertForcedMainArmamentTypeIs(MainArmamentType.LARGE_CALIBER_GUNS, forcedMainArmamentTypes.get(4));
        assertForcedMainArmamentTypeIs(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS, forcedMainArmamentTypes.get(5));
    }

    @Test
    void getForNumberShouldReturnShipRestrictionsForMinAndMaxNumber() throws UserInputException {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        assertNotNull(randomShipRestrictionGenerator.getForNumber(1));
        assertNotNull(randomShipRestrictionGenerator.getForNumber(100));
    }

    @Test
    void getForNumberZeroShouldThrowUserInputException() {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        UserInputException exception =
                assertThrows(UserInputException.class, () -> randomShipRestrictionGenerator.getForNumber(0));
        assertEquals("The number 0 is outside the allowed range (between 1 and 100)", exception.getMessage());
    }

    @Test
    void getForNumberOneHundredOneShouldThrowUserInputException() {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        UserInputException exception =
                assertThrows(UserInputException.class, () -> randomShipRestrictionGenerator.getForNumber(101));
        assertEquals("The number 101 is outside the allowed range (between 1 and 100)", exception.getMessage());
    }

    private void assertBannedMainArmamentTypeIs(
            MainArmamentType expectedMainArmamentType, BannedMainArmamentType bannedMainArmamentType) {
        assertEquals(expectedMainArmamentType, bannedMainArmamentType.mainArmamentType());
    }

    private void assertForcedMainArmamentTypeIs(
            MainArmamentType expectedMainArmamentType, ForcedMainArmamentType forcedMainArmamentType) {
        assertEquals(expectedMainArmamentType, forcedMainArmamentType.mainArmamentType());
    }

    private List<BannedMainArmamentType> getBannedMainArmamentTypes(
            Map<Integer, ShipRestriction> shipRestrictionsByNumber) {
        return shipRestrictionsByNumber.values()
                .stream()
                .filter(shipRestriction -> shipRestriction instanceof BannedMainArmamentType)
                .map(shipRestriction -> (BannedMainArmamentType) shipRestriction)
                .toList();
    }

    private List<ForcedMainArmamentType> getForcedMainArmamentTypes(
            Map<Integer, ShipRestriction> shipRestrictionsByNumber) {
        return shipRestrictionsByNumber.values()
                .stream()
                .filter(shipRestriction -> shipRestriction instanceof ForcedMainArmamentType)
                .map(shipRestriction -> (ForcedMainArmamentType) shipRestriction)
                .toList();
    }

    private RandomShipRestrictionGenerator mockRandomNextIntWithPredictableValues() {
        AtomicInteger counterForRange100 = new AtomicInteger(0);
        AtomicInteger counterForRange4 = new AtomicInteger(0);
        when(mockedRandom.nextInt(1, 101)).thenAnswer(_ -> (counterForRange100.getAndIncrement() % 100) + 1);
        when(mockedRandom.nextInt(1, 5)).thenAnswer(_ -> (counterForRange4.getAndIncrement() % 4) + 1);
        return new RandomShipRestrictionGenerator(mockedRandom);
    }
}
