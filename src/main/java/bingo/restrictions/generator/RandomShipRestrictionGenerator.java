package bingo.restrictions.generator;

import bingo.game.input.UserInputException;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.restrictions.impl.ForcedMainArmamentType;
import bingo.ships.MainArmamentType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomShipRestrictionGenerator {
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 100;

    private final Random random;

    private Map<Integer, ShipRestriction> shipRestrictionsByNumber;

    RandomShipRestrictionGenerator(Random random) {
        this.random = random;
    }

    public RandomShipRestrictionGenerator() {
        this(new Random());
    }

    /**
     * @param number a number between 1 and 100.
     * @return a random {@link ShipRestriction} for the chosen number.
     * @throws UserInputException if the number is outside the allowed range.
     */
    public ShipRestriction getForNumber(int number) throws UserInputException {
        if (number < MIN_NUMBER || number > MAX_NUMBER) {
            String message = "The number %s is outside the allowed range (between %s and %s)".formatted(
                    number,
                    MIN_NUMBER,
                    MAX_NUMBER);
            throw new UserInputException(message);
        }
        assignShipRestrictionsToAllNumbers();
        return shipRestrictionsByNumber.get(number);
    }

    Map<Integer, ShipRestriction> getShipRestrictionsByNumber() {
        return shipRestrictionsByNumber;
    }

    void assignShipRestrictionsToAllNumbers() {
        shipRestrictionsByNumber = new HashMap<>();
        int assignedSlots = 0;
        while (assignedSlots < MAX_NUMBER / 2) {
            int number = random.nextInt(MIN_NUMBER, MAX_NUMBER + 1);
            if (numberIsUnassigned(number)) {
                shipRestrictionsByNumber.put(number, new BannedMainArmamentType(getRandomMainArmamentType()));
                assignedSlots++;
            }
        }
        for (int number = MIN_NUMBER; number <= MAX_NUMBER; number++) {
            if (numberIsUnassigned(number)) {
                shipRestrictionsByNumber.put(number, new ForcedMainArmamentType(getRandomMainArmamentType()));
            }
        }
    }

    private boolean numberIsUnassigned(int number) {
        return !shipRestrictionsByNumber.containsKey(number);
    }

    private MainArmamentType getRandomMainArmamentType() {
        int number = random.nextInt(1, 5);
        return switch (number) {
            case 1 -> MainArmamentType.SMALL_CALIBER_GUNS;
            case 2 -> MainArmamentType.MEDIUM_CALIBER_GUNS;
            case 3 -> MainArmamentType.LARGE_CALIBER_GUNS;
            case 4 -> MainArmamentType.EXTRA_LARGE_CALIBER_GUNS;
            default -> throw new IllegalArgumentException("Unexpected value %s from random number generator".formatted(
                    number));
        };
    }
}
