package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class ChromeFilter extends LightFilter
{
    private float amount;
    private float exposure;
    
    public ChromeFilter() {
        this.amount = 0.5f;
        this.exposure = 1.0f;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setExposure(final float exposure) {
        this.exposure = exposure;
    }
    
    public float getExposure() {
        return this.exposure;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        this.setColorSource(1);
        dst = super.filter(src, dst);
        final TransferFilter tf = new TransferFilter() {
            @Override
            protected float transferFunction(float v) {
                v += ChromeFilter.this.amount * (float)Math.sin(v * 2.0f * 3.141592653589793);
                return 1.0f - (float)Math.exp(-v * ChromeFilter.this.exposure);
            }
        };
        return tf.filter(dst, dst);
    }
    
    @Override
    public String toString() {
        return "Effects/Chrome...";
    }
}
