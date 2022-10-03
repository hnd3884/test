package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;

public class XDDFPictureFillProperties implements XDDFFillProperties
{
    private CTBlipFillProperties props;
    
    public XDDFPictureFillProperties() {
        this(CTBlipFillProperties.Factory.newInstance());
    }
    
    protected XDDFPictureFillProperties(final CTBlipFillProperties properties) {
        this.props = properties;
    }
    
    @Internal
    public CTBlipFillProperties getXmlObject() {
        return this.props;
    }
    
    public XDDFPicture getPicture() {
        if (this.props.isSetBlip()) {
            return new XDDFPicture(this.props.getBlip());
        }
        return null;
    }
    
    public void setPicture(final XDDFPicture picture) {
        if (picture == null) {
            this.props.unsetBlip();
        }
        else {
            this.props.setBlip(picture.getXmlObject());
        }
    }
    
    public Boolean isRotatingWithShape() {
        if (this.props.isSetRotWithShape()) {
            return this.props.getRotWithShape();
        }
        return null;
    }
    
    public void setRotatingWithShape(final Boolean rotating) {
        if (rotating == null) {
            if (this.props.isSetRotWithShape()) {
                this.props.unsetRotWithShape();
            }
        }
        else {
            this.props.setRotWithShape((boolean)rotating);
        }
    }
    
    public Long getDpi() {
        if (this.props.isSetDpi()) {
            return this.props.getDpi();
        }
        return null;
    }
    
    public void setDpi(final Long dpi) {
        if (dpi == null) {
            if (this.props.isSetDpi()) {
                this.props.unsetDpi();
            }
        }
        else {
            this.props.setDpi((long)dpi);
        }
    }
    
    public XDDFRelativeRectangle getSourceRectangle() {
        if (this.props.isSetSrcRect()) {
            return new XDDFRelativeRectangle(this.props.getSrcRect());
        }
        return null;
    }
    
    public void setSourceRectangle(final XDDFRelativeRectangle rectangle) {
        if (rectangle == null) {
            if (this.props.isSetSrcRect()) {
                this.props.unsetSrcRect();
            }
        }
        else {
            this.props.setSrcRect(rectangle.getXmlObject());
        }
    }
    
    public XDDFStretchInfoProperties getStetchInfoProperties() {
        if (this.props.isSetStretch()) {
            return new XDDFStretchInfoProperties(this.props.getStretch());
        }
        return null;
    }
    
    public void setStretchInfoProperties(final XDDFStretchInfoProperties properties) {
        if (properties == null) {
            if (this.props.isSetStretch()) {
                this.props.unsetStretch();
            }
        }
        else {
            this.props.setStretch(properties.getXmlObject());
        }
    }
    
    public XDDFTileInfoProperties getTileInfoProperties() {
        if (this.props.isSetTile()) {
            return new XDDFTileInfoProperties(this.props.getTile());
        }
        return null;
    }
    
    public void setTileInfoProperties(final XDDFTileInfoProperties properties) {
        if (properties == null) {
            if (this.props.isSetTile()) {
                this.props.unsetTile();
            }
        }
        else {
            this.props.setTile(properties.getXmlObject());
        }
    }
}
