package bingo.game.restrictions.impl;

import bingo.game.restrictions.ShipRestriction;
import bingo.game.ships.MainArmamentType;

import java.io.Serial;

public record BannedMainArmamentType(MainArmamentType mainArmamentType) implements ShipRestriction {
    @Serial
    private static final long serialVersionUID = -5528515174847401056L;

    @Override
    public String getDisplayText() {
        String mainArmamentText = mainArmamentType.getDisplayText().toLowerCase();
        return "cannot use ships with %s as main armament".formatted(mainArmamentText);
    }

    @Override
    public boolean allowsMainArmamentType(MainArmamentType mainArmamentType) {
        return !mainArmamentType.equals(this.mainArmamentType);
    }
}
