package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaAttributeModel
{
    public static final int NONE = 0;
    public static final int STRICT = 1;
    public static final int LAX = 2;
    public static final int SKIP = 3;
    
    SchemaLocalAttribute[] getAttributes();
    
    SchemaLocalAttribute getAttribute(final QName p0);
    
    QNameSet getWildcardSet();
    
    int getWildcardProcess();
}
