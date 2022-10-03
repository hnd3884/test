package sun.font;

import java.util.Iterator;
import java.util.Map;
import java.security.AccessController;
import sun.misc.ThreadGroupUtils;
import java.util.HashMap;
import java.io.OutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;
import sun.awt.AppContext;
import java.util.concurrent.Semaphore;

public class CreatedFontTracker
{
    public static final int MAX_FILE_SIZE = 33554432;
    public static final int MAX_TOTAL_BYTES = 335544320;
    static CreatedFontTracker tracker;
    int numBytes;
    
    public static synchronized CreatedFontTracker getTracker() {
        if (CreatedFontTracker.tracker == null) {
            CreatedFontTracker.tracker = new CreatedFontTracker();
        }
        return CreatedFontTracker.tracker;
    }
    
    private CreatedFontTracker() {
        this.numBytes = 0;
    }
    
    public synchronized int getNumBytes() {
        return this.numBytes;
    }
    
    public synchronized void addBytes(final int n) {
        this.numBytes += n;
    }
    
    public synchronized void subBytes(final int n) {
        this.numBytes -= n;
    }
    
    private static synchronized Semaphore getCS() {
        final AppContext appContext = AppContext.getAppContext();
        Semaphore semaphore = (Semaphore)appContext.get(CreatedFontTracker.class);
        if (semaphore == null) {
            semaphore = new Semaphore(5, true);
            appContext.put(CreatedFontTracker.class, semaphore);
        }
        return semaphore;
    }
    
    public boolean acquirePermit() throws InterruptedException {
        return getCS().tryAcquire(120L, TimeUnit.SECONDS);
    }
    
    public void releasePermit() {
        getCS().release();
    }
    
    public void add(final File file) {
        TempFileDeletionHook.add(file);
    }
    
    public void set(final File file, final OutputStream outputStream) {
        TempFileDeletionHook.set(file, outputStream);
    }
    
    public void remove(final File file) {
        TempFileDeletionHook.remove(file);
    }
    
    private static class TempFileDeletionHook
    {
        private static HashMap<File, OutputStream> files;
        private static Thread t;
        
        static void init() {
            if (TempFileDeletionHook.t == null) {
                AccessController.doPrivileged(() -> {
                    (TempFileDeletionHook.t = new Thread(ThreadGroupUtils.getRootThreadGroup(), TempFileDeletionHook::runHooks)).setContextClassLoader(null);
                    Runtime.getRuntime().addShutdownHook(TempFileDeletionHook.t);
                    return null;
                });
            }
        }
        
        static synchronized void add(final File file) {
            init();
            TempFileDeletionHook.files.put(file, null);
        }
        
        static synchronized void set(final File file, final OutputStream outputStream) {
            TempFileDeletionHook.files.put(file, outputStream);
        }
        
        static synchronized void remove(final File file) {
            TempFileDeletionHook.files.remove(file);
        }
        
        static synchronized void runHooks() {
            if (TempFileDeletionHook.files.isEmpty()) {
                return;
            }
            for (final Map.Entry entry : TempFileDeletionHook.files.entrySet()) {
                try {
                    if (entry.getValue() != null) {
                        ((OutputStream)entry.getValue()).close();
                    }
                }
                catch (final Exception ex) {}
                ((File)entry.getKey()).delete();
            }
        }
        
        static {
            TempFileDeletionHook.files = new HashMap<File, OutputStream>();
            TempFileDeletionHook.t = null;
        }
    }
}
