package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.EnumerationDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class EnumerationDocumentImpl extends XmlComplexContentImpl implements EnumerationDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ENUMERATION$0;
    
    public EnumerationDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NoFixedFacet getEnumeration() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)this.get_store().find_element_user(EnumerationDocumentImpl.ENUMERATION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setEnumeration(final NoFixedFacet enumeration) {
        this.generatedSetterHelperImpl(enumeration, EnumerationDocumentImpl.ENUMERATION$0, 0, (short)1);
    }
    
    @Override
    public NoFixedFacet addNewEnumeration() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)this.get_store().add_element_user(EnumerationDocumentImpl.ENUMERATION$0);
            return target;
        }
    }
    
    static {
        ENUMERATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "enumeration");
    }
}
