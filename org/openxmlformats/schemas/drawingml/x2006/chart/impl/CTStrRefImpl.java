package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStrRefImpl extends XmlComplexContentImpl implements CTStrRef
{
    private static final long serialVersionUID = 1L;
    private static final QName F$0;
    private static final QName STRCACHE$2;
    private static final QName EXTLST$4;
    
    public CTStrRefImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTStrRefImpl.F$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTStrRefImpl.F$0, 0);
        }
    }
    
    public void setF(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTStrRefImpl.F$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTStrRefImpl.F$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetF(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTStrRefImpl.F$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTStrRefImpl.F$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public CTStrData getStrCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrData ctStrData = (CTStrData)this.get_store().find_element_user(CTStrRefImpl.STRCACHE$2, 0);
            if (ctStrData == null) {
                return null;
            }
            return ctStrData;
        }
    }
    
    public boolean isSetStrCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrRefImpl.STRCACHE$2) != 0;
        }
    }
    
    public void setStrCache(final CTStrData ctStrData) {
        this.generatedSetterHelperImpl((XmlObject)ctStrData, CTStrRefImpl.STRCACHE$2, 0, (short)1);
    }
    
    public CTStrData addNewStrCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrData)this.get_store().add_element_user(CTStrRefImpl.STRCACHE$2);
        }
    }
    
    public void unsetStrCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrRefImpl.STRCACHE$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTStrRefImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrRefImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStrRefImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTStrRefImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrRefImpl.EXTLST$4, 0);
        }
    }
    
    static {
        F$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "f");
        STRCACHE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "strCache");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
