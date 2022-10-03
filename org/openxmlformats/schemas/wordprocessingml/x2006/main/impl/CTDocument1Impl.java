package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;

public class CTDocument1Impl extends CTDocumentBaseImpl implements CTDocument1
{
    private static final long serialVersionUID = 1L;
    private static final QName BODY$0;
    
    public CTDocument1Impl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTBody getBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBody ctBody = (CTBody)this.get_store().find_element_user(CTDocument1Impl.BODY$0, 0);
            if (ctBody == null) {
                return null;
            }
            return ctBody;
        }
    }
    
    @Override
    public boolean isSetBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDocument1Impl.BODY$0) != 0;
        }
    }
    
    @Override
    public void setBody(final CTBody ctBody) {
        this.generatedSetterHelperImpl((XmlObject)ctBody, CTDocument1Impl.BODY$0, 0, (short)1);
    }
    
    @Override
    public CTBody addNewBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBody)this.get_store().add_element_user(CTDocument1Impl.BODY$0);
        }
    }
    
    @Override
    public void unsetBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDocument1Impl.BODY$0, 0);
        }
    }
    
    static {
        BODY$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "body");
    }
}
