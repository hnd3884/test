package java.awt.font;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FontRenderContext
{
    private transient AffineTransform tx;
    private transient Object aaHintValue;
    private transient Object fmHintValue;
    private transient boolean defaulting;
    
    protected FontRenderContext() {
        this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
        this.defaulting = true;
    }
    
    public FontRenderContext(final AffineTransform affineTransform, final boolean b, final boolean b2) {
        if (affineTransform != null && !affineTransform.isIdentity()) {
            this.tx = new AffineTransform(affineTransform);
        }
        if (b) {
            this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        }
        else {
            this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        }
        if (b2) {
            this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
        }
        else {
            this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
        }
    }
    
    public FontRenderContext(final AffineTransform affineTransform, final Object aaHintValue, final Object fmHintValue) {
        if (affineTransform != null && !affineTransform.isIdentity()) {
            this.tx = new AffineTransform(affineTransform);
        }
        try {
            if (!RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(aaHintValue)) {
                throw new IllegalArgumentException("AA hint:" + aaHintValue);
            }
            this.aaHintValue = aaHintValue;
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("AA hint:" + aaHintValue);
        }
        try {
            if (!RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(fmHintValue)) {
                throw new IllegalArgumentException("FM hint:" + fmHintValue);
            }
            this.fmHintValue = fmHintValue;
        }
        catch (final Exception ex2) {
            throw new IllegalArgumentException("FM hint:" + fmHintValue);
        }
    }
    
    public boolean isTransformed() {
        if (!this.defaulting) {
            return this.tx != null;
        }
        return !this.getTransform().isIdentity();
    }
    
    public int getTransformType() {
        if (this.defaulting) {
            return this.getTransform().getType();
        }
        if (this.tx == null) {
            return 0;
        }
        return this.tx.getType();
    }
    
    public AffineTransform getTransform() {
        return (this.tx == null) ? new AffineTransform() : new AffineTransform(this.tx);
    }
    
    public boolean isAntiAliased() {
        return this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    }
    
    public boolean usesFractionalMetrics() {
        return this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_OFF && this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
    }
    
    public Object getAntiAliasingHint() {
        if (!this.defaulting) {
            return this.aaHintValue;
        }
        if (this.isAntiAliased()) {
            return RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        }
        return RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    }
    
    public Object getFractionalMetricsHint() {
        if (!this.defaulting) {
            return this.fmHintValue;
        }
        if (this.usesFractionalMetrics()) {
            return RenderingHints.VALUE_FRACTIONALMETRICS_ON;
        }
        return RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            return this.equals((FontRenderContext)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public boolean equals(final FontRenderContext fontRenderContext) {
        if (this == fontRenderContext) {
            return true;
        }
        if (fontRenderContext == null) {
            return false;
        }
        if (!fontRenderContext.defaulting && !this.defaulting) {
            return fontRenderContext.aaHintValue == this.aaHintValue && fontRenderContext.fmHintValue == this.fmHintValue && ((this.tx == null) ? (fontRenderContext.tx == null) : this.tx.equals(fontRenderContext.tx));
        }
        return fontRenderContext.getAntiAliasingHint() == this.getAntiAliasingHint() && fontRenderContext.getFractionalMetricsHint() == this.getFractionalMetricsHint() && fontRenderContext.getTransform().equals(this.getTransform());
    }
    
    @Override
    public int hashCode() {
        final int n = (this.tx == null) ? 0 : this.tx.hashCode();
        int n2;
        if (this.defaulting) {
            n2 = n + this.getAntiAliasingHint().hashCode() + this.getFractionalMetricsHint().hashCode();
        }
        else {
            n2 = n + this.aaHintValue.hashCode() + this.fmHintValue.hashCode();
        }
        return n2;
    }
}
