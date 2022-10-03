package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTControlList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommonSlideDataImpl extends XmlComplexContentImpl implements CTCommonSlideData
{
    private static final long serialVersionUID = 1L;
    private static final QName BG$0;
    private static final QName SPTREE$2;
    private static final QName CUSTDATALST$4;
    private static final QName CONTROLS$6;
    private static final QName EXTLST$8;
    private static final QName NAME$10;
    
    public CTCommonSlideDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBackground getBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBackground ctBackground = (CTBackground)this.get_store().find_element_user(CTCommonSlideDataImpl.BG$0, 0);
            if (ctBackground == null) {
                return null;
            }
            return ctBackground;
        }
    }
    
    public boolean isSetBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommonSlideDataImpl.BG$0) != 0;
        }
    }
    
    public void setBg(final CTBackground ctBackground) {
        this.generatedSetterHelperImpl((XmlObject)ctBackground, CTCommonSlideDataImpl.BG$0, 0, (short)1);
    }
    
    public CTBackground addNewBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBackground)this.get_store().add_element_user(CTCommonSlideDataImpl.BG$0);
        }
    }
    
    public void unsetBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommonSlideDataImpl.BG$0, 0);
        }
    }
    
    public CTGroupShape getSpTree() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShape ctGroupShape = (CTGroupShape)this.get_store().find_element_user(CTCommonSlideDataImpl.SPTREE$2, 0);
            if (ctGroupShape == null) {
                return null;
            }
            return ctGroupShape;
        }
    }
    
    public void setSpTree(final CTGroupShape ctGroupShape) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShape, CTCommonSlideDataImpl.SPTREE$2, 0, (short)1);
    }
    
    public CTGroupShape addNewSpTree() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShape)this.get_store().add_element_user(CTCommonSlideDataImpl.SPTREE$2);
        }
    }
    
    public CTCustomerDataList getCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomerDataList list = (CTCustomerDataList)this.get_store().find_element_user(CTCommonSlideDataImpl.CUSTDATALST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommonSlideDataImpl.CUSTDATALST$4) != 0;
        }
    }
    
    public void setCustDataLst(final CTCustomerDataList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommonSlideDataImpl.CUSTDATALST$4, 0, (short)1);
    }
    
    public CTCustomerDataList addNewCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomerDataList)this.get_store().add_element_user(CTCommonSlideDataImpl.CUSTDATALST$4);
        }
    }
    
    public void unsetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommonSlideDataImpl.CUSTDATALST$4, 0);
        }
    }
    
    public CTControlList getControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTControlList list = (CTControlList)this.get_store().find_element_user(CTCommonSlideDataImpl.CONTROLS$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommonSlideDataImpl.CONTROLS$6) != 0;
        }
    }
    
    public void setControls(final CTControlList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommonSlideDataImpl.CONTROLS$6, 0, (short)1);
    }
    
    public CTControlList addNewControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTControlList)this.get_store().add_element_user(CTCommonSlideDataImpl.CONTROLS$6);
        }
    }
    
    public void unsetControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommonSlideDataImpl.CONTROLS$6, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCommonSlideDataImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommonSlideDataImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommonSlideDataImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCommonSlideDataImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommonSlideDataImpl.EXTLST$8, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommonSlideDataImpl.NAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCommonSlideDataImpl.NAME$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTCommonSlideDataImpl.NAME$10);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTCommonSlideDataImpl.NAME$10);
            }
            return xmlString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCommonSlideDataImpl.NAME$10) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCommonSlideDataImpl.NAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCommonSlideDataImpl.NAME$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTCommonSlideDataImpl.NAME$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTCommonSlideDataImpl.NAME$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCommonSlideDataImpl.NAME$10);
        }
    }
    
    static {
        BG$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bg");
        SPTREE$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "spTree");
        CUSTDATALST$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custDataLst");
        CONTROLS$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "controls");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        NAME$10 = new QName("", "name");
    }
}
