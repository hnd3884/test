package org.apache.poi.common.usermodel.fonts;

import java.util.Collections;
import java.util.List;

public interface FontInfo
{
    default Integer getIndex() {
        return null;
    }
    
    default void setIndex(final int index) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    String getTypeface();
    
    default void setTypeface(final String typeface) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    default FontCharset getCharset() {
        return FontCharset.ANSI;
    }
    
    default void setCharset(final FontCharset charset) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    default FontFamily getFamily() {
        return FontFamily.FF_DONTCARE;
    }
    
    default void setFamily(final FontFamily family) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    default FontPitch getPitch() {
        return null;
    }
    
    default void setPitch(final FontPitch pitch) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    default byte[] getPanose() {
        return null;
    }
    
    default void setPanose(final byte[] panose) {
        throw new UnsupportedOperationException("FontInfo is read-only.");
    }
    
    default List<? extends FontFacet> getFacets() {
        return Collections.emptyList();
    }
}
