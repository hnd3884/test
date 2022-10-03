package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class StylesDocumentImpl extends XmlComplexContentImpl implements StylesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName STYLES$0;
    
    public StylesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStyles getStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyles ctStyles = (CTStyles)this.get_store().find_element_user(StylesDocumentImpl.STYLES$0, 0);
            if (ctStyles == null) {
                return null;
            }
            return ctStyles;
        }
    }
    
    public void setStyles(final CTStyles ctStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctStyles, StylesDocumentImpl.STYLES$0, 0, (short)1);
    }
    
    public CTStyles addNewStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyles)this.get_store().add_element_user(StylesDocumentImpl.STYLES$0);
        }
    }
    
    static {
        STYLES$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styles");
    }
}
