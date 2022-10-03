package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

interface SecurityCtx
{
    byte[] wrap(final byte[] p0, final int p1, final int p2) throws SaslException;
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2) throws SaslException;
}
