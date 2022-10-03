package sun.font;

import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.lang.ref.Reference;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.io.File;
import java.nio.ByteBuffer;
import java.awt.FontFormatException;

public abstract class FileFont extends PhysicalFont
{
    protected boolean useJavaRasterizer;
    protected int fileSize;
    protected FontScaler scaler;
    protected boolean checkedNatives;
    protected boolean useNatives;
    protected NativeFont[] nativeFonts;
    protected char[] glyphToCharMap;
    
    FileFont(final String s, final Object o) throws FontFormatException {
        super(s, o);
        this.useJavaRasterizer = true;
    }
    
    @Override
    FontStrike createStrike(final FontStrikeDesc fontStrikeDesc) {
        if (!this.checkedNatives) {
            this.checkUseNatives();
        }
        return new FileFontStrike(this, fontStrikeDesc);
    }
    
    protected boolean checkUseNatives() {
        this.checkedNatives = true;
        return this.useNatives;
    }
    
    protected abstract void close();
    
    abstract ByteBuffer readBlock(final int p0, final int p1);
    
    @Override
    public boolean canDoStyle(final int n) {
        return true;
    }
    
    void setFileToRemove(final File file, final CreatedFontTracker createdFontTracker) {
        Disposer.addObjectRecord(this, new CreatedFontFileDisposerRecord(file, createdFontTracker));
    }
    
    static void setFileToRemove(final Object o, final File file, final CreatedFontTracker createdFontTracker) {
        Disposer.addObjectRecord(o, new CreatedFontFileDisposerRecord(file, createdFontTracker));
    }
    
    synchronized void deregisterFontAndClearStrikeCache() {
        SunFontManager.getInstance().deRegisterBadFont(this);
        for (final Reference reference : this.strikeCache.values()) {
            if (reference != null) {
                final FileFontStrike fileFontStrike = (FileFontStrike)reference.get();
                if (fileFontStrike == null || fileFontStrike.pScalerContext == 0L) {
                    continue;
                }
                this.scaler.invalidateScalerContext(fileFontStrike.pScalerContext);
            }
        }
        if (this.scaler != null) {
            this.scaler.disposeScaler();
        }
        this.scaler = FontScaler.getNullScaler();
    }
    
    @Override
    StrikeMetrics getFontMetrics(final long n) {
        try {
            return this.getScaler().getFontMetrics(n);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getFontMetrics(n);
        }
    }
    
    @Override
    float getGlyphAdvance(final long n, final int n2) {
        try {
            return this.getScaler().getGlyphAdvance(n, n2);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphAdvance(n, n2);
        }
    }
    
    @Override
    void getGlyphMetrics(final long n, final int n2, final Point2D.Float float1) {
        try {
            this.getScaler().getGlyphMetrics(n, n2, float1);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            this.getGlyphMetrics(n, n2, float1);
        }
    }
    
    @Override
    long getGlyphImage(final long n, final int n2) {
        try {
            return this.getScaler().getGlyphImage(n, n2);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphImage(n, n2);
        }
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final long n, final int n2) {
        try {
            return this.getScaler().getGlyphOutlineBounds(n, n2);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphOutlineBounds(n, n2);
        }
    }
    
    @Override
    GeneralPath getGlyphOutline(final long n, final int n2, final float n3, final float n4) {
        try {
            return this.getScaler().getGlyphOutline(n, n2, n3, n4);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphOutline(n, n2, n3, n4);
        }
    }
    
    @Override
    GeneralPath getGlyphVectorOutline(final long n, final int[] array, final int n2, final float n3, final float n4) {
        try {
            return this.getScaler().getGlyphVectorOutline(n, array, n2, n3, n4);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphVectorOutline(n, array, n2, n3, n4);
        }
    }
    
    protected abstract FontScaler getScaler();
    
    @Override
    protected long getUnitsPerEm() {
        return this.getScaler().getUnitsPerEm();
    }
    
    protected String getPublicFileName() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return this.platName;
        }
        boolean b = true;
        try {
            securityManager.checkPropertyAccess("java.io.tmpdir");
        }
        catch (final SecurityException ex) {
            b = false;
        }
        if (b) {
            return this.platName;
        }
        final File file = new File(this.platName);
        final Boolean false = Boolean.FALSE;
        Boolean true;
        try {
            true = AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() {
                    final File file = new File(System.getProperty("java.io.tmpdir"));
                    try {
                        final String canonicalPath = file.getCanonicalPath();
                        final String canonicalPath2 = file.getCanonicalPath();
                        return canonicalPath2 == null || canonicalPath2.startsWith(canonicalPath);
                    }
                    catch (final IOException ex) {
                        return Boolean.TRUE;
                    }
                }
            });
        }
        catch (final PrivilegedActionException ex2) {
            true = Boolean.TRUE;
        }
        return true ? "temp file" : this.platName;
    }
    
    private static class CreatedFontFileDisposerRecord implements DisposerRecord
    {
        File fontFile;
        CreatedFontTracker tracker;
        
        private CreatedFontFileDisposerRecord(final File fontFile, final CreatedFontTracker tracker) {
            this.fontFile = null;
            this.fontFile = fontFile;
            this.tracker = tracker;
        }
        
        @Override
        public void dispose() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    if (CreatedFontFileDisposerRecord.this.fontFile != null) {
                        try {
                            if (CreatedFontFileDisposerRecord.this.tracker != null) {
                                CreatedFontFileDisposerRecord.this.tracker.subBytes((int)CreatedFontFileDisposerRecord.this.fontFile.length());
                            }
                            CreatedFontFileDisposerRecord.this.fontFile.delete();
                            SunFontManager.getInstance().tmpFontFiles.remove(CreatedFontFileDisposerRecord.this.fontFile);
                        }
                        catch (final Exception ex) {}
                    }
                    return null;
                }
            });
        }
    }
}
