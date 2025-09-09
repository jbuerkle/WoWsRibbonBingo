package bingo.game.utility;

import bingo.game.BingoGame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
