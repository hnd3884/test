package com.microsoft.sqlserver.jdbc;

import java.util.List;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class Geography extends SQLServerSpatialDatatype
{
    protected Geography() {
    }
    
    protected Geography(final String wkt, final int srid) throws SQLServerException {
        if (null == wkt || wkt.length() <= 0) {
            this.throwIllegalWKT();
        }
        this.wkt = wkt;
        this.srid = srid;
        this.parseWKTForSerialization(this, this.currentWktPos, -1, false);
        this.serializeToWkb(false, this);
        this.isNull = false;
    }
    
    protected Geography(final byte[] wkb) throws SQLServerException {
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
    
    public static Geography STGeomFromText(final String wkt, final int srid) throws SQLServerException {
        return new Geography(wkt, srid);
    }
    
    public static Geography STGeomFromWKB(final byte[] wkb) throws SQLServerException {
        return new Geography(wkb);
    }
    
    public static Geography deserialize(final byte[] wkb) throws SQLServerException {
        return new Geography(wkb);
    }
    
    public static Geography parse(final String wkt) throws SQLServerException {
        return new Geography(wkt, 4326);
    }
    
    public static Geography point(final double lat, final double lon, final int srid) throws SQLServerException {
        return new Geography("POINT (" + lon + " " + lat + ")", srid);
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
    
    public Double getLatitude() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.yValues.length == 1) {
            return this.yValues[0];
        }
        return null;
    }
    
    public Double getLongitude() {
        if (null != this.internalType && this.internalType == InternalSpatialDatatype.POINT && this.xValues.length == 1) {
            return this.xValues[0];
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
    
    public String STGeographyType() {
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
