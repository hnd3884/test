package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapDistance;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTopBottom;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapThrough;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTight;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapSquare;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapNone;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTEffectExtent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosV;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosH;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAnchorImpl extends XmlComplexContentImpl implements CTAnchor
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLEPOS$0;
    private static final QName POSITIONH$2;
    private static final QName POSITIONV$4;
    private static final QName EXTENT$6;
    private static final QName EFFECTEXTENT$8;
    private static final QName WRAPNONE$10;
    private static final QName WRAPSQUARE$12;
    private static final QName WRAPTIGHT$14;
    private static final QName WRAPTHROUGH$16;
    private static final QName WRAPTOPANDBOTTOM$18;
    private static final QName DOCPR$20;
    private static final QName CNVGRAPHICFRAMEPR$22;
    private static final QName GRAPHIC$24;
    private static final QName DISTT$26;
    private static final QName DISTB$28;
    private static final QName DISTL$30;
    private static final QName DISTR$32;
    private static final QName SIMPLEPOS2$34;
    private static final QName RELATIVEHEIGHT$36;
    private static final QName BEHINDDOC$38;
    private static final QName LOCKED$40;
    private static final QName LAYOUTINCELL$42;
    private static final QName HIDDEN$44;
    private static final QName ALLOWOVERLAP$46;
    
    public CTAnchorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPoint2D getSimplePos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPoint2D ctPoint2D = (CTPoint2D)this.get_store().find_element_user(CTAnchorImpl.SIMPLEPOS$0, 0);
            if (ctPoint2D == null) {
                return null;
            }
            return ctPoint2D;
        }
    }
    
    public void setSimplePos(final CTPoint2D ctPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPoint2D, CTAnchorImpl.SIMPLEPOS$0, 0, (short)1);
    }
    
    public CTPoint2D addNewSimplePos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPoint2D)this.get_store().add_element_user(CTAnchorImpl.SIMPLEPOS$0);
        }
    }
    
    public CTPosH getPositionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPosH ctPosH = (CTPosH)this.get_store().find_element_user(CTAnchorImpl.POSITIONH$2, 0);
            if (ctPosH == null) {
                return null;
            }
            return ctPosH;
        }
    }
    
    public void setPositionH(final CTPosH ctPosH) {
        this.generatedSetterHelperImpl((XmlObject)ctPosH, CTAnchorImpl.POSITIONH$2, 0, (short)1);
    }
    
    public CTPosH addNewPositionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPosH)this.get_store().add_element_user(CTAnchorImpl.POSITIONH$2);
        }
    }
    
    public CTPosV getPositionV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPosV ctPosV = (CTPosV)this.get_store().find_element_user(CTAnchorImpl.POSITIONV$4, 0);
            if (ctPosV == null) {
                return null;
            }
            return ctPosV;
        }
    }
    
    public void setPositionV(final CTPosV ctPosV) {
        this.generatedSetterHelperImpl((XmlObject)ctPosV, CTAnchorImpl.POSITIONV$4, 0, (short)1);
    }
    
    public CTPosV addNewPositionV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPosV)this.get_store().add_element_user(CTAnchorImpl.POSITIONV$4);
        }
    }
    
    public CTPositiveSize2D getExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTAnchorImpl.EXTENT$6, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public void setExtent(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTAnchorImpl.EXTENT$6, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTAnchorImpl.EXTENT$6);
        }
    }
    
    public CTEffectExtent getEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectExtent ctEffectExtent = (CTEffectExtent)this.get_store().find_element_user(CTAnchorImpl.EFFECTEXTENT$8, 0);
            if (ctEffectExtent == null) {
                return null;
            }
            return ctEffectExtent;
        }
    }
    
    public boolean isSetEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.EFFECTEXTENT$8) != 0;
        }
    }
    
    public void setEffectExtent(final CTEffectExtent ctEffectExtent) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectExtent, CTAnchorImpl.EFFECTEXTENT$8, 0, (short)1);
    }
    
    public CTEffectExtent addNewEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectExtent)this.get_store().add_element_user(CTAnchorImpl.EFFECTEXTENT$8);
        }
    }
    
    public void unsetEffectExtent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.EFFECTEXTENT$8, 0);
        }
    }
    
    public CTWrapNone getWrapNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrapNone ctWrapNone = (CTWrapNone)this.get_store().find_element_user(CTAnchorImpl.WRAPNONE$10, 0);
            if (ctWrapNone == null) {
                return null;
            }
            return ctWrapNone;
        }
    }
    
    public boolean isSetWrapNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.WRAPNONE$10) != 0;
        }
    }
    
    public void setWrapNone(final CTWrapNone ctWrapNone) {
        this.generatedSetterHelperImpl((XmlObject)ctWrapNone, CTAnchorImpl.WRAPNONE$10, 0, (short)1);
    }
    
    public CTWrapNone addNewWrapNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrapNone)this.get_store().add_element_user(CTAnchorImpl.WRAPNONE$10);
        }
    }
    
    public void unsetWrapNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.WRAPNONE$10, 0);
        }
    }
    
    public CTWrapSquare getWrapSquare() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrapSquare ctWrapSquare = (CTWrapSquare)this.get_store().find_element_user(CTAnchorImpl.WRAPSQUARE$12, 0);
            if (ctWrapSquare == null) {
                return null;
            }
            return ctWrapSquare;
        }
    }
    
    public boolean isSetWrapSquare() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.WRAPSQUARE$12) != 0;
        }
    }
    
    public void setWrapSquare(final CTWrapSquare ctWrapSquare) {
        this.generatedSetterHelperImpl((XmlObject)ctWrapSquare, CTAnchorImpl.WRAPSQUARE$12, 0, (short)1);
    }
    
    public CTWrapSquare addNewWrapSquare() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrapSquare)this.get_store().add_element_user(CTAnchorImpl.WRAPSQUARE$12);
        }
    }
    
    public void unsetWrapSquare() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.WRAPSQUARE$12, 0);
        }
    }
    
    public CTWrapTight getWrapTight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrapTight ctWrapTight = (CTWrapTight)this.get_store().find_element_user(CTAnchorImpl.WRAPTIGHT$14, 0);
            if (ctWrapTight == null) {
                return null;
            }
            return ctWrapTight;
        }
    }
    
    public boolean isSetWrapTight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.WRAPTIGHT$14) != 0;
        }
    }
    
    public void setWrapTight(final CTWrapTight ctWrapTight) {
        this.generatedSetterHelperImpl((XmlObject)ctWrapTight, CTAnchorImpl.WRAPTIGHT$14, 0, (short)1);
    }
    
    public CTWrapTight addNewWrapTight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrapTight)this.get_store().add_element_user(CTAnchorImpl.WRAPTIGHT$14);
        }
    }
    
    public void unsetWrapTight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.WRAPTIGHT$14, 0);
        }
    }
    
    public CTWrapThrough getWrapThrough() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrapThrough ctWrapThrough = (CTWrapThrough)this.get_store().find_element_user(CTAnchorImpl.WRAPTHROUGH$16, 0);
            if (ctWrapThrough == null) {
                return null;
            }
            return ctWrapThrough;
        }
    }
    
    public boolean isSetWrapThrough() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.WRAPTHROUGH$16) != 0;
        }
    }
    
    public void setWrapThrough(final CTWrapThrough ctWrapThrough) {
        this.generatedSetterHelperImpl((XmlObject)ctWrapThrough, CTAnchorImpl.WRAPTHROUGH$16, 0, (short)1);
    }
    
    public CTWrapThrough addNewWrapThrough() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrapThrough)this.get_store().add_element_user(CTAnchorImpl.WRAPTHROUGH$16);
        }
    }
    
    public void unsetWrapThrough() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.WRAPTHROUGH$16, 0);
        }
    }
    
    public CTWrapTopBottom getWrapTopAndBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrapTopBottom ctWrapTopBottom = (CTWrapTopBottom)this.get_store().find_element_user(CTAnchorImpl.WRAPTOPANDBOTTOM$18, 0);
            if (ctWrapTopBottom == null) {
                return null;
            }
            return ctWrapTopBottom;
        }
    }
    
    public boolean isSetWrapTopAndBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.WRAPTOPANDBOTTOM$18) != 0;
        }
    }
    
    public void setWrapTopAndBottom(final CTWrapTopBottom ctWrapTopBottom) {
        this.generatedSetterHelperImpl((XmlObject)ctWrapTopBottom, CTAnchorImpl.WRAPTOPANDBOTTOM$18, 0, (short)1);
    }
    
    public CTWrapTopBottom addNewWrapTopAndBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrapTopBottom)this.get_store().add_element_user(CTAnchorImpl.WRAPTOPANDBOTTOM$18);
        }
    }
    
    public void unsetWrapTopAndBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.WRAPTOPANDBOTTOM$18, 0);
        }
    }
    
    public CTNonVisualDrawingProps getDocPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTAnchorImpl.DOCPR$20, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setDocPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTAnchorImpl.DOCPR$20, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewDocPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTAnchorImpl.DOCPR$20);
        }
    }
    
    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = (CTNonVisualGraphicFrameProperties)this.get_store().find_element_user(CTAnchorImpl.CNVGRAPHICFRAMEPR$22, 0);
            if (ctNonVisualGraphicFrameProperties == null) {
                return null;
            }
            return ctNonVisualGraphicFrameProperties;
        }
    }
    
    public boolean isSetCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAnchorImpl.CNVGRAPHICFRAMEPR$22) != 0;
        }
    }
    
    public void setCNvGraphicFramePr(final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualGraphicFrameProperties, CTAnchorImpl.CNVGRAPHICFRAMEPR$22, 0, (short)1);
    }
    
    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualGraphicFrameProperties)this.get_store().add_element_user(CTAnchorImpl.CNVGRAPHICFRAMEPR$22);
        }
    }
    
    public void unsetCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAnchorImpl.CNVGRAPHICFRAMEPR$22, 0);
        }
    }
    
    public CTGraphicalObject getGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObject ctGraphicalObject = (CTGraphicalObject)this.get_store().find_element_user(CTAnchorImpl.GRAPHIC$24, 0);
            if (ctGraphicalObject == null) {
                return null;
            }
            return ctGraphicalObject;
        }
    }
    
    public void setGraphic(final CTGraphicalObject ctGraphicalObject) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObject, CTAnchorImpl.GRAPHIC$24, 0, (short)1);
    }
    
    public CTGraphicalObject addNewGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObject)this.get_store().add_element_user(CTAnchorImpl.GRAPHIC$24);
        }
    }
    
    public long getDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTT$26);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTT$26);
        }
    }
    
    public boolean isSetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.DISTT$26) != null;
        }
    }
    
    public void setDistT(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.DISTT$26);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistT(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTT$26);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTAnchorImpl.DISTT$26);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.DISTT$26);
        }
    }
    
    public long getDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTB$28);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTB$28);
        }
    }
    
    public boolean isSetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.DISTB$28) != null;
        }
    }
    
    public void setDistB(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTB$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.DISTB$28);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistB(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTB$28);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTAnchorImpl.DISTB$28);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.DISTB$28);
        }
    }
    
    public long getDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTL$30);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTL$30);
        }
    }
    
    public boolean isSetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.DISTL$30) != null;
        }
    }
    
    public void setDistL(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTL$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.DISTL$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistL(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTL$30);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTAnchorImpl.DISTL$30);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.DISTL$30);
        }
    }
    
    public long getDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTR$32);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STWrapDistance xgetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTR$32);
        }
    }
    
    public boolean isSetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.DISTR$32) != null;
        }
    }
    
    public void setDistR(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.DISTR$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.DISTR$32);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDistR(final STWrapDistance stWrapDistance) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STWrapDistance stWrapDistance2 = (STWrapDistance)this.get_store().find_attribute_user(CTAnchorImpl.DISTR$32);
            if (stWrapDistance2 == null) {
                stWrapDistance2 = (STWrapDistance)this.get_store().add_attribute_user(CTAnchorImpl.DISTR$32);
            }
            stWrapDistance2.set((XmlObject)stWrapDistance);
        }
    }
    
    public void unsetDistR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.DISTR$32);
        }
    }
    
    public boolean getSimplePos2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSimplePos2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
        }
    }
    
    public boolean isSetSimplePos2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.SIMPLEPOS2$34) != null;
        }
    }
    
    public void setSimplePos2(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSimplePos2(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.SIMPLEPOS2$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSimplePos2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.SIMPLEPOS2$34);
        }
    }
    
    public long getRelativeHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetRelativeHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
        }
    }
    
    public void setRelativeHeight(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRelativeHeight(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTAnchorImpl.RELATIVEHEIGHT$36);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public boolean getBehindDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.BEHINDDOC$38);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBehindDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.BEHINDDOC$38);
        }
    }
    
    public void setBehindDoc(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.BEHINDDOC$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.BEHINDDOC$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBehindDoc(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.BEHINDDOC$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.BEHINDDOC$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.LOCKED$40);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.LOCKED$40);
        }
    }
    
    public void setLocked(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.LOCKED$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.LOCKED$40);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLocked(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.LOCKED$40);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.LOCKED$40);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getLayoutInCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLayoutInCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
        }
    }
    
    public void setLayoutInCell(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLayoutInCell(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.LAYOUTINCELL$42);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.HIDDEN$44);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.HIDDEN$44);
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorImpl.HIDDEN$44) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.HIDDEN$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.HIDDEN$44);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.HIDDEN$44);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.HIDDEN$44);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorImpl.HIDDEN$44);
        }
    }
    
    public boolean getAllowOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAllowOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
        }
    }
    
    public void setAllowOverlap(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAllowOverlap(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorImpl.ALLOWOVERLAP$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    static {
        SIMPLEPOS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "simplePos");
        POSITIONH$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "positionH");
        POSITIONV$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "positionV");
        EXTENT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "extent");
        EFFECTEXTENT$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "effectExtent");
        WRAPNONE$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wrapNone");
        WRAPSQUARE$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wrapSquare");
        WRAPTIGHT$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wrapTight");
        WRAPTHROUGH$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wrapThrough");
        WRAPTOPANDBOTTOM$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wrapTopAndBottom");
        DOCPR$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "docPr");
        CNVGRAPHICFRAMEPR$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "cNvGraphicFramePr");
        GRAPHIC$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphic");
        DISTT$26 = new QName("", "distT");
        DISTB$28 = new QName("", "distB");
        DISTL$30 = new QName("", "distL");
        DISTR$32 = new QName("", "distR");
        SIMPLEPOS2$34 = new QName("", "simplePos");
        RELATIVEHEIGHT$36 = new QName("", "relativeHeight");
        BEHINDDOC$38 = new QName("", "behindDoc");
        LOCKED$40 = new QName("", "locked");
        LAYOUTINCELL$42 = new QName("", "layoutInCell");
        HIDDEN$44 = new QName("", "hidden");
        ALLOWOVERLAP$46 = new QName("", "allowOverlap");
    }
}
