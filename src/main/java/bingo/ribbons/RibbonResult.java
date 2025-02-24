package bingo.ribbons;

import bingo.ships.MainArmamentType;

import java.util.Objects;

public record RibbonResult(Ribbon ribbon, int amount) {

    public int getPointValue(MainArmamentType mainArmamentType) {
        return ribbon.getPointValue(mainArmamentType) * amount;
    }

    public String getAsString(MainArmamentType mainArmamentType) {
        return ribbon.getDisplayText() + ": " + amount + " * " + ribbon.getPointValue(mainArmamentType) + " points";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        RibbonResult that = (RibbonResult) object;
        return ribbon == that.ribbon;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ribbon);
    }
}
