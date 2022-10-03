package org.jscep.util;

import org.bouncycastle.asn1.x500.X500Name;
import javax.security.auth.x500.X500Principal;

public final class X500Utils
{
    private X500Utils() {
    }
    
    public static X500Name toX500Name(final X500Principal principal) {
        final byte[] bytes = principal.getEncoded();
        return X500Name.getInstance((Object)bytes);
    }
}
