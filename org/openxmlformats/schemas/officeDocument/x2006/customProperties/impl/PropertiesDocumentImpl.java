package org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PropertiesDocumentImpl extends XmlComplexContentImpl implements PropertiesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PROPERTIES$0;
    
    public PropertiesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTProperties getProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProperties ctProperties = (CTProperties)this.get_store().find_element_user(PropertiesDocumentImpl.PROPERTIES$0, 0);
            if (ctProperties == null) {
                return null;
            }
            return ctProperties;
        }
    }
    
    public void setProperties(final CTProperties ctProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctProperties, PropertiesDocumentImpl.PROPERTIES$0, 0, (short)1);
    }
    
    public CTProperties addNewProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProperties)this.get_store().add_element_user(PropertiesDocumentImpl.PROPERTIES$0);
        }
    }
    
    static {
        PROPERTIES$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/custom-properties", "Properties");
    }
}
