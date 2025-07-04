package bingo.game;

import bingo.game.results.BingoResult;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BingoGameTest {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 1800 points ✅ Unlocked reward: 128 subs \uD83C\uDF81 This is the highest reward you can get. Congratulations! \uD83C\uDF8A Total reward: 128 + (unused extra lives: 2) * 6 = 140 subs \uD83C\uDF81";
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
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub \uD83C\uDF81 Total reward: 1 + (unused extra lives: 1) * 6 = 7 subs \uD83C\uDF81";
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

    @Test
    void submitSharedDivisionAchievementsTwiceShouldBeSuccessful() {
        assertSubmitSharedDivisionAchievementsIsSuccessful();
        assertSubmitSharedDivisionAchievementsIsSuccessful();
    }

    @Test
    void submitSharedDivisionAchievementsShouldUpdateTokenCounter() {
        assertSubmitSharedDivisionAchievementsIsSuccessful();
        verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
    }

    @Test
    void getSharedDivisionAchievementsShouldReturnEmptyOptionalWhenNoneAreSet() {
        assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
    }

    @Test
    void getSharedDivisionAchievementsShouldReturnTheOnesWhichWereSet() {
        assertSubmitSharedDivisionAchievementsIsSuccessful();
        Optional<SharedDivisionAchievements> returnedDivisionAchievements = bingoGame.getSharedDivisionAchievements();
        assertTrue(returnedDivisionAchievements.isPresent());
        assertEquals(mockedDivisionAchievements, returnedDivisionAchievements.get());
    }

    @Test
    void setActiveRetryRulesShouldOverwriteTheListWhichWasPreviouslySet() {
        setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE));
        setActiveRetryRules(Collections.emptyList());
        assertTrue(bingoGame.getActiveRetryRules().isEmpty());
    }

    @Test
    void setActiveRetryRulesShouldUpdateTokenCounter() {
        List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
        setActiveRetryRules(activeRetryRules);
        verify(mockedTokenCounter).calculateMatchResult(false, true, activeRetryRules);
    }

    @Test
    void getActiveRetryRulesShouldReturnEmptyListWhenNoneAreSet() {
        assertTrue(bingoGame.getActiveRetryRules().isEmpty());
    }

    @Test
    void getActiveRetryRulesShouldReturnTheOnesWhichWereSet() {
        List<RetryRule> activeRetryRules = List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE);
        setActiveRetryRules(activeRetryRules);
        assertEquals(activeRetryRules, bingoGame.getActiveRetryRules());
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenNoneIsSet() {
        assertTrue(bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenThePreviousOneIsRemoved() {
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER);
        assertTrue(bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
    }

    @Test
    void setShipRestrictionShouldNotBeSuccessfulWhenOneIsAlreadySet() {
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        assertFalse(bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction));
    }

    @Test
    void setShipRestrictionShouldThrowIllegalArgumentException() {
        assertIllegalArgumentExceptionIsThrownWithMessage(
                INCORRECT_PLAYER,
                () -> bingoGame.setShipRestrictionForPlayer(
                        PLAYER_D,
                        mockedShipRestriction));
    }

    @Test
    void getShipRestrictionShouldReturnEmptyOptionalWhenNoneIsSet() {
        assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
    }

    @Test
    void getShipRestrictionShouldReturnTheOneWhichWasSet() {
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        Optional<ShipRestriction> returnedShipRestriction = bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER);
        assertTrue(returnedShipRestriction.isPresent());
        assertEquals(mockedShipRestriction, returnedShipRestriction.get());
    }

    @Test
    void getShipRestrictionShouldThrowIllegalArgumentException() {
        assertIllegalArgumentExceptionIsThrownWithMessage(
                INCORRECT_PLAYER,
                () -> bingoGame.getShipRestrictionForPlayer(PLAYER_D));
    }

    @Test
    void removeShipRestrictionShouldRemoveTheRestrictionWhichWasPreviouslySet() {
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        bingoGame.removeShipRestrictionForPlayer(SINGLE_PLAYER);
        assertTrue(bingoGame.getShipRestrictionForPlayer(SINGLE_PLAYER).isEmpty());
    }

    @Test
    void removeShipRestrictionShouldThrowIllegalArgumentException() {
        assertIllegalArgumentExceptionIsThrownWithMessage(
                INCORRECT_PLAYER,
                () -> bingoGame.removeShipRestrictionForPlayer(PLAYER_D));
    }

    @Test
    void addShipUsedShouldBeSuccessfulWhenShipNamesAreUnique() {
        assertTrue(bingoGame.addShipUsed(SHIP_A));
        assertTrue(bingoGame.addShipUsed(SHIP_B));
        assertTrue(bingoGame.addShipUsed(SHIP_C));
        assertEquals(3, bingoGame.getShipsUsed().size());
    }

    @Test
    void addShipUsedShouldNotBeSuccessfulWhenItWasAlreadyAdded() {
        bingoGame.addShipUsed(SHIP_D);
        assertFalse(bingoGame.addShipUsed(new Ship("ship d")));
        assertEquals(1, bingoGame.getShipsUsed().size());
    }

    @Test
    void getShipsUsedShouldReturnEmptyList() {
        assertTrue(bingoGame.getShipsUsed().isEmpty());
    }

    @Test
    void getShipsUsedShouldReturnListOfShipsInTheOrderTheyWereAdded() {
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
    void removeShipUsedShouldBeSuccessfulWhenItWasPreviouslyAdded() {
        bingoGame.addShipUsed(SHIP_A);
        assertTrue(bingoGame.removeShipUsed(SHIP_A));
        assertTrue(bingoGame.getShipsUsed().isEmpty());
    }

    @Test
    void removeShipUsedShouldNotBeSuccessfulWhenItWasNeverAdded() {
        bingoGame.addShipUsed(SHIP_A);
        assertFalse(bingoGame.removeShipUsed(SHIP_B));
        assertEquals(1, bingoGame.getShipsUsed().size());
    }

    @Test
    void getPlayersShouldReturnSinglePlayer() {
        assertEquals(List.of(SINGLE_PLAYER), bingoGame.getPlayers());
    }

    @Test
    void getPlayersShouldReturnTheOnesWhichWereRegistered() {
        List<Player> players = List.of(PLAYER_A, PLAYER_B, PLAYER_C);
        setupBingoGameWithPlayers(players);
        assertEquals(players, bingoGame.getPlayers());
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
    void getAllResultBarsAndRewardsInTableFormatShouldReturnLongString() {
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

    @Test
    void toStringMethodShouldReturnFirstResultBarWhenNoResultWasSubmitted() {
        mockTokenCounterToString();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnFirstResultBarWithShipRestrictionWhenNoResultWasSubmitted() {
        mockTokenCounterToString();
        mockShipRestrictionGetDisplayText();
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        assertEquals(LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnGameOverWhenSubmittedResultIsInsufficient() {
        mockBingoResultToString();
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedDueToImbalancedMatchmaking() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockInsufficientBingoResult();
        setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING));
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_IMBALANCED_MATCHMAKING + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedDueToAnUnfairDisadvantage() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockInsufficientBingoResult();
        setActiveRetryRules(List.of(RetryRule.UNFAIR_DISADVANTAGE));
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_UNFAIR_DISADVANTAGE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedBecauseOfAnExtraLife() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockTokenCounterHasExtraLife();
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_EXTRA_LIFE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_TRANSITION_TO_TWO, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWhenNoResultWasSubmitted() {
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWithoutShipRestrictionWhenNoResultWasSubmitted() {
        mockTokenCounterToString();
        mockSufficientBingoResult();
        bingoGame.setShipRestrictionForPlayer(SINGLE_PLAYER, mockedShipRestriction);
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_TWO_TRANSITION_TO_THREE, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterHasExtraLife();
        mockExtraLivesInTokenCounterAre(2);
        mockSufficientBingoResult();
        for (int level = START_LEVEL; level < MAX_LEVEL; level++) {
            assertSubmitBingoResultIsSuccessful();
            assertConfirmCurrentResultIsSuccessful();
        }
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(
                DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS + END_OF_CHALLENGE_CONFIRMED,
                bingoGame.toString());
        verify(mockedTokenCounter, times(MAX_LEVEL - 1)).calculateMatchResult(true, true, Collections.emptyList());
        verify(mockedTokenCounter, times(1)).calculateMatchResult(true, false, Collections.emptyList());
        verify(mockedTokenCounter, times(MAX_LEVEL)).confirmMatchResult();
    }

    @Test
    void toStringMethodShouldShowHigherRequirementForTwoPlayers() {
        mockTokenCounterToString();
        setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B));
        assertEquals(LEVEL_ONE_REQUIREMENT_FOR_TWO_PLAYERS, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldShowHigherRequirementForThreePlayers() {
        mockTokenCounterToString();
        setupBingoGameWithPlayers(List.of(PLAYER_A, PLAYER_B, PLAYER_C));
        assertEquals(LEVEL_ONE_REQUIREMENT_FOR_THREE_PLAYERS, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldUpdateStepByStepForMultiplayer() {
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

    @Test
    void endChallengeShouldNotBePossibleWhenAnyResultIsSubmitted() {
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEndChallengeIsNotSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_TWO_VOLUNTARY_END, bingoGame.toString());
    }

    @Test
    void endChallengeShouldBePossibleWhenNoResultWasSubmitted() {
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END, bingoGame.toString());
    }

    @Test
    void endChallengeShouldConvertExtraLivesToSubs() {
        mockTokenCounterHasExtraLife();
        mockExtraLivesInTokenCounterAre(1);
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
    }

    @Test
    void endChallengeTwiceShouldBePossibleButHaveNoAdditionalEffect() {
        assertEndChallengeIsSuccessful();
        endChallengeShouldBePossibleWhenNoResultWasSubmitted();
    }

    @Test
    void submitBingoResultShouldBePossibleIfEndOfChallengeIsNotConfirmed() {
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertEndChallengeIsSuccessful();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void submitBingoResultShouldBePossibleIfPreviousResultIsNotConfirmed() {
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
    }

    @Test
    void submitBingoResultShouldUpdateTokenCounter() {
        assertSubmitBingoResultIsSuccessful();
        verify(mockedTokenCounter).calculateMatchResult(false, true, Collections.emptyList());
    }

    @Test
    void submitBingoResultShouldThrowIllegalArgumentException() {
        assertIllegalArgumentExceptionIsThrownWithMessage(
                INCORRECT_PLAYER,
                () -> bingoGame.submitBingoResultForPlayer(
                        PLAYER_D,
                        mockedBingoResult));
    }

    @Test
    void getBingoResultShouldReturnEmptyOptionalWhenNoneIsSet() {
        assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
    }

    @Test
    void getBingoResultShouldReturnTheOneWhichWasSet() {
        assertSubmitBingoResultIsSuccessful();
        Optional<BingoResult> returnedBingoResult = bingoGame.getBingoResultForPlayer(SINGLE_PLAYER);
        assertTrue(returnedBingoResult.isPresent());
        assertEquals(mockedBingoResult, returnedBingoResult.get());
    }

    @Test
    void getBingoResultShouldThrowIllegalArgumentException() {
        assertIllegalArgumentExceptionIsThrownWithMessage(
                INCORRECT_PLAYER,
                () -> bingoGame.getBingoResultForPlayer(PLAYER_D));
    }

    @Test
    void confirmCurrentResultShouldNotBePossibleWhenThereIsNothingToConfirm() {
        mockTokenCounterToString();
        assertConfirmCurrentResultIsNotSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldBePossibleWhenNoResultWasSubmitted() {
        mockTokenCounterToString();
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldCancelEndOfChallenge() {
        mockTokenCounterToString();
        assertEndChallengeIsSuccessful();
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldRemovePreviouslySubmittedResults() {
        mockTokenCounterToString();
        mockSufficientBingoResult();
        setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING, RetryRule.UNFAIR_DISADVANTAGE));
        assertSubmitBingoResultIsSuccessful();
        assertSubmitSharedDivisionAchievementsIsSuccessful();
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
        assertAllSubmittedMatchResultsAreRemoved();
        verify(mockedTokenCounter).cancelMatchResult();
    }

    @Test
    void noOtherActionsShouldBeAllowedWhenEndOfChallengeIsAlreadyConfirmed() {
        assertEndChallengeIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsNotSuccessful();
        assertSubmitSharedDivisionAchievementsIsNotSuccessful();
        assertConfirmCurrentResultIsNotSuccessful();
        assertResetCurrentLevelIsNotSuccessful();
        assertEndChallengeIsNotSuccessful();
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

    private void mockInsufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(30L);
    }

    private void mockSufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(3000L);
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

    private void setupBingoGameWithPlayers(List<Player> players) {
        bingoGame = new BingoGame(players, mockedTokenCounter);
    }

    private void setActiveRetryRules(List<RetryRule> activeRetryRules) {
        bingoGame.setActiveRetryRules(activeRetryRules);
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
        assertEquals(MULTIPLAYER_RESULT_ABC, bingoGame.toString());
    }

    private void submitDivisionAchievementsAndCheckResults() {
        bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements);
        assertEquals(MULTIPLAYER_RESULT_WITH_DIVISION_ACHIEVEMENTS, bingoGame.toString());
    }

    private void assertSubmitSharedDivisionAchievementsIsSuccessful() {
        assertTrue(bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements));
    }

    private void assertSubmitSharedDivisionAchievementsIsNotSuccessful() {
        assertFalse(bingoGame.submitSharedDivisionAchievements(mockedDivisionAchievements));
    }

    private void assertSubmitBingoResultIsSuccessful() {
        assertTrue(bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult));
    }

    private void assertSubmitBingoResultIsNotSuccessful() {
        assertFalse(bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, mockedBingoResult));
    }

    private void assertConfirmCurrentResultIsSuccessful() {
        assertTrue(bingoGame.confirmCurrentResult());
    }

    private void assertConfirmCurrentResultIsNotSuccessful() {
        assertFalse(bingoGame.confirmCurrentResult());
    }

    private void assertResetCurrentLevelIsSuccessful() {
        assertTrue(bingoGame.doResetForCurrentLevel());
    }

    private void assertResetCurrentLevelIsNotSuccessful() {
        assertFalse(bingoGame.doResetForCurrentLevel());
    }

    private void assertEndChallengeIsSuccessful() {
        assertTrue(bingoGame.endChallenge());
    }

    private void assertEndChallengeIsNotSuccessful() {
        assertFalse(bingoGame.endChallenge());
    }

    private void assertAllSubmittedMatchResultsAreRemoved() {
        assertTrue(bingoGame.getBingoResultForPlayer(SINGLE_PLAYER).isEmpty());
        assertTrue(bingoGame.getSharedDivisionAchievements().isEmpty());
        assertTrue(bingoGame.getActiveRetryRules().isEmpty());
    }

    private void assertToStringMethodReturnsFirstResultBar() {
        assertEquals(LEVEL_ONE_REQUIREMENT, bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals(LEVEL_TWO_REQUIREMENT, bingoGame.toString());
    }

    private void assertIllegalArgumentExceptionIsThrownWithMessage(String expectedMessage, Executable executable) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
