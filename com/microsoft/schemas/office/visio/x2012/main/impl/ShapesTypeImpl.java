package com.microsoft.schemas.office.visio.x2012.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ShapesTypeImpl extends XmlComplexContentImpl implements ShapesType
{
    private static final long serialVersionUID = 1L;
    private static final QName SHAPE$0;
    
    public ShapesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<ShapeSheetType> getShapeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShapeList extends AbstractList<ShapeSheetType>
            {
                @Override
                public ShapeSheetType get(final int n) {
                    return ShapesTypeImpl.this.getShapeArray(n);
                }
                
                @Override
                public ShapeSheetType set(final int n, final ShapeSheetType shapeSheetType) {
                    final ShapeSheetType shapeArray = ShapesTypeImpl.this.getShapeArray(n);
                    ShapesTypeImpl.this.setShapeArray(n, shapeSheetType);
                    return shapeArray;
                }
                
                @Override
                public void add(final int n, final ShapeSheetType shapeSheetType) {
                    ShapesTypeImpl.this.insertNewShape(n).set((XmlObject)shapeSheetType);
                }
                
                @Override
                public ShapeSheetType remove(final int n) {
                    final ShapeSheetType shapeArray = ShapesTypeImpl.this.getShapeArray(n);
                    ShapesTypeImpl.this.removeShape(n);
                    return shapeArray;
                }
                
                @Override
                public int size() {
                    return ShapesTypeImpl.this.sizeOfShapeArray();
                }
            }
            return new ShapeList();
        }
    }
    
    @Deprecated
    public ShapeSheetType[] getShapeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(ShapesTypeImpl.SHAPE$0, (List)list);
            final ShapeSheetType[] array = new ShapeSheetType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public ShapeSheetType getShapeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ShapeSheetType shapeSheetType = (ShapeSheetType)this.get_store().find_element_user(ShapesTypeImpl.SHAPE$0, n);
            if (shapeSheetType == null) {
                throw new IndexOutOfBoundsException();
            }
            return shapeSheetType;
        }
    }
    
    public int sizeOfShapeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ShapesTypeImpl.SHAPE$0);
        }
    }
    
    public void setShapeArray(final ShapeSheetType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, ShapesTypeImpl.SHAPE$0);
    }
    
    public void setShapeArray(final int n, final ShapeSheetType shapeSheetType) {
        this.generatedSetterHelperImpl((XmlObject)shapeSheetType, ShapesTypeImpl.SHAPE$0, n, (short)2);
    }
    
    public ShapeSheetType insertNewShape(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ShapeSheetType)this.get_store().insert_element_user(ShapesTypeImpl.SHAPE$0, n);
        }
    }
    
    public ShapeSheetType addNewShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ShapeSheetType)this.get_store().add_element_user(ShapesTypeImpl.SHAPE$0);
        }
    }
    
    public void removeShape(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ShapesTypeImpl.SHAPE$0, n);
        }
    }
    
    static {
        SHAPE$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Shape");
    }
}
