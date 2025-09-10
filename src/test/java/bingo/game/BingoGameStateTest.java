package bingo.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BingoGameStateTest {

    @Test
    void isFinalShouldReturnFalse() {
        List<BingoGameState> nonFinalStates = List.of(
                BingoGameState.LEVEL_INITIALIZED,
                BingoGameState.PREREQUISITE_SETUP_DONE,
                BingoGameState.PARTIAL_RESULT_SUBMITTED,
                BingoGameState.UNCONFIRMED_VOLUNTARY_END,
                BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH,
                BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        for (BingoGameState state : nonFinalStates) {
            assertFalse(state.isFinal());
        }
    }

    @Test
    void isFinalShouldReturnTrue() {
        List<BingoGameState> finalStates = List.of(
                BingoGameState.CHALLENGE_ENDED_VOLUNTARILY,
                BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY,
                BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
        for (BingoGameState state : finalStates) {
            assertTrue(state.isFinal());
        }
    }
}
