package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;

public class XDDFDashStop
{
    private CTDashStop stop;
    
    @Internal
    protected XDDFDashStop(final CTDashStop stop) {
        this.stop = stop;
    }
    
    @Internal
    protected CTDashStop getXmlObject() {
        return this.stop;
    }
    
    public int getDashLength() {
        return this.stop.getD();
    }
    
    public void setDashLength(final int length) {
        this.stop.setD(length);
    }
    
    public int getSpaceLength() {
        return this.stop.getSp();
    }
    
    public void setSpaceLength(final int length) {
        this.stop.setSp(length);
    }
}
