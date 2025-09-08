package bingo.game;

import bingo.game.modifiers.ChallengeModifier;
import bingo.game.results.BingoResult;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BingoGameTest {
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 1800 points ✅ Unlocked reward: 128 subs \uD83C\uDF81 This is the highest reward you can get. Congratulations! \uD83C\uDF8A Total reward: 128 subs + (unused extra lives: 2) * 6 subs = 140 subs \uD83C\uDF81";
    private static final String LEVEL_ONE_GAME_OVER =
            ". Requirement of level 1: 300 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub \uD83C\uDF81";
    private static final String LEVEL_ONE_IMBALANCED_MATCHMAKING =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Imbalanced matchmaking (rule 8a or 8b) ✅ ";
    private static final String LEVEL_ONE_UNFAIR_DISADVANTAGE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Unfair disadvantage (rule 8c) ✅ ";
    private static final String LEVEL_ONE_EXTRA_LIFE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Extra life (rule 8d) ✅ ";
    private static final String LEVEL_ONE_VOLUNTARY_END =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub \uD83C\uDF81";
    private static final String LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub \uD83C\uDF81 Total reward: 1 sub + (unused extra lives: 1) * 6 subs = 7 subs \uD83C\uDF81";
    private static final String LEVEL_ONE_REQUIREMENT =
            "Requirement of level 1: 300 points. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_REQUIREMENT_FOR_TWO_PLAYERS =
            "Requirement of level 1: 420 points. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_REQUIREMENT_FOR_THREE_PLAYERS =
            "Requirement of level 1: 540 points. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION =
            "Requirement of level 1: 300 points. Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String LEVEL_ONE_TRANSITION_TO_TWO =
            ". Requirement of level 1: 300 points ✅ Unlocked reward: 2 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 2: 500 points";
    private static final String LEVEL_TWO_VOLUNTARY_END =
            "Challenge ended voluntarily on level 2. Your reward from the previous level: 2 subs \uD83C\uDF81";
    private static final String LEVEL_TWO_REQUIREMENT =
            "Requirement of level 2: 500 points. Token counter: Dummy token text.";
    private static final String LEVEL_TWO_TRANSITION_TO_THREE =
            ". Requirement of level 2: 500 points ✅ Unlocked reward: 4 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 3: 700 points";
    private static final String MULTIPLAYER_RESTRICTION_B =
            "Requirement of level 1: 540 points. Player B's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESTRICTION_AB =
            "Requirement of level 1: 540 points. Player A's ship restriction: Dummy ship restriction text. Player B's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESTRICTION_ABC =
            "Requirement of level 1: 540 points. Player A's ship restriction: Dummy ship restriction text. Player B's ship restriction: Dummy ship restriction text. Player C's ship restriction: Dummy ship restriction text. Token counter: Dummy token text.";
    private static final String MULTIPLAYER_RESULT_B =
            "Player B's Ribbon Bingo result: Dummy result text. Total result: 30 points. " +
                    MULTIPLAYER_RESTRICTION_ABC;
    private static final String MULTIPLAYER_RESULT_AB =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Total result: 30 points + 30 points = 60 points. " +
                    MULTIPLAYER_RESTRICTION_ABC;
    private static final String MULTIPLAYER_RESULT_ABC =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Player C's Ribbon Bingo result: Dummy result text. Total result: 30 points + 30 points + 30 points = 90 points. Requirement of level 1: 540 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub \uD83C\uDF81";
    private static final String MULTIPLAYER_RESULT_WITH_DIVISION_ACHIEVEMENTS =
            "Player A's Ribbon Bingo result: Dummy result text. Player B's Ribbon Bingo result: Dummy result text. Player C's Ribbon Bingo result: Dummy result text. Shared division achievements: Dummy division text. Total result: 30 points + 30 points + 30 points + 600 points = 690 points. Requirement of level 1: 540 points ✅ Unlocked reward: 2 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 2: 900 points";
    private static final String DUMMY_SHIP_RESTRICTION_TEXT = "Dummy ship restriction text";
    private static final String DUMMY_TOKEN_TEXT = "Token counter: Dummy token text.";
    private static final String DUMMY_RESULT_TEXT = "Ribbon Bingo result: Dummy result text";
    private static final String DUMMY_DIVISION_TEXT = "Shared division achievements: Dummy division text";
    private static final String INCORRECT_NUMBER_OF_PLAYERS = "The number of players must be between 1 and 3";
    private static final String INCORRECT_PLAYER = "Player Player D is not part of the game";
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
    private TokenCounter mockedTokenCounter;
    @Mock
    private BingoGameStateMachine mockedBingoGameStateMachine;
    @Mock
    private BingoResult mockedBingoResult;
    @Mock
    private ShipRestriction mockedShipRestriction;
    @Mock
    private SharedDivisionAchievements mockedDivisionAchievements;

    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        setupBingoGameWithPlayers(List.of(SINGLE_PLAYER));
    }

    @Nested
    class ActionIsAllowed {

        @Test
        void shouldDelegateToBingoGameStateMachineAndReturnTrue() {
            mockBingoGameActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
            assertTrue(bingoGame.actionIsAllowed(BingoGameAction.CONFIRM_RESULT));
        }

        @Test
        void shouldDelegateToBingoGameStateMachineAndReturnFalse() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertFalse(bingoGame.actionIsAllowed(BingoGameAction.SUBMIT_RESULT));
        }
    }

    @Nested
    class SubmitAndGetSharedDivisionAchievements {

        @Test
        void submitTwiceShouldBeSuccessful() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitSharedDivisionAchievementsIsSuccessful();
            assertSubmitSharedDivisionAchievementsIsSuccessful();
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWhenSetIsAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitSharedDivisionAchievementsIsSuccessful();
            verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(false, false);
        }

        @Test
        void submitShouldNotUpdateTokenCounterOrProcessSubmitResultActionWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitSharedDivisionAchievementsIsNotSuccessful();
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
            verify(mockedBingoGameStateMachine, never()).processSubmitResultAction(anyBoolean(), anyBoolean());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoSharedDivisionAchievementsAreSet() {
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitSharedDivisionAchievementsIsNotSuccessful();
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
        }

        @Test
        void getShouldReturnTheSharedDivisionAchievementsWhichWereSet() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitSharedDivisionAchievementsIsSuccessful();
            Optional<SharedDivisionAchievements> returnedDivisionAchievements =
                    bingoGame.getSharedDivisionAchievements();
            assertTrue(returnedDivisionAchievements.isPresent());
            assertEquals(mockedDivisionAchievements, returnedDivisionAchievements.get());
        }

        private void assertSubmitSharedDivisionAchievementsIsSuccessful() {
            assertTrue(bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements));
        }

        private void assertSubmitSharedDivisionAchievementsIsNotSuccessful() {
            assertFalse(bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements));
        }
    }

    @Nested
    class SetAndGetActiveRetryRules {

        @Test
        void setShouldOverwriteTheListWhichWasPreviouslySet() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertSetActiveRetryRulesIsSuccessful(List.of(
                    RetryRule.IMBALANCED_MATCHMAKING,
                    RetryRule.UNFAIR_DISADVANTAGE));
            assertSetActiveRetryRulesIsSuccessful(Collections.emptyList());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void setShouldUpdateTokenCounterWhenSetIsAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertSetActiveRetryRulesIsSuccessful(activeRetryRules);
            verify(mockedTokenCounter).calculateMatchResult(false, true, activeRetryRules);
        }

        @Test
        void setShouldNotUpdateTokenCounterWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertSetActiveRetryRulesIsNotSuccessful(activeRetryRules);
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
        }

        @Test
        void getShouldReturnEmptyListWhenNoActiveRetryRulesAreSet() {
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void getShouldReturnEmptyListWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertSetActiveRetryRulesIsNotSuccessful(activeRetryRules);
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        @Test
        void getShouldReturnTheActiveRetryRulesWhichWereSet() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            assertSetActiveRetryRulesIsSuccessful(activeRetryRules);
            assertEquals(activeRetryRules, bingoGame.getActiveRetryRules());
        }

        private void assertSetActiveRetryRulesIsSuccessful(List<RetryRule> activeRetryRules) {
            assertTrue(bingoGame.setActiveRetryRules(activeRetryRules));
        }

        private void assertSetActiveRetryRulesIsNotSuccessful(List<RetryRule> activeRetryRules) {
            assertFalse(bingoGame.setActiveRetryRules(activeRetryRules));
        }
    }

    @Nested
    class SetGetAndRemoveShipRestrictionForPlayer {

        @Test
        void setShouldBeSuccessfulWhenNoShipRestrictionIsSet() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
        }

        @Test
        void setShouldBeSuccessfulWhenThePreviousShipRestrictionIsRemoved() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
            assertRemoveShipRestrictionIsSuccessful();
            assertSetShipRestrictionIsSuccessful();
        }

        @Test
        void setShouldNotBeSuccessfulWhenAShipRestrictionIsAlreadySet() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
            assertSetShipRestrictionIsNotSuccessful();
        }

        @Test
        void setShouldThrowIllegalArgumentException() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.setShipRestrictionForPlayer(PLAYER_D, mockedShipRestriction));
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoShipRestrictionIsSet() {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsNotSuccessful();
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnTheShipRestrictionWhichWasSet() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
            Optional<ShipRestriction> returnedShipRestriction = bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER);
            assertTrue(returnedShipRestriction.isPresent());
            assertEquals(mockedShipRestriction, returnedShipRestriction.get());
        }

        @Test
        void getShouldThrowIllegalArgumentException() {
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.getShipRestrictionForPlayer(PLAYER_D));
        }

        @Test
        void removeShouldRemoveTheShipRestrictionWhichWasPreviouslySet() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
            assertRemoveShipRestrictionIsSuccessful();
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenNotAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertSetShipRestrictionIsSuccessful();
            mockBingoGameActionIsNotAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertRemoveShipRestrictionIsNotSuccessful();
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isPresent());
        }

        @Test
        void removeShouldThrowIllegalArgumentException() {
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.removeShipRestrictionForPlayer(PLAYER_D));
        }

        private void assertSetShipRestrictionIsSuccessful() {
            assertTrue(bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
        }

        private void assertSetShipRestrictionIsNotSuccessful() {
            assertFalse(bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
        }

        private void assertRemoveShipRestrictionIsSuccessful() {
            assertTrue(bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER));
        }

        private void assertRemoveShipRestrictionIsNotSuccessful() {
            assertFalse(bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER));
        }
    }

    @Nested
    class AddGetAndRemoveShipsUsed {

        @Test
        void addShouldBeSuccessfulWhenShipNamesAreUnique() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_A));
            assertTrue(bingoGame.addShipUsed(SHIP_B));
            assertTrue(bingoGame.addShipUsed(SHIP_C));
            assertEquals(3, bingoGame.getShipsUsed().size());
        }

        @Test
        void addShouldNotBeSuccessfulWhenTheShipWasAlreadyAdded() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_D));
            assertFalse(bingoGame.addShipUsed(new Ship("ship d")));
            assertEquals(1, bingoGame.getShipsUsed().size());
        }

        @Test
        void getShouldReturnEmptyListWhenNoShipsWereAdded() {
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void getShouldReturnEmptyListWhenAddIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            assertFalse(bingoGame.addShipUsed(SHIP_D));
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void getShouldReturnListOfShipsInTheOrderTheyWereAdded() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_A));
            assertTrue(bingoGame.addShipUsed(SHIP_B));
            assertTrue(bingoGame.addShipUsed(SHIP_C));
            List<Ship> shipsUsed = bingoGame.getShipsUsed();
            assertEquals(3, shipsUsed.size());
            Iterator<Ship> shipIterator = shipsUsed.iterator();
            assertEquals(SHIP_A, shipIterator.next());
            assertEquals(SHIP_B, shipIterator.next());
            assertEquals(SHIP_C, shipIterator.next());
        }

        @Test
        void removeShouldBeSuccessfulWhenTheShipWasPreviouslyAdded() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_A));
            assertTrue(bingoGame.removeShipUsed(SHIP_A));
            assertTrue(bingoGame.getShipsUsed().isEmpty());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenTheShipWasNeverAdded() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_A));
            assertFalse(bingoGame.removeShipUsed(SHIP_B));
            assertEquals(1, bingoGame.getShipsUsed().size());
        }

        @Test
        void removeShouldNotBeSuccessfulWhenNotAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            assertTrue(bingoGame.addShipUsed(SHIP_B));
            mockBingoGameActionIsNotAllowed(BingoGameAction.OTHER_ACTION);
            assertFalse(bingoGame.removeShipUsed(SHIP_B));
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
        void shouldReturnThePlayersWhichWereRegistered() {
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
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_NUMBER_OF_PLAYERS,
                    () -> setupBingoGameWithPlayers(Collections.emptyList()));
        }

        @Test
        void setupShouldFailBecauseMoreThanThreePlayersWereRegistered() {
            List<Player> players = List.of(PLAYER_A, PLAYER_B, PLAYER_C, PLAYER_D);
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_NUMBER_OF_PLAYERS,
                    () -> setupBingoGameWithPlayers(players));
        }

        @Test
        void shouldFilterCorrectChallengeModifiersForSoloStreamerChallenge() {
            setupBingoGame(List.of(SINGLE_PLAYER), allModifiers);
            List<ChallengeModifier> allowedModifiers = bingoGame.getChallengeModifiers();
            assertEquals(allModifiers.size() - 1, allowedModifiers.size());
            assertFalse(allowedModifiers.contains(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE));
        }

        @Test
        void shouldFilterCorrectChallengeModifiersForDuoTrioStreamerChallenge() {
            setupBingoGame(List.of(PLAYER_A, PLAYER_B, PLAYER_C), allModifiers);
            List<ChallengeModifier> allowedModifiers = bingoGame.getChallengeModifiers();
            assertEquals(allModifiers.size() - 1, allowedModifiers.size());
            assertFalse(allowedModifiers.contains(ChallengeModifier.NO_HELP));
        }
    }

    @Nested
    class GetAllResultBarsAndRewardsInTableFormat {

        @Test
        void shouldReturnLongString() {
            String expectedString = """
                    | Level | Points required | Number of subs as reward: 2^(Level) |
                    |---|---:|---:|
                    | 0 | 0 | 2^0 = 1 sub \uD83C\uDF81 |
                    | 1 | 300 | 2^1 = 2 subs \uD83C\uDF81 |
                    | 2 | 500 | 2^2 = 4 subs \uD83C\uDF81 |
                    | 3 | 700 | 2^3 = 8 subs \uD83C\uDF81 |
                    | 4 | 900 | 2^4 = 16 subs \uD83C\uDF81 |
                    | 5 | 1200 | 2^5 = 32 subs \uD83C\uDF81 |
                    | 6 | 1500 | 2^6 = 64 subs \uD83C\uDF81 |
                    | 7 | 1800 | 2^7 = 128 subs \uD83C\uDF81 |
                    """;
            assertEquals(expectedString, bingoGame.getAllResultBarsAndRewardsInTableFormat());
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnFirstResultBarWhenNoResultWasSubmitted() {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            assertToStringMethodReturnsFirstResultBar();
        }

        @Test
        void shouldReturnFirstResultBarWithShipRestrictionWhenNoResultWasSubmitted() {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            mockTokenCounterToString();
            mockShipRestrictionGetDisplayText();
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
            assertEquals(LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION, bingoGame.toString());
        }

        @Test
        void shouldReturnGameOverWhenSubmittedResultIsInsufficient() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoResultToString();
            mockInsufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER, bingoGame.toString());
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedDueToImbalancedMatchmaking() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockInsufficientBingoResult();
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_IMBALANCED_MATCHMAKING + DUMMY_TOKEN_TEXT, bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedDueToAnUnfairDisadvantage() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockInsufficientBingoResult();
            bingoGame.setActiveRetryRules(List.of(RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_UNFAIR_DISADVANTAGE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        }

        @Test
        void shouldReturnRetryAllowedBecauseOfAnExtraLife() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockTokenCounterHasExtraLife();
            mockInsufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_EXTRA_LIFE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockSufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_TRANSITION_TO_TWO, bingoGame.toString());
        }

        @Test
        void shouldReturnSecondResultBarWhenNoResultWasSubmitted() {
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            assertToStringMethodReturnsSecondResultBar();
        }

        @Test
        void shouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockSufficientBingoResult();
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            assertEquals(DUMMY_RESULT_TEXT + LEVEL_TWO_TRANSITION_TO_THREE, bingoGame.toString());
        }

        @Test
        void shouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() {
            skipLevelsUntilReachingLevel(7);
            mockBingoResultToString();
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(2);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
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
        void shouldReturnLevelTwoVoluntaryEnd() {
            skipLevelsUntilReachingLevel(2);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_TWO_VOLUNTARY_END, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelOneVoluntaryEnd() {
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_ONE_VOLUNTARY_END, bingoGame.toString());
        }

        @Test
        void shouldReturnLevelOneVoluntaryEndWithExtraLife() {
            mockTokenCounterHasExtraLife();
            mockExtraLivesInTokenCounterAre(1);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_VOLUNTARY_END);
            assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE, bingoGame.toString());
            mockCurrentBingoGameStateIs(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
            assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldShowHigherRequirementForTwoPlayers() {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B));
            assertEquals(LEVEL_ONE_REQUIREMENT_FOR_TWO_PLAYERS, bingoGame.toString());
        }

        @Test
        void shouldShowHigherRequirementForThreePlayers() {
            mockCurrentBingoGameStateIs(BingoGameState.LEVEL_INITIALIZED);
            mockTokenCounterToString();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            assertEquals(LEVEL_ONE_REQUIREMENT_FOR_THREE_PLAYERS, bingoGame.toString());
        }

        @Test
        void shouldUpdateStepByStepForMultiplayer() {
            mockCurrentBingoGameStateIs(BingoGameState.PARTIAL_RESULT_SUBMITTED);
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            mockBingoResultToString();
            mockTokenCounterToString();
            mockShipRestrictionGetDisplayText();
            mockSharedDivisionAchievementsToString();
            mockInsufficientBingoResult();
            mockSharedDivisionAchievementsGetPointValue();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            setShipRestrictionsOneByOneAndCheckResults();
            submitBingoResultsOneByOneAndCheckResults();
            submitDivisionAchievementsAndCheckResults();
        }

        private void setShipRestrictionsOneByOneAndCheckResults() {
            bingoGame.setShipRestrictionForPlayer(PLAYER_B, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_B, bingoGame.toString());
            bingoGame.setShipRestrictionForPlayer(PLAYER_A, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_AB, bingoGame.toString());
            bingoGame.setShipRestrictionForPlayer(PLAYER_C, mockedShipRestriction);
            assertEquals(MULTIPLAYER_RESTRICTION_ABC, bingoGame.toString());
        }

        private void submitBingoResultsOneByOneAndCheckResults() {
            bingoGame.submitBingoResultForPlayer(PLAYER_B, mockedBingoResult);
            assertEquals(MULTIPLAYER_RESULT_B, bingoGame.toString());
            bingoGame.submitBingoResultForPlayer(PLAYER_A, mockedBingoResult);
            assertEquals(MULTIPLAYER_RESULT_AB, bingoGame.toString());
            bingoGame.submitBingoResultForPlayer(PLAYER_C, mockedBingoResult);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH);
            assertEquals(MULTIPLAYER_RESULT_ABC, bingoGame.toString());
        }

        private void submitDivisionAchievementsAndCheckResults() {
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            mockCurrentBingoGameStateIs(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH);
            assertEquals(MULTIPLAYER_RESULT_WITH_DIVISION_ACHIEVEMENTS, bingoGame.toString());
        }

        private void skipLevelsUntilReachingLevel(int levelToReach) {
            if (levelToReach < 2 || levelToReach > MAX_LEVEL) {
                fail("Parameter 'levelToReach' must not be lower than 2 or greater than MAX_LEVEL");
            }
            mockSufficientBingoResult();
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
            mockBingoGameStateMachineForConfirmAction(
                    BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH,
                    BingoGameState.LEVEL_INITIALIZED);
            while (bingoGame.getCurrentLevel() < levelToReach) {
                bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
                bingoGame.confirmCurrentResult();
            }
        }

        private void mockCurrentBingoGameStateIs(BingoGameState currentState) {
            when(mockedBingoGameStateMachine.getCurrentState()).thenReturn(currentState);
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

        private void assertToStringMethodReturnsFirstResultBar() {
            assertEquals(LEVEL_ONE_REQUIREMENT, bingoGame.toString());
        }

        private void assertToStringMethodReturnsSecondResultBar() {
            assertEquals(LEVEL_TWO_REQUIREMENT, bingoGame.toString());
        }
    }

    @Nested
    class EndChallenge {

        @Test
        void shouldBeSuccessfulWhenAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
            assertEndChallengeIsSuccessful();
            verify(mockedBingoGameStateMachine).processEndChallengeVoluntarilyAction();
        }

        @Test
        void shouldNotBeSuccessfulWhenNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
            assertEndChallengeIsNotSuccessful();
            verify(mockedBingoGameStateMachine, never()).processEndChallengeVoluntarilyAction();
        }

        private void assertEndChallengeIsSuccessful() {
            assertTrue(bingoGame.endChallenge());
        }

        private void assertEndChallengeIsNotSuccessful() {
            assertFalse(bingoGame.endChallenge());
        }
    }

    @Nested
    class SubmitAndGetBingoResult {

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithInsufficientResult() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockInsufficientBingoResult();
            assertSubmitBingoResultIsSuccessful();
            verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, false);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithSufficientResult() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockSufficientBingoResult();
            assertSubmitBingoResultIsSuccessful();
            verify(mockedTokenCounter).calculateMatchResult(true, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, true);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithActiveRetryRules() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            bingoGame.setActiveRetryRules(activeRetryRules);
            assertSubmitBingoResultIsSuccessful();
            verify(mockedTokenCounter, times(2)).calculateMatchResult(false, true, activeRetryRules);
            verify(mockedBingoGameStateMachine).processSubmitResultAction(true, false);
        }

        @Test
        void submitShouldUpdateTokenCounterAndProcessSubmitResultActionWithMultiplePlayers() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockSufficientBingoResult();
            setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
            assertTrue(bingoGame.submitBingoResultForPlayer(PLAYER_A, mockedBingoResult));
            assertTrue(bingoGame.submitBingoResultForPlayer(PLAYER_B, mockedBingoResult));
            assertTrue(bingoGame.submitBingoResultForPlayer(PLAYER_C, mockedBingoResult));
            verify(mockedTokenCounter, times(3)).calculateMatchResult(true, true, Collections.emptyList());
            verify(mockedBingoGameStateMachine, times(2)).processSubmitResultAction(false, true);
            verify(mockedBingoGameStateMachine, times(1)).processSubmitResultAction(true, true);
        }

        @Test
        void submitShouldNotUpdateTokenCounterOrProcessSubmitResultActionWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitBingoResultIsNotSuccessful();
            verify(mockedTokenCounter, never()).calculateMatchResult(anyBoolean(), anyBoolean(), anyList());
            verify(mockedBingoGameStateMachine, never()).processSubmitResultAction(anyBoolean(), anyBoolean());
        }

        @Test
        void submitShouldThrowIllegalArgumentException() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.submitBingoResultForPlayer(PLAYER_D, mockedBingoResult));
        }

        @Test
        void getShouldReturnEmptyOptionalWhenNoBingoResultIsSet() {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnEmptyOptionalWhenSetIsNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitBingoResultIsNotSuccessful();
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
        }

        @Test
        void getShouldReturnTheBingoResultWhichWasSet() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            assertSubmitBingoResultIsSuccessful();
            Optional<BingoResult> returnedBingoResult = bingoGame.getBingoResultForPlayer(SINGLE_PLAYER);
            assertTrue(returnedBingoResult.isPresent());
            assertEquals(mockedBingoResult, returnedBingoResult.get());
        }

        @Test
        void getShouldThrowIllegalArgumentException() {
            assertIllegalArgumentExceptionIsThrownWithMessage(
                    INCORRECT_PLAYER,
                    () -> bingoGame.getBingoResultForPlayer(PLAYER_D));
        }

        private void assertSubmitBingoResultIsSuccessful() {
            assertTrue(bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult));
        }

        private void assertSubmitBingoResultIsNotSuccessful() {
            assertFalse(bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult));
        }
    }

    @Nested
    class ConfirmCurrentResult {

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithChallengeEndedVoluntarilyAsNewState() {
            List<RetryRule> activeRetryRules = Collections.emptyList();
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockBingoGameActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
            mockBingoGameStateMachineForConfirmAction(
                    BingoGameState.UNCONFIRMED_VOLUNTARY_END,
                    BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
            assertConfirmCurrentResultIsSuccessful();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine, times(2)).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, false);
            assertPreviouslySubmittedMatchResultsAreNotRemoved(activeRetryRules);
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithUnconfirmedSuccessfulMatchAsPreviousState() {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockBingoGameActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
            mockBingoGameStateMachineForConfirmAction(
                    BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH,
                    BingoGameState.LEVEL_INITIALIZED);
            assertConfirmCurrentResultIsSuccessful();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine, times(2)).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, true);
            assertPreviouslySubmittedMatchResultsAreRemoved();
            assertPreviouslySetShipRestrictionIsRemoved();
            assertEquals(2, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldUpdateTokenCounterAndProcessConfirmResultActionWithUnconfirmedUnsuccessfulMatchAsPreviousState() {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockBingoGameActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
            mockBingoGameStateMachineForConfirmAction(
                    BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH,
                    BingoGameState.LEVEL_INITIALIZED);
            assertConfirmCurrentResultIsSuccessful();
            verify(mockedTokenCounter).confirmMatchResult();
            verify(mockedBingoGameStateMachine, times(2)).getCurrentState();
            verify(mockedBingoGameStateMachine).processConfirmResultAction(true, true);
            assertPreviouslySubmittedMatchResultsAreRemoved();
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        @Test
        void shouldNotUpdateTokenCounterOrProcessConfirmResultActionWhenConfirmingIsNotAllowed() {
            List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
            setupMatchResultsBeforeConfirmation(activeRetryRules);
            mockBingoGameActionIsNotAllowed(BingoGameAction.CONFIRM_RESULT);
            assertConfirmCurrentResultIsNotSuccessful();
            verify(mockedTokenCounter, never()).confirmMatchResult();
            verify(mockedBingoGameStateMachine, never()).getCurrentState();
            verify(mockedBingoGameStateMachine, never()).processConfirmResultAction(anyBoolean(), anyBoolean());
            assertPreviouslySubmittedMatchResultsAreNotRemoved(activeRetryRules);
            assertPreviouslySetShipRestrictionIsNotRemoved();
            assertEquals(1, bingoGame.getCurrentLevel());
        }

        private void setupMatchResultsBeforeConfirmation(List<RetryRule> activeRetryRules) {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            bingoGame.setActiveRetryRules(activeRetryRules);
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
            bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        }

        private void assertConfirmCurrentResultIsSuccessful() {
            assertTrue(bingoGame.confirmCurrentResult());
        }

        private void assertConfirmCurrentResultIsNotSuccessful() {
            assertFalse(bingoGame.confirmCurrentResult());
        }

        private void assertPreviouslySubmittedMatchResultsAreRemoved() {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }

        private void assertPreviouslySubmittedMatchResultsAreNotRemoved(List<RetryRule> activeRetryRules) {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isPresent());
            assertTrue(bingoGame.getSharedDivisionAchievements().isPresent());
            assertEquals(activeRetryRules, bingoGame.getActiveRetryRules());
        }

        private void assertPreviouslySetShipRestrictionIsRemoved() {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
        }

        private void assertPreviouslySetShipRestrictionIsNotRemoved() {
            assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isPresent());
        }
    }

    @Nested
    class DoResetForCurrentLevel {

        @Test
        void shouldBeSuccessfulWhenAllowed() {
            mockBingoGameActionIsAllowed(BingoGameAction.PERFORM_RESET);
            assertResetForCurrentLevelIsSuccessful();
            verify(mockedTokenCounter).cancelMatchResult();
            verify(mockedBingoGameStateMachine).processPerformResetAction();
        }

        @Test
        void shouldNotBeSuccessfulWhenNotAllowed() {
            mockBingoGameActionIsNotAllowed(BingoGameAction.PERFORM_RESET);
            assertResetForCurrentLevelIsNotSuccessful();
            verify(mockedTokenCounter, never()).cancelMatchResult();
            verify(mockedBingoGameStateMachine, never()).processPerformResetAction();
        }

        @Test
        void shouldRemoveAllPreviouslySubmittedMatchResults() {
            mockBingoGameActionIsAllowed(BingoGameAction.PERFORM_RESET);
            setupMatchResultsBeforeReset();
            assertResetForCurrentLevelIsSuccessful();
            assertPreviouslySubmittedMatchResultsAreRemoved();
        }

        private void setupMatchResultsBeforeReset() {
            mockBingoGameActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
            mockBingoGameActionIsAllowed(BingoGameAction.OTHER_ACTION);
            bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult);
            bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
        }

        private void assertResetForCurrentLevelIsSuccessful() {
            assertTrue(bingoGame.doResetForCurrentLevel());
        }

        private void assertResetForCurrentLevelIsNotSuccessful() {
            assertFalse(bingoGame.doResetForCurrentLevel());
        }

        private void assertPreviouslySubmittedMatchResultsAreRemoved() {
            assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
            assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
            assertTrue(bingoGame.getActiveRetryRules().isEmpty());
        }
    }

    private void setupBingoGameWithPlayers(List<Player> players) {
        setupBingoGame(players, Collections.emptyList());
    }

    private void setupBingoGame(List<Player> players, List<ChallengeModifier> challengeModifiers) {
        bingoGame = new BingoGame(players, challengeModifiers, mockedTokenCounter, mockedBingoGameStateMachine);
    }

    private void mockBingoGameActionIsAllowed(BingoGameAction action) {
        when(mockedBingoGameStateMachine.actionIsAllowed(action)).thenReturn(true);
    }

    private void mockBingoGameActionIsNotAllowed(BingoGameAction action) {
        when(mockedBingoGameStateMachine.actionIsAllowed(action)).thenReturn(false);
    }

    private void mockBingoGameStateMachineForConfirmAction(BingoGameState previousState, BingoGameState newState) {
        AtomicBoolean isConfirmed = new AtomicBoolean(false);
        when(mockedBingoGameStateMachine.getCurrentState()).thenAnswer(_ -> {
            BingoGameState stateToReturn = isConfirmed.get() ? newState : previousState;
            if (!stateToReturn.isFinal()) {
                isConfirmed.set(false);
            }
            return stateToReturn;
        });
        doAnswer(_ -> {
            isConfirmed.set(true);
            return null;
        }).when(mockedBingoGameStateMachine).processConfirmResultAction(anyBoolean(), anyBoolean());
    }

    private void mockInsufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(30L);
    }

    private void mockSufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(3000L);
    }

    private void assertIllegalArgumentExceptionIsThrownWithMessage(String expectedMessage, Executable executable) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
