package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;

import java.io.Serial;

public record BannedMainArmamentType(MainArmamentType mainArmamentType) implements ShipRestriction {
    @Serial
    private static final long serialVersionUID = -5528515174847401056L;

    @Override
    public String getDisplayText() {
        String mainArmamentText = mainArmamentType.getDisplayText().toLowerCase();
        return "Ships with %s as main armament are banned from use in the current level".formatted(mainArmamentText);
    }
}
