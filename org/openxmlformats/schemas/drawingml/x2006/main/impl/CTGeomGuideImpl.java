package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideFormula;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGeomGuideImpl extends XmlComplexContentImpl implements CTGeomGuide
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName FMLA$2;
    
    public CTGeomGuideImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomGuideImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideName xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideName)this.get_store().find_attribute_user(CTGeomGuideImpl.NAME$0);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomGuideImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomGuideImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STGeomGuideName stGeomGuideName) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideName stGeomGuideName2 = (STGeomGuideName)this.get_store().find_attribute_user(CTGeomGuideImpl.NAME$0);
            if (stGeomGuideName2 == null) {
                stGeomGuideName2 = (STGeomGuideName)this.get_store().add_attribute_user(CTGeomGuideImpl.NAME$0);
            }
            stGeomGuideName2.set((XmlObject)stGeomGuideName);
        }
    }
    
    public String getFmla() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomGuideImpl.FMLA$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGeomGuideFormula xgetFmla() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGeomGuideFormula)this.get_store().find_attribute_user(CTGeomGuideImpl.FMLA$2);
        }
    }
    
    public void setFmla(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGeomGuideImpl.FMLA$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGeomGuideImpl.FMLA$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFmla(final STGeomGuideFormula stGeomGuideFormula) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGeomGuideFormula stGeomGuideFormula2 = (STGeomGuideFormula)this.get_store().find_attribute_user(CTGeomGuideImpl.FMLA$2);
            if (stGeomGuideFormula2 == null) {
                stGeomGuideFormula2 = (STGeomGuideFormula)this.get_store().add_attribute_user(CTGeomGuideImpl.FMLA$2);
            }
            stGeomGuideFormula2.set((XmlObject)stGeomGuideFormula);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        FMLA$2 = new QName("", "fmla");
    }
}
