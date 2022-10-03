package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class KeyDocumentImpl extends XmlComplexContentImpl implements KeyDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName KEY$0;
    
    public KeyDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Keybase getKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().find_element_user(KeyDocumentImpl.KEY$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setKey(final Keybase key) {
        this.generatedSetterHelperImpl(key, KeyDocumentImpl.KEY$0, 0, (short)1);
    }
    
    @Override
    public Keybase addNewKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().add_element_user(KeyDocumentImpl.KEY$0);
            return target;
        }
    }
    
    static {
        KEY$0 = new QName("http://www.w3.org/2001/XMLSchema", "key");
    }
}
