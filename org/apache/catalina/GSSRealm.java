package org.apache.catalina;

import java.security.Principal;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

@Deprecated
public interface GSSRealm extends Realm
{
    Principal authenticate(final GSSName p0, final GSSCredential p1);
}
