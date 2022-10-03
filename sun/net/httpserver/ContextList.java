package sun.net.httpserver;

import java.util.Iterator;
import java.util.LinkedList;

class ContextList
{
    static final int MAX_CONTEXTS = 50;
    LinkedList<HttpContextImpl> list;
    
    ContextList() {
        this.list = new LinkedList<HttpContextImpl>();
    }
    
    public synchronized void add(final HttpContextImpl httpContextImpl) {
        assert httpContextImpl.getPath() != null;
        this.list.add(httpContextImpl);
    }
    
    public synchronized int size() {
        return this.list.size();
    }
    
    synchronized HttpContextImpl findContext(final String s, final String s2) {
        return this.findContext(s, s2, false);
    }
    
    synchronized HttpContextImpl findContext(String lowerCase, final String s, final boolean b) {
        lowerCase = lowerCase.toLowerCase();
        String s2 = "";
        HttpContextImpl httpContextImpl = null;
        for (final HttpContextImpl httpContextImpl2 : this.list) {
            if (!httpContextImpl2.getProtocol().equals(lowerCase)) {
                continue;
            }
            final String path = httpContextImpl2.getPath();
            if (b && !path.equals(s)) {
                continue;
            }
            if (!b && !s.startsWith(path)) {
                continue;
            }
            if (path.length() <= s2.length()) {
                continue;
            }
            s2 = path;
            httpContextImpl = httpContextImpl2;
        }
        return httpContextImpl;
    }
    
    public synchronized void remove(final String s, final String s2) throws IllegalArgumentException {
        final HttpContextImpl context = this.findContext(s, s2, true);
        if (context == null) {
            throw new IllegalArgumentException("cannot remove element from list");
        }
        this.list.remove(context);
    }
    
    public synchronized void remove(final HttpContextImpl httpContextImpl) throws IllegalArgumentException {
        for (final HttpContextImpl httpContextImpl2 : this.list) {
            if (httpContextImpl2.equals(httpContextImpl)) {
                this.list.remove(httpContextImpl2);
                return;
            }
        }
        throw new IllegalArgumentException("no such context in list");
    }
}
