package org.bouncycastle.jcajce;

import org.bouncycastle.util.StoreException;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.security.cert.Certificate;

public interface PKIXCertStore<T extends Certificate> extends Store<T>
{
    Collection<T> getMatches(final Selector<T> p0) throws StoreException;
}
