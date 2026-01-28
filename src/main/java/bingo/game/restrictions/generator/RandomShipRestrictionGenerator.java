package bingo.game.restrictions.generator;

import bingo.game.input.UserInputException;
import bingo.game.restrictions.ShipRestriction;
import bingo.game.restrictions.impl.BannedMainArmamentType;
import bingo.game.restrictions.impl.ForcedMainArmamentType;
import bingo.game.ships.MainArmamentType;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomShipRestrictionGenerator {
    private final List<ShipRestriction> allPossibleRestrictions;
    private final Random random;

    RandomShipRestrictionGenerator(Random random) {
        List<MainArmamentType> allPossibleMainArmamentTypes = List.of(
                MainArmamentType.SMALL_CALIBER_GUNS,
                MainArmamentType.MEDIUM_CALIBER_GUNS,
                MainArmamentType.LARGE_CALIBER_GUNS,
                MainArmamentType.EXTRA_LARGE_CALIBER_GUNS);
        this.allPossibleRestrictions = new LinkedList<>();
        for (MainArmamentType mainArmamentType : allPossibleMainArmamentTypes) {
            allPossibleRestrictions.add(new BannedMainArmamentType(mainArmamentType));
            allPossibleRestrictions.add(new ForcedMainArmamentType(mainArmamentType));
        }
        this.random = random;
    }

    public RandomShipRestrictionGenerator() {
        this(new Random());
    }

    /**
     * @param number any positive integer (including 0).
     * @return a random {@link ShipRestriction} for the chosen number.
     * @throws UserInputException if the number is outside the allowed range.
     */
    public ShipRestriction getForNumber(int number) throws UserInputException {
        if (number < 0) {
            String message = "The number %s is outside the allowed range (not a positive number)".formatted(number);
            throw new UserInputException(message);
        }
        List<ShipRestriction> restrictionsInRandomOrder = getAllPossibleRestrictionsInRandomOrder();
        int index = number % restrictionsInRandomOrder.size();
        return restrictionsInRandomOrder.get(index);
    }

    private List<ShipRestriction> getAllPossibleRestrictionsInRandomOrder() {
        List<ShipRestriction> copyOfAllRestrictions = new LinkedList<>(allPossibleRestrictions);
        List<ShipRestriction> restrictionsInRandomOrder = new LinkedList<>();
        while (!copyOfAllRestrictions.isEmpty()) {
            int randomIndex = random.nextInt(0, copyOfAllRestrictions.size());
            ShipRestriction shipRestriction = copyOfAllRestrictions.remove(randomIndex);
            restrictionsInRandomOrder.add(shipRestriction);
        }
        return restrictionsInRandomOrder;
    }
}
