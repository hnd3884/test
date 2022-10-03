package sun.font;

import sun.java2d.DisposerRecord;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.Iterator;
import sun.java2d.pipe.BufferedContext;
import java.awt.GraphicsConfiguration;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import java.awt.GraphicsEnvironment;
import sun.java2d.Disposer;
import java.util.ArrayList;
import java.lang.ref.ReferenceQueue;
import sun.misc.Unsafe;

public final class StrikeCache
{
    static final Unsafe unsafe;
    static ReferenceQueue refQueue;
    static ArrayList<GlyphDisposedListener> disposeListeners;
    static int MINSTRIKES;
    static int recentStrikeIndex;
    static FontStrike[] recentStrikes;
    static boolean cacheRefTypeWeak;
    static int nativeAddressSize;
    static int glyphInfoSize;
    static int xAdvanceOffset;
    static int yAdvanceOffset;
    static int boundsOffset;
    static int widthOffset;
    static int heightOffset;
    static int rowBytesOffset;
    static int topLeftXOffset;
    static int topLeftYOffset;
    static int pixelDataOffset;
    static int cacheCellOffset;
    static int managedOffset;
    static long invisibleGlyphPtr;
    
    static native void getGlyphCacheDescription(final long[] p0);
    
    static void refStrike(final FontStrike fontStrike) {
        int recentStrikeIndex = StrikeCache.recentStrikeIndex;
        StrikeCache.recentStrikes[recentStrikeIndex] = fontStrike;
        if (++recentStrikeIndex == StrikeCache.MINSTRIKES) {
            recentStrikeIndex = 0;
        }
        StrikeCache.recentStrikeIndex = recentStrikeIndex;
    }
    
    private static final void doDispose(final FontStrikeDisposer fontStrikeDisposer) {
        if (fontStrikeDisposer.intGlyphImages != null) {
            freeCachedIntMemory(fontStrikeDisposer.intGlyphImages, fontStrikeDisposer.pScalerContext);
        }
        else if (fontStrikeDisposer.longGlyphImages != null) {
            freeCachedLongMemory(fontStrikeDisposer.longGlyphImages, fontStrikeDisposer.pScalerContext);
        }
        else if (fontStrikeDisposer.segIntGlyphImages != null) {
            for (int i = 0; i < fontStrikeDisposer.segIntGlyphImages.length; ++i) {
                if (fontStrikeDisposer.segIntGlyphImages[i] != null) {
                    freeCachedIntMemory(fontStrikeDisposer.segIntGlyphImages[i], fontStrikeDisposer.pScalerContext);
                    fontStrikeDisposer.pScalerContext = 0L;
                    fontStrikeDisposer.segIntGlyphImages[i] = null;
                }
            }
            if (fontStrikeDisposer.pScalerContext != 0L) {
                freeCachedIntMemory(new int[0], fontStrikeDisposer.pScalerContext);
            }
        }
        else if (fontStrikeDisposer.segLongGlyphImages != null) {
            for (int j = 0; j < fontStrikeDisposer.segLongGlyphImages.length; ++j) {
                if (fontStrikeDisposer.segLongGlyphImages[j] != null) {
                    freeCachedLongMemory(fontStrikeDisposer.segLongGlyphImages[j], fontStrikeDisposer.pScalerContext);
                    fontStrikeDisposer.pScalerContext = 0L;
                    fontStrikeDisposer.segLongGlyphImages[j] = null;
                }
            }
            if (fontStrikeDisposer.pScalerContext != 0L) {
                freeCachedLongMemory(new long[0], fontStrikeDisposer.pScalerContext);
            }
        }
        else if (fontStrikeDisposer.pScalerContext != 0L) {
            if (longAddresses()) {
                freeCachedLongMemory(new long[0], fontStrikeDisposer.pScalerContext);
            }
            else {
                freeCachedIntMemory(new int[0], fontStrikeDisposer.pScalerContext);
            }
        }
    }
    
    private static boolean longAddresses() {
        return StrikeCache.nativeAddressSize == 8;
    }
    
    static void disposeStrike(final FontStrikeDisposer fontStrikeDisposer) {
        if (Disposer.pollingQueue) {
            doDispose(fontStrikeDisposer);
            return;
        }
        RenderQueue renderQueue = null;
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!GraphicsEnvironment.isHeadless()) {
            final GraphicsConfiguration defaultConfiguration = localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
            if (defaultConfiguration instanceof AccelGraphicsConfig) {
                final BufferedContext context = ((AccelGraphicsConfig)defaultConfiguration).getContext();
                if (context != null) {
                    renderQueue = context.getRenderQueue();
                }
            }
        }
        if (renderQueue != null) {
            renderQueue.lock();
            try {
                renderQueue.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        doDispose(fontStrikeDisposer);
                        Disposer.pollRemove();
                    }
                });
            }
            finally {
                renderQueue.unlock();
            }
        }
        else {
            doDispose(fontStrikeDisposer);
        }
    }
    
    static native void freeIntPointer(final int p0);
    
    static native void freeLongPointer(final long p0);
    
    private static native void freeIntMemory(final int[] p0, final long p1);
    
    private static native void freeLongMemory(final long[] p0, final long p1);
    
    private static void freeCachedIntMemory(final int[] array, final long n) {
        synchronized (StrikeCache.disposeListeners) {
            if (StrikeCache.disposeListeners.size() > 0) {
                ArrayList<Long> list = null;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] != 0 && StrikeCache.unsafe.getByte(array[i] + StrikeCache.managedOffset) == 0) {
                        if (list == null) {
                            list = new ArrayList<Long>();
                        }
                        list.add((long)array[i]);
                    }
                }
                if (list != null) {
                    notifyDisposeListeners(list);
                }
            }
        }
        freeIntMemory(array, n);
    }
    
    private static void freeCachedLongMemory(final long[] array, final long n) {
        synchronized (StrikeCache.disposeListeners) {
            if (StrikeCache.disposeListeners.size() > 0) {
                ArrayList<Long> list = null;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] != 0L && StrikeCache.unsafe.getByte(array[i] + StrikeCache.managedOffset) == 0) {
                        if (list == null) {
                            list = new ArrayList<Long>();
                        }
                        list.add(array[i]);
                    }
                }
                if (list != null) {
                    notifyDisposeListeners(list);
                }
            }
        }
        freeLongMemory(array, n);
    }
    
    public static void addGlyphDisposedListener(final GlyphDisposedListener glyphDisposedListener) {
        synchronized (StrikeCache.disposeListeners) {
            StrikeCache.disposeListeners.add(glyphDisposedListener);
        }
    }
    
    private static void notifyDisposeListeners(final ArrayList<Long> list) {
        final Iterator<GlyphDisposedListener> iterator = StrikeCache.disposeListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().glyphDisposed(list);
        }
    }
    
    public static Reference getStrikeRef(final FontStrike fontStrike) {
        return getStrikeRef(fontStrike, StrikeCache.cacheRefTypeWeak);
    }
    
    public static Reference getStrikeRef(final FontStrike fontStrike, final boolean b) {
        if (fontStrike.disposer == null) {
            if (b) {
                return new WeakReference(fontStrike);
            }
            return new SoftReference(fontStrike);
        }
        else {
            if (b) {
                return new WeakDisposerRef(fontStrike);
            }
            return new SoftDisposerRef(fontStrike);
        }
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        StrikeCache.refQueue = Disposer.getQueue();
        StrikeCache.disposeListeners = new ArrayList<GlyphDisposedListener>(1);
        StrikeCache.MINSTRIKES = 8;
        StrikeCache.recentStrikeIndex = 0;
        final long[] array = new long[13];
        getGlyphCacheDescription(array);
        StrikeCache.nativeAddressSize = (int)array[0];
        StrikeCache.glyphInfoSize = (int)array[1];
        StrikeCache.xAdvanceOffset = (int)array[2];
        StrikeCache.yAdvanceOffset = (int)array[3];
        StrikeCache.widthOffset = (int)array[4];
        StrikeCache.heightOffset = (int)array[5];
        StrikeCache.rowBytesOffset = (int)array[6];
        StrikeCache.topLeftXOffset = (int)array[7];
        StrikeCache.topLeftYOffset = (int)array[8];
        StrikeCache.pixelDataOffset = (int)array[9];
        StrikeCache.invisibleGlyphPtr = array[10];
        StrikeCache.cacheCellOffset = (int)array[11];
        StrikeCache.managedOffset = (int)array[12];
        if (StrikeCache.nativeAddressSize < 4) {
            throw new InternalError("Unexpected address size for font data: " + StrikeCache.nativeAddressSize);
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                StrikeCache.cacheRefTypeWeak = System.getProperty("sun.java2d.font.reftype", "soft").equals("weak");
                final String property = System.getProperty("sun.java2d.font.minstrikes");
                if (property != null) {
                    try {
                        StrikeCache.MINSTRIKES = Integer.parseInt(property);
                        if (StrikeCache.MINSTRIKES <= 0) {
                            StrikeCache.MINSTRIKES = 1;
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
                StrikeCache.recentStrikes = new FontStrike[StrikeCache.MINSTRIKES];
                return null;
            }
        });
    }
    
    static class SoftDisposerRef extends SoftReference implements DisposableStrike
    {
        private FontStrikeDisposer disposer;
        
        @Override
        public FontStrikeDisposer getDisposer() {
            return this.disposer;
        }
        
        SoftDisposerRef(final FontStrike fontStrike) {
            super(fontStrike, StrikeCache.refQueue);
            Disposer.addReference(this, this.disposer = fontStrike.disposer);
        }
    }
    
    static class WeakDisposerRef extends WeakReference implements DisposableStrike
    {
        private FontStrikeDisposer disposer;
        
        @Override
        public FontStrikeDisposer getDisposer() {
            return this.disposer;
        }
        
        WeakDisposerRef(final FontStrike fontStrike) {
            super(fontStrike, StrikeCache.refQueue);
            Disposer.addReference(this, this.disposer = fontStrike.disposer);
        }
    }
    
    interface DisposableStrike
    {
        FontStrikeDisposer getDisposer();
    }
}
