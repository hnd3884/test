package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPresetGeometry2DImpl extends XmlComplexContentImpl implements CTPresetGeometry2D
{
    private static final long serialVersionUID = 1L;
    private static final QName AVLST$0;
    private static final QName PRST$2;
    
    public CTPresetGeometry2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGeomGuideList getAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGeomGuideList list = (CTGeomGuideList)this.get_store().find_element_user(CTPresetGeometry2DImpl.AVLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresetGeometry2DImpl.AVLST$0) != 0;
        }
    }
    
    public void setAvLst(final CTGeomGuideList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresetGeometry2DImpl.AVLST$0, 0, (short)1);
    }
    
    public CTGeomGuideList addNewAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomGuideList)this.get_store().add_element_user(CTPresetGeometry2DImpl.AVLST$0);
        }
    }
    
    public void unsetAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresetGeometry2DImpl.AVLST$0, 0);
        }
    }
    
    public STShapeType.Enum getPrst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresetGeometry2DImpl.PRST$2);
            if (simpleValue == null) {
                return null;
            }
            return (STShapeType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STShapeType xgetPrst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STShapeType)this.get_store().find_attribute_user(CTPresetGeometry2DImpl.PRST$2);
        }
    }
    
    public void setPrst(final STShapeType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresetGeometry2DImpl.PRST$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresetGeometry2DImpl.PRST$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrst(final STShapeType stShapeType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShapeType stShapeType2 = (STShapeType)this.get_store().find_attribute_user(CTPresetGeometry2DImpl.PRST$2);
            if (stShapeType2 == null) {
                stShapeType2 = (STShapeType)this.get_store().add_attribute_user(CTPresetGeometry2DImpl.PRST$2);
            }
            stShapeType2.set((XmlObject)stShapeType);
        }
    }
    
    static {
        AVLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "avLst");
        PRST$2 = new QName("", "prst");
    }
}
