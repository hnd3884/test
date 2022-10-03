package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class QualifyingPropertiesDocumentImpl extends XmlComplexContentImpl implements QualifyingPropertiesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName QUALIFYINGPROPERTIES$0;
    
    public QualifyingPropertiesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public QualifyingPropertiesType getQualifyingProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final QualifyingPropertiesType qualifyingPropertiesType = (QualifyingPropertiesType)this.get_store().find_element_user(QualifyingPropertiesDocumentImpl.QUALIFYINGPROPERTIES$0, 0);
            if (qualifyingPropertiesType == null) {
                return null;
            }
            return qualifyingPropertiesType;
        }
    }
    
    public void setQualifyingProperties(final QualifyingPropertiesType qualifyingPropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)qualifyingPropertiesType, QualifyingPropertiesDocumentImpl.QUALIFYINGPROPERTIES$0, 0, (short)1);
    }
    
    public QualifyingPropertiesType addNewQualifyingProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (QualifyingPropertiesType)this.get_store().add_element_user(QualifyingPropertiesDocumentImpl.QUALIFYINGPROPERTIES$0);
        }
    }
    
    static {
        QUALIFYINGPROPERTIES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "QualifyingProperties");
    }
}
