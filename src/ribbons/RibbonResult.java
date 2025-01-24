package ribbons;

import java.util.Objects;

public record RibbonResult(Ribbon ribbon, int amount) {

    public int getPointValue() {
        return ribbon.getPointValue() * amount;
    }

    @Override
    public String toString() {
        return ribbon.getDisplayText() + ": " + amount + " * " + ribbon.getPointValue() + " points";
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
