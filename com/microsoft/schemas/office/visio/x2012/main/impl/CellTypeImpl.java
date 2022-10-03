package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.RefByType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CellTypeImpl extends XmlComplexContentImpl implements CellType
{
    private static final long serialVersionUID = 1L;
    private static final QName REFBY$0;
    private static final QName N$2;
    private static final QName U$4;
    private static final QName E$6;
    private static final QName F$8;
    private static final QName V$10;
    
    public CellTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<RefByType> getRefByList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RefByList extends AbstractList<RefByType>
            {
                @Override
                public RefByType get(final int n) {
                    return CellTypeImpl.this.getRefByArray(n);
                }
                
                @Override
                public RefByType set(final int n, final RefByType refByType) {
                    final RefByType refByArray = CellTypeImpl.this.getRefByArray(n);
                    CellTypeImpl.this.setRefByArray(n, refByType);
                    return refByArray;
                }
                
                @Override
                public void add(final int n, final RefByType refByType) {
                    CellTypeImpl.this.insertNewRefBy(n).set((XmlObject)refByType);
                }
                
                @Override
                public RefByType remove(final int n) {
                    final RefByType refByArray = CellTypeImpl.this.getRefByArray(n);
                    CellTypeImpl.this.removeRefBy(n);
                    return refByArray;
                }
                
                @Override
                public int size() {
                    return CellTypeImpl.this.sizeOfRefByArray();
                }
            }
            return new RefByList();
        }
    }
    
    @Deprecated
    public RefByType[] getRefByArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CellTypeImpl.REFBY$0, (List)list);
            final RefByType[] array = new RefByType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public RefByType getRefByArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RefByType refByType = (RefByType)this.get_store().find_element_user(CellTypeImpl.REFBY$0, n);
            if (refByType == null) {
                throw new IndexOutOfBoundsException();
            }
            return refByType;
        }
    }
    
    public int sizeOfRefByArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CellTypeImpl.REFBY$0);
        }
    }
    
    public void setRefByArray(final RefByType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CellTypeImpl.REFBY$0);
    }
    
    public void setRefByArray(final int n, final RefByType refByType) {
        this.generatedSetterHelperImpl((XmlObject)refByType, CellTypeImpl.REFBY$0, n, (short)2);
    }
    
    public RefByType insertNewRefBy(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RefByType)this.get_store().insert_element_user(CellTypeImpl.REFBY$0, n);
        }
    }
    
    public RefByType addNewRefBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RefByType)this.get_store().add_element_user(CellTypeImpl.REFBY$0);
        }
    }
    
    public void removeRefBy(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CellTypeImpl.REFBY$0, n);
        }
    }
    
    public String getN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.N$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CellTypeImpl.N$2);
        }
    }
    
    public void setN(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.N$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CellTypeImpl.N$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetN(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CellTypeImpl.N$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CellTypeImpl.N$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public String getU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.U$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CellTypeImpl.U$4);
        }
    }
    
    public boolean isSetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CellTypeImpl.U$4) != null;
        }
    }
    
    public void setU(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.U$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CellTypeImpl.U$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetU(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CellTypeImpl.U$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CellTypeImpl.U$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CellTypeImpl.U$4);
        }
    }
    
    public String getE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.E$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CellTypeImpl.E$6);
        }
    }
    
    public boolean isSetE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CellTypeImpl.E$6) != null;
        }
    }
    
    public void setE(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.E$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CellTypeImpl.E$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetE(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CellTypeImpl.E$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CellTypeImpl.E$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CellTypeImpl.E$6);
        }
    }
    
    public String getF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.F$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CellTypeImpl.F$8);
        }
    }
    
    public boolean isSetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CellTypeImpl.F$8) != null;
        }
    }
    
    public void setF(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.F$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CellTypeImpl.F$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetF(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CellTypeImpl.F$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CellTypeImpl.F$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CellTypeImpl.F$8);
        }
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.V$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CellTypeImpl.V$10);
        }
    }
    
    public boolean isSetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CellTypeImpl.V$10) != null;
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CellTypeImpl.V$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CellTypeImpl.V$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CellTypeImpl.V$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CellTypeImpl.V$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CellTypeImpl.V$10);
        }
    }
    
    static {
        REFBY$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "RefBy");
        N$2 = new QName("", "N");
        U$4 = new QName("", "U");
        E$6 = new QName("", "E");
        F$8 = new QName("", "F");
        V$10 = new QName("", "V");
    }
}
