package sun.security.krb5.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Date;
import sun.security.krb5.Credentials;
import sun.security.krb5.PrincipalName;
import java.util.Map;

final class ReferralsCache
{
    private static Map<ReferralCacheKey, Map<String, ReferralCacheEntry>> referralsMap;
    
    static synchronized void put(final PrincipalName principalName, final PrincipalName principalName2, final String s, final String s2, final Credentials credentials) {
        final ReferralCacheKey referralCacheKey = new ReferralCacheKey(principalName, principalName2);
        pruneExpired(referralCacheKey);
        if (credentials.getEndTime().before(new Date())) {
            return;
        }
        Map map = ReferralsCache.referralsMap.get(referralCacheKey);
        if (map == null) {
            map = new HashMap();
            ReferralsCache.referralsMap.put(referralCacheKey, map);
        }
        map.remove(s);
        final ReferralCacheEntry referralCacheEntry = new ReferralCacheEntry(credentials, s2);
        map.put(s, referralCacheEntry);
        ReferralCacheEntry referralCacheEntry2 = referralCacheEntry;
        final LinkedList list = new LinkedList();
        while (referralCacheEntry2 != null) {
            if (list.contains(referralCacheEntry2)) {
                map.remove(referralCacheEntry.getToRealm());
                break;
            }
            list.add(referralCacheEntry2);
            referralCacheEntry2 = (ReferralCacheEntry)map.get(referralCacheEntry2.getToRealm());
        }
    }
    
    static synchronized ReferralCacheEntry get(final PrincipalName principalName, final PrincipalName principalName2, final String s) {
        final ReferralCacheKey referralCacheKey = new ReferralCacheKey(principalName, principalName2);
        pruneExpired(referralCacheKey);
        final Map map = ReferralsCache.referralsMap.get(referralCacheKey);
        if (map != null) {
            final ReferralCacheEntry referralCacheEntry = (ReferralCacheEntry)map.get(s);
            if (referralCacheEntry != null) {
                return referralCacheEntry;
            }
        }
        return null;
    }
    
    private static void pruneExpired(final ReferralCacheKey referralCacheKey) {
        final Date date = new Date();
        final Map map = ReferralsCache.referralsMap.get(referralCacheKey);
        if (map != null) {
            for (final Map.Entry entry : map.entrySet()) {
                if (((ReferralCacheEntry)entry.getValue()).getCreds().getEndTime().before(date)) {
                    map.remove(entry.getKey());
                }
            }
        }
    }
    
    static {
        ReferralsCache.referralsMap = new HashMap<ReferralCacheKey, Map<String, ReferralCacheEntry>>();
    }
    
    private static final class ReferralCacheKey
    {
        private PrincipalName cname;
        private PrincipalName sname;
        
        ReferralCacheKey(final PrincipalName cname, final PrincipalName sname) {
            this.cname = cname;
            this.sname = sname;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof ReferralCacheKey)) {
                return false;
            }
            final ReferralCacheKey referralCacheKey = (ReferralCacheKey)o;
            return this.cname.equals(referralCacheKey.cname) && this.sname.equals(referralCacheKey.sname);
        }
        
        @Override
        public int hashCode() {
            return this.cname.hashCode() + this.sname.hashCode();
        }
    }
    
    static final class ReferralCacheEntry
    {
        private final Credentials creds;
        private final String toRealm;
        
        ReferralCacheEntry(final Credentials creds, final String toRealm) {
            this.creds = creds;
            this.toRealm = toRealm;
        }
        
        Credentials getCreds() {
            return this.creds;
        }
        
        String getToRealm() {
            return this.toRealm;
        }
    }
}
