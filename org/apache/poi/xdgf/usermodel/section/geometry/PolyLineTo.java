package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.util.NotImplemented;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class PolyLineTo implements GeometryRow
{
    PolyLineTo _master;
    Double x;
    Double y;
    String a;
    Boolean deleted;
    
    public PolyLineTo(final RowType row) {
        if (row.isSetDel()) {
            this.deleted = row.getDel();
        }
        for (final CellType cell : row.getCellArray()) {
            final String cellName = cell.getN();
            if (cellName.equals("X")) {
                this.x = XDGFCell.parseDoubleValue(cell);
            }
            else if (cellName.equals("Y")) {
                this.y = XDGFCell.parseDoubleValue(cell);
            }
            else {
                if (!cellName.equals("A")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in ArcTo row");
                }
                this.a = cell.getV();
            }
        }
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
    
    public String getA() {
        return (this.a == null) ? this._master.a : this.a;
    }
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (PolyLineTo)row;
    }
    
    @NotImplemented
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        throw new POIXMLException("Polyline support not implemented");
    }
}
