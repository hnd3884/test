package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.util.Vector;

public interface RenderableImage
{
    public static final String HINTS_OBSERVED = "HINTS_OBSERVED";
    
    Vector<RenderableImage> getSources();
    
    Object getProperty(final String p0);
    
    String[] getPropertyNames();
    
    boolean isDynamic();
    
    float getWidth();
    
    float getHeight();
    
    float getMinX();
    
    float getMinY();
    
    RenderedImage createScaledRendering(final int p0, final int p1, final RenderingHints p2);
    
    RenderedImage createDefaultRendering();
    
    RenderedImage createRendering(final RenderContext p0);
}
