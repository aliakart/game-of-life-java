package pl.lodz.p;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GameOfLifeColumn extends AbstractGameOfLifeLine {
    public GameOfLifeColumn(GameOfLifeCell[] cells) {
        super(cells);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
