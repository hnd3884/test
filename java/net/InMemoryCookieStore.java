package java.net;

import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.List;

class InMemoryCookieStore implements CookieStore
{
    private List<HttpCookie> cookieJar;
    private Map<String, List<HttpCookie>> domainIndex;
    private Map<URI, List<HttpCookie>> uriIndex;
    private ReentrantLock lock;
    
    public InMemoryCookieStore() {
        this.cookieJar = null;
        this.domainIndex = null;
        this.uriIndex = null;
        this.lock = null;
        this.cookieJar = new ArrayList<HttpCookie>();
        this.domainIndex = new HashMap<String, List<HttpCookie>>();
        this.uriIndex = new HashMap<URI, List<HttpCookie>>();
        this.lock = new ReentrantLock(false);
    }
    
    @Override
    public void add(final URI uri, final HttpCookie httpCookie) {
        if (httpCookie == null) {
            throw new NullPointerException("cookie is null");
        }
        this.lock.lock();
        try {
            this.cookieJar.remove(httpCookie);
            if (httpCookie.getMaxAge() != 0L) {
                this.cookieJar.add(httpCookie);
                if (httpCookie.getDomain() != null) {
                    this.addIndex(this.domainIndex, httpCookie.getDomain(), httpCookie);
                }
                if (uri != null) {
                    this.addIndex(this.uriIndex, this.getEffectiveURI(uri), httpCookie);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public List<HttpCookie> get(final URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri is null");
        }
        final ArrayList list = new ArrayList();
        final boolean equalsIgnoreCase = "https".equalsIgnoreCase(uri.getScheme());
        this.lock.lock();
        try {
            this.getInternal1(list, this.domainIndex, uri.getHost(), equalsIgnoreCase);
            this.getInternal2(list, this.uriIndex, this.getEffectiveURI(uri), equalsIgnoreCase);
        }
        finally {
            this.lock.unlock();
        }
        return list;
    }
    
    @Override
    public List<HttpCookie> getCookies() {
        this.lock.lock();
        List<Object> unmodifiableList = null;
        try {
            final Iterator<HttpCookie> iterator = this.cookieJar.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().hasExpired()) {
                    iterator.remove();
                }
            }
        }
        finally {
            unmodifiableList = Collections.unmodifiableList((List<?>)this.cookieJar);
            this.lock.unlock();
        }
        return (List<HttpCookie>)unmodifiableList;
    }
    
    @Override
    public List<URI> getURIs() {
        final ArrayList list = new ArrayList();
        this.lock.lock();
        try {
            final Iterator<URI> iterator = this.uriIndex.keySet().iterator();
            while (iterator.hasNext()) {
                final List list2 = this.uriIndex.get(iterator.next());
                if (list2 == null || list2.size() == 0) {
                    iterator.remove();
                }
            }
        }
        finally {
            list.addAll(this.uriIndex.keySet());
            this.lock.unlock();
        }
        return list;
    }
    
    @Override
    public boolean remove(final URI uri, final HttpCookie httpCookie) {
        if (httpCookie == null) {
            throw new NullPointerException("cookie is null");
        }
        boolean remove = false;
        this.lock.lock();
        try {
            remove = this.cookieJar.remove(httpCookie);
        }
        finally {
            this.lock.unlock();
        }
        return remove;
    }
    
    @Override
    public boolean removeAll() {
        this.lock.lock();
        try {
            if (this.cookieJar.isEmpty()) {
                return false;
            }
            this.cookieJar.clear();
            this.domainIndex.clear();
            this.uriIndex.clear();
        }
        finally {
            this.lock.unlock();
        }
        return true;
    }
    
    private boolean netscapeDomainMatches(final String s, final String s2) {
        if (s == null || s2 == null) {
            return false;
        }
        final boolean equalsIgnoreCase = ".local".equalsIgnoreCase(s);
        int n = s.indexOf(46);
        if (n == 0) {
            n = s.indexOf(46, 1);
        }
        if (!equalsIgnoreCase && (n == -1 || n == s.length() - 1)) {
            return false;
        }
        if (s2.indexOf(46) == -1 && equalsIgnoreCase) {
            return true;
        }
        final int n2 = s2.length() - s.length();
        if (n2 == 0) {
            return s2.equalsIgnoreCase(s);
        }
        if (n2 > 0) {
            s2.substring(0, n2);
            return s2.substring(n2).equalsIgnoreCase(s);
        }
        return n2 == -1 && s.charAt(0) == '.' && s2.equalsIgnoreCase(s.substring(1));
    }
    
    private void getInternal1(final List<HttpCookie> list, final Map<String, List<HttpCookie>> map, final String s, final boolean b) {
        final ArrayList list2 = new ArrayList();
        for (final Map.Entry entry : map.entrySet()) {
            final String s2 = (String)entry.getKey();
            final List list3 = (List)entry.getValue();
            for (final HttpCookie httpCookie : list3) {
                if ((httpCookie.getVersion() == 0 && this.netscapeDomainMatches(s2, s)) || (httpCookie.getVersion() == 1 && HttpCookie.domainMatches(s2, s))) {
                    if (this.cookieJar.indexOf(httpCookie) != -1) {
                        if (!httpCookie.hasExpired()) {
                            if ((!b && httpCookie.getSecure()) || list.contains(httpCookie)) {
                                continue;
                            }
                            list.add(httpCookie);
                        }
                        else {
                            list2.add(httpCookie);
                        }
                    }
                    else {
                        list2.add(httpCookie);
                    }
                }
            }
            for (final HttpCookie httpCookie2 : list2) {
                list3.remove(httpCookie2);
                this.cookieJar.remove(httpCookie2);
            }
            list2.clear();
        }
    }
    
    private <T> void getInternal2(final List<HttpCookie> list, final Map<T, List<HttpCookie>> map, final Comparable<T> comparable, final boolean b) {
        for (final T next : map.keySet()) {
            if (comparable.compareTo(next) == 0) {
                final List list2 = map.get(next);
                if (list2 == null) {
                    continue;
                }
                final Iterator iterator2 = list2.iterator();
                while (iterator2.hasNext()) {
                    final HttpCookie httpCookie = (HttpCookie)iterator2.next();
                    if (this.cookieJar.indexOf(httpCookie) != -1) {
                        if (!httpCookie.hasExpired()) {
                            if ((!b && httpCookie.getSecure()) || list.contains(httpCookie)) {
                                continue;
                            }
                            list.add(httpCookie);
                        }
                        else {
                            iterator2.remove();
                            this.cookieJar.remove(httpCookie);
                        }
                    }
                    else {
                        iterator2.remove();
                    }
                }
            }
        }
    }
    
    private <T> void addIndex(final Map<T, List<HttpCookie>> map, final T t, final HttpCookie httpCookie) {
        if (t != null) {
            final List list = map.get(t);
            if (list != null) {
                list.remove(httpCookie);
                list.add(httpCookie);
            }
            else {
                final ArrayList list2 = new ArrayList();
                list2.add(httpCookie);
                map.put(t, list2);
            }
        }
    }
    
    private URI getEffectiveURI(final URI uri) {
        URI uri2;
        try {
            uri2 = new URI("http", uri.getHost(), null, null, null);
        }
        catch (final URISyntaxException ex) {
            uri2 = uri;
        }
        return uri2;
    }
}
