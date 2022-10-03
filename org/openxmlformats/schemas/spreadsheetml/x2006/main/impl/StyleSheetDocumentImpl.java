package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.StyleSheetDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class StyleSheetDocumentImpl extends XmlComplexContentImpl implements StyleSheetDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName STYLESHEET$0;
    
    public StyleSheetDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStylesheet getStyleSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStylesheet ctStylesheet = (CTStylesheet)this.get_store().find_element_user(StyleSheetDocumentImpl.STYLESHEET$0, 0);
            if (ctStylesheet == null) {
                return null;
            }
            return ctStylesheet;
        }
    }
    
    public void setStyleSheet(final CTStylesheet ctStylesheet) {
        this.generatedSetterHelperImpl((XmlObject)ctStylesheet, StyleSheetDocumentImpl.STYLESHEET$0, 0, (short)1);
    }
    
    public CTStylesheet addNewStyleSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStylesheet)this.get_store().add_element_user(StyleSheetDocumentImpl.STYLESHEET$0);
        }
    }
    
    static {
        STYLESHEET$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "styleSheet");
    }
}
