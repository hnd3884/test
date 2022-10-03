package sun.java2d;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.java2d.loops.BlitBg;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import java.awt.AlphaComposite;
import sun.java2d.loops.Blit;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import sun.java2d.loops.CompositeType;
import sun.awt.image.SurfaceManager;
import sun.awt.DisplayChangedListener;

public abstract class SurfaceDataProxy implements DisplayChangedListener, SurfaceManager.FlushableCacheData
{
    private static boolean cachingAllowed;
    private static int defaultThreshold;
    public static SurfaceDataProxy UNCACHED;
    private int threshold;
    private StateTracker srcTracker;
    private int numtries;
    private SurfaceData cachedSD;
    private StateTracker cacheTracker;
    private boolean valid;
    
    public static boolean isCachingAllowed() {
        return SurfaceDataProxy.cachingAllowed;
    }
    
    public abstract boolean isSupportedOperation(final SurfaceData p0, final int p1, final CompositeType p2, final Color p3);
    
    public abstract SurfaceData validateSurfaceData(final SurfaceData p0, final SurfaceData p1, final int p2, final int p3);
    
    public StateTracker getRetryTracker(final SurfaceData surfaceData) {
        return new CountdownTracker(this.threshold);
    }
    
    public SurfaceDataProxy() {
        this(SurfaceDataProxy.defaultThreshold);
    }
    
    public SurfaceDataProxy(final int threshold) {
        this.threshold = threshold;
        this.srcTracker = StateTracker.NEVER_CURRENT;
        this.cacheTracker = StateTracker.NEVER_CURRENT;
        this.valid = true;
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public void invalidate() {
        this.valid = false;
    }
    
    @Override
    public boolean flush(final boolean b) {
        if (b) {
            this.invalidate();
        }
        this.flush();
        return !this.isValid();
    }
    
    public synchronized void flush() {
        final SurfaceData cachedSD = this.cachedSD;
        this.cachedSD = null;
        this.cacheTracker = StateTracker.NEVER_CURRENT;
        if (cachedSD != null) {
            cachedSD.flush();
        }
    }
    
    public boolean isAccelerated() {
        return this.isValid() && this.srcTracker.isCurrent() && this.cacheTracker.isCurrent();
    }
    
    protected void activateDisplayListener() {
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (localGraphicsEnvironment instanceof SunGraphicsEnvironment) {
            ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(this);
        }
    }
    
    @Override
    public void displayChanged() {
        this.flush();
    }
    
    @Override
    public void paletteChanged() {
        this.srcTracker = StateTracker.NEVER_CURRENT;
    }
    
    public SurfaceData replaceData(final SurfaceData surfaceData, final int n, final CompositeType compositeType, final Color color) {
        if (this.isSupportedOperation(surfaceData, n, compositeType, color)) {
            if (!this.srcTracker.isCurrent()) {
                synchronized (this) {
                    this.numtries = this.threshold;
                    this.srcTracker = surfaceData.getStateTracker();
                    this.cacheTracker = StateTracker.NEVER_CURRENT;
                }
                if (!this.srcTracker.isCurrent()) {
                    if (surfaceData.getState() == StateTrackable.State.UNTRACKABLE) {
                        this.invalidate();
                        this.flush();
                    }
                    return surfaceData;
                }
            }
            SurfaceData cachedSD = this.cachedSD;
            if (!this.cacheTracker.isCurrent()) {
                synchronized (this) {
                    if (this.numtries > 0) {
                        --this.numtries;
                        return surfaceData;
                    }
                }
                final Rectangle bounds = surfaceData.getBounds();
                final int width = bounds.width;
                final int height = bounds.height;
                final StateTracker srcTracker = this.srcTracker;
                cachedSD = this.validateSurfaceData(surfaceData, cachedSD, width, height);
                if (cachedSD == null) {
                    synchronized (this) {
                        if (srcTracker == this.srcTracker) {
                            this.cacheTracker = this.getRetryTracker(surfaceData);
                            this.cachedSD = null;
                        }
                    }
                    return surfaceData;
                }
                this.updateSurfaceData(surfaceData, cachedSD, width, height);
                if (!cachedSD.isValid()) {
                    return surfaceData;
                }
                synchronized (this) {
                    if (srcTracker == this.srcTracker && srcTracker.isCurrent()) {
                        this.cacheTracker = cachedSD.getStateTracker();
                        this.cachedSD = cachedSD;
                    }
                }
            }
            if (cachedSD != null) {
                return cachedSD;
            }
        }
        return surfaceData;
    }
    
    public void updateSurfaceData(final SurfaceData surfaceData, final SurfaceData surfaceData2, final int n, final int n2) {
        Blit.getFromCache(surfaceData.getSurfaceType(), CompositeType.SrcNoEa, surfaceData2.getSurfaceType()).Blit(surfaceData, surfaceData2, AlphaComposite.Src, null, 0, 0, 0, 0, n, n2);
        surfaceData2.markDirty();
    }
    
    public void updateSurfaceDataBg(final SurfaceData surfaceData, final SurfaceData surfaceData2, final int n, final int n2, final Color color) {
        BlitBg.getFromCache(surfaceData.getSurfaceType(), CompositeType.SrcNoEa, surfaceData2.getSurfaceType()).BlitBg(surfaceData, surfaceData2, AlphaComposite.Src, null, color.getRGB(), 0, 0, 0, 0, n, n2);
        surfaceData2.markDirty();
    }
    
    static {
        SurfaceDataProxy.cachingAllowed = true;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.managedimages"));
        if (s != null && s.equals("false")) {
            SurfaceDataProxy.cachingAllowed = false;
            System.out.println("Disabling managed images");
        }
        SurfaceDataProxy.defaultThreshold = 1;
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.accthreshold"));
        if (s2 != null) {
            try {
                final int int1 = Integer.parseInt(s2);
                if (int1 >= 0) {
                    SurfaceDataProxy.defaultThreshold = int1;
                    System.out.println("New Default Acceleration Threshold: " + SurfaceDataProxy.defaultThreshold);
                }
            }
            catch (final NumberFormatException ex) {
                System.err.println("Error setting new threshold:" + ex);
            }
        }
        SurfaceDataProxy.UNCACHED = new SurfaceDataProxy(0) {
            @Override
            public boolean isAccelerated() {
                return false;
            }
            
            @Override
            public boolean isSupportedOperation(final SurfaceData surfaceData, final int n, final CompositeType compositeType, final Color color) {
                return false;
            }
            
            @Override
            public SurfaceData validateSurfaceData(final SurfaceData surfaceData, final SurfaceData surfaceData2, final int n, final int n2) {
                throw new InternalError("UNCACHED should never validate SDs");
            }
            
            @Override
            public SurfaceData replaceData(final SurfaceData surfaceData, final int n, final CompositeType compositeType, final Color color) {
                return surfaceData;
            }
        };
    }
    
    public static class CountdownTracker implements StateTracker
    {
        private int countdown;
        
        public CountdownTracker(final int countdown) {
            this.countdown = countdown;
        }
        
        @Override
        public synchronized boolean isCurrent() {
            final int countdown = this.countdown - 1;
            this.countdown = countdown;
            return countdown >= 0;
        }
    }
}
