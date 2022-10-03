package sun.java2d.opengl;

import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.BufferedContext;

public class OGLContext extends BufferedContext
{
    private final OGLGraphicsConfig config;
    
    OGLContext(final RenderQueue renderQueue, final OGLGraphicsConfig config) {
        super(renderQueue);
        this.config = config;
    }
    
    static void setScratchSurface(final OGLGraphicsConfig oglGraphicsConfig) {
        setScratchSurface(oglGraphicsConfig.getNativeConfigInfo());
    }
    
    static void setScratchSurface(final long n) {
        OGLContext.currentContext = null;
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        final RenderBuffer buffer = instance.getBuffer();
        instance.ensureCapacityAndAlignment(12, 4);
        buffer.putInt(71);
        buffer.putLong(n);
    }
    
    static void invalidateCurrentContext() {
        if (OGLContext.currentContext != null) {
            OGLContext.currentContext.invalidateContext();
            OGLContext.currentContext = null;
        }
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.ensureCapacity(4);
        instance.getBuffer().putInt(75);
        instance.flushNow();
    }
    
    @Override
    public RenderQueue getRenderQueue() {
        return OGLRenderQueue.getInstance();
    }
    
    static final native String getOGLIdString();
    
    @Override
    public void saveState() {
        this.invalidateContext();
        invalidateCurrentContext();
        setScratchSurface(this.config);
        this.rq.ensureCapacity(4);
        this.buf.putInt(78);
        this.rq.flushNow();
    }
    
    @Override
    public void restoreState() {
        this.invalidateContext();
        invalidateCurrentContext();
        setScratchSurface(this.config);
        this.rq.ensureCapacity(4);
        this.buf.putInt(79);
        this.rq.flushNow();
    }
    
    static class OGLContextCaps extends ContextCapabilities
    {
        static final int CAPS_EXT_FBOBJECT = 12;
        static final int CAPS_DOUBLEBUFFERED = 65536;
        static final int CAPS_EXT_LCD_SHADER = 131072;
        static final int CAPS_EXT_BIOP_SHADER = 262144;
        static final int CAPS_EXT_GRAD_SHADER = 524288;
        static final int CAPS_EXT_TEXRECT = 1048576;
        static final int CAPS_EXT_TEXBARRIER = 2097152;
        
        OGLContextCaps(final int n, final String s) {
            super(n, s);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(super.toString());
            if ((this.caps & 0xC) != 0x0) {
                sb.append("CAPS_EXT_FBOBJECT|");
            }
            if ((this.caps & 0x10000) != 0x0) {
                sb.append("CAPS_DOUBLEBUFFERED|");
            }
            if ((this.caps & 0x20000) != 0x0) {
                sb.append("CAPS_EXT_LCD_SHADER|");
            }
            if ((this.caps & 0x40000) != 0x0) {
                sb.append("CAPS_BIOP_SHADER|");
            }
            if ((this.caps & 0x80000) != 0x0) {
                sb.append("CAPS_EXT_GRAD_SHADER|");
            }
            if ((this.caps & 0x100000) != 0x0) {
                sb.append("CAPS_EXT_TEXRECT|");
            }
            if ((this.caps & 0x200000) != 0x0) {
                sb.append("CAPS_EXT_TEXBARRIER|");
            }
            return sb.toString();
        }
    }
}
