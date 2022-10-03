package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;

public class XDDFTileInfoProperties
{
    private CTTileInfoProperties props;
    
    protected XDDFTileInfoProperties(final CTTileInfoProperties properties) {
        this.props = properties;
    }
    
    @Internal
    protected CTTileInfoProperties getXmlObject() {
        return this.props;
    }
    
    public void setAlignment(final RectangleAlignment alignment) {
        if (alignment == null) {
            if (this.props.isSetAlgn()) {
                this.props.unsetAlgn();
            }
        }
        else {
            this.props.setAlgn(alignment.underlying);
        }
    }
    
    public TileFlipMode getFlipMode() {
        if (this.props.isSetFlip()) {
            return TileFlipMode.valueOf(this.props.getFlip());
        }
        return null;
    }
    
    public void setFlipMode(final TileFlipMode mode) {
        if (mode == null) {
            if (this.props.isSetFlip()) {
                this.props.unsetFlip();
            }
        }
        else {
            this.props.setFlip(mode.underlying);
        }
    }
    
    public Integer getSx() {
        if (this.props.isSetSx()) {
            return this.props.getSx();
        }
        return null;
    }
    
    public void setSx(final Integer value) {
        if (value == null) {
            if (this.props.isSetSx()) {
                this.props.unsetSx();
            }
        }
        else {
            this.props.setSx((int)value);
        }
    }
    
    public Integer getSy() {
        if (this.props.isSetSy()) {
            return this.props.getSy();
        }
        return null;
    }
    
    public void setSy(final Integer value) {
        if (value == null) {
            if (this.props.isSetSy()) {
                this.props.unsetSy();
            }
        }
        else {
            this.props.setSy((int)value);
        }
    }
    
    public Long getTx() {
        if (this.props.isSetTx()) {
            return this.props.getTx();
        }
        return null;
    }
    
    public void setTx(final Long value) {
        if (value == null) {
            if (this.props.isSetTx()) {
                this.props.unsetTx();
            }
        }
        else {
            this.props.setTx((long)value);
        }
    }
    
    public Long getTy() {
        if (this.props.isSetTy()) {
            return this.props.getTy();
        }
        return null;
    }
    
    public void setTy(final Long value) {
        if (value == null) {
            if (this.props.isSetTy()) {
                this.props.unsetTy();
            }
        }
        else {
            this.props.setTy((long)value);
        }
    }
}
