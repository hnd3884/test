package sun.java2d.pipe.hw;

import java.awt.ImageCapabilities;
import java.awt.BufferCapabilities;

public class ExtendedBufferCapabilities extends BufferCapabilities
{
    private VSyncType vsync;
    
    public ExtendedBufferCapabilities(final BufferCapabilities bufferCapabilities) {
        super(bufferCapabilities.getFrontBufferCapabilities(), bufferCapabilities.getBackBufferCapabilities(), bufferCapabilities.getFlipContents());
        this.vsync = VSyncType.VSYNC_DEFAULT;
    }
    
    public ExtendedBufferCapabilities(final ImageCapabilities imageCapabilities, final ImageCapabilities imageCapabilities2, final FlipContents flipContents) {
        super(imageCapabilities, imageCapabilities2, flipContents);
        this.vsync = VSyncType.VSYNC_DEFAULT;
    }
    
    public ExtendedBufferCapabilities(final ImageCapabilities imageCapabilities, final ImageCapabilities imageCapabilities2, final FlipContents flipContents, final VSyncType vsync) {
        super(imageCapabilities, imageCapabilities2, flipContents);
        this.vsync = vsync;
    }
    
    public ExtendedBufferCapabilities(final BufferCapabilities bufferCapabilities, final VSyncType vsync) {
        super(bufferCapabilities.getFrontBufferCapabilities(), bufferCapabilities.getBackBufferCapabilities(), bufferCapabilities.getFlipContents());
        this.vsync = vsync;
    }
    
    public ExtendedBufferCapabilities derive(final VSyncType vSyncType) {
        return new ExtendedBufferCapabilities(this, vSyncType);
    }
    
    public VSyncType getVSync() {
        return this.vsync;
    }
    
    @Override
    public final boolean isPageFlipping() {
        return true;
    }
    
    public enum VSyncType
    {
        VSYNC_DEFAULT(0), 
        VSYNC_ON(1), 
        VSYNC_OFF(2);
        
        private int id;
        
        public int id() {
            return this.id;
        }
        
        private VSyncType(final int id) {
            this.id = id;
        }
    }
}
