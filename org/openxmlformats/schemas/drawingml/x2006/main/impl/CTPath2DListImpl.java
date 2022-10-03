package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPath2DListImpl extends XmlComplexContentImpl implements CTPath2DList
{
    private static final long serialVersionUID = 1L;
    private static final QName PATH$0;
    
    public CTPath2DListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPath2D> getPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PathList extends AbstractList<CTPath2D>
            {
                @Override
                public CTPath2D get(final int n) {
                    return CTPath2DListImpl.this.getPathArray(n);
                }
                
                @Override
                public CTPath2D set(final int n, final CTPath2D ctPath2D) {
                    final CTPath2D pathArray = CTPath2DListImpl.this.getPathArray(n);
                    CTPath2DListImpl.this.setPathArray(n, ctPath2D);
                    return pathArray;
                }
                
                @Override
                public void add(final int n, final CTPath2D ctPath2D) {
                    CTPath2DListImpl.this.insertNewPath(n).set((XmlObject)ctPath2D);
                }
                
                @Override
                public CTPath2D remove(final int n) {
                    final CTPath2D pathArray = CTPath2DListImpl.this.getPathArray(n);
                    CTPath2DListImpl.this.removePath(n);
                    return pathArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DListImpl.this.sizeOfPathArray();
                }
            }
            return new PathList();
        }
    }
    
    @Deprecated
    public CTPath2D[] getPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DListImpl.PATH$0, (List)list);
            final CTPath2D[] array = new CTPath2D[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2D getPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2D ctPath2D = (CTPath2D)this.get_store().find_element_user(CTPath2DListImpl.PATH$0, n);
            if (ctPath2D == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2D;
        }
    }
    
    public int sizeOfPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DListImpl.PATH$0);
        }
    }
    
    public void setPathArray(final CTPath2D[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DListImpl.PATH$0);
    }
    
    public void setPathArray(final int n, final CTPath2D ctPath2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2D, CTPath2DListImpl.PATH$0, n, (short)2);
    }
    
    public CTPath2D insertNewPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2D)this.get_store().insert_element_user(CTPath2DListImpl.PATH$0, n);
        }
    }
    
    public CTPath2D addNewPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2D)this.get_store().add_element_user(CTPath2DListImpl.PATH$0);
        }
    }
    
    public void removePath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DListImpl.PATH$0, n);
        }
    }
    
    static {
        PATH$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "path");
    }
}
