package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import org.apache.poi.xdgf.util.ObjectFactory;

public class GeometryRowFactory
{
    static final ObjectFactory<GeometryRow, RowType> _rowTypes;
    
    public static GeometryRow load(final RowType row) {
        return GeometryRowFactory._rowTypes.load(row.getT(), row);
    }
    
    static {
        _rowTypes = new ObjectFactory<GeometryRow, RowType>();
        try {
            GeometryRowFactory._rowTypes.put("ArcTo", ArcTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("Ellipse", Ellipse.class, RowType.class);
            GeometryRowFactory._rowTypes.put("EllipticalArcTo", EllipticalArcTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("InfiniteLine", InfiniteLine.class, RowType.class);
            GeometryRowFactory._rowTypes.put("LineTo", LineTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("MoveTo", MoveTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("NURBSTo", NURBSTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("PolylineTo", PolyLineTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("PolyLineTo", PolyLineTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("RelCubBezTo", RelCubBezTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("RelEllipticalArcTo", RelEllipticalArcTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("RelLineTo", RelLineTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("RelMoveTo", RelMoveTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("RelQuadBezTo", RelQuadBezTo.class, RowType.class);
            GeometryRowFactory._rowTypes.put("SplineKnot", SplineKnot.class, RowType.class);
            GeometryRowFactory._rowTypes.put("SplineStart", SplineStart.class, RowType.class);
        }
        catch (final NoSuchMethodException | SecurityException e) {
            throw new POIXMLException("Internal error", e);
        }
    }
}
