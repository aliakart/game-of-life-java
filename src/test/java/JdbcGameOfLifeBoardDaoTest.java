import pl.lodz.p.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcGameOfLifeBoardDaoTest {

    private static final String TEST_BOARD_NAME = "test_db_board_junit";

    private static final String URL = "jdbc:postgresql://localhost:5432/nbddb";
    private static final String USER = "nbd";
    private static final String PASS = "nbdpassword";

    private GameOfLifeBoard originalBoard;

    @BeforeEach
    public void setUp() {
        originalBoard = new GameOfLifeBoard(5, 5);
        originalBoard.setCell(0, 0, true);
        originalBoard.setCell(2, 2, true);
        originalBoard.setCell(4, 4, true);
        originalBoard.setNeighbors();

        cleanUpDatabase();
    }

    @AfterEach
    public void tearDown() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM boards WHERE name = ?")) {
            stmt.setString(1, TEST_BOARD_NAME);
            stmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    //TEST 1
    @Test
    public void testWriteAndReadBoardFromDatabase() {
        // 1. ZAPIS
        try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getJdbcDao(TEST_BOARD_NAME)) {
            assertDoesNotThrow(() -> dao.write(originalBoard),
                    "Zapis do bazy nie powinien rzucać wyjątków");
        } catch (Exception e) {
            fail("Błąd podczas tworzenia lub zamykania DAO podczas zapisu: " + e.getMessage());
        }

        // 2. ODCZYT
        GameOfLifeBoard readBoard = null;
        try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getJdbcDao(TEST_BOARD_NAME)) {
            readBoard = dao.read();
        } catch (Exception e) {
            fail("Błąd podczas odczytu z bazy: " + e.getMessage());
        }

        // 3. WERYFIKACJA
        assertNotNull(readBoard, "Wczytana plansza nie powinna być nullem");

        // sprawdzenie czy to różne obiekty w pamięci
        assertNotSame(originalBoard, readBoard, "Obiekty powinny mieć różne referencje w pamięci");

        // prawdzenie czy zawartość jest identyczna
        assertEquals(originalBoard, readBoard, "Zawartość planszy z bazy powinna zgadzać się z oryginałem");

        //  sprawdzenie konkretnej komórki
        assertTrue(readBoard.getCell(2, 2).getCellValue(), "Komórka (2,2) powinna być żywa");
    }

    //TEST 2
    @Test
    public void testReadNonExistentBoardThrowsException() {
        String fakeName = "missing_board_" + System.currentTimeMillis();

        try (Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getJdbcDao(fakeName)) {
            //wyjątek GameOfLifeException
            assertThrows(GameOfLifeException.class, () -> {
                dao.read();
            }, "Odczyt nieistniejącej planszy powinien rzucić wyjątek");

        } catch (Exception e) {
        }
    }
}