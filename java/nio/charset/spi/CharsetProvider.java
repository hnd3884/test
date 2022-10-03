package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.security.Permission;

public abstract class CharsetProvider
{
    protected CharsetProvider() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("charsetProvider"));
        }
    }
    
    public abstract Iterator<Charset> charsets();
    
    public abstract Charset charsetForName(final String p0);
}
