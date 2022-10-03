package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.awt.RenderingHints;

public interface RenderedImageFactory
{
    RenderedImage create(final ParameterBlock p0, final RenderingHints p1);
}
