package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.UniqueDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class UniqueDocumentImpl extends XmlComplexContentImpl implements UniqueDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName UNIQUE$0;
    
    public UniqueDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Keybase getUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().find_element_user(UniqueDocumentImpl.UNIQUE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setUnique(final Keybase unique) {
        this.generatedSetterHelperImpl(unique, UniqueDocumentImpl.UNIQUE$0, 0, (short)1);
    }
    
    @Override
    public Keybase addNewUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().add_element_user(UniqueDocumentImpl.UNIQUE$0);
            return target;
        }
    }
    
    static {
        UNIQUE$0 = new QName("http://www.w3.org/2001/XMLSchema", "unique");
    }
}
