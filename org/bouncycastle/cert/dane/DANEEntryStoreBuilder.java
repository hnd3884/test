package org.bouncycastle.cert.dane;

public class DANEEntryStoreBuilder
{
    private final DANEEntryFetcherFactory daneEntryFetcher;
    
    public DANEEntryStoreBuilder(final DANEEntryFetcherFactory daneEntryFetcher) {
        this.daneEntryFetcher = daneEntryFetcher;
    }
    
    public DANEEntryStore build(final String s) throws DANEException {
        return new DANEEntryStore(this.daneEntryFetcher.build(s).getEntries());
    }
}
