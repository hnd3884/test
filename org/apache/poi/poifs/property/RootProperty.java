package org.apache.poi.poifs.property;

import org.apache.commons.math3.util.ArithmeticUtils;

public final class RootProperty extends DirectoryProperty
{
    private static final String NAME = "Root Entry";
    
    RootProperty() {
        super("Root Entry");
        this.setNodeColor((byte)1);
        this.setPropertyType((byte)5);
        this.setStartBlock(-2);
    }
    
    RootProperty(final int index, final byte[] array, final int offset) {
        super(index, array, offset);
    }
    
    public void setSize(final int size) {
        final int BLOCK_SHIFT = 6;
        final int _block_size = 64;
        super.setSize(ArithmeticUtils.mulAndCheck(size, 64));
    }
    
    @Override
    public String getName() {
        return "Root Entry";
    }
}
