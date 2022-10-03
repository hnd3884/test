package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.Composite;

public final class ContourComposite implements Composite
{
    private int offset;
    
    public ContourComposite(final int offset) {
        this.offset = offset;
    }
    
    public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
        return new ContourCompositeContext(this.offset, srcColorModel, dstColorModel);
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ContourComposite;
    }
}
