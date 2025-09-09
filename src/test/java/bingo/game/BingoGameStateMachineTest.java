package bingo.game;

import bingo.game.input.UserInputException;
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
        void shouldStayInLevelInitializedStateWhenPerformingReset() throws UserInputException {
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldStayInLevelInitializedStateWhenChangingShipRestriction() throws UserInputException {
            bingoGameStateMachine.processChangeShipRestrictionAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldStayInLevelInitializedStateWhenPerformingOtherAction() throws UserInputException {
            bingoGameStateMachine.processOtherAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToUnsuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToSuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToLevelInitializedState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToPartialResultSubmittedState() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToUnsuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToSuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToLevelInitializedState() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPartialResultSubmittedState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToLevelInitializedStateWhenConfirmingResult()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToLevelInitializedStateWhenPerformingReset()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulToSuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToPartialResultSubmittedState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToLevelInitializedStateWhenConfirmingResult()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToLevelInitializedStateWhenPerformingReset() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromSuccessfulToUnsuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToPartialResultSubmittedState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionToUnconfirmedVoluntaryEndState() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
        }

        @Test
        void shouldTransitionToUnsuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToSuccessfulMatch() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
        }

        @Test
        void shouldTransitionToChallengeEndedVoluntarilyState() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
        }

        @Test
        void shouldTransitionToChallengeEndedSuccessfullyState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY);
        }

        @Test
        void shouldTransitionToChallengeEndedUnsuccessfullyState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
        }

        @Test
        void shouldThrowUserInputExceptionWhenConfirmingResultInLevelInitializedState() {
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CONFIRM_RESULT is not allowed in the LEVEL_INITIALIZED state",
                    () -> bingoGameStateMachine.processConfirmResultAction(false, false));
        }

        @Test
        void shouldThrowUserInputExceptionWhenSubmittingResultInChallengeEndedVoluntarilyState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertUserInputExceptionIsThrownWithMessage(
                    "Action SUBMIT_RESULT is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processSubmitResultAction(false, false));
        }

        @Test
        void shouldThrowUserInputExceptionWhenPerformingResetInChallengeEndedVoluntarilyState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertUserInputExceptionIsThrownWithMessage(
                    "Action PERFORM_RESET is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processPerformResetAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenEndingChallengeInChallengeEndedVoluntarilyState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertUserInputExceptionIsThrownWithMessage(
                    "Action END_CHALLENGE_VOLUNTARILY is not allowed in the CHALLENGE_ENDED_VOLUNTARILY state",
                    () -> bingoGameStateMachine.processEndChallengeVoluntarilyAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenEndingChallengeInUnconfirmedVoluntaryEndState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action END_CHALLENGE_VOLUNTARILY is not allowed in the UNCONFIRMED_VOLUNTARY_END state",
                    () -> bingoGameStateMachine.processEndChallengeVoluntarilyAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenChangingShipRestrictionInUnconfirmedVoluntaryEndState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CHANGE_SHIP_RESTRICTION is not allowed in the UNCONFIRMED_VOLUNTARY_END state",
                    () -> bingoGameStateMachine.processChangeShipRestrictionAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenPerformingOtherActionInUnconfirmedVoluntaryEndState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action OTHER_ACTION is not allowed in the UNCONFIRMED_VOLUNTARY_END state",
                    () -> bingoGameStateMachine.processOtherAction());
        }

        private void assertUserInputExceptionIsThrownWithMessage(String expectedMessage, Executable executable) {
            UserInputException exception = assertThrows(UserInputException.class, executable);
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class ActionIsAllowed {

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedVoluntarilyState() throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedSuccessfullyState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldAlwaysReturnFalseWhenInChallengeEndedUnsuccessfullyState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            bingoGameStateMachine.processConfirmResultAction(false, false);
            assertReturnsFalseForAllActions();
        }

        @Test
        void shouldReturnFalseForActionsWhichDoNotAffectStateWhenInUnconfirmedVoluntaryEndState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            List<BingoGameAction> disallowedActions = List.of(
                    BingoGameAction.END_CHALLENGE_VOLUNTARILY,
                    BingoGameAction.CHANGE_SHIP_RESTRICTION,
                    BingoGameAction.OTHER_ACTION);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptEndChallengeVoluntarilyAndChangeShipRestrictionWhenInUnconfirmedSuccessfulMatchState()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            List<BingoGameAction> disallowedActions =
                    List.of(BingoGameAction.END_CHALLENGE_VOLUNTARILY, BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptEndChallengeVoluntarilyAndChangeShipRestrictionWhenInUnconfirmedUnsuccessfulMatchState()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            List<BingoGameAction> disallowedActions =
                    List.of(BingoGameAction.END_CHALLENGE_VOLUNTARILY, BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueOnlyForPerformResetActionAndActionsWhichChangeTheMatchResultWhenInPartialResultSubmittedState()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            List<BingoGameAction> disallowedActions = List.of(
                    BingoGameAction.END_CHALLENGE_VOLUNTARILY,
                    BingoGameAction.CONFIRM_RESULT,
                    BingoGameAction.CHANGE_SHIP_RESTRICTION);
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
