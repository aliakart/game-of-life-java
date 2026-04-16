package pl.lodz.p;

import java.sql.*;

public class JdbcGameOfLifeBoardDao implements Dao<GameOfLifeBoard> {

    private static final String URL = "jdbc:postgresql://localhost:5432/nbddb";
    private static final String USER = "nbd";
    private static final String PASS = "nbdpassword";

    private final Connection connection;
    private final String boardName;


    public JdbcGameOfLifeBoardDao(String boardName) throws BoardDatabaseException {
        this.boardName = boardName;
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASS);
            initializeTables();
        } catch (SQLException e) {
            throw new BoardDatabaseException("error.db.connect", e);
        }
    }

    private void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS boards ("
                            + "id SERIAL PRIMARY KEY, "
                            + "name VARCHAR(255) UNIQUE NOT NULL, "
                            + "width INTEGER NOT NULL, "
                            + "height INTEGER NOT NULL)"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cells ("
                            + "board_id INTEGER NOT NULL, "
                            + "x INTEGER NOT NULL, "
                            + "y INTEGER NOT NULL, "
                            + "FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE)"
            );
        }
    }

    @Override
    public void write(GameOfLifeBoard board) throws GameOfLifeException {
        try {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement psDelete = connection.prepareStatement(
                        "DELETE FROM boards WHERE name = ?")) {
                    psDelete.setString(1, boardName);
                    psDelete.executeUpdate();
                }

                int boardId;
                String sqlBoard = "INSERT INTO boards (name, width, height) VALUES (?, ?, ?) RETURNING id";

                try (PreparedStatement psBoard = connection.prepareStatement(sqlBoard)) {
                    psBoard.setString(1, boardName);
                    psBoard.setInt(2, board.getWidth());
                    psBoard.setInt(3, board.getHeight());

                    try (ResultSet rs = psBoard.executeQuery()) {
                        if (rs.next()) {
                            boardId = rs.getInt(1);
                        } else {
                            throw new SQLException("Nie udało się pobrać ID nowej planszy.");
                        }
                    }
                }

                String sqlCell = "INSERT INTO cells (board_id, x, y) VALUES (?, ?, ?)";
                try (PreparedStatement psCell = connection.prepareStatement(sqlCell)) {
                    for (int i = 0; i < board.getHeight(); i++) {
                        for (int j = 0; j < board.getWidth(); j++) {
                            if (board.getCell(i, j).getCellValue()) {
                                psCell.setInt(1, boardId);
                                psCell.setInt(2, i); // Row
                                psCell.setInt(3, j); // Col
                                psCell.addBatch();
                            }
                        }
                    }
                    psCell.executeBatch();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new BoardDatabaseException("error.db", e);
        }
    }

    @Override
    public GameOfLifeBoard read() throws GameOfLifeException {
        try {
            int width;
            int boardId;
            int height;

            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT id, width, height FROM boards WHERE name = ?")) {
                ps.setString(1, boardName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boardId = rs.getInt("id");
                        width = rs.getInt("width");
                        height = rs.getInt("height");
                    } else {
                        throw new BoardDatabaseException("error.db.missing", null);
                    }
                }
            }


            GameOfLifeBoard board = new GameOfLifeBoard(width, height, 0.0);

            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT x, y FROM cells WHERE board_id = ?")) {
                ps.setInt(1, boardId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int x = rs.getInt("x");
                        int y = rs.getInt("y");
                        board.setCell(x, y, true);
                    }
                }
            }

            return board;

        } catch (SQLException e) {
            throw new BoardDatabaseException("error.db", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas zamykania połączenia: " + e.getMessage());
        }
    }
}