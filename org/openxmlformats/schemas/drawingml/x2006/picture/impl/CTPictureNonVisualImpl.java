package org.openxmlformats.schemas.drawingml.x2006.picture.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureNonVisualImpl extends XmlComplexContentImpl implements CTPictureNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVPICPR$2;
    
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
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "cNvPr");
        CNVPICPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "cNvPicPr");
    }
}
