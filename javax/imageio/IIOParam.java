package javax.imageio;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class IIOParam
{
    protected Rectangle sourceRegion;
    protected int sourceXSubsampling;
    protected int sourceYSubsampling;
    protected int subsamplingXOffset;
    protected int subsamplingYOffset;
    protected int[] sourceBands;
    protected ImageTypeSpecifier destinationType;
    protected Point destinationOffset;
    protected IIOParamController defaultController;
    protected IIOParamController controller;
    
    protected IIOParam() {
        this.sourceRegion = null;
        this.sourceXSubsampling = 1;
        this.sourceYSubsampling = 1;
        this.subsamplingXOffset = 0;
        this.subsamplingYOffset = 0;
        this.sourceBands = null;
        this.destinationType = null;
        this.destinationOffset = new Point(0, 0);
        this.defaultController = null;
        this.controller = null;
        this.controller = this.defaultController;
    }
    
    public void setSourceRegion(final Rectangle rectangle) {
        if (rectangle == null) {
            this.sourceRegion = null;
            return;
        }
        if (rectangle.x < 0) {
            throw new IllegalArgumentException("sourceRegion.x < 0!");
        }
        if (rectangle.y < 0) {
            throw new IllegalArgumentException("sourceRegion.y < 0!");
        }
        if (rectangle.width <= 0) {
            throw new IllegalArgumentException("sourceRegion.width <= 0!");
        }
        if (rectangle.height <= 0) {
            throw new IllegalArgumentException("sourceRegion.height <= 0!");
        }
        if (rectangle.width <= this.subsamplingXOffset) {
            throw new IllegalStateException("sourceRegion.width <= subsamplingXOffset!");
        }
        if (rectangle.height <= this.subsamplingYOffset) {
            throw new IllegalStateException("sourceRegion.height <= subsamplingYOffset!");
        }
        this.sourceRegion = (Rectangle)rectangle.clone();
    }
    
    public Rectangle getSourceRegion() {
        if (this.sourceRegion == null) {
            return null;
        }
        return (Rectangle)this.sourceRegion.clone();
    }
    
    public void setSourceSubsampling(final int sourceXSubsampling, final int sourceYSubsampling, final int subsamplingXOffset, final int subsamplingYOffset) {
        if (sourceXSubsampling <= 0) {
            throw new IllegalArgumentException("sourceXSubsampling <= 0!");
        }
        if (sourceYSubsampling <= 0) {
            throw new IllegalArgumentException("sourceYSubsampling <= 0!");
        }
        if (subsamplingXOffset < 0 || subsamplingXOffset >= sourceXSubsampling) {
            throw new IllegalArgumentException("subsamplingXOffset out of range!");
        }
        if (subsamplingYOffset < 0 || subsamplingYOffset >= sourceYSubsampling) {
            throw new IllegalArgumentException("subsamplingYOffset out of range!");
        }
        if (this.sourceRegion != null && (subsamplingXOffset >= this.sourceRegion.width || subsamplingYOffset >= this.sourceRegion.height)) {
            throw new IllegalStateException("region contains no pixels!");
        }
        this.sourceXSubsampling = sourceXSubsampling;
        this.sourceYSubsampling = sourceYSubsampling;
        this.subsamplingXOffset = subsamplingXOffset;
        this.subsamplingYOffset = subsamplingYOffset;
    }
    
    public int getSourceXSubsampling() {
        return this.sourceXSubsampling;
    }
    
    public int getSourceYSubsampling() {
        return this.sourceYSubsampling;
    }
    
    public int getSubsamplingXOffset() {
        return this.subsamplingXOffset;
    }
    
    public int getSubsamplingYOffset() {
        return this.subsamplingYOffset;
    }
    
    public void setSourceBands(final int[] array) {
        if (array == null) {
            this.sourceBands = null;
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
            this.sourceBands = array.clone();
        }
    }
    
    public int[] getSourceBands() {
        if (this.sourceBands == null) {
            return null;
        }
        return this.sourceBands.clone();
    }
    
    public void setDestinationType(final ImageTypeSpecifier destinationType) {
        this.destinationType = destinationType;
    }
    
    public ImageTypeSpecifier getDestinationType() {
        return this.destinationType;
    }
    
    public void setDestinationOffset(final Point point) {
        if (point == null) {
            throw new IllegalArgumentException("destinationOffset == null!");
        }
        this.destinationOffset = (Point)point.clone();
    }
    
    public Point getDestinationOffset() {
        return (Point)this.destinationOffset.clone();
    }
    
    public void setController(final IIOParamController controller) {
        this.controller = controller;
    }
    
    public IIOParamController getController() {
        return this.controller;
    }
    
    public IIOParamController getDefaultController() {
        return this.defaultController;
    }
    
    public boolean hasController() {
        return this.controller != null;
    }
    
    public boolean activateController() {
        if (!this.hasController()) {
            throw new IllegalStateException("hasController() == false!");
        }
        return this.getController().activate(this);
    }
}
