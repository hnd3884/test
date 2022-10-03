package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import java.math.BigInteger;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLatentStylesImpl extends XmlComplexContentImpl implements CTLatentStyles
{
    private static final long serialVersionUID = 1L;
    private static final QName LSDEXCEPTION$0;
    private static final QName DEFLOCKEDSTATE$2;
    private static final QName DEFUIPRIORITY$4;
    private static final QName DEFSEMIHIDDEN$6;
    private static final QName DEFUNHIDEWHENUSED$8;
    private static final QName DEFQFORMAT$10;
    private static final QName COUNT$12;
    
    public CTLatentStylesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTLsdException> getLsdExceptionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LsdExceptionList extends AbstractList<CTLsdException>
            {
                @Override
                public CTLsdException get(final int n) {
                    return CTLatentStylesImpl.this.getLsdExceptionArray(n);
                }
                
                @Override
                public CTLsdException set(final int n, final CTLsdException ex) {
                    final CTLsdException lsdExceptionArray = CTLatentStylesImpl.this.getLsdExceptionArray(n);
                    CTLatentStylesImpl.this.setLsdExceptionArray(n, ex);
                    return lsdExceptionArray;
                }
                
                @Override
                public void add(final int n, final CTLsdException ex) {
                    CTLatentStylesImpl.this.insertNewLsdException(n).set((XmlObject)ex);
                }
                
                @Override
                public CTLsdException remove(final int n) {
                    final CTLsdException lsdExceptionArray = CTLatentStylesImpl.this.getLsdExceptionArray(n);
                    CTLatentStylesImpl.this.removeLsdException(n);
                    return lsdExceptionArray;
                }
                
                @Override
                public int size() {
                    return CTLatentStylesImpl.this.sizeOfLsdExceptionArray();
                }
            }
            return new LsdExceptionList();
        }
    }
    
    @Deprecated
    public CTLsdException[] getLsdExceptionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTLatentStylesImpl.LSDEXCEPTION$0, (List)list);
            final CTLsdException[] array = new CTLsdException[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLsdException getLsdExceptionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLsdException ex = (CTLsdException)this.get_store().find_element_user(CTLatentStylesImpl.LSDEXCEPTION$0, n);
            if (ex == null) {
                throw new IndexOutOfBoundsException();
            }
            return ex;
        }
    }
    
    public int sizeOfLsdExceptionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLatentStylesImpl.LSDEXCEPTION$0);
        }
    }
    
    public void setLsdExceptionArray(final CTLsdException[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLatentStylesImpl.LSDEXCEPTION$0);
    }
    
    public void setLsdExceptionArray(final int n, final CTLsdException ex) {
        this.generatedSetterHelperImpl((XmlObject)ex, CTLatentStylesImpl.LSDEXCEPTION$0, n, (short)2);
    }
    
    public CTLsdException insertNewLsdException(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLsdException)this.get_store().insert_element_user(CTLatentStylesImpl.LSDEXCEPTION$0, n);
        }
    }
    
    public CTLsdException addNewLsdException() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLsdException)this.get_store().add_element_user(CTLatentStylesImpl.LSDEXCEPTION$0);
        }
    }
    
    public void removeLsdException(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLatentStylesImpl.LSDEXCEPTION$0, n);
        }
    }
    
    public STOnOff.Enum getDefLockedState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDefLockedState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
        }
    }
    
    public boolean isSetDefLockedState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2) != null;
        }
    }
    
    public void setDefLockedState(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefLockedState(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDefLockedState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.DEFLOCKEDSTATE$2);
        }
    }
    
    public BigInteger getDefUIPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetDefUIPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
        }
    }
    
    public boolean isSetDefUIPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4) != null;
        }
    }
    
    public void setDefUIPriority(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDefUIPriority(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFUIPRIORITY$4);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetDefUIPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.DEFUIPRIORITY$4);
        }
    }
    
    public STOnOff.Enum getDefSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDefSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
        }
    }
    
    public boolean isSetDefSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6) != null;
        }
    }
    
    public void setDefSemiHidden(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefSemiHidden(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDefSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.DEFSEMIHIDDEN$6);
        }
    }
    
    public STOnOff.Enum getDefUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDefUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
        }
    }
    
    public boolean isSetDefUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8) != null;
        }
    }
    
    public void setDefUnhideWhenUsed(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefUnhideWhenUsed(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDefUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.DEFUNHIDEWHENUSED$8);
        }
    }
    
    public STOnOff.Enum getDefQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDefQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
        }
    }
    
    public boolean isSetDefQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10) != null;
        }
    }
    
    public void setDefQFormat(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefQFormat(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLatentStylesImpl.DEFQFORMAT$10);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDefQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.DEFQFORMAT$10);
        }
    }
    
    public BigInteger getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.COUNT$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTLatentStylesImpl.COUNT$12);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLatentStylesImpl.COUNT$12) != null;
        }
    }
    
    public void setCount(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLatentStylesImpl.COUNT$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLatentStylesImpl.COUNT$12);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetCount(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTLatentStylesImpl.COUNT$12);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTLatentStylesImpl.COUNT$12);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLatentStylesImpl.COUNT$12);
        }
    }
    
    static {
        LSDEXCEPTION$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lsdException");
        DEFLOCKEDSTATE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defLockedState");
        DEFUIPRIORITY$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defUIPriority");
        DEFSEMIHIDDEN$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defSemiHidden");
        DEFUNHIDEWHENUSED$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defUnhideWhenUsed");
        DEFQFORMAT$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defQFormat");
        COUNT$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "count");
    }
}
