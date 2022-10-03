package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCol;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableColImpl extends XmlComplexContentImpl implements CTTableCol
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName W$2;
    
    public CTTableColImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableColImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableColImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableColImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColImpl.EXTLST$0, 0);
        }
    }
    
    public long getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColImpl.W$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTTableColImpl.W$2);
        }
    }
    
    public void setW(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColImpl.W$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColImpl.W$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetW(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTTableColImpl.W$2);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTTableColImpl.W$2);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        W$2 = new QName("", "w");
    }
}
