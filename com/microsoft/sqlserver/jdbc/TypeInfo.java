package com.microsoft.sqlserver.jdbc;

import java.util.EnumMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.io.Serializable;

final class TypeInfo implements Serializable
{
    private static final long serialVersionUID = 6641910171379986768L;
    private int maxLength;
    private SSLenType ssLenType;
    private int precision;
    private int displaySize;
    private int scale;
    private short flags;
    private SSType ssType;
    private int userType;
    private String udtTypeName;
    private SQLCollation collation;
    private Charset charset;
    static int UPDATABLE_READ_ONLY;
    static int UPDATABLE_READ_WRITE;
    static int UPDATABLE_UNKNOWN;
    private static final Map<TDSType, Builder> builderMap;
    
    SSType getSSType() {
        return this.ssType;
    }
    
    void setSSType(final SSType ssType) {
        this.ssType = ssType;
    }
    
    SSLenType getSSLenType() {
        return this.ssLenType;
    }
    
    void setSSLenType(final SSLenType ssLenType) {
        this.ssLenType = ssLenType;
    }
    
    String getSSTypeName() {
        return (SSType.UDT == this.ssType) ? this.udtTypeName : this.ssType.toString();
    }
    
    int getMaxLength() {
        return this.maxLength;
    }
    
    void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    int getPrecision() {
        return this.precision;
    }
    
    void setPrecision(final int precision) {
        this.precision = precision;
    }
    
    int getDisplaySize() {
        return this.displaySize;
    }
    
    void setDisplaySize(final int displaySize) {
        this.displaySize = displaySize;
    }
    
    int getScale() {
        return this.scale;
    }
    
    SQLCollation getSQLCollation() {
        return this.collation;
    }
    
    void setSQLCollation(final SQLCollation collation) {
        this.collation = collation;
    }
    
    Charset getCharset() {
        return this.charset;
    }
    
    void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    boolean isNullable() {
        return 0x1 == (this.flags & 0x1);
    }
    
    boolean isCaseSensitive() {
        return 0x2 == (this.flags & 0x2);
    }
    
    boolean isSparseColumnSet() {
        return 0x400 == (this.flags & 0x400);
    }
    
    boolean isEncrypted() {
        return 0x800 == (this.flags & 0x800);
    }
    
    int getUpdatability() {
        return this.flags >> 2 & 0x3;
    }
    
    boolean isIdentity() {
        return 0x10 == (this.flags & 0x10);
    }
    
    byte[] getFlags() {
        final byte[] f = { (byte)(this.flags & 0xFF), (byte)(this.flags >> 8 & 0xFF) };
        return f;
    }
    
    short getFlagsAsShort() {
        return this.flags;
    }
    
    void setFlags(final Short flags) {
        this.flags = flags;
    }
    
    void setScale(final int scale) {
        this.scale = scale;
    }
    
    boolean supportsFastAsciiConversion() {
        switch (this.ssType) {
            case CHAR:
            case VARCHAR:
            case VARCHARMAX:
            case TEXT: {
                return this.collation.hasAsciiCompatibleSBCS();
            }
            default: {
                return false;
            }
        }
    }
    
    private TypeInfo() {
    }
    
    static TypeInfo getInstance(final TDSReader tdsReader, final boolean readFlags) throws SQLServerException {
        final TypeInfo typeInfo = new TypeInfo();
        typeInfo.userType = tdsReader.readInt();
        if (readFlags) {
            typeInfo.flags = tdsReader.readShort();
        }
        TDSType tdsType = null;
        try {
            tdsType = TDSType.valueOf(tdsReader.readUnsignedByte());
        }
        catch (final IllegalArgumentException e) {
            tdsReader.getConnection().terminate(4, e.getMessage(), e);
        }
        assert null != TypeInfo.builderMap.get(tdsType) : "Missing TypeInfo builder for TDSType " + tdsType;
        return TypeInfo.builderMap.get(tdsType).build(typeInfo, tdsReader);
    }
    
    static {
        TypeInfo.UPDATABLE_READ_ONLY = 0;
        TypeInfo.UPDATABLE_READ_WRITE = 1;
        TypeInfo.UPDATABLE_UNKNOWN = 2;
        builderMap = new EnumMap<TDSType, Builder>(TDSType.class);
        for (final Builder builder : Builder.values()) {
            TypeInfo.builderMap.put(builder.getTDSType(), builder);
        }
    }
    
    enum Builder
    {
        BIT(TDSType.BIT1, (Strategy)new FixedLenStrategy(SSType.BIT, 1, 1, "1".length(), 0)), 
        BIGINT(TDSType.INT8, (Strategy)new FixedLenStrategy(SSType.BIGINT, 8, Long.toString(Long.MAX_VALUE).length(), ("-" + Long.toString(Long.MAX_VALUE)).length(), 0)), 
        INTEGER(TDSType.INT4, (Strategy)new FixedLenStrategy(SSType.INTEGER, 4, Integer.toString(Integer.MAX_VALUE).length(), ("-" + Integer.toString(Integer.MAX_VALUE)).length(), 0)), 
        SMALLINT(TDSType.INT2, (Strategy)new FixedLenStrategy(SSType.SMALLINT, 2, Short.toString((short)32767).length(), ("-" + Short.toString((short)32767)).length(), 0)), 
        TINYINT(TDSType.INT1, (Strategy)new FixedLenStrategy(SSType.TINYINT, 1, Byte.toString((byte)127).length(), Byte.toString((byte)127).length(), 0)), 
        REAL(TDSType.FLOAT4, (Strategy)new FixedLenStrategy(SSType.REAL, 4, 7, 13, 0)), 
        FLOAT(TDSType.FLOAT8, (Strategy)new FixedLenStrategy(SSType.FLOAT, 8, 15, 22, 0)), 
        SMALLDATETIME(TDSType.DATETIME4, (Strategy)new FixedLenStrategy(SSType.SMALLDATETIME, 4, "yyyy-mm-dd hh:mm".length(), "yyyy-mm-dd hh:mm".length(), 0)), 
        DATETIME(TDSType.DATETIME8, (Strategy)new FixedLenStrategy(SSType.DATETIME, 8, "yyyy-mm-dd hh:mm:ss.fff".length(), "yyyy-mm-dd hh:mm:ss.fff".length(), 3)), 
        SMALLMONEY(TDSType.MONEY4, (Strategy)new FixedLenStrategy(SSType.SMALLMONEY, 4, Integer.toString(Integer.MAX_VALUE).length(), ("-." + Integer.toString(Integer.MAX_VALUE)).length(), 4)), 
        MONEY(TDSType.MONEY8, (Strategy)new FixedLenStrategy(SSType.MONEY, 8, Long.toString(Long.MAX_VALUE).length(), ("-." + Long.toString(Long.MAX_VALUE)).length(), 4)), 
        BITN(TDSType.BITN, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                if (1 != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                Builder.BIT.build(typeInfo, tdsReader);
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        }), 
        INTN(TDSType.INTN, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                switch (tdsReader.readUnsignedByte()) {
                    case 8: {
                        Builder.BIGINT.build(typeInfo, tdsReader);
                        break;
                    }
                    case 4: {
                        Builder.INTEGER.build(typeInfo, tdsReader);
                        break;
                    }
                    case 2: {
                        Builder.SMALLINT.build(typeInfo, tdsReader);
                        break;
                    }
                    case 1: {
                        Builder.TINYINT.build(typeInfo, tdsReader);
                        break;
                    }
                    default: {
                        tdsReader.throwInvalidTDS();
                        break;
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        }), 
        DECIMAL(TDSType.DECIMALN, (Strategy)new DecimalNumericStrategy(SSType.DECIMAL)), 
        NUMERIC(TDSType.NUMERICN, (Strategy)new DecimalNumericStrategy(SSType.NUMERIC)), 
        FLOATN(TDSType.FLOATN, (Strategy)new BigOrSmallByteLenStrategy(Builder.FLOAT, Builder.REAL)), 
        MONEYN(TDSType.MONEYN, (Strategy)new BigOrSmallByteLenStrategy(Builder.MONEY, Builder.SMALLMONEY)), 
        DATETIMEN(TDSType.DATETIMEN, (Strategy)new BigOrSmallByteLenStrategy(Builder.DATETIME, Builder.SMALLDATETIME)), 
        TIME(TDSType.TIMEN, (Strategy)new KatmaiScaledTemporalStrategy(SSType.TIME)), 
        DATETIME2(TDSType.DATETIME2N, (Strategy)new KatmaiScaledTemporalStrategy(SSType.DATETIME2)), 
        DATETIMEOFFSET(TDSType.DATETIMEOFFSETN, (Strategy)new KatmaiScaledTemporalStrategy(SSType.DATETIMEOFFSET)), 
        DATE(TDSType.DATEN, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssType = SSType.DATE;
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.maxLength = 3;
                typeInfo.displaySize = (typeInfo.precision = "yyyy-mm-dd".length());
            }
        }), 
        BIGBINARY(TDSType.BIGBINARY, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.precision = typeInfo.maxLength;
                typeInfo.displaySize = 2 * typeInfo.maxLength;
                typeInfo.ssType = ((80 == typeInfo.userType) ? SSType.TIMESTAMP : SSType.BINARY);
            }
        }), 
        BIGVARBINARY(TDSType.BIGVARBINARY, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.VARBINARYMAX;
                    typeInfo.displaySize = (typeInfo.precision = Integer.MAX_VALUE);
                }
                else if (typeInfo.maxLength <= 8000) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.VARBINARY;
                    typeInfo.precision = typeInfo.maxLength;
                    typeInfo.displaySize = 2 * typeInfo.maxLength;
                }
                else {
                    tdsReader.throwInvalidTDS();
                }
            }
        }), 
        IMAGE(TDSType.IMAGE, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.IMAGE;
                typeInfo.displaySize = (typeInfo.precision = Integer.MAX_VALUE);
            }
        }), 
        BIGCHAR(TDSType.BIGCHAR, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.displaySize = (typeInfo.precision = typeInfo.maxLength);
                typeInfo.ssType = SSType.CHAR;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        }), 
        BIGVARCHAR(TDSType.BIGVARCHAR, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.VARCHARMAX;
                    typeInfo.displaySize = (typeInfo.precision = Integer.MAX_VALUE);
                }
                else if (typeInfo.maxLength <= 8000) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.VARCHAR;
                    typeInfo.displaySize = (typeInfo.precision = typeInfo.maxLength);
                }
                else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        }), 
        TEXT(TDSType.TEXT, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.TEXT;
                typeInfo.displaySize = (typeInfo.precision = Integer.MAX_VALUE);
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = typeInfo.collation.getCharset();
            }
        }), 
        NCHAR(TDSType.NCHAR, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (typeInfo.maxLength > 8000 || 0 != typeInfo.maxLength % 2) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.displaySize = (typeInfo.precision = typeInfo.maxLength / 2);
                typeInfo.ssType = SSType.NCHAR;
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        }), 
        NVARCHAR(TDSType.NVARCHAR, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.maxLength = tdsReader.readUnsignedShort();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                    typeInfo.ssType = SSType.NVARCHARMAX;
                    typeInfo.displaySize = (typeInfo.precision = 1073741823);
                }
                else if (typeInfo.maxLength <= 8000 && 0 == typeInfo.maxLength % 2) {
                    typeInfo.ssLenType = SSLenType.USHORTLENTYPE;
                    typeInfo.ssType = SSType.NVARCHAR;
                    typeInfo.displaySize = (typeInfo.precision = typeInfo.maxLength / 2);
                }
                else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        }), 
        NTEXT(TDSType.NTEXT, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                if (typeInfo.maxLength < 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssType = SSType.NTEXT;
                typeInfo.displaySize = (typeInfo.precision = 1073741823);
                typeInfo.collation = tdsReader.readCollation();
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        }), 
        GUID(TDSType.GUID, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                final int maxLength = tdsReader.readUnsignedByte();
                if (maxLength != 16 && maxLength != 0) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = SSType.GUID;
                typeInfo.maxLength = maxLength;
                typeInfo.displaySize = (typeInfo.precision = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN".length());
            }
        }), 
        UDT(TDSType.UDT, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                final UDTTDSHeader udtTDSHeader = new UDTTDSHeader(tdsReader);
                typeInfo.maxLength = udtTDSHeader.getMaxLen();
                if (65535 == typeInfo.maxLength) {
                    typeInfo.precision = Integer.MAX_VALUE;
                    typeInfo.displaySize = Integer.MAX_VALUE;
                }
                else if (typeInfo.maxLength <= 8000) {
                    typeInfo.precision = typeInfo.maxLength;
                    typeInfo.displaySize = 2 * typeInfo.maxLength;
                }
                else {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                typeInfo.ssType = SSType.UDT;
                typeInfo.udtTypeName = udtTDSHeader.getTypeName();
            }
        }), 
        XML(TDSType.XML, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                new XMLTDSHeader(tdsReader);
                typeInfo.ssLenType = SSLenType.PARTLENTYPE;
                typeInfo.ssType = SSType.XML;
                typeInfo.displaySize = (typeInfo.precision = 1073741823);
                typeInfo.charset = Encoding.UNICODE.charset();
            }
        }), 
        SQL_VARIANT(TDSType.SQL_VARIANT, (Strategy)new Strategy() {
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.ssLenType = SSLenType.LONGLENTYPE;
                typeInfo.maxLength = tdsReader.readInt();
                typeInfo.ssType = SSType.SQL_VARIANT;
            }
        });
        
        private final TDSType tdsType;
        private final Strategy strategy;
        
        private Builder(final TDSType tdsType, final Strategy strategy) {
            this.tdsType = tdsType;
            this.strategy = strategy;
        }
        
        final TDSType getTDSType() {
            return this.tdsType;
        }
        
        final TypeInfo build(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
            this.strategy.apply(typeInfo, tdsReader);
            assert null != typeInfo.ssType;
            assert null != typeInfo.ssLenType;
            return typeInfo;
        }
        
        private static final class FixedLenStrategy implements Strategy
        {
            private final SSType ssType;
            private final int maxLength;
            private final int precision;
            private final int displaySize;
            private final int scale;
            
            FixedLenStrategy(final SSType ssType, final int maxLength, final int precision, final int displaySize, final int scale) {
                this.ssType = ssType;
                this.maxLength = maxLength;
                this.precision = precision;
                this.displaySize = displaySize;
                this.scale = scale;
            }
            
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) {
                typeInfo.ssLenType = SSLenType.FIXEDLENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.maxLength = this.maxLength;
                typeInfo.precision = this.precision;
                typeInfo.displaySize = this.displaySize;
                typeInfo.scale = this.scale;
            }
        }
        
        private static final class DecimalNumericStrategy implements Strategy
        {
            private final SSType ssType;
            
            DecimalNumericStrategy(final SSType ssType) {
                this.ssType = ssType;
            }
            
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                final int maxLength = tdsReader.readUnsignedByte();
                final int precision = tdsReader.readUnsignedByte();
                final int scale = tdsReader.readUnsignedByte();
                if (maxLength > 17) {
                    tdsReader.throwInvalidTDS();
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.maxLength = maxLength;
                typeInfo.precision = precision;
                typeInfo.displaySize = precision + 2;
                typeInfo.scale = scale;
            }
        }
        
        private static final class BigOrSmallByteLenStrategy implements Strategy
        {
            private final Builder bigBuilder;
            private final Builder smallBuilder;
            
            BigOrSmallByteLenStrategy(final Builder bigBuilder, final Builder smallBuilder) {
                this.bigBuilder = bigBuilder;
                this.smallBuilder = smallBuilder;
            }
            
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                switch (tdsReader.readUnsignedByte()) {
                    case 8: {
                        this.bigBuilder.build(typeInfo, tdsReader);
                        break;
                    }
                    case 4: {
                        this.smallBuilder.build(typeInfo, tdsReader);
                        break;
                    }
                    default: {
                        tdsReader.throwInvalidTDS();
                        break;
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
            }
        }
        
        private static final class KatmaiScaledTemporalStrategy implements Strategy
        {
            private final SSType ssType;
            
            KatmaiScaledTemporalStrategy(final SSType ssType) {
                this.ssType = ssType;
            }
            
            private int getPrecision(final String baseFormat, final int scale) {
                return baseFormat.length() + ((scale > 0) ? (1 + scale) : 0);
            }
            
            @Override
            public void apply(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
                typeInfo.scale = tdsReader.readUnsignedByte();
                if (typeInfo.scale > 7) {
                    tdsReader.throwInvalidTDS();
                }
                switch (this.ssType) {
                    case TIME: {
                        typeInfo.precision = this.getPrecision("hh:mm:ss", typeInfo.scale);
                        typeInfo.maxLength = TDS.timeValueLength(typeInfo.scale);
                        break;
                    }
                    case DATETIME2: {
                        typeInfo.precision = this.getPrecision("yyyy-mm-dd hh:mm:ss", typeInfo.scale);
                        typeInfo.maxLength = TDS.datetime2ValueLength(typeInfo.scale);
                        break;
                    }
                    case DATETIMEOFFSET: {
                        typeInfo.precision = this.getPrecision("yyyy-mm-dd hh:mm:ss +HH:MM", typeInfo.scale);
                        typeInfo.maxLength = TDS.datetimeoffsetValueLength(typeInfo.scale);
                        break;
                    }
                    default: {
                        assert false : "Unexpected SSType: " + this.ssType;
                        break;
                    }
                }
                typeInfo.ssLenType = SSLenType.BYTELENTYPE;
                typeInfo.ssType = this.ssType;
                typeInfo.displaySize = typeInfo.precision;
            }
        }
        
        private interface Strategy
        {
            void apply(final TypeInfo p0, final TDSReader p1) throws SQLServerException;
        }
    }
}
