package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;
import java.util.Vector;

public class MimeHeaders
{
    protected Vector headers;
    
    public MimeHeaders() {
        this.headers = new Vector();
    }
    
    public String[] getHeader(final String name) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.headers.size(); ++i) {
            final MimeHeader mimeheader = this.headers.elementAt(i);
            if (mimeheader.getName().equalsIgnoreCase(name) && mimeheader.getValue() != null) {
                vector.addElement(mimeheader.getValue());
            }
        }
        if (vector.size() == 0) {
            return null;
        }
        final String[] as = new String[vector.size()];
        vector.copyInto(as);
        return as;
    }
    
    public void setHeader(final String name, final String value) {
        boolean flag = false;
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Illegal MimeHeader name");
        }
        for (int i = 0; i < this.headers.size(); ++i) {
            final MimeHeader mimeheader = this.headers.elementAt(i);
            if (mimeheader.getName().equalsIgnoreCase(name)) {
                if (!flag) {
                    this.headers.setElementAt(new MimeHeader(mimeheader.getName(), value), i);
                    flag = true;
                }
                else {
                    this.headers.removeElementAt(i--);
                }
            }
        }
        if (!flag) {
            this.addHeader(name, value);
        }
    }
    
    public void addHeader(final String name, final String value) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Illegal MimeHeader name");
        }
        final int i = this.headers.size();
        for (int j = i - 1; j >= 0; --j) {
            final MimeHeader mimeheader = this.headers.elementAt(j);
            if (mimeheader.getName().equalsIgnoreCase(name)) {
                this.headers.insertElementAt(new MimeHeader(name, value), j + 1);
                return;
            }
        }
        this.headers.addElement(new MimeHeader(name, value));
    }
    
    public void removeHeader(final String name) {
        for (int i = 0; i < this.headers.size(); ++i) {
            final MimeHeader mimeheader = this.headers.elementAt(i);
            if (mimeheader.getName().equalsIgnoreCase(name)) {
                this.headers.removeElementAt(i--);
            }
        }
    }
    
    public void removeAllHeaders() {
        this.headers.removeAllElements();
    }
    
    public Iterator getAllHeaders() {
        return this.headers.iterator();
    }
    
    public Iterator getMatchingHeaders(final String[] names) {
        return new MatchingIterator(names, true);
    }
    
    public Iterator getNonMatchingHeaders(final String[] names) {
        return new MatchingIterator(names, false);
    }
    
    class MatchingIterator implements Iterator
    {
        private boolean match;
        private Iterator iterator;
        private String[] names;
        private Object nextHeader;
        
        private Object nextMatch() {
        Label_0000:
            while (this.iterator.hasNext()) {
                final MimeHeader mimeheader = this.iterator.next();
                if (this.names == null) {
                    return this.match ? null : mimeheader;
                }
                int i = 0;
                while (i < this.names.length) {
                    if (!mimeheader.getName().equalsIgnoreCase(this.names[i])) {
                        ++i;
                    }
                    else {
                        if (this.match) {
                            return mimeheader;
                        }
                        continue Label_0000;
                    }
                }
                if (!this.match) {
                    return mimeheader;
                }
            }
            return null;
        }
        
        @Override
        public boolean hasNext() {
            if (this.nextHeader == null) {
                this.nextHeader = this.nextMatch();
            }
            return this.nextHeader != null;
        }
        
        @Override
        public Object next() {
            if (this.nextHeader != null) {
                final Object obj = this.nextHeader;
                this.nextHeader = null;
                return obj;
            }
            if (this.hasNext()) {
                return this.nextHeader;
            }
            return null;
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
        }
        
        MatchingIterator(final String[] as, final boolean flag) {
            this.match = flag;
            this.names = as;
            this.iterator = MimeHeaders.this.headers.iterator();
        }
    }
}
