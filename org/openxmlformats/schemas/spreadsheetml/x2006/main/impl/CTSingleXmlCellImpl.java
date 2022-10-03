package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlCellPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSingleXmlCellImpl extends XmlComplexContentImpl implements CTSingleXmlCell
{
    private static final long serialVersionUID = 1L;
    private static final QName XMLCELLPR$0;
    private static final QName EXTLST$2;
    private static final QName ID$4;
    private static final QName R$6;
    private static final QName CONNECTIONID$8;
    
    public CTSingleXmlCellImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTXmlCellPr getXmlCellPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTXmlCellPr ctXmlCellPr = (CTXmlCellPr)this.get_store().find_element_user(CTSingleXmlCellImpl.XMLCELLPR$0, 0);
            if (ctXmlCellPr == null) {
                return null;
            }
            return ctXmlCellPr;
        }
    }
    
    public void setXmlCellPr(final CTXmlCellPr ctXmlCellPr) {
        this.generatedSetterHelperImpl((XmlObject)ctXmlCellPr, CTSingleXmlCellImpl.XMLCELLPR$0, 0, (short)1);
    }
    
    public CTXmlCellPr addNewXmlCellPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXmlCellPr)this.get_store().add_element_user(CTSingleXmlCellImpl.XMLCELLPR$0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSingleXmlCellImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSingleXmlCellImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSingleXmlCellImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSingleXmlCellImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSingleXmlCellImpl.EXTLST$2, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.ID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSingleXmlCellImpl.ID$4);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSingleXmlCellImpl.ID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSingleXmlCellImpl.ID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSingleXmlCellImpl.ID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.R$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTSingleXmlCellImpl.R$6);
        }
    }
    
    public void setR(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.R$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSingleXmlCellImpl.R$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetR(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTSingleXmlCellImpl.R$6);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTSingleXmlCellImpl.R$6);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public long getConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
        }
    }
    
    public void setConnectionId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetConnectionId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSingleXmlCellImpl.CONNECTIONID$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        XMLCELLPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "xmlCellPr");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        ID$4 = new QName("", "id");
        R$6 = new QName("", "r");
        CONNECTIONID$8 = new QName("", "connectionId");
    }
}
