package com.microsoft.sqlserver.jdbc;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.math.BigInteger;
import java.math.BigDecimal;
import microsoft.sql.DateTimeOffset;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.Locale;
import java.text.MessageFormat;
import java.util.Calendar;

final class Parameter
{
    private TypeInfo typeInfo;
    CryptoMetadata cryptoMeta;
    private boolean shouldHonorAEForParameter;
    private boolean userProvidesPrecision;
    private boolean userProvidesScale;
    private String typeDefinition;
    boolean renewDefinition;
    private JDBCType jdbcTypeSetByUser;
    private int valueLength;
    private boolean forceEncryption;
    int scale;
    private int outScale;
    private String name;
    private String schemaName;
    private DTV getterDTV;
    private DTV registeredOutDTV;
    private DTV setterDTV;
    private DTV inputDTV;
    
    TypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    final CryptoMetadata getCryptoMetadata() {
        return this.cryptoMeta;
    }
    
    Parameter(final boolean honorAE) {
        this.cryptoMeta = null;
        this.shouldHonorAEForParameter = false;
        this.userProvidesPrecision = false;
        this.userProvidesScale = false;
        this.typeDefinition = null;
        this.renewDefinition = false;
        this.jdbcTypeSetByUser = null;
        this.valueLength = 0;
        this.forceEncryption = false;
        this.scale = 0;
        this.outScale = 4;
        this.registeredOutDTV = null;
        this.setterDTV = null;
        this.inputDTV = null;
        this.shouldHonorAEForParameter = honorAE;
    }
    
    boolean isOutput() {
        return null != this.registeredOutDTV;
    }
    
    JDBCType getJdbcType() throws SQLServerException {
        return (null != this.inputDTV) ? this.inputDTV.getJdbcType() : JDBCType.UNKNOWN;
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
    
    void registerForOutput(JDBCType jdbcType, final SQLServerConnection con) throws SQLServerException {
        if (JDBCType.DATETIMEOFFSET == jdbcType && !con.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (con.sendStringParametersAsUnicode()) {
            if (this.shouldHonorAEForParameter) {
                this.setJdbcTypeSetByUser(jdbcType);
            }
            jdbcType = getSSPAUJDBCType(jdbcType);
        }
        (this.registeredOutDTV = new DTV()).setJdbcType(jdbcType);
        if (null == this.setterDTV) {
            this.inputDTV = this.registeredOutDTV;
        }
        this.resetOutputValue();
    }
    
    int getOutScale() {
        return this.outScale;
    }
    
    void setOutScale(final int outScale) {
        this.outScale = outScale;
        this.userProvidesScale = true;
    }
    
    final Parameter cloneForBatch() {
        final Parameter clonedParam = new Parameter(this.shouldHonorAEForParameter);
        clonedParam.typeInfo = this.typeInfo;
        clonedParam.typeDefinition = this.typeDefinition;
        clonedParam.outScale = this.outScale;
        clonedParam.name = this.name;
        clonedParam.getterDTV = this.getterDTV;
        clonedParam.registeredOutDTV = this.registeredOutDTV;
        clonedParam.setterDTV = this.setterDTV;
        clonedParam.inputDTV = this.inputDTV;
        clonedParam.cryptoMeta = this.cryptoMeta;
        clonedParam.jdbcTypeSetByUser = this.jdbcTypeSetByUser;
        clonedParam.valueLength = this.valueLength;
        clonedParam.userProvidesPrecision = this.userProvidesPrecision;
        clonedParam.userProvidesScale = this.userProvidesScale;
        return clonedParam;
    }
    
    final void skipValue(final TDSReader tdsReader, final boolean isDiscard) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.deriveTypeInfo(tdsReader);
        this.getterDTV.skipValue(this.typeInfo, tdsReader, isDiscard);
    }
    
    final void skipRetValStatus(final TDSReader tdsReader) throws SQLServerException {
        final StreamRetValue srv = new StreamRetValue();
        srv.setFromTDS(tdsReader);
    }
    
    void clearInputValue() {
        this.setterDTV = null;
        this.inputDTV = this.registeredOutDTV;
    }
    
    void resetOutputValue() {
        this.getterDTV = null;
        this.typeInfo = null;
    }
    
    void deriveTypeInfo(final TDSReader tdsReader) throws SQLServerException {
        if (null == this.typeInfo) {
            this.typeInfo = TypeInfo.getInstance(tdsReader, true);
            if (this.shouldHonorAEForParameter && this.typeInfo.isEncrypted()) {
                final CekTableEntry cekEntry = this.cryptoMeta.getCekTableEntry();
                (this.cryptoMeta = new StreamRetValue().getCryptoMetadata(tdsReader)).setCekTableEntry(cekEntry);
            }
        }
    }
    
    void setFromReturnStatus(final int returnStatus, final SQLServerConnection con) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.getterDTV.setValue(null, JDBCType.INTEGER, returnStatus, JavaType.INTEGER, null, null, null, con, this.getForceEncryption());
    }
    
    void setValue(JDBCType jdbcType, Object value, JavaType javaType, final StreamSetterArgs streamSetterArgs, final Calendar calendar, final Integer precision, final Integer scale, final SQLServerConnection con, final boolean forceEncrypt, final SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting, final int parameterIndex, final String userSQL, final String tvpName) throws SQLServerException {
        if (this.shouldHonorAEForParameter) {
            this.userProvidesPrecision = false;
            this.userProvidesScale = false;
            if (null != precision) {
                this.userProvidesPrecision = true;
            }
            if (null != scale) {
                this.userProvidesScale = true;
            }
            if (!this.isOutput() && JavaType.SHORT == javaType && (JDBCType.TINYINT == jdbcType || JDBCType.SMALLINT == jdbcType)) {
                if ((short)value >= 0 && (short)value <= 255) {
                    value = ((Short)value).byteValue();
                    javaType = JavaType.of(value);
                    jdbcType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                }
                else if (JDBCType.TINYINT == jdbcType) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    final Object[] msgArgs = { javaType.toString().toLowerCase(Locale.ENGLISH), jdbcType.toString().toLowerCase(Locale.ENGLISH) };
                    throw new SQLServerException(form.format(msgArgs), (Throwable)null);
                }
            }
        }
        if (forceEncrypt && !Util.shouldHonorAEForParameters(stmtColumnEncriptionSetting, con)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAEFalse"));
            final Object[] msgArgs = { parameterIndex, userSQL };
            SQLServerException.makeFromDriverError(con, this, form.format(msgArgs), null, true);
        }
        if ((JDBCType.DATETIMEOFFSET == jdbcType || JavaType.DATETIMEOFFSET == javaType) && !con.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        if (JavaType.TVP == javaType) {
            TVP tvpValue;
            if (null == value) {
                tvpValue = new TVP(tvpName);
            }
            else if (value instanceof SQLServerDataTable) {
                tvpValue = new TVP(tvpName, (SQLServerDataTable)value);
            }
            else if (value instanceof ResultSet) {
                tvpValue = new TVP(tvpName, (ResultSet)value);
            }
            else {
                if (!(value instanceof ISQLServerDataRecord)) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_TVPInvalidValue"));
                    final Object[] msgArgs2 = { parameterIndex };
                    throw new SQLServerException(form2.format(msgArgs2), (Throwable)null);
                }
                tvpValue = new TVP(tvpName, (ISQLServerDataRecord)value);
            }
            if (!tvpValue.isNull() && 0 == tvpValue.getTVPColumnCount()) {
                throw new SQLServerException(SQLServerException.getErrString("R_TVPEmptyMetadata"), (Throwable)null);
            }
            this.name = tvpValue.getTVPName();
            this.schemaName = tvpValue.getOwningSchemaNameTVP();
            value = tvpValue;
        }
        if (this.shouldHonorAEForParameter) {
            this.setForceEncryption(forceEncrypt);
            if (!this.isOutput() || this.jdbcTypeSetByUser == null) {
                this.setJdbcTypeSetByUser(jdbcType);
            }
            if ((!jdbcType.isTextual() && !jdbcType.isBinary()) || !this.isOutput() || this.valueLength == 0) {
                this.valueLength = Util.getValueLengthBaseOnJavaType(value, javaType, precision, scale, jdbcType);
            }
            if (null != scale) {
                this.outScale = scale;
            }
        }
        if (con.sendStringParametersAsUnicode() && (JavaType.STRING == javaType || JavaType.READER == javaType || JavaType.CLOB == javaType || JavaType.OBJECT == javaType)) {
            jdbcType = getSSPAUJDBCType(jdbcType);
        }
        final DTV newDTV = new DTV();
        newDTV.setValue(con.getDatabaseCollation(), jdbcType, value, javaType, streamSetterArgs, calendar, scale, con, forceEncrypt);
        if (!con.sendStringParametersAsUnicode()) {
            newDTV.sendStringParametersAsUnicode = false;
        }
        final DTV dtv = newDTV;
        this.setterDTV = dtv;
        this.inputDTV = dtv;
    }
    
    boolean isNull() {
        return null != this.getterDTV && this.getterDTV.isNull();
    }
    
    boolean isValueGotten() {
        return null != this.getterDTV;
    }
    
    Object getValue(final JDBCType jdbcType, final InputStreamGetterArgs getterArgs, final Calendar cal, final TDSReader tdsReader) throws SQLServerException {
        if (null == this.getterDTV) {
            this.getterDTV = new DTV();
        }
        this.deriveTypeInfo(tdsReader);
        return this.getterDTV.getValue(jdbcType, this.outScale, getterArgs, cal, this.typeInfo, this.cryptoMeta, tdsReader);
    }
    
    Object getSetterValue() {
        return this.setterDTV.getSetterValue();
    }
    
    int getInt(final TDSReader tdsReader) throws SQLServerException {
        final Integer value = (Integer)this.getValue(JDBCType.INTEGER, null, null, tdsReader);
        return (null != value) ? value : 0;
    }
    
    String getTypeDefinition(final SQLServerConnection con, final TDSReader tdsReader) throws SQLServerException {
        if (null == this.inputDTV) {
            return null;
        }
        this.inputDTV.executeOp(new GetTypeDefinitionOp(this, con));
        return this.typeDefinition;
    }
    
    void sendByRPC(final TDSWriter tdsWriter, final SQLServerConnection conn) throws SQLServerException {
        assert null != this.inputDTV : "Parameter was neither set nor registered";
        try {
            this.inputDTV.sendCryptoMetaData(this.cryptoMeta, tdsWriter);
            this.inputDTV.setJdbcTypeSetByUser(this.getJdbcTypeSetByUser(), this.getValueLength());
            this.inputDTV.sendByRPC(this.name, null, conn.getDatabaseCollation(), this.valueLength, this.isOutput() ? this.outScale : this.scale, this.isOutput(), tdsWriter, conn);
        }
        finally {
            this.inputDTV.sendCryptoMetaData(null, tdsWriter);
        }
        if (JavaType.INPUTSTREAM == this.inputDTV.getJavaType() || JavaType.READER == this.inputDTV.getJavaType()) {
            final DTV dtv = null;
            this.setterDTV = dtv;
            this.inputDTV = dtv;
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
    
    void setValueLength(final int valueLength) {
        this.valueLength = valueLength;
        this.userProvidesPrecision = true;
    }
    
    boolean getForceEncryption() {
        return this.forceEncryption;
    }
    
    void setForceEncryption(final boolean forceEncryption) {
        this.forceEncryption = forceEncryption;
    }
    
    final class GetTypeDefinitionOp extends DTVExecuteOp
    {
        private static final String NVARCHAR_MAX = "nvarchar(max)";
        private static final String NVARCHAR_4K = "nvarchar(4000)";
        private static final String NTEXT = "ntext";
        private static final String VARCHAR_MAX = "varchar(max)";
        private static final String VARCHAR_8K = "varchar(8000)";
        private static final String TEXT = "text";
        private static final String VARBINARY_MAX = "varbinary(max)";
        private static final String VARBINARY_8K = "varbinary(8000)";
        private static final String IMAGE = "image";
        private final Parameter param;
        private final SQLServerConnection con;
        
        GetTypeDefinitionOp(final Parameter param, final SQLServerConnection con) {
            this.param = param;
            this.con = con;
        }
        
        private void setTypeDefinition(final DTV dtv) {
            switch (dtv.getJdbcType()) {
                case TINYINT: {
                    this.param.typeDefinition = SSType.TINYINT.toString();
                    break;
                }
                case SMALLINT: {
                    this.param.typeDefinition = SSType.SMALLINT.toString();
                    break;
                }
                case INTEGER: {
                    this.param.typeDefinition = SSType.INTEGER.toString();
                    break;
                }
                case BIGINT: {
                    this.param.typeDefinition = SSType.BIGINT.toString();
                    break;
                }
                case REAL: {
                    if (this.param.shouldHonorAEForParameter && null != Parameter.this.jdbcTypeSetByUser && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.REAL.toString();
                        break;
                    }
                    this.param.typeDefinition = SSType.FLOAT.toString();
                    break;
                }
                case FLOAT:
                case DOUBLE: {
                    this.param.typeDefinition = SSType.FLOAT.toString();
                    break;
                }
                case DECIMAL:
                case NUMERIC: {
                    if (Parameter.this.scale > 38) {
                        Parameter.this.scale = 38;
                    }
                    final Integer inScale = dtv.getScale();
                    if (null != inScale && Parameter.this.scale < inScale) {
                        Parameter.this.scale = inScale;
                    }
                    if (this.param.isOutput() && Parameter.this.scale < this.param.getOutScale()) {
                        Parameter.this.scale = this.param.getOutScale();
                    }
                    if (!this.param.shouldHonorAEForParameter || null == Parameter.this.jdbcTypeSetByUser || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = "decimal(38," + Parameter.this.scale + ")";
                        break;
                    }
                    if (0 == Parameter.this.valueLength) {
                        if (!Parameter.this.isOutput()) {
                            this.param.typeDefinition = "decimal(18, " + Parameter.this.scale + ")";
                        }
                    }
                    else if (18 >= Parameter.this.valueLength) {
                        this.param.typeDefinition = "decimal(18," + Parameter.this.scale + ")";
                        if (18 < Parameter.this.valueLength + Parameter.this.scale) {
                            this.param.typeDefinition = "decimal(" + (18 + Parameter.this.scale) + "," + Parameter.this.scale + ")";
                        }
                    }
                    else {
                        this.param.typeDefinition = "decimal(38," + Parameter.this.scale + ")";
                    }
                    if (Parameter.this.isOutput()) {
                        this.param.typeDefinition = "decimal(38, " + Parameter.this.scale + ")";
                    }
                    if (Parameter.this.userProvidesPrecision) {
                        this.param.typeDefinition = "decimal(" + Parameter.this.valueLength + "," + Parameter.this.scale + ")";
                        break;
                    }
                    break;
                }
                case MONEY: {
                    this.param.typeDefinition = SSType.MONEY.toString();
                    break;
                }
                case SMALLMONEY: {
                    this.param.typeDefinition = SSType.MONEY.toString();
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.SMALLMONEY.toString();
                        break;
                    }
                    break;
                }
                case BIT:
                case BOOLEAN: {
                    this.param.typeDefinition = SSType.BIT.toString();
                    break;
                }
                case LONGVARBINARY:
                case BLOB: {
                    this.param.typeDefinition = "varbinary(max)";
                    break;
                }
                case BINARY:
                case VARBINARY: {
                    if ("varbinary(max)".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if ("image".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if (!this.param.shouldHonorAEForParameter || null == Parameter.this.jdbcTypeSetByUser || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = "varbinary(8000)";
                        break;
                    }
                    if (0 == Parameter.this.valueLength) {
                        this.param.typeDefinition = "varbinary(1)";
                        Parameter.this.valueLength++;
                    }
                    else {
                        this.param.typeDefinition = "varbinary(" + Parameter.this.valueLength + ")";
                    }
                    if (JDBCType.LONGVARBINARY == Parameter.this.jdbcTypeSetByUser) {
                        this.param.typeDefinition = "varbinary(max)";
                        break;
                    }
                    break;
                }
                case DATE: {
                    this.param.typeDefinition = (this.con.isKatmaiOrLater() ? SSType.DATE.toString() : SSType.DATETIME.toString());
                    break;
                }
                case TIME: {
                    if (!this.param.shouldHonorAEForParameter || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = (this.con.getSendTimeAsDatetime() ? SSType.DATETIME.toString() : SSType.TIME.toString());
                        break;
                    }
                    if (Parameter.this.userProvidesScale) {
                        this.param.typeDefinition = SSType.TIME.toString() + "(" + Parameter.this.outScale + ")";
                        break;
                    }
                    this.param.typeDefinition = SSType.TIME.toString() + "(" + Parameter.this.valueLength + ")";
                    break;
                }
                case TIMESTAMP: {
                    if (!this.param.shouldHonorAEForParameter || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = (this.con.isKatmaiOrLater() ? SSType.DATETIME2.toString() : SSType.DATETIME.toString());
                        break;
                    }
                    if (Parameter.this.userProvidesScale) {
                        this.param.typeDefinition = (this.con.isKatmaiOrLater() ? (SSType.DATETIME2.toString() + "(" + Parameter.this.outScale + ")") : SSType.DATETIME.toString());
                        break;
                    }
                    this.param.typeDefinition = (this.con.isKatmaiOrLater() ? (SSType.DATETIME2.toString() + "(" + Parameter.this.valueLength + ")") : SSType.DATETIME.toString());
                    break;
                }
                case DATETIME: {
                    this.param.typeDefinition = SSType.DATETIME2.toString();
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.DATETIME.toString();
                    }
                    if (!this.param.shouldHonorAEForParameter) {
                        if (this.param.isOutput()) {
                            this.param.typeDefinition = SSType.DATETIME2.toString() + "(" + Parameter.this.outScale + ")";
                            break;
                        }
                        break;
                    }
                    else {
                        if (null == this.param.getCryptoMetadata() && this.param.renewDefinition && this.param.isOutput()) {
                            this.param.typeDefinition = SSType.DATETIME2.toString() + "(" + Parameter.this.outScale + ")";
                            break;
                        }
                        break;
                    }
                    break;
                }
                case SMALLDATETIME: {
                    this.param.typeDefinition = SSType.DATETIME2.toString();
                    if (this.param.shouldHonorAEForParameter && (null != this.param.getCryptoMetadata() || !this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.SMALLDATETIME.toString();
                        break;
                    }
                    break;
                }
                case TIME_WITH_TIMEZONE:
                case TIMESTAMP_WITH_TIMEZONE:
                case DATETIMEOFFSET: {
                    if (!this.param.shouldHonorAEForParameter || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = SSType.DATETIMEOFFSET.toString();
                        break;
                    }
                    if (Parameter.this.userProvidesScale) {
                        this.param.typeDefinition = SSType.DATETIMEOFFSET.toString() + "(" + Parameter.this.outScale + ")";
                        break;
                    }
                    this.param.typeDefinition = SSType.DATETIMEOFFSET.toString() + "(" + Parameter.this.valueLength + ")";
                    break;
                }
                case LONGVARCHAR:
                case CLOB: {
                    this.param.typeDefinition = "varchar(max)";
                    break;
                }
                case CHAR:
                case VARCHAR: {
                    if ("varchar(max)".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if ("text".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if (!this.param.shouldHonorAEForParameter || null == Parameter.this.jdbcTypeSetByUser || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = "varchar(8000)";
                        break;
                    }
                    if (0 == Parameter.this.valueLength) {
                        this.param.typeDefinition = "varchar(1)";
                        Parameter.this.valueLength++;
                        break;
                    }
                    this.param.typeDefinition = "varchar(" + Parameter.this.valueLength + ")";
                    if (8000 <= Parameter.this.valueLength) {
                        this.param.typeDefinition = "varchar(max)";
                        break;
                    }
                    break;
                }
                case LONGNVARCHAR: {
                    if (!this.param.shouldHonorAEForParameter || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = "nvarchar(max)";
                        break;
                    }
                    if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.VARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.CHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.LONGVARCHAR)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "varchar(1)";
                            Parameter.this.valueLength++;
                        }
                        else if (8000 < Parameter.this.valueLength) {
                            this.param.typeDefinition = "varchar(max)";
                        }
                        else {
                            this.param.typeDefinition = "varchar(" + Parameter.this.valueLength + ")";
                        }
                        if (Parameter.this.jdbcTypeSetByUser == JDBCType.LONGVARCHAR) {
                            this.param.typeDefinition = "varchar(max)";
                            break;
                        }
                        break;
                    }
                    else if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.NVARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.LONGNVARCHAR)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(1)";
                            Parameter.this.valueLength++;
                        }
                        else if (4000 < Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(max)";
                        }
                        else {
                            this.param.typeDefinition = "nvarchar(" + Parameter.this.valueLength + ")";
                        }
                        if (Parameter.this.jdbcTypeSetByUser == JDBCType.LONGNVARCHAR) {
                            this.param.typeDefinition = "nvarchar(max)";
                            break;
                        }
                        break;
                    }
                    else {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(1)";
                            Parameter.this.valueLength++;
                            break;
                        }
                        this.param.typeDefinition = "nvarchar(" + Parameter.this.valueLength + ")";
                        if (8000 <= Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(max)";
                            break;
                        }
                        break;
                    }
                    break;
                }
                case NCLOB: {
                    this.param.typeDefinition = "nvarchar(max)";
                    break;
                }
                case NCHAR:
                case NVARCHAR: {
                    if ("nvarchar(max)".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if ("ntext".equals(this.param.typeDefinition)) {
                        break;
                    }
                    if (!this.param.shouldHonorAEForParameter || (null == this.param.getCryptoMetadata() && this.param.renewDefinition)) {
                        this.param.typeDefinition = "nvarchar(4000)";
                        break;
                    }
                    if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.VARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.CHAR || JDBCType.LONGVARCHAR == Parameter.this.jdbcTypeSetByUser)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "varchar(1)";
                            Parameter.this.valueLength++;
                        }
                        else {
                            this.param.typeDefinition = "varchar(" + Parameter.this.valueLength + ")";
                            if (8000 < Parameter.this.valueLength) {
                                this.param.typeDefinition = "varchar(max)";
                            }
                        }
                        if (JDBCType.LONGVARCHAR == Parameter.this.jdbcTypeSetByUser) {
                            this.param.typeDefinition = "varchar(max)";
                            break;
                        }
                        break;
                    }
                    else if (null != Parameter.this.jdbcTypeSetByUser && (Parameter.this.jdbcTypeSetByUser == JDBCType.NVARCHAR || Parameter.this.jdbcTypeSetByUser == JDBCType.NCHAR || JDBCType.LONGNVARCHAR == Parameter.this.jdbcTypeSetByUser)) {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(1)";
                            Parameter.this.valueLength++;
                        }
                        else {
                            this.param.typeDefinition = "nvarchar(" + Parameter.this.valueLength + ")";
                            if (8000 <= Parameter.this.valueLength) {
                                this.param.typeDefinition = "nvarchar(max)";
                            }
                        }
                        if (JDBCType.LONGNVARCHAR == Parameter.this.jdbcTypeSetByUser) {
                            this.param.typeDefinition = "nvarchar(max)";
                            break;
                        }
                        break;
                    }
                    else {
                        if (0 == Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(1)";
                            Parameter.this.valueLength++;
                            break;
                        }
                        this.param.typeDefinition = "nvarchar(" + Parameter.this.valueLength + ")";
                        if (8000 <= Parameter.this.valueLength) {
                            this.param.typeDefinition = "nvarchar(max)";
                            break;
                        }
                        break;
                    }
                    break;
                }
                case SQLXML: {
                    this.param.typeDefinition = SSType.XML.toString();
                    break;
                }
                case TVP: {
                    final String schema = this.param.schemaName;
                    if (null != schema) {
                        this.param.typeDefinition = "[" + schema + "].[" + this.param.name + "] READONLY";
                        break;
                    }
                    this.param.typeDefinition = "[" + this.param.name + "] READONLY";
                    break;
                }
                case GUID: {
                    this.param.typeDefinition = SSType.GUID.toString();
                    break;
                }
                case SQL_VARIANT: {
                    this.param.typeDefinition = SSType.SQL_VARIANT.toString();
                    break;
                }
                case GEOMETRY: {
                    this.param.typeDefinition = SSType.GEOMETRY.toString();
                    break;
                }
                case GEOGRAPHY: {
                    this.param.typeDefinition = SSType.GEOGRAPHY.toString();
                    break;
                }
                default: {
                    assert false : "Unexpected JDBC type " + dtv.getJdbcType();
                    break;
                }
            }
        }
        
        @Override
        void execute(final DTV dtv, final String strValue) throws SQLServerException {
            if (null != strValue && strValue.length() > 4000) {
                dtv.setJdbcType(JDBCType.LONGNVARCHAR);
            }
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Clob clobValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Byte byteValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Integer intValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Time timeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Date dateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Timestamp timestampValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final java.util.Date utildateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Calendar calendarValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final LocalDate localDateValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final LocalTime localTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final LocalDateTime localDateTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final OffsetTime offsetTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final OffsetDateTime OffsetDateTimeValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final DateTimeOffset dtoValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Float floatValue) throws SQLServerException {
            Parameter.this.scale = 4;
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Double doubleValue) throws SQLServerException {
            Parameter.this.scale = 4;
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final BigDecimal bigDecimalValue) throws SQLServerException {
            if (null != bigDecimalValue) {
                Parameter.this.scale = bigDecimalValue.scale();
                if (Parameter.this.scale < 0) {
                    Parameter.this.scale = 0;
                }
            }
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Long longValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final BigInteger bigIntegerValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Short shortValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Boolean booleanValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final byte[] byteArrayValue) throws SQLServerException {
            if (null != byteArrayValue && byteArrayValue.length > 8000) {
                dtv.setJdbcType(dtv.getJdbcType().isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
            }
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Blob blobValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final InputStream inputStreamValue) throws SQLServerException {
            final StreamSetterArgs streamSetterArgs = dtv.getStreamSetterArgs();
            final JDBCType jdbcType = dtv.getJdbcType();
            if (JDBCType.CHAR == jdbcType || JDBCType.VARCHAR == jdbcType || JDBCType.BINARY == jdbcType || JDBCType.VARBINARY == jdbcType) {
                if (streamSetterArgs.getLength() > 8000L) {
                    dtv.setJdbcType(jdbcType.isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
                }
                else if (-1L == streamSetterArgs.getLength()) {
                    final byte[] vartypeBytes = new byte[8001];
                    final BufferedInputStream bufferedStream = new BufferedInputStream(inputStreamValue, vartypeBytes.length);
                    int bytesRead = 0;
                    try {
                        bufferedStream.mark(vartypeBytes.length);
                        bytesRead = bufferedStream.read(vartypeBytes, 0, vartypeBytes.length);
                        if (-1 == bytesRead) {
                            bytesRead = 0;
                        }
                        bufferedStream.reset();
                    }
                    catch (final IOException e) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                        final Object[] msgArgs = { e.toString() };
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                    }
                    dtv.setValue(bufferedStream, JavaType.INPUTSTREAM);
                    if (bytesRead > 8000) {
                        dtv.setJdbcType(jdbcType.isBinary() ? JDBCType.LONGVARBINARY : JDBCType.LONGVARCHAR);
                    }
                    else {
                        streamSetterArgs.setLength(bytesRead);
                    }
                }
            }
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final Reader readerValue) throws SQLServerException {
            if (JDBCType.NCHAR == dtv.getJdbcType() || JDBCType.NVARCHAR == dtv.getJdbcType()) {
                final StreamSetterArgs streamSetterArgs = dtv.getStreamSetterArgs();
                if (streamSetterArgs.getLength() > 4000L) {
                    dtv.setJdbcType(JDBCType.LONGNVARCHAR);
                }
                else if (-1L == streamSetterArgs.getLength()) {
                    final char[] vartypeChars = new char[4001];
                    final BufferedReader bufferedReader = new BufferedReader(readerValue, vartypeChars.length);
                    int charsRead = 0;
                    try {
                        bufferedReader.mark(vartypeChars.length);
                        charsRead = bufferedReader.read(vartypeChars, 0, vartypeChars.length);
                        if (-1 == charsRead) {
                            charsRead = 0;
                        }
                        bufferedReader.reset();
                    }
                    catch (final IOException e) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                        final Object[] msgArgs = { e.toString() };
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                    }
                    dtv.setValue(bufferedReader, JavaType.READER);
                    if (charsRead > 4000) {
                        dtv.setJdbcType(JDBCType.LONGNVARCHAR);
                    }
                    else {
                        streamSetterArgs.setLength(charsRead);
                    }
                }
            }
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final SQLServerSQLXML xmlValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final TVP tvpValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
        
        @Override
        void execute(final DTV dtv, final SqlVariant SqlVariantValue) throws SQLServerException {
            this.setTypeDefinition(dtv);
        }
    }
}
