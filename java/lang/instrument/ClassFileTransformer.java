package java.lang.instrument;

import java.security.ProtectionDomain;

public interface ClassFileTransformer
{
    byte[] transform(final ClassLoader p0, final String p1, final Class<?> p2, final ProtectionDomain p3, final byte[] p4) throws IllegalClassFormatException;
}
