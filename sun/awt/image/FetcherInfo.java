package sun.awt.image;

import sun.awt.AppContext;
import java.util.Vector;

class FetcherInfo
{
    static final int MAX_NUM_FETCHERS_PER_APPCONTEXT = 4;
    Thread[] fetchers;
    int numFetchers;
    int numWaiting;
    Vector waitList;
    private static final Object FETCHER_INFO_KEY;
    
    private FetcherInfo() {
        this.fetchers = new Thread[4];
        this.numFetchers = 0;
        this.numWaiting = 0;
        this.waitList = new Vector();
    }
    
    static FetcherInfo getFetcherInfo() {
        final AppContext appContext = AppContext.getAppContext();
        synchronized (appContext) {
            FetcherInfo fetcherInfo = (FetcherInfo)appContext.get(FetcherInfo.FETCHER_INFO_KEY);
            if (fetcherInfo == null) {
                fetcherInfo = new FetcherInfo();
                appContext.put(FetcherInfo.FETCHER_INFO_KEY, fetcherInfo);
            }
            return fetcherInfo;
        }
    }
    
    static {
        FETCHER_INFO_KEY = new StringBuffer("FetcherInfo");
    }
}
