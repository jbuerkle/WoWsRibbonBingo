package bingo.game.utility;

import bingo.game.BingoGameStateMachine;
import bingo.game.results.BingoResultBars;
import bingo.game.tokens.TokenCounter;
import bingo.game.tokens.impl.NonFunctionalTokenCounter;
import bingo.game.tokens.impl.TokenCounterImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BingoGameDependencyInjectorTest {
    private final BingoGameDependencyInjector bingoGameDependencyInjector = new BingoGameDependencyInjector();

    @Test
    void createTokenCounterShouldReturnTokenCounterImplementation() {
        TokenCounter tokenCounter = bingoGameDependencyInjector.createTokenCounter(true);
        assertInstanceOf(TokenCounterImpl.class, tokenCounter);
    }

    @Test
    void createTokenCounterShouldReturnNonFunctionalTokenCounter() {
        TokenCounter tokenCounter = bingoGameDependencyInjector.createTokenCounter(false);
        assertInstanceOf(NonFunctionalTokenCounter.class, tokenCounter);
    }

    @Test
    void createBingoGameStateMachineShouldReturnInstance() {
        BingoGameStateMachine bingoGameStateMachine =
                bingoGameDependencyInjector.createBingoGameStateMachine(false, true);
        assertInstanceOf(BingoGameStateMachine.class, bingoGameStateMachine);
    }

    @Test
    void createBingoResultBarsShouldReturnInstance() {
        BingoResultBars bingoResultBars = bingoGameDependencyInjector.createBingoResultBars(1.5, 7);
        assertInstanceOf(BingoResultBars.class, bingoResultBars);
    }
}
