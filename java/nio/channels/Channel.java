package java.nio.channels;

import java.io.IOException;
import java.io.Closeable;

public interface Channel extends Closeable
{
    boolean isOpen();
    
    void close() throws IOException;
}
