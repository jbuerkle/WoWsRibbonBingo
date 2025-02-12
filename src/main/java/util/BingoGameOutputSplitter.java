package util;

public class BingoGameOutputSplitter {
    private static final int CHAT_MESSAGE_MAX_LENGTH = 500;

    public String process(String bingoGameOutput) {
        if (bingoGameOutput.length() <= CHAT_MESSAGE_MAX_LENGTH) {
            return bingoGameOutput;
        }
        int indexOfEqualsSign = bingoGameOutput.indexOf('=');
        if (indexOfEqualsSign <= CHAT_MESSAGE_MAX_LENGTH) {
            return splitAt(indexOfEqualsSign, bingoGameOutput);
        }
        int lastIndexOfPlusSign = bingoGameOutput.length();
        while (lastIndexOfPlusSign > CHAT_MESSAGE_MAX_LENGTH) {
            lastIndexOfPlusSign = bingoGameOutput.lastIndexOf('+', lastIndexOfPlusSign - 1);
        }
        return splitFirstPart(lastIndexOfPlusSign, bingoGameOutput) +
                process(splitRest(lastIndexOfPlusSign, bingoGameOutput));
    }

    private String splitAt(int index, String bingoGameOutput) {
        return splitFirstPart(index, bingoGameOutput) + splitRest(index, bingoGameOutput);
    }

    private String splitFirstPart(int index, String bingoGameOutput) {
        return bingoGameOutput.substring(0, index) + "\n";
    }

    private String splitRest(int index, String bingoGameOutput) {
        return bingoGameOutput.substring(index);
    }
}
