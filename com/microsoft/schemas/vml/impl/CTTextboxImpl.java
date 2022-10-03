package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.office.office.STInsetMode;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.office.office.STTrueFalse;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTTextbox;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextboxImpl extends XmlComplexContentImpl implements CTTextbox
{
    private static final long serialVersionUID = 1L;
    private static final QName TXBXCONTENT$0;
    private static final QName ID$2;
    private static final QName STYLE$4;
    private static final QName INSET$6;
    private static final QName SINGLECLICK$8;
    private static final QName INSETMODE$10;
    
    public CTTextboxImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTxbxContent getTxbxContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTxbxContent ctTxbxContent = (CTTxbxContent)this.get_store().find_element_user(CTTextboxImpl.TXBXCONTENT$0, 0);
            if (ctTxbxContent == null) {
                return null;
            }
            return ctTxbxContent;
        }
    }
    
    public boolean isSetTxbxContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextboxImpl.TXBXCONTENT$0) != 0;
        }
    }
    
    public void setTxbxContent(final CTTxbxContent ctTxbxContent) {
        this.generatedSetterHelperImpl((XmlObject)ctTxbxContent, CTTextboxImpl.TXBXCONTENT$0, 0, (short)1);
    }
    
    public CTTxbxContent addNewTxbxContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTxbxContent)this.get_store().add_element_user(CTTextboxImpl.TXBXCONTENT$0);
        }
    }
    
    public void unsetTxbxContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextboxImpl.TXBXCONTENT$0, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.ID$2);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextboxImpl.ID$2) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextboxImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.ID$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextboxImpl.ID$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextboxImpl.ID$2);
        }
    }
    
    public String getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.STYLE$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.STYLE$4);
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextboxImpl.STYLE$4) != null;
        }
    }
    
    public void setStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.STYLE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextboxImpl.STYLE$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.STYLE$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextboxImpl.STYLE$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextboxImpl.STYLE$4);
        }
    }
    
    public String getInset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.INSET$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetInset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.INSET$6);
        }
    }
    
    public boolean isSetInset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextboxImpl.INSET$6) != null;
        }
    }
    
    public void setInset(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.INSET$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextboxImpl.INSET$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetInset(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextboxImpl.INSET$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextboxImpl.INSET$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetInset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextboxImpl.INSET$6);
        }
    }
    
    public STTrueFalse.Enum getSingleclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.SINGLECLICK$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetSingleclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTTextboxImpl.SINGLECLICK$8);
        }
    }
    
    public boolean isSetSingleclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextboxImpl.SINGLECLICK$8) != null;
        }
    }
    
    public void setSingleclick(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.SINGLECLICK$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextboxImpl.SINGLECLICK$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSingleclick(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTTextboxImpl.SINGLECLICK$8);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTTextboxImpl.SINGLECLICK$8);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetSingleclick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextboxImpl.SINGLECLICK$8);
        }
    }
    
    public STInsetMode.Enum getInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.INSETMODE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextboxImpl.INSETMODE$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STInsetMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STInsetMode xgetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode = (STInsetMode)this.get_store().find_attribute_user(CTTextboxImpl.INSETMODE$10);
            if (stInsetMode == null) {
                stInsetMode = (STInsetMode)this.get_default_attribute_value(CTTextboxImpl.INSETMODE$10);
            }
            return stInsetMode;
        }
    }
    
    public boolean isSetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextboxImpl.INSETMODE$10) != null;
        }
    }
    
    public void setInsetmode(final STInsetMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextboxImpl.INSETMODE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextboxImpl.INSETMODE$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetmode(final STInsetMode stInsetMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode2 = (STInsetMode)this.get_store().find_attribute_user(CTTextboxImpl.INSETMODE$10);
            if (stInsetMode2 == null) {
                stInsetMode2 = (STInsetMode)this.get_store().add_attribute_user(CTTextboxImpl.INSETMODE$10);
            }
            stInsetMode2.set((XmlObject)stInsetMode);
        }
    }
    
    public void unsetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextboxImpl.INSETMODE$10);
        }
    }
    
    static {
        TXBXCONTENT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "txbxContent");
        ID$2 = new QName("", "id");
        STYLE$4 = new QName("", "style");
        INSET$6 = new QName("", "inset");
        SINGLECLICK$8 = new QName("urn:schemas-microsoft-com:office:office", "singleclick");
        INSETMODE$10 = new QName("urn:schemas-microsoft-com:office:office", "insetmode");
    }
}
