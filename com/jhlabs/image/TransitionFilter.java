package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.ColorModel;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.awt.image.BufferedImageOp;
import java.lang.reflect.Method;
import java.awt.image.BufferedImage;

public class TransitionFilter extends AbstractBufferedImageOp
{
    private float transition;
    private BufferedImage destination;
    private String property;
    private Method method;
    protected BufferedImageOp filter;
    protected float minValue;
    protected float maxValue;
    
    private TransitionFilter() {
        this.transition = 0.0f;
    }
    
    public TransitionFilter(final BufferedImageOp filter, final String property, final float minValue, final float maxValue) {
        this.transition = 0.0f;
        this.filter = filter;
        this.property = property;
        this.minValue = minValue;
        this.maxValue = maxValue;
        try {
            final BeanInfo info = Introspector.getBeanInfo(filter.getClass());
            final PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (int i = 0; i < pds.length; ++i) {
                final PropertyDescriptor pd = pds[i];
                if (property.equals(pd.getName())) {
                    this.method = pd.getWriteMethod();
                    break;
                }
            }
            if (this.method == null) {
                throw new IllegalArgumentException("No such property in object: " + property);
            }
        }
        catch (final IntrospectionException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    public void setTransition(final float transition) {
        this.transition = transition;
    }
    
    public float getTransition() {
        return this.transition;
    }
    
    public void setDestination(final BufferedImage destination) {
        this.destination = destination;
    }
    
    public BufferedImage getDestination() {
        return this.destination;
    }
    
    public void prepareFilter(final float transition) {
        try {
            this.method.invoke(this.filter, new Float(transition));
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Error setting value for property: " + this.property);
        }
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        if (this.destination == null) {
            return dst;
        }
        final float itransition = 1.0f - this.transition;
        final Graphics2D g = dst.createGraphics();
        if (this.transition != 1.0f) {
            final float t = this.minValue + this.transition * (this.maxValue - this.minValue);
            this.prepareFilter(t);
            g.drawImage(src, this.filter, 0, 0);
        }
        if (this.transition != 0.0f) {
            g.setComposite(AlphaComposite.getInstance(3, this.transition));
            final float t = this.minValue + itransition * (this.maxValue - this.minValue);
            this.prepareFilter(t);
            g.drawImage(this.destination, this.filter, 0, 0);
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Transitions/Transition...";
    }
}
