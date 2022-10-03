package com.microsoft.sqlserver.jdbc;

import java.math.BigDecimal;
import java.util.Locale;
import java.text.MessageFormat;
import java.nio.ByteOrder;
import java.util.ArrayList;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Point;
import java.util.List;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Segment;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Shape;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Figure;
import java.nio.ByteBuffer;

abstract class SQLServerSpatialDatatype
{
    protected ByteBuffer buffer;
    protected InternalSpatialDatatype internalType;
    protected String wkt;
    protected String wktNoZM;
    protected byte[] wkb;
    protected byte[] wkbNoZM;
    protected int srid;
    protected byte version;
    protected int numberOfPoints;
    protected int numberOfFigures;
    protected int numberOfShapes;
    protected int numberOfSegments;
    protected StringBuffer WKTsb;
    protected StringBuffer WKTsbNoZM;
    protected int currentPointIndex;
    protected int currentFigureIndex;
    protected int currentSegmentIndex;
    protected int currentShapeIndex;
    protected double[] xValues;
    protected double[] yValues;
    protected double[] zValues;
    protected double[] mValues;
    protected Figure[] figures;
    protected Shape[] shapes;
    protected Segment[] segments;
    protected boolean hasZvalues;
    protected boolean hasMvalues;
    protected boolean isValid;
    protected boolean isSinglePoint;
    protected boolean isSingleLineSegment;
    protected boolean isLargerThanHemisphere;
    protected boolean isNull;
    protected final byte FA_INTERIOR_RING = 0;
    protected final byte FA_STROKE = 1;
    protected final byte FA_EXTERIOR_RING = 2;
    protected final byte FA_POINT = 0;
    protected final byte FA_LINE = 1;
    protected final byte FA_ARC = 2;
    protected final byte FA_COMPOSITE_CURVE = 3;
    protected int currentWktPos;
    protected List<Point> pointList;
    protected List<Figure> figureList;
    protected List<Shape> shapeList;
    protected List<Segment> segmentList;
    protected byte serializationProperties;
    private final byte SEGMENT_LINE = 0;
    private final byte SEGMENT_ARC = 1;
    private final byte SEGMENT_FIRST_LINE = 2;
    private final byte SEGMENT_FIRST_ARC = 3;
    private final byte hasZvaluesMask = 1;
    private final byte hasMvaluesMask = 2;
    private final byte isValidMask = 4;
    private final byte isSinglePointMask = 8;
    private final byte isSingleLineSegmentMask = 16;
    private final byte isLargerThanHemisphereMask = 32;
    private List<Integer> version_one_shape_indexes;
    
    SQLServerSpatialDatatype() {
        this.version = 1;
        this.currentPointIndex = 0;
        this.currentFigureIndex = 0;
        this.currentSegmentIndex = 0;
        this.currentShapeIndex = 0;
        this.hasZvalues = false;
        this.hasMvalues = false;
        this.isValid = true;
        this.isSinglePoint = false;
        this.isSingleLineSegment = false;
        this.isLargerThanHemisphere = false;
        this.isNull = true;
        this.currentWktPos = 0;
        this.pointList = new ArrayList<Point>();
        this.figureList = new ArrayList<Figure>();
        this.shapeList = new ArrayList<Shape>();
        this.segmentList = new ArrayList<Segment>();
        this.serializationProperties = 0;
        this.version_one_shape_indexes = new ArrayList<Integer>();
    }
    
    protected void serializeToWkb(final boolean excludeZMFromWKB, final SQLServerSpatialDatatype type) {
        final ByteBuffer buf = ByteBuffer.allocate(this.determineWkbCapacity(excludeZMFromWKB));
        this.createSerializationProperties();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(this.srid);
        buf.put(this.version);
        if (excludeZMFromWKB) {
            byte serializationPropertiesNoZM = this.serializationProperties;
            if (this.hasZvalues) {
                --serializationPropertiesNoZM;
            }
            if (this.hasMvalues) {
                serializationPropertiesNoZM -= 2;
            }
            buf.put(serializationPropertiesNoZM);
        }
        else {
            buf.put(this.serializationProperties);
        }
        if (!this.isSinglePoint && !this.isSingleLineSegment) {
            buf.putInt(this.numberOfPoints);
        }
        if (type instanceof Geometry) {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                buf.putDouble(this.xValues[i]);
                buf.putDouble(this.yValues[i]);
            }
        }
        else {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                buf.putDouble(this.yValues[i]);
                buf.putDouble(this.xValues[i]);
            }
        }
        if (!excludeZMFromWKB) {
            if (this.hasZvalues) {
                for (int i = 0; i < this.numberOfPoints; ++i) {
                    buf.putDouble(this.zValues[i]);
                }
            }
            if (this.hasMvalues) {
                for (int i = 0; i < this.numberOfPoints; ++i) {
                    buf.putDouble(this.mValues[i]);
                }
            }
        }
        if (this.isSinglePoint || this.isSingleLineSegment) {
            if (excludeZMFromWKB) {
                this.wkbNoZM = buf.array();
            }
            else {
                this.wkb = buf.array();
            }
            return;
        }
        buf.putInt(this.numberOfFigures);
        for (int i = 0; i < this.numberOfFigures; ++i) {
            buf.put(this.figures[i].getFiguresAttribute());
            buf.putInt(this.figures[i].getPointOffset());
        }
        buf.putInt(this.numberOfShapes);
        for (int i = 0; i < this.numberOfShapes; ++i) {
            buf.putInt(this.shapes[i].getParentOffset());
            buf.putInt(this.shapes[i].getFigureOffset());
            buf.put(this.shapes[i].getOpenGISType());
        }
        if (this.version == 2 && null != this.segments) {
            buf.putInt(this.numberOfSegments);
            for (int i = 0; i < this.numberOfSegments; ++i) {
                buf.put(this.segments[i].getSegmentType());
            }
        }
        if (excludeZMFromWKB) {
            this.wkbNoZM = buf.array();
        }
        else {
            this.wkb = buf.array();
        }
    }
    
    protected void parseWkb(final SQLServerSpatialDatatype type) throws SQLServerException {
        this.srid = this.readInt();
        this.version = this.readByte();
        this.serializationProperties = this.readByte();
        this.interpretSerializationPropBytes();
        this.readNumberOfPoints();
        this.readPoints(type);
        if (this.hasZvalues) {
            this.readZvalues();
        }
        if (this.hasMvalues) {
            this.readMvalues();
        }
        if (!this.isSinglePoint && !this.isSingleLineSegment) {
            this.readNumberOfFigures();
            this.readFigures();
            this.readNumberOfShapes();
            this.readShapes();
        }
        this.determineInternalType();
        if (this.buffer.hasRemaining() && this.version == 2 && this.internalType.getTypeCode() != 8 && this.internalType.getTypeCode() != 11) {
            this.readNumberOfSegments();
            this.readSegments();
        }
    }
    
    protected void constructWKT(final SQLServerSpatialDatatype sd, final InternalSpatialDatatype isd, final int pointIndexEnd, final int figureIndexEnd, final int segmentIndexEnd, final int shapeIndexEnd) throws SQLServerException {
        if (this.numberOfPoints != 0) {
            this.appendToWKTBuffers(isd.getTypeName());
            this.appendToWKTBuffers("(");
            switch (isd) {
                case POINT: {
                    this.constructPointWKT(this.currentPointIndex);
                    break;
                }
                case LINESTRING:
                case CIRCULARSTRING: {
                    this.constructLineWKT(this.currentPointIndex, pointIndexEnd);
                    break;
                }
                case POLYGON: {
                    this.constructShapeWKT(this.currentFigureIndex, figureIndexEnd);
                    break;
                }
                case MULTIPOINT:
                case MULTILINESTRING: {
                    this.constructMultiShapeWKT(this.currentShapeIndex, shapeIndexEnd);
                    break;
                }
                case COMPOUNDCURVE: {
                    this.constructCompoundcurveWKT(this.currentSegmentIndex, segmentIndexEnd, pointIndexEnd);
                    break;
                }
                case MULTIPOLYGON: {
                    this.constructMultipolygonWKT(this.currentShapeIndex, shapeIndexEnd);
                    break;
                }
                case GEOMETRYCOLLECTION: {
                    this.constructGeometryCollectionWKT(shapeIndexEnd);
                    break;
                }
                case CURVEPOLYGON: {
                    this.constructCurvepolygonWKT(this.currentFigureIndex, figureIndexEnd, this.currentSegmentIndex, segmentIndexEnd);
                    break;
                }
                default: {
                    this.throwIllegalWKTPosition();
                    break;
                }
            }
            this.appendToWKTBuffers(")");
            return;
        }
        if (isd.getTypeCode() == 11) {
            if (sd instanceof Geometry) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalTypeForGeometry"));
                throw new SQLServerException(form.format(new Object[] { "Fullglobe" }), null, 0, null);
            }
            this.appendToWKTBuffers("FULLGLOBE");
        }
        else {
            if (isd.getTypeCode() == 7 && this.currentShapeIndex != shapeIndexEnd - 1) {
                ++this.currentShapeIndex;
                this.appendToWKTBuffers(isd.getTypeName() + "(");
                this.constructWKT(this, InternalSpatialDatatype.valueOf(this.shapes[this.currentShapeIndex].getOpenGISType()), this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
                this.appendToWKTBuffers(")");
                return;
            }
            this.appendToWKTBuffers(isd.getTypeName() + " EMPTY");
        }
    }
    
    protected void parseWKTForSerialization(final SQLServerSpatialDatatype sd, final int startPos, final int parentShapeIndex, final boolean isGeoCollection) throws SQLServerException {
        while (this.hasMoreToken()) {
            if (startPos != 0) {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    return;
                }
                if (this.wkt.charAt(this.currentWktPos) == ',') {
                    ++this.currentWktPos;
                }
            }
            final String nextToken = this.getNextStringToken().toUpperCase(Locale.US);
            InternalSpatialDatatype isd = InternalSpatialDatatype.INVALID_TYPE;
            try {
                isd = InternalSpatialDatatype.valueOf(nextToken);
            }
            catch (final Exception e) {
                this.throwIllegalWKTPosition();
            }
            byte fa = 0;
            if (this.version == 1 && ("CIRCULARSTRING".equals(nextToken) || "COMPOUNDCURVE".equals(nextToken) || "CURVEPOLYGON".equals(nextToken))) {
                this.version = 2;
            }
            if ("FULLGLOBE".equals(nextToken)) {
                if (sd instanceof Geometry) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalTypeForGeometry"));
                    throw new SQLServerException(form.format(new Object[] { "Fullglobe" }), null, 0, null);
                }
                if (startPos != 0) {
                    this.throwIllegalWKTPosition();
                }
                this.shapeList.add(new Shape(parentShapeIndex, -1, isd.getTypeCode()));
                this.isLargerThanHemisphere = true;
                this.version = 2;
                break;
            }
            else {
                if (this.checkEmptyKeyword(parentShapeIndex, isd, false)) {
                    continue;
                }
                this.readOpenBracket();
                final String s = nextToken;
                switch (s) {
                    case "POINT": {
                        if (startPos == 0 && "POINT".equals(nextToken.toUpperCase())) {
                            this.isSinglePoint = true;
                            this.internalType = InternalSpatialDatatype.POINT;
                        }
                        if (isGeoCollection) {
                            this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                            this.figureList.add(new Figure((byte)1, this.pointList.size()));
                        }
                        this.readPointWkt();
                        break;
                    }
                    case "LINESTRING":
                    case "CIRCULARSTRING": {
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        fa = (byte)((isd.getTypeCode() == InternalSpatialDatatype.LINESTRING.getTypeCode()) ? 1 : 2);
                        this.figureList.add(new Figure(fa, this.pointList.size()));
                        this.readLineWkt();
                        if (startPos == 0 && "LINESTRING".equals(nextToken.toUpperCase()) && this.pointList.size() == 2) {
                            this.isSingleLineSegment = true;
                            break;
                        }
                        break;
                    }
                    case "POLYGON":
                    case "MULTIPOINT":
                    case "MULTILINESTRING": {
                        final int thisShapeIndex = this.shapeList.size();
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.readShapeWkt(thisShapeIndex, nextToken);
                        break;
                    }
                    case "MULTIPOLYGON": {
                        final int thisShapeIndex = this.shapeList.size();
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.readMultiPolygonWkt(thisShapeIndex, nextToken);
                        break;
                    }
                    case "COMPOUNDCURVE": {
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.figureList.add(new Figure((byte)3, this.pointList.size()));
                        this.readCompoundCurveWkt(true);
                        break;
                    }
                    case "CURVEPOLYGON": {
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.readCurvePolygon();
                        break;
                    }
                    case "GEOMETRYCOLLECTION": {
                        final int thisShapeIndex = this.shapeList.size();
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.parseWKTForSerialization(this, this.currentWktPos, thisShapeIndex, true);
                        break;
                    }
                    default: {
                        this.throwIllegalWKTPosition();
                        break;
                    }
                }
                this.readCloseBracket();
            }
        }
        this.populateStructures();
    }
    
    protected void constructPointWKT(final int pointIndex) {
        if (this.xValues[pointIndex] % 1.0 == 0.0) {
            this.appendToWKTBuffers((int)this.xValues[pointIndex]);
        }
        else {
            this.appendToWKTBuffers(this.xValues[pointIndex]);
        }
        this.appendToWKTBuffers(" ");
        if (this.yValues[pointIndex] % 1.0 == 0.0) {
            this.appendToWKTBuffers((int)this.yValues[pointIndex]);
        }
        else {
            this.appendToWKTBuffers(this.yValues[pointIndex]);
        }
        this.appendToWKTBuffers(" ");
        if (this.hasZvalues && !Double.isNaN(this.zValues[pointIndex])) {
            if (this.zValues[pointIndex] % 1.0 == 0.0) {
                this.WKTsb.append((long)this.zValues[pointIndex]);
            }
            else {
                this.WKTsb.append(this.zValues[pointIndex]);
            }
            this.WKTsb.append(" ");
        }
        else if (this.hasMvalues && !Double.isNaN(this.mValues[pointIndex])) {
            this.WKTsb.append("NULL ");
        }
        if (this.hasMvalues && !Double.isNaN(this.mValues[pointIndex])) {
            if (this.mValues[pointIndex] % 1.0 == 0.0) {
                this.WKTsb.append((long)this.mValues[pointIndex]);
            }
            else {
                this.WKTsb.append(this.mValues[pointIndex]);
            }
            this.WKTsb.append(" ");
        }
        ++this.currentPointIndex;
        this.WKTsb.setLength(this.WKTsb.length() - 1);
        this.WKTsbNoZM.setLength(this.WKTsbNoZM.length() - 1);
    }
    
    protected void constructLineWKT(final int pointStartIndex, final int pointEndIndex) {
        for (int i = pointStartIndex; i < pointEndIndex; ++i) {
            this.constructPointWKT(i);
            if (i != pointEndIndex - 1) {
                this.appendToWKTBuffers(", ");
            }
        }
    }
    
    protected void constructShapeWKT(final int figureStartIndex, final int figureEndIndex) {
        for (int i = figureStartIndex; i < figureEndIndex; ++i) {
            this.appendToWKTBuffers("(");
            if (i != this.numberOfFigures - 1) {
                this.constructLineWKT(this.figures[i].getPointOffset(), this.figures[i + 1].getPointOffset());
            }
            else {
                this.constructLineWKT(this.figures[i].getPointOffset(), this.numberOfPoints);
            }
            if (i != figureEndIndex - 1) {
                this.appendToWKTBuffers("), ");
            }
            else {
                this.appendToWKTBuffers(")");
            }
        }
    }
    
    protected void constructMultiShapeWKT(final int shapeStartIndex, final int shapeEndIndex) {
        for (int i = shapeStartIndex + 1; i < shapeEndIndex; ++i) {
            if (this.shapes[i].getFigureOffset() == -1) {
                this.appendToWKTBuffers("EMPTY");
            }
            else {
                this.constructShapeWKT(this.shapes[i].getFigureOffset(), this.shapes[i].getFigureOffset() + 1);
            }
            if (i != shapeEndIndex - 1) {
                this.appendToWKTBuffers(", ");
            }
        }
    }
    
    protected void constructCompoundcurveWKT(final int segmentStartIndex, final int segmentEndIndex, final int pointEndIndex) {
        for (int i = segmentStartIndex; i < segmentEndIndex; ++i) {
            final byte segment = this.segments[i].getSegmentType();
            this.constructSegmentWKT(i, segment, pointEndIndex);
            if (i == segmentEndIndex - 1) {
                this.appendToWKTBuffers(")");
                break;
            }
            switch (segment) {
                case 0:
                case 2: {
                    if (this.segments[i + 1].getSegmentType() != 0) {
                        this.appendToWKTBuffers("), ");
                        break;
                    }
                    break;
                }
                case 1:
                case 3: {
                    if (this.segments[i + 1].getSegmentType() != 1) {
                        this.appendToWKTBuffers("), ");
                        break;
                    }
                    break;
                }
                default: {
                    return;
                }
            }
        }
    }
    
    protected void constructMultipolygonWKT(final int shapeStartIndex, final int shapeEndIndex) {
        for (int i = shapeStartIndex + 1; i < shapeEndIndex; ++i) {
            int figureEndIndex = this.figures.length;
            if (this.shapes[i].getFigureOffset() == -1) {
                this.appendToWKTBuffers("EMPTY");
                if (i != shapeEndIndex - 1) {
                    this.appendToWKTBuffers(", ");
                }
            }
            else {
                final int figureStartIndex = this.shapes[i].getFigureOffset();
                if (i == this.shapes.length - 1) {
                    figureEndIndex = this.figures.length;
                }
                else {
                    for (int tempCurrentShapeIndex = i + 1; tempCurrentShapeIndex < this.shapes.length; ++tempCurrentShapeIndex) {
                        if (this.shapes[tempCurrentShapeIndex].getFigureOffset() != -1) {
                            figureEndIndex = this.shapes[tempCurrentShapeIndex].getFigureOffset();
                            break;
                        }
                    }
                }
                this.appendToWKTBuffers("(");
                for (int j = figureStartIndex; j < figureEndIndex; ++j) {
                    this.appendToWKTBuffers("(");
                    if (j == this.figures.length - 1) {
                        this.constructLineWKT(this.figures[j].getPointOffset(), this.numberOfPoints);
                    }
                    else {
                        this.constructLineWKT(this.figures[j].getPointOffset(), this.figures[j + 1].getPointOffset());
                    }
                    if (j == figureEndIndex - 1) {
                        this.appendToWKTBuffers(")");
                    }
                    else {
                        this.appendToWKTBuffers("), ");
                    }
                }
                this.appendToWKTBuffers(")");
                if (i != shapeEndIndex - 1) {
                    this.appendToWKTBuffers(", ");
                }
            }
        }
    }
    
    protected void constructCurvepolygonWKT(final int figureStartIndex, final int figureEndIndex, int segmentStartIndex, final int segmentEndIndex) {
        for (int i = figureStartIndex; i < figureEndIndex; ++i) {
            switch (this.figures[i].getFiguresAttribute()) {
                case 1: {
                    this.appendToWKTBuffers("(");
                    if (i == this.figures.length - 1) {
                        this.constructLineWKT(this.currentPointIndex, this.numberOfPoints);
                    }
                    else {
                        this.constructLineWKT(this.currentPointIndex, this.figures[i + 1].getPointOffset());
                    }
                    this.appendToWKTBuffers(")");
                    break;
                }
                case 2: {
                    this.appendToWKTBuffers("CIRCULARSTRING(");
                    if (i == this.figures.length - 1) {
                        this.constructLineWKT(this.currentPointIndex, this.numberOfPoints);
                    }
                    else {
                        this.constructLineWKT(this.currentPointIndex, this.figures[i + 1].getPointOffset());
                    }
                    this.appendToWKTBuffers(")");
                    break;
                }
                case 3: {
                    this.appendToWKTBuffers("COMPOUNDCURVE(");
                    int pointEndIndex = 0;
                    if (i == this.figures.length - 1) {
                        pointEndIndex = this.numberOfPoints;
                    }
                    else {
                        pointEndIndex = this.figures[i + 1].getPointOffset();
                    }
                    while (this.currentPointIndex < pointEndIndex) {
                        final byte segment = this.segments[segmentStartIndex].getSegmentType();
                        this.constructSegmentWKT(segmentStartIndex, segment, pointEndIndex);
                        if (this.currentPointIndex >= pointEndIndex) {
                            this.appendToWKTBuffers("))");
                        }
                        else {
                            switch (segment) {
                                case 0:
                                case 2: {
                                    if (this.segments[segmentStartIndex + 1].getSegmentType() != 0) {
                                        this.appendToWKTBuffers("), ");
                                        break;
                                    }
                                    break;
                                }
                                case 1:
                                case 3: {
                                    if (this.segments[segmentStartIndex + 1].getSegmentType() != 1) {
                                        this.appendToWKTBuffers("), ");
                                        break;
                                    }
                                    break;
                                }
                                default: {
                                    return;
                                }
                            }
                        }
                        ++segmentStartIndex;
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            if (i != figureEndIndex - 1) {
                this.appendToWKTBuffers(", ");
            }
        }
    }
    
    protected void constructSegmentWKT(final int currentSegment, final byte segment, final int pointEndIndex) {
        switch (segment) {
            case 0: {
                this.appendToWKTBuffers(", ");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 1);
                if (currentSegment == this.segments.length - 1) {
                    break;
                }
                if (this.segments[currentSegment + 1].getSegmentType() != 0) {
                    --this.currentPointIndex;
                    this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                    break;
                }
                break;
            }
            case 1: {
                this.appendToWKTBuffers(", ");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 2);
                if (currentSegment == this.segments.length - 1) {
                    break;
                }
                if (this.segments[currentSegment + 1].getSegmentType() != 1) {
                    --this.currentPointIndex;
                    this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                    break;
                }
                break;
            }
            case 2: {
                this.appendToWKTBuffers("(");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 2);
                if (currentSegment == this.segments.length - 1) {
                    break;
                }
                if (this.segments[currentSegment + 1].getSegmentType() != 0) {
                    --this.currentPointIndex;
                    this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                    break;
                }
                break;
            }
            case 3: {
                this.appendToWKTBuffers("CIRCULARSTRING(");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 3);
                if (currentSegment == this.segments.length - 1) {
                    break;
                }
                if (this.segments[currentSegment + 1].getSegmentType() != 1) {
                    --this.currentPointIndex;
                    this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                    break;
                }
                break;
            }
            default: {}
        }
    }
    
    protected void constructGeometryCollectionWKT(final int shapeEndIndex) throws SQLServerException {
        ++this.currentShapeIndex;
        this.constructGeometryCollectionWKThelper(shapeEndIndex);
    }
    
    protected void readPointWkt() throws SQLServerException {
        int numOfCoordinates = 0;
        final double[] coords = new double[4];
        for (int i = 0; i < coords.length; ++i) {
            coords[i] = Double.NaN;
        }
        while (numOfCoordinates < 4) {
            double sign = 1.0;
            if (this.wkt.charAt(this.currentWktPos) == '-') {
                sign = -1.0;
                ++this.currentWktPos;
            }
            final int startPos = this.currentWktPos;
            if (this.wkt.charAt(this.currentWktPos) == ')') {
                break;
            }
            while (this.currentWktPos < this.wkt.length() && (Character.isDigit(this.wkt.charAt(this.currentWktPos)) || this.wkt.charAt(this.currentWktPos) == '.' || this.wkt.charAt(this.currentWktPos) == 'E' || this.wkt.charAt(this.currentWktPos) == 'e')) {
                ++this.currentWktPos;
            }
            try {
                coords[numOfCoordinates] = sign * new BigDecimal(this.wkt.substring(startPos, this.currentWktPos)).doubleValue();
                if (numOfCoordinates == 2) {
                    this.hasZvalues = true;
                }
                else if (numOfCoordinates == 3) {
                    this.hasMvalues = true;
                }
            }
            catch (final Exception e) {
                if (this.wkt.length() > this.currentWktPos + 3 && "null".equalsIgnoreCase(this.wkt.substring(this.currentWktPos, this.currentWktPos + 4))) {
                    coords[numOfCoordinates] = Double.NaN;
                    this.currentWktPos += 4;
                }
                else {
                    this.throwIllegalWKTPosition();
                }
            }
            ++numOfCoordinates;
            this.skipWhiteSpaces();
            if (numOfCoordinates == 4 && this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) != ',' && this.wkt.charAt(this.currentWktPos) != ')') {
                this.throwIllegalWKTPosition();
            }
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                if (numOfCoordinates == 1) {
                    this.throwIllegalWKTPosition();
                }
                ++this.currentWktPos;
                this.skipWhiteSpaces();
                break;
            }
            this.skipWhiteSpaces();
        }
        this.pointList.add(new Point(coords[0], coords[1], coords[2], coords[3]));
    }
    
    protected void readLineWkt() throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            this.readPointWkt();
        }
    }
    
    protected void readShapeWkt(final int parentShapeIndex, final String nextToken) throws SQLServerException {
        byte fa = 0;
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (!"POLYGON".equals(nextToken) && this.checkEmptyKeyword(parentShapeIndex, InternalSpatialDatatype.valueOf(nextToken), true)) {
                continue;
            }
            if ("MULTIPOINT".equals(nextToken)) {
                this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), InternalSpatialDatatype.POINT.getTypeCode()));
            }
            else if ("MULTILINESTRING".equals(nextToken)) {
                this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), InternalSpatialDatatype.LINESTRING.getTypeCode()));
            }
            if (this.version == 1) {
                if ("MULTIPOINT".equals(nextToken)) {
                    fa = 1;
                }
                else if ("MULTILINESTRING".equals(nextToken) || "POLYGON".equals(nextToken)) {
                    fa = 2;
                }
                this.version_one_shape_indexes.add(this.figureList.size());
            }
            else if (this.version == 2 && ("MULTIPOINT".equals(nextToken) || "MULTILINESTRING".equals(nextToken) || "POLYGON".equals(nextToken) || "MULTIPOLYGON".equals(nextToken))) {
                fa = 1;
            }
            this.figureList.add(new Figure(fa, this.pointList.size()));
            this.readOpenBracket();
            this.readLineWkt();
            this.readCloseBracket();
            this.skipWhiteSpaces();
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
            }
            else {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    continue;
                }
                this.throwIllegalWKTPosition();
            }
        }
    }
    
    protected void readCurvePolygon() throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            final String nextPotentialToken = this.getNextStringToken().toUpperCase(Locale.US);
            if ("CIRCULARSTRING".equals(nextPotentialToken)) {
                this.figureList.add(new Figure((byte)2, this.pointList.size()));
                this.readOpenBracket();
                this.readLineWkt();
                this.readCloseBracket();
            }
            else if ("COMPOUNDCURVE".equals(nextPotentialToken)) {
                this.figureList.add(new Figure((byte)3, this.pointList.size()));
                this.readOpenBracket();
                this.readCompoundCurveWkt(true);
                this.readCloseBracket();
            }
            else if (this.wkt.charAt(this.currentWktPos) == '(') {
                this.figureList.add(new Figure((byte)1, this.pointList.size()));
                this.readOpenBracket();
                this.readLineWkt();
                this.readCloseBracket();
            }
            else {
                this.throwIllegalWKTPosition();
            }
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
            }
            else {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    continue;
                }
                this.throwIllegalWKTPosition();
            }
        }
    }
    
    protected void readMultiPolygonWkt(final int thisShapeIndex, final String nextToken) throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (this.checkEmptyKeyword(thisShapeIndex, InternalSpatialDatatype.valueOf(nextToken), true)) {
                continue;
            }
            this.shapeList.add(new Shape(thisShapeIndex, this.figureList.size(), InternalSpatialDatatype.POLYGON.getTypeCode()));
            this.readOpenBracket();
            this.readShapeWkt(thisShapeIndex, nextToken);
            this.readCloseBracket();
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
            }
            else {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    continue;
                }
                this.throwIllegalWKTPosition();
            }
        }
    }
    
    protected void readSegmentWkt(final int segmentType, final boolean isFirstIteration) throws SQLServerException {
        this.segmentList.add(new Segment((byte)segmentType));
        int segmentLength = segmentType;
        if (segmentLength < 2) {
            ++segmentLength;
        }
        for (int i = 0; i < segmentLength; ++i) {
            if (i == 0 && !isFirstIteration && segmentType >= 2) {
                this.skipFirstPointWkt();
            }
            else {
                this.readPointWkt();
            }
        }
        if (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (segmentType == 3 || segmentType == 1) {
                this.readSegmentWkt(1, false);
            }
            else if (segmentType == 2 || segmentType == 0) {
                this.readSegmentWkt(0, false);
            }
        }
    }
    
    protected void readCompoundCurveWkt(boolean isFirstIteration) throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            final String nextPotentialToken = this.getNextStringToken().toUpperCase(Locale.US);
            if ("CIRCULARSTRING".equals(nextPotentialToken)) {
                this.readOpenBracket();
                this.readSegmentWkt(3, isFirstIteration);
                this.readCloseBracket();
            }
            else if (this.wkt.charAt(this.currentWktPos) == '(') {
                this.readOpenBracket();
                this.readSegmentWkt(2, isFirstIteration);
                this.readCloseBracket();
            }
            else {
                this.throwIllegalWKTPosition();
            }
            isFirstIteration = false;
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
            }
            else {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    continue;
                }
                this.throwIllegalWKTPosition();
            }
        }
    }
    
    protected String getNextStringToken() {
        this.skipWhiteSpaces();
        int endIndex;
        for (endIndex = this.currentWktPos; endIndex < this.wkt.length() && Character.isLetter(this.wkt.charAt(endIndex)); ++endIndex) {}
        final int temp = this.currentWktPos;
        this.currentWktPos = endIndex;
        this.skipWhiteSpaces();
        return this.wkt.substring(temp, endIndex);
    }
    
    protected void populateStructures() {
        if (this.pointList.size() > 0) {
            this.xValues = new double[this.pointList.size()];
            this.yValues = new double[this.pointList.size()];
            for (int i = 0; i < this.pointList.size(); ++i) {
                this.xValues[i] = this.pointList.get(i).getX();
                this.yValues[i] = this.pointList.get(i).getY();
            }
            if (this.hasZvalues) {
                this.zValues = new double[this.pointList.size()];
                for (int i = 0; i < this.pointList.size(); ++i) {
                    this.zValues[i] = this.pointList.get(i).getZ();
                }
            }
            if (this.hasMvalues) {
                this.mValues = new double[this.pointList.size()];
                for (int i = 0; i < this.pointList.size(); ++i) {
                    this.mValues[i] = this.pointList.get(i).getM();
                }
            }
        }
        if (this.version == 2) {
            for (int i = 0; i < this.version_one_shape_indexes.size(); ++i) {
                this.figureList.get(this.version_one_shape_indexes.get(i)).setFiguresAttribute((byte)1);
            }
        }
        if (this.figureList.size() > 0) {
            this.figures = new Figure[this.figureList.size()];
            for (int i = 0; i < this.figureList.size(); ++i) {
                this.figures[i] = this.figureList.get(i);
            }
        }
        if (this.pointList.size() == 0 && this.shapeList.size() > 0 && this.shapeList.get(0).getOpenGISType() == 7) {
            this.shapeList.get(0).setFigureOffset(-1);
        }
        if (this.shapeList.size() > 0) {
            this.shapes = new Shape[this.shapeList.size()];
            for (int i = 0; i < this.shapeList.size(); ++i) {
                this.shapes[i] = this.shapeList.get(i);
            }
        }
        if (this.segmentList.size() > 0) {
            this.segments = new Segment[this.segmentList.size()];
            for (int i = 0; i < this.segmentList.size(); ++i) {
                this.segments[i] = this.segmentList.get(i);
            }
        }
        this.numberOfPoints = this.pointList.size();
        this.numberOfFigures = this.figureList.size();
        this.numberOfShapes = this.shapeList.size();
        this.numberOfSegments = this.segmentList.size();
    }
    
    protected void readOpenBracket() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == '(') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        }
        else {
            this.throwIllegalWKTPosition();
        }
    }
    
    protected void readCloseBracket() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == ')') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        }
        else {
            this.throwIllegalWKTPosition();
        }
    }
    
    protected boolean hasMoreToken() {
        this.skipWhiteSpaces();
        return this.currentWktPos < this.wkt.length();
    }
    
    protected void createSerializationProperties() {
        this.serializationProperties = 0;
        if (this.hasZvalues) {
            ++this.serializationProperties;
        }
        if (this.hasMvalues) {
            this.serializationProperties += 2;
        }
        if (this.isValid) {
            this.serializationProperties += 4;
        }
        if (this.isSinglePoint) {
            this.serializationProperties += 8;
        }
        if (this.isSingleLineSegment) {
            this.serializationProperties += 16;
        }
        if (this.version == 2 && this.isLargerThanHemisphere) {
            this.serializationProperties += 32;
        }
    }
    
    protected int determineWkbCapacity(final boolean excludeZMFromWKB) {
        int totalSize = 0;
        totalSize += 6;
        if (this.isSinglePoint || this.isSingleLineSegment) {
            totalSize += 16 * this.numberOfPoints;
            if (!excludeZMFromWKB) {
                if (this.hasZvalues) {
                    totalSize += 8 * this.numberOfPoints;
                }
                if (this.hasMvalues) {
                    totalSize += 8 * this.numberOfPoints;
                }
            }
            return totalSize;
        }
        int pointSize = 16;
        if (!excludeZMFromWKB) {
            if (this.hasZvalues) {
                pointSize += 8;
            }
            if (this.hasMvalues) {
                pointSize += 8;
            }
        }
        totalSize += 12;
        totalSize += this.numberOfPoints * pointSize;
        totalSize += this.numberOfFigures * 5;
        totalSize += this.numberOfShapes * 9;
        if (this.version == 2) {
            totalSize += 4;
            totalSize += this.numberOfSegments;
        }
        return totalSize;
    }
    
    protected void appendToWKTBuffers(final Object o) {
        this.WKTsb.append(o);
        this.WKTsbNoZM.append(o);
    }
    
    protected void interpretSerializationPropBytes() {
        this.hasZvalues = ((this.serializationProperties & 0x1) != 0x0);
        this.hasMvalues = ((this.serializationProperties & 0x2) != 0x0);
        this.isValid = ((this.serializationProperties & 0x4) != 0x0);
        this.isSinglePoint = ((this.serializationProperties & 0x8) != 0x0);
        this.isSingleLineSegment = ((this.serializationProperties & 0x10) != 0x0);
        this.isLargerThanHemisphere = ((this.serializationProperties & 0x20) != 0x0);
    }
    
    protected void readNumberOfPoints() throws SQLServerException {
        if (this.isSinglePoint) {
            this.numberOfPoints = 1;
        }
        else if (this.isSingleLineSegment) {
            this.numberOfPoints = 2;
        }
        else {
            this.checkNegSize(this.numberOfPoints = this.readInt());
        }
    }
    
    protected void readZvalues() throws SQLServerException {
        this.zValues = new double[this.numberOfPoints];
        for (int i = 0; i < this.numberOfPoints; ++i) {
            this.zValues[i] = this.readDouble();
        }
    }
    
    protected void readMvalues() throws SQLServerException {
        this.mValues = new double[this.numberOfPoints];
        for (int i = 0; i < this.numberOfPoints; ++i) {
            this.mValues[i] = this.readDouble();
        }
    }
    
    protected void readNumberOfFigures() throws SQLServerException {
        this.checkNegSize(this.numberOfFigures = this.readInt());
    }
    
    protected void readFigures() throws SQLServerException {
        this.figures = new Figure[this.numberOfFigures];
        for (int i = 0; i < this.numberOfFigures; ++i) {
            final byte fa = this.readByte();
            final int po = this.readInt();
            this.figures[i] = new Figure(fa, po);
        }
    }
    
    protected void readNumberOfShapes() throws SQLServerException {
        this.checkNegSize(this.numberOfShapes = this.readInt());
    }
    
    protected void readShapes() throws SQLServerException {
        this.shapes = new Shape[this.numberOfShapes];
        for (int i = 0; i < this.numberOfShapes; ++i) {
            final int po = this.readInt();
            final int fo = this.readInt();
            final byte ogt = this.readByte();
            this.shapes[i] = new Shape(po, fo, ogt);
        }
    }
    
    protected void readNumberOfSegments() throws SQLServerException {
        this.checkNegSize(this.numberOfSegments = this.readInt());
    }
    
    protected void readSegments() throws SQLServerException {
        this.segments = new Segment[this.numberOfSegments];
        for (int i = 0; i < this.numberOfSegments; ++i) {
            final byte st = this.readByte();
            this.segments[i] = new Segment(st);
        }
    }
    
    protected void determineInternalType() {
        if (this.isSinglePoint) {
            this.internalType = InternalSpatialDatatype.POINT;
        }
        else if (this.isSingleLineSegment) {
            this.internalType = InternalSpatialDatatype.LINESTRING;
        }
        else {
            this.internalType = InternalSpatialDatatype.valueOf(this.shapes[0].getOpenGISType());
        }
    }
    
    protected boolean checkEmptyKeyword(final int parentShapeIndex, final InternalSpatialDatatype isd, final boolean isInsideAnotherShape) throws SQLServerException {
        final String potentialEmptyKeyword = this.getNextStringToken().toUpperCase(Locale.US);
        if ("EMPTY".equals(potentialEmptyKeyword)) {
            byte typeCode = 0;
            if (isInsideAnotherShape) {
                final byte parentTypeCode = isd.getTypeCode();
                if (parentTypeCode == 4) {
                    typeCode = InternalSpatialDatatype.POINT.getTypeCode();
                }
                else if (parentTypeCode == 5) {
                    typeCode = InternalSpatialDatatype.LINESTRING.getTypeCode();
                }
                else if (parentTypeCode == 6) {
                    typeCode = InternalSpatialDatatype.POLYGON.getTypeCode();
                }
                else {
                    if (parentTypeCode != 7) {
                        final String strError = SQLServerException.getErrString("R_illegalWKT");
                        throw new SQLServerException(strError, null, 0, null);
                    }
                    typeCode = InternalSpatialDatatype.GEOMETRYCOLLECTION.getTypeCode();
                }
            }
            else {
                typeCode = isd.getTypeCode();
            }
            this.shapeList.add(new Shape(parentShapeIndex, -1, typeCode));
            this.skipWhiteSpaces();
            if (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) == ',') {
                ++this.currentWktPos;
                this.skipWhiteSpaces();
            }
            return true;
        }
        if (!"".equals(potentialEmptyKeyword)) {
            this.throwIllegalWKTPosition();
        }
        return false;
    }
    
    protected void throwIllegalWKT() throws SQLServerException {
        final String strError = SQLServerException.getErrString("R_illegalWKT");
        throw new SQLServerException(strError, null, 0, null);
    }
    
    protected void throwIllegalWKB() throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
        final Object[] msgArgs = { JDBCType.VARBINARY };
        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
    }
    
    private void incrementPointNumStartIfPointNotReused(final int pointEndIndex) {
        if (this.currentPointIndex + 1 >= pointEndIndex) {
            ++this.currentPointIndex;
        }
    }
    
    private void constructGeometryCollectionWKThelper(final int shapeEndIndex) throws SQLServerException {
        while (this.currentShapeIndex < shapeEndIndex) {
            final InternalSpatialDatatype isd = InternalSpatialDatatype.valueOf(this.shapes[this.currentShapeIndex].getOpenGISType());
            final int figureIndex = this.shapes[this.currentShapeIndex].getFigureOffset();
            int pointIndexEnd = this.numberOfPoints;
            int figureIndexEnd = this.numberOfFigures;
            int segmentIndexEnd = this.numberOfSegments;
            int shapeIndexEnd = this.numberOfShapes;
            int figureIndexIncrement = 0;
            int segmentIndexIncrement = 0;
            int shapeIndexIncrement = 0;
            int localCurrentSegmentIndex = 0;
            int localCurrentShapeIndex = 0;
            switch (isd) {
                case POINT: {
                    ++figureIndexIncrement;
                    ++this.currentShapeIndex;
                    break;
                }
                case LINESTRING:
                case CIRCULARSTRING: {
                    ++figureIndexIncrement;
                    ++this.currentShapeIndex;
                    pointIndexEnd = this.figures[figureIndex + 1].getPointOffset();
                    break;
                }
                case POLYGON:
                case CURVEPOLYGON: {
                    if (this.currentShapeIndex < this.shapes.length - 1) {
                        figureIndexEnd = this.shapes[this.currentShapeIndex + 1].getFigureOffset();
                    }
                    figureIndexIncrement = figureIndexEnd - this.currentFigureIndex;
                    ++this.currentShapeIndex;
                    localCurrentSegmentIndex = this.currentSegmentIndex;
                    if (isd.equals(InternalSpatialDatatype.CURVEPOLYGON)) {
                        for (int i = this.currentFigureIndex; i < figureIndexEnd; ++i) {
                            if (this.figures[i].getFiguresAttribute() == 3) {
                                int pointOffsetEnd;
                                if (i == this.figures.length - 1) {
                                    pointOffsetEnd = this.numberOfPoints;
                                }
                                else {
                                    pointOffsetEnd = this.figures[i + 1].getPointOffset();
                                }
                                final int increment = this.calculateSegmentIncrement(localCurrentSegmentIndex, pointOffsetEnd - this.figures[i].getPointOffset());
                                segmentIndexIncrement += increment;
                                localCurrentSegmentIndex += increment;
                            }
                        }
                    }
                    segmentIndexEnd = localCurrentSegmentIndex;
                    break;
                }
                case MULTIPOINT:
                case MULTILINESTRING:
                case MULTIPOLYGON: {
                    final int thisShapesParentOffset = this.shapes[this.currentShapeIndex].getParentOffset();
                    int tempShapeIndex = this.currentShapeIndex;
                    ++tempShapeIndex;
                    while (tempShapeIndex < this.shapes.length && this.shapes[tempShapeIndex].getParentOffset() != thisShapesParentOffset) {
                        if (tempShapeIndex != this.shapes.length - 1 && this.shapes[tempShapeIndex + 1].getFigureOffset() != -1) {
                            figureIndexEnd = this.shapes[tempShapeIndex + 1].getFigureOffset();
                        }
                        ++tempShapeIndex;
                    }
                    figureIndexIncrement = figureIndexEnd - this.currentFigureIndex;
                    shapeIndexIncrement = tempShapeIndex - this.currentShapeIndex;
                    shapeIndexEnd = tempShapeIndex;
                    break;
                }
                case GEOMETRYCOLLECTION: {
                    this.appendToWKTBuffers(isd.getTypeName());
                    if (this.shapes[this.currentShapeIndex].getFigureOffset() == -1) {
                        this.appendToWKTBuffers(" EMPTY");
                        ++this.currentShapeIndex;
                        if (this.currentShapeIndex < shapeEndIndex) {
                            this.appendToWKTBuffers(", ");
                            continue;
                        }
                        continue;
                    }
                    else {
                        this.appendToWKTBuffers("(");
                        int geometryCollectionParentIndex;
                        for (geometryCollectionParentIndex = this.shapes[this.currentShapeIndex].getParentOffset(), localCurrentShapeIndex = this.currentShapeIndex; localCurrentShapeIndex < this.shapes.length - 1 && this.shapes[localCurrentShapeIndex + 1].getParentOffset() > geometryCollectionParentIndex; ++localCurrentShapeIndex) {}
                        ++localCurrentShapeIndex;
                        ++this.currentShapeIndex;
                        this.constructGeometryCollectionWKThelper(localCurrentShapeIndex);
                        if (this.currentShapeIndex < shapeEndIndex) {
                            this.appendToWKTBuffers("), ");
                            continue;
                        }
                        this.appendToWKTBuffers(")");
                        continue;
                    }
                    break;
                }
                case COMPOUNDCURVE: {
                    if (this.currentFigureIndex == this.figures.length - 1) {
                        pointIndexEnd = this.numberOfPoints;
                    }
                    else {
                        pointIndexEnd = this.figures[this.currentFigureIndex + 1].getPointOffset();
                    }
                    final int increment2 = segmentIndexIncrement = this.calculateSegmentIncrement(this.currentSegmentIndex, pointIndexEnd - this.figures[this.currentFigureIndex].getPointOffset());
                    segmentIndexEnd = this.currentSegmentIndex + increment2;
                    ++figureIndexIncrement;
                    ++this.currentShapeIndex;
                    break;
                }
                case FULLGLOBE: {
                    this.appendToWKTBuffers("FULLGLOBE");
                    break;
                }
            }
            this.constructWKT(this, isd, pointIndexEnd, figureIndexEnd, segmentIndexEnd, shapeIndexEnd);
            this.currentFigureIndex += figureIndexIncrement;
            this.currentSegmentIndex += segmentIndexIncrement;
            this.currentShapeIndex += shapeIndexIncrement;
            if (this.currentShapeIndex < shapeEndIndex) {
                this.appendToWKTBuffers(", ");
            }
        }
    }
    
    private int calculateSegmentIncrement(int segmentStart, int pointDifference) {
        int segmentIncrement = 0;
        while (pointDifference > 0) {
            switch (this.segments[segmentStart].getSegmentType()) {
                case 0: {
                    --pointDifference;
                    if (segmentStart == this.segments.length - 1) {
                        break;
                    }
                    if (pointDifference < 1) {
                        break;
                    }
                    if (this.segments[segmentStart + 1].getSegmentType() != 0) {
                        ++pointDifference;
                        break;
                    }
                    break;
                }
                case 1: {
                    pointDifference -= 2;
                    if (segmentStart == this.segments.length - 1) {
                        break;
                    }
                    if (pointDifference < 1) {
                        break;
                    }
                    if (this.segments[segmentStart + 1].getSegmentType() != 1) {
                        ++pointDifference;
                        break;
                    }
                    break;
                }
                case 2: {
                    pointDifference -= 2;
                    if (segmentStart == this.segments.length - 1) {
                        break;
                    }
                    if (pointDifference < 1) {
                        break;
                    }
                    if (this.segments[segmentStart + 1].getSegmentType() != 0) {
                        ++pointDifference;
                        break;
                    }
                    break;
                }
                case 3: {
                    pointDifference -= 3;
                    if (segmentStart == this.segments.length - 1) {
                        break;
                    }
                    if (pointDifference < 1) {
                        break;
                    }
                    if (this.segments[segmentStart + 1].getSegmentType() != 1) {
                        ++pointDifference;
                        break;
                    }
                    break;
                }
                default: {
                    return segmentIncrement;
                }
            }
            ++segmentStart;
            ++segmentIncrement;
        }
        return segmentIncrement;
    }
    
    private void skipFirstPointWkt() {
        for (int numOfCoordinates = 0; numOfCoordinates < 4; ++numOfCoordinates) {
            if (this.wkt.charAt(this.currentWktPos) == '-') {
                ++this.currentWktPos;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') {
                break;
            }
            while (this.currentWktPos < this.wkt.length() && (Character.isDigit(this.wkt.charAt(this.currentWktPos)) || this.wkt.charAt(this.currentWktPos) == '.' || this.wkt.charAt(this.currentWktPos) == 'E' || this.wkt.charAt(this.currentWktPos) == 'e')) {
                ++this.currentWktPos;
            }
            this.skipWhiteSpaces();
            if (this.wkt.charAt(this.currentWktPos) == ',') {
                ++this.currentWktPos;
                this.skipWhiteSpaces();
                ++numOfCoordinates;
                break;
            }
            this.skipWhiteSpaces();
        }
    }
    
    private void readComma() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == ',') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        }
        else {
            this.throwIllegalWKTPosition();
        }
    }
    
    private void skipWhiteSpaces() {
        while (this.currentWktPos < this.wkt.length() && Character.isWhitespace(this.wkt.charAt(this.currentWktPos))) {
            ++this.currentWktPos;
        }
    }
    
    private void checkNegSize(final int num) throws SQLServerException {
        if (num < 0) {
            this.throwIllegalWKB();
        }
    }
    
    private void readPoints(final SQLServerSpatialDatatype type) throws SQLServerException {
        this.xValues = new double[this.numberOfPoints];
        this.yValues = new double[this.numberOfPoints];
        if (type instanceof Geometry) {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                this.xValues[i] = this.readDouble();
                this.yValues[i] = this.readDouble();
            }
        }
        else {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                this.yValues[i] = this.readDouble();
                this.xValues[i] = this.readDouble();
            }
        }
    }
    
    private void checkBuffer(final int i) throws SQLServerException {
        if (this.buffer.remaining() < i) {
            this.throwIllegalWKB();
        }
    }
    
    private boolean checkSQLLength(final int length) throws SQLServerException {
        if (null == this.wkt || this.wkt.length() < length) {
            this.throwIllegalWKTPosition();
        }
        return true;
    }
    
    private void throwIllegalWKTPosition() throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalWKTposition"));
        throw new SQLServerException(form.format(new Object[] { this.currentWktPos }), null, 0, null);
    }
    
    protected byte readByte() throws SQLServerException {
        this.checkBuffer(1);
        return this.buffer.get();
    }
    
    protected int readInt() throws SQLServerException {
        this.checkBuffer(4);
        return this.buffer.getInt();
    }
    
    protected double readDouble() throws SQLServerException {
        this.checkBuffer(8);
        return this.buffer.getDouble();
    }
    
    public List<Point> getPointList() {
        return this.pointList;
    }
    
    public List<Figure> getFigureList() {
        return this.figureList;
    }
    
    public List<Shape> getShapeList() {
        return this.shapeList;
    }
    
    public List<Segment> getSegmentList() {
        return this.segmentList;
    }
}
