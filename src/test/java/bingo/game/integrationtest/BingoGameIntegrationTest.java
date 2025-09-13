package bingo.game.integrationtest;

import bingo.achievements.Achievement;
import bingo.achievements.division.DivisionAchievement;
import bingo.game.BingoGame;
import bingo.game.input.UserInputException;
import bingo.game.results.BingoResult;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.players.Player;
import bingo.ribbons.Ribbon;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BingoGameIntegrationTest {

    @Nested
    class SingleplayerTests {
        private static final int START_LEVEL = 1;
        private static final int MAX_LEVEL = 7;
        private static final String END_OF_CHALLENGE_CONFIRMED =
                "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
        private static final String LEVEL_THREE_UNSUCCESSFUL_END =
                "Ribbon Bingo result: Main gun hit: 50 * 3 points + Set on fire: 5 * 20 points = 250 points. Requirement of level 3: 700 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub 🎁";
        private static final String LEVEL_FIVE_VOLUNTARY_END =
                "Challenge ended voluntarily on level 5. Your reward from the previous level: 16 subs 🎁";
        private static final String LEVEL_SEVEN_SUCCESSFUL_END =
                "Ribbon Bingo result: Main gun hit: 500 * 2 points + Destroyed: 6 * 120 points + Set on fire: 25 * 20 points + Witherer: 30 points + (Set on fire: 25 * 20 points) * 0.3 + Kraken Unleashed: 30 points + (Destroyed: 6 * 120 points) * 0.2 + High Caliber: 150 points = 2724 points. Requirement of level 7: 1800 points ✅ Unlocked reward: 128 subs 🎁 This is the highest reward you can get. Congratulations! 🎊 Total reward: 128 subs + (unused extra lives: 1) * 6 subs = 134 subs 🎁";
        private static final String LEVEL_FOUR_WITH_ZERO_TOKENS =
                "Requirement of level 4: 900 points. Token counter: Now 0 tokens 🪙 total.";
        private static final String LEVEL_ONE_SUCCESSFUL_MATCH =
                "Ribbon Bingo result: Main gun hit: 400 points + Set on fire: 15 * 20 points + Destroyed: 2 * 120 points + Arsonist: 2 * (30 points + (Set on fire: 15 * 20 points) * 0.1) = 1060 points. Requirement of level 1: 300 points ✅ Unlocked reward: 2 subs 🎁 Token counter: +1 token (successful match). Now 1 token 🪙 total. ➡️ Requirement of level 2: 500 points";
        private static final Player SINGLE_PLAYER = new Player("Single Player");

        private BingoGame bingoGame;

        @BeforeEach
        void setup() throws UserInputException {
            bingoGame = new BingoGame(List.of(SINGLE_PLAYER), Collections.emptyList());
        }

        @Test
        void shouldProceedToLevelThreeThenEndTheChallengeUnsuccessfully() throws UserInputException {
            submitBingoResult(getBingoResultWithMoreThanOneThousandPoints());
            bingoGame.confirmCurrentResult();
            submitBingoResult(getBingoResultWithMoreThanOneThousandPoints());
            bingoGame.confirmCurrentResult();
            submitBingoResult(getBingoResultWithLessThanThreeHundredPoints());
            bingoGame.confirmCurrentResult();
            assertEquals(LEVEL_THREE_UNSUCCESSFUL_END + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldProceedToLevelFiveThenEndTheChallengeVoluntarily() throws UserInputException {
            for (int level = START_LEVEL; level < 5; level++) {
                submitBingoResult(getBingoResultWithMoreThanOneThousandPoints());
                bingoGame.confirmCurrentResult();
            }
            bingoGame.endChallenge();
            bingoGame.confirmCurrentResult();
            assertEquals(LEVEL_FIVE_VOLUNTARY_END + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldProceedToLevelSevenThenEndTheChallengeSuccessfully() throws UserInputException {
            for (int level = START_LEVEL; level <= MAX_LEVEL; level++) {
                submitBingoResult(getBingoResultWithMoreThanTwoThousandPoints());
                bingoGame.confirmCurrentResult();
            }
            assertEquals(LEVEL_SEVEN_SUCCESSFUL_END + END_OF_CHALLENGE_CONFIRMED, bingoGame.toString());
        }

        @Test
        void shouldAddAnExtraLifeThenConsumeItToContinueAfterAnUnsuccessfulMatch() throws UserInputException {
            for (int level = START_LEVEL; level < 4; level++) {
                submitBingoResult(getBingoResultWithMoreThanOneThousandPoints());
                bingoGame.setActiveRetryRules(List.of(RetryRule.IMBALANCED_MATCHMAKING));
                bingoGame.confirmCurrentResult();
            }
            submitBingoResult(getBingoResultWithLessThanThreeHundredPoints());
            bingoGame.confirmCurrentResult();
            assertEquals(LEVEL_FOUR_WITH_ZERO_TOKENS, bingoGame.toString());
        }

        @Test
        void shouldAllowRetryButNotAwardAnyTokens() throws UserInputException {
            String initialBingoGameText = bingoGame.toString();
            submitBingoResult(getBingoResultWithLessThanThreeHundredPoints());
            bingoGame.setActiveRetryRules(List.of(RetryRule.UNFAIR_DISADVANTAGE));
            bingoGame.confirmCurrentResult();
            assertEquals(initialBingoGameText, bingoGame.toString());
        }

        @Test
        void shouldNotAwardPointsForDivisionAchievementsEvenIfAdded() throws UserInputException {
            submitBingoResult(getBingoResultWithMoreThanOneThousandPoints());
            bingoGame.submitSharedDivisionAchievements(getDivisionAchievements());
            assertEquals(LEVEL_ONE_SUCCESSFUL_MATCH, bingoGame.toString());
        }

        private void submitBingoResult(BingoResult bingoResult) throws UserInputException {
            bingoGame.submitBingoResultForPlayer(SINGLE_PLAYER, bingoResult);
        }
    }

    @Nested
    class MultiplayerTests {
        private static final Player PLAYER_A = new Player("Player A");
        private static final Player PLAYER_B = new Player("Player B");
        private static final Player PLAYER_C = new Player("Player C");
        private static final String LEVEL_ONE_SUCCESSFUL_MATCH =
                "Player A's Ribbon Bingo result: Main gun hit: 50 * 3 points + Set on fire: 5 * 20 points = 250 points. Shared division achievements: General Offensive: 2 * 100 points + Brothers-in-Arms: 150 points = 350 points. Total result: 250 points + 350 points = 600 points. Requirement of level 1: 540 points. Token counter: +1 token (successful match). Now 1 token 🪙 total.";
        private static final String LEVEL_ONE_WITH_BINGO_RESULT =
                "Player A's Ribbon Bingo result: Main gun hit: 50 * 3 points + Set on fire: 5 * 20 points = 250 points. Total result: 250 points. Requirement of level 1: 540 points. Token counter: Now 0 tokens 🪙 total.";
        private static final String LEVEL_ONE_WITH_DIVISION_ACHIEVEMENTS =
                "Shared division achievements: General Offensive: 2 * 100 points + Brothers-in-Arms: 150 points = 350 points. Total result: 350 points. Requirement of level 1: 540 points. Token counter: Now 0 tokens 🪙 total.";
        private static final String LEVEL_TWO_WITH_ONE_TOKEN =
                "Requirement of level 2: 900 points. Token counter: Now 1 token 🪙 total.";

        private BingoGame bingoGame;

        @BeforeEach
        void setup() throws UserInputException {
            bingoGame = new BingoGame(List.of(PLAYER_A, PLAYER_B, PLAYER_C), Collections.emptyList());
        }

        @Test
        void shouldShowCorrectlyUpdatedTokenCounterEvenWhenBingoResultsAreOnlyPartiallySubmitted()
                throws UserInputException {
            bingoGame.submitBingoResultForPlayer(PLAYER_A, getBingoResultWithLessThanThreeHundredPoints());
            bingoGame.submitSharedDivisionAchievements(getDivisionAchievements());
            assertEquals(LEVEL_ONE_SUCCESSFUL_MATCH, bingoGame.toString());
        }

        @Test
        void shouldNotShowMatchOutcomeWhenBingoResultsAreOnlyPartiallySubmitted() throws UserInputException {
            bingoGame.submitBingoResultForPlayer(PLAYER_A, getBingoResultWithLessThanThreeHundredPoints());
            assertEquals(LEVEL_ONE_WITH_BINGO_RESULT, bingoGame.toString());
        }

        @Test
        void shouldNotShowMatchOutcomeWhenOnlyDivisionAchievementsAreSubmitted() throws UserInputException {
            bingoGame.submitSharedDivisionAchievements(getDivisionAchievements());
            assertEquals(LEVEL_ONE_WITH_DIVISION_ACHIEVEMENTS, bingoGame.toString());
        }

        @Test
        void shouldNotAllowProceedingToLevelTwoBeforeResultsAreSubmittedForAllPlayers() throws UserInputException {
            bingoGame.submitSharedDivisionAchievements(getDivisionAchievements());
            assertUserInputExceptionIsThrownForPartialResult(() -> bingoGame.confirmCurrentResult());
            bingoGame.submitBingoResultForPlayer(PLAYER_A, getBingoResultWithMoreThanOneThousandPoints());
            assertUserInputExceptionIsThrownForPartialResult(() -> bingoGame.confirmCurrentResult());
            bingoGame.submitBingoResultForPlayer(PLAYER_B, getBingoResultWithMoreThanOneThousandPoints());
            assertUserInputExceptionIsThrownForPartialResult(() -> bingoGame.confirmCurrentResult());
            bingoGame.submitBingoResultForPlayer(PLAYER_C, getBingoResultWithMoreThanOneThousandPoints());
            bingoGame.confirmCurrentResult();
            assertEquals(LEVEL_TWO_WITH_ONE_TOKEN, bingoGame.toString());
        }

        private void assertUserInputExceptionIsThrownForPartialResult(Executable executable) {
            UserInputException exception = assertThrows(UserInputException.class, executable);
            assertEquals(
                    "Action CONFIRM_RESULT is not allowed in the PARTIAL_RESULT_SUBMITTED state",
                    exception.getMessage());
        }
    }

    private BingoResult getBingoResultWithLessThanThreeHundredPoints() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.LARGE_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 50);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 5);
        return bingoResult;
    }

    private BingoResult getBingoResultWithMoreThanOneThousandPoints() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 400);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 15);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 2);
        bingoResult.addAchievementResult(Achievement.ARSONIST, 2);
        return bingoResult;
    }

    private BingoResult getBingoResultWithMoreThanTwoThousandPoints() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 500);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 25);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 6);
        bingoResult.addAchievementResult(Achievement.WITHERER, 1);
        bingoResult.addAchievementResult(Achievement.HIGH_CALIBER, 1);
        bingoResult.addAchievementResult(Achievement.KRAKEN_UNLEASHED, 1);
        return bingoResult;
    }

    private SharedDivisionAchievements getDivisionAchievements() {
        SharedDivisionAchievements divisionAchievements = new SharedDivisionAchievements(3);
        divisionAchievements.addAchievementResult(DivisionAchievement.GENERAL_OFFENSIVE, 2);
        divisionAchievements.addAchievementResult(DivisionAchievement.BROTHERS_IN_ARMS, 1);
        return divisionAchievements;
    }
}
