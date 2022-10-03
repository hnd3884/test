package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class XDDFShapeProperties
{
    private CTShapeProperties props;
    
    public XDDFShapeProperties() {
        this(CTShapeProperties.Factory.newInstance());
    }
    
    @Internal
    public XDDFShapeProperties(final CTShapeProperties properties) {
        this.props = properties;
    }
    
    @Internal
    public CTShapeProperties getXmlObject() {
        return this.props;
    }
    
    public BlackWhiteMode getBlackWhiteMode() {
        if (this.props.isSetBwMode()) {
            return BlackWhiteMode.valueOf(this.props.getBwMode());
        }
        return null;
    }
    
    public void setBlackWhiteMode(final BlackWhiteMode mode) {
        if (mode == null) {
            if (this.props.isSetBwMode()) {
                this.props.unsetBwMode();
            }
        }
        else {
            this.props.setBwMode(mode.underlying);
        }
    }
    
    public XDDFFillProperties getFillProperties() {
        if (this.props.isSetGradFill()) {
            return new XDDFGradientFillProperties(this.props.getGradFill());
        }
        if (this.props.isSetGrpFill()) {
            return new XDDFGroupFillProperties(this.props.getGrpFill());
        }
        if (this.props.isSetNoFill()) {
            return new XDDFNoFillProperties(this.props.getNoFill());
        }
        if (this.props.isSetPattFill()) {
            return new XDDFPatternFillProperties(this.props.getPattFill());
        }
        if (this.props.isSetBlipFill()) {
            return new XDDFPictureFillProperties(this.props.getBlipFill());
        }
        if (this.props.isSetSolidFill()) {
            return new XDDFSolidFillProperties(this.props.getSolidFill());
        }
        return null;
    }
    
    public void setFillProperties(final XDDFFillProperties properties) {
        if (this.props.isSetBlipFill()) {
            this.props.unsetBlipFill();
        }
        if (this.props.isSetGradFill()) {
            this.props.unsetGradFill();
        }
        if (this.props.isSetGrpFill()) {
            this.props.unsetGrpFill();
        }
        if (this.props.isSetNoFill()) {
            this.props.unsetNoFill();
        }
        if (this.props.isSetPattFill()) {
            this.props.unsetPattFill();
        }
        if (this.props.isSetSolidFill()) {
            this.props.unsetSolidFill();
        }
        if (properties == null) {
            return;
        }
        if (properties instanceof XDDFGradientFillProperties) {
            this.props.setGradFill(((XDDFGradientFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFGroupFillProperties) {
            this.props.setGrpFill(((XDDFGroupFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFNoFillProperties) {
            this.props.setNoFill(((XDDFNoFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFPatternFillProperties) {
            this.props.setPattFill(((XDDFPatternFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFPictureFillProperties) {
            this.props.setBlipFill(((XDDFPictureFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFSolidFillProperties) {
            this.props.setSolidFill(((XDDFSolidFillProperties)properties).getXmlObject());
        }
    }
    
    public XDDFLineProperties getLineProperties() {
        if (this.props.isSetLn()) {
            return new XDDFLineProperties(this.props.getLn());
        }
        return null;
    }
    
    public void setLineProperties(final XDDFLineProperties properties) {
        if (properties == null) {
            if (this.props.isSetLn()) {
                this.props.unsetLn();
            }
        }
        else {
            this.props.setLn(properties.getXmlObject());
        }
    }
    
    public XDDFCustomGeometry2D getCustomGeometry2D() {
        if (this.props.isSetCustGeom()) {
            return new XDDFCustomGeometry2D(this.props.getCustGeom());
        }
        return null;
    }
    
    public void setCustomGeometry2D(final XDDFCustomGeometry2D geometry) {
        if (geometry == null) {
            if (this.props.isSetCustGeom()) {
                this.props.unsetCustGeom();
            }
        }
        else {
            this.props.setCustGeom(geometry.getXmlObject());
        }
    }
    
    public XDDFPresetGeometry2D getPresetGeometry2D() {
        if (this.props.isSetPrstGeom()) {
            return new XDDFPresetGeometry2D(this.props.getPrstGeom());
        }
        return null;
    }
    
    public void setPresetGeometry2D(final XDDFPresetGeometry2D geometry) {
        if (geometry == null) {
            if (this.props.isSetPrstGeom()) {
                this.props.unsetPrstGeom();
            }
        }
        else {
            this.props.setPrstGeom(geometry.getXmlObject());
        }
    }
    
    public XDDFEffectContainer getEffectContainer() {
        if (this.props.isSetEffectDag()) {
            return new XDDFEffectContainer(this.props.getEffectDag());
        }
        return null;
    }
    
    public void setEffectContainer(final XDDFEffectContainer container) {
        if (container == null) {
            if (this.props.isSetEffectDag()) {
                this.props.unsetEffectDag();
            }
        }
        else {
            this.props.setEffectDag(container.getXmlObject());
        }
    }
    
    public XDDFEffectList getEffectList() {
        if (this.props.isSetEffectLst()) {
            return new XDDFEffectList(this.props.getEffectLst());
        }
        return null;
    }
    
    public void setEffectList(final XDDFEffectList list) {
        if (list == null) {
            if (this.props.isSetEffectLst()) {
                this.props.unsetEffectLst();
            }
        }
        else {
            this.props.setEffectLst(list.getXmlObject());
        }
    }
    
    public XDDFExtensionList getExtensionList() {
        if (this.props.isSetExtLst()) {
            return new XDDFExtensionList(this.props.getExtLst());
        }
        return null;
    }
    
    public void setExtensionList(final XDDFExtensionList list) {
        if (list == null) {
            if (this.props.isSetExtLst()) {
                this.props.unsetExtLst();
            }
        }
        else {
            this.props.setExtLst(list.getXmlObject());
        }
    }
    
    public XDDFScene3D getScene3D() {
        if (this.props.isSetScene3D()) {
            return new XDDFScene3D(this.props.getScene3D());
        }
        return null;
    }
    
    public void setScene3D(final XDDFScene3D scene) {
        if (scene == null) {
            if (this.props.isSetScene3D()) {
                this.props.unsetScene3D();
            }
        }
        else {
            this.props.setScene3D(scene.getXmlObject());
        }
    }
    
    public XDDFShape3D getShape3D() {
        if (this.props.isSetSp3D()) {
            return new XDDFShape3D(this.props.getSp3D());
        }
        return null;
    }
    
    public void setShape3D(final XDDFShape3D shape) {
        if (shape == null) {
            if (this.props.isSetSp3D()) {
                this.props.unsetSp3D();
            }
        }
        else {
            this.props.setSp3D(shape.getXmlObject());
        }
    }
    
    public XDDFTransform2D getTransform2D() {
        if (this.props.isSetXfrm()) {
            return new XDDFTransform2D(this.props.getXfrm());
        }
        return null;
    }
    
    public void setTransform2D(final XDDFTransform2D transform) {
        if (transform == null) {
            if (this.props.isSetXfrm()) {
                this.props.unsetXfrm();
            }
        }
        else {
            this.props.setXfrm(transform.getXmlObject());
        }
    }
}
