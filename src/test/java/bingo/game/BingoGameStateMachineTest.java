package bingo.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameStateMachineTest {
    private BingoGameStateMachine bingoGameStateMachine;

    @BeforeEach
    void setup() {
        bingoGameStateMachine = new BingoGameStateMachine();
    }

    @Nested
    class Constructor {

        @Test
        void shouldSetStateToLevelInitialized() {
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }
    }

    @Nested
    class ProcessAction {

        @Test
        void shouldStayInLevelInitializedState() {
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToUnsuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToSuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToLevelInitializedState() {
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToPartialResultSubmittedState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToUnsuccessfulMatch() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToSuccessfulMatch() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToLevelInitializedState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPartialResultSubmittedState() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToLevelInitializedStateWhenConfirmingResult() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToLevelInitializedStateWhenPerformingReset() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulToSuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToPartialResultSubmittedState() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToLevelInitializedStateWhenConfirmingResult() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToLevelInitializedStateWhenPerformingReset() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromSuccessfulToUnsuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToPartialResultSubmittedState() {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionToUnconfirmedVoluntaryEndState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
        }

        @Test
        void shouldTransitionToUnsuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToSuccessfulMatch() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToChallengeEndedVoluntarilyState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
        }

        @Test
        void shouldTransitionToChallengeEndedSuccessfullyState() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY);
        }

        @Test
        void shouldTransitionToChallengeEndedUnsuccessfullyState() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenConfirmingResultInLevelInitializedState() {
            assertIllegalStateExceptionIsThrownWithMessage(
                    "Action CONFIRM_RESULT is not allowed in the LEVEL_INITIALIZED state",
                    () -> bingoGameStateMachine.processConfirmResultAction(false, false));
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenSubmittingResultInChallengeEndedVoluntarilyState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertIllegalStateExceptionIsThrownWithMessage(
                    "Action SUBMIT_RESULT is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processSubmitResultAction(false, false));
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenPerformingResetInChallengeEndedVoluntarilyState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertIllegalStateExceptionIsThrownWithMessage(
                    "Action PERFORM_RESET is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processPerformResetAction());
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenEndingChallengeInChallengeEndedVoluntarilyState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertIllegalStateExceptionIsThrownWithMessage(
                    "Action END_CHALLENGE_VOLUNTARILY is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processEndChallengeVoluntarilyAction());
        }

        private void assertIllegalStateExceptionIsThrownWithMessage(String expectedMessage, Executable executable) {
            IllegalStateException exception = assertThrows(IllegalStateException.class, executable);
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class ActionIsAllowed {

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedVoluntarilyState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedSuccessfullyState() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedUnsuccessfullyState() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldAlwaysReturnTrueWhenInUnconfirmedVoluntaryEndState() {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertReturnsTrueForAllActions();
        }

        @Test
        void shouldReturnTrueForAllActionsExceptEndChallengeVoluntarilyWhenInUnconfirmedSuccessfulMatchState() {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            List<BingoGameAction> disallowedActions = List.of(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptEndChallengeVoluntarilyWhenInUnconfirmedUnsuccessfulMatchState() {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            List<BingoGameAction> disallowedActions = List.of(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptEndChallengeVoluntarilyAndConfirmResultWhenInPartialResultSubmittedState() {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            List<BingoGameAction> disallowedActions =
                    List.of(BingoGameAction.END_CHALLENGE_VOLUNTARILY, BingoGameAction.CONFIRM_RESULT);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptConfirmResultWhenInLevelInitializedState() {
            List<BingoGameAction> disallowedActions = List.of(BingoGameAction.CONFIRM_RESULT);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        private void assertReturnsFalseForAllActions() {
            for (BingoGameAction action : BingoGameAction.values()) {
                assertFalse(bingoGameStateMachine.actionIsAllowed(action));
            }
        }

        private void assertReturnsTrueForAllActions() {
            for (BingoGameAction action : BingoGameAction.values()) {
                assertTrue(bingoGameStateMachine.actionIsAllowed(action));
            }
        }

        private void assertReturnsTrueForAllActionsExcept(List<BingoGameAction> disallowedActions) {
            List<BingoGameAction> allowedActions =
                    Stream.of(BingoGameAction.values()).filter(action -> !disallowedActions.contains(action)).toList();
            for (BingoGameAction action : allowedActions) {
                assertTrue(bingoGameStateMachine.actionIsAllowed(action));
            }
            for (BingoGameAction action : disallowedActions) {
                assertFalse(bingoGameStateMachine.actionIsAllowed(action));
            }
        }
    }

    private void assertBingoGameStateIs(BingoGameState expectedState) {
        assertEquals(expectedState, bingoGameStateMachine.getCurrentState());
    }
}
