package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapDistance;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTEffectExtent;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTInlineImpl extends XmlComplexContentImpl implements CTInline
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTENT$0;
    private static final QName EFFECTEXTENT$2;
    private static final QName DOCPR$4;
    private static final QName CNVGRAPHICFRAMEPR$6;
    private static final QName GRAPHIC$8;
    private static final QName DISTT$10;
    private static final QName DISTB$12;
    private static final QName DISTL$14;
    private static final QName DISTR$16;
    
    public CTInlineImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPositiveSize2D getExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTInlineImpl.EXTENT$0, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public void setExtent(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTInlineImpl.EXTENT$0, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTInlineImpl.EXTENT$0);
        }
    }
    
    public CTEffectExtent getEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectExtent ctEffectExtent = (CTEffectExtent)this.get_store().find_element_user(CTInlineImpl.EFFECTEXTENT$2, 0);
            if (ctEffectExtent == null) {
                return null;
            }
            return ctEffectExtent;
        }
    }
    
    public boolean isSetEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTInlineImpl.EFFECTEXTENT$2) != 0;
        }
    }
    
    public void setEffectExtent(final CTEffectExtent ctEffectExtent) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectExtent, CTInlineImpl.EFFECTEXTENT$2, 0, (short)1);
    }
    
    public CTEffectExtent addNewEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectExtent)this.get_store().add_element_user(CTInlineImpl.EFFECTEXTENT$2);
        }
    }
    
    public void unsetEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTInlineImpl.EFFECTEXTENT$2, 0);
        }
    }
    
    public CTNonVisualDrawingProps getDocPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTInlineImpl.DOCPR$4, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setDocPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTInlineImpl.DOCPR$4, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewDocPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTInlineImpl.DOCPR$4);
        }
    }
    
    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = (CTNonVisualGraphicFrameProperties)this.get_store().find_element_user(CTInlineImpl.CNVGRAPHICFRAMEPR$6, 0);
            if (ctNonVisualGraphicFrameProperties == null) {
                return null;
            }
            return ctNonVisualGraphicFrameProperties;
        }
    }
    
    public boolean isSetCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTInlineImpl.CNVGRAPHICFRAMEPR$6) != 0;
        }
    }
    
    public void setCNvGraphicFramePr(final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualGraphicFrameProperties, CTInlineImpl.CNVGRAPHICFRAMEPR$6, 0, (short)1);
    }
    
    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualGraphicFrameProperties)this.get_store().add_element_user(CTInlineImpl.CNVGRAPHICFRAMEPR$6);
        }
    }
    
    public void unsetCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTInlineImpl.CNVGRAPHICFRAMEPR$6, 0);
        }
    }
    
    public CTGraphicalObject getGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObject ctGraphicalObject = (CTGraphicalObject)this.get_store().find_element_user(CTInlineImpl.GRAPHIC$8, 0);
            if (ctGraphicalObject == null) {
                return null;
            }
            return ctGraphicalObject;
        }
    }
    
    public void setGraphic(final CTGraphicalObject ctGraphicalObject) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObject, CTInlineImpl.GRAPHIC$8, 0, (short)1);
    }
    
    public CTGraphicalObject addNewGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObject)this.get_store().add_element_user(CTInlineImpl.GRAPHIC$8);
        }
    }
    
    public long getDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTT$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTT$10);
        }
    }
    
    public boolean isSetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTInlineImpl.DISTT$10) != null;
        }
    }
    
    public void setDistT(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTInlineImpl.DISTT$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistT(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTT$10);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTInlineImpl.DISTT$10);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTInlineImpl.DISTT$10);
        }
    }
    
    public long getDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTB$12);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTB$12);
        }
    }
    
    public boolean isSetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTInlineImpl.DISTB$12) != null;
        }
    }
    
    public void setDistB(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTB$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTInlineImpl.DISTB$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistB(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTB$12);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTInlineImpl.DISTB$12);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTInlineImpl.DISTB$12);
        }
    }
    
    public long getDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTL$14);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTL$14);
        }
    }
    
    public boolean isSetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTInlineImpl.DISTL$14) != null;
        }
    }
    
    public void setDistL(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTL$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTInlineImpl.DISTL$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistL(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTL$14);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTInlineImpl.DISTL$14);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTInlineImpl.DISTL$14);
        }
    }
    
    public long getDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTR$16);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTR$16);
        }
    }
    
    public boolean isSetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTInlineImpl.DISTR$16) != null;
        }
    }
    
    public void setDistR(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTInlineImpl.DISTR$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTInlineImpl.DISTR$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistR(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTInlineImpl.DISTR$16);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTInlineImpl.DISTR$16);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTInlineImpl.DISTR$16);
        }
    }
    
    static {
        EXTENT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "extent");
        EFFECTEXTENT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "effectExtent");
        DOCPR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "docPr");
        CNVGRAPHICFRAMEPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "cNvGraphicFramePr");
        GRAPHIC$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphic");
        DISTT$10 = new QName("", "distT");
        DISTB$12 = new QName("", "distB");
        DISTL$14 = new QName("", "distL");
        DISTR$16 = new QName("", "distR");
    }
}
