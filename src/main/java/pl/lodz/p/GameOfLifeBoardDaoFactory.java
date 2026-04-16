package pl.lodz.p;

public class GameOfLifeBoardDaoFactory {

    public static Dao<GameOfLifeBoard> getFileDao(String filename) {
        return new FileGameOfLifeBoardDao(filename);
    }

    public static Dao<GameOfLifeBoard> getJdbcDao(String boardName) throws GameOfLifeException {
        return new JdbcGameOfLifeBoardDao(boardName);
    }
}