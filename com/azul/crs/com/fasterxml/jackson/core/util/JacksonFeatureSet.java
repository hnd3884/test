package com.azul.crs.com.fasterxml.jackson.core.util;

public final class JacksonFeatureSet<F extends JacksonFeature>
{
    protected int _enabled;
    
    protected JacksonFeatureSet(final int bitmask) {
        this._enabled = bitmask;
    }
    
    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromDefaults(final F[] allFeatures) {
        if (allFeatures.length > 31) {
            final String desc = allFeatures[0].getClass().getName();
            throw new IllegalArgumentException(String.format("Can not use type `%s` with JacksonFeatureSet: too many entries (%d > 31)", desc, allFeatures.length));
        }
        int flags = 0;
        for (final F f : allFeatures) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return new JacksonFeatureSet<F>(flags);
    }
    
    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromBitmask(final int bitmask) {
        return new JacksonFeatureSet<F>(bitmask);
    }
    
    public JacksonFeatureSet<F> with(final F feature) {
        final int newMask = this._enabled | feature.getMask();
        return (newMask == this._enabled) ? this : new JacksonFeatureSet<F>(newMask);
    }
    
    public JacksonFeatureSet<F> without(final F feature) {
        final int newMask = this._enabled & ~feature.getMask();
        return (newMask == this._enabled) ? this : new JacksonFeatureSet<F>(newMask);
    }
    
    public boolean isEnabled(final F feature) {
        return (feature.getMask() & this._enabled) != 0x0;
    }
    
    public int asBitmask() {
        return this._enabled;
    }
}
