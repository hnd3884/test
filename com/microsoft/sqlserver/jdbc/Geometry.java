package com.microsoft.sqlserver.jdbc;

import java.util.List;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class Geometry extends SQLServerSpatialDatatype
{
    protected Geometry() {
    }
    
    protected Geometry(final String wkt, final int srid) throws SQLServerException {
        if (null == wkt || wkt.length() <= 0) {
            this.throwIllegalWKT();
        }
        this.wkt = wkt;
        this.srid = srid;
        this.parseWKTForSerialization(this, this.currentWktPos, -1, false);
        this.serializeToWkb(false, this);
        this.isNull = false;
    }
    
    protected Geometry(final byte[] wkb) throws SQLServerException {
        if (null == wkb || wkb.length <= 0) {
            this.throwIllegalWKB();
        }
        this.wkb = wkb;
        (this.buffer = ByteBuffer.wrap(wkb)).order(ByteOrder.LITTLE_ENDIAN);
        this.parseWkb(this);
        this.WKTsb = new StringBuffer();
        this.WKTsbNoZM = new StringBuffer();
        this.constructWKT(this, this.internalType, this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
        this.wkt = this.WKTsb.toString();
        this.wktNoZM = this.WKTsbNoZM.toString();
        this.isNull = false;
    }
    
    public static Geometry STGeomFromText(final String wkt, final int srid) throws SQLServerException {
        return new Geometry(wkt, srid);
    }
    
    public static Geometry STGeomFromWKB(final byte[] wkb) throws SQLServerException {
        return new Geometry(wkb);
    }
    
    public static Geometry deserialize(final byte[] wkb) throws SQLServerException {
        return new Geometry(wkb);
    }
    
    public static Geometry parse(final String wkt) throws SQLServerException {
        return new Geometry(wkt, 0);
    }
    
    public static Geometry point(final double x, final double y, final int srid) throws SQLServerException {
        return new Geometry("POINT (" + x + " " + y + ")", srid);
    }
    
    public String STAsText() throws SQLServerException {
        if (null == this.wktNoZM) {
            (this.buffer = ByteBuffer.wrap(this.wkb)).order(ByteOrder.LITTLE_ENDIAN);
            this.parseWkb(this);
            this.WKTsb = new StringBuffer();
            this.WKTsbNoZM = new StringBuffer();
            this.constructWKT(this, this.internalType, this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
            this.wktNoZM = this.WKTsbNoZM.toString();
        }
        return this.wktNoZM;
    }
    
    public byte[] STAsBinary() {
        if (null == this.wkbNoZM) {
            this.serializeToWkb(true, this);
        }
        return this.wkbNoZM;
    }
    
    public byte[] serialize() {
        return this.wkb;
    }
    
    public boolean hasM() {
        return this.hasMvalues;
    }
    
    public boolean hasZ() {
        return this.hasZvalues;
    }
    
    public Double getX() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.xValues.length == 1) {
            return this.xValues[0];
        }
        return null;
    }
    
    public Double getY() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.yValues.length == 1) {
            return this.yValues[0];
        }
        return null;
    }
    
    public Double getM() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.hasM()) {
            return this.mValues[0];
        }
        return null;
    }
    
    public Double getZ() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.hasZ()) {
            return this.zValues[0];
        }
        return null;
    }
    
    public int getSrid() {
        return this.srid;
    }
    
    public boolean isNull() {
        return this.isNull;
    }
    
    public int STNumPoints() {
        return this.numberOfPoints;
    }
    
    public String STGeometryType() {
        if (null != this.internalType) {
            return this.internalType.getTypeName();
        }
        return null;
    }
    
    public String asTextZM() {
        return this.wkt;
    }
    
    @Override
    public String toString() {
        return this.wkt;
    }
}
