package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.FldType;
import com.microsoft.schemas.office.visio.x2012.main.TpType;
import com.microsoft.schemas.office.visio.x2012.main.PpType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.CpType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.TextType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TextTypeImpl extends XmlComplexContentImpl implements TextType
{
    private static final long serialVersionUID = 1L;
    private static final QName CP$0;
    private static final QName PP$2;
    private static final QName TP$4;
    private static final QName FLD$6;
    
    public TextTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CpType> getCpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CpList extends AbstractList<CpType>
            {
                @Override
                public CpType get(final int n) {
                    return TextTypeImpl.this.getCpArray(n);
                }
                
                @Override
                public CpType set(final int n, final CpType cpType) {
                    final CpType cpArray = TextTypeImpl.this.getCpArray(n);
                    TextTypeImpl.this.setCpArray(n, cpType);
                    return cpArray;
                }
                
                @Override
                public void add(final int n, final CpType cpType) {
                    TextTypeImpl.this.insertNewCp(n).set((XmlObject)cpType);
                }
                
                @Override
                public CpType remove(final int n) {
                    final CpType cpArray = TextTypeImpl.this.getCpArray(n);
                    TextTypeImpl.this.removeCp(n);
                    return cpArray;
                }
                
                @Override
                public int size() {
                    return TextTypeImpl.this.sizeOfCpArray();
                }
            }
            return new CpList();
        }
    }
    
    @Deprecated
    public CpType[] getCpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TextTypeImpl.CP$0, (List)list);
            final CpType[] array = new CpType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CpType getCpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CpType cpType = (CpType)this.get_store().find_element_user(TextTypeImpl.CP$0, n);
            if (cpType == null) {
                throw new IndexOutOfBoundsException();
            }
            return cpType;
        }
    }
    
    public int sizeOfCpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TextTypeImpl.CP$0);
        }
    }
    
    public void setCpArray(final CpType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, TextTypeImpl.CP$0);
    }
    
    public void setCpArray(final int n, final CpType cpType) {
        this.generatedSetterHelperImpl((XmlObject)cpType, TextTypeImpl.CP$0, n, (short)2);
    }
    
    public CpType insertNewCp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CpType)this.get_store().insert_element_user(TextTypeImpl.CP$0, n);
        }
    }
    
    public CpType addNewCp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CpType)this.get_store().add_element_user(TextTypeImpl.CP$0);
        }
    }
    
    public void removeCp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TextTypeImpl.CP$0, n);
        }
    }
    
    public List<PpType> getPpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PpList extends AbstractList<PpType>
            {
                @Override
                public PpType get(final int n) {
                    return TextTypeImpl.this.getPpArray(n);
                }
                
                @Override
                public PpType set(final int n, final PpType ppType) {
                    final PpType ppArray = TextTypeImpl.this.getPpArray(n);
                    TextTypeImpl.this.setPpArray(n, ppType);
                    return ppArray;
                }
                
                @Override
                public void add(final int n, final PpType ppType) {
                    TextTypeImpl.this.insertNewPp(n).set((XmlObject)ppType);
                }
                
                @Override
                public PpType remove(final int n) {
                    final PpType ppArray = TextTypeImpl.this.getPpArray(n);
                    TextTypeImpl.this.removePp(n);
                    return ppArray;
                }
                
                @Override
                public int size() {
                    return TextTypeImpl.this.sizeOfPpArray();
                }
            }
            return new PpList();
        }
    }
    
    @Deprecated
    public PpType[] getPpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TextTypeImpl.PP$2, (List)list);
            final PpType[] array = new PpType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public PpType getPpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PpType ppType = (PpType)this.get_store().find_element_user(TextTypeImpl.PP$2, n);
            if (ppType == null) {
                throw new IndexOutOfBoundsException();
            }
            return ppType;
        }
    }
    
    public int sizeOfPpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TextTypeImpl.PP$2);
        }
    }
    
    public void setPpArray(final PpType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, TextTypeImpl.PP$2);
    }
    
    public void setPpArray(final int n, final PpType ppType) {
        this.generatedSetterHelperImpl((XmlObject)ppType, TextTypeImpl.PP$2, n, (short)2);
    }
    
    public PpType insertNewPp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PpType)this.get_store().insert_element_user(TextTypeImpl.PP$2, n);
        }
    }
    
    public PpType addNewPp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PpType)this.get_store().add_element_user(TextTypeImpl.PP$2);
        }
    }
    
    public void removePp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TextTypeImpl.PP$2, n);
        }
    }
    
    public List<TpType> getTpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TpList extends AbstractList<TpType>
            {
                @Override
                public TpType get(final int n) {
                    return TextTypeImpl.this.getTpArray(n);
                }
                
                @Override
                public TpType set(final int n, final TpType tpType) {
                    final TpType tpArray = TextTypeImpl.this.getTpArray(n);
                    TextTypeImpl.this.setTpArray(n, tpType);
                    return tpArray;
                }
                
                @Override
                public void add(final int n, final TpType tpType) {
                    TextTypeImpl.this.insertNewTp(n).set((XmlObject)tpType);
                }
                
                @Override
                public TpType remove(final int n) {
                    final TpType tpArray = TextTypeImpl.this.getTpArray(n);
                    TextTypeImpl.this.removeTp(n);
                    return tpArray;
                }
                
                @Override
                public int size() {
                    return TextTypeImpl.this.sizeOfTpArray();
                }
            }
            return new TpList();
        }
    }
    
    @Deprecated
    public TpType[] getTpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TextTypeImpl.TP$4, (List)list);
            final TpType[] array = new TpType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public TpType getTpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TpType tpType = (TpType)this.get_store().find_element_user(TextTypeImpl.TP$4, n);
            if (tpType == null) {
                throw new IndexOutOfBoundsException();
            }
            return tpType;
        }
    }
    
    public int sizeOfTpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TextTypeImpl.TP$4);
        }
    }
    
    public void setTpArray(final TpType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, TextTypeImpl.TP$4);
    }
    
    public void setTpArray(final int n, final TpType tpType) {
        this.generatedSetterHelperImpl((XmlObject)tpType, TextTypeImpl.TP$4, n, (short)2);
    }
    
    public TpType insertNewTp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TpType)this.get_store().insert_element_user(TextTypeImpl.TP$4, n);
        }
    }
    
    public TpType addNewTp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TpType)this.get_store().add_element_user(TextTypeImpl.TP$4);
        }
    }
    
    public void removeTp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TextTypeImpl.TP$4, n);
        }
    }
    
    public List<FldType> getFldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldList extends AbstractList<FldType>
            {
                @Override
                public FldType get(final int n) {
                    return TextTypeImpl.this.getFldArray(n);
                }
                
                @Override
                public FldType set(final int n, final FldType fldType) {
                    final FldType fldArray = TextTypeImpl.this.getFldArray(n);
                    TextTypeImpl.this.setFldArray(n, fldType);
                    return fldArray;
                }
                
                @Override
                public void add(final int n, final FldType fldType) {
                    TextTypeImpl.this.insertNewFld(n).set((XmlObject)fldType);
                }
                
                @Override
                public FldType remove(final int n) {
                    final FldType fldArray = TextTypeImpl.this.getFldArray(n);
                    TextTypeImpl.this.removeFld(n);
                    return fldArray;
                }
                
                @Override
                public int size() {
                    return TextTypeImpl.this.sizeOfFldArray();
                }
            }
            return new FldList();
        }
    }
    
    @Deprecated
    public FldType[] getFldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TextTypeImpl.FLD$6, (List)list);
            final FldType[] array = new FldType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public FldType getFldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final FldType fldType = (FldType)this.get_store().find_element_user(TextTypeImpl.FLD$6, n);
            if (fldType == null) {
                throw new IndexOutOfBoundsException();
            }
            return fldType;
        }
    }
    
    public int sizeOfFldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TextTypeImpl.FLD$6);
        }
    }
    
    public void setFldArray(final FldType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, TextTypeImpl.FLD$6);
    }
    
    public void setFldArray(final int n, final FldType fldType) {
        this.generatedSetterHelperImpl((XmlObject)fldType, TextTypeImpl.FLD$6, n, (short)2);
    }
    
    public FldType insertNewFld(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (FldType)this.get_store().insert_element_user(TextTypeImpl.FLD$6, n);
        }
    }
    
    public FldType addNewFld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (FldType)this.get_store().add_element_user(TextTypeImpl.FLD$6);
        }
    }
    
    public void removeFld(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TextTypeImpl.FLD$6, n);
        }
    }
    
    static {
        CP$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "cp");
        PP$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "pp");
        TP$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "tp");
        FLD$6 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "fld");
    }
}
