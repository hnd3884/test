package org.apache.poi.xdgf.usermodel.section;

import org.apache.poi.xdgf.usermodel.section.geometry.SplineKnot;
import org.apache.poi.xdgf.geom.SplineCollector;
import org.apache.poi.xdgf.usermodel.section.geometry.SplineStart;
import org.apache.poi.xdgf.usermodel.section.geometry.InfiniteLine;
import org.apache.poi.xdgf.usermodel.section.geometry.Ellipse;
import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import java.util.Iterator;
import java.util.Map;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRowFactory;
import org.apache.poi.ooxml.POIXMLException;
import java.util.TreeMap;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow;
import java.util.SortedMap;

public class GeometrySection extends XDGFSection
{
    GeometrySection _master;
    SortedMap<Long, GeometryRow> _rows;
    
    public GeometrySection(final SectionType section, final XDGFSheet containingSheet) {
        super(section, containingSheet);
        this._rows = new TreeMap<Long, GeometryRow>();
        for (final RowType row : section.getRowArray()) {
            if (this._rows.containsKey(row.getIX())) {
                throw new POIXMLException("Index element '" + row.getIX() + "' already exists");
            }
            this._rows.put(row.getIX(), GeometryRowFactory.load(row));
        }
    }
    
    @Override
    public void setupMaster(final XDGFSection master) {
        this._master = (GeometrySection)master;
        for (final Map.Entry<Long, GeometryRow> entry : this._rows.entrySet()) {
            final GeometryRow masterRow = this._master._rows.get(entry.getKey());
            if (masterRow != null) {
                try {
                    entry.getValue().setupMaster(masterRow);
                }
                catch (final ClassCastException ex) {}
            }
        }
    }
    
    public Boolean getNoShow() {
        final Boolean noShow = XDGFCell.maybeGetBoolean(this._cells, "NoShow");
        if (noShow != null) {
            return noShow;
        }
        if (this._master != null) {
            return this._master.getNoShow();
        }
        return false;
    }
    
    public Iterable<GeometryRow> getCombinedRows() {
        return new CombinedIterable<GeometryRow>(this._rows, (this._master == null) ? null : this._master._rows);
    }
    
    public Path2D.Double getPath(final XDGFShape parent) {
        final Iterator<GeometryRow> rows = this.getCombinedRows().iterator();
        GeometryRow first = rows.hasNext() ? rows.next() : null;
        if (first instanceof Ellipse) {
            return ((Ellipse)first).getPath();
        }
        if (first instanceof InfiniteLine) {
            return ((InfiniteLine)first).getPath();
        }
        if (first instanceof SplineStart) {
            throw new POIXMLException("SplineStart must be preceded by another type");
        }
        final Path2D.Double path = new Path2D.Double();
        SplineCollector renderer = null;
        while (true) {
            GeometryRow row;
            if (first != null) {
                row = first;
                first = null;
            }
            else {
                if (!rows.hasNext()) {
                    if (renderer != null) {
                        renderer.addToPath(path, parent);
                    }
                    return path;
                }
                row = rows.next();
            }
            if (row instanceof SplineStart) {
                if (renderer != null) {
                    throw new POIXMLException("SplineStart found multiple times!");
                }
                renderer = new SplineCollector((SplineStart)row);
            }
            else if (row instanceof SplineKnot) {
                if (renderer == null) {
                    throw new POIXMLException("SplineKnot found without SplineStart!");
                }
                renderer.addKnot((SplineKnot)row);
            }
            else {
                if (renderer != null) {
                    renderer.addToPath(path, parent);
                    renderer = null;
                }
                row.addToPath(path, parent);
            }
        }
    }
}
