package sun.net;

import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;

public class ProgressMonitor
{
    private static ProgressMeteringPolicy meteringPolicy;
    private static ProgressMonitor pm;
    private ArrayList<ProgressSource> progressSourceList;
    private ArrayList<ProgressListener> progressListenerList;
    
    public ProgressMonitor() {
        this.progressSourceList = new ArrayList<ProgressSource>();
        this.progressListenerList = new ArrayList<ProgressListener>();
    }
    
    public static synchronized ProgressMonitor getDefault() {
        return ProgressMonitor.pm;
    }
    
    public static synchronized void setDefault(final ProgressMonitor pm) {
        if (pm != null) {
            ProgressMonitor.pm = pm;
        }
    }
    
    public static synchronized void setMeteringPolicy(final ProgressMeteringPolicy meteringPolicy) {
        if (meteringPolicy != null) {
            ProgressMonitor.meteringPolicy = meteringPolicy;
        }
    }
    
    public ArrayList<ProgressSource> getProgressSources() {
        final ArrayList list = new ArrayList();
        try {
            synchronized (this.progressSourceList) {
                final Iterator<ProgressSource> iterator = this.progressSourceList.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next().clone());
                }
            }
        }
        catch (final CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public synchronized int getProgressUpdateThreshold() {
        return ProgressMonitor.meteringPolicy.getProgressUpdateThreshold();
    }
    
    public boolean shouldMeterInput(final URL url, final String s) {
        return ProgressMonitor.meteringPolicy.shouldMeterInput(url, s);
    }
    
    public void registerSource(final ProgressSource progressSource) {
        synchronized (this.progressSourceList) {
            if (this.progressSourceList.contains(progressSource)) {
                return;
            }
            this.progressSourceList.add(progressSource);
        }
        if (this.progressListenerList.size() > 0) {
            final ArrayList list = new ArrayList();
            synchronized (this.progressListenerList) {
                final Iterator<ProgressListener> iterator = this.progressListenerList.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            }
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                ((ProgressListener)iterator2.next()).progressStart(new ProgressEvent(progressSource, progressSource.getURL(), progressSource.getMethod(), progressSource.getContentType(), progressSource.getState(), progressSource.getProgress(), progressSource.getExpected()));
            }
        }
    }
    
    public void unregisterSource(final ProgressSource progressSource) {
        synchronized (this.progressSourceList) {
            if (!this.progressSourceList.contains(progressSource)) {
                return;
            }
            progressSource.close();
            this.progressSourceList.remove(progressSource);
        }
        if (this.progressListenerList.size() > 0) {
            final ArrayList list = new ArrayList();
            synchronized (this.progressListenerList) {
                final Iterator<ProgressListener> iterator = this.progressListenerList.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            }
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                ((ProgressListener)iterator2.next()).progressFinish(new ProgressEvent(progressSource, progressSource.getURL(), progressSource.getMethod(), progressSource.getContentType(), progressSource.getState(), progressSource.getProgress(), progressSource.getExpected()));
            }
        }
    }
    
    public void updateProgress(final ProgressSource progressSource) {
        synchronized (this.progressSourceList) {
            if (!this.progressSourceList.contains(progressSource)) {
                return;
            }
        }
        if (this.progressListenerList.size() > 0) {
            final ArrayList list = new ArrayList();
            synchronized (this.progressListenerList) {
                final Iterator<ProgressListener> iterator = this.progressListenerList.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            }
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                ((ProgressListener)iterator2.next()).progressUpdate(new ProgressEvent(progressSource, progressSource.getURL(), progressSource.getMethod(), progressSource.getContentType(), progressSource.getState(), progressSource.getProgress(), progressSource.getExpected()));
            }
        }
    }
    
    public void addProgressListener(final ProgressListener progressListener) {
        synchronized (this.progressListenerList) {
            this.progressListenerList.add(progressListener);
        }
    }
    
    public void removeProgressListener(final ProgressListener progressListener) {
        synchronized (this.progressListenerList) {
            this.progressListenerList.remove(progressListener);
        }
    }
    
    static {
        ProgressMonitor.meteringPolicy = new DefaultProgressMeteringPolicy();
        ProgressMonitor.pm = new ProgressMonitor();
    }
}
