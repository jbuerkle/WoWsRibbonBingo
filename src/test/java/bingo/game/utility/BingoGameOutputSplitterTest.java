package bingo.game.utility;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BingoGameOutputSplitterTest {
    private static final String LONG_CALCULATION_STRING_PART_1 =
            "Ribbon Bingo result: Kraken Unleashed: 3000 * (30 points + (Destroyed: 3000 * 120 points) * 0.2) + Combat Scout: 3000 * (60 points + (Spotted: 3000 * 30 points) * 0.6) + Witherer: 3000 * (30 points + (Set on fire: 3000 * 20 points) * 0.3 + (Caused flooding: 3000 * 40 points) * 0.3) + AA Defense Expert: 3000 * (45 points + (Aircraft shot down: 3000 * 10 points) * 0.3 + (Shot down by fighter: 3000 * 10 points) * 0.3) + Arsonist: 3000 * (30 points + (Set on fire: 3000 * 20 points) * 0.1)";
    private static final String LONG_CALCULATION_STRING_PART_2 =
            " + Solo Warrior: 3000 * 300 points + High Caliber: 3000 * 150 points + Confederate: 3000 * 150 points + Destroyed: 3000 * 120 points + Captured: 3000 * 80 points + Double Strike: 3000 * 75 points + Buff picked up: 3000 * 60 points + Dreadnought: 3000 * 50 points + First Blood: 3000 * 50 points + It's Just a Flesh Wound: 3000 * 50 points + Unsinkable: 3000 * 50 points + Die-Hard: 3000 * 50 points + Devastating Strike: 3000 * 50 points + Fireproof: 3000 * 50 points";
    private static final String LONG_CALCULATION_STRING_PART_3 =
            " + Assisted in capture: 3000 * 40 points + Torpedo hit: 3000 * 40 points + Caused flooding: 3000 * 40 points + Spotted: 3000 * 30 points + Close Quarters Expert: 3000 * 25 points + Citadel hit: 3000 * 20 points + Set on fire: 3000 * 20 points + Depth charge hit: 3000 * 10 points + Shot down by fighter: 3000 * 10 points + Aircraft shot down: 3000 * 10 points + Incapacitation: 3000 * 10 points + Defended: 3000 * 10 points + Sonar ping: 3000 * 5 points + Bomb hit: 3000 * 3 points";
    private static final String LONG_CALCULATION_STRING_PART_4 =
            " + Rocket hit: 3000 * 3 points + Secondary hit: 3000 points + Main gun hit: 3000 points = 617274000 points. Requirement of level 1: 300 points ✅ Unlocked reward: 2 subs \uD83C\uDF81 Token counter: +1 token (successful match). Now 1 token \uD83E\uDE99 total. ➡️ Requirement of level 2: 500 points";
    private static final String SHORT_STRING = "This is a short test string. It should not be split.";
    private static final String STRING_STARTING_WITH_DOUBLE_LINE_BREAK =
            "\n\nThis string should always be split off, because it starts with a double line break.";
    private static final String PLAYER_A_RESULT_STRING = getResultStringForPlayer("Player A");
    private static final String PLAYER_B_RESULT_STRING = getResultStringForPlayer("Player B");
    private static final String PLAYER_C_RESULT_STRING = getResultStringForPlayer("Player C");
    private static final String REST_OF_RESULT_STRING =
            "Shared division achievements: Dummy division text which is slightly too long. Total result: 30 points + 30 points + 30 points + 600 points = 690 points. Requirement of level 1: 540 points ✅ Unlocked reward: 2 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 2: 900 points";
    private static final String CALCULATION_STRING_WITH_EXACTLY_500_CHARACTERS =
            "Ribbon Bingo result: 234567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890 + 4567890";
    private static final String REST_OF_CALCULATION_STRING =
            " + and this looks like the rest of a calculation string, although it really isn't one.";
    private static final String DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS =
            "This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. This is a dummy text which is exactly 500 characters long. Except it's ending in a dot.";
    private static final String REST_OF_DUMMY_STRING = " And this looks like the start of another sentence.";
    private static final String UNEXPECTED_INPUT =
            "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    private static final String EXPECTED_COMBINED_TEXT = "Dummy text 1\n\nDummy text 2\n\nDummy text 3\n\nDummy text 4";
    private static final String DUMMY_TEXT_1 = "Dummy text 1";
    private static final String DUMMY_TEXT_2 = "Dummy text 2";
    private static final String DUMMY_TEXT_3 = "Dummy text 3";
    private static final String DUMMY_TEXT_4 = "Dummy text 4";

    private final BingoGameOutputSplitter bingoGameOutputSplitter = new BingoGameOutputSplitter();

    @Test
    void shouldAlwaysSplitAtDoubleLineBreakRegardlessOfLength() {
        String inputString = SHORT_STRING.concat(STRING_STARTING_WITH_DOUBLE_LINE_BREAK)
                .concat(STRING_STARTING_WITH_DOUBLE_LINE_BREAK);
        List<String> splitOutput = bingoGameOutputSplitter.process(inputString);
        assertEquals(3, splitOutput.size());
        assertEquals(SHORT_STRING, splitOutput.getFirst());
        assertEquals(STRING_STARTING_WITH_DOUBLE_LINE_BREAK.substring(2), splitOutput.get(1));
        assertEquals(STRING_STARTING_WITH_DOUBLE_LINE_BREAK.substring(2), splitOutput.get(2));
    }

    @Test
    void shouldSplitLongInputAtSentenceEndIfPossible() {
        String inputString = PLAYER_A_RESULT_STRING.concat(PLAYER_B_RESULT_STRING)
                .concat(PLAYER_C_RESULT_STRING)
                .concat(REST_OF_RESULT_STRING);
        List<String> splitOutput = bingoGameOutputSplitter.process(inputString);
        assertEquals(4, splitOutput.size());
        assertEquals(PLAYER_A_RESULT_STRING.substring(0, PLAYER_A_RESULT_STRING.length() - 1), splitOutput.getFirst());
        assertEquals(PLAYER_B_RESULT_STRING.substring(0, PLAYER_B_RESULT_STRING.length() - 1), splitOutput.get(1));
        assertEquals(PLAYER_C_RESULT_STRING.substring(0, PLAYER_C_RESULT_STRING.length() - 1), splitOutput.get(2));
        assertEquals(REST_OF_RESULT_STRING, splitOutput.get(3));
    }

    @Test
    void shouldSplitLongCalculationAtPlusSign() {
        String inputString = LONG_CALCULATION_STRING_PART_1.concat(LONG_CALCULATION_STRING_PART_2)
                .concat(LONG_CALCULATION_STRING_PART_3)
                .concat(LONG_CALCULATION_STRING_PART_4);
        List<String> splitOutput = bingoGameOutputSplitter.process(inputString);
        assertEquals(4, splitOutput.size());
        assertEquals(LONG_CALCULATION_STRING_PART_1, splitOutput.getFirst());
        assertEquals(LONG_CALCULATION_STRING_PART_2.substring(1), splitOutput.get(1));
        assertEquals(LONG_CALCULATION_STRING_PART_3.substring(1), splitOutput.get(2));
        assertEquals(LONG_CALCULATION_STRING_PART_4.substring(1), splitOutput.get(3));
    }

    @Test
    void shouldSplitInputAtSentenceEndWithExactlyFiveHundredCharacters() {
        assertEquals(500, DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS.length());
        String inputString = DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS.concat(REST_OF_DUMMY_STRING);
        List<String> splitOutput = bingoGameOutputSplitter.process(inputString);
        assertEquals(2, splitOutput.size());
        assertEquals(DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS, splitOutput.getFirst());
        assertEquals(REST_OF_DUMMY_STRING.substring(1), splitOutput.get(1));
    }

    @Test
    void shouldSplitInputAtPlusSignWithExactlyFiveHundredCharacters() {
        assertEquals(500, CALCULATION_STRING_WITH_EXACTLY_500_CHARACTERS.length());
        String inputString = CALCULATION_STRING_WITH_EXACTLY_500_CHARACTERS.concat(REST_OF_CALCULATION_STRING);
        List<String> splitOutput = bingoGameOutputSplitter.process(inputString);
        assertEquals(2, splitOutput.size());
        assertEquals(CALCULATION_STRING_WITH_EXACTLY_500_CHARACTERS, splitOutput.getFirst());
        assertEquals(REST_OF_CALCULATION_STRING.substring(1), splitOutput.get(1));
    }

    @Test
    void shouldThrowIllegalStateExceptionForUnexpectedInput() {
        assertEquals(510, UNEXPECTED_INPUT.length());
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> bingoGameOutputSplitter.process(UNEXPECTED_INPUT));
        assertEquals("Could not find any appropriate index to split the output", exception.getMessage());
    }

    @Test
    void shouldNotSplitShortInputAtAll() {
        List<String> splitOutput = bingoGameOutputSplitter.process(SHORT_STRING);
        assertEquals(1, splitOutput.size());
        assertEquals(SHORT_STRING, splitOutput.getFirst());
    }

    @Test
    void shouldNotSplitInputIfExactlyFiveHundredCharactersLong() {
        List<String> splitOutput = bingoGameOutputSplitter.process(DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS);
        assertEquals(1, splitOutput.size());
        assertEquals(DUMMY_STRING_WITH_EXACTLY_500_CHARACTERS, splitOutput.getFirst());
    }

    @Test
    void shouldCombineStringsInOrder() {
        List<String> splitOutput = List.of(DUMMY_TEXT_1, DUMMY_TEXT_2, DUMMY_TEXT_3, DUMMY_TEXT_4);
        String splitOutputAsString = bingoGameOutputSplitter.combineAsStringWithDoubleLineBreaks(splitOutput);
        assertEquals(EXPECTED_COMBINED_TEXT, splitOutputAsString);
    }

    private static String getResultStringForPlayer(String playerName) {
        return "%s's Ribbon Bingo result: Kraken Unleashed: 3000 * (30 points + (Destroyed: 3000 * 120 points) * 0.2) + Combat Scout: 3000 * (60 points + (Spotted: 3000 * 30 points) * 0.6) + Witherer: 3000 * (30 points + (Set on fire: 3000 * 20 points) * 0.3 + (Caused flooding: 3000 * 40 points) * 0.3) + AA Defense Expert: 3000 * (45 points + (Aircraft shot down: 3000 * 10 points) * 0.3 + (Shot down by fighter: 3000 * 10 points) * 0.3) = dummy result. ".formatted(
                playerName);
    }
}
