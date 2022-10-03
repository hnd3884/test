package com.microsoft.sqlserver.jdbc;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.math.BigInteger;
import java.math.BigDecimal;
import microsoft.sql.DateTimeOffset;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

enum JavaType
{
    INTEGER((Class<?>)Integer.class, JDBCType.INTEGER), 
    STRING((Class<?>)String.class, JDBCType.CHAR), 
    DATE((Class<?>)Date.class, JDBCType.DATE), 
    TIME((Class<?>)Time.class, JDBCType.TIME), 
    TIMESTAMP((Class<?>)Timestamp.class, JDBCType.TIMESTAMP), 
    UTILDATE((Class<?>)java.util.Date.class, JDBCType.TIMESTAMP), 
    CALENDAR((Class<?>)Calendar.class, JDBCType.TIMESTAMP), 
    LOCALDATE(getJavaClass("LocalDate"), JDBCType.DATE), 
    LOCALTIME(getJavaClass("LocalTime"), JDBCType.TIME), 
    LOCALDATETIME(getJavaClass("LocalDateTime"), JDBCType.TIMESTAMP), 
    OFFSETTIME(getJavaClass("OffsetTime"), JDBCType.TIME_WITH_TIMEZONE), 
    OFFSETDATETIME(getJavaClass("OffsetDateTime"), JDBCType.TIMESTAMP_WITH_TIMEZONE), 
    DATETIMEOFFSET((Class<?>)DateTimeOffset.class, JDBCType.DATETIMEOFFSET), 
    BOOLEAN((Class<?>)Boolean.class, JDBCType.BIT), 
    BIGDECIMAL((Class<?>)BigDecimal.class, JDBCType.DECIMAL), 
    DOUBLE((Class<?>)Double.class, JDBCType.DOUBLE), 
    FLOAT((Class<?>)Float.class, JDBCType.REAL), 
    SHORT((Class<?>)Short.class, JDBCType.SMALLINT), 
    LONG((Class<?>)Long.class, JDBCType.BIGINT), 
    BIGINTEGER((Class<?>)BigInteger.class, JDBCType.BIGINT), 
    BYTE((Class<?>)Byte.class, JDBCType.TINYINT), 
    BYTEARRAY((Class<?>)byte[].class, JDBCType.BINARY), 
    NCLOB((Class<?>)NClob.class, JDBCType.NCLOB), 
    CLOB((Class<?>)Clob.class, JDBCType.CLOB), 
    BLOB((Class<?>)Blob.class, JDBCType.BLOB), 
    TVP((Class<?>)TVP.class, JDBCType.TVP), 
    GEOMETRY((Class<?>)Geometry.class, JDBCType.GEOMETRY), 
    GEOGRAPHY((Class<?>)Geography.class, JDBCType.GEOGRAPHY), 
    INPUTSTREAM((Class)InputStream.class, JDBCType.UNKNOWN) {
        @Override
        JDBCType getJDBCType(final SSType ssType, final JDBCType jdbcTypeFromApp) {
            JDBCType jdbcType = null;
            if (SSType.UNKNOWN != ssType) {
                switch (ssType) {
                    case CHAR:
                    case VARCHAR:
                    case VARCHARMAX:
                    case TEXT:
                    case NCHAR:
                    case NVARCHAR:
                    case NVARCHARMAX:
                    case NTEXT: {
                        jdbcType = JDBCType.LONGVARCHAR;
                        break;
                    }
                    default: {
                        jdbcType = JDBCType.LONGVARBINARY;
                        break;
                    }
                }
            }
            else {
                jdbcType = (jdbcTypeFromApp.isTextual() ? JDBCType.LONGVARCHAR : JDBCType.LONGVARBINARY);
            }
            assert null != jdbcType;
            return jdbcType;
        }
    }, 
    READER((Class<?>)Reader.class, JDBCType.LONGVARCHAR), 
    SQLXML((Class<?>)SQLServerSQLXML.class, JDBCType.SQLXML), 
    OBJECT((Class<?>)Object.class, JDBCType.UNKNOWN);
    
    private final Class<?> javaClass;
    private final JDBCType jdbcTypeFromJavaType;
    private static double jvmVersion;
    private static final JavaType[] VALUES;
    
    private JavaType(final Class<?> javaClass, final JDBCType jdbcTypeFromJavaType) {
        this.javaClass = javaClass;
        this.jdbcTypeFromJavaType = jdbcTypeFromJavaType;
    }
    
    static Class<?> getJavaClass(final String className) {
        if (0.0 == JavaType.jvmVersion) {
            try {
                final String jvmSpecVersion = System.getProperty("java.specification.version");
                if (jvmSpecVersion != null) {
                    JavaType.jvmVersion = Double.parseDouble(jvmSpecVersion);
                }
            }
            catch (final NumberFormatException e) {
                JavaType.jvmVersion = 0.1;
            }
        }
        if (JavaType.jvmVersion < 1.8) {
            return null;
        }
        switch (className) {
            case "LocalDate": {
                return LocalDate.class;
            }
            case "LocalTime": {
                return LocalTime.class;
            }
            case "LocalDateTime": {
                return LocalDateTime.class;
            }
            case "OffsetTime": {
                return OffsetTime.class;
            }
            case "OffsetDateTime": {
                return OffsetDateTime.class;
            }
            default: {
                return null;
            }
        }
    }
    
    static JavaType of(final Object obj) {
        if (obj instanceof SQLServerDataTable || obj instanceof ResultSet || obj instanceof ISQLServerDataRecord) {
            return JavaType.TVP;
        }
        if (null != obj) {
            for (final JavaType javaType : JavaType.VALUES) {
                if (null != javaType.javaClass && javaType.javaClass.isInstance(obj)) {
                    return javaType;
                }
            }
        }
        return JavaType.OBJECT;
    }
    
    JDBCType getJDBCType(final SSType ssType, final JDBCType jdbcTypeFromApp) {
        return this.jdbcTypeFromJavaType;
    }
    
    static {
        JavaType.jvmVersion = 0.0;
        VALUES = values();
    }
    
    enum SetterConversionAE
    {
        BIT(JavaType.BOOLEAN, EnumSet.of(JDBCType.BIT, JDBCType.TINYINT, JDBCType.SMALLINT, JDBCType.INTEGER, JDBCType.BIGINT)), 
        SHORT(JavaType.SHORT, EnumSet.of(JDBCType.TINYINT, JDBCType.SMALLINT, JDBCType.INTEGER, JDBCType.BIGINT)), 
        INTEGER(JavaType.INTEGER, EnumSet.of(JDBCType.INTEGER, JDBCType.BIGINT)), 
        LONG(JavaType.LONG, EnumSet.of(JDBCType.BIGINT)), 
        BIGDECIMAL(JavaType.BIGDECIMAL, EnumSet.of(JDBCType.MONEY, JDBCType.SMALLMONEY, JDBCType.DECIMAL, JDBCType.NUMERIC)), 
        BYTE(JavaType.BYTE, EnumSet.of(JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY, JDBCType.TINYINT)), 
        BYTEARRAY(JavaType.BYTEARRAY, EnumSet.of(JDBCType.BINARY, JDBCType.VARBINARY, JDBCType.LONGVARBINARY)), 
        DATE(JavaType.DATE, EnumSet.of(JDBCType.DATE)), 
        DATETIMEOFFSET(JavaType.DATETIMEOFFSET, EnumSet.of(JDBCType.DATETIMEOFFSET)), 
        DOUBLE(JavaType.DOUBLE, EnumSet.of(JDBCType.DOUBLE)), 
        FLOAT(JavaType.FLOAT, EnumSet.of(JDBCType.REAL, JDBCType.DOUBLE)), 
        STRING(JavaType.STRING, EnumSet.of(JDBCType.CHAR, JDBCType.VARCHAR, JDBCType.LONGVARCHAR, JDBCType.NCHAR, JDBCType.NVARCHAR, JDBCType.LONGNVARCHAR, JDBCType.GUID)), 
        TIME(JavaType.TIME, EnumSet.of(JDBCType.TIME)), 
        TIMESTAMP(JavaType.TIMESTAMP, EnumSet.of(JDBCType.TIME, JDBCType.TIMESTAMP, JDBCType.DATETIME, JDBCType.SMALLDATETIME));
        
        private final EnumSet<JDBCType> to;
        private final JavaType from;
        private static final SetterConversionAE[] VALUES;
        private static final EnumMap<JavaType, EnumSet<JDBCType>> setterConversionAEMap;
        
        private SetterConversionAE(final JavaType from, final EnumSet<JDBCType> to) {
            this.from = from;
            this.to = to;
        }
        
        static boolean converts(final JavaType fromJavaType, final JDBCType toJDBCType, final boolean sendStringParametersAsUnicode) {
            return null == fromJavaType || JavaType.OBJECT == fromJavaType || (!sendStringParametersAsUnicode && fromJavaType == JavaType.BYTEARRAY && (toJDBCType == JDBCType.VARCHAR || toJDBCType == JDBCType.CHAR || toJDBCType == JDBCType.LONGVARCHAR)) || (SetterConversionAE.setterConversionAEMap.containsKey(fromJavaType) && SetterConversionAE.setterConversionAEMap.get(fromJavaType).contains(toJDBCType));
        }
        
        static {
            VALUES = values();
            setterConversionAEMap = new EnumMap<JavaType, EnumSet<JDBCType>>(JavaType.class);
            for (final JavaType javaType : JavaType.VALUES) {
                SetterConversionAE.setterConversionAEMap.put(javaType, EnumSet.noneOf(JDBCType.class));
            }
            for (final SetterConversionAE conversion : SetterConversionAE.VALUES) {
                SetterConversionAE.setterConversionAEMap.get(conversion.from).addAll(conversion.to);
            }
        }
    }
}
