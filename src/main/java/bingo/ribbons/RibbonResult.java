package bingo.ribbons;

import bingo.ships.MainArmamentType;

public record RibbonResult(Ribbon ribbon, int amount) {

    public int getPointValue(MainArmamentType mainArmamentType) {
        return ribbon.getPointValue(mainArmamentType) * amount;
    }

    public String getAsString(MainArmamentType mainArmamentType) {
        return ribbon.getDisplayText() + ": " + amount + " * " + ribbon.getPointValue(mainArmamentType) + " points";
    }
}
