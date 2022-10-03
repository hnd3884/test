package org.apache.poi.xddf.usermodel;

import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;

public class XDDFGradientFillProperties implements XDDFFillProperties
{
    private CTGradientFillProperties props;
    
    public XDDFGradientFillProperties() {
        this(CTGradientFillProperties.Factory.newInstance());
    }
    
    protected XDDFGradientFillProperties(final CTGradientFillProperties properties) {
        this.props = properties;
    }
    
    @Internal
    public CTGradientFillProperties getXmlObject() {
        return this.props;
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
    
    public TileFlipMode getTileFlipMode() {
        if (this.props.isSetFlip()) {
            return TileFlipMode.valueOf(this.props.getFlip());
        }
        return null;
    }
    
    public void setTileFlipMode(final TileFlipMode mode) {
        if (mode == null) {
            if (this.props.isSetFlip()) {
                this.props.unsetFlip();
            }
        }
        else {
            this.props.setFlip(mode.underlying);
        }
    }
    
    public XDDFGradientStop addGradientStop() {
        if (!this.props.isSetGsLst()) {
            this.props.addNewGsLst();
        }
        return new XDDFGradientStop(this.props.getGsLst().addNewGs());
    }
    
    public XDDFGradientStop insertGradientStop(final int index) {
        if (!this.props.isSetGsLst()) {
            this.props.addNewGsLst();
        }
        return new XDDFGradientStop(this.props.getGsLst().insertNewGs(index));
    }
    
    public void removeGradientStop(final int index) {
        if (this.props.isSetGsLst()) {
            this.props.getGsLst().removeGs(index);
        }
    }
    
    public XDDFGradientStop getGradientStop(final int index) {
        if (this.props.isSetGsLst()) {
            return new XDDFGradientStop(this.props.getGsLst().getGsArray(index));
        }
        return null;
    }
    
    public List<XDDFGradientStop> getGradientStops() {
        if (this.props.isSetGsLst()) {
            return Collections.unmodifiableList((List<? extends XDDFGradientStop>)this.props.getGsLst().getGsList().stream().map(XDDFGradientStop::new).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public int countGradientStops() {
        if (this.props.isSetGsLst()) {
            return this.props.getGsLst().sizeOfGsArray();
        }
        return 0;
    }
    
    public XDDFLinearShadeProperties getLinearShadeProperties() {
        if (this.props.isSetLin()) {
            return new XDDFLinearShadeProperties(this.props.getLin());
        }
        return null;
    }
    
    public void setLinearShadeProperties(final XDDFLinearShadeProperties properties) {
        if (properties == null) {
            if (this.props.isSetLin()) {
                this.props.unsetLin();
            }
        }
        else {
            this.props.setLin(properties.getXmlObject());
        }
    }
    
    public XDDFPathShadeProperties getPathShadeProperties() {
        if (this.props.isSetPath()) {
            return new XDDFPathShadeProperties(this.props.getPath());
        }
        return null;
    }
    
    public void setPathShadeProperties(final XDDFPathShadeProperties properties) {
        if (properties == null) {
            if (this.props.isSetPath()) {
                this.props.unsetPath();
            }
        }
        else {
            this.props.setPath(properties.getXmlObject());
        }
    }
    
    public XDDFRelativeRectangle getTileRectangle() {
        if (this.props.isSetTileRect()) {
            return new XDDFRelativeRectangle(this.props.getTileRect());
        }
        return null;
    }
    
    public void setTileRectangle(final XDDFRelativeRectangle rectangle) {
        if (rectangle == null) {
            if (this.props.isSetTileRect()) {
                this.props.unsetTileRect();
            }
        }
        else {
            this.props.setTileRect(rectangle.getXmlObject());
        }
    }
}
