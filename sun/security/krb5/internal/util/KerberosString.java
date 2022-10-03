package sun.security.krb5.internal.util;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.io.IOException;
import sun.security.util.DerValue;

public final class KerberosString
{
    public static final boolean MSNAME;
    private final String s;
    
    public KerberosString(final String s) {
        this.s = s;
    }
    
    public KerberosString(final DerValue derValue) throws IOException {
        if (derValue.tag != 27) {
            throw new IOException("KerberosString's tag is incorrect: " + derValue.tag);
        }
        this.s = new String(derValue.getDataBytes(), KerberosString.MSNAME ? "UTF8" : "ASCII");
    }
    
    @Override
    public String toString() {
        return this.s;
    }
    
    public DerValue toDerValue() throws IOException {
        return new DerValue((byte)27, this.s.getBytes(KerberosString.MSNAME ? "UTF8" : "ASCII"));
    }
    
    static {
        MSNAME = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.krb5.msinterop.kstring"));
    }
}
