package bingo.game.restrictions;

import bingo.game.ships.MainArmamentType;

import java.io.Serializable;

public interface ShipRestriction extends Serializable {

    String getDisplayText();

    boolean allowsMainArmamentType(MainArmamentType mainArmamentType);
}
