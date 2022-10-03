package sun.misc;

import java.io.IOException;
import java.io.ObjectInputStream;

@FunctionalInterface
public interface JavaObjectInputStreamReadString
{
    String readString(final ObjectInputStream p0) throws IOException;
}
