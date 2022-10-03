package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectFrameLocking;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGraphicalObjectFrameLockingImpl extends XmlComplexContentImpl implements CTGraphicalObjectFrameLocking
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName NOGRP$2;
    private static final QName NODRILLDOWN$4;
    private static final QName NOSELECT$6;
    private static final QName NOCHANGEASPECT$8;
    private static final QName NOMOVE$10;
    private static final QName NORESIZE$12;
    
    public CTGraphicalObjectFrameLockingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTGraphicalObjectFrameLockingImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGraphicalObjectFrameLockingImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTGraphicalObjectFrameLockingImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTGraphicalObjectFrameLockingImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGraphicalObjectFrameLockingImpl.EXTLST$0, 0);
        }
    }
    
    public boolean getNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2) != null;
        }
    }
    
    public void setNoGrp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoGrp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NOGRP$2);
        }
    }
    
    public boolean getNoDrilldown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoDrilldown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoDrilldown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4) != null;
        }
    }
    
    public void setNoDrilldown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoDrilldown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoDrilldown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NODRILLDOWN$4);
        }
    }
    
    public boolean getNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6) != null;
        }
    }
    
    public void setNoSelect(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoSelect(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NOSELECT$6);
        }
    }
    
    public boolean getNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8) != null;
        }
    }
    
    public void setNoChangeAspect(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoChangeAspect(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NOCHANGEASPECT$8);
        }
    }
    
    public boolean getNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10) != null;
        }
    }
    
    public void setNoMove(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoMove(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NOMOVE$10);
        }
    }
    
    public boolean getNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12) != null;
        }
    }
    
    public void setNoResize(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoResize(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameLockingImpl.NORESIZE$12);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        NOGRP$2 = new QName("", "noGrp");
        NODRILLDOWN$4 = new QName("", "noDrilldown");
        NOSELECT$6 = new QName("", "noSelect");
        NOCHANGEASPECT$8 = new QName("", "noChangeAspect");
        NOMOVE$10 = new QName("", "noMove");
        NORESIZE$12 = new QName("", "noResize");
    }
}
