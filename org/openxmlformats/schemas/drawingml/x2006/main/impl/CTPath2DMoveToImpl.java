package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPath2DMoveToImpl extends XmlComplexContentImpl implements CTPath2DMoveTo
{
    private static final long serialVersionUID = 1L;
    private static final QName PT$0;
    
    public CTPath2DMoveToImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAdjPoint2D getPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjPoint2D ctAdjPoint2D = (CTAdjPoint2D)this.get_store().find_element_user(CTPath2DMoveToImpl.PT$0, 0);
            if (ctAdjPoint2D == null) {
                return null;
            }
            return ctAdjPoint2D;
        }
    }
    
    public void setPt(final CTAdjPoint2D ctAdjPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctAdjPoint2D, CTPath2DMoveToImpl.PT$0, 0, (short)1);
    }
    
    public CTAdjPoint2D addNewPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().add_element_user(CTPath2DMoveToImpl.PT$0);
        }
    }
    
    static {
        PT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pt");
    }
}
