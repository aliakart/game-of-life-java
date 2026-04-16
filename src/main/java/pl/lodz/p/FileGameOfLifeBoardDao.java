package pl.lodz.p;

import java.io.*;

public class FileGameOfLifeBoardDao implements Dao<GameOfLifeBoard> {

    private final String filename;

    public FileGameOfLifeBoardDao(String filename) {
        this.filename = filename;
    }

    @Override
    public GameOfLifeBoard read() throws BoardFileException {

        try (
                FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            return (GameOfLifeBoard) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new BoardFileException("error.load", e);
        }
    }

    @Override
    public void write(GameOfLifeBoard obj) throws BoardFileException {
        try (
                FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
        ) {
            objectOut.writeObject(obj);
        } catch (IOException e) {
            throw new BoardFileException("error.save", e);
        }
    }

    @Override
    public void close() {
    }
}