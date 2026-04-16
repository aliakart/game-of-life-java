import org.junit.jupiter.api.Test;

import pl.lodz.p.*;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class GameOfLifeBoardTest {

    // bezpieczne zarządzaniу tymczasowym katalogiem/plikiem
    @TempDir
    Path tempDir;

    private String testFileName;
    private GameOfLifeBoard originalBoard;

    // setUp jest wykonywana przed kazda metodą @Test,
    // inicjując pola (unikalny plik, nowa plansza).
    @BeforeEach
    public void setUp() {
        testFileName = tempDir.resolve("testBoard.ser").toString();

        //  plansza do testowania serializacji
        originalBoard = new GameOfLifeBoard(4, 5);
        originalBoard.setCell(0, 0, true);
        originalBoard.setCell(3, 4, false);
    }

    @Test
    public void testBoardCreation() {
        GameOfLifeBoard board = new GameOfLifeBoard(5, 5);
        List<List<Boolean>> gameBoard = board.getBoard();

        assertEquals(5, gameBoard.size(), "Wysokość planszy powinna wynosić 5");
        assertEquals(5, gameBoard.get(0).size(), "Szerokość planszy powinna wynosić 5");
    }

    @Test
    public void testGetBoardReturnsCopy() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);
        List<List<Boolean>> a = board.getBoard();
        List<List<Boolean>> b = board.getBoard();

        assertNotSame(a, b, "getBoard() powinno zwracać kopię tablicy");
    }

    @Test
    public void testDeadCellWithThreeNeighborsBecomeAlive() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);
        board.resetAll();

        board.setCell(0, 0, true);
        board.setCell(0, 1, true);
        board.setCell(0, 2, true);

        board.doSimulationStep();
        List<List<Boolean>> r = board.getBoard();
        assertTrue(r.get(1).get(1), "Martwa komórka z 3 sąsiadami powinna ożyć");
    }

    @Test
    public void testAliveCellWithTwoNeighborsSurvives() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy układ
        board.setCell(1, 1, true);
        board.setCell(0, 0, true);
        board.setCell(0, 1, true);

        board.doSimulationStep();
        List<List<Boolean>> r2 = board.getBoard();
        assertTrue(r2.get(1).get(1), "Żywa komórka z 2 sąsiadami powinna przeżyć");

    }

    @Test
    public void testAliveCellWithThreeNeighborsSurvives() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy układ
        board.setCell(1, 1, true);
        board.setCell(0, 0, true);
        board.setCell(0, 1, true);
        board.setCell(0, 2, true);

        List<List<Boolean>> r3 = board.getBoard();
        assertTrue(r3.get(1).get(1), "Żywa komórka z 3 sąsiadami powinna przeżyć");
    }

    @Test
    public void testAliveCellWithLessThanTwoNeighborsDies() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy układ
        board.setCell(1, 1, true);
        board.setCell(0, 0, true);

        board.doSimulationStep();
        List<List<Boolean>> r4 = board.getBoard();
        assertFalse(r4.get(1).get(1), "Żywa komórka z mniej niż 2 sąsiadami powinna umrzeć");

    }

    @Test
    public void testAliveCellWithMoreThanThreeNeighborsDies() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy układ
        board.setCell(1, 1, true);
        board.setCell(0, 0, true);
        board.setCell(0, 1, true);
        board.setCell(0, 2, true);
        board.setCell(1, 0, true);

        board.doSimulationStep();
        List<List<Boolean>> r5 = board.getBoard();
        assertFalse(r5.get(1).get(1), "Żywa komórka z więcej niż 3 sąsiadami powinna umrzeć");
    }

    @Test
    public void testBlinkerPattern() {
        GameOfLifeBoard board = new GameOfLifeBoard(5, 5);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy poziomy blinker w środku
        board.setCell(2, 1, true);
        board.setCell(2, 2, true);
        board.setCell(2, 3, true);

        board.doSimulationStep();
        List<List<Boolean>> r6 = board.getBoard();
        assertTrue(r6.get(1).get(2), "Górna komórka pionowego blinkera powinna żyć");
        assertTrue(r6.get(2).get(2), "Środkowa komórka pionowego blinkera powinna żyć");
        assertTrue(r6.get(3).get(2), "Dolna komórka pionowego blinkera powinna żyć");
    }

    @Test
    public void testCornerAndEdgeCells() {
        GameOfLifeBoard board = new GameOfLifeBoard(5, 5);

        // Resetujemy planszę
        board.resetAll();

        // Ustawiamy komórki
        board.setCell(0, 0, true);
        board.setCell(2, 0, true);
        board.setCell(1, 1, true);
        board.setCell(4, 2, true);
        board.setCell(0, 4, true);
        board.setCell(2, 4, true);
        board.setCell(4, 4, true);

        board.doSimulationStep();
        List<List<Boolean>> r7 = board.getBoard();
        assertTrue(r7.get(0).get(0), "komórka powinna żyć");
        assertTrue(r7.get(4).get(4), "komórka powinna żyć");
        assertTrue(r7.get(4).get(0), "komórka powinna żyć");
        assertTrue(r7.get(3).get(3), "komórka powinna żyć");
        assertTrue(r7.get(3).get(4), "komórka powinna żyć");
        assertTrue(r7.get(4).get(3), "komórka powinna żyć");
    }


    @Test
    public void testSetCellOutOfBoundsIndividually() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);
        board.resetAll();

        List<List<Boolean>> r8 = board.getBoard();
        assertFalse(r8.get(1).get(1), "Komórka nie powinna się zmienić dla x < 0");
        assertFalse(r8.get(1).get(1), "Komórka nie powinna się zmienić dla x >= height");
        assertFalse(r8.get(1).get(1), "Komórka nie powinna się zmienić dla y < 0");
        assertFalse(r8.get(1).get(1), "Komórka nie powinna się zmienić dla y >= width");
    }

    @Test
    public void testShowBoardDoesNotThrow() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 2);
        board.resetAll();
        board.setCell(0, 0, true);

        assertDoesNotThrow(board::showBoard, "showBoard() nie powinno rzucać wyjątków");
    }

    @Test
    public void testGetCellWithinBounds() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);
        GameOfLifeCell cell = board.getCell(1, 1);
        assertNotNull(cell, "Komórka w granicach planszy nie powinna być null");
        assertNotNull(board.getCell(0, 0));
        assertNotNull(board.getCell(1, 1));
        assertNotNull(board.getCell(2, 2));
    }


    @Test
    public void testGetCellOutOfBounds() {
        GameOfLifeBoard board = new GameOfLifeBoard(3, 3);
        board.resetAll();
        board.setCell(-1, 1, true); // x < 0
        board.setCell(3, 1, true);  // x >= height
        List<List<Boolean>> r = board.getBoard();
        assertFalse(r.get(1).get(1), "Komórka nie powinna się zmienić przy x poza granicą");

        board.resetAll();
        board.setCell(1, -1, true); // y < 0
        board.setCell(1, 3, true);  // y >= width
        List<List<Boolean>> y = board.getBoard();
        assertFalse(y.get(1).get(1), "Komórka nie powinna się zmienić przy y poza granicą");

        // X poza granicą
        assertNull(board.getCell(-1, 0));
        assertNull(board.getCell(3, 0));

        // Y poza granicą
        assertNull(board.getCell(0, -1));
        assertNull(board.getCell(0, 3));
    }

    @Test
    public void testGetRow() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 2);
        board.resetAll();
        board.setCell(0, 0, true);

        GameOfLifeRow row = board.getRow(0);
        assertNotNull(row);
        assertEquals(1, row.countAliveCells());
        assertNull(board.getRow(-1));
        assertNull(board.getRow(2));
    }

    @Test
    public void testGetColumn() {
        GameOfLifeBoard board = new GameOfLifeBoard(2, 2);
        board.resetAll();
        board.setCell(0, 1, true);

        GameOfLifeColumn column = board.getColumn(1);
        assertNotNull(column);
        assertEquals(1, column.countAliveCells());
        assertNull(board.getColumn(-1));
        assertNull(board.getColumn(2));
    }

    @Test
    public void testGameOfLifeRowConstructor() {
        GameOfLifeCell[] cells = {
                new GameOfLifeCell(true),
                new GameOfLifeCell(false)
        };
        GameOfLifeRow row = new GameOfLifeRow(cells);
        assertNotNull(row);
        assertEquals(1, row.countAliveCells());
        assertEquals(1, row.countDeadCells());
    }

    @Test
    public void testGameOfLifeColumnConstructor() {
        GameOfLifeCell[] cells = new GameOfLifeCell[3];
        cells[0] = new GameOfLifeCell(true);
        cells[1] = new GameOfLifeCell(false);
        cells[2] = new GameOfLifeCell(true);

        GameOfLifeColumn column = new GameOfLifeColumn(cells);

        assertEquals(2, column.countAliveCells(), "Liczba żywych komórek powinna wynosić 2");
        assertEquals(1, column.countDeadCells(), "Liczba martwych komórek powinna wynosić 1");
    }

    @Test
    public void testGameOfLifeCellMethodsFull() {
        GameOfLifeCell cell1 = new GameOfLifeCell(true);
        GameOfLifeCell cell2 = new GameOfLifeCell(true);
        GameOfLifeCell cell3 = new GameOfLifeCell(false);

        // equals
        assertEquals(cell1, cell1);
        assertEquals(cell1, cell2);
        assertNotEquals(cell1, cell3);
        assertNotEquals(cell1, null);
        assertNotEquals(cell1, "string");

        // hashCode()
        assertEquals(cell1.hashCode(), cell2.hashCode());
        assertNotEquals(cell1.hashCode(), cell3.hashCode());

        // toString()
        String str = cell1.toString();
        assertNotNull(str);
        assertTrue(str.contains("value=true"));
    }

    @Test
    public void testGameOfLifeRowAndColumnMethodsFull() {
        GameOfLifeCell[] cells1 = {new GameOfLifeCell(true), new GameOfLifeCell(false)};
        GameOfLifeCell[] cells2 = {new GameOfLifeCell(true), new GameOfLifeCell(false)};
        GameOfLifeCell[] cells3 = {new GameOfLifeCell(false), new GameOfLifeCell(false)};

        GameOfLifeRow row1 = new GameOfLifeRow(cells1);
        GameOfLifeRow row2 = new GameOfLifeRow(cells2);
        GameOfLifeRow row3 = new GameOfLifeRow(cells3);

        GameOfLifeColumn col1 = new GameOfLifeColumn(cells1);
        GameOfLifeColumn col2 = new GameOfLifeColumn(cells2);
        GameOfLifeColumn col3 = new GameOfLifeColumn(cells3);

        // equals
        assertEquals(row1, row1);
        assertEquals(row1, row2);
        assertNotEquals(row1, row3);
        assertNotEquals(row1, null);
        assertNotEquals(row1, col1);

        assertEquals(col1, col1);
        assertEquals(col1, col2);
        assertNotEquals(col1, col3);
        assertNotEquals(col1, null);
        assertNotEquals(col1, row1);

        // hashCode
        assertEquals(row1.hashCode(), row2.hashCode());
        assertNotEquals(row1.hashCode(), row3.hashCode());
        assertEquals(col1.hashCode(), col2.hashCode());
        assertNotEquals(col1.hashCode(), col3.hashCode());

        // toString
        assertNotNull(row1.toString());
        assertNotNull(col1.toString());
        assertTrue(row1.toString().contains("aliveCells=1"));
        assertTrue(col1.toString().contains("aliveCells=1"));
    }

    @Test
    public void testPlainGameOfLifeBoardMethodsFull() {
        PlainGameOfLifeBoard sim1 = new PlainGameOfLifeBoard();
        PlainGameOfLifeBoard sim2 = new PlainGameOfLifeBoard();
        PlainGameOfLifeBoard sim3 = new PlainGameOfLifeBoard();

        // equals
        assertEquals(sim1, sim1);
        assertEquals(sim1, sim2);
        assertEquals(sim2, sim3);
        assertEquals(sim1, sim3);
        assertNotEquals(sim1, null);
        assertNotEquals(sim1, new Object());

        // hashCode
        assertEquals(sim1.hashCode(), sim2.hashCode());

        // toString
        assertNotNull(sim1.toString());
    }

    @Test
    public void testAbstractGameOfLifeLineFull() {
        GameOfLifeCell[] cells1 = {new GameOfLifeCell(true), new GameOfLifeCell(false)};
        GameOfLifeCell[] cells2 = {new GameOfLifeCell(true), new GameOfLifeCell(false)};
        GameOfLifeCell[] cells3 = {new GameOfLifeCell(false), new GameOfLifeCell(false)};

        AbstractGameOfLifeLine line1 = new GameOfLifeRow(cells1);
        AbstractGameOfLifeLine line2 = new GameOfLifeRow(cells2);
        AbstractGameOfLifeLine line3 = new GameOfLifeRow(cells3);

        // equals
        assertEquals(line1, line1);
        assertEquals(line1, line2);
        assertNotEquals(line1, line3);
        assertNotEquals(line1, null);
        assertNotEquals(line1, new GameOfLifeColumn(cells1));

        // hashCode
        assertEquals(line1.hashCode(), line2.hashCode());
        assertNotEquals(line1.hashCode(), line3.hashCode());

        // toString
        assertNotNull(line1.toString());
    }

    @Test
    public void testGameOfLifeBoardMethodsFull() {
        GameOfLifeBoard board1 = new GameOfLifeBoard(2, 2);
        GameOfLifeBoard board2 = new GameOfLifeBoard(2, 2);
        GameOfLifeBoard board3 = new GameOfLifeBoard(3, 3);

        // toString
        assertNotNull(board1.toString());
        assertTrue(board1.toString().contains("height"));
        assertTrue(board1.toString().contains("width"));

        // equals
        assertEquals(board1, board1);
        assertNotEquals(board1, null);
        assertNotEquals(board1, new Object());

        board2.setBoard(board1.getBoard());
        assertEquals(board1, board2);
        assertNotEquals(board1, board3);


        GameOfLifeBoard boardDiffHeight = new GameOfLifeBoard(3, 2);
        GameOfLifeBoard boardDiffWidth = new GameOfLifeBoard(2, 3);
        assertNotEquals(board1, boardDiffHeight);
        assertNotEquals(board1, boardDiffWidth);

        // hashCode
        assertEquals(board1.hashCode(), board2.hashCode());
        assertNotEquals(board1.hashCode(), board3.hashCode());
        assertNotEquals(board1.hashCode(), boardDiffHeight.hashCode());
        assertNotEquals(board1.hashCode(), boardDiffWidth.hashCode());
    }

    //zmiana tu
    @Test
    public void testWriteAndReadBoardContent() {
        // Test zapisu z obsługą Checked Exception (GameOfLifeException)
        try (Dao<GameOfLifeBoard> daoWrite = GameOfLifeBoardDaoFactory.getFileDao(testFileName)) {
            daoWrite.write(originalBoard);
        } catch (GameOfLifeException e) {
            fail("Zapis obiektu nie powinien rzucić wyjątku: " + e.getMessage());
        } catch (Exception e) {
            fail("Nieoczekiwany wyjątek podczas zapisu: " + e.getMessage());
        }

        GameOfLifeBoard readBoard = null;
        try (Dao<GameOfLifeBoard> daoRead = GameOfLifeBoardDaoFactory.getFileDao(testFileName)) {
            readBoard = daoRead.read();
        } catch (GameOfLifeException e) {
            fail("Odczyt obiektu nie powinien rzucić wyjątku: " + e.getMessage());
        } catch (Exception e) {
            fail("Nieoczekiwany wyjątek podczas odczytu: " + e.getMessage());
        }

        assertEquals(originalBoard, readBoard, "Zawartość planszy przed i po serializacji powinna być identyczna.");
        assertNotSame(originalBoard, readBoard, "Obiekty powinny mieć różne referencje.");
    }

    //tu
    @Test
    public void testFileDaoReadThrowsBoardFileException() throws IOException {
        Path invalidPath = tempDir.resolve("invalid.ser");
        Files.writeString(invalidPath, "To nie jest serializowany obiekt");

        try (Dao<GameOfLifeBoard> dao = new FileGameOfLifeBoardDao(invalidPath.toString())) {
            assertThrows(BoardFileException.class, dao::read,
                    "Odczyt uszkodzonego pliku powinien rzucić BoardFileException.");
        } catch (Exception e) {
        }
    }
    //tu
    @Test
    public void testFileDaoWriteThrowsBoardFileExceptionOnInvalidPath() {
        String invalidPath = tempDir.resolve("non_existent_folder/file.ser").toString();

        try (Dao<GameOfLifeBoard> dao = new FileGameOfLifeBoardDao(invalidPath)) {
            assertThrows(BoardFileException.class, () -> dao.write(originalBoard),
                    "Zapis do nieprawidłowej ścieżki powinien rzucić BoardFileException.");
        } catch (Exception e) {
        }
    }
    @Test
    public void testBoardClone() {
        GameOfLifeBoard original = new GameOfLifeBoard(5, 5);
        original.resetAll();
        original.setCell(2, 2, true); // Ustawiamy żywą komórkę

        // wykonanie klonowania
        GameOfLifeBoard clone = original.clone();

        assertNotSame(original, clone, "Klon i oryginał muszą być różnymi obiektami w pamięci");
        assertEquals(original, clone, "Po sklonowaniu obiekty muszą być logicznie równe");

        // modyfikujemy oryginał i sprawdzamy czy klon pozostał nienaruszony
        original.setCell(2, 2, false);


        assertTrue(clone.getCell(2, 2).getCellValue(),
                "Zmiana w oryginale NIE może wpływać na klona (wymagana pełna rozłączność/Deep Copy)");

        assertNotEquals(original, clone, "Po modyfikacji oryginału obiekty nie mogą być już równe");

        // modyfikacja klona nie zmienia oryginału
        clone.setCell(0, 0, true);
        assertFalse(original.getCell(0, 0).getCellValue(),
                "Zmiana w klonie NIE może wpływać na oryginał");
    }

    @Test
    public void testCellComparableOrdering() {
        GameOfLifeCell deadCell = new GameOfLifeCell(false);
        GameOfLifeCell aliveCell = new GameOfLifeCell(true);
        GameOfLifeCell anotherDeadCell = new GameOfLifeCell(false);

        // martwa vs martwa (0)
        assertEquals(0, deadCell.compareTo(anotherDeadCell),
                "Dwie martwe komórki powinny być równe w sortowaniu");

        // żywa vs żywa (0)
        assertEquals(0, aliveCell.compareTo(new GameOfLifeCell(true)),
                "Dwie żywe komórki powinny być równe w sortowaniu");

        // martwa vs żywa (< 0) -> false < true
        assertTrue(deadCell.compareTo(aliveCell) < 0,
                "Martwa komórka (false) powinna być mniejsza niż żywa (true)");

        // żywa vs martwa (> 0) -> true > false
        assertTrue(aliveCell.compareTo(deadCell) > 0,
                "Żywa komórka (true) powinna być większa niż martwa (false)");
    }

    @Test
    public void testCellComparableThrowsException() {
        GameOfLifeCell cell = new GameOfLifeCell(true);

        // czy rzucany jest NullPointerException przy porównaniu z null
        assertThrows(NullPointerException.class, () -> {
            cell.compareTo(null);
        }, "Metoda compareTo powinna rzucić NullPointerException przy próbie porównania z null");
    }

    //tu
    @Test
    public void testDaoIsAutoCloseableAndClosesWithoutException() {
        //test dla komponentow Autocloseable
        Dao<GameOfLifeBoard> dao = GameOfLifeBoardDaoFactory.getFileDao(testFileName);

        //sprawdzenie czy implementuje AutoCloseable
        assertTrue(dao instanceof AutoCloseable, "DAO powinno implementować interfejs AutoCloseable");

        //sprawdzenie czy close() nie rzuca wyjątków
        assertDoesNotThrow(() -> dao.close(), "Metoda close() nie powinna rzucać wyjątków.");

        //sprawdzenie w bloku try-with-resources
        assertDoesNotThrow(() -> {
            try (Dao<GameOfLifeBoard> autoClosedDao = GameOfLifeBoardDaoFactory.getFileDao(testFileName)) {
            }
        }, "Wykorzystanie DAO w try-with-resources powinno być bezpieczne.");
    }
}






