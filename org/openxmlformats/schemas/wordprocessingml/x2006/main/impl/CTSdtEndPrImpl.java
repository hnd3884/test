package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSdtEndPrImpl extends XmlComplexContentImpl implements CTSdtEndPr
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    
    public CTSdtEndPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRPr> getRPrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RPrList extends AbstractList<CTRPr>
            {
                @Override
                public CTRPr get(final int n) {
                    return CTSdtEndPrImpl.this.getRPrArray(n);
                }
                
                @Override
                public CTRPr set(final int n, final CTRPr ctrPr) {
                    final CTRPr rPrArray = CTSdtEndPrImpl.this.getRPrArray(n);
                    CTSdtEndPrImpl.this.setRPrArray(n, ctrPr);
                    return rPrArray;
                }
                
                @Override
                public void add(final int n, final CTRPr ctrPr) {
                    CTSdtEndPrImpl.this.insertNewRPr(n).set((XmlObject)ctrPr);
                }
                
                @Override
                public CTRPr remove(final int n) {
                    final CTRPr rPrArray = CTSdtEndPrImpl.this.getRPrArray(n);
                    CTSdtEndPrImpl.this.removeRPr(n);
                    return rPrArray;
                }
                
                @Override
                public int size() {
                    return CTSdtEndPrImpl.this.sizeOfRPrArray();
                }
            }
            return new RPrList();
        }
    }
    
    @Deprecated
    public CTRPr[] getRPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtEndPrImpl.RPR$0, (List)list);
            final CTRPr[] array = new CTRPr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRPr getRPrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTSdtEndPrImpl.RPR$0, n);
            if (ctrPr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctrPr;
        }
    }
    
    public int sizeOfRPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtEndPrImpl.RPR$0);
        }
    }
    
    public void setRPrArray(final CTRPr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtEndPrImpl.RPR$0);
    }
    
    public void setRPrArray(final int n, final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTSdtEndPrImpl.RPR$0, n, (short)2);
    }
    
    public CTRPr insertNewRPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().insert_element_user(CTSdtEndPrImpl.RPR$0, n);
        }
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTSdtEndPrImpl.RPR$0);
        }
    }
    
    public void removeRPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtEndPrImpl.RPR$0, n);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
    }
}
