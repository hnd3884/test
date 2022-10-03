package org.apache.poi.xssf.model;

import org.apache.poi.ss.usermodel.RichTextString;

public interface SharedStrings
{
    RichTextString getItemAt(final int p0);
    
    int getCount();
    
    int getUniqueCount();
}
