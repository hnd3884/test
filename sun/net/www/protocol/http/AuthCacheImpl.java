package sun.net.www.protocol.http;

import java.util.ListIterator;
import java.util.LinkedList;
import java.util.HashMap;

public class AuthCacheImpl implements AuthCache
{
    HashMap<String, LinkedList<AuthCacheValue>> hashtable;
    
    public AuthCacheImpl() {
        this.hashtable = new HashMap<String, LinkedList<AuthCacheValue>>();
    }
    
    public void setMap(final HashMap<String, LinkedList<AuthCacheValue>> hashtable) {
        this.hashtable = hashtable;
    }
    
    @Override
    public synchronized void put(final String s, final AuthCacheValue authCacheValue) {
        LinkedList list = this.hashtable.get(s);
        final String path = authCacheValue.getPath();
        if (list == null) {
            list = new LinkedList();
            this.hashtable.put(s, list);
        }
        final ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            final AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
            if (authenticationInfo.path == null || authenticationInfo.path.startsWith(path)) {
                listIterator.remove();
            }
        }
        listIterator.add(authCacheValue);
    }
    
    @Override
    public synchronized AuthCacheValue get(final String s, final String s2) {
        final LinkedList list = this.hashtable.get(s);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (s2 == null) {
            return (AuthenticationInfo)list.get(0);
        }
        final ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            final AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
            if (s2.startsWith(authenticationInfo.path)) {
                return authenticationInfo;
            }
        }
        return null;
    }
    
    @Override
    public synchronized void remove(final String s, final AuthCacheValue authCacheValue) {
        final LinkedList list = this.hashtable.get(s);
        if (list == null) {
            return;
        }
        if (authCacheValue == null) {
            list.clear();
            return;
        }
        final ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            if (authCacheValue.equals(listIterator.next())) {
                listIterator.remove();
            }
        }
    }
}
