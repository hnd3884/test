package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMultiLevelType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAbstractNumImpl extends XmlComplexContentImpl implements CTAbstractNum
{
    private static final long serialVersionUID = 1L;
    private static final QName NSID$0;
    private static final QName MULTILEVELTYPE$2;
    private static final QName TMPL$4;
    private static final QName NAME$6;
    private static final QName STYLELINK$8;
    private static final QName NUMSTYLELINK$10;
    private static final QName LVL$12;
    private static final QName ABSTRACTNUMID$14;
    
    public CTAbstractNumImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLongHexNumber getNsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLongHexNumber ctLongHexNumber = (CTLongHexNumber)this.get_store().find_element_user(CTAbstractNumImpl.NSID$0, 0);
            if (ctLongHexNumber == null) {
                return null;
            }
            return ctLongHexNumber;
        }
    }
    
    public boolean isSetNsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.NSID$0) != 0;
        }
    }
    
    public void setNsid(final CTLongHexNumber ctLongHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctLongHexNumber, CTAbstractNumImpl.NSID$0, 0, (short)1);
    }
    
    public CTLongHexNumber addNewNsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLongHexNumber)this.get_store().add_element_user(CTAbstractNumImpl.NSID$0);
        }
    }
    
    public void unsetNsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.NSID$0, 0);
        }
    }
    
    public CTMultiLevelType getMultiLevelType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMultiLevelType ctMultiLevelType = (CTMultiLevelType)this.get_store().find_element_user(CTAbstractNumImpl.MULTILEVELTYPE$2, 0);
            if (ctMultiLevelType == null) {
                return null;
            }
            return ctMultiLevelType;
        }
    }
    
    public boolean isSetMultiLevelType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.MULTILEVELTYPE$2) != 0;
        }
    }
    
    public void setMultiLevelType(final CTMultiLevelType ctMultiLevelType) {
        this.generatedSetterHelperImpl((XmlObject)ctMultiLevelType, CTAbstractNumImpl.MULTILEVELTYPE$2, 0, (short)1);
    }
    
    public CTMultiLevelType addNewMultiLevelType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMultiLevelType)this.get_store().add_element_user(CTAbstractNumImpl.MULTILEVELTYPE$2);
        }
    }
    
    public void unsetMultiLevelType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.MULTILEVELTYPE$2, 0);
        }
    }
    
    public CTLongHexNumber getTmpl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLongHexNumber ctLongHexNumber = (CTLongHexNumber)this.get_store().find_element_user(CTAbstractNumImpl.TMPL$4, 0);
            if (ctLongHexNumber == null) {
                return null;
            }
            return ctLongHexNumber;
        }
    }
    
    public boolean isSetTmpl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.TMPL$4) != 0;
        }
    }
    
    public void setTmpl(final CTLongHexNumber ctLongHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctLongHexNumber, CTAbstractNumImpl.TMPL$4, 0, (short)1);
    }
    
    public CTLongHexNumber addNewTmpl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLongHexNumber)this.get_store().add_element_user(CTAbstractNumImpl.TMPL$4);
        }
    }
    
    public void unsetTmpl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.TMPL$4, 0);
        }
    }
    
    public CTString getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTAbstractNumImpl.NAME$6, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.NAME$6) != 0;
        }
    }
    
    public void setName(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTAbstractNumImpl.NAME$6, 0, (short)1);
    }
    
    public CTString addNewName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTAbstractNumImpl.NAME$6);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.NAME$6, 0);
        }
    }
    
    public CTString getStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTAbstractNumImpl.STYLELINK$8, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.STYLELINK$8) != 0;
        }
    }
    
    public void setStyleLink(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTAbstractNumImpl.STYLELINK$8, 0, (short)1);
    }
    
    public CTString addNewStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTAbstractNumImpl.STYLELINK$8);
        }
    }
    
    public void unsetStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.STYLELINK$8, 0);
        }
    }
    
    public CTString getNumStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTAbstractNumImpl.NUMSTYLELINK$10, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetNumStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.NUMSTYLELINK$10) != 0;
        }
    }
    
    public void setNumStyleLink(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTAbstractNumImpl.NUMSTYLELINK$10, 0, (short)1);
    }
    
    public CTString addNewNumStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTAbstractNumImpl.NUMSTYLELINK$10);
        }
    }
    
    public void unsetNumStyleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.NUMSTYLELINK$10, 0);
        }
    }
    
    public List<CTLvl> getLvlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LvlList extends AbstractList<CTLvl>
            {
                @Override
                public CTLvl get(final int n) {
                    return CTAbstractNumImpl.this.getLvlArray(n);
                }
                
                @Override
                public CTLvl set(final int n, final CTLvl ctLvl) {
                    final CTLvl lvlArray = CTAbstractNumImpl.this.getLvlArray(n);
                    CTAbstractNumImpl.this.setLvlArray(n, ctLvl);
                    return lvlArray;
                }
                
                @Override
                public void add(final int n, final CTLvl ctLvl) {
                    CTAbstractNumImpl.this.insertNewLvl(n).set((XmlObject)ctLvl);
                }
                
                @Override
                public CTLvl remove(final int n) {
                    final CTLvl lvlArray = CTAbstractNumImpl.this.getLvlArray(n);
                    CTAbstractNumImpl.this.removeLvl(n);
                    return lvlArray;
                }
                
                @Override
                public int size() {
                    return CTAbstractNumImpl.this.sizeOfLvlArray();
                }
            }
            return new LvlList();
        }
    }
    
    @Deprecated
    public CTLvl[] getLvlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAbstractNumImpl.LVL$12, (List)list);
            final CTLvl[] array = new CTLvl[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLvl getLvlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLvl ctLvl = (CTLvl)this.get_store().find_element_user(CTAbstractNumImpl.LVL$12, n);
            if (ctLvl == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLvl;
        }
    }
    
    public int sizeOfLvlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAbstractNumImpl.LVL$12);
        }
    }
    
    public void setLvlArray(final CTLvl[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAbstractNumImpl.LVL$12);
    }
    
    public void setLvlArray(final int n, final CTLvl ctLvl) {
        this.generatedSetterHelperImpl((XmlObject)ctLvl, CTAbstractNumImpl.LVL$12, n, (short)2);
    }
    
    public CTLvl insertNewLvl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLvl)this.get_store().insert_element_user(CTAbstractNumImpl.LVL$12, n);
        }
    }
    
    public CTLvl addNewLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLvl)this.get_store().add_element_user(CTAbstractNumImpl.LVL$12);
        }
    }
    
    public void removeLvl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAbstractNumImpl.LVL$12, n);
        }
    }
    
    public BigInteger getAbstractNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetAbstractNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
        }
    }
    
    public void setAbstractNumId(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetAbstractNumId(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTAbstractNumImpl.ABSTRACTNUMID$14);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    static {
        NSID$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "nsid");
        MULTILEVELTYPE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "multiLevelType");
        TMPL$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tmpl");
        NAME$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name");
        STYLELINK$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleLink");
        NUMSTYLELINK$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numStyleLink");
        LVL$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvl");
        ABSTRACTNUMID$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "abstractNumId");
    }
}
