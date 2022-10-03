package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.ConnectsType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PageContentsTypeImpl extends XmlComplexContentImpl implements PageContentsType
{
    private static final long serialVersionUID = 1L;
    private static final QName SHAPES$0;
    private static final QName CONNECTS$2;
    
    public PageContentsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public ShapesType getShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ShapesType shapesType = (ShapesType)this.get_store().find_element_user(PageContentsTypeImpl.SHAPES$0, 0);
            if (shapesType == null) {
                return null;
            }
            return shapesType;
        }
    }
    
    public boolean isSetShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(PageContentsTypeImpl.SHAPES$0) != 0;
        }
    }
    
    public void setShapes(final ShapesType shapesType) {
        this.generatedSetterHelperImpl((XmlObject)shapesType, PageContentsTypeImpl.SHAPES$0, 0, (short)1);
    }
    
    public ShapesType addNewShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ShapesType)this.get_store().add_element_user(PageContentsTypeImpl.SHAPES$0);
        }
    }
    
    public void unsetShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(PageContentsTypeImpl.SHAPES$0, 0);
        }
    }
    
    public ConnectsType getConnects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ConnectsType connectsType = (ConnectsType)this.get_store().find_element_user(PageContentsTypeImpl.CONNECTS$2, 0);
            if (connectsType == null) {
                return null;
            }
            return connectsType;
        }
    }
    
    public boolean isSetConnects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(PageContentsTypeImpl.CONNECTS$2) != 0;
        }
    }
    
    public void setConnects(final ConnectsType connectsType) {
        this.generatedSetterHelperImpl((XmlObject)connectsType, PageContentsTypeImpl.CONNECTS$2, 0, (short)1);
    }
    
    public ConnectsType addNewConnects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ConnectsType)this.get_store().add_element_user(PageContentsTypeImpl.CONNECTS$2);
        }
    }
    
    public void unsetConnects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(PageContentsTypeImpl.CONNECTS$2, 0);
        }
    }
    
    static {
        SHAPES$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Shapes");
        CONNECTS$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Connects");
    }
}
