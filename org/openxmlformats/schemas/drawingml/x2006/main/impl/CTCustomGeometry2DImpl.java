package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSiteList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCustomGeometry2DImpl extends XmlComplexContentImpl implements CTCustomGeometry2D
{
    private static final long serialVersionUID = 1L;
    private static final QName AVLST$0;
    private static final QName GDLST$2;
    private static final QName AHLST$4;
    private static final QName CXNLST$6;
    private static final QName RECT$8;
    private static final QName PATHLST$10;
    
    public CTCustomGeometry2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGeomGuideList getAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGeomGuideList list = (CTGeomGuideList)this.get_store().find_element_user(CTCustomGeometry2DImpl.AVLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomGeometry2DImpl.AVLST$0) != 0;
        }
    }
    
    public void setAvLst(final CTGeomGuideList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCustomGeometry2DImpl.AVLST$0, 0, (short)1);
    }
    
    public CTGeomGuideList addNewAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomGuideList)this.get_store().add_element_user(CTCustomGeometry2DImpl.AVLST$0);
        }
    }
    
    public void unsetAvLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomGeometry2DImpl.AVLST$0, 0);
        }
    }
    
    public CTGeomGuideList getGdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGeomGuideList list = (CTGeomGuideList)this.get_store().find_element_user(CTCustomGeometry2DImpl.GDLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetGdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomGeometry2DImpl.GDLST$2) != 0;
        }
    }
    
    public void setGdLst(final CTGeomGuideList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCustomGeometry2DImpl.GDLST$2, 0, (short)1);
    }
    
    public CTGeomGuideList addNewGdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomGuideList)this.get_store().add_element_user(CTCustomGeometry2DImpl.GDLST$2);
        }
    }
    
    public void unsetGdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomGeometry2DImpl.GDLST$2, 0);
        }
    }
    
    public CTAdjustHandleList getAhLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjustHandleList list = (CTAdjustHandleList)this.get_store().find_element_user(CTCustomGeometry2DImpl.AHLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetAhLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomGeometry2DImpl.AHLST$4) != 0;
        }
    }
    
    public void setAhLst(final CTAdjustHandleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCustomGeometry2DImpl.AHLST$4, 0, (short)1);
    }
    
    public CTAdjustHandleList addNewAhLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjustHandleList)this.get_store().add_element_user(CTCustomGeometry2DImpl.AHLST$4);
        }
    }
    
    public void unsetAhLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomGeometry2DImpl.AHLST$4, 0);
        }
    }
    
    public CTConnectionSiteList getCxnLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnectionSiteList list = (CTConnectionSiteList)this.get_store().find_element_user(CTCustomGeometry2DImpl.CXNLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCxnLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomGeometry2DImpl.CXNLST$6) != 0;
        }
    }
    
    public void setCxnLst(final CTConnectionSiteList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCustomGeometry2DImpl.CXNLST$6, 0, (short)1);
    }
    
    public CTConnectionSiteList addNewCxnLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnectionSiteList)this.get_store().add_element_user(CTCustomGeometry2DImpl.CXNLST$6);
        }
    }
    
    public void unsetCxnLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomGeometry2DImpl.CXNLST$6, 0);
        }
    }
    
    public CTGeomRect getRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGeomRect ctGeomRect = (CTGeomRect)this.get_store().find_element_user(CTCustomGeometry2DImpl.RECT$8, 0);
            if (ctGeomRect == null) {
                return null;
            }
            return ctGeomRect;
        }
    }
    
    public boolean isSetRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCustomGeometry2DImpl.RECT$8) != 0;
        }
    }
    
    public void setRect(final CTGeomRect ctGeomRect) {
        this.generatedSetterHelperImpl((XmlObject)ctGeomRect, CTCustomGeometry2DImpl.RECT$8, 0, (short)1);
    }
    
    public CTGeomRect addNewRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomRect)this.get_store().add_element_user(CTCustomGeometry2DImpl.RECT$8);
        }
    }
    
    public void unsetRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCustomGeometry2DImpl.RECT$8, 0);
        }
    }
    
    public CTPath2DList getPathLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DList list = (CTPath2DList)this.get_store().find_element_user(CTCustomGeometry2DImpl.PATHLST$10, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setPathLst(final CTPath2DList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCustomGeometry2DImpl.PATHLST$10, 0, (short)1);
    }
    
    public CTPath2DList addNewPathLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DList)this.get_store().add_element_user(CTCustomGeometry2DImpl.PATHLST$10);
        }
    }
    
    static {
        AVLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "avLst");
        GDLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gdLst");
        AHLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahLst");
        CXNLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cxnLst");
        RECT$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "rect");
        PATHLST$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pathLst");
    }
}
