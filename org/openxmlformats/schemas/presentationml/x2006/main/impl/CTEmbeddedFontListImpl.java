package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontListEntry;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEmbeddedFontListImpl extends XmlComplexContentImpl implements CTEmbeddedFontList
{
    private static final long serialVersionUID = 1L;
    private static final QName EMBEDDEDFONT$0;
    
    public CTEmbeddedFontListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTEmbeddedFontListEntry> getEmbeddedFontList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EmbeddedFontList extends AbstractList<CTEmbeddedFontListEntry>
            {
                @Override
                public CTEmbeddedFontListEntry get(final int n) {
                    return CTEmbeddedFontListImpl.this.getEmbeddedFontArray(n);
                }
                
                @Override
                public CTEmbeddedFontListEntry set(final int n, final CTEmbeddedFontListEntry ctEmbeddedFontListEntry) {
                    final CTEmbeddedFontListEntry embeddedFontArray = CTEmbeddedFontListImpl.this.getEmbeddedFontArray(n);
                    CTEmbeddedFontListImpl.this.setEmbeddedFontArray(n, ctEmbeddedFontListEntry);
                    return embeddedFontArray;
                }
                
                @Override
                public void add(final int n, final CTEmbeddedFontListEntry ctEmbeddedFontListEntry) {
                    CTEmbeddedFontListImpl.this.insertNewEmbeddedFont(n).set((XmlObject)ctEmbeddedFontListEntry);
                }
                
                @Override
                public CTEmbeddedFontListEntry remove(final int n) {
                    final CTEmbeddedFontListEntry embeddedFontArray = CTEmbeddedFontListImpl.this.getEmbeddedFontArray(n);
                    CTEmbeddedFontListImpl.this.removeEmbeddedFont(n);
                    return embeddedFontArray;
                }
                
                @Override
                public int size() {
                    return CTEmbeddedFontListImpl.this.sizeOfEmbeddedFontArray();
                }
            }
            return new EmbeddedFontList();
        }
    }
    
    @Deprecated
    public CTEmbeddedFontListEntry[] getEmbeddedFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEmbeddedFontListImpl.EMBEDDEDFONT$0, (List)list);
            final CTEmbeddedFontListEntry[] array = new CTEmbeddedFontListEntry[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmbeddedFontListEntry getEmbeddedFontArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontListEntry ctEmbeddedFontListEntry = (CTEmbeddedFontListEntry)this.get_store().find_element_user(CTEmbeddedFontListImpl.EMBEDDEDFONT$0, n);
            if (ctEmbeddedFontListEntry == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmbeddedFontListEntry;
        }
    }
    
    public int sizeOfEmbeddedFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEmbeddedFontListImpl.EMBEDDEDFONT$0);
        }
    }
    
    public void setEmbeddedFontArray(final CTEmbeddedFontListEntry[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEmbeddedFontListImpl.EMBEDDEDFONT$0);
    }
    
    public void setEmbeddedFontArray(final int n, final CTEmbeddedFontListEntry ctEmbeddedFontListEntry) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedFontListEntry, CTEmbeddedFontListImpl.EMBEDDEDFONT$0, n, (short)2);
    }
    
    public CTEmbeddedFontListEntry insertNewEmbeddedFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontListEntry)this.get_store().insert_element_user(CTEmbeddedFontListImpl.EMBEDDEDFONT$0, n);
        }
    }
    
    public CTEmbeddedFontListEntry addNewEmbeddedFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontListEntry)this.get_store().add_element_user(CTEmbeddedFontListImpl.EMBEDDEDFONT$0);
        }
    }
    
    public void removeEmbeddedFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEmbeddedFontListImpl.EMBEDDEDFONT$0, n);
        }
    }
    
    static {
        EMBEDDEDFONT$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "embeddedFont");
    }
}
