package com.microsoft.sqlserver.jdbc;

import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

final class ServerDTVImpl extends DTVImpl
{
    private int valueLength;
    private TDSReaderMark valueMark;
    private boolean isNull;
    private SqlVariant internalVariant;
    private static final int STREAMCONSUMED = -2;
    private static final Logger aeLogger;
    
    @Override
    void setValue(final DTV dtv, final SQLCollation collation, final JDBCType jdbcType, final Object value, final JavaType javaType, final StreamSetterArgs streamSetterArgs, final Calendar cal, final Integer scale, final SQLServerConnection con, final boolean forceEncrypt) throws SQLServerException {
        dtv.setImpl(new AppDTVImpl());
        dtv.setValue(collation, jdbcType, value, javaType, streamSetterArgs, cal, scale, con, forceEncrypt);
    }
    
    @Override
    void setValue(final Object value, final JavaType javaType) {
        assert false;
    }
    
    void setPositionAfterStreamed(final TDSReader tdsReader) {
        this.valueMark = tdsReader.mark();
        this.valueLength = -2;
    }
    
    @Override
    void setStreamSetterArgs(final StreamSetterArgs streamSetterArgs) {
        assert false;
    }
    
    @Override
    void setCalendar(final Calendar calendar) {
        assert false;
    }
    
    @Override
    void setScale(final Integer scale) {
        assert false;
    }
    
    @Override
    void setForceEncrypt(final boolean forceEncrypt) {
        assert false;
    }
    
    @Override
    StreamSetterArgs getStreamSetterArgs() {
        assert false;
        return null;
    }
    
    @Override
    Calendar getCalendar() {
        assert false;
        return null;
    }
    
    @Override
    Integer getScale() {
        assert false;
        return null;
    }
    
    @Override
    boolean isNull() {
        return this.isNull;
    }
    
    @Override
    void setJdbcType(final JDBCType jdbcType) {
        assert false;
    }
    
    @Override
    JDBCType getJdbcType() {
        assert false;
        return JDBCType.UNKNOWN;
    }
    
    @Override
    JavaType getJavaType() {
        assert false;
        return JavaType.OBJECT;
    }
    
    @Override
    final void initFromCompressedNull() {
        assert this.valueMark == null;
        this.isNull = true;
    }
    
    @Override
    final void skipValue(final TypeInfo type, final TDSReader tdsReader, final boolean isDiscard) throws SQLServerException {
        if (null == this.valueMark && this.isNull) {
            return;
        }
        if (null == this.valueMark) {
            this.getValuePrep(type, tdsReader);
        }
        tdsReader.reset(this.valueMark);
        if (this.valueLength != -2) {
            if (this.valueLength == -1) {
                assert SSLenType.PARTLENTYPE == type.getSSLenType();
                final PLPInputStream tempPLP = PLPInputStream.makeTempStream(tdsReader, isDiscard, this);
                try {
                    if (null != tempPLP) {
                        tempPLP.close();
                    }
                }
                catch (final IOException e) {
                    tdsReader.getConnection().terminate(3, e.getMessage());
                }
            }
            else {
                assert this.valueLength >= 0;
                tdsReader.skip(this.valueLength);
            }
        }
    }
    
    private void getValuePrep(final TypeInfo typeInfo, final TDSReader tdsReader) throws SQLServerException {
        assert null == this.valueMark;
        switch (typeInfo.getSSLenType()) {
            case PARTLENTYPE: {
                this.valueLength = -1;
                this.isNull = PLPInputStream.isNull(tdsReader);
                break;
            }
            case FIXEDLENTYPE: {
                this.valueLength = typeInfo.getMaxLength();
                this.isNull = (0 == this.valueLength);
                break;
            }
            case BYTELENTYPE: {
                this.valueLength = tdsReader.readUnsignedByte();
                this.isNull = (0 == this.valueLength);
                break;
            }
            case USHORTLENTYPE: {
                this.valueLength = tdsReader.readUnsignedShort();
                this.isNull = (65535 == this.valueLength);
                if (this.isNull) {
                    this.valueLength = 0;
                    break;
                }
                break;
            }
            case LONGLENTYPE: {
                if (SSType.TEXT == typeInfo.getSSType() || SSType.IMAGE == typeInfo.getSSType() || SSType.NTEXT == typeInfo.getSSType()) {
                    this.isNull = (0 == tdsReader.readUnsignedByte());
                    if (this.isNull) {
                        this.valueLength = 0;
                        break;
                    }
                    tdsReader.skip(24);
                    this.valueLength = tdsReader.readInt();
                    break;
                }
                else {
                    if (SSType.SQL_VARIANT == typeInfo.getSSType()) {
                        this.valueLength = tdsReader.readInt();
                        this.isNull = (0 == this.valueLength);
                        typeInfo.setSSType(SSType.SQL_VARIANT);
                        break;
                    }
                    break;
                }
                break;
            }
        }
        if (this.valueLength > typeInfo.getMaxLength()) {
            tdsReader.throwInvalidTDS();
        }
        this.valueMark = tdsReader.mark();
    }
    
    Object denormalizedValue(final byte[] decryptedValue, final JDBCType jdbcType, final TypeInfo baseTypeInfo, final SQLServerConnection con, final InputStreamGetterArgs streamGetterArgs, final byte normalizeRuleVersion, final Calendar cal) throws SQLServerException {
        if (1 != normalizeRuleVersion) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnsupportedNormalizationVersionAE"));
            throw new SQLServerException(form.format(new Object[] { normalizeRuleVersion, 1 }), null, 0, null);
        }
        if (ServerDTVImpl.aeLogger.isLoggable(Level.FINE)) {
            ServerDTVImpl.aeLogger.fine("Denormalizing decrypted data based on its SQL Server type(" + baseTypeInfo.getSSType() + ") and JDBC type(" + jdbcType + ").");
        }
        final SSType baseSSType = baseTypeInfo.getSSType();
        switch (baseSSType) {
            case CHAR:
            case VARCHAR:
            case NCHAR:
            case NVARCHAR:
            case VARCHARMAX:
            case NVARCHARMAX: {
                try {
                    String strVal = new String(decryptedValue, 0, decryptedValue.length, (null == baseTypeInfo.getCharset()) ? con.getDatabaseCollation().getCharset() : baseTypeInfo.getCharset());
                    if (SSType.CHAR == baseSSType || SSType.NCHAR == baseSSType) {
                        final StringBuilder sb = new StringBuilder(strVal);
                        for (int padLength = baseTypeInfo.getPrecision() - strVal.length(), i = 0; i < padLength; ++i) {
                            sb.append(' ');
                        }
                        strVal = sb.toString();
                    }
                    return DDC.convertStringToObject(strVal, baseTypeInfo.getCharset(), jdbcType, streamGetterArgs.streamType);
                }
                catch (final IllegalArgumentException e) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                    throw new SQLServerException(form2.format(new Object[] { baseSSType, jdbcType }), null, 0, e);
                }
                catch (final UnsupportedEncodingException e2) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_unsupportedEncoding"));
                    throw new SQLServerException(form2.format(new Object[] { baseTypeInfo.getCharset() }), null, 0, e2);
                }
            }
            case BIT:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
            case BIGINT: {
                if (8 != decryptedValue.length) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                    throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
                }
                return DDC.convertLongToObject(Util.readLong(decryptedValue, 0), jdbcType, baseSSType, streamGetterArgs.streamType);
            }
            case REAL:
            case FLOAT: {
                if (8 == decryptedValue.length) {
                    return DDC.convertDoubleToObject(ByteBuffer.wrap(decryptedValue).order(ByteOrder.LITTLE_ENDIAN).getDouble(), (JDBCType.VARBINARY == jdbcType) ? baseSSType.getJDBCType() : jdbcType, streamGetterArgs.streamType);
                }
                if (4 == decryptedValue.length) {
                    return DDC.convertFloatToObject(ByteBuffer.wrap(decryptedValue).order(ByteOrder.LITTLE_ENDIAN).getFloat(), (JDBCType.VARBINARY == jdbcType) ? baseSSType.getJDBCType() : jdbcType, streamGetterArgs.streamType);
                }
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
            }
            case SMALLMONEY: {
                return DDC.convertMoneyToObject(new BigDecimal(BigInteger.valueOf(Util.readInt(decryptedValue, 4)), 4), (JDBCType.VARBINARY == jdbcType) ? baseSSType.getJDBCType() : jdbcType, streamGetterArgs.streamType, 4);
            }
            case MONEY: {
                final BigInteger bi = BigInteger.valueOf((long)Util.readInt(decryptedValue, 0) << 32 | ((long)Util.readInt(decryptedValue, 4) & 0xFFFFFFFFL));
                return DDC.convertMoneyToObject(new BigDecimal(bi, 4), (JDBCType.VARBINARY == jdbcType) ? baseSSType.getJDBCType() : jdbcType, streamGetterArgs.streamType, 8);
            }
            case NUMERIC:
            case DECIMAL: {
                return DDC.convertBigDecimalToObject(Util.readBigDecimal(decryptedValue, decryptedValue.length, baseTypeInfo.getScale()), (JDBCType.VARBINARY == jdbcType) ? baseSSType.getJDBCType() : jdbcType, streamGetterArgs.streamType);
            }
            case BINARY:
            case VARBINARY:
            case VARBINARYMAX: {
                return DDC.convertBytesToObject(decryptedValue, jdbcType, baseTypeInfo);
            }
            case DATE: {
                if (3 != decryptedValue.length) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                    throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
                }
                final int daysIntoCE = this.getDaysIntoCE(decryptedValue, baseSSType);
                return DDC.convertTemporalToObject(jdbcType, baseSSType, cal, daysIntoCE, 0L, 0);
            }
            case TIME: {
                final long localNanosSinceMidnight = this.readNanosSinceMidnightAE(decryptedValue, baseTypeInfo.getScale(), baseSSType);
                return DDC.convertTemporalToObject(jdbcType, SSType.TIME, cal, 0, localNanosSinceMidnight, baseTypeInfo.getScale());
            }
            case DATETIME2: {
                if (8 != decryptedValue.length) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                    throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
                }
                final int dateOffset = decryptedValue.length - 3;
                final byte[] timePortion = new byte[dateOffset];
                final byte[] datePortion = new byte[3];
                System.arraycopy(decryptedValue, 0, timePortion, 0, dateOffset);
                System.arraycopy(decryptedValue, dateOffset, datePortion, 0, 3);
                final long localNanosSinceMidnight2 = this.readNanosSinceMidnightAE(timePortion, baseTypeInfo.getScale(), baseSSType);
                final int daysIntoCE2 = this.getDaysIntoCE(datePortion, baseSSType);
                return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME2, cal, daysIntoCE2, localNanosSinceMidnight2, baseTypeInfo.getScale());
            }
            case SMALLDATETIME: {
                if (4 != decryptedValue.length) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                    throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
                }
                return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME, cal, Util.readUnsignedShort(decryptedValue, 0), Util.readUnsignedShort(decryptedValue, 2) * 60L * 1000L, 0);
            }
            case DATETIME: {
                final int ticksSinceMidnight = (Util.readInt(decryptedValue, 4) * 10 + 1) / 3;
                if (8 != decryptedValue.length || Integer.MAX_VALUE < ticksSinceMidnight) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
                    throw new SQLServerException(form2.format(new Object[] { baseSSType }), null, 0, null);
                }
                return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME, cal, Util.readInt(decryptedValue, 0), ticksSinceMidnight, 0);
            }
            case DATETIMEOFFSET: {
                final int dateOffset = decryptedValue.length - 5;
                final byte[] timePortion = new byte[dateOffset];
                final byte[] datePortion = new byte[3];
                final byte[] offsetPortion = new byte[2];
                System.arraycopy(decryptedValue, 0, timePortion, 0, dateOffset);
                System.arraycopy(decryptedValue, dateOffset, datePortion, 0, 3);
                System.arraycopy(decryptedValue, dateOffset + 3, offsetPortion, 0, 2);
                final long localNanosSinceMidnight3 = this.readNanosSinceMidnightAE(timePortion, baseTypeInfo.getScale(), baseSSType);
                final int daysIntoCE3 = this.getDaysIntoCE(datePortion, baseSSType);
                final int localMinutesOffset = ByteBuffer.wrap(offsetPortion).order(ByteOrder.LITTLE_ENDIAN).getShort();
                return DDC.convertTemporalToObject(jdbcType, SSType.DATETIMEOFFSET, new GregorianCalendar(new SimpleTimeZone(localMinutesOffset * 60 * 1000, ""), Locale.US), daysIntoCE3, localNanosSinceMidnight3, baseTypeInfo.getScale());
            }
            case GUID: {
                return Util.readGUID(decryptedValue);
            }
            default: {
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
                throw new SQLServerException(form3.format(new Object[] { baseSSType }), null, 0, null);
            }
        }
    }
    
    @Override
    Object getValue(final DTV dtv, final JDBCType jdbcType, final int scale, InputStreamGetterArgs streamGetterArgs, final Calendar cal, final TypeInfo typeInfo, final CryptoMetadata cryptoMetadata, final TDSReader tdsReader) throws SQLServerException {
        final SQLServerConnection con = tdsReader.getConnection();
        Object convertedValue = null;
        boolean encrypted = false;
        SSType baseSSType = typeInfo.getSSType();
        if (null != cryptoMetadata) {
            assert SSType.VARBINARYMAX == typeInfo.getSSType();
            baseSSType = cryptoMetadata.baseTypeInfo.getSSType();
            encrypted = true;
            if (ServerDTVImpl.aeLogger.isLoggable(Level.FINE)) {
                ServerDTVImpl.aeLogger.fine("Data is encrypted, SQL Server Data Type: " + baseSSType + ", Encryption Type: " + cryptoMetadata.getEncryptionType());
            }
        }
        if (null == this.valueMark && !this.isNull) {
            this.getValuePrep(typeInfo, tdsReader);
        }
        assert this.valueMark == null && this.isNull;
        if (null != streamGetterArgs) {
            if (!streamGetterArgs.streamType.convertsFrom(typeInfo)) {
                DataTypes.throwConversionError(typeInfo.getSSType().toString(), streamGetterArgs.streamType.toString());
            }
        }
        else {
            if (!baseSSType.convertsTo(jdbcType) && !this.isNull) {
                if (encrypted) {
                    if (!Util.isBinaryType(jdbcType.getIntValue())) {
                        DataTypes.throwConversionError(baseSSType.toString(), jdbcType.toString());
                    }
                }
                else {
                    DataTypes.throwConversionError(baseSSType.toString(), jdbcType.toString());
                }
            }
            streamGetterArgs = InputStreamGetterArgs.getDefaultArgs();
        }
        if (-2 == this.valueLength) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_dataAlreadyAccessed"), null, 0, false);
        }
        Label_1202: {
            if (!this.isNull) {
                tdsReader.reset(this.valueMark);
                if (encrypted) {
                    if (-1 == this.valueLength) {
                        convertedValue = DDC.convertStreamToObject(PLPInputStream.makeStream(tdsReader, streamGetterArgs, this), typeInfo, JDBCType.VARBINARY, streamGetterArgs);
                    }
                    else {
                        convertedValue = DDC.convertStreamToObject(new SimpleInputStream(tdsReader, this.valueLength, streamGetterArgs, this), typeInfo, JDBCType.VARBINARY, streamGetterArgs);
                    }
                    ServerDTVImpl.aeLogger.fine("Encrypted data is retrieved.");
                    if (convertedValue instanceof SimpleInputStream || convertedValue instanceof PLPInputStream) {
                        throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), (Throwable)null);
                    }
                    final byte[] decryptedValue = SQLServerSecurityUtility.decryptWithKey((byte[])convertedValue, cryptoMetadata, con);
                    return this.denormalizedValue(decryptedValue, jdbcType, cryptoMetadata.baseTypeInfo, con, streamGetterArgs, cryptoMetadata.normalizationRuleVersion, cal);
                }
                else {
                    switch (baseSSType) {
                        case VARCHARMAX:
                        case NVARCHARMAX:
                        case VARBINARYMAX:
                        case UDT: {
                            convertedValue = DDC.convertStreamToObject(PLPInputStream.makeStream(tdsReader, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                            break;
                        }
                        case XML: {
                            convertedValue = DDC.convertStreamToObject((jdbcType.isBinary() || jdbcType == JDBCType.SQLXML) ? PLPXMLInputStream.makeXMLStream(tdsReader, streamGetterArgs, this) : PLPInputStream.makeStream(tdsReader, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                            break;
                        }
                        case CHAR:
                        case VARCHAR:
                        case NCHAR:
                        case NVARCHAR:
                        case BINARY:
                        case VARBINARY:
                        case TEXT:
                        case NTEXT:
                        case IMAGE:
                        case TIMESTAMP: {
                            convertedValue = DDC.convertStreamToObject(new SimpleInputStream(tdsReader, this.valueLength, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                            break;
                        }
                        case BIT:
                        case TINYINT:
                        case SMALLINT:
                        case INTEGER:
                        case BIGINT: {
                            switch (this.valueLength) {
                                case 8: {
                                    convertedValue = DDC.convertLongToObject(tdsReader.readLong(), jdbcType, baseSSType, streamGetterArgs.streamType);
                                    break Label_1202;
                                }
                                case 4: {
                                    convertedValue = DDC.convertIntegerToObject(tdsReader.readInt(), this.valueLength, jdbcType, streamGetterArgs.streamType);
                                    break Label_1202;
                                }
                                case 2: {
                                    convertedValue = DDC.convertIntegerToObject(tdsReader.readShort(), this.valueLength, jdbcType, streamGetterArgs.streamType);
                                    break Label_1202;
                                }
                                case 1: {
                                    convertedValue = DDC.convertIntegerToObject(tdsReader.readUnsignedByte(), this.valueLength, jdbcType, streamGetterArgs.streamType);
                                    break Label_1202;
                                }
                                default: {
                                    assert false : "Unexpected valueLength" + this.valueLength;
                                    break Label_1202;
                                }
                            }
                            break;
                        }
                        case NUMERIC:
                        case DECIMAL: {
                            convertedValue = tdsReader.readDecimal(this.valueLength, typeInfo, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case SMALLMONEY:
                        case MONEY: {
                            convertedValue = tdsReader.readMoney(this.valueLength, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case FLOAT: {
                            convertedValue = tdsReader.readFloat(this.valueLength, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case REAL: {
                            convertedValue = tdsReader.readReal(this.valueLength, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case SMALLDATETIME:
                        case DATETIME: {
                            convertedValue = tdsReader.readDateTime(this.valueLength, cal, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case DATE: {
                            convertedValue = tdsReader.readDate(this.valueLength, cal, jdbcType);
                            break;
                        }
                        case TIME: {
                            convertedValue = tdsReader.readTime(this.valueLength, typeInfo, cal, jdbcType);
                            break;
                        }
                        case DATETIME2: {
                            convertedValue = tdsReader.readDateTime2(this.valueLength, typeInfo, cal, jdbcType);
                            break;
                        }
                        case DATETIMEOFFSET: {
                            convertedValue = tdsReader.readDateTimeOffset(this.valueLength, typeInfo, jdbcType);
                            break;
                        }
                        case GUID: {
                            convertedValue = tdsReader.readGUID(this.valueLength, jdbcType, streamGetterArgs.streamType);
                            break;
                        }
                        case SQL_VARIANT: {
                            final int baseType = tdsReader.readUnsignedByte();
                            final int cbPropsActual = tdsReader.readUnsignedByte();
                            if (null == this.internalVariant) {
                                this.internalVariant = new SqlVariant(baseType);
                            }
                            convertedValue = this.readSqlVariant(baseType, cbPropsActual, this.valueLength, tdsReader, baseSSType, typeInfo, jdbcType, streamGetterArgs, cal);
                            break;
                        }
                        default: {
                            assert false : "Unexpected SSType " + typeInfo.getSSType();
                            break;
                        }
                    }
                }
            }
        }
        assert null != convertedValue;
        return convertedValue;
    }
    
    @Override
    SqlVariant getInternalVariant() {
        return this.internalVariant;
    }
    
    private Object readSqlVariant(final int intbaseType, final int cbPropsActual, final int valueLength, final TDSReader tdsReader, final SSType baseSSType, final TypeInfo typeInfo, JDBCType jdbcType, final InputStreamGetterArgs streamGetterArgs, final Calendar cal) throws SQLServerException {
        Object convertedValue = null;
        int lengthConsumed = 2 + cbPropsActual;
        final int expectedValueLength = valueLength - lengthConsumed;
        SQLCollation collation = null;
        final TDSType baseType = TDSType.valueOf(intbaseType);
        Label_1716: {
            switch (baseType) {
                case INT8: {
                    jdbcType = JDBCType.BIGINT;
                    convertedValue = DDC.convertLongToObject(tdsReader.readLong(), jdbcType, baseSSType, streamGetterArgs.streamType);
                    break;
                }
                case INT4: {
                    jdbcType = JDBCType.INTEGER;
                    convertedValue = DDC.convertIntegerToObject(tdsReader.readInt(), valueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case INT2: {
                    jdbcType = JDBCType.SMALLINT;
                    convertedValue = DDC.convertIntegerToObject(tdsReader.readShort(), valueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case INT1: {
                    jdbcType = JDBCType.TINYINT;
                    convertedValue = DDC.convertIntegerToObject(tdsReader.readUnsignedByte(), valueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case DECIMALN:
                case NUMERICN: {
                    if (TDSType.DECIMALN == baseType) {
                        jdbcType = JDBCType.DECIMAL;
                    }
                    else if (TDSType.NUMERICN == baseType) {
                        jdbcType = JDBCType.NUMERIC;
                    }
                    if (cbPropsActual != sqlVariantProbBytes.DECIMALN.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    jdbcType = JDBCType.DECIMAL;
                    final int precision = tdsReader.readUnsignedByte();
                    final int scale = tdsReader.readUnsignedByte();
                    typeInfo.setScale(scale);
                    this.internalVariant.setPrecision(precision);
                    this.internalVariant.setScale(scale);
                    convertedValue = tdsReader.readDecimal(expectedValueLength, typeInfo, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case FLOAT4: {
                    jdbcType = JDBCType.REAL;
                    convertedValue = tdsReader.readReal(expectedValueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case FLOAT8: {
                    jdbcType = JDBCType.FLOAT;
                    convertedValue = tdsReader.readFloat(expectedValueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case MONEY4: {
                    jdbcType = JDBCType.SMALLMONEY;
                    final int precision = Long.toString(Long.MAX_VALUE).length();
                    typeInfo.setPrecision(precision);
                    final int scale = 4;
                    typeInfo.setDisplaySize(("-." + Integer.toString(Integer.MAX_VALUE)).length());
                    typeInfo.setScale(scale);
                    this.internalVariant.setPrecision(precision);
                    this.internalVariant.setScale(scale);
                    convertedValue = tdsReader.readMoney(expectedValueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case MONEY8: {
                    jdbcType = JDBCType.MONEY;
                    final int precision = Long.toString(Long.MAX_VALUE).length();
                    final int scale = 4;
                    typeInfo.setPrecision(precision);
                    typeInfo.setDisplaySize(("-." + Integer.toString(Integer.MAX_VALUE)).length());
                    typeInfo.setScale(scale);
                    this.internalVariant.setPrecision(precision);
                    this.internalVariant.setScale(scale);
                    convertedValue = tdsReader.readMoney(expectedValueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case BIT1:
                case BITN: {
                    jdbcType = JDBCType.BIT;
                    switch (expectedValueLength) {
                        case 8: {
                            convertedValue = DDC.convertLongToObject(tdsReader.readLong(), jdbcType, baseSSType, streamGetterArgs.streamType);
                            break Label_1716;
                        }
                        case 4: {
                            convertedValue = DDC.convertIntegerToObject(tdsReader.readInt(), expectedValueLength, jdbcType, streamGetterArgs.streamType);
                            break Label_1716;
                        }
                        case 2: {
                            convertedValue = DDC.convertIntegerToObject(tdsReader.readShort(), expectedValueLength, jdbcType, streamGetterArgs.streamType);
                            break Label_1716;
                        }
                        case 1: {
                            convertedValue = DDC.convertIntegerToObject(tdsReader.readUnsignedByte(), expectedValueLength, jdbcType, streamGetterArgs.streamType);
                            break Label_1716;
                        }
                        default: {
                            assert false : "Unexpected valueLength" + expectedValueLength;
                            break Label_1716;
                        }
                    }
                    break;
                }
                case BIGVARCHAR:
                case BIGCHAR: {
                    if (cbPropsActual != sqlVariantProbBytes.BIGCHAR.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    if (TDSType.BIGVARCHAR == baseType) {
                        jdbcType = JDBCType.VARCHAR;
                    }
                    else if (TDSType.BIGCHAR == baseType) {
                        jdbcType = JDBCType.CHAR;
                    }
                    collation = tdsReader.readCollation();
                    typeInfo.setSQLCollation(collation);
                    final int maxLength = tdsReader.readUnsignedShort();
                    if (maxLength > 8000) {
                        tdsReader.throwInvalidTDS();
                    }
                    typeInfo.setDisplaySize(maxLength);
                    typeInfo.setPrecision(maxLength);
                    this.internalVariant.setPrecision(maxLength);
                    this.internalVariant.setCollation(collation);
                    typeInfo.setCharset(collation.getCharset());
                    convertedValue = DDC.convertStreamToObject(new SimpleInputStream(tdsReader, expectedValueLength, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                    break;
                }
                case NCHAR:
                case NVARCHAR: {
                    if (cbPropsActual != sqlVariantProbBytes.NCHAR.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    if (TDSType.NCHAR == baseType) {
                        jdbcType = JDBCType.NCHAR;
                    }
                    else if (TDSType.NVARCHAR == baseType) {
                        jdbcType = JDBCType.NVARCHAR;
                    }
                    collation = tdsReader.readCollation();
                    typeInfo.setSQLCollation(collation);
                    final int maxLength = tdsReader.readUnsignedShort();
                    if (maxLength > 8000 || 0 != maxLength % 2) {
                        tdsReader.throwInvalidTDS();
                    }
                    typeInfo.setDisplaySize(maxLength / 2);
                    typeInfo.setPrecision(maxLength / 2);
                    this.internalVariant.setPrecision(maxLength / 2);
                    this.internalVariant.setCollation(collation);
                    typeInfo.setCharset(Encoding.UNICODE.charset());
                    convertedValue = DDC.convertStreamToObject(new SimpleInputStream(tdsReader, expectedValueLength, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                    break;
                }
                case DATETIME8: {
                    jdbcType = JDBCType.DATETIME;
                    convertedValue = tdsReader.readDateTime(expectedValueLength, cal, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case DATETIME4: {
                    jdbcType = JDBCType.SMALLDATETIME;
                    convertedValue = tdsReader.readDateTime(expectedValueLength, cal, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                case DATEN: {
                    jdbcType = JDBCType.DATE;
                    convertedValue = tdsReader.readDate(expectedValueLength, cal, jdbcType);
                    break;
                }
                case TIMEN: {
                    if (cbPropsActual != sqlVariantProbBytes.TIMEN.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    if (this.internalVariant.isBaseTypeTimeValue()) {
                        jdbcType = JDBCType.TIMESTAMP;
                    }
                    final int scale = tdsReader.readUnsignedByte();
                    typeInfo.setScale(scale);
                    this.internalVariant.setScale(scale);
                    convertedValue = tdsReader.readTime(expectedValueLength, typeInfo, cal, jdbcType);
                    break;
                }
                case DATETIME2N: {
                    if (cbPropsActual != sqlVariantProbBytes.DATETIME2N.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    jdbcType = JDBCType.TIMESTAMP;
                    final int scale = tdsReader.readUnsignedByte();
                    typeInfo.setScale(scale);
                    this.internalVariant.setScale(scale);
                    convertedValue = tdsReader.readDateTime2(expectedValueLength, typeInfo, cal, jdbcType);
                    break;
                }
                case BIGBINARY:
                case BIGVARBINARY: {
                    if (cbPropsActual != sqlVariantProbBytes.BIGBINARY.getIntValue()) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProbbytes"));
                        throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                    }
                    if (TDSType.BIGBINARY == baseType) {
                        jdbcType = JDBCType.BINARY;
                    }
                    else if (TDSType.BIGVARBINARY == baseType) {
                        jdbcType = JDBCType.VARBINARY;
                    }
                    final int maxLength = tdsReader.readUnsignedShort();
                    this.internalVariant.setMaxLength(maxLength);
                    if (maxLength > 8000) {
                        tdsReader.throwInvalidTDS();
                    }
                    typeInfo.setDisplaySize(2 * maxLength);
                    typeInfo.setPrecision(maxLength);
                    convertedValue = DDC.convertStreamToObject(new SimpleInputStream(tdsReader, expectedValueLength, streamGetterArgs, this), typeInfo, jdbcType, streamGetterArgs);
                    break;
                }
                case GUID: {
                    jdbcType = JDBCType.GUID;
                    this.internalVariant.setBaseType(intbaseType);
                    this.internalVariant.setBaseJDBCType(jdbcType);
                    typeInfo.setDisplaySize("NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN".length());
                    lengthConsumed = 2 + cbPropsActual;
                    convertedValue = tdsReader.readGUID(expectedValueLength, jdbcType, streamGetterArgs.streamType);
                    break;
                }
                default: {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidDataTypeSupportForSQLVariant"));
                    throw new SQLServerException(form.format(new Object[] { baseType }), null, 0, null);
                }
            }
        }
        return convertedValue;
    }
    
    @Override
    Object getSetterValue() {
        assert false;
        return null;
    }
    
    private long readNanosSinceMidnightAE(final byte[] value, final int scale, final SSType baseSSType) throws SQLServerException {
        long hundredNanosSinceMidnight = 0L;
        for (int i = 0; i < value.length; ++i) {
            hundredNanosSinceMidnight |= ((long)value[i] & 0xFFL) << 8 * i;
        }
        if (0L > hundredNanosSinceMidnight || hundredNanosSinceMidnight >= 864000000000L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
            throw new SQLServerException(form.format(new Object[] { baseSSType }), null, 0, null);
        }
        return 100L * hundredNanosSinceMidnight;
    }
    
    private int getDaysIntoCE(final byte[] datePortion, final SSType baseSSType) throws SQLServerException {
        int daysIntoCE = 0;
        for (int i = 0; i < datePortion.length; ++i) {
            daysIntoCE |= (datePortion[i] & 0xFF) << 8 * i;
        }
        if (daysIntoCE < 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NormalizationErrorAE"));
            throw new SQLServerException(form.format(new Object[] { baseSSType }), null, 0, null);
        }
        return daysIntoCE;
    }
    
    static {
        aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.DTV");
    }
}
