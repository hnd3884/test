package com.sun.security.jgss;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import jdk.Exported;
import org.ietf.jgss.GSSCredential;

@Exported
public interface ExtendedGSSCredential extends GSSCredential
{
    GSSCredential impersonate(final GSSName p0) throws GSSException;
}
