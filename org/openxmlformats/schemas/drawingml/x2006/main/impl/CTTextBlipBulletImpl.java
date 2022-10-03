package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBlipBullet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextBlipBulletImpl extends XmlComplexContentImpl implements CTTextBlipBullet
{
    private static final long serialVersionUID = 1L;
    private static final QName BLIP$0;
    
    public CTTextBlipBulletImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBlip getBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlip ctBlip = (CTBlip)this.get_store().find_element_user(CTTextBlipBulletImpl.BLIP$0, 0);
            if (ctBlip == null) {
                return null;
            }
            return ctBlip;
        }
    }
    
    public void setBlip(final CTBlip ctBlip) {
        this.generatedSetterHelperImpl((XmlObject)ctBlip, CTTextBlipBulletImpl.BLIP$0, 0, (short)1);
    }
    
    public CTBlip addNewBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlip)this.get_store().add_element_user(CTTextBlipBulletImpl.BLIP$0);
        }
    }
    
    static {
        BLIP$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blip");
    }
}
