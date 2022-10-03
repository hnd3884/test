package sun.corba;

import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.ORB;

public class EncapsInputStreamFactory
{
    public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] array, final int n, final boolean b, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(orb, array, n, b, giopVersion);
            }
        });
    }
    
    public static EncapsInputStream newEncapsInputStream(final ORB orb, final ByteBuffer byteBuffer, final int n, final boolean b, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(orb, byteBuffer, n, b, giopVersion);
            }
        });
    }
    
    public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] array, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(orb, array, n);
            }
        });
    }
    
    public static EncapsInputStream newEncapsInputStream(final EncapsInputStream encapsInputStream) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(encapsInputStream);
            }
        });
    }
    
    public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] array, final int n, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(orb, array, n, giopVersion);
            }
        });
    }
    
    public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] array, final int n, final GIOPVersion giopVersion, final CodeBase codeBase) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsInputStream>)new PrivilegedAction<EncapsInputStream>() {
            @Override
            public EncapsInputStream run() {
                return new EncapsInputStream(orb, array, n, giopVersion, codeBase);
            }
        });
    }
    
    public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final byte[] array, final int n, final boolean b, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<TypeCodeInputStream>)new PrivilegedAction<TypeCodeInputStream>() {
            @Override
            public TypeCodeInputStream run() {
                return new TypeCodeInputStream(orb, array, n, b, giopVersion);
            }
        });
    }
    
    public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final ByteBuffer byteBuffer, final int n, final boolean b, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<TypeCodeInputStream>)new PrivilegedAction<TypeCodeInputStream>() {
            @Override
            public TypeCodeInputStream run() {
                return new TypeCodeInputStream(orb, byteBuffer, n, b, giopVersion);
            }
        });
    }
    
    public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final byte[] array, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<TypeCodeInputStream>)new PrivilegedAction<TypeCodeInputStream>() {
            @Override
            public TypeCodeInputStream run() {
                return new TypeCodeInputStream(orb, array, n);
            }
        });
    }
}
