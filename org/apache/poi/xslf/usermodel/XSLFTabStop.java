package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.apache.poi.sl.usermodel.TabStop;

public class XSLFTabStop implements TabStop
{
    final CTTextTabStop tabStop;
    
    XSLFTabStop(final CTTextTabStop tabStop) {
        this.tabStop = tabStop;
    }
    
    public int getPosition() {
        return this.tabStop.getPos();
    }
    
    public void setPosition(final int position) {
        this.tabStop.setPos(position);
    }
    
    public double getPositionInPoints() {
        return Units.toPoints((long)this.getPosition());
    }
    
    public void setPositionInPoints(final double points) {
        this.setPosition(Units.toEMU(points));
    }
    
    public TabStop.TabStopType getType() {
        return TabStop.TabStopType.fromOoxmlId(this.tabStop.getAlgn().intValue());
    }
    
    public void setType(final TabStop.TabStopType tabStopType) {
        this.tabStop.setAlgn(STTextTabAlignType.Enum.forInt(tabStopType.ooxmlId));
    }
}
