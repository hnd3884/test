package sun.print;

import java.awt.Window;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public final class PrinterGraphicsDevice extends GraphicsDevice
{
    String printerID;
    GraphicsConfiguration graphicsConf;
    
    protected PrinterGraphicsDevice(final GraphicsConfiguration graphicsConf, final String printerID) {
        this.printerID = printerID;
        this.graphicsConf = graphicsConf;
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    @Override
    public String getIDstring() {
        return this.printerID;
    }
    
    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[] { this.graphicsConf };
    }
    
    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return this.graphicsConf;
    }
    
    @Override
    public void setFullScreenWindow(final Window window) {
    }
    
    @Override
    public Window getFullScreenWindow() {
        return null;
    }
}
