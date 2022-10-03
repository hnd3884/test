package org.apache.http.impl.client;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Comparator;
import org.apache.http.cookie.CookieIdentityComparator;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.http.cookie.Cookie;
import java.util.TreeSet;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Serializable;
import org.apache.http.client.CookieStore;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicCookieStore implements CookieStore, Serializable
{
    private static final long serialVersionUID = -7581093305228232025L;
    private final TreeSet<Cookie> cookies;
    private transient ReadWriteLock lock;
    
    public BasicCookieStore() {
        this.cookies = new TreeSet<Cookie>(new CookieIdentityComparator());
        this.lock = new ReentrantReadWriteLock();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.lock = new ReentrantReadWriteLock();
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
        if (cookie != null) {
            this.lock.writeLock().lock();
            try {
                this.cookies.remove(cookie);
                if (!cookie.isExpired(new Date())) {
                    this.cookies.add(cookie);
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }
    
    public void addCookies(final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }
    
    @Override
    public List<Cookie> getCookies() {
        this.lock.readLock().lock();
        try {
            return new ArrayList<Cookie>(this.cookies);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        this.lock.writeLock().lock();
        try {
            boolean removed = false;
            final Iterator<Cookie> it = this.cookies.iterator();
            while (it.hasNext()) {
                if (it.next().isExpired(date)) {
                    it.remove();
                    removed = true;
                }
            }
            return removed;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cookies.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        this.lock.readLock().lock();
        try {
            return this.cookies.toString();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}
