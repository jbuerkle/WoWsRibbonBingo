package bingo.game.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoGameOutputSplitterTest {
    private static final String PREFIX_LENGTH_21 = "Ribbon Bingo result: ";
    private static final String CALCULATION_LENGTH_25 = "Destroyed: 5 * 120 points";
    private static final String CALCULATION_LENGTH_118 =
            "Main gun hit: 100 * 1 points + Shot down by fighter: 10 * 5 points + Spotted: 5 * 5 points + Sonar ping: 10 * 1 points";
    private static final String CALCULATION_LENGTH_151 =
            "Secondary hit: 500 * 1 points + Torpedo hit: 10 * 30 points + Citadel hit: 10 * 30 points + Captured: 5 * 60 points + Aircraft shot down: 50 * 5 points";
    private static final String CALCULATION_LENGTH_278 =
            "Caused flooding: 5 * 40 points + Buff picked up: 5 * 40 points + Set on fire: 10 * 20 points + Assisted in capture: 5 * 30 points + Incapacitation: 10 * 10 points + Rocket hit: 50 * 2 points + Bomb hit: 50 * 2 points + Defended: 10 * 10 points + Depth charge hit: 10 * 10 points";
    private static final String CALCULATION_LENGTH_432 =
            CALCULATION_LENGTH_151.concat(" + ").concat(CALCULATION_LENGTH_278);
    private static final String CALCULATION_LENGTH_460 =
            CALCULATION_LENGTH_25.concat(" + ").concat(CALCULATION_LENGTH_432);
    private static final String RESULT_LENGTH_164 =
            "0 points. Requirement of level 1: 200 points, which means your result does not meet the point requirement, and the challenge is over. You lose any unlocked rewards.";
    private static final String RESULT_LENGTH_290 =
            "5000 points. Requirement of level 1: 200 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 1 sub. You can now choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 2: 400 points";
    private static final String STRING_LENGTH_170_STARTING_WITH_LINE_BREAK =
            "\n\nThis is a very long test string which should be ignored by the splitter, as it starts with a line break and therefore does not extend the actual length of the string.";

    private final BingoGameOutputSplitter bingoGameOutputSplitter = new BingoGameOutputSplitter();

    @Test
    void shouldSplitLongOutputAtPlusSign() {
        String stringWithLength895 = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_118)
                .concat(" = ")
                .concat(RESULT_LENGTH_290);
        String expectedOutput = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460)
                .concat(" \n+ ")
                .concat(CALCULATION_LENGTH_118)
                .concat(" = ")
                .concat(RESULT_LENGTH_290);
        assertEquals(expectedOutput, bingoGameOutputSplitter.process(stringWithLength895));
    }

    @Test
    void shouldSplitLongOutputAtEqualSign() {
        String stringWithLength774 =
                PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460).concat(" = ").concat(RESULT_LENGTH_290);
        String expectedOutput =
                PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460).concat(" \n= ").concat(RESULT_LENGTH_290);
        assertEquals(expectedOutput, bingoGameOutputSplitter.process(stringWithLength774));
    }

    @Test
    void shouldSplitVeryLongOutputAtPlusSignAndAtEqualSign() {
        String stringWithLength1700 = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_460)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_460)
                .concat(" = ")
                .concat(RESULT_LENGTH_290);
        String expectedOutput = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_460)
                .concat(" \n+ ")
                .concat(CALCULATION_LENGTH_460)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_25)
                .concat(" \n+ ")
                .concat(CALCULATION_LENGTH_432)
                .concat(" \n= ")
                .concat(RESULT_LENGTH_290);
        assertEquals(expectedOutput, bingoGameOutputSplitter.process(stringWithLength1700));
    }

    @Test
    void shouldNotSplitShorterOutputIfNotNecessary() {
        String stringWithLength493 = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_25)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_151)
                .concat(" = ")
                .concat(RESULT_LENGTH_290);
        assertEquals(stringWithLength493, bingoGameOutputSplitter.process(stringWithLength493));
    }

    @Test
    void shouldNotSplitLongerOutputIfNotNecessaryBecauseOfLineBreak() {
        String stringWithLength663 = PREFIX_LENGTH_21.concat(CALCULATION_LENGTH_25)
                .concat(" + ")
                .concat(CALCULATION_LENGTH_151)
                .concat(" = ")
                .concat(RESULT_LENGTH_290)
                .concat(STRING_LENGTH_170_STARTING_WITH_LINE_BREAK);
        assertEquals(stringWithLength663, bingoGameOutputSplitter.process(stringWithLength663));
    }

    @Test
    void shouldNotCrashWhenThereIsNeitherEqualSignNorPlusSignInTheOutput() {
        String stringWithLength185 = PREFIX_LENGTH_21.concat(RESULT_LENGTH_164);
        assertEquals(stringWithLength185, bingoGameOutputSplitter.process(stringWithLength185));
    }
}
