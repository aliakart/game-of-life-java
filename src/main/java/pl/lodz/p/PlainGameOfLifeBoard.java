package pl.lodz.p;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class PlainGameOfLifeBoard implements GameOfLifeSimulator {
    public void doStep(GameOfLifeBoard board) {
        List<List<Boolean>> next = new ArrayList<>(board.getHeight()); // Tymczasowa tablica na nowy stan
        for (int i = 0; i < board.getHeight(); i++) {
            List<Boolean> row = new ArrayList<>(board.getWidth());
            for (int j = 0; j < board.getWidth(); j++) {
                row.add(board.getCell(i, j).nextState()); // Liczba żywych sąsiadów
            }
            next.add(row);
        }
        board.setBoard(next); // Aktualizacja stanu planszy
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
