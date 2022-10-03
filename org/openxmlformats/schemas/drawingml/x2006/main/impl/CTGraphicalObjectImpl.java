package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGraphicalObjectImpl extends XmlComplexContentImpl implements CTGraphicalObject
{
    private static final long serialVersionUID = 1L;
    private static final QName GRAPHICDATA$0;
    
    public CTGraphicalObjectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGraphicalObjectData getGraphicData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectData ctGraphicalObjectData = (CTGraphicalObjectData)this.get_store().find_element_user(CTGraphicalObjectImpl.GRAPHICDATA$0, 0);
            if (ctGraphicalObjectData == null) {
                return null;
            }
            return ctGraphicalObjectData;
        }
    }
    
    public void setGraphicData(final CTGraphicalObjectData ctGraphicalObjectData) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectData, CTGraphicalObjectImpl.GRAPHICDATA$0, 0, (short)1);
    }
    
    public CTGraphicalObjectData addNewGraphicData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectData)this.get_store().add_element_user(CTGraphicalObjectImpl.GRAPHICDATA$0);
        }
    }
    
    static {
        GRAPHICDATA$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphicData");
    }
}
