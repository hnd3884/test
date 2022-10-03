package org.apache.xml.security.utils;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;

public class CachedXPathAPIHolder
{
    static ThreadLocal local;
    static ThreadLocal localDoc;
    
    public static void setDoc(final Document document) {
        if (CachedXPathAPIHolder.localDoc.get() != document) {
            final CachedXPathAPI cachedXPathAPI = CachedXPathAPIHolder.local.get();
            if (cachedXPathAPI == null) {
                CachedXPathAPIHolder.local.set(new CachedXPathAPI());
                CachedXPathAPIHolder.localDoc.set(document);
                return;
            }
            cachedXPathAPI.getXPathContext().reset();
            CachedXPathAPIHolder.localDoc.set(document);
        }
    }
    
    public static CachedXPathAPI getCachedXPathAPI() {
        CachedXPathAPI cachedXPathAPI = CachedXPathAPIHolder.local.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new CachedXPathAPI();
            CachedXPathAPIHolder.local.set(cachedXPathAPI);
            CachedXPathAPIHolder.localDoc.set(null);
        }
        return cachedXPathAPI;
    }
    
    static {
        CachedXPathAPIHolder.local = new ThreadLocal();
        CachedXPathAPIHolder.localDoc = new ThreadLocal();
    }
}
