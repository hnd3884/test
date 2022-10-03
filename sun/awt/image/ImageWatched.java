package sun.awt.image;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.lang.ref.WeakReference;
import java.awt.Image;
import java.awt.image.ImageObserver;

public abstract class ImageWatched
{
    public static Link endlink;
    public Link watcherList;
    
    public ImageWatched() {
        this.watcherList = ImageWatched.endlink;
    }
    
    public synchronized void addWatcher(final ImageObserver imageObserver) {
        if (imageObserver != null && !this.isWatcher(imageObserver)) {
            this.watcherList = new WeakLink(imageObserver, this.watcherList);
        }
        this.watcherList = this.watcherList.removeWatcher(null);
    }
    
    public synchronized boolean isWatcher(final ImageObserver imageObserver) {
        return this.watcherList.isWatcher(imageObserver);
    }
    
    public void removeWatcher(final ImageObserver imageObserver) {
        synchronized (this) {
            this.watcherList = this.watcherList.removeWatcher(imageObserver);
        }
        if (this.watcherList == ImageWatched.endlink) {
            this.notifyWatcherListEmpty();
        }
    }
    
    public boolean isWatcherListEmpty() {
        synchronized (this) {
            this.watcherList = this.watcherList.removeWatcher(null);
        }
        return this.watcherList == ImageWatched.endlink;
    }
    
    public void newInfo(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (this.watcherList.newInfo(image, n, n2, n3, n4, n5)) {
            this.removeWatcher(null);
        }
    }
    
    protected abstract void notifyWatcherListEmpty();
    
    static {
        ImageWatched.endlink = new Link();
    }
    
    public static class Link
    {
        public boolean isWatcher(final ImageObserver imageObserver) {
            return false;
        }
        
        public Link removeWatcher(final ImageObserver imageObserver) {
            return this;
        }
        
        public boolean newInfo(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
            return false;
        }
    }
    
    static class AccWeakReference<T> extends WeakReference<T>
    {
        private final AccessControlContext acc;
        
        AccWeakReference(final T t) {
            super(t);
            this.acc = AccessController.getContext();
        }
    }
    
    public static class WeakLink extends Link
    {
        private final AccWeakReference<ImageObserver> myref;
        private Link next;
        
        public WeakLink(final ImageObserver imageObserver, final Link next) {
            this.myref = new AccWeakReference<ImageObserver>(imageObserver);
            this.next = next;
        }
        
        @Override
        public boolean isWatcher(final ImageObserver imageObserver) {
            return this.myref.get() == imageObserver || this.next.isWatcher(imageObserver);
        }
        
        @Override
        public Link removeWatcher(final ImageObserver imageObserver) {
            final ImageObserver imageObserver2 = this.myref.get();
            if (imageObserver2 == null) {
                return this.next.removeWatcher(imageObserver);
            }
            if (imageObserver2 == imageObserver) {
                return this.next;
            }
            this.next = this.next.removeWatcher(imageObserver);
            return this;
        }
        
        private static boolean update(final ImageObserver imageObserver, final AccessControlContext accessControlContext, final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
            return (accessControlContext != null || System.getSecurityManager() != null) && AccessController.doPrivileged(() -> imageObserver2.imageUpdate(image2, n6, n7, n8, n9, n10), accessControlContext);
        }
        
        @Override
        public boolean newInfo(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
            boolean info = this.next.newInfo(image, n, n2, n3, n4, n5);
            final ImageObserver imageObserver = this.myref.get();
            if (imageObserver == null) {
                info = true;
            }
            else if (!update(imageObserver, ((AccWeakReference<Object>)this.myref).acc, image, n, n2, n3, n4, n5)) {
                this.myref.clear();
                info = true;
            }
            return info;
        }
    }
}
