package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumImpl extends XmlComplexContentImpl implements CTNum
{
    private static final long serialVersionUID = 1L;
    private static final QName ABSTRACTNUMID$0;
    private static final QName LVLOVERRIDE$2;
    private static final QName NUMID$4;
    
    public CTNumImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDecimalNumber getAbstractNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTNumImpl.ABSTRACTNUMID$0, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public void setAbstractNumId(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTNumImpl.ABSTRACTNUMID$0, 0, (short)1);
    }
    
    public CTDecimalNumber addNewAbstractNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTNumImpl.ABSTRACTNUMID$0);
        }
    }
    
    public List<CTNumLvl> getLvlOverrideList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LvlOverrideList extends AbstractList<CTNumLvl>
            {
                @Override
                public CTNumLvl get(final int n) {
                    return CTNumImpl.this.getLvlOverrideArray(n);
                }
                
                @Override
                public CTNumLvl set(final int n, final CTNumLvl ctNumLvl) {
                    final CTNumLvl lvlOverrideArray = CTNumImpl.this.getLvlOverrideArray(n);
                    CTNumImpl.this.setLvlOverrideArray(n, ctNumLvl);
                    return lvlOverrideArray;
                }
                
                @Override
                public void add(final int n, final CTNumLvl ctNumLvl) {
                    CTNumImpl.this.insertNewLvlOverride(n).set((XmlObject)ctNumLvl);
                }
                
                @Override
                public CTNumLvl remove(final int n) {
                    final CTNumLvl lvlOverrideArray = CTNumImpl.this.getLvlOverrideArray(n);
                    CTNumImpl.this.removeLvlOverride(n);
                    return lvlOverrideArray;
                }
                
                @Override
                public int size() {
                    return CTNumImpl.this.sizeOfLvlOverrideArray();
                }
            }
            return new LvlOverrideList();
        }
    }
    
    @Deprecated
    public CTNumLvl[] getLvlOverrideArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTNumImpl.LVLOVERRIDE$2, (List)list);
            final CTNumLvl[] array = new CTNumLvl[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNumLvl getLvlOverrideArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumLvl ctNumLvl = (CTNumLvl)this.get_store().find_element_user(CTNumImpl.LVLOVERRIDE$2, n);
            if (ctNumLvl == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNumLvl;
        }
    }
    
    public int sizeOfLvlOverrideArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumImpl.LVLOVERRIDE$2);
        }
    }
    
    public void setLvlOverrideArray(final CTNumLvl[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTNumImpl.LVLOVERRIDE$2);
    }
    
    public void setLvlOverrideArray(final int n, final CTNumLvl ctNumLvl) {
        this.generatedSetterHelperImpl((XmlObject)ctNumLvl, CTNumImpl.LVLOVERRIDE$2, n, (short)2);
    }
    
    public CTNumLvl insertNewLvlOverride(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumLvl)this.get_store().insert_element_user(CTNumImpl.LVLOVERRIDE$2, n);
        }
    }
    
    public CTNumLvl addNewLvlOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumLvl)this.get_store().add_element_user(CTNumImpl.LVLOVERRIDE$2);
        }
    }
    
    public void removeLvlOverride(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumImpl.LVLOVERRIDE$2, n);
        }
    }
    
    public BigInteger getNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumImpl.NUMID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTNumImpl.NUMID$4);
        }
    }
    
    public void setNumId(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumImpl.NUMID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumImpl.NUMID$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetNumId(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTNumImpl.NUMID$4);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTNumImpl.NUMID$4);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    static {
        ABSTRACTNUMID$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "abstractNumId");
        LVLOVERRIDE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvlOverride");
        NUMID$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numId");
    }
}
