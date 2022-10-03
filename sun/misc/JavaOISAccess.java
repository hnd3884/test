package sun.misc;

import java.io.InvalidClassException;
import java.io.ObjectInputStream;

public interface JavaOISAccess
{
    void setObjectInputFilter(final ObjectInputStream p0, final ObjectInputFilter p1);
    
    ObjectInputFilter getObjectInputFilter(final ObjectInputStream p0);
    
    void checkArray(final ObjectInputStream p0, final Class<?> p1, final int p2) throws InvalidClassException;
}
