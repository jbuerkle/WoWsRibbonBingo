package bingo.game;

import bingo.game.input.UserInputException;

import java.io.Serial;
import java.io.Serializable;

public class BingoGameStateMachine implements Serializable {
    @Serial
    private static final long serialVersionUID = -2884018232777089835L;

    private BingoGameState bingoGameState;

    public BingoGameStateMachine() {
        this.bingoGameState = BingoGameState.LEVEL_INITIALIZED;
    }

    public BingoGameState getCurrentState() {
        return bingoGameState;
    }

    public boolean actionIsAllowed(BingoGameAction action) {
        return switch (bingoGameState) {
            case LEVEL_INITIALIZED -> !action.equals(BingoGameAction.CONFIRM_RESULT);
            case PARTIAL_RESULT_SUBMITTED -> actionIsAllowedForPartialResultSubmittedState(action);
            case UNCONFIRMED_VOLUNTARY_END -> actionIsAllowedForUnconfirmedVoluntaryEndState(action);
            case UNCONFIRMED_SUCCESSFUL_MATCH, UNCONFIRMED_UNSUCCESSFUL_MATCH ->
                    actionIsAllowedForUnconfirmedMatchResultState(action);
            case CHALLENGE_ENDED_VOLUNTARILY, CHALLENGE_ENDED_SUCCESSFULLY, CHALLENGE_ENDED_UNSUCCESSFULLY -> false;
        };
    }

    private boolean actionIsAllowedForPartialResultSubmittedState(BingoGameAction action) {
        return switch (action) {
            case CONFIRM_RESULT, END_CHALLENGE_VOLUNTARILY, CHANGE_SHIP_RESTRICTION -> false;
            case SUBMIT_RESULT, PERFORM_RESET, OTHER_ACTION -> true;
        };
    }

    private boolean actionIsAllowedForUnconfirmedVoluntaryEndState(BingoGameAction action) {
        return switch (action) {
            case END_CHALLENGE_VOLUNTARILY, CHANGE_SHIP_RESTRICTION, OTHER_ACTION -> false;
            case SUBMIT_RESULT, CONFIRM_RESULT, PERFORM_RESET -> true;
        };
    }

    private boolean actionIsAllowedForUnconfirmedMatchResultState(BingoGameAction action) {
        return switch (action) {
            case END_CHALLENGE_VOLUNTARILY, CHANGE_SHIP_RESTRICTION -> false;
            case SUBMIT_RESULT, CONFIRM_RESULT, PERFORM_RESET, OTHER_ACTION -> true;
        };
    }

    public void ensureActionIsAllowed(BingoGameAction action) throws UserInputException {
        if (!actionIsAllowed(action)) {
            throw new UserInputException("Action %s is not allowed in the %s state".formatted(action, bingoGameState));
        }
    }

    /**
     * Action: {@link BingoGameAction#SUBMIT_RESULT}
     */
    public void processSubmitResultAction(
            boolean bingoResultIsSubmittedForAllPlayers, boolean requirementOfCurrentResultBarIsMet)
            throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
        if (bingoResultIsSubmittedForAllPlayers) {
            bingoGameState = requirementOfCurrentResultBarIsMet ?
                    BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH :
                    BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH;
        } else {
            bingoGameState = BingoGameState.PARTIAL_RESULT_SUBMITTED;
        }
    }

    /**
     * Action: {@link BingoGameAction#CONFIRM_RESULT}
     */
    public void processConfirmResultAction(boolean hasNextLevel, boolean retryingIsAllowed) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
        if (bingoGameState.equals(BingoGameState.UNCONFIRMED_VOLUNTARY_END)) {
            bingoGameState = BingoGameState.CHALLENGE_ENDED_VOLUNTARILY;
        } else if (bingoGameState.equals(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH)) {
            bingoGameState =
                    hasNextLevel ? BingoGameState.LEVEL_INITIALIZED : BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY;
        } else if (bingoGameState.equals(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH)) {
            bingoGameState = retryingIsAllowed ?
                    BingoGameState.LEVEL_INITIALIZED :
                    BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY;
        }
    }

    /**
     * Action: {@link BingoGameAction#PERFORM_RESET}
     */
    public void processPerformResetAction() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.PERFORM_RESET);
        bingoGameState = BingoGameState.LEVEL_INITIALIZED;
    }

    /**
     * Action: {@link BingoGameAction#END_CHALLENGE_VOLUNTARILY}
     */
    public void processEndChallengeVoluntarilyAction() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
        bingoGameState = BingoGameState.UNCONFIRMED_VOLUNTARY_END;
    }

    /**
     * Action: {@link BingoGameAction#CHANGE_SHIP_RESTRICTION}
     */
    public void processChangeShipRestrictionAction() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
    }

    /**
     * Action: {@link BingoGameAction#OTHER_ACTION}
     */
    public void processOtherAction() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.OTHER_ACTION);
    }
}
