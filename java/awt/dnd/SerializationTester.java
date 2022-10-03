package java.awt.dnd;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;

final class SerializationTester
{
    private static ObjectOutputStream stream;
    
    static boolean test(final Object o) {
        if (!(o instanceof Serializable)) {
            return false;
        }
        try {
            SerializationTester.stream.writeObject(o);
        }
        catch (final IOException ex) {
            return false;
        }
        finally {
            try {
                SerializationTester.stream.reset();
            }
            catch (final IOException ex2) {}
        }
        return true;
    }
    
    private SerializationTester() {
    }
    
    static {
        try {
            SerializationTester.stream = new ObjectOutputStream(new OutputStream() {
                @Override
                public void write(final int n) {
                }
            });
        }
        catch (final IOException ex) {}
    }
}
