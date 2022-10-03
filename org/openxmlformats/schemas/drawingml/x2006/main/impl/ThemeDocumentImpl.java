package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ThemeDocumentImpl extends XmlComplexContentImpl implements ThemeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName THEME$0;
    
    public ThemeDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOfficeStyleSheet getTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeStyleSheet ctOfficeStyleSheet = (CTOfficeStyleSheet)this.get_store().find_element_user(ThemeDocumentImpl.THEME$0, 0);
            if (ctOfficeStyleSheet == null) {
                return null;
            }
            return ctOfficeStyleSheet;
        }
    }
    
    public void setTheme(final CTOfficeStyleSheet ctOfficeStyleSheet) {
        this.generatedSetterHelperImpl((XmlObject)ctOfficeStyleSheet, ThemeDocumentImpl.THEME$0, 0, (short)1);
    }
    
    public CTOfficeStyleSheet addNewTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeStyleSheet)this.get_store().add_element_user(ThemeDocumentImpl.THEME$0);
        }
    }
    
    static {
        THEME$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "theme");
    }
}
