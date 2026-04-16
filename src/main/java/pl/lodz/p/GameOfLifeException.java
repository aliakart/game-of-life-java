package pl.lodz.p;


public class GameOfLifeException extends Exception {
    private final String messageKey;

    public GameOfLifeException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public GameOfLifeException(String messageKey, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}