package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOfficeArtExtensionListImpl extends XmlComplexContentImpl implements CTOfficeArtExtensionList
{
    private static final long serialVersionUID = 1L;
    private static final QName EXT$0;
    
    public CTOfficeArtExtensionListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTOfficeArtExtension> getExtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtList extends AbstractList<CTOfficeArtExtension>
            {
                @Override
                public CTOfficeArtExtension get(final int n) {
                    return CTOfficeArtExtensionListImpl.this.getExtArray(n);
                }
                
                @Override
                public CTOfficeArtExtension set(final int n, final CTOfficeArtExtension ctOfficeArtExtension) {
                    final CTOfficeArtExtension extArray = CTOfficeArtExtensionListImpl.this.getExtArray(n);
                    CTOfficeArtExtensionListImpl.this.setExtArray(n, ctOfficeArtExtension);
                    return extArray;
                }
                
                @Override
                public void add(final int n, final CTOfficeArtExtension ctOfficeArtExtension) {
                    CTOfficeArtExtensionListImpl.this.insertNewExt(n).set((XmlObject)ctOfficeArtExtension);
                }
                
                @Override
                public CTOfficeArtExtension remove(final int n) {
                    final CTOfficeArtExtension extArray = CTOfficeArtExtensionListImpl.this.getExtArray(n);
                    CTOfficeArtExtensionListImpl.this.removeExt(n);
                    return extArray;
                }
                
                @Override
                public int size() {
                    return CTOfficeArtExtensionListImpl.this.sizeOfExtArray();
                }
            }
            return new ExtList();
        }
    }
    
    @Deprecated
    public CTOfficeArtExtension[] getExtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTOfficeArtExtensionListImpl.EXT$0, (List)list);
            final CTOfficeArtExtension[] array = new CTOfficeArtExtension[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOfficeArtExtension getExtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtension ctOfficeArtExtension = (CTOfficeArtExtension)this.get_store().find_element_user(CTOfficeArtExtensionListImpl.EXT$0, n);
            if (ctOfficeArtExtension == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOfficeArtExtension;
        }
    }
    
    public int sizeOfExtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOfficeArtExtensionListImpl.EXT$0);
        }
    }
    
    public void setExtArray(final CTOfficeArtExtension[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTOfficeArtExtensionListImpl.EXT$0);
    }
    
    public void setExtArray(final int n, final CTOfficeArtExtension ctOfficeArtExtension) {
        this.generatedSetterHelperImpl((XmlObject)ctOfficeArtExtension, CTOfficeArtExtensionListImpl.EXT$0, n, (short)2);
    }
    
    public CTOfficeArtExtension insertNewExt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtension)this.get_store().insert_element_user(CTOfficeArtExtensionListImpl.EXT$0, n);
        }
    }
    
    public CTOfficeArtExtension addNewExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtension)this.get_store().add_element_user(CTOfficeArtExtensionListImpl.EXT$0);
        }
    }
    
    public void removeExt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOfficeArtExtensionListImpl.EXT$0, n);
        }
    }
    
    static {
        EXT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ext");
    }
}
