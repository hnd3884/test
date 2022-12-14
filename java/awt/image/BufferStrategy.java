package java.awt.image;

import java.awt.Graphics;
import java.awt.BufferCapabilities;

public abstract class BufferStrategy
{
    public abstract BufferCapabilities getCapabilities();
    
    public abstract Graphics getDrawGraphics();
    
    public abstract boolean contentsLost();
    
    public abstract boolean contentsRestored();
    
    public abstract void show();
    
    public void dispose() {
    }
}
