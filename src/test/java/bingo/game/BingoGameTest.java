package bingo.game;

import bingo.game.input.UserInputException;
import bingo.game.modifiers.ChallengeModifier;
import bingo.game.results.BingoResult;
import bingo.game.results.BingoResultBars;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.game.utility.BingoGameDependencyInjector;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BingoGameTest {
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 2100 points ✅ Unlocked reward: Dummy reward text: 8 sub(s) 🎁 This is the highest reward you can get. Congratulations! 🎊 Total reward: 8 subs + unused extra lives: 2 * 6 subs = 20 subs 🎁";
    private static final String LEVEL_SEVEN_CONGRATULATIONS_WITH_ALL_CHALLENGE_MODIFIERS =
            ". Requirement of level 7: 2100 points ✅ Unlocked reward: Dummy reward text: 8 sub(s) 🎁 This is the highest reward you can get. Congratulations! 🎊 Total reward: (8 subs + unused extra lives: 2 * 6 subs) * (challenge modifiers: 1 + Random ship restrictions: 0.5 + Increased difficulty: 0.25 + No help: 0.25 + No giving up: 0.25 + No safety net: 0.75) = 60 subs 🎁";
    private static final String LEVEL_ONE_GAME_OVER =
            ". Requirement of level 1: 300 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: Dummy reward text: 1 sub(s) 🎁";
    private static final String LEVEL_ONE_GAME_OVER_WITH_NO_GIVING_UP_AND_NO_SAFETY_NET =
            ". Requirement of level 1: 300 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: Dummy reward text: 1 sub(s) 🎁 Total reward: 1 sub * (challenge modifiers: 1 + No giving up: 0.25 + No safety net: 0.75) = 2 subs 🎁";
    private static final String LEVEL_ONE_IMBALANCED_MATCHMAKING_WITHOUT_TOKEN_COUNTER =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Imbalanced matchmaking (rule 8a or 8b) 🔄";
    private static final String LEVEL_ONE_IMBALANCED_MATCHMAKING =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Imbalanced matchmaking (rule 8a or 8b) 🔄 Token counter: Dummy token text.";
    private static final String LEVEL_ONE_UNFAIR_DISADVANTAGE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Unfair disadvantage (rule 8c) 🔄 Token counter: Dummy token text.";
    private static final String LEVEL_ONE_EXTRA_LIFE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Extra life (rule 8d) 🔄 Token counter: Dummy token text.";
    private static final String LEVEL_ONE_VOLUNTARY_END =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: Dummy reward text: 1 sub(s) 🎁";
    private static final String LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: Dummy reward text: 1 sub(s) 🎁 Total reward: 1 sub + unused extra lives: 1 * 6 subs = 7 subs 🎁";
    private static final String LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE_AND_NO_SAFETY_NET =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: Dummy reward text: 1 sub(s) 🎁 Total reward: (1 sub + unused extra lives: 1 * 6 subs) * (challenge modifiers: 1 + No safety net: 0.75) = 12 subs 🎁";
    private static final String LEVEL_ONE_REQUIREMENT_WITHOUT_TOKEN_COUNTER = "Requirement of level 1: 300 points";
    private static final String LEVEL_ONE_REQUIREMENT =
            "Requirement of level 1: 300 points. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION =
            "Requirement of level 1: 300 points. Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_TRANSITION_TO_TWO_WITHOUT_TOKEN_COUNTER =
            ". Requirement of level 1: 300 points ✅ Unlocked reward: Dummy reward text: 2 sub(s) 🎁 ➡️ Requirement of level 2: 600 points";
    private static final String LEVEL_ONE_TRANSITION_TO_TWO =
            ". Requirement of level 1: 300 points ✅ Unlocked reward: Dummy reward text: 2 sub(s) 🎁 Token counter: Dummy token text. ➡️ Requirement of level 2: 600 points";
    private static final String LEVEL_TWO_VOLUNTARY_END =
            "Challenge ended voluntarily on level 2. Your reward from the previous level: Dummy reward text: 2 sub(s) 🎁";
    private static final String LEVEL_TWO_REQUIREMENT =
            "Requirement of level 2: 600 points. Token counter: Dummy token text.";
    private static final String LEVEL_TWO_TRANSITION_TO_THREE =
            ". Requirement of level 2: 600 points ✅ Unlocked reward: Dummy reward text: 3 sub(s) 🎁 Token counter: Dummy token text. ➡️ Requirement of level 3: 900 points";
    private static final String MULTIPLAYER_RESTRICTION_B =
            "Requirement of level 1: 300 points. Player B's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESTRICTION_AB =
            "Requirement of level 1: 300 points. Player A's ship restriction: Dummy ship restriction text. Player B's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESTRICTION_ABC =
            "Requirement of level 1: 300 points. Player A's ship restriction: Dummy ship restriction text. Player B's ship restriction: Dummy ship restriction text. Player C's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESULT_B =
            "Player B's Ribbon Bingo result: Dummy result text. Total result: 30 points. " +
                    MULTIPLAYER_RESTRICTION_ABC;
    private static final String MULTIPLAYER_RESULT_AB =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Total result: 30 points + 30 points = 60 points. " +
                    MULTIPLAYER_RESTRICTION_ABC;
    private static final String MULTIPLAYER_RESULT_ABC =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Player C's Ribbon Bingo result: Dummy result text. Total result: 30 points + 30 points + 30 points = 90 points. Requirement of level 1: 300 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: Dummy reward text: 1 sub(s) 🎁";
    private static final String MULTIPLAYER_RESULT_WITH_DIVISION_ACHIEVEMENTS =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Player C's Ribbon Bingo result: Dummy result text. Shared division achievements: Dummy division text. Total result: 30 points + 30 points + 30 points + 600 points = 690 points. Requirement of level 1: 300 points ✅ Unlocked reward: Dummy reward text: 2 sub(s) 🎁 Token counter: Dummy token text. ➡️ Requirement of level 2: 600 points";
    private static final String DUMMY_SHIP_RESTRICTION_TEXT = "Dummy ship restriction text";
    private static final String DUMMY_TOKEN_TEXT = "Token counter: Dummy token text.";
    private static final String DUMMY_RESULT_TEXT = "Ribbon Bingo result: Dummy result text";
    private static final String DUMMY_DIVISION_TEXT = "Shared division achievements: Dummy division text";
    private static final String DUMMY_MAIN_ARMAMENT_TEXT = "Dummy main armament";
    private static final String INCORRECT_NUMBER_OF_PLAYERS = "The number of players must be between 1 and 3";
    private static final String INCORRECT_PLAYER = "Player D is not participating in the game";
    private static final Ship SHIP_A = new Ship("Ship A");
    private static final Ship SHIP_B = new Ship("Ship B");
    private static final Ship SHIP_C = new Ship("Ship C");
    private static final Ship SHIP_D = new Ship("Ship D");
    private static final Player PLAYER_A = new Player("Player A");
    private static final Player PLAYER_B = new Player("Player B");
    private static final Player PLAYER_C = new Player("Player C");
    private static final Player PLAYER_D = new Player("Player D");
    private static final Player SINGLE_PLAYER = new Player("Single Player");

    @Mock
    private BingoGameDependencyInjector mockedBingoGameDependencyInjector;
    @Mock
    private BingoGameStateMachine mockedBingoGameStateMachine;
    @Mock
    private BingoResultBars mockedBingoResultBars;
    @Mock
    private BingoResult mockedBingoResult;
    @Mock
    private TokenCounter mockedTokenCounter;
    @Mock
    private ShipRestriction mockedShipRestriction;
    @Mock
    private MainArmamentType mockedMainArmamentType;
    @Mock
    private SharedDivisionAchievements mockedDivisionAchievements;
    @Mock
    private UserInputException mockedUserInputException;

    private BingoGame bingoGame;

    @BeforeEach
    void setup() throws UserInputException {
        setupBingoGameWithPlayers(List.of(SINGLE_PLAYER));
    }

    @Nested
    class ActionIsAllowed {

        @Test
        void shouldDelegateToBingoGameStateMachineAndReturnTrueForConfirmResultAction() {
            mockSubmitResultActionIsAllowedReturns(false);
            mockConfirmResultActionIsAllowedReturns(true);
            assertFalse(bingoGame.actionIsAllowed(BingoGameAction.SUBMIT_RESULT));
            assertTrue(bingoGame.actionIsAllowed(BingoGameAction.CONFIRM_RESULT));
        }

        @Test
        void shouldDelegateToBingoGameStateMachineAndReturnTrueForSubmitResultAction() {
            mockSubmitResultActionIsAllowedReturns(true);
            mockConfirmResultActionIsAllowedReturns(false);
            assertTrue(bingoGame.actionIsAllowed(BingoGameAction.SUBMIT_RESULT));
            assertFalse(bingoGame.actionIsAllowed(BingoGameAction.CONFIRM_RESULT));
        }

        private void mockSubmitResultActionIsAllowedReturns(boolean isAllowed) {
            when(mockedBingoGameStateMachine.actionIsAllowed(BingoGameAction.SUBMIT_RESULT)).thenReturn(isAllowed);
        }

        private void mockConfirmResultActionIsAllowedReturns(boolean isAllowed) {
            when(mockedBingoGameStateMachine.actionIsAllowed(BingoGameAction.CONFIRM_RESULT)).thenReturn(isAllowed);
        }
    }

    @Nested
    class SubmitAndGetSharedDivisionAchievements {

        @Test
        void submitTwiceShouldBeSuccessful() throws UserInputException {
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWhenSetIsAllowed() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(false, false);
        }

        @Test
        void submitShouldNotUpdateTokenCounterOrProcessSubmitResultActionWhenSetIsNotAllowed()
                throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.submitSharedDivisionAchievements(
                    mockedDivisionAchievements));
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
            verify(mockedBingoGameStateMachine, never()).processSubmitResultAction(anyBoolean(), anyBoolean());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoSharedDivisionAchievementsAreSet() {
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.submitSharedDivisionAchievements(
                    mockedDivisionAchievements));
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
        }

        @Test
        void getShouldReturnTheSharedDivisionAchievementsWhichWereSet() throws UserInputException {
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            Optional<SharedDivisionAchievements> returnedDivisionAchievements =
                    bingoGame.getSharedDivisionAchievements();
            assertTrue(returnedDivisionAchievements.isPresent());
            assertEquals(mockedDivisionAchievements, returnedDivisionAchievements.get());
        }
    }

    @Nested
    class SetAndGetActiveRetryRules {

        @Test
        void setShouldOverwriteTheListWhichWasPreviouslySet() throws UserInputException {
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.setActiveRetryRules(Collections.emptyList());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void setShouldUpdateTokenCounterWhenSetIsAllowed() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            bingoGame.setActiveRetryRules(activeRetryRules);
            verify(mockedTokenCounter).calculateMatchResult(false, true, activeRetryRules);
        }

        @Test
        void setShouldNotUpdateTokenCounterWhenSetIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.setActiveRetryRules(activeRetryRules));
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
        }

        @Test
        void getShouldReturnEmptyListWhenNoActiveRetryRulesAreSet() {
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void getShouldReturnEmptyListWhenSetIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.setActiveRetryRules(activeRetryRules));
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void getShouldReturnTheActiveRetryRulesWhichWereSet() throws UserInputException {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            bingoGame.setActiveRetryRules(activeRetryRules);
            assertEquals(activeRetryRules, bingoGame.getActiveRetryRules());
        }
    }

    @Nested
    class SetGetAndRemoveShipRestrictionForPlayer {

        @Test
        void setShouldBeSuccessfulWhenNoShipRestrictionIsSet() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        }

        @Test
        void setShouldBeSuccessfulWhenThePreviousShipRestrictionIsRemoved() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER);
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        }

        @Test
        void setShouldNotBeSuccessfulWhenAShipRestrictionIsAlreadySet() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            assertUserInputExceptionIsThrownWithMessage(
                    "A ship restriction is already set for Single Player",
                    () -> bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
        }

        @Test
        void setShouldProcessChangeShipRestrictionActionWhenSuccessful() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            verify(mockedBingoGameStateMachine).processChangeShipRestrictionAction(true);
        }

        @Test
        void setShouldNotProcessChangeShipRestrictionActionWhenUnsuccessful() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.setShipRestrictionForPlayer(
                    SINGLE_PLAYER,
                    mockedShipRestriction));
            verify(mockedBingoGameStateMachine, never()).processChangeShipRestrictionAction(anyBoolean());
        }

        @Test
        void setShouldProcessChangeShipRestrictionActionWithMultiplePlayers() throws UserInputException {
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            bingoGame.setShipRestrictionForPlayer(PLAYER_A, mockedShipRestriction);
            bingoGame.setShipRestrictionForPlayer(PLAYER_B, mockedShipRestriction);
            bingoGame.setShipRestrictionForPlayer(PLAYER_C, mockedShipRestriction);
            verify(mockedBingoGameStateMachine, times(2)).processChangeShipRestrictionAction(false);
            verify(mockedBingoGameStateMachine, times(1)).processChangeShipRestrictionAction(true);
        }

        @Test
        void setShouldThrowUserInputException() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.setShipRestrictionForPlayer(PLAYER_D, mockedShipRestriction));
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoShipRestrictionIsSet() throws UserInputException {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.setShipRestrictionForPlayer(
                    SINGLE_PLAYER,
                    mockedShipRestriction));
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnTheShipRestrictionWhichWasSet() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            Optional<ShipRestriction> returnedShipRestriction = bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER);
            assertTrue(returnedShipRestriction.isPresent());
            assertEquals(mockedShipRestriction, returnedShipRestriction.get());
        }

        @Test
        void getShouldThrowUserInputException() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.getShipRestrictionForPlayer(PLAYER_D));
        }

        @Test
        void removeShouldRemoveTheShipRestrictionWhichWasPreviouslySet() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER);
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenNotAllowed() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER));
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isPresent());
        }

        @Test
        void removeShouldProcessChangeShipRestrictionActionWhenSuccessful() throws UserInputException {
            bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER);
            verify(mockedBingoGameStateMachine).processChangeShipRestrictionAction(false);
        }

        @Test
        void removeShouldNotProcessChangeShipRestrictionActionWhenUnsuccessful() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER));
            verify(mockedBingoGameStateMachine, never()).processChangeShipRestrictionAction(anyBoolean());
        }

        @Test
        void removeShouldThrowUserInputException() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.removeShipRestrictionForPlayer(PLAYER_D));
        }
    }

    @Nested
    class AddGetAndRemoveShipsUsed {

        @Test
        void addShouldBeSuccessfulWhenShipNamesAreUnique() throws UserInputException {
            bingoGame.addShipUsed(SHIP_A);
            bingoGame.addShipUsed(SHIP_B);
            bingoGame.addShipUsed(SHIP_C);
            assertEquals(3, bingoGame.getShipsUsed().size());
        }

        @Test
        void addShouldNotBeSuccessfulWhenTheShipWasAlreadyAdded() throws UserInputException {
            bingoGame.addShipUsed(SHIP_D);
            assertUserInputExceptionIsThrownWithMessage(
                    "ship d was already used",
                    () -> bingoGame.addShipUsed(new Ship("ship d")));
            assertEquals(1, bingoGame.getShipsUsed().size());
        }

        @Test
        void getShouldReturnEmptyListWhenNoShipsWereAdded() {
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void getShouldReturnEmptyListWhenAddIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.addShipUsed(SHIP_D));
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void getShouldReturnListOfShipsInTheOrderTheyWereAdded() throws UserInputException {
            bingoGame.addShipUsed(SHIP_A);
            bingoGame.addShipUsed(SHIP_B);
            bingoGame.addShipUsed(SHIP_C);
            List<Ship> shipsUsed = bingoGame.getShipsUsed();
            assertEquals(3, shipsUsed.size());
            Iterator<Ship> shipIterator = shipsUsed.iterator();
            assertEquals(SHIP_A, shipIterator.next());
            assertEquals(SHIP_B, shipIterator.next());
            assertEquals(SHIP_C, shipIterator.next());
        }

        @Test
        void removeShouldBeSuccessfulWhenTheShipWasPreviouslyAdded() throws UserInputException {
            bingoGame.addShipUsed(SHIP_A);
            bingoGame.removeShipUsed(SHIP_A);
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenTheShipWasNeverAdded() throws UserInputException {
            bingoGame.addShipUsed(SHIP_A);
            assertUserInputExceptionIsThrownWithMessage(
                    "Ship B is not in the list of ships used, so it cannot be removed",
                    () -> bingoGame.removeShipUsed(SHIP_B));
            assertEquals(1, bingoGame.getShipsUsed().size());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenNotAllowed() throws UserInputException {
            bingoGame.addShipUsed(SHIP_B);
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.removeShipUsed(SHIP_B));
            assertEquals(1, bingoGame.getShipsUsed().size());
        }
    }

    @Nested
    class GetPlayers {

        @Test
        void shouldReturnSinglePlayer() {
            assertEquals(List.of(SINGLE_PLAYER), bingoGame.getPlayers());
        }

        @Test
        void shouldReturnThePlayersWhichWereRegistered() throws UserInputException {
            List<Player> players = List.of(PLAYER_A, PLAYER_B, PLAYER_C);
            setupBingoGameWithPlayers(players);
            assertEquals(players, bingoGame.getPlayers());
        }
    }

    @Nested
    class Constructor {
        private final List<ChallengeModifier> allModifiers = List.of(ChallengeModifier.values());

        @Test
        void shouldSetCurrentLevelToOne() {
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void setupShouldFailBecauseNoPlayersWereRegistered() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_NUMBER_OF_PLAYERS,
                    () -> setupBingoGameWithPlayers(Collections.emptyList()));
        }

        @Test
        void setupShouldFailBecauseMoreThanThreePlayersWereRegistered() {
            List<Player> players = List.of(PLAYER_A, PLAYER_B, PLAYER_C, PLAYER_D);
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_NUMBER_OF_PLAYERS,
                    () -> setupBingoGameWithPlayers(players));
        }

        @Test
        void shouldFilterCorrectChallengeModifiersForSoloStreamerChallenge() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), allModifiers);
            List<ChallengeModifier> allowedModifiers = bingoGame.getChallengeModifiers();
            assertEquals(allModifiers.size() - 1, allowedModifiers.size());
            assertFalse(allowedModifiers.contains(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE));
        }

        @Test
        void shouldFilterCorrectChallengeModifiersForDuoTrioStreamerChallenge() throws UserInputException {
            setupBingoGame(List.of(PLAYER_A, PLAYER_B, PLAYER_C), allModifiers);
            List<ChallengeModifier> allowedModifiers = bingoGame.getChallengeModifiers();
            assertEquals(allModifiers.size() - 1, allowedModifiers.size());
            assertFalse(allowedModifiers.contains(ChallengeModifier.NO_HELP));
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithoutChallengeModifiers() {
            verify(mockedBingoGameDependencyInjector).createBingoGameStateMachine(false, true);
            verify(mockedBingoGameDependencyInjector).createBingoResultBars(1.0, 7);
            verify(mockedBingoGameDependencyInjector).createTokenCounter(true);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithRandomShipRestrictions() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.RANDOM_SHIP_RESTRICTIONS));
            verify(mockedBingoGameDependencyInjector).createBingoGameStateMachine(true, true);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithNoGivingUp() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_GIVING_UP));
            verify(mockedBingoGameDependencyInjector).createBingoGameStateMachine(false, false);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithNoSafetyNet() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_SAFETY_NET));
            verify(mockedBingoGameDependencyInjector).createTokenCounter(false);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithIncreasedDifficulty() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.INCREASED_DIFFICULTY));
            verify(mockedBingoGameDependencyInjector).createBingoResultBars(1.2, 7);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithTwoPlayersAndIncreasedDifficulty() throws UserInputException {
            setupBingoGame(List.of(PLAYER_A, PLAYER_B), List.of(ChallengeModifier.INCREASED_DIFFICULTY));
            verify(mockedBingoGameDependencyInjector).createBingoResultBars(1.6, 7);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithThreePlayers() throws UserInputException {
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            verify(mockedBingoGameDependencyInjector).createBingoResultBars(1.8, 7);
        }

        @Test
        void shouldInvokeBingoGameDependencyInjectorWithDoubleDifficultyIncrease() throws UserInputException {
            setupBingoGame(
                    List.of(PLAYER_A, PLAYER_B, PLAYER_C),
                    List.of(ChallengeModifier.INCREASED_DIFFICULTY, ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE));
            verify(mockedBingoGameDependencyInjector).createBingoResultBars(2.2, 7);
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnFirstResultBarWithoutTokenCounter() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_SAFETY_NET));
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockBingoResultBarsGetPointRequirement();
            assertEquals(LEVEL_ONE_REQUIREMENT_WITHOUT_TOKEN_COUNTER, bingoGame.toString());
        }

        @Test
        void shouldReturnFirstResultBarWhenNoResultWasSubmitted() {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            mockBingoResultBarsGetPointRequirement();
            assertEquals(LEVEL_ONE_REQUIREMENT, bingoGame.toString());
        }

        @Test
        void shouldReturnFirstResultBarWithShipRestrictionWhenNoResultWasSubmitted() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            mockShipRestrictionGetDisplayText();
            mockBingoResultBarsGetPointRequirement();
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            assertEquals(LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION, bingoGame.toString());
        }

        @Test
        void shouldReturnGameOverWhenSubmittedResultIsInsufficient() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER, bingoGame.toString());
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldReturnGameOverWithNoGivingUpAndNoSafetyNet() throws UserInputException {
            setupBingoGame(
                    List.of(SINGLE_PLAYER),
                    List.of(ChallengeModifier.NO_GIVING_UP, ChallengeModifier.NO_SAFETY_NET));
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsReward();
            mockBingoResultBarsGetNumberOfSubsAsString();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(
                    DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER_WITH_NO_GIVING_UP_AND_NO_SAFETY_NET,
                    bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedWithoutTokenCounter() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_SAFETY_NET));
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(
                    DUMMY_RESULT_TEXT + LEVEL_ONE_IMBALANCED_MATCHMAKING_WITHOUT_TOKEN_COUNTER,
                    bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedDueToImbalancedMatchmaking() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_IMBALANCED_MATCHMAKING, bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedDueToAnUnfairDisadvantage() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            bingoGame.setActiveRetryRules(List.of(RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_UNFAIR_DISADVANTAGE, bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedBecauseOfAnExtraLife() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockTokenCounterHasExtraLife();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_EXTRA_LIFE, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelTwoNextWithoutTokenCounter() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_SAFETY_NET));
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockSufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_TRANSITION_TO_TWO_WITHOUT_TOKEN_COUNTER, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockSufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_TRANSITION_TO_TWO, bingoGame.toString());
        }

        @Test
        void shouldReturnSecondResultBarWhenNoResultWasSubmitted() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            assertEquals(LEVEL_TWO_REQUIREMENT, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockSufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_TWO_TRANSITION_TO_THREE, bingoGame.toString());
        }

        @Test
        void shouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsReward();
            mockBingoResultBarsGetNumberOfSubsAsString();
            skipLevelsUntilReachingLevel(7);
            mockBingoResultToString();
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(2);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS, bingoGame.toString());
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY);
            assertEquals(
                    DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS + END_OF_CHALLENGE_CONFIRMED,
                    bingoGame.toString());
            verify(mockedTokenCounter, times(MAX_LEVEL - 1)).calculateMatchResult(true, true, Collections.emptyList());
            verify(mockedTokenCounter, times(1)).calculateMatchResult(true, false, Collections.emptyList());
            verify(mockedTokenCounter, times(MAX_LEVEL - 1)).confirmMatchResult();
        }

        @Test
        void shouldReturnCongratulationsForLevelSevenWithAllChallengeModifiers() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.values()));
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsReward();
            mockBingoResultBarsGetNumberOfSubsAsString();
            skipLevelsUntilReachingLevel(7);
            mockBingoResultToString();
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(2);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            assertEquals(
                    DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS_WITH_ALL_CHALLENGE_MODIFIERS,
                    bingoGame.toString());
        }

        @Test
        void shouldReturnLevelTwoVoluntaryEnd() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_TWO_VOLUNTARY_END, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelOneVoluntaryEnd() {
            mockBingoResultBarsGetNumberOfSubsAsString();
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_ONE_VOLUNTARY_END, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelOneVoluntaryEndWithExtraLife() {
            mockBingoResultBarsGetNumberOfSubsAsReward();
            mockBingoResultBarsGetNumberOfSubsAsString();
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(1);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE, bingoGame.toString());
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
            assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelOneVoluntaryEndWithExtraLifeAndNoSafetyNet() throws UserInputException {
            setupBingoGame(List.of(SINGLE_PLAYER), List.of(ChallengeModifier.NO_SAFETY_NET));
            mockBingoResultBarsGetNumberOfSubsAsReward();
            mockBingoResultBarsGetNumberOfSubsAsString();
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(1);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE_AND_NO_SAFETY_NET, bingoGame.toString());
        }

        @Test
        void shouldUpdateStepByStepForMultiplayer() throws UserInputException {
            mockCurrentBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            mockBingoResultToString();
            mockBingoResultGetMainArmamentType();
            mockTokenCounterToString();
            mockShipRestrictionGetDisplayText();
            mockShipRestrictionAllowsMainArmamentType();
            mockSharedDivisionAchievementsToString();
            mockInsufficientBingoResult();
            mockBingoResultBarsGetPointRequirement();
            mockBingoResultBarsGetNumberOfSubsAsString();
            mockSharedDivisionAchievementsGetPointValue();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            setShipRestrictionsOneByOneAndCheckResults();
            submitBingoResultsOneByOneAndCheckResults();
            submitDivisionAchievementsAndCheckResults();
        }

        private void setShipRestrictionsOneByOneAndCheckResults() throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(PLAYER_B, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_B, bingoGame.toString());
            bingoGame.setShipRestrictionForPlayer(PLAYER_A, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_AB, bingoGame.toString());
            bingoGame.setShipRestrictionForPlayer(PLAYER_C, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_ABC, bingoGame.toString());
        }

        private void submitBingoResultsOneByOneAndCheckResults() throws UserInputException {
            bingoGame.submitBingoResultForPlayer(PLAYER_B, mockedBingoResult);
            assertEquals(MULTIPLAYER_RESULT_B, bingoGame.toString());
            bingoGame.submitBingoResultForPlayer(PLAYER_A, mockedBingoResult);
            assertEquals(MULTIPLAYER_RESULT_AB, bingoGame.toString());
            bingoGame.submitBingoResultForPlayer(PLAYER_C, mockedBingoResult);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            assertEquals(MULTIPLAYER_RESULT_ABC, bingoGame.toString());
        }

        private void submitDivisionAchievementsAndCheckResults() throws UserInputException {
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            assertEquals(MULTIPLAYER_RESULT_WITH_DIVISION_ACHIEVEMENTS, bingoGame.toString());
        }

        private void skipLevelsUntilReachingLevel(int levelToReach) throws UserInputException {
            if (levelToReach < 2 || levelToReach > MAX_LEVEL) {
                fail("Parameter 'levelToReach' must not be lower than 2 or greater than MAX_LEVEL");
            }
            mockSufficientBingoResult();
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            while (bingoGame.getCurrentLevel() < levelToReach) {
                bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
                bingoGame.confirmCurrentResult();
            }
        }

        private void mockShipRestrictionGetDisplayText() {
            when(mockedShipRestriction.getDisplayText()).thenReturn(DUMMY_SHIP_RESTRICTION_TEXT);
        }

        private void mockSharedDivisionAchievementsToString() {
            when(mockedDivisionAchievements.toString()).thenReturn(DUMMY_DIVISION_TEXT);
        }

        private void mockSharedDivisionAchievementsGetPointValue() {
            when(mockedDivisionAchievements.getPointValue()).thenReturn(600L);
        }

        private void mockBingoResultToString() {
            when(mockedBingoResult.toString()).thenReturn(DUMMY_RESULT_TEXT);
        }

        private void mockTokenCounterToString() {
            when(mockedTokenCounter.toString()).thenReturn(DUMMY_TOKEN_TEXT);
        }

        private void mockTokenCounterHasExtraLife() {
            when(mockedTokenCounter.hasExtraLife()).thenReturn(true);
        }

        private void mockExtraLivesInTokenCounterAre(int extraLives) {
            when(mockedTokenCounter.getCurrentExtraLives()).thenReturn(extraLives);
        }
    }

    @Nested
    class EndChallenge {

        @Test
        void shouldBeSuccessfulWhenAllowed() throws UserInputException {
            bingoGame.endChallenge();
            verify(mockedBingoGameStateMachine).processEndChallengeVoluntarilyAction();
        }

        @Test
        void shouldNotBeSuccessfulWhenNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.endChallenge());
            verify(mockedBingoGameStateMachine, never()).processEndChallengeVoluntarilyAction();
        }
    }

    @Nested
    class SubmitAndGetBingoResult {

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithInsufficientResult()
                throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockInsufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, false);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithSufficientResult()
                throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockSufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            verify(mockedTokenCounter).calculateMatchResult(true, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, true);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithActiveRetryRules()
                throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockInsufficientBingoResult();
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            bingoGame.setActiveRetryRules(activeRetryRules);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            verify(mockedTokenCounter, times(2)).calculateMatchResult(false, true, activeRetryRules);
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, false);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithMultiplePlayers() throws UserInputException {
            mockBingoResultBarsGetPointRequirement();
            mockSufficientBingoResult();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            bingoGame.submitBingoResultForPlayer(PLAYER_A, mockedBingoResult);
            bingoGame.submitBingoResultForPlayer(PLAYER_B, mockedBingoResult);
            bingoGame.submitBingoResultForPlayer(PLAYER_C, mockedBingoResult);
            verify(mockedTokenCounter, times(3)).calculateMatchResult(true, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine, times(2)).processSubmitResultAction(false, true);
            verify(mockedBingoGameStateMachine, times(1)).processSubmitResultAction(true, true);
        }

        @Test
        void submitShouldNotUpdateTokenCounterOrProcessSubmitResultActionWhenSetIsNotAllowed()
                throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.submitBingoResultForPlayer(
                    SINGLE_PLAYER,
                    mockedBingoResult));
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
            verify(mockedBingoGameStateMachine, never()).processSubmitResultAction(anyBoolean(), anyBoolean());
        }

        @Test
        void submitShouldNotThrowUserInputExceptionWhenShipRestrictionAllowsMainArmamentType()
                throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            mockBingoResultGetMainArmamentType();
            mockShipRestrictionAllowsMainArmamentType();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
        }

        @Test
        void submitShouldThrowUserInputExceptionWhenShipRestrictionProhibitsMainArmamentType()
                throws UserInputException {
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            mockBingoResultGetMainArmamentType();
            mockMainArmamentTypeGetDisplayText();
            assertUserInputExceptionIsThrownWithMessage(
                    "Ships with dummy main armament as main armament are currently prohibited due to the ship restriction set for Single Player",
                    () -> bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult));
        }

        @Test
        void submitShouldThrowUserInputException() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.submitBingoResultForPlayer(PLAYER_D, mockedBingoResult));
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoBingoResultIsSet() throws UserInputException {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.submitBingoResultForPlayer(
                    SINGLE_PLAYER,
                    mockedBingoResult));
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnTheBingoResultWhichWasSet() throws UserInputException {
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            Optional<BingoResult> returnedBingoResult = bingoGame.getBingoResultForPlayer(SINGLE_PLAYER);
            assertTrue(returnedBingoResult.isPresent());
            assertEquals(mockedBingoResult, returnedBingoResult.get());
        }

        @Test
        void getShouldThrowUserInputException() {
            assertUserInputExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.getBingoResultForPlayer(PLAYER_D));
        }

        private void mockMainArmamentTypeGetDisplayText() {
            when(mockedMainArmamentType.getDisplayText()).thenReturn(DUMMY_MAIN_ARMAMENT_TEXT);
        }
    }

    @Nested
    class ConfirmCurrentResult {

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithChallengeEndedVoluntarilyAsNewState()
                throws UserInputException {
            List<RetryRule> activeRetryRules = Collections.emptyList();
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
            bingoGame.confirmCurrentResult();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, false);
            assertPreviouslySubmittedMatchResultsAreNotRemoved(activeRetryRules);
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithLevelInitializedAsNewState()
                throws UserInputException {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockBingoResultBarsGetPointRequirement();
            mockInsufficientBingoResult();
            bingoGame.confirmCurrentResult();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, true);
            assertPreviouslySubmittedMatchResultsAreRemoved();
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithPrerequisiteSetupDoneAsNewState()
                throws UserInputException {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockCurrentBingoGameStateIs(BingoGameState.PREREQUISITE_SETUP_DONE);
            mockBingoResultBarsGetPointRequirement();
            mockInsufficientBingoResult();
            bingoGame.confirmCurrentResult();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, true);
            assertPreviouslySubmittedMatchResultsAreRemoved();
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithInitialStateAndSuccessfulMatch()
                throws UserInputException {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockBingoResultBarsGetPointRequirement();
            mockSufficientBingoResult();
            bingoGame.confirmCurrentResult();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, true);
            assertPreviouslySubmittedMatchResultsAreRemoved();
            assertPreviouslySetShipRestrictionIsRemoved();
            assertEquals(2, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldNotUpdateTokenCounterOrProcessConfirmResultActionWhenConfirmingIsNotAllowed()
                throws UserInputException {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockBingoGameActionIsNotAllowed(BingoGameAction.CONFIRM_RESULT);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.confirmCurrentResult());
            verify(mockedTokenCounter, never()).confirmMatchResult();
            verify(mockedBingoGameStateMachine, never()).getCurrentState();
            verify(mockedBingoGameStateMachine, never()).processConfirmResultAction(anyBoolean(), anyBoolean());
            assertPreviouslySubmittedMatchResultsAreNotRemoved(activeRetryRules);
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        private void setupMatchResultsBeforeConfirmation(List<RetryRule> activeRetryRules) throws UserInputException {
            bingoGame.setActiveRetryRules(activeRetryRules);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        }

        private void assertPreviouslySubmittedMatchResultsAreRemoved() throws UserInputException {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        private void assertPreviouslySubmittedMatchResultsAreNotRemoved(List<RetryRule> activeRetryRules)
                throws UserInputException {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isPresent());
            assertTrue(bingoGame.getSharedDivisionAchievements().isPresent());
            assertEquals(activeRetryRules, bingoGame.getActiveRetryRules());
        }

        private void assertPreviouslySetShipRestrictionIsRemoved() throws UserInputException {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        private void assertPreviouslySetShipRestrictionIsNotRemoved() throws UserInputException {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isPresent());
        }
    }

    @Nested
    class DoResetForCurrentLevel {

        @Test
        void shouldBeSuccessfulWhenAllowed() throws UserInputException {
            bingoGame.doResetForCurrentLevel();
            verify(mockedTokenCounter).cancelMatchResult();
            verify(mockedBingoGameStateMachine).processPerformResetAction();
        }

        @Test
        void shouldNotBeSuccessfulWhenNotAllowed() throws UserInputException {
            mockBingoGameActionIsNotAllowed(BingoGameAction.PERFORM_RESET);
            assertMockedUserInputExceptionIsThrown(() -> bingoGame.doResetForCurrentLevel());
            verify(mockedTokenCounter, never()).cancelMatchResult();
            verify(mockedBingoGameStateMachine, never()).processPerformResetAction();
        }

        @Test
        void shouldRemoveAllPreviouslySubmittedMatchResults() throws UserInputException {
            setupMatchResultsBeforeReset();
            bingoGame.doResetForCurrentLevel();
            assertPreviouslySubmittedMatchResultsAreRemoved();
        }

        private void setupMatchResultsBeforeReset() throws UserInputException {
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
        }

        private void assertPreviouslySubmittedMatchResultsAreRemoved() throws UserInputException {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }
    }

    private void setupBingoGameWithPlayers(List<Player> players) throws UserInputException {
        setupBingoGame(players, Collections.emptyList());
    }

    private void setupBingoGame(List<Player> players, List<ChallengeModifier> challengeModifiers)
            throws UserInputException {
        mockBingoGameDependencyInjector();
        bingoGame = new BingoGame(players, challengeModifiers, mockedBingoGameDependencyInjector);
    }

    private void mockBingoGameDependencyInjector() {
        lenient().when(mockedBingoGameDependencyInjector.createBingoGameStateMachine(anyBoolean(), anyBoolean()))
                .thenReturn(mockedBingoGameStateMachine);
        lenient().when(mockedBingoGameDependencyInjector.createBingoResultBars(anyDouble(), anyInt()))
                .thenReturn(mockedBingoResultBars);
        lenient().when(mockedBingoGameDependencyInjector.createTokenCounter(anyBoolean()))
                .thenReturn(mockedTokenCounter);
    }

    private void mockBingoGameActionIsNotAllowed(BingoGameAction action) throws UserInputException {
        doThrow(mockedUserInputException).when(mockedBingoGameStateMachine).ensureActionIsAllowed(action);
    }

    private void mockCurrentBingoGameStateIs(BingoGameState currentState) {
        when(mockedBingoGameStateMachine.getCurrentState()).thenReturn(currentState);
    }

    private void mockBingoResultBarsGetPointRequirement() {
        when(mockedBingoResultBars.getPointRequirementOfLevel(anyInt())).thenAnswer(invocationOnMock -> (
                invocationOnMock.getArgument(0, Integer.class) * 300L));
    }

    private void mockBingoResultBarsGetNumberOfSubsAsReward() {
        when(mockedBingoResultBars.getNumberOfSubsAsRewardForLevel(anyInt())).thenAnswer(this::convertLevelToNumberOfSubs);
    }

    private void mockBingoResultBarsGetNumberOfSubsAsString() {
        when(mockedBingoResultBars.getNumberOfSubsAsStringForLevel(anyInt())).thenAnswer(invocationOnMock -> "Dummy reward text: %s sub(s) 🎁".formatted(
                convertLevelToNumberOfSubs(invocationOnMock)));
    }

    private int convertLevelToNumberOfSubs(InvocationOnMock invocationOnMock) {
        return invocationOnMock.getArgument(0, Integer.class) + 1;
    }

    private void mockInsufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(30L);
    }

    private void mockSufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(3000L);
    }

    private void mockBingoResultGetMainArmamentType() {
        when(mockedBingoResult.getMainArmamentType()).thenReturn(mockedMainArmamentType);
    }

    private void mockShipRestrictionAllowsMainArmamentType() {
        when(mockedShipRestriction.allowsMainArmamentType(mockedMainArmamentType)).thenReturn(true);
    }

    private void assertMockedUserInputExceptionIsThrown(Executable executable) {
        UserInputException exception = assertThrows(UserInputException.class, executable);
        assertSame(mockedUserInputException, exception);
    }

    private void assertUserInputExceptionIsThrownWithMessage(String expectedMessage, Executable executable) {
        UserInputException exception = assertThrows(UserInputException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
