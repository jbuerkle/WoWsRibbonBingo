package bingo;

import ribbons.Ribbon;
import ribbons.RibbonResult;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoResult {
    private final boolean battleshipModifierEnabled;
    private final Set<RibbonResult> ribbonResultSet;

    public BingoResult(boolean battleshipModifierEnabled) {
        this.battleshipModifierEnabled = battleshipModifierEnabled;
        this.ribbonResultSet = new HashSet<>();
    }

    public void addRibbonResult(Ribbon ribbon, int amount) {
        RibbonResult ribbonResult = new RibbonResult(ribbon, amount);
        if (amount > 0) {
            ribbonResultSet.add(ribbonResult);
        } else {
            ribbonResultSet.remove(ribbonResult);
        }
    }

    public int getPointResult() {
        return ribbonResultSet.stream().map(this::getPointValue).reduce(Integer::sum).orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Ribbon Bingo result: ");
        int resultsAdded = 0;
        for (RibbonResult ribbonResult : getSortedRibbonResultSet()) {
            if (resultsAdded > 0) {
                stringBuilder.append(" + ");
            }
            stringBuilder.append(ribbonResult.getAsString(battleshipModifierEnabled));
            resultsAdded++;
        }
        if (resultsAdded > 0) {
            stringBuilder.append(" = ");
        }
        stringBuilder.append(getPointResult()).append(" points");
        return stringBuilder.toString();
    }

    private List<RibbonResult> getSortedRibbonResultSet() {
        return ribbonResultSet.stream().sorted(Comparator.comparingInt(this::getPointValue)).toList().reversed();
    }

    private int getPointValue(RibbonResult ribbonResult) {
        return ribbonResult.getPointValue(battleshipModifierEnabled);
    }
}
