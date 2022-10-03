package sun.java2d.d3d;

import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.BufferedContext;

class D3DContext extends BufferedContext
{
    private final D3DGraphicsDevice device;
    
    D3DContext(final RenderQueue renderQueue, final D3DGraphicsDevice device) {
        super(renderQueue);
        this.device = device;
    }
    
    static void invalidateCurrentContext() {
        if (D3DContext.currentContext != null) {
            D3DContext.currentContext.invalidateContext();
            D3DContext.currentContext = null;
        }
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.ensureCapacity(4);
        instance.getBuffer().putInt(75);
        instance.flushNow();
    }
    
    static void setScratchSurface(final D3DContext d3DContext) {
        if (d3DContext != D3DContext.currentContext) {
            D3DContext.currentContext = null;
        }
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        final RenderBuffer buffer = instance.getBuffer();
        instance.ensureCapacity(8);
        buffer.putInt(71);
        buffer.putInt(d3DContext.getDevice().getScreen());
    }
    
    @Override
    public RenderQueue getRenderQueue() {
        return D3DRenderQueue.getInstance();
    }
    
    @Override
    public void saveState() {
        this.invalidateContext();
        invalidateCurrentContext();
        setScratchSurface(this);
        this.rq.ensureCapacity(4);
        this.buf.putInt(78);
        this.rq.flushNow();
    }
    
    @Override
    public void restoreState() {
        this.invalidateContext();
        invalidateCurrentContext();
        setScratchSurface(this);
        this.rq.ensureCapacity(4);
        this.buf.putInt(79);
        this.rq.flushNow();
    }
    
    D3DGraphicsDevice getDevice() {
        return this.device;
    }
    
    static class D3DContextCaps extends ContextCapabilities
    {
        static final int CAPS_LCD_SHADER = 65536;
        static final int CAPS_BIOP_SHADER = 131072;
        static final int CAPS_DEVICE_OK = 262144;
        static final int CAPS_AA_SHADER = 524288;
        
        D3DContextCaps(final int n, final String s) {
            super(n, s);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(super.toString());
            if ((this.caps & 0x10000) != 0x0) {
                sb.append("CAPS_LCD_SHADER|");
            }
            if ((this.caps & 0x20000) != 0x0) {
                sb.append("CAPS_BIOP_SHADER|");
            }
            if ((this.caps & 0x80000) != 0x0) {
                sb.append("CAPS_AA_SHADER|");
            }
            if ((this.caps & 0x40000) != 0x0) {
                sb.append("CAPS_DEVICE_OK|");
            }
            return sb.toString();
        }
    }
}
