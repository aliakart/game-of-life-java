package pl.lodz.p;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class GameOfLifeCell implements Comparable<GameOfLifeCell>, Cloneable, Serializable {
    private boolean cellValue;
    private GameOfLifeCell[] neighbors;

    public GameOfLifeCell(boolean cellValue) {
        this.cellValue = cellValue;
        this.neighbors = new GameOfLifeCell[8];
    }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    public boolean getCellValue() {
        return cellValue;
    }

    public void setCellValue(boolean newValue) {
        boolean oldValue = this.cellValue;
        this.cellValue = newValue;
        pcs.firePropertyChange("cellValue", oldValue, newValue);
    }

    public void setNeighbors(GameOfLifeCell[] neighbors) {
        this.neighbors = neighbors;
    }

    public boolean nextState() {
        int aliveNeighbors = 0;
        for (GameOfLifeCell neighbor : neighbors) {
            if (neighbor.getCellValue()) {
                aliveNeighbors++;
            }
        }
        if (cellValue) {
            return aliveNeighbors == 2 || aliveNeighbors == 3;
        } else {
            return aliveNeighbors == 3;
        }
    }

    public void updateState(boolean newState) {
        this.cellValue = newState;
    }

    @Override
    public int compareTo(GameOfLifeCell other) {
        //dodac wyjatek
        return Comparator.comparing(GameOfLifeCell::getCellValue).compare(this, other);
    }

    @Override
    public GameOfLifeCell clone() {
            try {
                //plytka kopia
                //kopiuje cała tablice i jej referencje do nowej tablicy.
                GameOfLifeCell clonedCell = (GameOfLifeCell) super.clone();
                clonedCell.neighbors = Arrays.copyOf(this.neighbors, this.neighbors.length);
                return clonedCell;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("value", cellValue).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GameOfLifeCell that = (GameOfLifeCell) obj;
        return new EqualsBuilder().append(cellValue, that.cellValue).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cellValue).toHashCode();
    }
}
