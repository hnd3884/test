package org.apache.commons.httpclient;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class HeaderGroup
{
    private List headers;
    
    public HeaderGroup() {
        this.headers = new ArrayList();
    }
    
    public void clear() {
        this.headers.clear();
    }
    
    public void addHeader(final Header header) {
        this.headers.add(header);
    }
    
    public void removeHeader(final Header header) {
        this.headers.remove(header);
    }
    
    public void setHeaders(final Header[] headers) {
        this.clear();
        for (int i = 0; i < headers.length; ++i) {
            this.addHeader(headers[i]);
        }
    }
    
    public Header getCondensedHeader(final String name) {
        final Header[] headers = this.getHeaders(name);
        if (headers.length == 0) {
            return null;
        }
        if (headers.length == 1) {
            return new Header(headers[0].getName(), headers[0].getValue());
        }
        final StringBuffer valueBuffer = new StringBuffer(headers[0].getValue());
        for (int i = 1; i < headers.length; ++i) {
            valueBuffer.append(", ");
            valueBuffer.append(headers[i].getValue());
        }
        return new Header(name.toLowerCase(), valueBuffer.toString());
    }
    
    public Header[] getHeaders(final String name) {
        final ArrayList headersFound = new ArrayList();
        final Iterator headerIter = this.headers.iterator();
        while (headerIter.hasNext()) {
            final Header header = headerIter.next();
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }
        return headersFound.toArray(new Header[headersFound.size()]);
    }
    
    public Header getFirstHeader(final String name) {
        final Iterator headerIter = this.headers.iterator();
        while (headerIter.hasNext()) {
            final Header header = headerIter.next();
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }
    
    public Header getLastHeader(final String name) {
        for (int i = this.headers.size() - 1; i >= 0; --i) {
            final Header header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }
    
    public Header[] getAllHeaders() {
        return this.headers.toArray(new Header[this.headers.size()]);
    }
    
    public boolean containsHeader(final String name) {
        final Iterator headerIter = this.headers.iterator();
        while (headerIter.hasNext()) {
            final Header header = headerIter.next();
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public Iterator getIterator() {
        return this.headers.iterator();
    }
}
