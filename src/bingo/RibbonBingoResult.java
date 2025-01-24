package bingo;

import ribbons.RibbonResult;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RibbonBingoResult {
    private final Set<RibbonResult> ribbonResultSet;

    public RibbonBingoResult() {
        this.ribbonResultSet = new HashSet<>();
    }

    public void addRibbonResult(RibbonResult ribbonResult) {
        if (ribbonResult.amount() > 0) {
            ribbonResultSet.add(ribbonResult);
        } else {
            ribbonResultSet.remove(ribbonResult);
        }
    }

    public int getPointResult() {
        return ribbonResultSet.stream().map(RibbonResult::getPointValue).reduce(Integer::sum).orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Ribbon Bingo result: ");
        int resultsAdded = 0;
        for (RibbonResult ribbonResult : getSortedRibbonResultSet()) {
            if (resultsAdded > 0) {
                stringBuilder.append(" + ");
            }
            stringBuilder.append(ribbonResult.toString());
            resultsAdded++;
        }
        if (resultsAdded > 0) {
            stringBuilder.append(" = ");
        }
        stringBuilder.append(getPointResult()).append(" points");
        return stringBuilder.toString();
    }

    private List<RibbonResult> getSortedRibbonResultSet() {
        return ribbonResultSet.stream().sorted(Comparator.comparingInt(RibbonResult::getPointValue)).toList().reversed();
    }
}
