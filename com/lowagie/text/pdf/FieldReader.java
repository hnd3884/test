package com.lowagie.text.pdf;

import java.util.List;
import java.util.HashMap;

public interface FieldReader
{
    HashMap getFields();
    
    String getFieldValue(final String p0);
    
    List getListValues(final String p0);
}
