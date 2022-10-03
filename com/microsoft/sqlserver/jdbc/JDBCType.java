package com.microsoft.sqlserver.jdbc;

import java.util.Collection;
import java.util.EnumMap;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.sql.Timestamp;
import java.time.OffsetTime;
import java.sql.Time;
import java.sql.NClob;
import java.math.BigDecimal;
import microsoft.sql.DateTimeOffset;
import java.sql.Date;
import java.sql.Clob;
import java.sql.Blob;
import java.text.MessageFormat;
import java.util.EnumSet;

enum JDBCType
{
    UNKNOWN(Category.UNKNOWN, 999, Object.class.getName()), 
    ARRAY(Category.UNKNOWN, 2003, Object.class.getName()), 
    BIGINT(Category.NUMERIC, -5, Long.class.getName()), 
    BINARY(Category.BINARY, -2, "[B"), 
    BIT(Category.NUMERIC, -7, Boolean.class.getName()), 
    BLOB(Category.BLOB, 2004, Blob.class.getName()), 
    BOOLEAN(Category.NUMERIC, 16, Boolean.class.getName()), 
    CHAR(Category.CHARACTER, 1, String.class.getName()), 
    CLOB(Category.CLOB, 2005, Clob.class.getName()), 
    DATALINK(Category.UNKNOWN, 70, Object.class.getName()), 
    DATE(Category.DATE, 91, Date.class.getName()), 
    DATETIMEOFFSET(Category.DATETIMEOFFSET, -155, DateTimeOffset.class.getName()), 
    DECIMAL(Category.NUMERIC, 3, BigDecimal.class.getName()), 
    DISTINCT(Category.UNKNOWN, 2001, Object.class.getName()), 
    DOUBLE(Category.NUMERIC, 8, Double.class.getName()), 
    FLOAT(Category.NUMERIC, 6, Double.class.getName()), 
    INTEGER(Category.NUMERIC, 4, Integer.class.getName()), 
    JAVA_OBJECT(Category.UNKNOWN, 2000, Object.class.getName()), 
    LONGNVARCHAR(Category.LONG_NCHARACTER, -16, String.class.getName()), 
    LONGVARBINARY(Category.LONG_BINARY, -4, "[B"), 
    LONGVARCHAR(Category.LONG_CHARACTER, -1, String.class.getName()), 
    NCHAR(Category.NCHARACTER, -15, String.class.getName()), 
    NCLOB(Category.NCLOB, 2011, NClob.class.getName()), 
    NULL(Category.UNKNOWN, 0, Object.class.getName()), 
    NUMERIC(Category.NUMERIC, 2, BigDecimal.class.getName()), 
    NVARCHAR(Category.NCHARACTER, -9, String.class.getName()), 
    OTHER(Category.UNKNOWN, 1111, Object.class.getName()), 
    REAL(Category.NUMERIC, 7, Float.class.getName()), 
    REF(Category.UNKNOWN, 2006, Object.class.getName()), 
    ROWID(Category.UNKNOWN, -8, Object.class.getName()), 
    SMALLINT(Category.NUMERIC, 5, Short.class.getName()), 
    SQLXML(Category.SQLXML, 2009, Object.class.getName()), 
    STRUCT(Category.UNKNOWN, 2002, Object.class.getName()), 
    TIME(Category.TIME, 92, Time.class.getName()), 
    TIME_WITH_TIMEZONE(Category.TIME_WITH_TIMEZONE, 2013, OffsetTime.class.getName()), 
    TIMESTAMP(Category.TIMESTAMP, 93, Timestamp.class.getName()), 
    TIMESTAMP_WITH_TIMEZONE(Category.TIMESTAMP_WITH_TIMEZONE, 2014, OffsetDateTime.class.getName()), 
    TINYINT(Category.NUMERIC, -6, Short.class.getName()), 
    VARBINARY(Category.BINARY, -3, "[B"), 
    VARCHAR(Category.CHARACTER, 12, String.class.getName()), 
    MONEY(Category.NUMERIC, -148, BigDecimal.class.getName()), 
    SMALLMONEY(Category.NUMERIC, -146, BigDecimal.class.getName()), 
    TVP(Category.TVP, -153, Object.class.getName()), 
    DATETIME(Category.TIMESTAMP, -151, Timestamp.class.getName()), 
    SMALLDATETIME(Category.TIMESTAMP, -150, Timestamp.class.getName()), 
    GUID(Category.CHARACTER, -145, String.class.getName()), 
    SQL_VARIANT(Category.SQL_VARIANT, -156, Object.class.getName()), 
    GEOMETRY(Category.GEOMETRY, -157, Object.class.getName()), 
    GEOGRAPHY(Category.GEOGRAPHY, -158, Object.class.getName()), 
    LOCALDATETIME(Category.TIMESTAMP, 93, LocalDateTime.class.getName());
    
    final Category category;
    private final int intValue;
    private final String className;
    private static final JDBCType[] VALUES;
    private static final EnumSet<JDBCType> signedTypes;
    private static final EnumSet<JDBCType> binaryTypes;
    private static final EnumSet<Category> textualCategories;
    
    final String className() {
        return this.className;
    }
    
    private JDBCType(final Category category, final int intValue, final String className) {
        this.category = category;
        this.intValue = intValue;
        this.className = className;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    boolean convertsTo(final JDBCType jdbcType) {
        return SetterConversion.converts(this, jdbcType);
    }
    
    boolean convertsTo(final SSType ssType) {
        return UpdaterConversion.converts(this, ssType);
    }
    
    static JDBCType of(final int intValue) throws SQLServerException {
        for (final JDBCType jdbcType : JDBCType.VALUES) {
            if (jdbcType.intValue == intValue) {
                return jdbcType;
            }
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownJDBCType"));
        final Object[] msgArgs = { intValue };
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return JDBCType.UNKNOWN;
    }
    
    boolean isSigned() {
        return JDBCType.signedTypes.contains(this);
    }
    
    boolean isBinary() {
        return JDBCType.binaryTypes.contains(this);
    }
    
    boolean isTextual() {
        return JDBCType.textualCategories.contains(this.category);
    }
    
    boolean isUnsupported() {
        return Category.UNKNOWN == this.category;
    }
    
    int asJavaSqlType() {
        if (!"1.5".equals(Util.SYSTEM_SPEC_VERSION)) {
            return this.intValue;
        }
        switch (this) {
            case NCHAR: {
                return 1;
            }
            case NVARCHAR:
            case SQLXML: {
                return 12;
            }
            case LONGNVARCHAR: {
                return -1;
            }
            case NCLOB: {
                return 2005;
            }
            case ROWID: {
                return 1111;
            }
            default: {
                return this.intValue;
            }
        }
    }
    
    boolean normalizationCheck(final SSType ssType) {
        return NormalizationAE.converts(this, ssType);
    }
    
    static {
        VALUES = values();
        signedTypes = EnumSet.of(JDBCType.SMALLINT, JDBCType.INTEGER, JDBCType.BIGINT, JDBCType.REAL, JDBCType.FLOAT, JDBCType.DOUBLE, JDBCType.DECIMAL, JDBCType.NUMERIC, JDBCType.MONEY, JDBCType.SMALLMONEY);
        binaryTypes = EnumSet.of(JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY, JDBCType.BLOB);
        textualCategories = EnumSet.of(Category.CHARACTER, Category.LONG_CHARACTER, Category.CLOB, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.NCLOB);
    }
    
    enum Category
    {
        CHARACTER, 
        LONG_CHARACTER, 
        CLOB, 
        NCHARACTER, 
        LONG_NCHARACTER, 
        NCLOB, 
        BINARY, 
        LONG_BINARY, 
        BLOB, 
        NUMERIC, 
        DATE, 
        TIME, 
        TIMESTAMP, 
        TIME_WITH_TIMEZONE, 
        TIMESTAMP_WITH_TIMEZONE, 
        DATETIMEOFFSET, 
        SQLXML, 
        UNKNOWN, 
        TVP, 
        GUID, 
        SQL_VARIANT, 
        GEOMETRY, 
        GEOGRAPHY;
        
        private static final Category[] VALUES;
        
        static {
            VALUES = values();
        }
    }
    
    enum SetterConversion
    {
        CHARACTER(Category.CHARACTER, EnumSet.of(Category.NUMERIC, Category.DATE, Category.TIME, Category.TIMESTAMP, Category.DATETIMEOFFSET, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.BINARY, Category.LONG_BINARY, Category.GUID, Category.SQL_VARIANT)), 
        LONG_CHARACTER(Category.LONG_CHARACTER, EnumSet.of(Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.BINARY, Category.LONG_BINARY)), 
        CLOB(Category.CLOB, EnumSet.of(Category.CLOB, Category.LONG_CHARACTER, Category.LONG_NCHARACTER)), 
        NCHARACTER(Category.NCHARACTER, EnumSet.of(Category.NCHARACTER, Category.LONG_NCHARACTER, Category.NCLOB, Category.SQL_VARIANT)), 
        LONG_NCHARACTER(Category.LONG_NCHARACTER, EnumSet.of(Category.NCHARACTER, Category.LONG_NCHARACTER)), 
        NCLOB(Category.NCLOB, EnumSet.of(Category.LONG_NCHARACTER, Category.NCLOB)), 
        BINARY(Category.BINARY, EnumSet.of(Category.NUMERIC, Category.DATE, Category.TIME, Category.TIMESTAMP, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.BINARY, Category.LONG_BINARY, Category.BLOB, Category.GUID, Category.SQL_VARIANT)), 
        LONG_BINARY(Category.LONG_BINARY, EnumSet.of(Category.BINARY, Category.LONG_BINARY)), 
        BLOB(Category.BLOB, EnumSet.of(Category.LONG_BINARY, Category.BLOB)), 
        NUMERIC(Category.NUMERIC, EnumSet.of(Category.NUMERIC, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.SQL_VARIANT)), 
        DATE(Category.DATE, EnumSet.of(Category.DATE, Category.TIMESTAMP, Category.DATETIMEOFFSET, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.SQL_VARIANT)), 
        TIME(Category.TIME, EnumSet.of(Category.TIME, Category.TIMESTAMP, Category.DATETIMEOFFSET, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.SQL_VARIANT)), 
        TIMESTAMP(Category.TIMESTAMP, EnumSet.of(Category.DATE, Category.TIME, Category.TIMESTAMP, Category.DATETIMEOFFSET, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER, Category.SQL_VARIANT)), 
        TIME_WITH_TIMEZONE(Category.TIME_WITH_TIMEZONE, EnumSet.of(Category.TIME_WITH_TIMEZONE, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER)), 
        TIMESTAMP_WITH_TIMEZONE(Category.TIMESTAMP_WITH_TIMEZONE, EnumSet.of(Category.TIMESTAMP_WITH_TIMEZONE, Category.TIME_WITH_TIMEZONE, Category.CHARACTER, Category.LONG_CHARACTER, Category.NCHARACTER, Category.LONG_NCHARACTER)), 
        DATETIMEOFFSET(Category.DATETIMEOFFSET, EnumSet.of(Category.DATE, Category.TIME, Category.TIMESTAMP, Category.DATETIMEOFFSET)), 
        SQLXML(Category.SQLXML, EnumSet.of(Category.SQLXML)), 
        TVP(Category.TVP, EnumSet.of(Category.TVP)), 
        GEOMETRY(Category.GEOMETRY, EnumSet.of(Category.GEOMETRY)), 
        GEOGRAPHY(Category.GEOGRAPHY, EnumSet.of(Category.GEOGRAPHY));
        
        private final Category from;
        private final EnumSet<Category> to;
        private static final SetterConversion[] VALUES;
        private static final EnumMap<Category, EnumSet<Category>> conversionMap;
        
        private SetterConversion(final Category from, final EnumSet<Category> to) {
            this.from = from;
            this.to = to;
        }
        
        static boolean converts(final JDBCType fromJDBCType, final JDBCType toJDBCType) {
            return SetterConversion.conversionMap.get(fromJDBCType.category).contains(toJDBCType.category);
        }
        
        static {
            VALUES = values();
            conversionMap = new EnumMap<Category, EnumSet<Category>>(Category.class);
            for (final Category category : Category.VALUES) {
                SetterConversion.conversionMap.put(category, EnumSet.noneOf(Category.class));
            }
            for (final SetterConversion conversion : SetterConversion.VALUES) {
                SetterConversion.conversionMap.get(conversion.from).addAll(conversion.to);
            }
        }
    }
    
    enum UpdaterConversion
    {
        CHARACTER(Category.CHARACTER, EnumSet.of(SSType.Category.NUMERIC, SSType.Category.DATE, SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML, SSType.Category.BINARY, SSType.Category.LONG_BINARY, SSType.Category.UDT, SSType.Category.GUID, SSType.Category.TIMESTAMP, SSType.Category.SQL_VARIANT)), 
        LONG_CHARACTER(Category.LONG_CHARACTER, EnumSet.of(SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML, SSType.Category.BINARY, SSType.Category.LONG_BINARY)), 
        CLOB(Category.CLOB, EnumSet.of(SSType.Category.LONG_CHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML)), 
        NCHARACTER(Category.NCHARACTER, EnumSet.of(SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML, SSType.Category.SQL_VARIANT)), 
        LONG_NCHARACTER(Category.LONG_NCHARACTER, EnumSet.of(SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML)), 
        NCLOB(Category.NCLOB, EnumSet.of(SSType.Category.LONG_NCHARACTER, SSType.Category.XML)), 
        BINARY(Category.BINARY, EnumSet.of(SSType.Category.NUMERIC, SSType.Category.DATETIME, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.XML, SSType.Category.BINARY, SSType.Category.LONG_BINARY, SSType.Category.UDT, SSType.Category.TIMESTAMP, SSType.Category.GUID, SSType.Category.SQL_VARIANT)), 
        LONG_BINARY(Category.LONG_BINARY, EnumSet.of(SSType.Category.XML, SSType.Category.BINARY, SSType.Category.LONG_BINARY, SSType.Category.UDT)), 
        BLOB(Category.BLOB, EnumSet.of(SSType.Category.LONG_BINARY, SSType.Category.XML)), 
        SQLXML(Category.SQLXML, EnumSet.of(SSType.Category.XML)), 
        NUMERIC(Category.NUMERIC, EnumSet.of(SSType.Category.NUMERIC, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.SQL_VARIANT)), 
        DATE(Category.DATE, EnumSet.of(SSType.Category.DATE, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.SQL_VARIANT)), 
        TIME(Category.TIME, EnumSet.of(SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.SQL_VARIANT)), 
        TIMESTAMP(Category.TIMESTAMP, EnumSet.of(SSType.Category.DATE, SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER, SSType.Category.SQL_VARIANT)), 
        DATETIMEOFFSET(Category.DATETIMEOFFSET, EnumSet.of(SSType.Category.DATE, SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER)), 
        TIME_WITH_TIMEZONE(Category.TIME_WITH_TIMEZONE, EnumSet.of(SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER)), 
        TIMESTAMP_WITH_TIMEZONE(Category.TIMESTAMP_WITH_TIMEZONE, EnumSet.of(SSType.Category.DATE, SSType.Category.TIME, SSType.Category.DATETIME, SSType.Category.DATETIME2, SSType.Category.DATETIMEOFFSET, SSType.Category.CHARACTER, SSType.Category.LONG_CHARACTER, SSType.Category.NCHARACTER, SSType.Category.LONG_NCHARACTER)), 
        SQL_VARIANT(Category.SQL_VARIANT, EnumSet.of(SSType.Category.SQL_VARIANT));
        
        private final Category from;
        private final EnumSet<SSType.Category> to;
        private static final UpdaterConversion[] VALUES;
        private static final EnumMap<Category, EnumSet<SSType.Category>> conversionMap;
        
        private UpdaterConversion(final Category from, final EnumSet<SSType.Category> to) {
            this.from = from;
            this.to = to;
        }
        
        static boolean converts(final JDBCType fromJDBCType, final SSType toSSType) {
            return UpdaterConversion.conversionMap.get(fromJDBCType.category).contains(toSSType.category);
        }
        
        static {
            VALUES = values();
            conversionMap = new EnumMap<Category, EnumSet<SSType.Category>>(Category.class);
            for (final Category category : Category.VALUES) {
                UpdaterConversion.conversionMap.put(category, EnumSet.noneOf(SSType.Category.class));
            }
            for (final UpdaterConversion conversion : UpdaterConversion.VALUES) {
                UpdaterConversion.conversionMap.get(conversion.from).addAll(conversion.to);
            }
        }
    }
    
    enum NormalizationAE
    {
        CHARACTER_NORMALIZED_TO(JDBCType.CHAR, EnumSet.of(SSType.CHAR, SSType.VARCHAR, SSType.VARCHARMAX)), 
        VARCHARACTER_NORMALIZED_TO(JDBCType.VARCHAR, EnumSet.of(SSType.CHAR, SSType.VARCHAR, SSType.VARCHARMAX)), 
        LONGVARCHARACTER_NORMALIZED_TO(JDBCType.LONGVARCHAR, EnumSet.of(SSType.CHAR, SSType.VARCHAR, SSType.VARCHARMAX)), 
        NCHAR_NORMALIZED_TO(JDBCType.NCHAR, EnumSet.of(SSType.NCHAR, SSType.NVARCHAR, SSType.NVARCHARMAX)), 
        NVARCHAR_NORMALIZED_TO(JDBCType.NVARCHAR, EnumSet.of(SSType.NCHAR, SSType.NVARCHAR, SSType.NVARCHARMAX)), 
        LONGNVARCHAR_NORMALIZED_TO(JDBCType.LONGNVARCHAR, EnumSet.of(SSType.NCHAR, SSType.NVARCHAR, SSType.NVARCHARMAX)), 
        BIT_NORMALIZED_TO(JDBCType.BIT, EnumSet.of(SSType.BIT, SSType.TINYINT, SSType.SMALLINT, SSType.INTEGER, SSType.BIGINT)), 
        TINYINT_NORMALIZED_TO(JDBCType.TINYINT, EnumSet.of(SSType.TINYINT, SSType.SMALLINT, SSType.INTEGER, SSType.BIGINT)), 
        SMALLINT_NORMALIZED_TO(JDBCType.SMALLINT, EnumSet.of(SSType.SMALLINT, SSType.INTEGER, SSType.BIGINT)), 
        INTEGER_NORMALIZED_TO(JDBCType.INTEGER, EnumSet.of(SSType.INTEGER, SSType.BIGINT)), 
        BIGINT_NORMALIZED_TO(JDBCType.BIGINT, EnumSet.of(SSType.BIGINT)), 
        BINARY_NORMALIZED_TO(JDBCType.BINARY, EnumSet.of(SSType.BINARY, SSType.VARBINARY, SSType.VARBINARYMAX)), 
        VARBINARY_NORMALIZED_TO(JDBCType.VARBINARY, EnumSet.of(SSType.BINARY, SSType.VARBINARY, SSType.VARBINARYMAX)), 
        LONGVARBINARY_NORMALIZED_TO(JDBCType.LONGVARBINARY, EnumSet.of(SSType.BINARY, SSType.VARBINARY, SSType.VARBINARYMAX)), 
        FLOAT_NORMALIZED_TO(JDBCType.DOUBLE, EnumSet.of(SSType.FLOAT)), 
        REAL_NORMALIZED_TO(JDBCType.REAL, EnumSet.of(SSType.REAL)), 
        DECIMAL_NORMALIZED_TO(JDBCType.DECIMAL, EnumSet.of(SSType.DECIMAL, SSType.NUMERIC)), 
        SMALLMONEY_NORMALIZED_TO(JDBCType.SMALLMONEY, EnumSet.of(SSType.SMALLMONEY, SSType.MONEY)), 
        MONEY_NORMALIZED_TO(JDBCType.MONEY, EnumSet.of(SSType.MONEY)), 
        NUMERIC_NORMALIZED_TO(JDBCType.NUMERIC, EnumSet.of(SSType.DECIMAL, SSType.NUMERIC)), 
        DATE_NORMALIZED_TO(JDBCType.DATE, EnumSet.of(SSType.DATE)), 
        TIME_NORMALIZED_TO(JDBCType.TIME, EnumSet.of(SSType.TIME)), 
        DATETIME2_NORMALIZED_TO(JDBCType.TIMESTAMP, EnumSet.of(SSType.DATETIME2)), 
        DATETIMEOFFSET_NORMALIZED_TO(JDBCType.DATETIMEOFFSET, EnumSet.of(SSType.DATETIMEOFFSET)), 
        DATETIME_NORMALIZED_TO(JDBCType.DATETIME, EnumSet.of(SSType.DATETIME)), 
        SMALLDATETIME_NORMALIZED_TO(JDBCType.SMALLDATETIME, EnumSet.of(SSType.SMALLDATETIME)), 
        GUID_NORMALIZED_TO(JDBCType.GUID, EnumSet.of(SSType.GUID));
        
        private final JDBCType from;
        private final EnumSet<SSType> to;
        private static final NormalizationAE[] VALUES;
        private static final EnumMap<JDBCType, EnumSet<SSType>> normalizationMapAE;
        
        private NormalizationAE(final JDBCType from, final EnumSet<SSType> to) {
            this.from = from;
            this.to = to;
        }
        
        static boolean converts(final JDBCType fromJDBCType, final SSType toSSType) {
            return NormalizationAE.normalizationMapAE.get(fromJDBCType).contains(toSSType);
        }
        
        static {
            VALUES = values();
            normalizationMapAE = new EnumMap<JDBCType, EnumSet<SSType>>(JDBCType.class);
            for (final JDBCType jdbcType : JDBCType.VALUES) {
                NormalizationAE.normalizationMapAE.put(jdbcType, EnumSet.noneOf(SSType.class));
            }
            for (final NormalizationAE conversion : NormalizationAE.VALUES) {
                NormalizationAE.normalizationMapAE.get(conversion.from).addAll(conversion.to);
            }
        }
    }
}
