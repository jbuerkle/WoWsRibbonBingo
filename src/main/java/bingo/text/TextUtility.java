package bingo.text;

public class TextUtility {

    private TextUtility() {
    }

    public static String getAsPercentage(double bonusModifier) {
        return Math.round(bonusModifier * 100) + "%";
    }
}
