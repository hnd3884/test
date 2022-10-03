package sun.corba;

import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.spi.orb.ORB;

public final class OutputStreamFactory
{
    private OutputStreamFactory() {
    }
    
    public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB orb) {
        return AccessController.doPrivileged((PrivilegedAction<TypeCodeOutputStream>)new PrivilegedAction<TypeCodeOutputStream>() {
            @Override
            public TypeCodeOutputStream run() {
                return new TypeCodeOutputStream(orb);
            }
        });
    }
    
    public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB orb, final boolean b) {
        return AccessController.doPrivileged((PrivilegedAction<TypeCodeOutputStream>)new PrivilegedAction<TypeCodeOutputStream>() {
            @Override
            public TypeCodeOutputStream run() {
                return new TypeCodeOutputStream(orb, b);
            }
        });
    }
    
    public static EncapsOutputStream newEncapsOutputStream(final ORB orb) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsOutputStream>)new PrivilegedAction<EncapsOutputStream>() {
            @Override
            public EncapsOutputStream run() {
                return new EncapsOutputStream(orb);
            }
        });
    }
    
    public static EncapsOutputStream newEncapsOutputStream(final ORB orb, final GIOPVersion giopVersion) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsOutputStream>)new PrivilegedAction<EncapsOutputStream>() {
            @Override
            public EncapsOutputStream run() {
                return new EncapsOutputStream(orb, giopVersion);
            }
        });
    }
    
    public static EncapsOutputStream newEncapsOutputStream(final ORB orb, final boolean b) {
        return AccessController.doPrivileged((PrivilegedAction<EncapsOutputStream>)new PrivilegedAction<EncapsOutputStream>() {
            @Override
            public EncapsOutputStream run() {
                return new EncapsOutputStream(orb, b);
            }
        });
    }
    
    public static CDROutputObject newCDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message message, final byte b) {
        return AccessController.doPrivileged((PrivilegedAction<CDROutputObject>)new PrivilegedAction<CDROutputObject>() {
            @Override
            public CDROutputObject run() {
                return new CDROutputObject(orb, messageMediator, message, b);
            }
        });
    }
    
    public static CDROutputObject newCDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message message, final byte b, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<CDROutputObject>)new PrivilegedAction<CDROutputObject>() {
            @Override
            public CDROutputObject run() {
                return new CDROutputObject(orb, messageMediator, message, b, n);
            }
        });
    }
    
    public static CDROutputObject newCDROutputObject(final ORB orb, final CorbaMessageMediator corbaMessageMediator, final GIOPVersion giopVersion, final CorbaConnection corbaConnection, final Message message, final byte b) {
        return AccessController.doPrivileged((PrivilegedAction<CDROutputObject>)new PrivilegedAction<CDROutputObject>() {
            @Override
            public CDROutputObject run() {
                return new CDROutputObject(orb, corbaMessageMediator, giopVersion, corbaConnection, message, b);
            }
        });
    }
}
