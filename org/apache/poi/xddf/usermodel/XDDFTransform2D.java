package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;

public class XDDFTransform2D
{
    private CTTransform2D transform;
    
    protected XDDFTransform2D(final CTTransform2D transform) {
        this.transform = transform;
    }
    
    @Internal
    protected CTTransform2D getXmlObject() {
        return this.transform;
    }
    
    public Boolean getFlipHorizontal() {
        if (this.transform.isSetFlipH()) {
            return this.transform.getFlipH();
        }
        return null;
    }
    
    public void setFlipHorizontal(final Boolean flip) {
        if (flip == null) {
            if (this.transform.isSetFlipH()) {
                this.transform.unsetFlipH();
            }
        }
        else {
            this.transform.setFlipH((boolean)flip);
        }
    }
    
    public Boolean getFlipVertical() {
        if (this.transform.isSetFlipV()) {
            return this.transform.getFlipV();
        }
        return null;
    }
    
    public void setFlipVertical(final Boolean flip) {
        if (flip == null) {
            if (this.transform.isSetFlipV()) {
                this.transform.unsetFlipV();
            }
        }
        else {
            this.transform.setFlipV((boolean)flip);
        }
    }
    
    public XDDFPositiveSize2D getExtension() {
        if (this.transform.isSetExt()) {
            return new XDDFPositiveSize2D(this.transform.getExt());
        }
        return null;
    }
    
    public void setExtension(final XDDFPositiveSize2D extension) {
        if (extension == null) {
            if (this.transform.isSetExt()) {
                this.transform.unsetExt();
            }
            return;
        }
        CTPositiveSize2D xformExt;
        if (this.transform.isSetExt()) {
            xformExt = this.transform.getExt();
        }
        else {
            xformExt = this.transform.addNewExt();
        }
        xformExt.setCx(extension.getX());
        xformExt.setCy(extension.getY());
    }
    
    public XDDFPoint2D getOffset() {
        if (this.transform.isSetOff()) {
            return new XDDFPoint2D(this.transform.getOff());
        }
        return null;
    }
    
    public void setOffset(final XDDFPoint2D offset) {
        if (offset == null) {
            if (this.transform.isSetOff()) {
                this.transform.unsetOff();
            }
            return;
        }
        CTPoint2D xformOff;
        if (this.transform.isSetOff()) {
            xformOff = this.transform.getOff();
        }
        else {
            xformOff = this.transform.addNewOff();
        }
        xformOff.setX(offset.getX());
        xformOff.setY(offset.getY());
    }
    
    public Double getRotation() {
        if (this.transform.isSetRot()) {
            return Angles.attributeToDegrees(this.transform.getRot());
        }
        return null;
    }
    
    public void setRotation(final Double rotation) {
        if (rotation == null) {
            if (this.transform.isSetRot()) {
                this.transform.unsetRot();
            }
        }
        else {
            this.transform.setRot(Angles.degreesToAttribute(rotation));
        }
    }
}
