package bingo.game.utility;

import bingo.game.BingoGameStateMachine;
import bingo.game.results.BingoResultBars;
import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class BingoGameDependencyInjectorTest {
    private final BingoGameDependencyInjector bingoGameDependencyInjector = new BingoGameDependencyInjector();

    @Test
    void createTokenCounterShouldReturnTokenCounterImplementation() {
        TokenCounter tokenCounter = bingoGameDependencyInjector.createTokenCounter(true);
        assertInstanceOf(TokenCounter.class, tokenCounter);
    }

    @Test
    void createTokenCounterShouldReturnNonfunctionalTokenCounter() {
        TokenCounter tokenCounter = bingoGameDependencyInjector.createTokenCounter(false);
        assertNull(tokenCounter);
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
