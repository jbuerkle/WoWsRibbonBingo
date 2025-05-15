package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;

public record BannedMainArmamentType(MainArmamentType mainArmamentType) implements ShipRestriction {

    @Override
    public String getDisplayText() {
        String mainArmamentText = mainArmamentType.getDisplayText().toLowerCase();
        return "Ships with %s as main armament are banned from use in the current level".formatted(mainArmamentText);
    }
}
