package sun.java2d.pipe.hw;

public class ContextCapabilities
{
    public static final int CAPS_EMPTY = 0;
    public static final int CAPS_RT_PLAIN_ALPHA = 2;
    public static final int CAPS_RT_TEXTURE_ALPHA = 4;
    public static final int CAPS_RT_TEXTURE_OPAQUE = 8;
    public static final int CAPS_MULTITEXTURE = 16;
    public static final int CAPS_TEXNONPOW2 = 32;
    public static final int CAPS_TEXNONSQUARE = 64;
    public static final int CAPS_PS20 = 128;
    public static final int CAPS_PS30 = 256;
    protected static final int FIRST_PRIVATE_CAP = 65536;
    protected final int caps;
    protected final String adapterId;
    
    protected ContextCapabilities(final int caps, final String s) {
        this.caps = caps;
        this.adapterId = ((s != null) ? s : "unknown adapter");
    }
    
    public String getAdapterId() {
        return this.adapterId;
    }
    
    public int getCaps() {
        return this.caps;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContextCapabilities: adapter=" + this.adapterId + ", caps=");
        if (this.caps == 0) {
            sb.append("CAPS_EMPTY");
        }
        else {
            if ((this.caps & 0x2) != 0x0) {
                sb.append("CAPS_RT_PLAIN_ALPHA|");
            }
            if ((this.caps & 0x4) != 0x0) {
                sb.append("CAPS_RT_TEXTURE_ALPHA|");
            }
            if ((this.caps & 0x8) != 0x0) {
                sb.append("CAPS_RT_TEXTURE_OPAQUE|");
            }
            if ((this.caps & 0x10) != 0x0) {
                sb.append("CAPS_MULTITEXTURE|");
            }
            if ((this.caps & 0x20) != 0x0) {
                sb.append("CAPS_TEXNONPOW2|");
            }
            if ((this.caps & 0x40) != 0x0) {
                sb.append("CAPS_TEXNONSQUARE|");
            }
            if ((this.caps & 0x80) != 0x0) {
                sb.append("CAPS_PS20|");
            }
            if ((this.caps & 0x100) != 0x0) {
                sb.append("CAPS_PS30|");
            }
        }
        return sb.toString();
    }
}
