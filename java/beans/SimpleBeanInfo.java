package java.beans;

import java.net.URL;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.awt.Image;

public class SimpleBeanInfo implements BeanInfo
{
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return null;
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return null;
    }
    
    @Override
    public int getDefaultPropertyIndex() {
        return -1;
    }
    
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return null;
    }
    
    @Override
    public int getDefaultEventIndex() {
        return -1;
    }
    
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return null;
    }
    
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }
    
    @Override
    public Image getIcon(final int n) {
        return null;
    }
    
    public Image loadImage(final String s) {
        try {
            final URL resource = this.getClass().getResource(s);
            if (resource != null) {
                final ImageProducer imageProducer = (ImageProducer)resource.getContent();
                if (imageProducer != null) {
                    return Toolkit.getDefaultToolkit().createImage(imageProducer);
                }
            }
        }
        catch (final Exception ex) {}
        return null;
    }
}
