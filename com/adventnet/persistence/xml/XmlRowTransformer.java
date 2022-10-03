package com.adventnet.persistence.xml;

import java.util.Map;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;

public interface XmlRowTransformer
{
    Row createRow(final String p0, final Attributes p1);
    
    void setDisplayNames(final String p0, final Map p1);
    
    void setColumnNames(final String p0, final Map p1);
}
