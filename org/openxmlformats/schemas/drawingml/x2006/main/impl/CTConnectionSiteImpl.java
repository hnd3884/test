package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectionSiteImpl extends XmlComplexContentImpl implements CTConnectionSite
{
    private static final long serialVersionUID = 1L;
    private static final QName POS$0;
    private static final QName ANG$2;
    
    public CTConnectionSiteImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAdjPoint2D getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjPoint2D ctAdjPoint2D = (CTAdjPoint2D)this.get_store().find_element_user(CTConnectionSiteImpl.POS$0, 0);
            if (ctAdjPoint2D == null) {
                return null;
            }
            return ctAdjPoint2D;
        }
    }
    
    public void setPos(final CTAdjPoint2D ctAdjPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctAdjPoint2D, CTConnectionSiteImpl.POS$0, 0, (short)1);
    }
    
    public CTAdjPoint2D addNewPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().add_element_user(CTConnectionSiteImpl.POS$0);
        }
    }
    
    public Object getAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionSiteImpl.ANG$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STAdjAngle xgetAng() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAdjAngle)this.get_store().find_attribute_user(CTConnectionSiteImpl.ANG$2);
        }
    }
    
    public void setAng(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionSiteImpl.ANG$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConnectionSiteImpl.ANG$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetAng(final STAdjAngle stAdjAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAdjAngle stAdjAngle2 = (STAdjAngle)this.get_store().find_attribute_user(CTConnectionSiteImpl.ANG$2);
            if (stAdjAngle2 == null) {
                stAdjAngle2 = (STAdjAngle)this.get_store().add_attribute_user(CTConnectionSiteImpl.ANG$2);
            }
            stAdjAngle2.set((XmlObject)stAdjAngle);
        }
    }
    
    static {
        POS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pos");
        ANG$2 = new QName("", "ang");
    }
}
