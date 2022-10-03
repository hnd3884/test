package org.bouncycastle.cert.dane;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;

public class DANECertificateFetcher
{
    private final DANEEntryFetcherFactory fetcherFactory;
    private final DANEEntrySelectorFactory selectorFactory;
    
    public DANECertificateFetcher(final DANEEntryFetcherFactory fetcherFactory, final DigestCalculator digestCalculator) {
        this.fetcherFactory = fetcherFactory;
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }
    
    public List fetch(final String s) throws DANEException {
        final DANEEntrySelector selector = this.selectorFactory.createSelector(s);
        final List entries = this.fetcherFactory.build(selector.getDomainName()).getEntries();
        final ArrayList list = new ArrayList(entries.size());
        for (final DANEEntry daneEntry : entries) {
            if (selector.match(daneEntry)) {
                list.add((Object)daneEntry.getCertificate());
            }
        }
        return Collections.unmodifiableList((List<?>)list);
    }
}
