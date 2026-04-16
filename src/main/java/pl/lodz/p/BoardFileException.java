package pl.lodz.p;

public class BoardFileException extends GameOfLifeException {
    public BoardFileException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }
}