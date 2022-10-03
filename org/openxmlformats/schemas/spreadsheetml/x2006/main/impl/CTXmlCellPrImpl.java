package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlCellPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTXmlCellPrImpl extends XmlComplexContentImpl implements CTXmlCellPr
{
    private static final long serialVersionUID = 1L;
    private static final QName XMLPR$0;
    private static final QName EXTLST$2;
    private static final QName ID$4;
    private static final QName UNIQUENAME$6;
    
    public CTXmlCellPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTXmlPr getXmlPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTXmlPr ctXmlPr = (CTXmlPr)this.get_store().find_element_user(CTXmlCellPrImpl.XMLPR$0, 0);
            if (ctXmlPr == null) {
                return null;
            }
            return ctXmlPr;
        }
    }
    
    public void setXmlPr(final CTXmlPr ctXmlPr) {
        this.generatedSetterHelperImpl((XmlObject)ctXmlPr, CTXmlCellPrImpl.XMLPR$0, 0, (short)1);
    }
    
    public CTXmlPr addNewXmlPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXmlPr)this.get_store().add_element_user(CTXmlCellPrImpl.XMLPR$0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTXmlCellPrImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTXmlCellPrImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTXmlCellPrImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTXmlCellPrImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTXmlCellPrImpl.EXTLST$2, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlCellPrImpl.ID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTXmlCellPrImpl.ID$4);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlCellPrImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXmlCellPrImpl.ID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTXmlCellPrImpl.ID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTXmlCellPrImpl.ID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
        }
    }
    
    public boolean isSetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6) != null;
        }
    }
    
    public void setUniqueName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUniqueName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTXmlCellPrImpl.UNIQUENAME$6);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXmlCellPrImpl.UNIQUENAME$6);
        }
    }
    
    static {
        XMLPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "xmlPr");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        ID$4 = new QName("", "id");
        UNIQUENAME$6 = new QName("", "uniqueName");
    }
}
