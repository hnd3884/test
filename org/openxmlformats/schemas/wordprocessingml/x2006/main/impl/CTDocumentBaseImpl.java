package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBackground;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocumentBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDocumentBaseImpl extends XmlComplexContentImpl implements CTDocumentBase
{
    private static final long serialVersionUID = 1L;
    private static final QName BACKGROUND$0;
    
    public CTDocumentBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBackground getBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBackground ctBackground = (CTBackground)this.get_store().find_element_user(CTDocumentBaseImpl.BACKGROUND$0, 0);
            if (ctBackground == null) {
                return null;
            }
            return ctBackground;
        }
    }
    
    public boolean isSetBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDocumentBaseImpl.BACKGROUND$0) != 0;
        }
    }
    
    public void setBackground(final CTBackground ctBackground) {
        this.generatedSetterHelperImpl((XmlObject)ctBackground, CTDocumentBaseImpl.BACKGROUND$0, 0, (short)1);
    }
    
    public CTBackground addNewBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBackground)this.get_store().add_element_user(CTDocumentBaseImpl.BACKGROUND$0);
        }
    }
    
    public void unsetBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDocumentBaseImpl.BACKGROUND$0, 0);
        }
    }
    
    static {
        BACKGROUND$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "background");
    }
}
