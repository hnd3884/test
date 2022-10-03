package org.apache.poi.common.usermodel.fonts;

public interface FontFacet
{
    default int getWeight() {
        return 400;
    }
    
    default void setWeight(final int weight) {
        throw new UnsupportedOperationException("FontFacet is read-only.");
    }
    
    default boolean isItalic() {
        return false;
    }
    
    default void setItalic(final boolean italic) {
        throw new UnsupportedOperationException("FontFacet is read-only.");
    }
    
    default Object getFontData() {
        return null;
    }
}
