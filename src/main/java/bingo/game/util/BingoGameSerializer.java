package bingo.game.util;

import bingo.game.BingoGame;

import java.io.*;

public class BingoGameSerializer {

    public void saveGame(BingoGame bingoGame, String filePath) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(bingoGame);
        }
    }

    public BingoGame loadGame(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            return (BingoGame) inputStream.readObject();
        }
    }
}
