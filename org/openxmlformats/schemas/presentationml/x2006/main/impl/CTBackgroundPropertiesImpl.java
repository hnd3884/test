package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBackgroundPropertiesImpl extends XmlComplexContentImpl implements CTBackgroundProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName NOFILL$0;
    private static final QName SOLIDFILL$2;
    private static final QName GRADFILL$4;
    private static final QName BLIPFILL$6;
    private static final QName PATTFILL$8;
    private static final QName GRPFILL$10;
    private static final QName EFFECTLST$12;
    private static final QName EFFECTDAG$14;
    private static final QName EXTLST$16;
    private static final QName SHADETOTITLE$18;
    
    public CTBackgroundPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.NOFILL$0, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.NOFILL$0) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTBackgroundPropertiesImpl.NOFILL$0, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.NOFILL$0);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.NOFILL$0, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.SOLIDFILL$2, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.SOLIDFILL$2) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTBackgroundPropertiesImpl.SOLIDFILL$2, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.SOLIDFILL$2);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.SOLIDFILL$2, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.GRADFILL$4, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.GRADFILL$4) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTBackgroundPropertiesImpl.GRADFILL$4, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.GRADFILL$4);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.GRADFILL$4, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.BLIPFILL$6, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.BLIPFILL$6) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTBackgroundPropertiesImpl.BLIPFILL$6, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.BLIPFILL$6);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.BLIPFILL$6, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.PATTFILL$8, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.PATTFILL$8) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTBackgroundPropertiesImpl.PATTFILL$8, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.PATTFILL$8);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.PATTFILL$8, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTBackgroundPropertiesImpl.GRPFILL$10, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.GRPFILL$10) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTBackgroundPropertiesImpl.GRPFILL$10, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTBackgroundPropertiesImpl.GRPFILL$10);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.GRPFILL$10, 0);
        }
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTBackgroundPropertiesImpl.EFFECTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.EFFECTLST$12) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBackgroundPropertiesImpl.EFFECTLST$12, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTBackgroundPropertiesImpl.EFFECTLST$12);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.EFFECTLST$12, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTBackgroundPropertiesImpl.EFFECTDAG$14, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.EFFECTDAG$14) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTBackgroundPropertiesImpl.EFFECTDAG$14, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTBackgroundPropertiesImpl.EFFECTDAG$14);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.EFFECTDAG$14, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTBackgroundPropertiesImpl.EXTLST$16, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundPropertiesImpl.EXTLST$16) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBackgroundPropertiesImpl.EXTLST$16, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTBackgroundPropertiesImpl.EXTLST$16);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundPropertiesImpl.EXTLST$16, 0);
        }
    }
    
    public boolean getShadeToTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShadeToTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShadeToTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18) != null;
        }
    }
    
    public void setShadeToTitle(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShadeToTitle(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShadeToTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundPropertiesImpl.SHADETOTITLE$18);
        }
    }
    
    static {
        NOFILL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        EFFECTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        EXTLST$16 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SHADETOTITLE$18 = new QName("", "shadeToTitle");
    }
}
