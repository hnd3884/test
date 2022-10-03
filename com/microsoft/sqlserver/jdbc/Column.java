package com.microsoft.sqlserver.jdbc;

import java.util.UUID;
import java.text.MessageFormat;
import java.util.Calendar;

final class Column
{
    private TypeInfo typeInfo;
    private CryptoMetadata cryptoMetadata;
    private SqlVariant internalVariant;
    private DTV updaterDTV;
    private final DTV getterDTV;
    private JDBCType jdbcTypeSetByUser;
    private int valueLength;
    private String columnName;
    private String baseColumnName;
    private int tableNum;
    private int infoStatus;
    private SQLIdentifier tableName;
    ColumnFilter filter;
    
    final void setInternalVariant(final SqlVariant type) {
        this.internalVariant = type;
    }
    
    final SqlVariant getInternalVariant() {
        return this.internalVariant;
    }
    
    final TypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    final void setColumnName(final String name) {
        this.columnName = name;
    }
    
    final String getColumnName() {
        return this.columnName;
    }
    
    final void setBaseColumnName(final String name) {
        this.baseColumnName = name;
    }
    
    final String getBaseColumnName() {
        return this.baseColumnName;
    }
    
    final void setTableNum(final int num) {
        this.tableNum = num;
    }
    
    final int getTableNum() {
        return this.tableNum;
    }
    
    final void setInfoStatus(final int status) {
        this.infoStatus = status;
    }
    
    final boolean hasDifferentName() {
        return 0x0 != (this.infoStatus & 0x20);
    }
    
    final boolean isHidden() {
        return 0x0 != (this.infoStatus & 0x10);
    }
    
    final boolean isKey() {
        return 0x0 != (this.infoStatus & 0x8);
    }
    
    final boolean isExpression() {
        return 0x0 != (this.infoStatus & 0x4);
    }
    
    final boolean isUpdatable() {
        return !this.isExpression() && !this.isHidden() && this.tableName.getObjectName().length() > 0;
    }
    
    final void setTableName(final SQLIdentifier name) {
        this.tableName = name;
    }
    
    final SQLIdentifier getTableName() {
        return this.tableName;
    }
    
    Column(final TypeInfo typeInfo, final String columnName, final SQLIdentifier tableName, final CryptoMetadata cryptoMeta) {
        this.getterDTV = new DTV();
        this.jdbcTypeSetByUser = null;
        this.valueLength = 0;
        this.typeInfo = typeInfo;
        this.columnName = columnName;
        this.baseColumnName = columnName;
        this.tableName = tableName;
        this.cryptoMetadata = cryptoMeta;
    }
    
    CryptoMetadata getCryptoMetadata() {
        return this.cryptoMetadata;
    }
    
    final void clear() {
        this.getterDTV.clear();
    }
    
    final void skipValue(final TDSReader tdsReader, final boolean isDiscard) throws SQLServerException {
        this.getterDTV.skipValue(this.typeInfo, tdsReader, isDiscard);
    }
    
    final void initFromCompressedNull() {
        this.getterDTV.initFromCompressedNull();
    }
    
    void setFilter(final ColumnFilter filter) {
        this.filter = filter;
    }
    
    final boolean isNull() {
        return this.getterDTV.isNull();
    }
    
    final boolean isInitialized() {
        return this.getterDTV.isInitialized();
    }
    
    Object getValue(final JDBCType jdbcType, final InputStreamGetterArgs getterArgs, final Calendar cal, final TDSReader tdsReader) throws SQLServerException {
        final Object value = this.getterDTV.getValue(jdbcType, this.typeInfo.getScale(), getterArgs, cal, this.typeInfo, this.cryptoMetadata, tdsReader);
        this.setInternalVariant(this.getterDTV.getInternalVariant());
        return (null != this.filter) ? this.filter.apply(value, jdbcType) : value;
    }
    
    int getInt(final TDSReader tdsReader) throws SQLServerException {
        return (int)this.getValue(JDBCType.INTEGER, null, null, tdsReader);
    }
    
    void updateValue(JDBCType jdbcType, Object value, JavaType javaType, final StreamSetterArgs streamSetterArgs, final Calendar cal, Integer scale, final SQLServerConnection con, final SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting, final Integer precision, final boolean forceEncrypt, final int parameterIndex) throws SQLServerException {
        final SSType ssType = this.typeInfo.getSSType();
        if (null != this.cryptoMetadata) {
            if (SSType.VARBINARYMAX == this.cryptoMetadata.baseTypeInfo.getSSType() && JDBCType.BINARY == jdbcType) {
                jdbcType = this.cryptoMetadata.baseTypeInfo.getSSType().getJDBCType();
            }
            if (null != value) {
                if (JDBCType.TINYINT == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() && javaType == JavaType.SHORT) {
                    if (value instanceof Boolean) {
                        if (value) {
                            value = 1;
                        }
                        else {
                            value = 0;
                        }
                    }
                    final String stringValue = "" + value;
                    final Short shortValue = Short.valueOf(stringValue);
                    if (shortValue >= 0 && shortValue <= 255) {
                        value = shortValue.byteValue();
                        javaType = JavaType.BYTE;
                        jdbcType = JDBCType.TINYINT;
                    }
                }
            }
            else if (jdbcType.isBinary()) {
                jdbcType = this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType();
            }
        }
        if (null == scale && null != this.cryptoMetadata) {
            scale = this.cryptoMetadata.getBaseTypeInfo().getScale();
        }
        if (null != this.cryptoMetadata && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType) && (JDBCType.NVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.NCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.LONGNVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType())) {
            jdbcType = this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType();
        }
        if (Util.shouldHonorAEForParameters(stmtColumnEncriptionSetting, con)) {
            if (null == this.cryptoMetadata && forceEncrypt) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumnRS"));
                final Object[] msgArgs = { parameterIndex };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            this.setJdbcTypeSetByUser(jdbcType);
            this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            if (null != this.cryptoMetadata && (JDBCType.NCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.NVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType() || JDBCType.LONGNVARCHAR == this.cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType())) {
                this.valueLength *= 2;
            }
        }
        else if (forceEncrypt) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAEFalseRS"));
            final Object[] msgArgs = { parameterIndex };
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        if (null != streamSetterArgs) {
            if (!streamSetterArgs.streamType.convertsTo(this.typeInfo)) {
                DataTypes.throwConversionError(streamSetterArgs.streamType.toString(), ssType.toString());
            }
        }
        else if (null != this.cryptoMetadata) {
            if (JDBCType.UNKNOWN == jdbcType && value instanceof UUID) {
                javaType = JavaType.STRING;
                jdbcType = JDBCType.GUID;
                this.setJdbcTypeSetByUser(jdbcType);
            }
            final SSType basicSSType = this.cryptoMetadata.baseTypeInfo.getSSType();
            if (!jdbcType.convertsTo(basicSSType)) {
                DataTypes.throwConversionError(jdbcType.toString(), ssType.toString());
            }
            final JDBCType jdbcTypeFromSSType = getJDBCTypeFromBaseSSType(basicSSType, jdbcType);
            if (jdbcTypeFromSSType != jdbcType) {
                this.setJdbcTypeSetByUser(jdbcTypeFromSSType);
                jdbcType = jdbcTypeFromSSType;
                this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            }
        }
        else if (!jdbcType.convertsTo(ssType)) {
            DataTypes.throwConversionError(jdbcType.toString(), ssType.toString());
        }
        if ((JDBCType.DATETIMEOFFSET == jdbcType || JavaType.DATETIMEOFFSET == javaType) && !con.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (null != this.cryptoMetadata && con.sendStringParametersAsUnicode() && (JavaType.STRING == javaType || JavaType.READER == javaType || JavaType.CLOB == javaType || JavaType.OBJECT == javaType)) {
            jdbcType = getSSPAUJDBCType(jdbcType);
        }
        if ((SSType.NCHAR == ssType || SSType.NVARCHAR == ssType || SSType.NVARCHARMAX == ssType || SSType.NTEXT == ssType || SSType.XML == ssType) && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.LONGVARCHAR == jdbcType || JDBCType.CLOB == jdbcType)) {
            jdbcType = ((JDBCType.CLOB == jdbcType) ? JDBCType.NCLOB : JDBCType.NVARCHAR);
        }
        else if ((SSType.BINARY == ssType || SSType.VARBINARY == ssType || SSType.VARBINARYMAX == ssType || SSType.IMAGE == ssType || SSType.UDT == ssType) && (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.LONGVARCHAR == jdbcType)) {
            jdbcType = JDBCType.VARBINARY;
        }
        else if ((JDBCType.TIMESTAMP == jdbcType || JDBCType.DATE == jdbcType || JDBCType.TIME == jdbcType || JDBCType.DATETIMEOFFSET == jdbcType) && (SSType.CHAR == ssType || SSType.VARCHAR == ssType || SSType.VARCHARMAX == ssType || SSType.TEXT == ssType || SSType.NCHAR == ssType || SSType.NVARCHAR == ssType || SSType.NVARCHARMAX == ssType || SSType.NTEXT == ssType)) {
            jdbcType = JDBCType.NCHAR;
        }
        if (null == this.updaterDTV) {
            this.updaterDTV = new DTV();
        }
        this.updaterDTV.setValue(this.typeInfo.getSQLCollation(), jdbcType, value, javaType, streamSetterArgs, cal, scale, con, false);
    }
    
    private static JDBCType getSSPAUJDBCType(final JDBCType jdbcType) {
        switch (jdbcType) {
            case CHAR: {
                return JDBCType.NCHAR;
            }
            case VARCHAR: {
                return JDBCType.NVARCHAR;
            }
            case LONGVARCHAR: {
                return JDBCType.LONGNVARCHAR;
            }
            case CLOB: {
                return JDBCType.NCLOB;
            }
            default: {
                return jdbcType;
            }
        }
    }
    
    private static JDBCType getJDBCTypeFromBaseSSType(final SSType basicSSType, final JDBCType jdbcType) {
        switch (jdbcType) {
            case TIMESTAMP: {
                if (SSType.DATETIME == basicSSType) {
                    return JDBCType.DATETIME;
                }
                if (SSType.SMALLDATETIME == basicSSType) {
                    return JDBCType.SMALLDATETIME;
                }
                return jdbcType;
            }
            case NUMERIC:
            case DECIMAL: {
                if (SSType.MONEY == basicSSType) {
                    return JDBCType.MONEY;
                }
                if (SSType.SMALLMONEY == basicSSType) {
                    return JDBCType.SMALLMONEY;
                }
                return jdbcType;
            }
            case CHAR: {
                if (SSType.GUID == basicSSType) {
                    return JDBCType.GUID;
                }
                if (SSType.VARCHARMAX == basicSSType) {
                    return JDBCType.LONGVARCHAR;
                }
                return jdbcType;
            }
            default: {
                return jdbcType;
            }
        }
    }
    
    boolean hasUpdates() {
        return null != this.updaterDTV;
    }
    
    void cancelUpdates() {
        this.updaterDTV = null;
    }
    
    void sendByRPC(final TDSWriter tdsWriter, final SQLServerConnection conn) throws SQLServerException {
        if (null == this.updaterDTV) {
            return;
        }
        try {
            this.updaterDTV.sendCryptoMetaData(this.cryptoMetadata, tdsWriter);
            this.updaterDTV.setJdbcTypeSetByUser(this.getJdbcTypeSetByUser(), this.getValueLength());
            this.updaterDTV.sendByRPC(this.baseColumnName, this.typeInfo, (null != this.cryptoMetadata) ? this.cryptoMetadata.getBaseTypeInfo().getSQLCollation() : this.typeInfo.getSQLCollation(), (null != this.cryptoMetadata) ? this.cryptoMetadata.getBaseTypeInfo().getPrecision() : this.typeInfo.getPrecision(), (null != this.cryptoMetadata) ? this.cryptoMetadata.getBaseTypeInfo().getScale() : this.typeInfo.getScale(), false, tdsWriter, conn);
        }
        finally {
            this.updaterDTV.sendCryptoMetaData(null, tdsWriter);
        }
    }
    
    JDBCType getJdbcTypeSetByUser() {
        return this.jdbcTypeSetByUser;
    }
    
    void setJdbcTypeSetByUser(final JDBCType jdbcTypeSetByUser) {
        this.jdbcTypeSetByUser = jdbcTypeSetByUser;
    }
    
    int getValueLength() {
        return this.valueLength;
    }
}
