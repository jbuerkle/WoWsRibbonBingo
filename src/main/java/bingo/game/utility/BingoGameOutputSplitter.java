package bingo.game.utility;

import java.util.LinkedList;
import java.util.List;

public class BingoGameOutputSplitter {
    private static final int CHAT_MESSAGE_MAX_LENGTH = 500;
    private static final String DOUBLE_LINE_BREAK = "\n\n";

    public List<String> process(String bingoGameOutput) {
        List<String> firstResult = splitAtDoubleLineBreakIfFound(bingoGameOutput);
        if (!firstResult.isEmpty()) {
            return firstResult;
        }
        if (bingoGameOutput.length() <= CHAT_MESSAGE_MAX_LENGTH) {
            return List.of(bingoGameOutput);
        }
        List<String> secondResult = splitAtSearchStringIfFound(bingoGameOutput, ". ");
        if (!secondResult.isEmpty()) {
            return secondResult;
        }
        List<String> thirdResult = splitAtSearchStringIfFound(bingoGameOutput, " +");
        if (!thirdResult.isEmpty()) {
            return thirdResult;
        }
        throw new IllegalStateException("Could not find any appropriate index to split the output");
    }

    public String combineAsStringWithDoubleLineBreaks(List<String> splitOutput) {
        return splitOutput.stream()
                .reduce((stringA, stringB) -> stringA.concat(DOUBLE_LINE_BREAK).concat(stringB))
                .orElseThrow();
    }

    private List<String> splitAtSearchStringIfFound(String bingoGameOutput, String searchString) {
        int indexOfSpace = searchString.indexOf(" ");
        List<String> splitOutput = new LinkedList<>();
        int lastIndexOfSearchString = bingoGameOutput.length();
        while (lastIndexOfSearchString + indexOfSpace > CHAT_MESSAGE_MAX_LENGTH) {
            lastIndexOfSearchString = bingoGameOutput.lastIndexOf(searchString, lastIndexOfSearchString - 1);
        }
        if (lastIndexOfSearchString != -1) {
            splitOutput.add(splitFirstPart(lastIndexOfSearchString + indexOfSpace, bingoGameOutput));
            splitOutput.addAll(process(splitRest(lastIndexOfSearchString + indexOfSpace + 1, bingoGameOutput)));
        }
        return splitOutput;
    }

    private List<String> splitAtDoubleLineBreakIfFound(String bingoGameOutput) {
        List<String> splitOutput = new LinkedList<>();
        int lastIndexOfDoubleLineBreak = bingoGameOutput.lastIndexOf(DOUBLE_LINE_BREAK);
        if (lastIndexOfDoubleLineBreak != -1) {
            splitOutput.addAll(process(splitFirstPart(lastIndexOfDoubleLineBreak, bingoGameOutput)));
            splitOutput.add(splitRest(lastIndexOfDoubleLineBreak + DOUBLE_LINE_BREAK.length(), bingoGameOutput));
        }
        return splitOutput;
    }

    private String splitFirstPart(int index, String bingoGameOutput) {
        return bingoGameOutput.substring(0, index);
    }

    private String splitRest(int index, String bingoGameOutput) {
        return bingoGameOutput.substring(index);
    }
}
