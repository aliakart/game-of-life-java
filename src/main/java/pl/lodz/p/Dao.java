package pl.lodz.p;

public interface Dao<T> extends AutoCloseable {

    T read() throws GameOfLifeException;

    void write(T obj) throws GameOfLifeException;

    @Override
    default void close() {

    }
}

