package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AllDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AllDocumentImpl extends XmlComplexContentImpl implements AllDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ALL$0;
    
    public AllDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public All getAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(AllDocumentImpl.ALL$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setAll(final All all) {
        this.generatedSetterHelperImpl(all, AllDocumentImpl.ALL$0, 0, (short)1);
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(AllDocumentImpl.ALL$0);
            return target;
        }
    }
    
    static {
        ALL$0 = new QName("http://www.w3.org/2001/XMLSchema", "all");
    }
}
