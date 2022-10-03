package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class BufferedImageDevice extends GraphicsDevice
{
    GraphicsConfiguration gc;
    
    public BufferedImageDevice(final BufferedImageGraphicsConfig gc) {
        this.gc = gc;
    }
    
    @Override
    public int getType() {
        return 2;
    }
    
    @Override
    public String getIDstring() {
        return "BufferedImage";
    }
    
    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[] { this.gc };
    }
    
    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return this.gc;
    }
}
