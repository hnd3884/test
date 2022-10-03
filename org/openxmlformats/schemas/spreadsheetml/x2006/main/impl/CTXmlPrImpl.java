package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTXmlPrImpl extends XmlComplexContentImpl implements CTXmlPr
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName MAPID$2;
    private static final QName XPATH$4;
    private static final QName XMLDATATYPE$6;
    
    public CTXmlPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTXmlPrImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTXmlPrImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTXmlPrImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTXmlPrImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTXmlPrImpl.EXTLST$0, 0);
        }
    }
    
    public long getMapId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.MAPID$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMapId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTXmlPrImpl.MAPID$2);
        }
    }
    
    public void setMapId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.MAPID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXmlPrImpl.MAPID$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMapId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTXmlPrImpl.MAPID$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTXmlPrImpl.MAPID$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getXpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.XPATH$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetXpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTXmlPrImpl.XPATH$4);
        }
    }
    
    public void setXpath(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.XPATH$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXmlPrImpl.XPATH$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetXpath(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTXmlPrImpl.XPATH$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTXmlPrImpl.XPATH$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public STXmlDataType.Enum getXmlDataType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STXmlDataType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STXmlDataType xgetXmlDataType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXmlDataType)this.get_store().find_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
        }
    }
    
    public void setXmlDataType(final STXmlDataType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetXmlDataType(final STXmlDataType stXmlDataType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXmlDataType stXmlDataType2 = (STXmlDataType)this.get_store().find_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
            if (stXmlDataType2 == null) {
                stXmlDataType2 = (STXmlDataType)this.get_store().add_attribute_user(CTXmlPrImpl.XMLDATATYPE$6);
            }
            stXmlDataType2.set((XmlObject)stXmlDataType);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        MAPID$2 = new QName("", "mapId");
        XPATH$4 = new QName("", "xpath");
        XMLDATATYPE$6 = new QName("", "xmlDataType");
    }
}
