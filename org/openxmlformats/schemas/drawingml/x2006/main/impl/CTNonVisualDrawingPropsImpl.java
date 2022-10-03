package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualDrawingPropsImpl extends XmlComplexContentImpl implements CTNonVisualDrawingProps
{
    private static final long serialVersionUID = 1L;
    private static final QName HLINKCLICK$0;
    private static final QName HLINKHOVER$2;
    private static final QName EXTLST$4;
    private static final QName ID$6;
    private static final QName NAME$8;
    private static final QName DESCR$10;
    private static final QName HIDDEN$12;
    
    public CTNonVisualDrawingPropsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTHyperlink getHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTNonVisualDrawingPropsImpl.HLINKCLICK$0, 0);
            if (ctHyperlink == null) {
                return null;
            }
            return ctHyperlink;
        }
    }
    
    public boolean isSetHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualDrawingPropsImpl.HLINKCLICK$0) != 0;
        }
    }
    
    public void setHlinkClick(final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTNonVisualDrawingPropsImpl.HLINKCLICK$0, 0, (short)1);
    }
    
    public CTHyperlink addNewHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTNonVisualDrawingPropsImpl.HLINKCLICK$0);
        }
    }
    
    public void unsetHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualDrawingPropsImpl.HLINKCLICK$0, 0);
        }
    }
    
    public CTHyperlink getHlinkHover() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTNonVisualDrawingPropsImpl.HLINKHOVER$2, 0);
            if (ctHyperlink == null) {
                return null;
            }
            return ctHyperlink;
        }
    }
    
    public boolean isSetHlinkHover() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualDrawingPropsImpl.HLINKHOVER$2) != 0;
        }
    }
    
    public void setHlinkHover(final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTNonVisualDrawingPropsImpl.HLINKHOVER$2, 0, (short)1);
    }
    
    public CTHyperlink addNewHlinkHover() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTNonVisualDrawingPropsImpl.HLINKHOVER$2);
        }
    }
    
    public void unsetHlinkHover() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualDrawingPropsImpl.HLINKHOVER$2, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualDrawingPropsImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualDrawingPropsImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualDrawingPropsImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualDrawingPropsImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualDrawingPropsImpl.EXTLST$4, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDrawingElementId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDrawingElementId)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final STDrawingElementId stDrawingElementId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDrawingElementId stDrawingElementId2 = (STDrawingElementId)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
            if (stDrawingElementId2 == null) {
                stDrawingElementId2 = (STDrawingElementId)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.ID$6);
            }
            stDrawingElementId2.set((XmlObject)stDrawingElementId);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.NAME$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public String getDescr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNonVisualDrawingPropsImpl.DESCR$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDescr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTNonVisualDrawingPropsImpl.DESCR$10);
            }
            return xmlString;
        }
    }
    
    public boolean isSetDescr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10) != null;
        }
    }
    
    public void setDescr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDescr(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.DESCR$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDescr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNonVisualDrawingPropsImpl.DESCR$10);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNonVisualDrawingPropsImpl.HIDDEN$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNonVisualDrawingPropsImpl.HIDDEN$12);
        }
    }
    
    static {
        HLINKCLICK$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlinkClick");
        HLINKHOVER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlinkHover");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        ID$6 = new QName("", "id");
        NAME$8 = new QName("", "name");
        DESCR$10 = new QName("", "descr");
        HIDDEN$12 = new QName("", "hidden");
    }
}
