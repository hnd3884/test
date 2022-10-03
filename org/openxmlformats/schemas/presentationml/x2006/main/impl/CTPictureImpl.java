package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPictureNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureImpl extends XmlComplexContentImpl implements CTPicture
{
    private static final long serialVersionUID = 1L;
    private static final QName NVPICPR$0;
    private static final QName BLIPFILL$2;
    private static final QName SPPR$4;
    private static final QName STYLE$6;
    private static final QName EXTLST$8;
    
    public CTPictureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPictureNonVisual getNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureNonVisual ctPictureNonVisual = (CTPictureNonVisual)this.get_store().find_element_user(CTPictureImpl.NVPICPR$0, 0);
            if (ctPictureNonVisual == null) {
                return null;
            }
            return ctPictureNonVisual;
        }
    }
    
    public void setNvPicPr(final CTPictureNonVisual ctPictureNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureNonVisual, CTPictureImpl.NVPICPR$0, 0, (short)1);
    }
    
    public CTPictureNonVisual addNewNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureNonVisual)this.get_store().add_element_user(CTPictureImpl.NVPICPR$0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTPictureImpl.BLIPFILL$2, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTPictureImpl.BLIPFILL$2, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTPictureImpl.BLIPFILL$2);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTPictureImpl.SPPR$4, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTPictureImpl.SPPR$4, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTPictureImpl.SPPR$4);
        }
    }
    
    public CTShapeStyle getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeStyle ctShapeStyle = (CTShapeStyle)this.get_store().find_element_user(CTPictureImpl.STYLE$6, 0);
            if (ctShapeStyle == null) {
                return null;
            }
            return ctShapeStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureImpl.STYLE$6) != 0;
        }
    }
    
    public void setStyle(final CTShapeStyle ctShapeStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeStyle, CTPictureImpl.STYLE$6, 0, (short)1);
    }
    
    public CTShapeStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeStyle)this.get_store().add_element_user(CTPictureImpl.STYLE$6);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureImpl.STYLE$6, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTPictureImpl.EXTLST$8, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTPictureImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTPictureImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureImpl.EXTLST$8, 0);
        }
    }
    
    static {
        NVPICPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPicPr");
        BLIPFILL$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "blipFill");
        SPPR$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "spPr");
        STYLE$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "style");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
