package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumRefImpl extends XmlComplexContentImpl implements CTNumRef
{
    private static final long serialVersionUID = 1L;
    private static final QName F$0;
    private static final QName NUMCACHE$2;
    private static final QName EXTLST$4;
    
    public CTNumRefImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumRefImpl.F$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTNumRefImpl.F$0, 0);
        }
    }
    
    public void setF(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumRefImpl.F$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTNumRefImpl.F$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetF(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTNumRefImpl.F$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTNumRefImpl.F$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public CTNumData getNumCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumData ctNumData = (CTNumData)this.get_store().find_element_user(CTNumRefImpl.NUMCACHE$2, 0);
            if (ctNumData == null) {
                return null;
            }
            return ctNumData;
        }
    }
    
    public boolean isSetNumCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumRefImpl.NUMCACHE$2) != 0;
        }
    }
    
    public void setNumCache(final CTNumData ctNumData) {
        this.generatedSetterHelperImpl((XmlObject)ctNumData, CTNumRefImpl.NUMCACHE$2, 0, (short)1);
    }
    
    public CTNumData addNewNumCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumData)this.get_store().add_element_user(CTNumRefImpl.NUMCACHE$2);
        }
    }
    
    public void unsetNumCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumRefImpl.NUMCACHE$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTNumRefImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumRefImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNumRefImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTNumRefImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumRefImpl.EXTLST$4, 0);
        }
    }
    
    static {
        F$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "f");
        NUMCACHE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numCache");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
