package java.nio.file;

import java.io.IOException;
import java.util.Iterator;
import java.io.Closeable;

public interface DirectoryStream<T> extends Closeable, Iterable<T>
{
    Iterator<T> iterator();
    
    @FunctionalInterface
    public interface Filter<T>
    {
        boolean accept(final T p0) throws IOException;
    }
}
