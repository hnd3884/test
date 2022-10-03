package org.apache.poi.ooxml.util;

import org.apache.poi.util.XMLHelper;
import javax.xml.transform.TransformerFactory;
import org.apache.poi.util.Removal;

@Removal(version = "5.0.0")
@Deprecated
public final class TransformerHelper
{
    private TransformerHelper() {
    }
    
    public static TransformerFactory getFactory() {
        return XMLHelper.getTransformerFactory();
    }
}
