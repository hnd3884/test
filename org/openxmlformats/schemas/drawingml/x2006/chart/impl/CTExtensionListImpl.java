package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtension;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExtensionListImpl extends XmlComplexContentImpl implements CTExtensionList
{
    private static final long serialVersionUID = 1L;
    private static final QName EXT$0;
    
    public CTExtensionListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTExtension> getExtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtList extends AbstractList<CTExtension>
            {
                @Override
                public CTExtension get(final int n) {
                    return CTExtensionListImpl.this.getExtArray(n);
                }
                
                @Override
                public CTExtension set(final int n, final CTExtension ctExtension) {
                    final CTExtension extArray = CTExtensionListImpl.this.getExtArray(n);
                    CTExtensionListImpl.this.setExtArray(n, ctExtension);
                    return extArray;
                }
                
                @Override
                public void add(final int n, final CTExtension ctExtension) {
                    CTExtensionListImpl.this.insertNewExt(n).set((XmlObject)ctExtension);
                }
                
                @Override
                public CTExtension remove(final int n) {
                    final CTExtension extArray = CTExtensionListImpl.this.getExtArray(n);
                    CTExtensionListImpl.this.removeExt(n);
                    return extArray;
                }
                
                @Override
                public int size() {
                    return CTExtensionListImpl.this.sizeOfExtArray();
                }
            }
            return new ExtList();
        }
    }
    
    @Deprecated
    public CTExtension[] getExtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTExtensionListImpl.EXT$0, (List)list);
            final CTExtension[] array = new CTExtension[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExtension getExtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtension ctExtension = (CTExtension)this.get_store().find_element_user(CTExtensionListImpl.EXT$0, n);
            if (ctExtension == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExtension;
        }
    }
    
    public int sizeOfExtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExtensionListImpl.EXT$0);
        }
    }
    
    public void setExtArray(final CTExtension[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTExtensionListImpl.EXT$0);
    }
    
    public void setExtArray(final int n, final CTExtension ctExtension) {
        this.generatedSetterHelperImpl((XmlObject)ctExtension, CTExtensionListImpl.EXT$0, n, (short)2);
    }
    
    public CTExtension insertNewExt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtension)this.get_store().insert_element_user(CTExtensionListImpl.EXT$0, n);
        }
    }
    
    public CTExtension addNewExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtension)this.get_store().add_element_user(CTExtensionListImpl.EXT$0);
        }
    }
    
    public void removeExt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExtensionListImpl.EXT$0, n);
        }
    }
    
    static {
        EXT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ext");
    }
}
