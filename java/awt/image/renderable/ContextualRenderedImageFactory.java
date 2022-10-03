package java.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

public interface ContextualRenderedImageFactory extends RenderedImageFactory
{
    RenderContext mapRenderContext(final int p0, final RenderContext p1, final ParameterBlock p2, final RenderableImage p3);
    
    RenderedImage create(final RenderContext p0, final ParameterBlock p1);
    
    Rectangle2D getBounds2D(final ParameterBlock p0);
    
    Object getProperty(final ParameterBlock p0, final String p1);
    
    String[] getPropertyNames();
    
    boolean isDynamic();
}
