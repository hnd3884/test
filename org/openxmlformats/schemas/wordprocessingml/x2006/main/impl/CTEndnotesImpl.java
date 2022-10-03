package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEndnotes;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEndnotesImpl extends XmlComplexContentImpl implements CTEndnotes
{
    private static final long serialVersionUID = 1L;
    private static final QName ENDNOTE$0;
    
    public CTEndnotesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFtnEdn> getEndnoteList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EndnoteList extends AbstractList<CTFtnEdn>
            {
                @Override
                public CTFtnEdn get(final int n) {
                    return CTEndnotesImpl.this.getEndnoteArray(n);
                }
                
                @Override
                public CTFtnEdn set(final int n, final CTFtnEdn ctFtnEdn) {
                    final CTFtnEdn endnoteArray = CTEndnotesImpl.this.getEndnoteArray(n);
                    CTEndnotesImpl.this.setEndnoteArray(n, ctFtnEdn);
                    return endnoteArray;
                }
                
                @Override
                public void add(final int n, final CTFtnEdn ctFtnEdn) {
                    CTEndnotesImpl.this.insertNewEndnote(n).set((XmlObject)ctFtnEdn);
                }
                
                @Override
                public CTFtnEdn remove(final int n) {
                    final CTFtnEdn endnoteArray = CTEndnotesImpl.this.getEndnoteArray(n);
                    CTEndnotesImpl.this.removeEndnote(n);
                    return endnoteArray;
                }
                
                @Override
                public int size() {
                    return CTEndnotesImpl.this.sizeOfEndnoteArray();
                }
            }
            return new EndnoteList();
        }
    }
    
    @Deprecated
    public CTFtnEdn[] getEndnoteArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEndnotesImpl.ENDNOTE$0, (List)list);
            final CTFtnEdn[] array = new CTFtnEdn[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFtnEdn getEndnoteArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnEdn ctFtnEdn = (CTFtnEdn)this.get_store().find_element_user(CTEndnotesImpl.ENDNOTE$0, n);
            if (ctFtnEdn == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFtnEdn;
        }
    }
    
    public int sizeOfEndnoteArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEndnotesImpl.ENDNOTE$0);
        }
    }
    
    public void setEndnoteArray(final CTFtnEdn[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEndnotesImpl.ENDNOTE$0);
    }
    
    public void setEndnoteArray(final int n, final CTFtnEdn ctFtnEdn) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnEdn, CTEndnotesImpl.ENDNOTE$0, n, (short)2);
    }
    
    public CTFtnEdn insertNewEndnote(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdn)this.get_store().insert_element_user(CTEndnotesImpl.ENDNOTE$0, n);
        }
    }
    
    public CTFtnEdn addNewEndnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdn)this.get_store().add_element_user(CTEndnotesImpl.ENDNOTE$0);
        }
    }
    
    public void removeEndnote(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEndnotesImpl.ENDNOTE$0, n);
        }
    }
    
    static {
        ENDNOTE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnote");
    }
}
