package ribbons;

import java.util.Objects;

public record RibbonResult(Ribbon ribbon, int amount) {

    public int getPointValue(boolean battleshipModifierEnabled) {
        return ribbon.getPointValue(battleshipModifierEnabled) * amount;
    }

    public String getAsString(boolean battleshipModifierEnabled) {
        return ribbon.getDisplayText() + ": " + amount + " * " + ribbon.getPointValue(battleshipModifierEnabled) + " points";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RibbonResult that = (RibbonResult) o;
        return ribbon == that.ribbon;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ribbon);
    }
}
