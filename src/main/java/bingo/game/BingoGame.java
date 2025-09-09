package bingo.game;

import bingo.game.input.UserInputException;
import bingo.game.modifiers.ChallengeModifier;
import bingo.game.results.BingoResult;
import bingo.game.results.BingoResultBar;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.math.terms.Term;
import bingo.math.terms.impl.Addition;
import bingo.math.terms.impl.Equation;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.TermWithPoints;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.Ship;
import bingo.text.TextUtility;
import bingo.tokens.TokenCounter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class BingoGame implements Serializable {
    @Serial
    private static final long serialVersionUID = -6697185137194220209L;
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String SENTENCE_END = ". ";

    private final TokenCounter tokenCounter;
    private final BingoGameStateMachine bingoGameStateMachine;
    private final List<BingoResultBar> resultBars;
    private final List<Ship> shipsUsed;
    private final List<Player> players;
    private final List<RetryRule> activeRetryRules;
    private final List<ChallengeModifier> challengeModifiers;
    private final Map<Player, ShipRestriction> shipRestrictionByPlayer;
    private final Map<Player, BingoResult> bingoResultByPlayer;
    private SharedDivisionAchievements sharedDivisionAchievements;
    private int currentLevel;

    BingoGame(
            List<Player> players, List<ChallengeModifier> challengeModifiers, TokenCounter tokenCounter,
            BingoGameStateMachine bingoGameStateMachine) throws UserInputException {
        if (players.isEmpty() || players.size() > 3) {
            throw new UserInputException("The number of players must be between 1 and 3");
        }
        this.tokenCounter = tokenCounter;
        this.bingoGameStateMachine = bingoGameStateMachine;
        this.resultBars = new LinkedList<>();
        this.shipsUsed = new LinkedList<>();
        this.players = new LinkedList<>(players);
        this.activeRetryRules = new LinkedList<>();
        this.challengeModifiers = filterDisallowedModifiers(challengeModifiers, players);
        this.shipRestrictionByPlayer = new HashMap<>();
        this.bingoResultByPlayer = new HashMap<>();
        for (int level = START_LEVEL - 1; level <= MAX_LEVEL; level++) {
            resultBars.add(new BingoResultBar(level));
        }
        this.currentLevel = START_LEVEL;
    }

    public BingoGame(List<Player> players, List<ChallengeModifier> challengeModifiers) throws UserInputException {
        this(players, challengeModifiers, new TokenCounter(), new BingoGameStateMachine());
    }

    private List<ChallengeModifier> filterDisallowedModifiers(
            List<ChallengeModifier> challengeModifiers, List<Player> players) {
        final List<ChallengeModifier> filteredChallengeModifiers;
        if (players.size() == 1) {
            filteredChallengeModifiers =
                    filterDisallowedModifier(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE, challengeModifiers);
        } else {
            filteredChallengeModifiers = filterDisallowedModifier(ChallengeModifier.NO_HELP, challengeModifiers);
        }
        return filteredChallengeModifiers;
    }

    private List<ChallengeModifier> filterDisallowedModifier(
            ChallengeModifier disallowedModifier, List<ChallengeModifier> challengeModifiers) {
        return challengeModifiers.stream()
                .filter(challengeModifier -> !challengeModifier.equals(disallowedModifier))
                .toList();
    }

    public boolean actionIsAllowed(BingoGameAction action) {
        return bingoGameStateMachine.actionIsAllowed(action);
    }

    private void ensureActionIsAllowed(BingoGameAction action) throws UserInputException {
        bingoGameStateMachine.ensureActionIsAllowed(action);
    }

    public void doResetForCurrentLevel() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.PERFORM_RESET);
        removeSubmittedMatchResults();
        tokenCounter.cancelMatchResult();
        bingoGameStateMachine.processPerformResetAction();
    }

    private void removeSubmittedMatchResults() {
        removeAllBingoResults();
        removeActiveRetryRules();
        removeSharedDivisionAchievements();
    }

    private boolean anyResultIsSubmitted() {
        return sharedDivisionAchievementsAreSubmitted() || !bingoResultByPlayer.isEmpty();
    }

    private boolean sharedDivisionAchievementsAreSubmitted() {
        return getSharedDivisionAchievements().isPresent();
    }

    public void submitSharedDivisionAchievements(SharedDivisionAchievements sharedDivisionAchievements)
            throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
        this.sharedDivisionAchievements = sharedDivisionAchievements;
        updateTokenCounterWithCurrentResults();
        submitResultActionToBingoGameStateMachine();
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

    private void submitResultActionToBingoGameStateMachine() throws UserInputException {
        bingoGameStateMachine.processSubmitResultAction(
                bingoResultIsSubmittedForAllPlayers(),
                requirementOfCurrentResultBarIsMet());
    }

    private boolean bingoResultIsSubmittedForAllPlayers() {
        return bingoResultByPlayer.size() == players.size();
    }

    public void submitBingoResultForPlayer(Player player, BingoResult bingoResult) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.SUBMIT_RESULT);
        ensurePlayerIsParticipatingInTheGame(player);
        bingoResultByPlayer.put(player, bingoResult);
        updateTokenCounterWithCurrentResults();
        submitResultActionToBingoGameStateMachine();
    }

    public Optional<BingoResult> getBingoResultForPlayer(Player player) throws UserInputException {
        ensurePlayerIsParticipatingInTheGame(player);
        return internalGetBingoResultForPlayer(player);
    }

    private Optional<BingoResult> internalGetBingoResultForPlayer(Player player) {
        return Optional.ofNullable(bingoResultByPlayer.get(player));
    }

    private void removeAllBingoResults() {
        bingoResultByPlayer.clear();
    }

    private void ensurePlayerIsParticipatingInTheGame(Player player) throws UserInputException {
        if (!players.contains(player)) {
            throw new UserInputException("%s is not participating in the game".formatted(player.name()));
        }
    }

    public void confirmCurrentResult() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
        BingoGameState previousState = bingoGameStateMachine.getCurrentState();
        bingoGameStateMachine.processConfirmResultAction(hasNextLevel(), retryingIsAllowed());
        BingoGameState newState = bingoGameStateMachine.getCurrentState();
        if (newState.equals(BingoGameState.LEVEL_INITIALIZED)) {
            if (previousState.equals(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH)) {
                removeAllShipRestrictions();
                currentLevel++;
            }
            removeSubmittedMatchResults();
        }
        tokenCounter.confirmMatchResult();
    }

    public void endChallenge() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
        bingoGameStateMachine.processEndChallengeVoluntarilyAction();
    }

    private boolean retryingIsAllowed() {
        return !activeRetryRules.isEmpty() || tokenCounter.hasExtraLife();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return getPointValueOfTotalResult() >= getPointRequirementOfLevel(currentLevel);
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
        return new TermWithPoints(new Literal((int) pointValue));
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

    public int getCurrentLevel() {
        return currentLevel;
    }

    private boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    public void setActiveRetryRules(List<RetryRule> activeRetryRules) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.OTHER_ACTION);
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
        return internalGetShipRestrictionForPlayer(player).isPresent();
    }

    public void setShipRestrictionForPlayer(Player player, ShipRestriction shipRestriction) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
        ensurePlayerIsParticipatingInTheGame(player);
        if (shipRestrictionIsSetForPlayer(player)) {
            throw new UserInputException("A ship restriction is already set for %s".formatted(player.name()));
        }
        shipRestrictionByPlayer.put(player, shipRestriction);
    }

    public Optional<ShipRestriction> getShipRestrictionForPlayer(Player player) throws UserInputException {
        ensurePlayerIsParticipatingInTheGame(player);
        return internalGetShipRestrictionForPlayer(player);
    }

    private Optional<ShipRestriction> internalGetShipRestrictionForPlayer(Player player) {
        return Optional.ofNullable(shipRestrictionByPlayer.get(player));
    }

    public void removeShipRestrictionForPlayer(Player player) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
        ensurePlayerIsParticipatingInTheGame(player);
        shipRestrictionByPlayer.remove(player);
    }

    private void removeAllShipRestrictions() {
        shipRestrictionByPlayer.clear();
    }

    public void addShipUsed(Ship shipUsed) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.OTHER_ACTION);
        String nameOfShipUsed = shipUsed.name();
        for (Ship previouslyUsedShip : shipsUsed) {
            if (nameOfShipUsed.equalsIgnoreCase(previouslyUsedShip.name())) {
                throw new UserInputException("%s was already used".formatted(nameOfShipUsed));
            }
        }
        shipsUsed.add(shipUsed);
    }

    public List<Ship> getShipsUsed() {
        return new LinkedList<>(shipsUsed);
    }

    public void removeShipUsed(Ship shipUsed) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.OTHER_ACTION);
        boolean shipWasNotFound = !shipsUsed.remove(shipUsed);
        if (shipWasNotFound) {
            String message = "%s is not in the list of ships used, so it cannot be removed".formatted(shipUsed.name());
            throw new UserInputException(message);
        }
    }

    private boolean moreThanOnePlayerIsRegistered() {
        return players.size() > 1;
    }

    public List<Player> getPlayers() {
        return new LinkedList<>(players);
    }

    public List<ChallengeModifier> getChallengeModifiers() {
        return new LinkedList<>(challengeModifiers);
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
        BingoGameState bingoGameState = bingoGameStateMachine.getCurrentState();
        if (bingoGameIsInVoluntaryEndState(bingoGameState)) {
            appendTextForVoluntaryEndOfChallenge(stringBuilder);
        } else {
            appendTextForBingoResults(stringBuilder);
            appendTextForSharedDivisionAchievements(stringBuilder);
            appendTextForTotalResult(stringBuilder);
            stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel));
            if (bingoGameIsInSuccessfulMatchState(bingoGameState)) {
                appendTextForSuccessfulMatch(stringBuilder);
            } else if (bingoGameIsInUnsuccessfulMatchState(bingoGameState)) {
                appendTextForUnsuccessfulMatch(stringBuilder);
            } else {
                appendTextForShipRestrictions(stringBuilder);
                appendTextForTokenCounter(stringBuilder);
            }
        }
        appendTextIfBingoGameIsInFinalState(stringBuilder);
        return stringBuilder.toString();
    }

    private boolean bingoGameIsInVoluntaryEndState(BingoGameState bingoGameState) {
        return bingoGameState.equals(BingoGameState.UNCONFIRMED_VOLUNTARY_END) ||
                bingoGameState.equals(BingoGameState.CHALLENGE_ENDED_VOLUNTARILY);
    }

    private boolean bingoGameIsInSuccessfulMatchState(BingoGameState bingoGameState) {
        return bingoGameState.equals(BingoGameState.UNCONFIRMED_SUCCESSFUL_MATCH) ||
                bingoGameState.equals(BingoGameState.CHALLENGE_ENDED_SUCCESSFULLY);
    }

    private boolean bingoGameIsInUnsuccessfulMatchState(BingoGameState bingoGameState) {
        return bingoGameState.equals(BingoGameState.UNCONFIRMED_UNSUCCESSFUL_MATCH) ||
                bingoGameState.equals(BingoGameState.CHALLENGE_ENDED_UNSUCCESSFULLY);
    }

    private void appendTextForBingoResults(StringBuilder stringBuilder) {
        for (Player player : players) {
            Optional<BingoResult> bingoResult = internalGetBingoResultForPlayer(player);
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
        if (moreThanOnePlayerIsRegistered() && anyResultIsSubmitted()) {
            stringBuilder.append(getTotalResultAsString()).append(SENTENCE_END);
        }
    }

    private void appendTextForShipRestrictions(StringBuilder stringBuilder) {
        for (Player player : players) {
            Optional<ShipRestriction> shipRestriction = internalGetShipRestrictionForPlayer(player);
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
            String unlockedRewardAsString = getSubsAsString(unlockedReward);
            String conversionFactorAsString = getSubsAsString(conversionFactorForExtraLives);
            String totalRewardAsString = getSubsAsString(totalReward);
            String calculationAsText = " Total reward: %s + (unused extra lives: %s) * %s = %s \uD83C\uDF81".formatted(
                    unlockedRewardAsString,
                    extraLives,
                    conversionFactorAsString,
                    totalRewardAsString);
            stringBuilder.append(calculationAsText);
        }
    }

    private String getSubsAsString(int numberOfSubs) {
        return numberOfSubs + TextUtility.getSuffixForSubs(numberOfSubs);
    }

    private void appendTextForTokenCounter(StringBuilder stringBuilder) {
        stringBuilder.append(SENTENCE_END).append(tokenCounter);
    }

    private void appendTextIfBingoGameIsInFinalState(StringBuilder stringBuilder) {
        if (bingoGameStateMachine.getCurrentState().isFinal()) {
            stringBuilder.append("\n\nEnd of challenge confirmed. Changes are no longer allowed.");
        }
    }
}
