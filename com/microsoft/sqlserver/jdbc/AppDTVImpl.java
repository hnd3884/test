package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.sql.Blob;
import java.math.BigInteger;
import java.math.RoundingMode;
import microsoft.sql.DateTimeOffset;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.sql.Clob;
import java.math.BigDecimal;
import java.util.Calendar;

final class AppDTVImpl extends DTVImpl
{
    private JDBCType jdbcType;
    private Object value;
    private JavaType javaType;
    private StreamSetterArgs streamSetterArgs;
    private Calendar cal;
    private Integer scale;
    private boolean forceEncrypt;
    private SqlVariant internalVariant;
    
    AppDTVImpl() {
        this.jdbcType = JDBCType.UNKNOWN;
    }
    
    @Override
    final void skipValue(final TypeInfo typeInfo, final TDSReader tdsReader, final boolean isDiscard) throws SQLServerException {
        assert false;
    }
    
    @Override
    final void initFromCompressedNull() {
        assert false;
    }
    
    @Override
    void setValue(final DTV dtv, final SQLCollation collation, final JDBCType jdbcType, final Object value, final JavaType javaType, final StreamSetterArgs streamSetterArgs, final Calendar cal, final Integer scale, final SQLServerConnection con, final boolean forceEncrypt) throws SQLServerException {
        dtv.setValue(value, javaType);
        dtv.setJdbcType(jdbcType);
        dtv.setStreamSetterArgs(streamSetterArgs);
        dtv.setCalendar(cal);
        dtv.setScale(scale);
        dtv.setForceEncrypt(forceEncrypt);
        dtv.executeOp(new SetValueOp(collation, con));
    }
    
    @Override
    void setValue(final Object value, final JavaType javaType) {
        this.value = value;
        this.javaType = javaType;
    }
    
    @Override
    void setStreamSetterArgs(final StreamSetterArgs streamSetterArgs) {
        this.streamSetterArgs = streamSetterArgs;
    }
    
    @Override
    void setCalendar(final Calendar cal) {
        this.cal = cal;
    }
    
    @Override
    void setScale(final Integer scale) {
        this.scale = scale;
    }
    
    @Override
    void setForceEncrypt(final boolean forceEncrypt) {
        this.forceEncrypt = forceEncrypt;
    }
    
    @Override
    StreamSetterArgs getStreamSetterArgs() {
        return this.streamSetterArgs;
    }
    
    @Override
    Calendar getCalendar() {
        return this.cal;
    }
    
    @Override
    Integer getScale() {
        return this.scale;
    }
    
    @Override
    boolean isNull() {
        return null == this.value;
    }
    
    @Override
    void setJdbcType(final JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }
    
    @Override
    JDBCType getJdbcType() {
        return this.jdbcType;
    }
    
    @Override
    JavaType getJavaType() {
        return this.javaType;
    }
    
    @Override
    Object getValue(final DTV dtv, final JDBCType jdbcType, final int scale, final InputStreamGetterArgs streamGetterArgs, final Calendar cal, final TypeInfo typeInfo, final CryptoMetadata cryptoMetadata, final TDSReader tdsReader) throws SQLServerException {
        if (this.jdbcType != jdbcType) {
            DataTypes.throwConversionError(this.jdbcType.toString(), jdbcType.toString());
        }
        return this.value;
    }
    
    @Override
    Object getSetterValue() {
        return this.value;
    }
    
    @Override
    SqlVariant getInternalVariant() {
        return this.internalVariant;
    }
    
    void setInternalVariant(final SqlVariant type) {
        this.internalVariant = type;
    }
    
    final class SetValueOp extends DTVExecuteOp
    {
        private final SQLCollation collation;
        private final SQLServerConnection con;
        
        SetValueOp(final SQLCollation collation, final SQLServerConnection con) {
            this.collation = collation;
            this.con = con;
        }
        
        @Override
        void execute(final DTV dtv, final String strValue) throws SQLServerException {
            final JDBCType jdbcType = dtv.getJdbcType();
            if (JDBCType.DECIMAL == jdbcType || JDBCType.NUMERIC == jdbcType || JDBCType.MONEY == jdbcType || JDBCType.SMALLMONEY == jdbcType) {
                assert null != strValue;
                try {
                    dtv.setValue(new BigDecimal(strValue), JavaType.BIGDECIMAL);
                }
                catch (final NumberFormatException e) {
                    DataTypes.throwConversionError("String", jdbcType.toString());
                }
            }
            else if (jdbcType.isBinary()) {
                assert null != strValue;
                dtv.setValue(ParameterUtils.HexToBin(strValue), JavaType.BYTEARRAY);
            }
            else if (null != this.collation && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.LONGVARCHAR == jdbcType || JDBCType.CLOB == jdbcType)) {
                byte[] nativeEncoding = null;
                if (null != strValue) {
                    nativeEncoding = strValue.getBytes(this.collation.getCharset());
                }
                dtv.setValue(nativeEncoding, JavaType.BYTEARRAY);
            }
        }
        
        @Override
        void execute(final DTV dtv, final Clob clobValue) throws SQLServerException {
            assert null != clobValue;
            try {
                DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), clobValue.length(), false);
            }
            catch (final SQLException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
        }
        
        @Override
        void execute(final DTV dtv, final SQLServerSQLXML xmlValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Byte byteValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Integer intValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Time timeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert timeValue != null : "value is null";
                dtv.setValue(timeValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final Date dateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert dateValue != null : "value is null";
                dtv.setValue(dateValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final Timestamp timestampValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert timestampValue != null : "value is null";
                dtv.setValue(timestampValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final java.util.Date utilDateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert utilDateValue != null : "value is null";
                dtv.setValue(utilDateValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final LocalDate localDateValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert localDateValue != null : "value is null";
                dtv.setValue(localDateValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final LocalTime localTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert localTimeValue != null : "value is null";
                dtv.setValue(localTimeValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final LocalDateTime localDateTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert localDateTimeValue != null : "value is null";
                dtv.setValue(localDateTimeValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final OffsetTime offsetTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert offsetTimeValue != null : "value is null";
                dtv.setValue(offsetTimeValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final OffsetDateTime offsetDateTimeValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert offsetDateTimeValue != null : "value is null";
                dtv.setValue(offsetDateTimeValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final Calendar calendarValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert calendarValue != null : "value is null";
                dtv.setValue(calendarValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final DateTimeOffset dtoValue) throws SQLServerException {
            if (dtv.getJdbcType().isTextual()) {
                assert dtoValue != null : "value is null";
                dtv.setValue(dtoValue.toString(), JavaType.STRING);
            }
        }
        
        @Override
        void execute(final DTV dtv, final TVP tvpValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Float floatValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Double doubleValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, BigDecimal bigDecimalValue) throws SQLServerException {
            if (null != bigDecimalValue) {
                final Integer biScale = bigDecimalValue.scale();
                Integer dtvScale;
                if (null == dtv.getScale() && JDBCType.DECIMAL == dtv.getJdbcType()) {
                    dtvScale = ((bigDecimalValue.precision() > 38) ? (38 - (bigDecimalValue.precision() - biScale)) : biScale);
                    if (dtvScale > 38) {
                        dtv.setScale(38);
                        dtvScale = 38;
                    }
                    else {
                        dtv.setScale(dtvScale);
                    }
                }
                else {
                    dtvScale = dtv.getScale();
                }
                if (null != dtvScale && 0 != Integer.compare(dtvScale, biScale)) {
                    bigDecimalValue = bigDecimalValue.setScale(dtvScale, RoundingMode.DOWN);
                }
            }
            dtv.setValue(bigDecimalValue, JavaType.BIGDECIMAL);
        }
        
        @Override
        void execute(final DTV dtv, final Long longValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final BigInteger bigIntegerValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Short shortValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Boolean booleanValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final byte[] byteArrayValue) throws SQLServerException {
        }
        
        @Override
        void execute(final DTV dtv, final Blob blobValue) throws SQLServerException {
            assert null != blobValue;
            try {
                DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), blobValue.length(), false);
            }
            catch (final SQLException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
        }
        
        @Override
        void execute(final DTV dtv, final InputStream inputStreamValue) throws SQLServerException {
            DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), dtv.getStreamSetterArgs().getLength(), true);
            if (JDBCType.NCHAR == AppDTVImpl.this.jdbcType || JDBCType.NVARCHAR == AppDTVImpl.this.jdbcType || JDBCType.LONGNVARCHAR == AppDTVImpl.this.jdbcType) {
                Reader readerValue = null;
                readerValue = new InputStreamReader(inputStreamValue, StandardCharsets.US_ASCII);
                dtv.setValue(readerValue, JavaType.READER);
                this.execute(dtv, readerValue);
            }
        }
        
        @Override
        void execute(final DTV dtv, final Reader readerValue) throws SQLServerException {
            assert null != readerValue;
            final JDBCType jdbcType = dtv.getJdbcType();
            final long readerLength = DataTypes.getCheckedLength(this.con, dtv.getJdbcType(), dtv.getStreamSetterArgs().getLength(), true);
            if (jdbcType.isBinary()) {
                final String stringValue = DDC.convertReaderToString(readerValue, (int)readerLength);
                if (-1L != readerLength && stringValue.length() != readerLength) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                    final Object[] msgArgs = { readerLength, stringValue.length() };
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                dtv.setValue(stringValue, JavaType.STRING);
                this.execute(dtv, stringValue);
            }
            else if (null != this.collation && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.LONGVARCHAR == jdbcType || JDBCType.CLOB == jdbcType)) {
                final ReaderInputStream streamValue = new ReaderInputStream(readerValue, this.collation.getCharset(), readerLength);
                dtv.setValue(streamValue, JavaType.INPUTSTREAM);
                dtv.setStreamSetterArgs(new StreamSetterArgs(StreamType.CHARACTER, -1L));
                this.execute(dtv, streamValue);
            }
        }
        
        @Override
        void execute(final DTV dtv, final SqlVariant SqlVariantValue) throws SQLServerException {
        }
    }
}
