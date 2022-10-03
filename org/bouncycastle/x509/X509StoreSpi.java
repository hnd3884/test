package org.bouncycastle.x509;

import java.util.Collection;
import org.bouncycastle.util.Selector;

public abstract class X509StoreSpi
{
    public abstract void engineInit(final X509StoreParameters p0);
    
    public abstract Collection engineGetMatches(final Selector p0);
}
