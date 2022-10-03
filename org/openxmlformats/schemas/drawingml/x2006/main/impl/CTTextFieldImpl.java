package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STGuid;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextFieldImpl extends XmlComplexContentImpl implements CTTextField
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName PPR$2;
    private static final QName T$4;
    private static final QName ID$6;
    private static final QName TYPE$8;
    
    public CTTextFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextCharacterProperties getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharacterProperties ctTextCharacterProperties = (CTTextCharacterProperties)this.get_store().find_element_user(CTTextFieldImpl.RPR$0, 0);
            if (ctTextCharacterProperties == null) {
                return null;
            }
            return ctTextCharacterProperties;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextFieldImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTTextCharacterProperties ctTextCharacterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharacterProperties, CTTextFieldImpl.RPR$0, 0, (short)1);
    }
    
    public CTTextCharacterProperties addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharacterProperties)this.get_store().add_element_user(CTTextFieldImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextFieldImpl.RPR$0, 0);
        }
    }
    
    public CTTextParagraphProperties getPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextFieldImpl.PPR$2, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextFieldImpl.PPR$2) != 0;
        }
    }
    
    public void setPPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextFieldImpl.PPR$2, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextFieldImpl.PPR$2);
        }
    }
    
    public void unsetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextFieldImpl.PPR$2, 0);
        }
    }
    
    public String getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTTextFieldImpl.T$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTTextFieldImpl.T$4, 0);
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextFieldImpl.T$4) != 0;
        }
    }
    
    public void setT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTTextFieldImpl.T$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTTextFieldImpl.T$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetT(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTTextFieldImpl.T$4, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTTextFieldImpl.T$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextFieldImpl.T$4, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFieldImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGuid xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGuid)this.get_store().find_attribute_user(CTTextFieldImpl.ID$6);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFieldImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFieldImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STGuid stGuid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGuid stGuid2 = (STGuid)this.get_store().find_attribute_user(CTTextFieldImpl.ID$6);
            if (stGuid2 == null) {
                stGuid2 = (STGuid)this.get_store().add_attribute_user(CTTextFieldImpl.ID$6);
            }
            stGuid2.set((XmlObject)stGuid);
        }
    }
    
    public String getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFieldImpl.TYPE$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextFieldImpl.TYPE$8);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextFieldImpl.TYPE$8) != null;
        }
    }
    
    public void setType(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextFieldImpl.TYPE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextFieldImpl.TYPE$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetType(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextFieldImpl.TYPE$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextFieldImpl.TYPE$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextFieldImpl.TYPE$8);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "rPr");
        PPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pPr");
        T$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "t");
        ID$6 = new QName("", "id");
        TYPE$8 = new QName("", "type");
    }
}
