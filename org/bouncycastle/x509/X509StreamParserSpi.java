package org.bouncycastle.x509;

import java.util.Collection;
import org.bouncycastle.x509.util.StreamParsingException;
import java.io.InputStream;

public abstract class X509StreamParserSpi
{
    public abstract void engineInit(final InputStream p0);
    
    public abstract Object engineRead() throws StreamParsingException;
    
    public abstract Collection engineReadAll() throws StreamParsingException;
}
