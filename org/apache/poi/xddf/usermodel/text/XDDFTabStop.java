package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Units;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;

public class XDDFTabStop
{
    private CTTextTabStop stop;
    
    @Internal
    protected XDDFTabStop(final CTTextTabStop stop) {
        this.stop = stop;
    }
    
    @Internal
    protected CTTextTabStop getXmlObject() {
        return this.stop;
    }
    
    public TabAlignment getAlignment() {
        if (this.stop.isSetAlgn()) {
            return TabAlignment.valueOf(this.stop.getAlgn());
        }
        return null;
    }
    
    public void setAlignment(final TabAlignment align) {
        if (align == null) {
            if (this.stop.isSetAlgn()) {
                this.stop.unsetAlgn();
            }
        }
        else {
            this.stop.setAlgn(align.underlying);
        }
    }
    
    public Double getPosition() {
        if (this.stop.isSetPos()) {
            return Units.toPoints((long)this.stop.getPos());
        }
        return null;
    }
    
    public void setPosition(final Double position) {
        if (position == null) {
            if (this.stop.isSetPos()) {
                this.stop.unsetPos();
            }
        }
        else {
            this.stop.setPos(Units.toEMU((double)position));
        }
    }
}
