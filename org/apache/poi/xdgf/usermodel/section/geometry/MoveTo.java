package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class MoveTo implements GeometryRow
{
    MoveTo _master;
    Double x;
    Double y;
    Boolean deleted;
    
    public MoveTo(final RowType row) {
        if (row.isSetDel()) {
            this.deleted = row.getDel();
        }
        for (final CellType cell : row.getCellArray()) {
            final String cellName = cell.getN();
            if (cellName.equals("X")) {
                this.x = XDGFCell.parseDoubleValue(cell);
            }
            else {
                if (!cellName.equals("Y")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in MoveTo row");
                }
                this.y = XDGFCell.parseDoubleValue(cell);
            }
        }
    }
    
    @Override
    public String toString() {
        return "MoveTo: x=" + this.getX() + "; y=" + this.getY();
    }
    
    public boolean getDel() {
        if (this.deleted != null) {
            return this.deleted;
        }
        return this._master != null && this._master.getDel();
    }
    
    public Double getX() {
        return (this.x == null) ? this._master.x : this.x;
    }
    
    public Double getY() {
        return (this.y == null) ? this._master.y : this.y;
    }
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (MoveTo)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        path.moveTo(this.getX(), this.getY());
    }
}
