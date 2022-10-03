package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPictureLocking;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureLockingImpl extends XmlComplexContentImpl implements CTPictureLocking
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName NOGRP$2;
    private static final QName NOSELECT$4;
    private static final QName NOROT$6;
    private static final QName NOCHANGEASPECT$8;
    private static final QName NOMOVE$10;
    private static final QName NORESIZE$12;
    private static final QName NOEDITPOINTS$14;
    private static final QName NOADJUSTHANDLES$16;
    private static final QName NOCHANGEARROWHEADS$18;
    private static final QName NOCHANGESHAPETYPE$20;
    private static final QName NOCROP$22;
    
    public CTPictureLockingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTPictureLockingImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureLockingImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPictureLockingImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTPictureLockingImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureLockingImpl.EXTLST$0, 0);
        }
    }
    
    public boolean getNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOGRP$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOGRP$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOGRP$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOGRP$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOGRP$2) != null;
        }
    }
    
    public void setNoGrp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOGRP$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOGRP$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoGrp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOGRP$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOGRP$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoGrp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOGRP$2);
        }
    }
    
    public boolean getNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOSELECT$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOSELECT$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOSELECT$4) != null;
        }
    }
    
    public void setNoSelect(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoSelect(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOSELECT$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoSelect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOSELECT$4);
        }
    }
    
    public boolean getNoRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOROT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOROT$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOROT$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOROT$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOROT$6) != null;
        }
    }
    
    public void setNoRot(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOROT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOROT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoRot(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOROT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOROT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOROT$6);
        }
    }
    
    public boolean getNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGEASPECT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGEASPECT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8) != null;
        }
    }
    
    public void setNoChangeAspect(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoChangeAspect(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGEASPECT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoChangeAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOCHANGEASPECT$8);
        }
    }
    
    public boolean getNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOMOVE$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOMOVE$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOMOVE$10) != null;
        }
    }
    
    public void setNoMove(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoMove(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOMOVE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoMove() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOMOVE$10);
        }
    }
    
    public boolean getNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NORESIZE$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NORESIZE$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NORESIZE$12) != null;
        }
    }
    
    public void setNoResize(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoResize(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NORESIZE$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NORESIZE$12);
        }
    }
    
    public boolean getNoEditPoints() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOEDITPOINTS$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoEditPoints() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOEDITPOINTS$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoEditPoints() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14) != null;
        }
    }
    
    public void setNoEditPoints(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoEditPoints(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOEDITPOINTS$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoEditPoints() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOEDITPOINTS$14);
        }
    }
    
    public boolean getNoAdjustHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoAdjustHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoAdjustHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16) != null;
        }
    }
    
    public void setNoAdjustHandles(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoAdjustHandles(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOADJUSTHANDLES$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoAdjustHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOADJUSTHANDLES$16);
        }
    }
    
    public boolean getNoChangeArrowheads() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoChangeArrowheads() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoChangeArrowheads() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18) != null;
        }
    }
    
    public void setNoChangeArrowheads(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoChangeArrowheads(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoChangeArrowheads() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOCHANGEARROWHEADS$18);
        }
    }
    
    public boolean getNoChangeShapeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoChangeShapeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoChangeShapeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20) != null;
        }
    }
    
    public void setNoChangeShapeType(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoChangeShapeType(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoChangeShapeType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOCHANGESHAPETYPE$20);
        }
    }
    
    public boolean getNoCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCROP$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureLockingImpl.NOCROP$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCROP$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureLockingImpl.NOCROP$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNoCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureLockingImpl.NOCROP$22) != null;
        }
    }
    
    public void setNoCrop(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCROP$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCROP$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoCrop(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureLockingImpl.NOCROP$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureLockingImpl.NOCROP$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureLockingImpl.NOCROP$22);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        NOGRP$2 = new QName("", "noGrp");
        NOSELECT$4 = new QName("", "noSelect");
        NOROT$6 = new QName("", "noRot");
        NOCHANGEASPECT$8 = new QName("", "noChangeAspect");
        NOMOVE$10 = new QName("", "noMove");
        NORESIZE$12 = new QName("", "noResize");
        NOEDITPOINTS$14 = new QName("", "noEditPoints");
        NOADJUSTHANDLES$16 = new QName("", "noAdjustHandles");
        NOCHANGEARROWHEADS$18 = new QName("", "noChangeArrowheads");
        NOCHANGESHAPETYPE$20 = new QName("", "noChangeShapeType");
        NOCROP$22 = new QName("", "noCrop");
    }
}
