package bingo.game;

import bingo.game.input.UserInputException;
import bingo.game.modifiers.ChallengeModifier;
import bingo.game.results.BingoResult;
import bingo.game.results.BingoResultBars;
import bingo.game.results.division.SharedDivisionAchievements;
import bingo.game.utility.BingoGameDependencyInjector;
import bingo.math.terms.Term;
import bingo.math.terms.impl.Addition;
import bingo.math.terms.impl.Equation;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.Multiplication;
import bingo.math.terms.impl.TermWithPoints;
import bingo.math.terms.impl.TermWithSubs;
import bingo.players.Player;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
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
    private static final String WHITESPACE = " ";

    private final List<Ship> shipsUsed;
    private final List<Player> players;
    private final List<RetryRule> activeRetryRules;
    private final List<ChallengeModifier> challengeModifiers;
    private final Map<Player, ShipRestriction> shipRestrictionByPlayer;
    private final Map<Player, BingoResult> bingoResultByPlayer;
    private final BingoGameStateMachine bingoGameStateMachine;
    private final BingoResultBars bingoResultBars;
    private final TokenCounter tokenCounter;
    private SharedDivisionAchievements sharedDivisionAchievements;
    private int currentLevel;

    BingoGame(
            List<Player> players, List<ChallengeModifier> challengeModifiers,
            BingoGameDependencyInjector bingoGameDependencyInjector) throws UserInputException {
        if (players.isEmpty() || players.size() > 3) {
            throw exceptionWithMessage("The number of players must be between 1 and 3");
        }
        this.shipsUsed = new LinkedList<>();
        this.players = new LinkedList<>(players);
        this.activeRetryRules = new LinkedList<>();
        this.challengeModifiers = filterDisallowedModifiers(challengeModifiers, players.size());
        this.shipRestrictionByPlayer = new HashMap<>();
        this.bingoResultByPlayer = new HashMap<>();
        this.bingoGameStateMachine =
                bingoGameDependencyInjector.createBingoGameStateMachine(
                        shipRestrictionsAreEnabled(),
                        endingVoluntarilyIsAllowed());
        this.bingoResultBars =
                bingoGameDependencyInjector.createBingoResultBars(getPointRequirementModifier(), MAX_LEVEL);
        this.tokenCounter = bingoGameDependencyInjector.createTokenCounter(extraLivesAreEnabled());
        this.currentLevel = START_LEVEL;
    }

    public BingoGame(List<Player> players, List<ChallengeModifier> challengeModifiers) throws UserInputException {
        this(players, challengeModifiers, new BingoGameDependencyInjector());
    }

    private List<ChallengeModifier> filterDisallowedModifiers(
            List<ChallengeModifier> challengeModifiers, int numberOfPlayers) {
        return challengeModifiers.stream()
                .filter(challengeModifier -> challengeModifier.allowsNumberOfPlayers(numberOfPlayers))
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
        if (shipRestrictionForPlayerProhibitsMainArmamentType(bingoResult.getMainArmamentType(), player)) {
            throw exceptionWithMessage(
                    "Ships with %s as main armament are currently prohibited due to the ship restriction set for %s".formatted(
                            bingoResult.getMainArmamentType().getDisplayText().toLowerCase(),
                            player.name()));
        }
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
            throw exceptionWithMessage("%s is not participating in the game".formatted(player.name()));
        }
    }

    public void confirmCurrentResult() throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CONFIRM_RESULT);
        bingoGameStateMachine.processConfirmResultAction(hasNextLevel(), retryingIsAllowed());
        BingoGameState newState = bingoGameStateMachine.getCurrentState();
        if (bingoGameIsInInitialState(newState)) {
            if (requirementOfCurrentResultBarIsMet()) {
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
        return getPointValueOfTotalResult() >= bingoResultBars.getPointRequirementOfLevel(currentLevel);
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

    private String getTotalRewardAsString(int unlockedReward) {
        Term baseReward = getBaseRewardAsTerm(unlockedReward);
        Term totalMultiplier = getTotalMultiplierAsTerm();
        Multiplication calculation = new Multiplication(baseReward, totalMultiplier);
        Term totalReward = new LabeledTerm("Total reward", new TermWithSubs(new Equation(calculation)));
        if (baseReward.getValue() == 1) {
            calculation.displayIdentity();
        }
        return totalReward.getAsString();
    }

    private Term getBaseRewardAsTerm(int unlockedReward) {
        Term unlockedRewardAsTerm = new TermWithSubs(new Literal(unlockedReward));
        Term extraLives = new LabeledTerm("unused extra lives", new Literal(tokenCounter.getCurrentExtraLives()));
        Term conversionFactor = new TermWithSubs(new Literal(6));
        Multiplication convertedExtraLives = new Multiplication(extraLives, conversionFactor);
        convertedExtraLives.displayIdentity();
        return new Addition(unlockedRewardAsTerm, convertedExtraLives);
    }

    private Term getTotalMultiplierAsTerm() {
        Term bonusMultiplier = challengeModifiers.stream()
                .map(ChallengeModifier::getAsTerm)
                .reduce(Addition::new)
                .orElse(new Literal(0));
        return new LabeledTerm("challenge modifiers", new Addition(new Literal(1), bonusMultiplier));
    }

    private double getPointRequirementModifier() {
        double rawModifier = 1 + getModifierIncreaseForPlayers() + getModifierIncreaseForChallengeModifiers();
        return roundedDoubleOf(rawModifier);
    }

    private double getModifierIncreaseForPlayers() {
        return (players.size() - 1) * 0.4;
    }

    private double getModifierIncreaseForChallengeModifiers() {
        return challengeModifiers.stream()
                .map(ChallengeModifier::getPointRequirementModifier)
                .reduce(Double::sum)
                .orElse(0.0);
    }

    private double roundedDoubleOf(double rawModifier) {
        return Math.round(rawModifier * 100) / 100.0;
    }

    private String getPointRequirementOfLevelAsString(int level) {
        return "Requirement of level %s: %s points".formatted(level, bingoResultBars.getPointRequirementOfLevel(level));
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

    private boolean shipRestrictionIsSetForAllPlayers() {
        return shipRestrictionByPlayer.size() == players.size();
    }

    private boolean shipRestrictionForPlayerProhibitsMainArmamentType(
            MainArmamentType mainArmamentType, Player player) {
        Optional<ShipRestriction> shipRestriction = internalGetShipRestrictionForPlayer(player);
        return shipRestriction.isPresent() && !shipRestriction.get().allowsMainArmamentType(mainArmamentType);
    }

    public void setShipRestrictionForPlayer(Player player, ShipRestriction shipRestriction) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.CHANGE_SHIP_RESTRICTION);
        ensurePlayerIsParticipatingInTheGame(player);
        if (shipRestrictionIsSetForPlayer(player)) {
            throw exceptionWithMessage("A ship restriction is already set for %s".formatted(player.name()));
        }
        shipRestrictionByPlayer.put(player, shipRestriction);
        bingoGameStateMachine.processChangeShipRestrictionAction(shipRestrictionIsSetForAllPlayers());
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
        bingoGameStateMachine.processChangeShipRestrictionAction(shipRestrictionIsSetForAllPlayers());
    }

    private void removeAllShipRestrictions() {
        shipRestrictionByPlayer.clear();
    }

    public void addShipUsed(Ship shipUsed) throws UserInputException {
        ensureActionIsAllowed(BingoGameAction.OTHER_ACTION);
        String nameOfShipUsed = shipUsed.name();
        for (Ship previouslyUsedShip : shipsUsed) {
            if (nameOfShipUsed.equalsIgnoreCase(previouslyUsedShip.name())) {
                throw exceptionWithMessage("%s was already used".formatted(nameOfShipUsed));
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
            throw exceptionWithMessage("%s is not in the list of ships used, so it cannot be removed".formatted(shipUsed.name()));
        }
    }

    private UserInputException exceptionWithMessage(String message) {
        return new UserInputException(message);
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
                appendTextForTokenCounterWithPrefix(SENTENCE_END, stringBuilder);
            }
        }
        appendTextIfBingoGameIsInFinalState(stringBuilder);
        return stringBuilder.toString();
    }

    private boolean bingoGameIsInInitialState(BingoGameState bingoGameState) {
        return bingoGameState.equals(BingoGameState.LEVEL_INITIALIZED) ||
                bingoGameState.equals(BingoGameState.PREREQUISITE_SETUP_DONE);
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
        int previousLevel = currentLevel - 1;
        stringBuilder.append("Challenge ended voluntarily on level ")
                .append(currentLevel)
                .append(". Your reward from the previous level: ")
                .append(bingoResultBars.getNumberOfSubsAsStringForLevel(previousLevel));
        appendTextForTotalReward(bingoResultBars.getNumberOfSubsAsRewardForLevel(previousLevel), stringBuilder);
    }

    private void appendTextForSuccessfulMatch(StringBuilder stringBuilder) {
        stringBuilder.append(" ✅ Unlocked reward: ")
                .append(bingoResultBars.getNumberOfSubsAsStringForLevel(currentLevel));
        if (hasNextLevel()) {
            appendTextForTokenCounterWithPrefix(WHITESPACE, stringBuilder);
            stringBuilder.append(" ➡️ ").append(getPointRequirementOfLevelAsString(currentLevel + 1));
        } else {
            stringBuilder.append(" This is the highest reward you can get. Congratulations! 🎊");
            appendTextForTotalReward(bingoResultBars.getNumberOfSubsAsRewardForLevel(currentLevel), stringBuilder);
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
            stringBuilder.append(" 🔄");
            appendTextForTokenCounterWithPrefix(WHITESPACE, stringBuilder);
        } else {
            stringBuilder.append(
                    "None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: ");
            stringBuilder.append(bingoResultBars.getNumberOfSubsAsStringForLevel(0));
            appendTextForTotalReward(bingoResultBars.getNumberOfSubsAsRewardForLevel(0), stringBuilder);
        }
    }

    private void appendTextForTotalReward(int unlockedReward, StringBuilder stringBuilder) {
        if (tokenCounter.hasExtraLife() || anyChallengeModifierIsActive()) {
            stringBuilder.append(WHITESPACE).append(getTotalRewardAsString(unlockedReward)).append(" 🎁");
        }
    }

    private void appendTextForTokenCounterWithPrefix(String prefix, StringBuilder stringBuilder) {
        if (extraLivesAreEnabled()) {
            stringBuilder.append(prefix).append(tokenCounter);
        }
    }

    private void appendTextIfBingoGameIsInFinalState(StringBuilder stringBuilder) {
        if (bingoGameStateMachine.getCurrentState().isFinal()) {
            stringBuilder.append("\n\nEnd of challenge confirmed. Changes are no longer allowed.");
        }
    }

    private boolean anyChallengeModifierIsActive() {
        return !challengeModifiers.isEmpty();
    }

    private boolean shipRestrictionsAreEnabled() {
        return challengeModifiers.contains(ChallengeModifier.RANDOM_SHIP_RESTRICTIONS);
    }

    private boolean endingVoluntarilyIsAllowed() {
        return !challengeModifiers.contains(ChallengeModifier.NO_GIVING_UP);
    }

    private boolean extraLivesAreEnabled() {
        return !challengeModifiers.contains(ChallengeModifier.NO_SAFETY_NET);
    }
}
