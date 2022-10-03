package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLangCode;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLang;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class STLangImpl extends XmlUnionImpl implements STLang, STLangCode, STString
{
    private static final long serialVersionUID = 1L;
    
    public STLangImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STLangImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
