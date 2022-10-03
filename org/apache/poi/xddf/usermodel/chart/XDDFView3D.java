package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;

public class XDDFView3D
{
    private final CTView3D view3D;
    
    @Internal
    protected XDDFView3D(final CTView3D view3D) {
        this.view3D = view3D;
    }
    
    public Byte getXRotationAngle() {
        if (this.view3D.isSetRotX()) {
            return this.view3D.getRotX().getVal();
        }
        return null;
    }
    
    public void setXRotationAngle(final Byte rotation) {
        if (rotation == null) {
            if (this.view3D.isSetRotX()) {
                this.view3D.unsetRotX();
            }
        }
        else {
            if (rotation < -90 || 90 < rotation) {
                throw new IllegalArgumentException("rotation must be between -90 and 90");
            }
            if (this.view3D.isSetRotX()) {
                this.view3D.getRotX().setVal((byte)rotation);
            }
            else {
                this.view3D.addNewRotX().setVal((byte)rotation);
            }
        }
    }
    
    public Integer getYRotationAngle() {
        if (this.view3D.isSetRotY()) {
            return this.view3D.getRotY().getVal();
        }
        return null;
    }
    
    public void setYRotationAngle(final Integer rotation) {
        if (rotation == null) {
            if (this.view3D.isSetRotY()) {
                this.view3D.unsetRotY();
            }
        }
        else {
            if (rotation < 0 || 360 < rotation) {
                throw new IllegalArgumentException("rotation must be between 0 and 360");
            }
            if (this.view3D.isSetRotY()) {
                this.view3D.getRotY().setVal((int)rotation);
            }
            else {
                this.view3D.addNewRotY().setVal((int)rotation);
            }
        }
    }
    
    public Boolean hasRightAngleAxes() {
        if (this.view3D.isSetRAngAx()) {
            return this.view3D.getRAngAx().getVal();
        }
        return null;
    }
    
    public void setRightAngleAxes(final Boolean rightAngles) {
        if (rightAngles == null) {
            if (this.view3D.isSetRAngAx()) {
                this.view3D.unsetRAngAx();
            }
        }
        else if (this.view3D.isSetRAngAx()) {
            this.view3D.getRAngAx().setVal((boolean)rightAngles);
        }
        else {
            this.view3D.addNewRAngAx().setVal((boolean)rightAngles);
        }
    }
    
    public Short getPerspectiveAngle() {
        if (this.view3D.isSetPerspective()) {
            return this.view3D.getPerspective().getVal();
        }
        return null;
    }
    
    public void setPerspectiveAngle(final Short perspective) {
        if (perspective == null) {
            if (this.view3D.isSetPerspective()) {
                this.view3D.unsetPerspective();
            }
        }
        else {
            if (perspective < 0 || 240 < perspective) {
                throw new IllegalArgumentException("perspective must be between 0 and 240");
            }
            if (this.view3D.isSetPerspective()) {
                this.view3D.getPerspective().setVal((short)perspective);
            }
            else {
                this.view3D.addNewPerspective().setVal((short)perspective);
            }
        }
    }
    
    public Integer getDepthPercent() {
        if (this.view3D.isSetDepthPercent()) {
            return this.view3D.getDepthPercent().getVal();
        }
        return null;
    }
    
    public void setDepthPercent(final Integer percent) {
        if (percent == null) {
            if (this.view3D.isSetDepthPercent()) {
                this.view3D.unsetDepthPercent();
            }
        }
        else {
            if (percent < 20 || 2000 < percent) {
                throw new IllegalArgumentException("percent must be between 20 and 2000");
            }
            if (this.view3D.isSetDepthPercent()) {
                this.view3D.getDepthPercent().setVal((int)percent);
            }
            else {
                this.view3D.addNewDepthPercent().setVal((int)percent);
            }
        }
    }
    
    public Integer getHPercent() {
        if (this.view3D.isSetHPercent()) {
            return this.view3D.getHPercent().getVal();
        }
        return null;
    }
    
    public void setHPercent(final Integer percent) {
        if (percent == null) {
            if (this.view3D.isSetHPercent()) {
                this.view3D.unsetHPercent();
            }
        }
        else {
            if (percent < 5 || 500 < percent) {
                throw new IllegalArgumentException("percent must be between 5 and 500");
            }
            if (this.view3D.isSetHPercent()) {
                this.view3D.getHPercent().setVal((int)percent);
            }
            else {
                this.view3D.addNewHPercent().setVal((int)percent);
            }
        }
    }
}
