package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPictureNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureNonVisualImpl extends XmlComplexContentImpl implements CTPictureNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVPICPR$2;
    private static final QName NVPR$4;
    
    public CTPictureNonVisualImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNonVisualDrawingProps getCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTPictureNonVisualImpl.CNVPR$0, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setCNvPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTPictureNonVisualImpl.CNVPR$0, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTPictureNonVisualImpl.CNVPR$0);
        }
    }
    
    public CTNonVisualPictureProperties getCNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualPictureProperties ctNonVisualPictureProperties = (CTNonVisualPictureProperties)this.get_store().find_element_user(CTPictureNonVisualImpl.CNVPICPR$2, 0);
            if (ctNonVisualPictureProperties == null) {
                return null;
            }
            return ctNonVisualPictureProperties;
        }
    }
    
    public void setCNvPicPr(final CTNonVisualPictureProperties ctNonVisualPictureProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualPictureProperties, CTPictureNonVisualImpl.CNVPICPR$2, 0, (short)1);
    }
    
    public CTNonVisualPictureProperties addNewCNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualPictureProperties)this.get_store().add_element_user(CTPictureNonVisualImpl.CNVPICPR$2);
        }
    }
    
    public CTApplicationNonVisualDrawingProps getNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTApplicationNonVisualDrawingProps ctApplicationNonVisualDrawingProps = (CTApplicationNonVisualDrawingProps)this.get_store().find_element_user(CTPictureNonVisualImpl.NVPR$4, 0);
            if (ctApplicationNonVisualDrawingProps == null) {
                return null;
            }
            return ctApplicationNonVisualDrawingProps;
        }
    }
    
    public void setNvPr(final CTApplicationNonVisualDrawingProps ctApplicationNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctApplicationNonVisualDrawingProps, CTPictureNonVisualImpl.NVPR$4, 0, (short)1);
    }
    
    public CTApplicationNonVisualDrawingProps addNewNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTApplicationNonVisualDrawingProps)this.get_store().add_element_user(CTPictureNonVisualImpl.NVPR$4);
        }
    }
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPr");
        CNVPICPR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPicPr");
        NVPR$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPr");
    }
}
