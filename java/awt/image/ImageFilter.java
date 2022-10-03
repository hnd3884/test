package java.awt.image;

import java.util.Hashtable;

public class ImageFilter implements ImageConsumer, Cloneable
{
    protected ImageConsumer consumer;
    
    public ImageFilter getFilterInstance(final ImageConsumer consumer) {
        final ImageFilter imageFilter = (ImageFilter)this.clone();
        imageFilter.consumer = consumer;
        return imageFilter;
    }
    
    @Override
    public void setDimensions(final int n, final int n2) {
        this.consumer.setDimensions(n, n2);
    }
    
    @Override
    public void setProperties(final Hashtable<?, ?> hashtable) {
        final Hashtable properties = (Hashtable)hashtable.clone();
        final Object value = properties.get("filters");
        if (value == null) {
            properties.put("filters", this.toString());
        }
        else if (value instanceof String) {
            properties.put("filters", (String)value + this.toString());
        }
        this.consumer.setProperties(properties);
    }
    
    @Override
    public void setColorModel(final ColorModel colorModel) {
        this.consumer.setColorModel(colorModel);
    }
    
    @Override
    public void setHints(final int hints) {
        this.consumer.setHints(hints);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        this.consumer.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        this.consumer.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
    }
    
    @Override
    public void imageComplete(final int n) {
        this.consumer.imageComplete(n);
    }
    
    public void resendTopDownLeftRight(final ImageProducer imageProducer) {
        imageProducer.requestTopDownLeftRightResend(this);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
}
