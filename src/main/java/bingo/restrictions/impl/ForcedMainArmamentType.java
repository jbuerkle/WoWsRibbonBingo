package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;

public record ForcedMainArmamentType(MainArmamentType mainArmamentType) implements ShipRestriction {

    @Override
    public String getDisplayText() {
        String mainArmamentText = mainArmamentType.getDisplayText().toLowerCase();
        return "You are forced to use a ship with %s as main armament in the current level".formatted(mainArmamentText);
    }
}
