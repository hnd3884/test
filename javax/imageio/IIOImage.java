package javax.imageio;

import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

public class IIOImage
{
    protected RenderedImage image;
    protected Raster raster;
    protected List<? extends BufferedImage> thumbnails;
    protected IIOMetadata metadata;
    
    public IIOImage(final RenderedImage image, final List<? extends BufferedImage> thumbnails, final IIOMetadata metadata) {
        this.thumbnails = null;
        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }
        this.image = image;
        this.raster = null;
        this.thumbnails = thumbnails;
        this.metadata = metadata;
    }
    
    public IIOImage(final Raster raster, final List<? extends BufferedImage> thumbnails, final IIOMetadata metadata) {
        this.thumbnails = null;
        if (raster == null) {
            throw new IllegalArgumentException("raster == null!");
        }
        this.raster = raster;
        this.image = null;
        this.thumbnails = thumbnails;
        this.metadata = metadata;
    }
    
    public RenderedImage getRenderedImage() {
        synchronized (this) {
            return this.image;
        }
    }
    
    public void setRenderedImage(final RenderedImage image) {
        synchronized (this) {
            if (image == null) {
                throw new IllegalArgumentException("image == null!");
            }
            this.image = image;
            this.raster = null;
        }
    }
    
    public boolean hasRaster() {
        synchronized (this) {
            return this.raster != null;
        }
    }
    
    public Raster getRaster() {
        synchronized (this) {
            return this.raster;
        }
    }
    
    public void setRaster(final Raster raster) {
        synchronized (this) {
            if (raster == null) {
                throw new IllegalArgumentException("raster == null!");
            }
            this.raster = raster;
            this.image = null;
        }
    }
    
    public int getNumThumbnails() {
        return (this.thumbnails == null) ? 0 : this.thumbnails.size();
    }
    
    public BufferedImage getThumbnail(final int n) {
        if (this.thumbnails == null) {
            throw new IndexOutOfBoundsException("No thumbnails available!");
        }
        return (BufferedImage)this.thumbnails.get(n);
    }
    
    public List<? extends BufferedImage> getThumbnails() {
        return this.thumbnails;
    }
    
    public void setThumbnails(final List<? extends BufferedImage> thumbnails) {
        this.thumbnails = thumbnails;
    }
    
    public IIOMetadata getMetadata() {
        return this.metadata;
    }
    
    public void setMetadata(final IIOMetadata metadata) {
        this.metadata = metadata;
    }
}
