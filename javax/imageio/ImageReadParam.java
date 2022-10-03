package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.Dimension;

public class ImageReadParam extends IIOParam
{
    protected boolean canSetSourceRenderSize;
    protected Dimension sourceRenderSize;
    protected BufferedImage destination;
    protected int[] destinationBands;
    protected int minProgressivePass;
    protected int numProgressivePasses;
    
    public ImageReadParam() {
        this.canSetSourceRenderSize = false;
        this.sourceRenderSize = null;
        this.destination = null;
        this.destinationBands = null;
        this.minProgressivePass = 0;
        this.numProgressivePasses = Integer.MAX_VALUE;
    }
    
    @Override
    public void setDestinationType(final ImageTypeSpecifier destinationType) {
        super.setDestinationType(destinationType);
        this.setDestination(null);
    }
    
    public void setDestination(final BufferedImage destination) {
        this.destination = destination;
    }
    
    public BufferedImage getDestination() {
        return this.destination;
    }
    
    public void setDestinationBands(final int[] array) {
        if (array == null) {
            this.destinationBands = null;
        }
        else {
            for (int length = array.length, i = 0; i < length; ++i) {
                final int n = array[i];
                if (n < 0) {
                    throw new IllegalArgumentException("Band value < 0!");
                }
                for (int j = i + 1; j < length; ++j) {
                    if (n == array[j]) {
                        throw new IllegalArgumentException("Duplicate band value!");
                    }
                }
            }
            this.destinationBands = array.clone();
        }
    }
    
    public int[] getDestinationBands() {
        if (this.destinationBands == null) {
            return null;
        }
        return this.destinationBands.clone();
    }
    
    public boolean canSetSourceRenderSize() {
        return this.canSetSourceRenderSize;
    }
    
    public void setSourceRenderSize(final Dimension dimension) throws UnsupportedOperationException {
        if (!this.canSetSourceRenderSize()) {
            throw new UnsupportedOperationException("Can't set source render size!");
        }
        if (dimension == null) {
            this.sourceRenderSize = null;
        }
        else {
            if (dimension.width <= 0 || dimension.height <= 0) {
                throw new IllegalArgumentException("width or height <= 0!");
            }
            this.sourceRenderSize = (Dimension)dimension.clone();
        }
    }
    
    public Dimension getSourceRenderSize() {
        return (this.sourceRenderSize == null) ? null : ((Dimension)this.sourceRenderSize.clone());
    }
    
    public void setSourceProgressivePasses(final int minProgressivePass, final int numProgressivePasses) {
        if (minProgressivePass < 0) {
            throw new IllegalArgumentException("minPass < 0!");
        }
        if (numProgressivePasses <= 0) {
            throw new IllegalArgumentException("numPasses <= 0!");
        }
        if (numProgressivePasses != Integer.MAX_VALUE && (minProgressivePass + numProgressivePasses - 1 & Integer.MIN_VALUE) != 0x0) {
            throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!");
        }
        this.minProgressivePass = minProgressivePass;
        this.numProgressivePasses = numProgressivePasses;
    }
    
    public int getSourceMinProgressivePass() {
        return this.minProgressivePass;
    }
    
    public int getSourceMaxProgressivePass() {
        if (this.numProgressivePasses == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return this.minProgressivePass + this.numProgressivePasses - 1;
    }
    
    public int getSourceNumProgressivePasses() {
        return this.numProgressivePasses;
    }
}
