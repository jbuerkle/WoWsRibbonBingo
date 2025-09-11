package bingo.game;

import bingo.game.input.UserInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BingoGameStateMachineTest {
    private BingoGameStateMachine bingoGameStateMachine;

    @BeforeEach
    void setup() {
        setupBingoGameStateMachineToDisableShipRestrictions();
    }

    @Nested
    class Constructor {

        @Test
        void shouldSetStateToPrerequisiteSetupDoneWhenShipRestrictionsAreDisabled() {
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldSetStateToLevelInitializedWhenShipRestrictionsAreEnabled() {
            setupBingoGameStateMachineToEnableShipRestrictions();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }
    }

    @Nested
    class ProcessAction {

        @Test
        void shouldStayInPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldStayInLevelInitializedStateWhenPerformingResetAndShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldStayInPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreAlreadySet()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldStayInLevelInitializedStateWhenSetupIsNotFinished() throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(false);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromLevelInitializedStateToPrerequisiteSetupDoneStateWhenSetupIsFinished()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromPrerequisiteSetupDoneStateToLevelInitializedStateWhenSetupIsNotFinished()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processChangeShipRestrictionAction(false);
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
        void shouldTransitionFromPartialResultSubmittedStateToPrerequisiteSetupDoneStateWhenShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromPartialResultSubmittedStateToPrerequisiteSetupDoneStateWhenShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processSubmitResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
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
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToPrerequisiteSetupDoneStateWhenShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToLevelInitializedStateWhenShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromUnconfirmedVoluntaryEndStateToPrerequisiteSetupDoneStateWhenShipRestrictionsAreAlreadySet()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPartialResultSubmittedState() throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processSubmitResultAction(false, false);
            assertBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPrerequisiteSetupDoneStateWhenConfirmingResultAndShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPrerequisiteSetupDoneStateWhenConfirmingResultAndShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(false, true);
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromUnsuccessfulMatchToPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processSubmitResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
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
        void shouldTransitionFromSuccessfulMatchToPrerequisiteSetupDoneStateWhenConfirmingResultAndShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToLevelInitializedStateWhenConfirmingResultAndShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processConfirmResultAction(true, false);
            assertBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreDisabled()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
        }

        @Test
        void shouldTransitionFromSuccessfulMatchToPrerequisiteSetupDoneStateWhenPerformingResetAndShipRestrictionsAreEnabled()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            bingoGameStateMachine.processSubmitResultAction(true, true);
            assertBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            bingoGameStateMachine.processPerformResetAction();
            assertBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
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
            setupBingoGameStateMachineToEnableShipRestrictions();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CONFIRM_RESULT is not allowed in the LEVEL_INITIALIZED state",
                    () -> bingoGameStateMachine.processConfirmResultAction(false, false));
        }

        @Test
        void shouldThrowUserInputExceptionWhenConfirmingResultInPrerequisiteSetupDone() {
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CONFIRM_RESULT is not allowed in the PREREQUISITE_SETUP_DONE state",
                    () -> bingoGameStateMachine.processConfirmResultAction(false, false));
        }

        @Test
        void shouldThrowUserInputExceptionWhenSubmittingResultInLevelInitializedState() {
            setupBingoGameStateMachineToEnableShipRestrictions();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action SUBMIT_RESULT is not allowed in the LEVEL_INITIALIZED state",
                    () -> bingoGameStateMachine.processSubmitResultAction(false, false));
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
        void shouldThrowUserInputExceptionWhenEndingTheChallengeVoluntarilyIsProhibitedAndShipRestrictionsAreDisabled() {
            setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarily();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action END_CHALLENGE_VOLUNTARILY is not allowed in the PREREQUISITE_SETUP_DONE state",
                    () -> bingoGameStateMachine.processEndChallengeVoluntarilyAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenEndingTheChallengeVoluntarilyIsProhibitedAndShipRestrictionsAreEnabled() {
            setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarilyAndEnableShipRestrictions();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action END_CHALLENGE_VOLUNTARILY is not allowed in the LEVEL_INITIALIZED state",
                    () -> bingoGameStateMachine.processEndChallengeVoluntarilyAction());
        }

        @Test
        void shouldThrowUserInputExceptionWhenChangingShipRestrictionInUnconfirmedVoluntaryEndState()
                throws UserInputException {
            bingoGameStateMachine.processEndChallengeVoluntarilyAction();
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CHANGE_SHIP_RESTRICTION is not allowed in the UNCONFIRMED_VOLUNTARY_END state",
                    () -> bingoGameStateMachine.processChangeShipRestrictionAction(true));
        }

        @Test
        void shouldThrowUserInputExceptionWhenChangingShipRestrictionAndShipRestrictionsAreDisabled() {
            assertUserInputExceptionIsThrownWithMessage(
                    "Action CHANGE_SHIP_RESTRICTION is not allowed in the PREREQUISITE_SETUP_DONE state",
                    () -> bingoGameStateMachine.processChangeShipRestrictionAction(true));
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
        void shouldReturnTrueForAllActionsExceptConfirmResultAndChangeShipRestrictionWhenShipRestrictionsAreDisabled() {
            List<BingoGameAction> disallowedActions =
                    List.of(BingoGameAction.CONFIRM_RESULT, BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptSubmitResultAndConfirmResultWhenShipRestrictionsAreEnabled() {
            setupBingoGameStateMachineToEnableShipRestrictions();
            List<BingoGameAction> disallowedActions =
                    List.of(BingoGameAction.SUBMIT_RESULT, BingoGameAction.CONFIRM_RESULT);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueForAllActionsExceptConfirmResultWhenShipRestrictionsAreAlreadySet()
                throws UserInputException {
            setupBingoGameStateMachineToEnableShipRestrictions();
            bingoGameStateMachine.processChangeShipRestrictionAction(true);
            List<BingoGameAction> disallowedActions = List.of(BingoGameAction.CONFIRM_RESULT);
            assertReturnsTrueForAllActionsExcept(disallowedActions);
        }

        @Test
        void shouldReturnTrueOnlyForPerformResetActionAndActionsWhichChangeTheMatchResultWhenInPrerequisiteSetupDoneState() {
            setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarily();
            List<BingoGameAction> allowedActions =
                    List.of(BingoGameAction.PERFORM_RESET, BingoGameAction.SUBMIT_RESULT, BingoGameAction.OTHER_ACTION);
            assertReturnsFalseForAllActionsExcept(allowedActions);
        }

        @Test
        void shouldReturnTrueOnlyForPerformResetActionAndActionsWhichChangeTheMatchResultWhenInPartialResultSubmittedState()
                throws UserInputException {
            bingoGameStateMachine.processSubmitResultAction(false, false);
            List<BingoGameAction> allowedActions =
                    List.of(BingoGameAction.PERFORM_RESET, BingoGameAction.SUBMIT_RESULT, BingoGameAction.OTHER_ACTION);
            assertReturnsFalseForAllActionsExcept(allowedActions);
        }

        @Test
        void shouldReturnTrueOnlyForChangeShipRestrictionAndActionsWhichDoNotAffectStateWhenInLevelInitializedState() {
            setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarilyAndEnableShipRestrictions();
            List<BingoGameAction> allowedActions = List.of(
                    BingoGameAction.CHANGE_SHIP_RESTRICTION,
                    BingoGameAction.PERFORM_RESET,
                    BingoGameAction.OTHER_ACTION);
            assertReturnsFalseForAllActionsExcept(allowedActions);
        }

        private void assertReturnsFalseForAllActions() {
            for (BingoGameAction action : BingoGameAction.values()) {
                assertFalse(bingoGameStateMachine.actionIsAllowed(action));
            }
        }

        private void assertReturnsFalseForAllActionsExcept(List<BingoGameAction> allowedActions) {
            List<BingoGameAction> disallowedActions =
                    Stream.of(BingoGameAction.values()).filter(action -> !allowedActions.contains(action)).toList();
            for (BingoGameAction action : allowedActions) {
                assertTrue(bingoGameStateMachine.actionIsAllowed(action));
            }
            for (BingoGameAction action : disallowedActions) {
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

    private void setupBingoGameStateMachineToDisableShipRestrictions() {
        bingoGameStateMachine = new BingoGameStateMachine(false, true);
    }

    private void setupBingoGameStateMachineToEnableShipRestrictions() {
        bingoGameStateMachine = new BingoGameStateMachine(true, true);
    }

    private void setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarily() {
        bingoGameStateMachine = new BingoGameStateMachine(false, false);
    }

    private void setupBingoGameStateMachineToProhibitEndingTheChallengeVoluntarilyAndEnableShipRestrictions() {
        bingoGameStateMachine = new BingoGameStateMachine(true, false);
    }

    private void assertBingoGameStateIs(BingoGameState expectedState) {
        assertEquals(expectedState, bingoGameStateMachine.getCurrentState());
    }
}
