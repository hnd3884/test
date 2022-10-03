package com.microsoft.sqlserver.jdbc;

public enum InternalSpatialDatatype
{
    POINT((byte)1, "POINT"), 
    LINESTRING((byte)2, "LINESTRING"), 
    POLYGON((byte)3, "POLYGON"), 
    MULTIPOINT((byte)4, "MULTIPOINT"), 
    MULTILINESTRING((byte)5, "MULTILINESTRING"), 
    MULTIPOLYGON((byte)6, "MULTIPOLYGON"), 
    GEOMETRYCOLLECTION((byte)7, "GEOMETRYCOLLECTION"), 
    CIRCULARSTRING((byte)8, "CIRCULARSTRING"), 
    COMPOUNDCURVE((byte)9, "COMPOUNDCURVE"), 
    CURVEPOLYGON((byte)10, "CURVEPOLYGON"), 
    FULLGLOBE((byte)11, "FULLGLOBE"), 
    INVALID_TYPE((byte)0, (String)null);
    
    private byte typeCode;
    private String typeName;
    private static final InternalSpatialDatatype[] VALUES;
    
    private InternalSpatialDatatype(final byte typeCode, final String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }
    
    byte getTypeCode() {
        return this.typeCode;
    }
    
    String getTypeName() {
        return this.typeName;
    }
    
    static InternalSpatialDatatype valueOf(final byte typeCode) {
        for (final InternalSpatialDatatype internalType : InternalSpatialDatatype.VALUES) {
            if (internalType.typeCode == typeCode) {
                return internalType;
            }
        }
        return InternalSpatialDatatype.INVALID_TYPE;
    }
    
    static {
        VALUES = values();
    }
}
