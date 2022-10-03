package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPicBullet;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumberingImpl extends XmlComplexContentImpl implements CTNumbering
{
    private static final long serialVersionUID = 1L;
    private static final QName NUMPICBULLET$0;
    private static final QName ABSTRACTNUM$2;
    private static final QName NUM$4;
    private static final QName NUMIDMACATCLEANUP$6;
    
    public CTNumberingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTNumPicBullet> getNumPicBulletList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NumPicBulletList extends AbstractList<CTNumPicBullet>
            {
                @Override
                public CTNumPicBullet get(final int n) {
                    return CTNumberingImpl.this.getNumPicBulletArray(n);
                }
                
                @Override
                public CTNumPicBullet set(final int n, final CTNumPicBullet ctNumPicBullet) {
                    final CTNumPicBullet numPicBulletArray = CTNumberingImpl.this.getNumPicBulletArray(n);
                    CTNumberingImpl.this.setNumPicBulletArray(n, ctNumPicBullet);
                    return numPicBulletArray;
                }
                
                @Override
                public void add(final int n, final CTNumPicBullet ctNumPicBullet) {
                    CTNumberingImpl.this.insertNewNumPicBullet(n).set((XmlObject)ctNumPicBullet);
                }
                
                @Override
                public CTNumPicBullet remove(final int n) {
                    final CTNumPicBullet numPicBulletArray = CTNumberingImpl.this.getNumPicBulletArray(n);
                    CTNumberingImpl.this.removeNumPicBullet(n);
                    return numPicBulletArray;
                }
                
                @Override
                public int size() {
                    return CTNumberingImpl.this.sizeOfNumPicBulletArray();
                }
            }
            return new NumPicBulletList();
        }
    }
    
    @Deprecated
    public CTNumPicBullet[] getNumPicBulletArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTNumberingImpl.NUMPICBULLET$0, (List)list);
            final CTNumPicBullet[] array = new CTNumPicBullet[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNumPicBullet getNumPicBulletArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumPicBullet ctNumPicBullet = (CTNumPicBullet)this.get_store().find_element_user(CTNumberingImpl.NUMPICBULLET$0, n);
            if (ctNumPicBullet == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNumPicBullet;
        }
    }
    
    public int sizeOfNumPicBulletArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumberingImpl.NUMPICBULLET$0);
        }
    }
    
    public void setNumPicBulletArray(final CTNumPicBullet[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTNumberingImpl.NUMPICBULLET$0);
    }
    
    public void setNumPicBulletArray(final int n, final CTNumPicBullet ctNumPicBullet) {
        this.generatedSetterHelperImpl((XmlObject)ctNumPicBullet, CTNumberingImpl.NUMPICBULLET$0, n, (short)2);
    }
    
    public CTNumPicBullet insertNewNumPicBullet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumPicBullet)this.get_store().insert_element_user(CTNumberingImpl.NUMPICBULLET$0, n);
        }
    }
    
    public CTNumPicBullet addNewNumPicBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumPicBullet)this.get_store().add_element_user(CTNumberingImpl.NUMPICBULLET$0);
        }
    }
    
    public void removeNumPicBullet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumberingImpl.NUMPICBULLET$0, n);
        }
    }
    
    public List<CTAbstractNum> getAbstractNumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AbstractNumList extends AbstractList<CTAbstractNum>
            {
                @Override
                public CTAbstractNum get(final int n) {
                    return CTNumberingImpl.this.getAbstractNumArray(n);
                }
                
                @Override
                public CTAbstractNum set(final int n, final CTAbstractNum ctAbstractNum) {
                    final CTAbstractNum abstractNumArray = CTNumberingImpl.this.getAbstractNumArray(n);
                    CTNumberingImpl.this.setAbstractNumArray(n, ctAbstractNum);
                    return abstractNumArray;
                }
                
                @Override
                public void add(final int n, final CTAbstractNum ctAbstractNum) {
                    CTNumberingImpl.this.insertNewAbstractNum(n).set((XmlObject)ctAbstractNum);
                }
                
                @Override
                public CTAbstractNum remove(final int n) {
                    final CTAbstractNum abstractNumArray = CTNumberingImpl.this.getAbstractNumArray(n);
                    CTNumberingImpl.this.removeAbstractNum(n);
                    return abstractNumArray;
                }
                
                @Override
                public int size() {
                    return CTNumberingImpl.this.sizeOfAbstractNumArray();
                }
            }
            return new AbstractNumList();
        }
    }
    
    @Deprecated
    public CTAbstractNum[] getAbstractNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTNumberingImpl.ABSTRACTNUM$2, (List)list);
            final CTAbstractNum[] array = new CTAbstractNum[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAbstractNum getAbstractNumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAbstractNum ctAbstractNum = (CTAbstractNum)this.get_store().find_element_user(CTNumberingImpl.ABSTRACTNUM$2, n);
            if (ctAbstractNum == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAbstractNum;
        }
    }
    
    public int sizeOfAbstractNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumberingImpl.ABSTRACTNUM$2);
        }
    }
    
    public void setAbstractNumArray(final CTAbstractNum[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTNumberingImpl.ABSTRACTNUM$2);
    }
    
    public void setAbstractNumArray(final int n, final CTAbstractNum ctAbstractNum) {
        this.generatedSetterHelperImpl((XmlObject)ctAbstractNum, CTNumberingImpl.ABSTRACTNUM$2, n, (short)2);
    }
    
    public CTAbstractNum insertNewAbstractNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAbstractNum)this.get_store().insert_element_user(CTNumberingImpl.ABSTRACTNUM$2, n);
        }
    }
    
    public CTAbstractNum addNewAbstractNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAbstractNum)this.get_store().add_element_user(CTNumberingImpl.ABSTRACTNUM$2);
        }
    }
    
    public void removeAbstractNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumberingImpl.ABSTRACTNUM$2, n);
        }
    }
    
    public List<CTNum> getNumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NumList extends AbstractList<CTNum>
            {
                @Override
                public CTNum get(final int n) {
                    return CTNumberingImpl.this.getNumArray(n);
                }
                
                @Override
                public CTNum set(final int n, final CTNum ctNum) {
                    final CTNum numArray = CTNumberingImpl.this.getNumArray(n);
                    CTNumberingImpl.this.setNumArray(n, ctNum);
                    return numArray;
                }
                
                @Override
                public void add(final int n, final CTNum ctNum) {
                    CTNumberingImpl.this.insertNewNum(n).set((XmlObject)ctNum);
                }
                
                @Override
                public CTNum remove(final int n) {
                    final CTNum numArray = CTNumberingImpl.this.getNumArray(n);
                    CTNumberingImpl.this.removeNum(n);
                    return numArray;
                }
                
                @Override
                public int size() {
                    return CTNumberingImpl.this.sizeOfNumArray();
                }
            }
            return new NumList();
        }
    }
    
    @Deprecated
    public CTNum[] getNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTNumberingImpl.NUM$4, (List)list);
            final CTNum[] array = new CTNum[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNum getNumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNum ctNum = (CTNum)this.get_store().find_element_user(CTNumberingImpl.NUM$4, n);
            if (ctNum == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNum;
        }
    }
    
    public int sizeOfNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumberingImpl.NUM$4);
        }
    }
    
    public void setNumArray(final CTNum[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTNumberingImpl.NUM$4);
    }
    
    public void setNumArray(final int n, final CTNum ctNum) {
        this.generatedSetterHelperImpl((XmlObject)ctNum, CTNumberingImpl.NUM$4, n, (short)2);
    }
    
    public CTNum insertNewNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNum)this.get_store().insert_element_user(CTNumberingImpl.NUM$4, n);
        }
    }
    
    public CTNum addNewNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNum)this.get_store().add_element_user(CTNumberingImpl.NUM$4);
        }
    }
    
    public void removeNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumberingImpl.NUM$4, n);
        }
    }
    
    public CTDecimalNumber getNumIdMacAtCleanup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTNumberingImpl.NUMIDMACATCLEANUP$6, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetNumIdMacAtCleanup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumberingImpl.NUMIDMACATCLEANUP$6) != 0;
        }
    }
    
    public void setNumIdMacAtCleanup(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTNumberingImpl.NUMIDMACATCLEANUP$6, 0, (short)1);
    }
    
    public CTDecimalNumber addNewNumIdMacAtCleanup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTNumberingImpl.NUMIDMACATCLEANUP$6);
        }
    }
    
    public void unsetNumIdMacAtCleanup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumberingImpl.NUMIDMACATCLEANUP$6, 0);
        }
    }
    
    static {
        NUMPICBULLET$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numPicBullet");
        ABSTRACTNUM$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "abstractNum");
        NUM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "num");
        NUMIDMACATCLEANUP$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numIdMacAtCleanup");
    }
}
