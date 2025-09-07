package bingo.text;

public class TextUtility {

    private TextUtility() {
    }

    public static String getAsPercentage(double bonusModifier) {
        return Math.round(bonusModifier * 100) + "%";
    }

    public static String getSuffixForPoints(double pointValue) {
        return pointValue == 1 ? " point" : " points";
    }

    public static String getSuffixForSubs(double numberOfSubs) {
        return numberOfSubs == 1 ? " sub" : " subs";
    }
}
