package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class CategoryLabelWidthType implements Serializable
{
    private static final long serialVersionUID = -6976024792582949656L;
    public static final CategoryLabelWidthType CATEGORY;
    public static final CategoryLabelWidthType RANGE;
    private String name;
    
    private CategoryLabelWidthType(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null 'name' argument.");
        }
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryLabelWidthType)) {
            return false;
        }
        final CategoryLabelWidthType t = (CategoryLabelWidthType)obj;
        return this.name.equals(t.toString());
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(CategoryLabelWidthType.CATEGORY)) {
            return CategoryLabelWidthType.CATEGORY;
        }
        if (this.equals(CategoryLabelWidthType.RANGE)) {
            return CategoryLabelWidthType.RANGE;
        }
        return null;
    }
    
    static {
        CATEGORY = new CategoryLabelWidthType("CategoryLabelWidthType.CATEGORY");
        RANGE = new CategoryLabelWidthType("CategoryLabelWidthType.RANGE");
    }
}
