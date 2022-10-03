package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;

class ImageFetcher extends Thread
{
    static final int HIGH_PRIORITY = 8;
    static final int LOW_PRIORITY = 3;
    static final int ANIM_PRIORITY = 2;
    static final int TIMEOUT = 5000;
    
    private ImageFetcher(final ThreadGroup threadGroup, final int n) {
        super(threadGroup, "Image Fetcher " + n);
        this.setDaemon(true);
    }
    
    public static boolean add(final ImageFetchable imageFetchable) {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        synchronized (fetcherInfo.waitList) {
            if (!fetcherInfo.waitList.contains(imageFetchable)) {
                fetcherInfo.waitList.addElement(imageFetchable);
                if (fetcherInfo.numWaiting == 0 && fetcherInfo.numFetchers < fetcherInfo.fetchers.length) {
                    createFetchers(fetcherInfo);
                }
                if (fetcherInfo.numFetchers <= 0) {
                    fetcherInfo.waitList.removeElement(imageFetchable);
                    return false;
                }
                fetcherInfo.waitList.notify();
            }
        }
        return true;
    }
    
    public static void remove(final ImageFetchable imageFetchable) {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        synchronized (fetcherInfo.waitList) {
            if (fetcherInfo.waitList.contains(imageFetchable)) {
                fetcherInfo.waitList.removeElement(imageFetchable);
            }
        }
    }
    
    public static boolean isFetcher(final Thread thread) {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        synchronized (fetcherInfo.waitList) {
            for (int i = 0; i < fetcherInfo.fetchers.length; ++i) {
                if (fetcherInfo.fetchers[i] == thread) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean amFetcher() {
        return isFetcher(Thread.currentThread());
    }
    
    private static ImageFetchable nextImage() {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        synchronized (fetcherInfo.waitList) {
            ImageFetchable imageFetchable = null;
            final long n = System.currentTimeMillis() + 5000L;
            while (imageFetchable == null) {
                while (fetcherInfo.waitList.size() == 0) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis >= n) {
                        return null;
                    }
                    try {
                        final FetcherInfo fetcherInfo2 = fetcherInfo;
                        ++fetcherInfo2.numWaiting;
                        fetcherInfo.waitList.wait(n - currentTimeMillis);
                    }
                    catch (final InterruptedException ex) {
                        return null;
                    }
                    finally {
                        final FetcherInfo fetcherInfo3 = fetcherInfo;
                        --fetcherInfo3.numWaiting;
                    }
                }
                imageFetchable = fetcherInfo.waitList.elementAt(0);
                fetcherInfo.waitList.removeElement(imageFetchable);
            }
            return imageFetchable;
        }
    }
    
    @Override
    public void run() {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        try {
            this.fetchloop();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            synchronized (fetcherInfo.waitList) {
                final Thread currentThread = Thread.currentThread();
                for (int i = 0; i < fetcherInfo.fetchers.length; ++i) {
                    if (fetcherInfo.fetchers[i] == currentThread) {
                        fetcherInfo.fetchers[i] = null;
                        final FetcherInfo fetcherInfo2 = fetcherInfo;
                        --fetcherInfo2.numFetchers;
                    }
                }
            }
        }
        finally {
            synchronized (fetcherInfo.waitList) {
                final Thread currentThread2 = Thread.currentThread();
                for (int j = 0; j < fetcherInfo.fetchers.length; ++j) {
                    if (fetcherInfo.fetchers[j] == currentThread2) {
                        fetcherInfo.fetchers[j] = null;
                        final FetcherInfo fetcherInfo3 = fetcherInfo;
                        --fetcherInfo3.numFetchers;
                    }
                }
            }
        }
    }
    
    private void fetchloop() {
        final Thread currentThread = Thread.currentThread();
        while (isFetcher(currentThread)) {
            Thread.interrupted();
            currentThread.setPriority(8);
            final ImageFetchable nextImage = nextImage();
            if (nextImage == null) {
                return;
            }
            try {
                nextImage.doFetch();
            }
            catch (final Exception ex) {
                System.err.println("Uncaught error fetching image:");
                ex.printStackTrace();
            }
            stoppingAnimation(currentThread);
        }
    }
    
    static void startingAnimation() {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        final Thread currentThread = Thread.currentThread();
        synchronized (fetcherInfo.waitList) {
            for (int i = 0; i < fetcherInfo.fetchers.length; ++i) {
                if (fetcherInfo.fetchers[i] == currentThread) {
                    fetcherInfo.fetchers[i] = null;
                    final FetcherInfo fetcherInfo2 = fetcherInfo;
                    --fetcherInfo2.numFetchers;
                    currentThread.setName("Image Animator " + i);
                    if (fetcherInfo.waitList.size() > fetcherInfo.numWaiting) {
                        createFetchers(fetcherInfo);
                    }
                    return;
                }
            }
        }
        currentThread.setPriority(2);
        currentThread.setName("Image Animator");
    }
    
    private static void stoppingAnimation(final Thread thread) {
        final FetcherInfo fetcherInfo = FetcherInfo.getFetcherInfo();
        synchronized (fetcherInfo.waitList) {
            int n = -1;
            for (int i = 0; i < fetcherInfo.fetchers.length; ++i) {
                if (fetcherInfo.fetchers[i] == thread) {
                    return;
                }
                if (fetcherInfo.fetchers[i] == null) {
                    n = i;
                }
            }
            if (n >= 0) {
                fetcherInfo.fetchers[n] = thread;
                final FetcherInfo fetcherInfo2 = fetcherInfo;
                ++fetcherInfo2.numFetchers;
                thread.setName("Image Fetcher " + n);
            }
        }
    }
    
    private static void createFetchers(final FetcherInfo fetcherInfo) {
        final AppContext appContext = AppContext.getAppContext();
        final ThreadGroup threadGroup = appContext.getThreadGroup();
        ThreadGroup threadGroup2;
        try {
            if (threadGroup.getParent() != null) {
                threadGroup2 = threadGroup;
            }
            else {
                ThreadGroup threadGroup3 = Thread.currentThread().getThreadGroup();
                for (ThreadGroup threadGroup4 = threadGroup3.getParent(); threadGroup4 != null && threadGroup4.getParent() != null; threadGroup4 = threadGroup3.getParent()) {
                    threadGroup3 = threadGroup4;
                }
                threadGroup2 = threadGroup3;
            }
        }
        catch (final SecurityException ex) {
            threadGroup2 = appContext.getThreadGroup();
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                for (int i = 0; i < fetcherInfo.fetchers.length; ++i) {
                    if (fetcherInfo.fetchers[i] == null) {
                        final ImageFetcher imageFetcher = new ImageFetcher(threadGroup2, i, null);
                        try {
                            imageFetcher.start();
                            fetcherInfo.fetchers[i] = imageFetcher;
                            final FetcherInfo val$info = fetcherInfo;
                            ++val$info.numFetchers;
                            break;
                        }
                        catch (final Error error) {}
                    }
                }
                return null;
            }
        });
    }
}
