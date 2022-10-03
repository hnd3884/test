package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFootnotesImpl extends XmlComplexContentImpl implements CTFootnotes
{
    private static final long serialVersionUID = 1L;
    private static final QName FOOTNOTE$0;
    
    public CTFootnotesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFtnEdn> getFootnoteList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FootnoteList extends AbstractList<CTFtnEdn>
            {
                @Override
                public CTFtnEdn get(final int n) {
                    return CTFootnotesImpl.this.getFootnoteArray(n);
                }
                
                @Override
                public CTFtnEdn set(final int n, final CTFtnEdn ctFtnEdn) {
                    final CTFtnEdn footnoteArray = CTFootnotesImpl.this.getFootnoteArray(n);
                    CTFootnotesImpl.this.setFootnoteArray(n, ctFtnEdn);
                    return footnoteArray;
                }
                
                @Override
                public void add(final int n, final CTFtnEdn ctFtnEdn) {
                    CTFootnotesImpl.this.insertNewFootnote(n).set((XmlObject)ctFtnEdn);
                }
                
                @Override
                public CTFtnEdn remove(final int n) {
                    final CTFtnEdn footnoteArray = CTFootnotesImpl.this.getFootnoteArray(n);
                    CTFootnotesImpl.this.removeFootnote(n);
                    return footnoteArray;
                }
                
                @Override
                public int size() {
                    return CTFootnotesImpl.this.sizeOfFootnoteArray();
                }
            }
            return new FootnoteList();
        }
    }
    
    @Deprecated
    public CTFtnEdn[] getFootnoteArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFootnotesImpl.FOOTNOTE$0, (List)list);
            final CTFtnEdn[] array = new CTFtnEdn[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFtnEdn getFootnoteArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnEdn ctFtnEdn = (CTFtnEdn)this.get_store().find_element_user(CTFootnotesImpl.FOOTNOTE$0, n);
            if (ctFtnEdn == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFtnEdn;
        }
    }
    
    public int sizeOfFootnoteArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFootnotesImpl.FOOTNOTE$0);
        }
    }
    
    public void setFootnoteArray(final CTFtnEdn[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFootnotesImpl.FOOTNOTE$0);
    }
    
    public void setFootnoteArray(final int n, final CTFtnEdn ctFtnEdn) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnEdn, CTFootnotesImpl.FOOTNOTE$0, n, (short)2);
    }
    
    public CTFtnEdn insertNewFootnote(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdn)this.get_store().insert_element_user(CTFootnotesImpl.FOOTNOTE$0, n);
        }
    }
    
    public CTFtnEdn addNewFootnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdn)this.get_store().add_element_user(CTFootnotesImpl.FOOTNOTE$0);
        }
    }
    
    public void removeFootnote(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFootnotesImpl.FOOTNOTE$0, n);
        }
    }
    
    static {
        FOOTNOTE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnote");
    }
}
