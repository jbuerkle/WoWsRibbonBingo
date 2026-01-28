package bingo.game.restrictions.impl;

import bingo.game.restrictions.ShipRestriction;
import bingo.game.ships.MainArmamentType;

import java.io.Serial;

public record ForcedMainArmamentType(MainArmamentType mainArmamentType) implements ShipRestriction {
    @Serial
    private static final long serialVersionUID = -4254381769352003990L;

    @Override
    public String getDisplayText() {
        String mainArmamentText = mainArmamentType.getDisplayText().toLowerCase();
        return "You are forced to use a ship with %s as main armament in the current level".formatted(mainArmamentText);
    }

    @Override
    public boolean allowsMainArmamentType(MainArmamentType mainArmamentType) {
        return mainArmamentType.equals(this.mainArmamentType);
    }
}
