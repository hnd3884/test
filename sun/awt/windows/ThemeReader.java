package sun.awt.windows;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.Insets;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.Map;

public final class ThemeReader
{
    private static final Map<String, Long> widgetToTheme;
    private static final ReadWriteLock readWriteLock;
    private static final Lock readLock;
    private static final Lock writeLock;
    private static volatile boolean valid;
    private static volatile boolean isThemed;
    static volatile boolean xpStyleEnabled;
    
    static void flush() {
        ThemeReader.valid = false;
    }
    
    private static native boolean initThemes();
    
    public static boolean isThemed() {
        ThemeReader.writeLock.lock();
        try {
            return ThemeReader.isThemed = initThemes();
        }
        finally {
            ThemeReader.writeLock.unlock();
        }
    }
    
    public static boolean isXPStyleEnabled() {
        return ThemeReader.xpStyleEnabled;
    }
    
    private static Long getThemeImpl(final String s) {
        Long n = ThemeReader.widgetToTheme.get(s);
        if (n == null) {
            final int index = s.indexOf("::");
            if (index > 0) {
                setWindowTheme(s.substring(0, index));
                n = openTheme(s.substring(index + 2));
                setWindowTheme(null);
            }
            else {
                n = openTheme(s);
            }
            ThemeReader.widgetToTheme.put(s, n);
        }
        return n;
    }
    
    private static Long getTheme(final String s) {
        if (!ThemeReader.isThemed) {
            throw new IllegalStateException("Themes are not loaded");
        }
        if (!ThemeReader.valid) {
            ThemeReader.readLock.unlock();
            ThemeReader.writeLock.lock();
            try {
                if (!ThemeReader.valid) {
                    final Iterator<Long> iterator = ThemeReader.widgetToTheme.values().iterator();
                    while (iterator.hasNext()) {
                        closeTheme(iterator.next());
                    }
                    ThemeReader.widgetToTheme.clear();
                    ThemeReader.valid = true;
                }
            }
            finally {
                ThemeReader.readLock.lock();
                ThemeReader.writeLock.unlock();
            }
        }
        Long themeImpl = ThemeReader.widgetToTheme.get(s);
        if (themeImpl == null) {
            ThemeReader.readLock.unlock();
            ThemeReader.writeLock.lock();
            try {
                themeImpl = getThemeImpl(s);
            }
            finally {
                ThemeReader.readLock.lock();
                ThemeReader.writeLock.unlock();
            }
        }
        return themeImpl;
    }
    
    private static native void paintBackground(final int[] p0, final long p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8);
    
    public static void paintBackground(final int[] array, final String s, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        ThemeReader.readLock.lock();
        try {
            paintBackground(array, getTheme(s), n, n2, n3, n4, n5, n6, n7);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native Insets getThemeMargins(final long p0, final int p1, final int p2, final int p3);
    
    public static Insets getThemeMargins(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getThemeMargins(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native boolean isThemePartDefined(final long p0, final int p1, final int p2);
    
    public static boolean isThemePartDefined(final String s, final int n, final int n2) {
        ThemeReader.readLock.lock();
        try {
            return isThemePartDefined(getTheme(s), n, n2);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native Color getColor(final long p0, final int p1, final int p2, final int p3);
    
    public static Color getColor(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getColor(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native int getInt(final long p0, final int p1, final int p2, final int p3);
    
    public static int getInt(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getInt(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native int getEnum(final long p0, final int p1, final int p2, final int p3);
    
    public static int getEnum(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getEnum(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native boolean getBoolean(final long p0, final int p1, final int p2, final int p3);
    
    public static boolean getBoolean(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getBoolean(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native boolean getSysBoolean(final long p0, final int p1);
    
    public static boolean getSysBoolean(final String s, final int n) {
        ThemeReader.readLock.lock();
        try {
            return getSysBoolean(getTheme(s), n);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native Point getPoint(final long p0, final int p1, final int p2, final int p3);
    
    public static Point getPoint(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getPoint(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native Dimension getPosition(final long p0, final int p1, final int p2, final int p3);
    
    public static Dimension getPosition(final String s, final int n, final int n2, final int n3) {
        ThemeReader.readLock.lock();
        try {
            return getPosition(getTheme(s), n, n2, n3);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native Dimension getPartSize(final long p0, final int p1, final int p2);
    
    public static Dimension getPartSize(final String s, final int n, final int n2) {
        ThemeReader.readLock.lock();
        try {
            return getPartSize(getTheme(s), n, n2);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    private static native long openTheme(final String p0);
    
    private static native void closeTheme(final long p0);
    
    private static native void setWindowTheme(final String p0);
    
    private static native long getThemeTransitionDuration(final long p0, final int p1, final int p2, final int p3, final int p4);
    
    public static long getThemeTransitionDuration(final String s, final int n, final int n2, final int n3, final int n4) {
        ThemeReader.readLock.lock();
        try {
            return getThemeTransitionDuration(getTheme(s), n, n2, n3, n4);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    public static native boolean isGetThemeTransitionDurationDefined();
    
    private static native Insets getThemeBackgroundContentMargins(final long p0, final int p1, final int p2, final int p3, final int p4);
    
    public static Insets getThemeBackgroundContentMargins(final String s, final int n, final int n2, final int n3, final int n4) {
        ThemeReader.readLock.lock();
        try {
            return getThemeBackgroundContentMargins(getTheme(s), n, n2, n3, n4);
        }
        finally {
            ThemeReader.readLock.unlock();
        }
    }
    
    static {
        widgetToTheme = new HashMap<String, Long>();
        readWriteLock = new ReentrantReadWriteLock();
        readLock = ThemeReader.readWriteLock.readLock();
        writeLock = ThemeReader.readWriteLock.writeLock();
        ThemeReader.valid = false;
    }
}
