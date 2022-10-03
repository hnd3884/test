package sun.java2d;

import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.ThreadGroupUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.lang.ref.ReferenceQueue;

public class Disposer implements Runnable
{
    private static final ReferenceQueue queue;
    private static final Hashtable records;
    private static Disposer disposerInstance;
    public static final int WEAK = 0;
    public static final int PHANTOM = 1;
    public static int refType;
    private static ArrayList<DisposerRecord> deferredRecords;
    public static volatile boolean pollingQueue;
    
    public static void addRecord(final Object o, final long n, final long n2) {
        Disposer.disposerInstance.add(o, new DefaultDisposerRecord(n, n2));
    }
    
    public static void addRecord(final Object o, final DisposerRecord disposerRecord) {
        Disposer.disposerInstance.add(o, disposerRecord);
    }
    
    synchronized void add(Object disposerReferent, final DisposerRecord disposerRecord) {
        if (disposerReferent instanceof DisposerTarget) {
            disposerReferent = ((DisposerTarget)disposerReferent).getDisposerReferent();
        }
        Object o;
        if (Disposer.refType == 1) {
            o = new PhantomReference(disposerReferent, Disposer.queue);
        }
        else {
            o = new WeakReference(disposerReferent, Disposer.queue);
        }
        Disposer.records.put(o, disposerRecord);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    final Reference remove = Disposer.queue.remove();
                    remove.clear();
                    ((DisposerRecord)Disposer.records.remove(remove)).dispose();
                    clearDeferredRecords();
                }
            }
            catch (final Exception ex) {
                System.out.println("Exception while removing reference.");
                continue;
            }
            break;
        }
    }
    
    private static void clearDeferredRecords() {
        if (Disposer.deferredRecords == null || Disposer.deferredRecords.isEmpty()) {
            return;
        }
        for (int i = 0; i < Disposer.deferredRecords.size(); ++i) {
            try {
                Disposer.deferredRecords.get(i).dispose();
            }
            catch (final Exception ex) {
                System.out.println("Exception while disposing deferred rec.");
            }
        }
        Disposer.deferredRecords.clear();
    }
    
    public static void pollRemove() {
        if (Disposer.pollingQueue) {
            return;
        }
        Disposer.pollingQueue = true;
        int n = 0;
        int n2 = 0;
        try {
            Reference poll;
            while ((poll = Disposer.queue.poll()) != null && n < 10000 && n2 < 100) {
                ++n;
                poll.clear();
                final DisposerRecord disposerRecord = Disposer.records.remove(poll);
                if (disposerRecord instanceof PollDisposable) {
                    disposerRecord.dispose();
                }
                else {
                    if (disposerRecord == null) {
                        continue;
                    }
                    ++n2;
                    if (Disposer.deferredRecords == null) {
                        Disposer.deferredRecords = new ArrayList<DisposerRecord>(5);
                    }
                    Disposer.deferredRecords.add(disposerRecord);
                }
            }
        }
        catch (final Exception ex) {
            System.out.println("Exception while removing reference.");
        }
        finally {
            Disposer.pollingQueue = false;
        }
    }
    
    private static native void initIDs();
    
    public static void addReference(final Reference reference, final DisposerRecord disposerRecord) {
        Disposer.records.put(reference, disposerRecord);
    }
    
    public static void addObjectRecord(final Object o, final DisposerRecord disposerRecord) {
        Disposer.records.put(new WeakReference(o, Disposer.queue), disposerRecord);
    }
    
    public static ReferenceQueue getQueue() {
        return Disposer.queue;
    }
    
    static {
        queue = new ReferenceQueue();
        records = new Hashtable();
        Disposer.refType = 1;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("awt");
                return null;
            }
        });
        initIDs();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.reftype"));
        if (s != null) {
            if (s.equals("weak")) {
                Disposer.refType = 0;
                System.err.println("Using WEAK refs");
            }
            else {
                Disposer.refType = 1;
                System.err.println("Using PHANTOM refs");
            }
        }
        Disposer.disposerInstance = new Disposer();
        AccessController.doPrivileged(() -> {
            final Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), Disposer.disposerInstance, "Java2D Disposer");
            thread.setContextClassLoader(null);
            thread.setDaemon(true);
            thread.setPriority(10);
            thread.start();
            return null;
        });
        Disposer.deferredRecords = null;
        Disposer.pollingQueue = false;
    }
    
    public interface PollDisposable
    {
    }
}
