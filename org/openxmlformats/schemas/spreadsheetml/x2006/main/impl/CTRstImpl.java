package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticRun;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRstImpl extends XmlComplexContentImpl implements CTRst
{
    private static final long serialVersionUID = 1L;
    private static final QName T$0;
    private static final QName R$2;
    private static final QName RPH$4;
    private static final QName PHONETICPR$6;
    
    public CTRstImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTRstImpl.T$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTRstImpl.T$0, 0);
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRstImpl.T$0) != 0;
        }
    }
    
    public void setT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTRstImpl.T$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTRstImpl.T$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetT(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTRstImpl.T$0, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTRstImpl.T$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRstImpl.T$0, 0);
        }
    }
    
    public List<CTRElt> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTRElt>
            {
                @Override
                public CTRElt get(final int n) {
                    return CTRstImpl.this.getRArray(n);
                }
                
                @Override
                public CTRElt set(final int n, final CTRElt ctrElt) {
                    final CTRElt rArray = CTRstImpl.this.getRArray(n);
                    CTRstImpl.this.setRArray(n, ctrElt);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTRElt ctrElt) {
                    CTRstImpl.this.insertNewR(n).set((XmlObject)ctrElt);
                }
                
                @Override
                public CTRElt remove(final int n) {
                    final CTRElt rArray = CTRstImpl.this.getRArray(n);
                    CTRstImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTRstImpl.this.sizeOfRArray();
                }
            }
            return new RList();
        }
    }
    
    @Deprecated
    public CTRElt[] getRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRstImpl.R$2, (List)list);
            final CTRElt[] array = new CTRElt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRElt getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRElt ctrElt = (CTRElt)this.get_store().find_element_user(CTRstImpl.R$2, n);
            if (ctrElt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctrElt;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRstImpl.R$2);
        }
    }
    
    public void setRArray(final CTRElt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRstImpl.R$2);
    }
    
    public void setRArray(final int n, final CTRElt ctrElt) {
        this.generatedSetterHelperImpl((XmlObject)ctrElt, CTRstImpl.R$2, n, (short)2);
    }
    
    public CTRElt insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRElt)this.get_store().insert_element_user(CTRstImpl.R$2, n);
        }
    }
    
    public CTRElt addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRElt)this.get_store().add_element_user(CTRstImpl.R$2);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRstImpl.R$2, n);
        }
    }
    
    public List<CTPhoneticRun> getRPhList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RPhList extends AbstractList<CTPhoneticRun>
            {
                @Override
                public CTPhoneticRun get(final int n) {
                    return CTRstImpl.this.getRPhArray(n);
                }
                
                @Override
                public CTPhoneticRun set(final int n, final CTPhoneticRun ctPhoneticRun) {
                    final CTPhoneticRun rPhArray = CTRstImpl.this.getRPhArray(n);
                    CTRstImpl.this.setRPhArray(n, ctPhoneticRun);
                    return rPhArray;
                }
                
                @Override
                public void add(final int n, final CTPhoneticRun ctPhoneticRun) {
                    CTRstImpl.this.insertNewRPh(n).set((XmlObject)ctPhoneticRun);
                }
                
                @Override
                public CTPhoneticRun remove(final int n) {
                    final CTPhoneticRun rPhArray = CTRstImpl.this.getRPhArray(n);
                    CTRstImpl.this.removeRPh(n);
                    return rPhArray;
                }
                
                @Override
                public int size() {
                    return CTRstImpl.this.sizeOfRPhArray();
                }
            }
            return new RPhList();
        }
    }
    
    @Deprecated
    public CTPhoneticRun[] getRPhArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRstImpl.RPH$4, (List)list);
            final CTPhoneticRun[] array = new CTPhoneticRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPhoneticRun getRPhArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPhoneticRun ctPhoneticRun = (CTPhoneticRun)this.get_store().find_element_user(CTRstImpl.RPH$4, n);
            if (ctPhoneticRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPhoneticRun;
        }
    }
    
    public int sizeOfRPhArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRstImpl.RPH$4);
        }
    }
    
    public void setRPhArray(final CTPhoneticRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRstImpl.RPH$4);
    }
    
    public void setRPhArray(final int n, final CTPhoneticRun ctPhoneticRun) {
        this.generatedSetterHelperImpl((XmlObject)ctPhoneticRun, CTRstImpl.RPH$4, n, (short)2);
    }
    
    public CTPhoneticRun insertNewRPh(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhoneticRun)this.get_store().insert_element_user(CTRstImpl.RPH$4, n);
        }
    }
    
    public CTPhoneticRun addNewRPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhoneticRun)this.get_store().add_element_user(CTRstImpl.RPH$4);
        }
    }
    
    public void removeRPh(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRstImpl.RPH$4, n);
        }
    }
    
    public CTPhoneticPr getPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPhoneticPr ctPhoneticPr = (CTPhoneticPr)this.get_store().find_element_user(CTRstImpl.PHONETICPR$6, 0);
            if (ctPhoneticPr == null) {
                return null;
            }
            return ctPhoneticPr;
        }
    }
    
    public boolean isSetPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRstImpl.PHONETICPR$6) != 0;
        }
    }
    
    public void setPhoneticPr(final CTPhoneticPr ctPhoneticPr) {
        this.generatedSetterHelperImpl((XmlObject)ctPhoneticPr, CTRstImpl.PHONETICPR$6, 0, (short)1);
    }
    
    public CTPhoneticPr addNewPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhoneticPr)this.get_store().add_element_user(CTRstImpl.PHONETICPR$6);
        }
    }
    
    public void unsetPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRstImpl.PHONETICPR$6, 0);
        }
    }
    
    static {
        T$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "t");
        R$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "r");
        RPH$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rPh");
        PHONETICPR$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "phoneticPr");
    }
}
