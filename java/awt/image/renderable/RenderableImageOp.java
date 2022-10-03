package java.awt.image.renderable;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.util.Vector;
import java.awt.geom.Rectangle2D;

public class RenderableImageOp implements RenderableImage
{
    ParameterBlock paramBlock;
    ContextualRenderedImageFactory myCRIF;
    Rectangle2D boundingBox;
    
    public RenderableImageOp(final ContextualRenderedImageFactory myCRIF, final ParameterBlock parameterBlock) {
        this.myCRIF = myCRIF;
        this.paramBlock = (ParameterBlock)parameterBlock.clone();
    }
    
    @Override
    public Vector<RenderableImage> getSources() {
        return this.getRenderableSources();
    }
    
    private Vector getRenderableSources() {
        Vector<RenderableImage> vector = null;
        if (this.paramBlock.getNumSources() > 0) {
            vector = new Vector<RenderableImage>();
            for (int i = 0; i < this.paramBlock.getNumSources(); ++i) {
                final Object source = this.paramBlock.getSource(i);
                if (!(source instanceof RenderableImage)) {
                    break;
                }
                vector.add((RenderableImage)source);
            }
        }
        return vector;
    }
    
    @Override
    public Object getProperty(final String s) {
        return this.myCRIF.getProperty(this.paramBlock, s);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.myCRIF.getPropertyNames();
    }
    
    @Override
    public boolean isDynamic() {
        return this.myCRIF.isDynamic();
    }
    
    @Override
    public float getWidth() {
        if (this.boundingBox == null) {
            this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
        }
        return (float)this.boundingBox.getWidth();
    }
    
    @Override
    public float getHeight() {
        if (this.boundingBox == null) {
            this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
        }
        return (float)this.boundingBox.getHeight();
    }
    
    @Override
    public float getMinX() {
        if (this.boundingBox == null) {
            this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
        }
        return (float)this.boundingBox.getMinX();
    }
    
    @Override
    public float getMinY() {
        if (this.boundingBox == null) {
            this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
        }
        return (float)this.boundingBox.getMinY();
    }
    
    public ParameterBlock setParameterBlock(final ParameterBlock parameterBlock) {
        final ParameterBlock paramBlock = this.paramBlock;
        this.paramBlock = (ParameterBlock)parameterBlock.clone();
        return paramBlock;
    }
    
    public ParameterBlock getParameterBlock() {
        return this.paramBlock;
    }
    
    @Override
    public RenderedImage createScaledRendering(final int n, final int n2, final RenderingHints renderingHints) {
        double n3 = n / (double)this.getWidth();
        final double n4 = n2 / (double)this.getHeight();
        if (Math.abs(n3 / n4 - 1.0) < 0.01) {
            n3 = n4;
        }
        return this.createRendering(new RenderContext(AffineTransform.getScaleInstance(n3, n4), renderingHints));
    }
    
    @Override
    public RenderedImage createDefaultRendering() {
        return this.createRendering(new RenderContext(new AffineTransform()));
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext renderContext) {
        final ParameterBlock parameterBlock = (ParameterBlock)this.paramBlock.clone();
        final Vector renderableSources = this.getRenderableSources();
        try {
            if (renderableSources != null) {
                final Vector sources = new Vector();
                for (int i = 0; i < renderableSources.size(); ++i) {
                    final RenderedImage rendering = renderableSources.elementAt(i).createRendering(this.myCRIF.mapRenderContext(i, renderContext, this.paramBlock, this));
                    if (rendering == null) {
                        return null;
                    }
                    sources.addElement(rendering);
                }
                if (sources.size() > 0) {
                    parameterBlock.setSources(sources);
                }
            }
            return this.myCRIF.create(renderContext, parameterBlock);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
}
