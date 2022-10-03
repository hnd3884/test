package sun.awt;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.CharsetEncoder;

public class FontDescriptor implements Cloneable
{
    String nativeName;
    public CharsetEncoder encoder;
    String charsetName;
    private int[] exclusionRanges;
    public CharsetEncoder unicodeEncoder;
    boolean useUnicode;
    static boolean isLE;
    
    public FontDescriptor(final String nativeName, final CharsetEncoder encoder, final int[] exclusionRanges) {
        this.nativeName = nativeName;
        this.encoder = encoder;
        this.exclusionRanges = exclusionRanges;
        this.useUnicode = false;
        final Charset charset = encoder.charset();
        if (charset instanceof HistoricallyNamedCharset) {
            this.charsetName = ((HistoricallyNamedCharset)charset).historicalName();
        }
        else {
            this.charsetName = charset.name();
        }
    }
    
    public String getNativeName() {
        return this.nativeName;
    }
    
    public CharsetEncoder getFontCharsetEncoder() {
        return this.encoder;
    }
    
    public String getFontCharsetName() {
        return this.charsetName;
    }
    
    public int[] getExclusionRanges() {
        return this.exclusionRanges;
    }
    
    public boolean isExcluded(final char c) {
        int i = 0;
        while (i < this.exclusionRanges.length) {
            final int n = this.exclusionRanges[i++];
            final int n2 = this.exclusionRanges[i++];
            if (c >= n && c <= n2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return super.toString() + " [" + this.nativeName + "|" + this.encoder + "]";
    }
    
    private static native void initIDs();
    
    public boolean useUnicode() {
        if (this.useUnicode && this.unicodeEncoder == null) {
            try {
                this.unicodeEncoder = (FontDescriptor.isLE ? StandardCharsets.UTF_16LE.newEncoder() : StandardCharsets.UTF_16BE.newEncoder());
            }
            catch (final IllegalArgumentException ex) {}
        }
        return this.useUnicode;
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
        FontDescriptor.isLE = !"UnicodeBig".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.io.unicode.encoding", "UnicodeBig")));
    }
}
