package bingo.game;

import bingo.game.results.BingoResult;
import bingo.game.results.BingoResultBar;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.math.terms.Term;
import bingo.math.terms.impl.*;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class BingoGame implements Serializable {
    @Serial
    private static final long serialVersionUID = -6697185137194220209L;
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String SENTENCE_END = ". ";

    private final TokenCounter tokenCounter;
    private final List<BingoResultBar> resultBars;
    private final List<Ship> shipsUsed;
    private final List<Player> players;
    private final List<RetryRule> activeRetryRules;
    private final Map<Player, ShipRestriction> shipRestrictionByPlayer;
    private final Map<Player, BingoResult> bingoResultByPlayer;
    private boolean challengeEndedVoluntarily;
    private SharedDivisionAchievements sharedDivisionAchievements;
    private BingoGameState bingoGameState;
    private int currentLevel;

    BingoGame(List<Player> players, TokenCounter tokenCounter) {
        if (players.isEmpty() || players.size() > 3) {
            throw new IllegalArgumentException("The number of players must be between 1 and 3");
        }
        this.tokenCounter = tokenCounter;
        this.resultBars = new LinkedList<>();
        this.shipsUsed = new LinkedList<>();
        this.players = new LinkedList<>(players);
        this.activeRetryRules = new LinkedList<>();
        this.shipRestrictionByPlayer = new HashMap<>();
        this.bingoResultByPlayer = new HashMap<>();
        this.challengeEndedVoluntarily = false;
        for (int level = START_LEVEL - 1; level <= MAX_LEVEL; level++) {
            resultBars.add(new BingoResultBar(level));
        }
        this.bingoGameState = BingoGameState.LEVEL_INITIALIZED;
        this.currentLevel = START_LEVEL;
    }

    public BingoGame(List<Player> players) {
        this(players, new TokenCounter());
    }

    private boolean processBingoGameAction(BingoGameAction selectedAction) {
        return switch (bingoGameState) {
            case LEVEL_INITIALIZED -> processActionForLevelInitializedState(selectedAction);
            case UNCONFIRMED_RESULT -> processActionForUnconfirmedResultState(selectedAction);
            case UNCONFIRMED_VOLUNTARY_END -> {
                processActionForUnconfirmedVoluntaryEndState(selectedAction);
                yield true;
            }
            case CHALLENGE_ENDED -> false;
        };
    }

    private boolean processActionForLevelInitializedState(BingoGameAction selectedAction) {
        return switch (selectedAction) {
            case SUBMIT_RESULT -> {
                bingoGameState = BingoGameState.UNCONFIRMED_RESULT;
                yield true;
            }
            case CONFIRM_RESULT -> false;
            case RESET_WITHOUT_CONFIRMING -> true;
            case END_CHALLENGE_VOLUNTARILY -> {
                bingoGameState = BingoGameState.UNCONFIRMED_VOLUNTARY_END;
                yield true;
            }
        };
    }

    private boolean processActionForUnconfirmedResultState(BingoGameAction selectedAction) {
        return switch (selectedAction) {
            case SUBMIT_RESULT -> true;
            case CONFIRM_RESULT -> {
                if (requirementOfCurrentResultBarIsMet()) {
                    if (hasNextLevel()) {
                        bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                        removeAllShipRestrictions();
                        removeSubmittedMatchResults();
                        currentLevel++;
                    } else {
                        bingoGameState = BingoGameState.CHALLENGE_ENDED;
                    }
                } else {
                    if (retryingIsAllowed()) {
                        bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                        removeSubmittedMatchResults();
                    } else {
                        bingoGameState = BingoGameState.CHALLENGE_ENDED;
                    }
                }
                tokenCounter.confirmMatchResult();
                yield true;
            }
            case RESET_WITHOUT_CONFIRMING -> {
                bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                yield true;
            }
            case END_CHALLENGE_VOLUNTARILY -> false;
        };
    }

    private void processActionForUnconfirmedVoluntaryEndState(BingoGameAction selectedAction) {
        switch (selectedAction) {
            case SUBMIT_RESULT -> bingoGameState = BingoGameState.UNCONFIRMED_RESULT;
            case CONFIRM_RESULT -> {
                bingoGameState = BingoGameState.CHALLENGE_ENDED;
                challengeEndedVoluntarily = true;
            }
            case RESET_WITHOUT_CONFIRMING -> bingoGameState = BingoGameState.LEVEL_INITIALIZED;
            case END_CHALLENGE_VOLUNTARILY -> {
            }
        }
    }

    public boolean doResetForCurrentLevel() {
        boolean stateChangeSuccessful = processBingoGameAction(BingoGameAction.RESET_WITHOUT_CONFIRMING);
        if (stateChangeSuccessful) {
            removeSubmittedMatchResults();
            tokenCounter.cancelMatchResult();
        }
        return stateChangeSuccessful;
    }

    private void removeSubmittedMatchResults() {
        removeAllBingoResults();
        removeActiveRetryRules();
        removeSharedDivisionAchievements();
    }

    private boolean sharedDivisionAchievementsAreSubmitted() {
        return getSharedDivisionAchievements().isPresent();
    }

    public boolean submitSharedDivisionAchievements(SharedDivisionAchievements sharedDivisionAchievements) {
        boolean stateChangeSuccessful = processBingoGameAction(BingoGameAction.SUBMIT_RESULT);
        if (stateChangeSuccessful) {
            this.sharedDivisionAchievements = sharedDivisionAchievements;
            updateTokenCounterWithCurrentResults();
        }
        return stateChangeSuccessful;
    }

    public Optional<SharedDivisionAchievements> getSharedDivisionAchievements() {
        return Optional.ofNullable(sharedDivisionAchievements);
    }

    private void removeSharedDivisionAchievements() {
        sharedDivisionAchievements = null;
    }

    private void updateTokenCounterWithCurrentResults() {
        tokenCounter.calculateMatchResult(requirementOfCurrentResultBarIsMet(), hasNextLevel(), activeRetryRules);
    }

    private boolean bingoResultIsSubmittedForAllPlayers() {
        return bingoResultByPlayer.size() == players.size();
    }

    public boolean submitBingoResultForPlayer(Player player, BingoResult bingoResult) {
        ensurePlayerIsPartOfTheGame(player);
        boolean stateChangeSuccessful = processBingoGameAction(BingoGameAction.SUBMIT_RESULT);
        if (stateChangeSuccessful) {
            bingoResultByPlayer.put(player, bingoResult);
            updateTokenCounterWithCurrentResults();
        }
        return stateChangeSuccessful;
    }

    public Optional<BingoResult> getBingoResultForPlayer(Player player) {
        ensurePlayerIsPartOfTheGame(player);
        return Optional.ofNullable(bingoResultByPlayer.get(player));
    }

    private void removeAllBingoResults() {
        bingoResultByPlayer.clear();
    }

    private void ensurePlayerIsPartOfTheGame(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player %s is not part of the game".formatted(player.name()));
        }
    }

    public boolean confirmCurrentResult() {
        return processBingoGameAction(BingoGameAction.CONFIRM_RESULT);
    }

    public boolean endChallenge() {
        return processBingoGameAction(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
    }

    private boolean retryingIsAllowed() {
        return !activeRetryRules.isEmpty() || tokenCounter.hasExtraLife();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return bingoResultIsSubmittedForAllPlayers() &&
                getPointValueOfTotalResult() >= getPointRequirementOfLevel(currentLevel);
    }

    private long getPointValueOfTotalResult() {
        return Math.round(getTotalResultAsTerm().getValue());
    }

    private String getTotalResultAsString() {
        return getTotalResultAsTerm().getAsString();
    }

    private Term getTotalResultAsTerm() {
        Term calculationTerm = bingoResultByPlayer.values()
                .stream()
                .map(bingoResult -> getAsTerm(bingoResult.getPointValue()))
                .reduce(Addition::new)
                .orElse(getAsTerm(0));
        if (sharedDivisionAchievementsAreSubmitted()) {
            calculationTerm = new Addition(calculationTerm, getAsTerm(sharedDivisionAchievements.getPointValue()));
        }
        Term calculationAsEquation = new TermWithPoints(new Equation(calculationTerm));
        return new LabeledTerm("Total result", calculationAsEquation);
    }

    private Term getAsTerm(long pointValue) {
        return new Literal((int) pointValue);
    }

    private long getPointRequirementOfLevel(int level) {
        int baseRequirement = resultBars.get(level).getPointRequirement();
        return Math.round(baseRequirement * getRequirementModifierForPlayers());
    }

    private double getRequirementModifierForPlayers() {
        return 1 + (players.size() - 1) * 0.4;
    }

    private String getPointRequirementOfLevelAsString(int level) {
        return "Requirement of level %s: %s points".formatted(level, getPointRequirementOfLevel(level));
    }

    private boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    public void setActiveRetryRules(List<RetryRule> activeRetryRules) {
        removeActiveRetryRules();
        this.activeRetryRules.addAll(activeRetryRules);
        updateTokenCounterWithCurrentResults();
    }

    public List<RetryRule> getActiveRetryRules() {
        return new LinkedList<>(activeRetryRules);
    }

    private void removeActiveRetryRules() {
        activeRetryRules.clear();
    }

    private boolean shipRestrictionIsSetForPlayer(Player player) {
        return getShipRestrictionForPlayer(player).isPresent();
    }

    public boolean setShipRestrictionForPlayer(Player player, ShipRestriction shipRestriction) {
        ensurePlayerIsPartOfTheGame(player);
        if (shipRestrictionIsSetForPlayer(player)) {
            return false;
        }
        shipRestrictionByPlayer.put(player, shipRestriction);
        return true;
    }

    public Optional<ShipRestriction> getShipRestrictionForPlayer(Player player) {
        ensurePlayerIsPartOfTheGame(player);
        return Optional.ofNullable(shipRestrictionByPlayer.get(player));
    }

    public void removeShipRestrictionForPlayer(Player player) {
        ensurePlayerIsPartOfTheGame(player);
        shipRestrictionByPlayer.remove(player);
    }

    private void removeAllShipRestrictions() {
        shipRestrictionByPlayer.clear();
    }

    public boolean addShipUsed(Ship shipUsed) {
        String nameOfShipUsed = shipUsed.name();
        for (Ship previouslyUsedShip : shipsUsed) {
            if (nameOfShipUsed.equalsIgnoreCase(previouslyUsedShip.name())) {
                return false;
            }
        }
        shipsUsed.add(shipUsed);
        return true;
    }

    public List<Ship> getShipsUsed() {
        return new LinkedList<>(shipsUsed);
    }

    public boolean removeShipUsed(Ship shipUsed) {
        return shipsUsed.remove(shipUsed);
    }

    private boolean moreThanOnePlayerIsRegistered() {
        return players.size() > 1;
    }

    public List<Player> getPlayers() {
        return new LinkedList<>(players);
    }

    public String getAllResultBarsAndRewardsInTableFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| Level | Points required | Number of subs as reward: 2^(Level) |\n");
        stringBuilder.append("|---|---:|---:|\n");
        for (BingoResultBar resultBar : resultBars) {
            stringBuilder.append("| %s | %s | 2^%s = %s |\n".formatted(
                    resultBar.level(),
                    resultBar.getPointRequirement(),
                    resultBar.level(),
                    resultBar.getNumberOfSubsAsString()));
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (bingoGameState.equals(BingoGameState.UNCONFIRMED_VOLUNTARY_END) || challengeEndedVoluntarily) {
            appendTextForVoluntaryEndOfChallenge(stringBuilder);
        } else {
            appendTextForBingoResults(stringBuilder);
            appendTextForSharedDivisionAchievements(stringBuilder);
            appendTextForTotalResult(stringBuilder);
            stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel));
            if (requirementOfCurrentResultBarIsMet()) {
                appendTextForSuccessfulMatch(stringBuilder);
            } else if (bingoResultIsSubmittedForAllPlayers()) {
                appendTextForUnsuccessfulMatch(stringBuilder);
            } else {
                appendTextForShipRestrictions(stringBuilder);
                appendTextForTokenCounter(stringBuilder);
            }
        }
        appendTextIfBingoGameIsInChallengeEndedState(stringBuilder);
        return stringBuilder.toString();
    }

    private void appendTextForBingoResults(StringBuilder stringBuilder) {
        for (Player player : players) {
            Optional<BingoResult> bingoResult = getBingoResultForPlayer(player);
            bingoResult.ifPresent(appendTextForBingoResult(stringBuilder, player));
        }
    }

    private Consumer<BingoResult> appendTextForBingoResult(StringBuilder stringBuilder, Player player) {
        return bingoResult -> {
            if (moreThanOnePlayerIsRegistered()) {
                stringBuilder.append(player.name()).append("'s ");
            }
            stringBuilder.append(bingoResult).append(SENTENCE_END);
        };
    }

    private void appendTextForSharedDivisionAchievements(StringBuilder stringBuilder) {
        if (moreThanOnePlayerIsRegistered() && sharedDivisionAchievementsAreSubmitted()) {
            stringBuilder.append(sharedDivisionAchievements).append(SENTENCE_END);
        }
    }

    private void appendTextForTotalResult(StringBuilder stringBuilder) {
        if (moreThanOnePlayerIsRegistered()) {
            stringBuilder.append(getTotalResultAsString()).append(SENTENCE_END);
        }
    }

    private void appendTextForShipRestrictions(StringBuilder stringBuilder) {
        for (Player player : players) {
            Optional<ShipRestriction> shipRestriction = getShipRestrictionForPlayer(player);
            shipRestriction.ifPresent(appendTextForShipRestriction(stringBuilder, player));
        }
    }

    private Consumer<ShipRestriction> appendTextForShipRestriction(StringBuilder stringBuilder, Player player) {
        return shipRestriction -> {
            stringBuilder.append(SENTENCE_END);
            if (moreThanOnePlayerIsRegistered()) {
                stringBuilder.append(player.name()).append("'s ship restriction: ");
            }
            stringBuilder.append(shipRestriction.getDisplayText());
        };
    }

    private void appendTextForVoluntaryEndOfChallenge(StringBuilder stringBuilder) {
        BingoResultBar previousResultBar = resultBars.get(currentLevel - 1);
        stringBuilder.append("Challenge ended voluntarily on level ")
                .append(currentLevel)
                .append(". Your reward from the previous level: ")
                .append(previousResultBar.getNumberOfSubsAsString());
        appendTextForConversionOfExtraLives(previousResultBar, stringBuilder);
    }

    private void appendTextForSuccessfulMatch(StringBuilder stringBuilder) {
        BingoResultBar currentResultBar = resultBars.get(currentLevel);
        stringBuilder.append(" ✅ Unlocked reward: ").append(currentResultBar.getNumberOfSubsAsString()).append(" ");
        if (hasNextLevel()) {
            stringBuilder.append(tokenCounter).append(" ➡️ ");
            stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel + 1));
        } else {
            stringBuilder.append("This is the highest reward you can get. Congratulations! \uD83C\uDF8A");
            appendTextForConversionOfExtraLives(currentResultBar, stringBuilder);
        }
    }

    private void appendTextForUnsuccessfulMatch(StringBuilder stringBuilder) {
        stringBuilder.append(" ❌ Active retry rules: ");
        if (retryingIsAllowed()) {
            if (activeRetryRules.contains(RetryRule.IMBALANCED_MATCHMAKING)) {
                stringBuilder.append(RetryRule.IMBALANCED_MATCHMAKING.getDisplayText());
            } else if (activeRetryRules.contains(RetryRule.UNFAIR_DISADVANTAGE)) {
                stringBuilder.append(RetryRule.UNFAIR_DISADVANTAGE.getDisplayText());
            } else if (tokenCounter.hasExtraLife()) {
                stringBuilder.append("Extra life (rule 8d)");
            }
            stringBuilder.append(" ✅ ").append(tokenCounter);
        } else {
            stringBuilder.append(
                    "None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: ");
            stringBuilder.append(resultBars.getFirst().getNumberOfSubsAsString());
        }
    }

    private void appendTextForConversionOfExtraLives(BingoResultBar resultBar, StringBuilder stringBuilder) {
        if (tokenCounter.hasExtraLife()) {
            int unlockedReward = resultBar.getNumberOfSubsAsReward();
            int extraLives = tokenCounter.getCurrentExtraLives();
            int conversionFactorForExtraLives = 6;
            int totalReward = unlockedReward + extraLives * conversionFactorForExtraLives;
            String calculationAsText =
                    " Total reward: %s + (unused extra lives: %s) * %s = %s subs \uD83C\uDF81".formatted(
                            unlockedReward,
                            extraLives,
                            conversionFactorForExtraLives,
                            totalReward);
            stringBuilder.append(calculationAsText);
        }
    }

    private void appendTextForTokenCounter(StringBuilder stringBuilder) {
        stringBuilder.append(SENTENCE_END).append(tokenCounter);
    }

    private void appendTextIfBingoGameIsInChallengeEndedState(StringBuilder stringBuilder) {
        if (bingoGameState.equals(BingoGameState.CHALLENGE_ENDED)) {
            stringBuilder.append("\n\nEnd of challenge confirmed. Changes are no longer allowed.");
        }
    }
}
