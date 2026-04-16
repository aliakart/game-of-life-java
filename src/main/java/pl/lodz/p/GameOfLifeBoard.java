package pl.lodz.p;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameOfLifeBoard implements Cloneable, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(GameOfLifeBoard.class);
    private final int height; // Liczba wierszy planszy
    private final int width; // Liczba kolumn planszy
    private List<List<GameOfLifeCell>> board; // Lista list przechowująca stan komórek
    private final Random random = new Random();

    // Konstruktor - tworzy planszę i losowo ustawia komórki
    public GameOfLifeBoard(int height, int width, double probability) {
        this.height = height;
        this.width = width;
        randomizeBoard(probability);
        setNeighbors();
    }

    // dodatkowy konstruktor dla testów
    public GameOfLifeBoard(int height, int width) {
        this(height, width, 0.5);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    // Losowe wypełnienie planszy komórkami żywymi lub martwymi
    private void randomizeBoard(double probability) {
        List<List<GameOfLifeCell>> tempBoard = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            List<GameOfLifeCell> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                boolean value = random.nextDouble() < probability; // losowość z API
                row.add(new GameOfLifeCell(value));
            }
            tempBoard.add(List.copyOf(row)); // wiersz nierozszerzalny
        }
        this.board = List.copyOf(tempBoard); // lista nierozszerzalna
    }


    public void setNeighbors() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                GameOfLifeCell[] neighbors = new GameOfLifeCell[8];
                int count = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) {
                            continue; // Pomijamy samą komórkę
                        }
                        int nx = (i + dx + height) % height; // owijanie w pionie
                        int ny = (j + dy + width) % width;   // owijanie w poziomie
                        neighbors[count++] = board.get(nx).get(ny);
                    }
                }
                board.get(i).get(j).setNeighbors(neighbors);
            }
        }
    }

    // reset planszy
    public void resetAll() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board.get(i).get(j).updateState(false);
            }
        }
    }

    public GameOfLifeCell getCell(int x, int y) {
        // pobieramy stan komórki na pozycji (x, y)
        if (x >= 0 && x < height && y >= 0 && y < width) {
           return board.get(x).get(y);
        }
        return null;
    }

    public void setCell(int x, int y, boolean value) {
        if (x >= 0 && x < height && y >= 0 && y < width) {
            board.get(x).get(y).updateState(value);
        }
    }

    public GameOfLifeRow getRow(int x) {
        if (x >= 0 && x < height) {
            GameOfLifeCell[] row = board.get(x).toArray(new GameOfLifeCell[0]);
            return new GameOfLifeRow(row);
        }
        return null;
    }

    public GameOfLifeColumn getColumn(int y) {
        if (y >= 0 && y < width) {
            GameOfLifeCell[] col = new GameOfLifeCell[height];
            for (int i = 0; i < height; i++) {
                col[i] = board.get(i).get(y);
            }
            return new GameOfLifeColumn(col);
        }
        return null;
    }

    // Wykonanie jednego kroku symulacji gry
    public void doSimulationStep() {
        GameOfLifeSimulator simulator = new PlainGameOfLifeBoard();
        simulator.doStep(this);
    }

    // Zwraca kopię planszy
    public List<List<Boolean>> getBoard() {
        List<List<Boolean>> copy = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            List<Boolean> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                row.add(board.get(i).get(j).getCellValue());
            }
            copy.add(row);
        }
        return copy;
    }

    public void setBoard(List<List<Boolean>> boardArray) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.board.get(i).get(j).updateState(boardArray.get(i).get(j));
            }
        }
    }

    // Wyświetlanie planszy w konsoli
    public void showBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(board.get(i).get(j).getCellValue() ? "⬜" : "⬛");
            }
            sb.append("\n");
        }
        logger.info("Current Board State:\n{}", sb.toString());
    }




    @Override
    public GameOfLifeBoard clone() {
        //gleboka kopia
        GameOfLifeBoard clonedBoard = SerializationUtils.clone(this);
        // trzeba ponownie ustawić sąsiadów w klonie, aby wskazywali na nowe sklonowane komórki.
        clonedBoard.setNeighbors();

        return clonedBoard;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("height", height)
                .append("width", width)
                .append("board", board)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GameOfLifeBoard that = (GameOfLifeBoard) obj;
        return new EqualsBuilder()
                .append(height, that.height)
                .append(width, that.width)
                .append(board, that.board)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(height)
                .append(width)
                .append(board)
                .toHashCode();
    }
}
