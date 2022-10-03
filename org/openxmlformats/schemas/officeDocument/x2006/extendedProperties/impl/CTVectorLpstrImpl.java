package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorLpstr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTVectorLpstrImpl extends XmlComplexContentImpl implements CTVectorLpstr
{
    private static final long serialVersionUID = 1L;
    private static final QName VECTOR$0;
    
    public CTVectorLpstrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTVector getVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVector ctVector = (CTVector)this.get_store().find_element_user(CTVectorLpstrImpl.VECTOR$0, 0);
            if (ctVector == null) {
                return null;
            }
            return ctVector;
        }
    }
    
    public void setVector(final CTVector ctVector) {
        this.generatedSetterHelperImpl((XmlObject)ctVector, CTVectorLpstrImpl.VECTOR$0, 0, (short)1);
    }
    
    public CTVector addNewVector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVector)this.get_store().add_element_user(CTVectorLpstrImpl.VECTOR$0);
        }
    }
    
    static {
        VECTOR$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "vector");
    }
}
