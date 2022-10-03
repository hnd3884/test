package org.apache.poi.common.usermodel;

import org.apache.poi.util.Removal;

public interface Hyperlink
{
    String getAddress();
    
    void setAddress(final String p0);
    
    String getLabel();
    
    void setLabel(final String p0);
    
    HyperlinkType getType();
    
    @Deprecated
    @Removal(version = "4.2")
    HyperlinkType getTypeEnum();
}
