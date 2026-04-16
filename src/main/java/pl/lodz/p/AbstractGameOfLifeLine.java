package pl.lodz.p;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public abstract class AbstractGameOfLifeLine implements Serializable, Cloneable {
    protected GameOfLifeCell[] cells;

    public AbstractGameOfLifeLine(GameOfLifeCell[] cells) {
        this.cells = cells;
    }

    public int countAliveCells() {
        int alive = 0;
        for (GameOfLifeCell cell : cells) {
            if (cell.getCellValue()) {
                alive++;
            }
        }
        return alive;
    }

    public int countDeadCells() {
        return cells.length - countAliveCells();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("cells", cells)
                .append("aliveCells", countAliveCells())
                .append("deadCells", countDeadCells())
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
        AbstractGameOfLifeLine that = (AbstractGameOfLifeLine) obj;
        return new EqualsBuilder().append(cells, that.cells).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cells).toHashCode();
    }

    @Override
    public AbstractGameOfLifeLine clone() {
        try {
            //return (AbstractGameOfLifeLine)
            AbstractGameOfLifeLine val = (AbstractGameOfLifeLine) super.clone();
            val.cells = cells.clone();
            return val;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
