package bingo.game.restrictions.generator;

import bingo.game.input.UserInputException;
import bingo.game.restrictions.ShipRestriction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomShipRestrictionGeneratorTest {
    private static final int NUMBER_OF_POSSIBLE_RESTRICTIONS = 8;

    @Mock
    private Random mockedRandom;

    @Test
    void getForNumberShouldReturnAllUniqueShipRestrictions() throws UserInputException {
        RandomShipRestrictionGenerator predictableShipRestrictionGenerator = mockRandomNextIntWithPredictableValues();
        Set<String> uniqueDisplayTexts = new HashSet<>();
        for (int number = 1; number <= NUMBER_OF_POSSIBLE_RESTRICTIONS; number++) {
            ShipRestriction shipRestriction = predictableShipRestrictionGenerator.getForNumber(number);
            uniqueDisplayTexts.add(shipRestriction.getDisplayText());
        }
        assertEquals(NUMBER_OF_POSSIBLE_RESTRICTIONS, uniqueDisplayTexts.size());
    }

    @Test
    void getForNumberShouldReturnShipRestrictionForMinimumNumber() throws UserInputException {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        assertNotNull(randomShipRestrictionGenerator.getForNumber(0));
    }

    @Test
    void getForNumberShouldReturnShipRestrictionForLargeNumber() throws UserInputException {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        assertNotNull(randomShipRestrictionGenerator.getForNumber(10000));
    }

    @Test
    void getForNumberShouldThrowUserInputExceptionForNegativeNumber() {
        RandomShipRestrictionGenerator randomShipRestrictionGenerator = new RandomShipRestrictionGenerator();

        UserInputException exception =
                assertThrows(UserInputException.class, () -> randomShipRestrictionGenerator.getForNumber(-1));
        assertEquals("The number -1 is outside the allowed range (not a positive number)", exception.getMessage());
    }

    private RandomShipRestrictionGenerator mockRandomNextIntWithPredictableValues() {
        when(mockedRandom.nextInt(any(Integer.class), any(Integer.class))).thenAnswer(invocationOnMock -> (
                invocationOnMock.getArgument(1, Integer.class) - 1));
        return new RandomShipRestrictionGenerator(mockedRandom);
    }
}
