package bingo.game.utility;

import bingo.game.BingoGameStateMachine;
import bingo.game.results.BingoResultBars;
import bingo.tokens.TokenCounter;
import bingo.tokens.impl.NonFunctionalTokenCounter;
import bingo.tokens.impl.TokenCounterImpl;

public class BingoGameDependencyInjector {

    public TokenCounter createTokenCounter(boolean extraLivesAreEnabled) {
        if (extraLivesAreEnabled) {
            return new TokenCounterImpl();
        } else {
            return new NonFunctionalTokenCounter();
        }
    }

    public BingoGameStateMachine createBingoGameStateMachine(
            boolean shipRestrictionsAreEnabled, boolean endingVoluntarilyIsAllowed) {
        return new BingoGameStateMachine(shipRestrictionsAreEnabled, endingVoluntarilyIsAllowed);
    }

    public BingoResultBars createBingoResultBars(double pointRequirementModifier, int maxLevel) {
        return new BingoResultBars(pointRequirementModifier, maxLevel);
    }
}
