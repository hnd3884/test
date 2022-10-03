package com.microsoft.sqlserver.jdbc;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.sql.ResultSetMetaData;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.Map;
import java.util.List;
import java.sql.ParameterMetaData;

public final class SQLServerParameterMetaData implements ParameterMetaData
{
    private static final int SQL_SERVER_2012_VERSION = 11;
    private final SQLServerPreparedStatement stmtParent;
    private SQLServerConnection con;
    private List<Map<String, Object>> procMetadata;
    protected boolean procedureIsFound;
    private static final Logger logger;
    private static final AtomicInteger baseID;
    private final String traceID;
    boolean isTVP;
    Map<Integer, QueryMeta> queryMetaMap;
    
    private static int nextInstanceID() {
        return SQLServerParameterMetaData.baseID.incrementAndGet();
    }
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    private void parseQueryMeta(final ResultSet rsQueryMeta) throws SQLServerException {
        final Pattern datatypePattern = Pattern.compile("(.*)\\((.*)(\\)|,(.*)\\))");
        try {
            if (null != rsQueryMeta) {
                while (rsQueryMeta.next()) {
                    final QueryMeta qm = new QueryMeta();
                    SSType ssType = null;
                    final int paramOrdinal = rsQueryMeta.getInt("parameter_ordinal");
                    String typename = rsQueryMeta.getString("suggested_system_type_name");
                    if (null == typename) {
                        typename = rsQueryMeta.getString("suggested_user_type_name");
                        try (final SQLServerPreparedStatement pstmt = (SQLServerPreparedStatement)this.con.prepareStatement("select max_length, precision, scale, is_nullable from sys.assembly_types where name = ?")) {
                            pstmt.setNString(1, typename);
                            try (final ResultSet assemblyRs = pstmt.executeQuery()) {
                                if (assemblyRs.next()) {
                                    qm.parameterTypeName = typename;
                                    qm.precision = assemblyRs.getInt("max_length");
                                    qm.scale = assemblyRs.getInt("scale");
                                    ssType = SSType.UDT;
                                }
                            }
                        }
                    }
                    else {
                        qm.precision = rsQueryMeta.getInt("suggested_precision");
                        qm.scale = rsQueryMeta.getInt("suggested_scale");
                        final Matcher matcher = datatypePattern.matcher(typename);
                        Label_0465: {
                            if (matcher.matches()) {
                                ssType = SSType.of(matcher.group(1));
                                if ("varchar(max)".equalsIgnoreCase(typename) || "varbinary(max)".equalsIgnoreCase(typename)) {
                                    qm.precision = Integer.MAX_VALUE;
                                }
                                else if ("nvarchar(max)".equalsIgnoreCase(typename)) {
                                    qm.precision = 1073741823;
                                }
                                else {
                                    if (SSType.Category.CHARACTER != ssType.category && SSType.Category.BINARY != ssType.category) {
                                        if (SSType.Category.NCHARACTER != ssType.category) {
                                            break Label_0465;
                                        }
                                    }
                                    try {
                                        qm.precision = Integer.parseInt(matcher.group(2));
                                    }
                                    catch (final NumberFormatException e) {
                                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_metaDataErrorForParameter"));
                                        final Object[] msgArgs = { paramOrdinal };
                                        SQLServerException.makeFromDriverError(this.con, this.stmtParent, form.format(msgArgs) + " " + e.getMessage(), null, false);
                                    }
                                }
                            }
                            else {
                                ssType = SSType.of(typename);
                            }
                        }
                        if (SSType.FLOAT == ssType) {
                            qm.precision = 15;
                        }
                        else if (SSType.REAL == ssType) {
                            qm.precision = 7;
                        }
                        else if (SSType.TEXT == ssType) {
                            qm.precision = Integer.MAX_VALUE;
                        }
                        else if (SSType.NTEXT == ssType) {
                            qm.precision = 1073741823;
                        }
                        else if (SSType.IMAGE == ssType) {
                            qm.precision = Integer.MAX_VALUE;
                        }
                        else if (SSType.GUID == ssType) {
                            qm.precision = 36;
                        }
                        else if (SSType.TIMESTAMP == ssType) {
                            qm.precision = 8;
                        }
                        else if (SSType.XML == ssType) {
                            qm.precision = 1073741823;
                        }
                        qm.parameterTypeName = ssType.toString();
                    }
                    if (null == ssType) {
                        throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), (Throwable)null);
                    }
                    final JDBCType jdbcType = ssType.getJDBCType();
                    qm.parameterClassName = jdbcType.className();
                    qm.parameterType = jdbcType.getIntValue();
                    qm.isSigned = (SSType.Category.NUMERIC == ssType.category && SSType.BIT != ssType && SSType.TINYINT != ssType);
                    this.queryMetaMap.put(paramOrdinal, qm);
                }
            }
        }
        catch (final SQLException e2) {
            throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), e2);
        }
    }
    
    private void parseFMTQueryMeta(final ResultSetMetaData md, final SQLServerFMTQuery f) throws SQLServerException {
        try {
            final List<String> columns = f.getColumns();
            final List<List<String>> params = f.getValuesList();
            int valueListOffset = 0;
            int mdIndex = 1;
            int mapIndex = 1;
            for (int i = 0; i < columns.size(); ++i) {
                if ("*".equals(columns.get(i))) {
                    for (int j = 0; j < params.get(valueListOffset).size(); ++j) {
                        if ("?".equals(params.get(valueListOffset).get(j)) && !md.isAutoIncrement(mdIndex + j)) {
                            final QueryMeta qm = this.getQueryMetaFromResultSetMetaData(md, mdIndex + j);
                            this.queryMetaMap.put(mapIndex++, qm);
                            ++i;
                        }
                    }
                    mdIndex += params.get(valueListOffset).size();
                    ++valueListOffset;
                }
                else {
                    final QueryMeta qm2 = this.getQueryMetaFromResultSetMetaData(md, mdIndex);
                    this.queryMetaMap.put(mapIndex++, qm2);
                    ++mdIndex;
                }
            }
        }
        catch (final SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), e);
        }
    }
    
    private QueryMeta getQueryMetaFromResultSetMetaData(final ResultSetMetaData md, final int index) throws SQLException {
        final QueryMeta qm = new QueryMeta();
        qm.parameterClassName = md.getColumnClassName(index);
        qm.parameterType = md.getColumnType(index);
        qm.parameterTypeName = md.getColumnTypeName(index);
        qm.precision = md.getPrecision(index);
        qm.scale = md.getScale(index);
        qm.isNullable = md.isNullable(index);
        qm.isSigned = md.isSigned(index);
        return qm;
    }
    
    String parseProcIdentifier(final String procIdentifier) throws SQLServerException {
        final ThreePartName threePartName = ThreePartName.parse(procIdentifier);
        final StringBuilder sb = new StringBuilder();
        if (threePartName.getDatabasePart() != null) {
            sb.append("@procedure_qualifier=");
            sb.append(threePartName.getDatabasePart());
            sb.append(", ");
        }
        if (threePartName.getOwnerPart() != null) {
            sb.append("@procedure_owner=");
            sb.append(threePartName.getOwnerPart());
            sb.append(", ");
        }
        if (threePartName.getProcedurePart() != null) {
            sb.append("@procedure_name=");
            sb.append(threePartName.getProcedurePart());
        }
        else {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, SQLServerException.getErrString("R_noMetadata"), null, false);
        }
        return sb.toString();
    }
    
    private void checkClosed() throws SQLServerException {
        this.con.checkClosed();
    }
    
    SQLServerParameterMetaData(final SQLServerPreparedStatement st, final String sProcString) throws SQLServerException {
        this.procedureIsFound = false;
        this.traceID = " SQLServerParameterMetaData:" + nextInstanceID();
        this.isTVP = false;
        this.queryMetaMap = null;
        assert null != st;
        this.stmtParent = st;
        this.con = st.connection;
        if (SQLServerParameterMetaData.logger.isLoggable(Level.FINE)) {
            SQLServerParameterMetaData.logger.fine(this.toString() + " created by (" + st.toString() + ")");
        }
        try {
            if (null != st.procedureName) {
                final String sProc = this.parseProcIdentifier(st.procedureName);
                try (final SQLServerStatement s = (SQLServerStatement)this.con.createStatement(1004, 1007);
                     final SQLServerResultSet rsProcedureMeta = s.executeQueryInternal(this.con.isKatmaiOrLater() ? ("exec sp_sproc_columns_100 " + sProc + ", @ODBCVer=3, @fUsePattern=0") : ("exec sp_sproc_columns " + sProc + ", @ODBCVer=3, @fUsePattern=0"))) {
                    if (rsProcedureMeta.next()) {
                        this.procedureIsFound = true;
                    }
                    else {
                        this.procedureIsFound = false;
                    }
                    rsProcedureMeta.beforeFirst();
                    rsProcedureMeta.getColumn(6).setFilter(new DataTypeFilter());
                    if (this.con.isKatmaiOrLater()) {
                        rsProcedureMeta.getColumn(8).setFilter(new ZeroFixupFilter());
                        rsProcedureMeta.getColumn(9).setFilter(new ZeroFixupFilter());
                        rsProcedureMeta.getColumn(17).setFilter(new ZeroFixupFilter());
                    }
                    this.procMetadata = new ArrayList<Map<String, Object>>();
                    while (rsProcedureMeta.next()) {
                        this.procMetadata.add(new HashMap<String, Object>() {
                            {
                                ((HashMap<String, Short>)this).put("DATA_TYPE", rsProcedureMeta.getShort("DATA_TYPE"));
                                ((HashMap<String, Integer>)this).put("COLUMN_TYPE", rsProcedureMeta.getInt("COLUMN_TYPE"));
                                ((HashMap<String, String>)this).put("TYPE_NAME", rsProcedureMeta.getString("TYPE_NAME"));
                                ((HashMap<String, Integer>)this).put("PRECISION", rsProcedureMeta.getInt("PRECISION"));
                                ((HashMap<String, Integer>)this).put("SCALE", rsProcedureMeta.getInt("SCALE"));
                                ((HashMap<String, Integer>)this).put("NULLABLE", rsProcedureMeta.getInt("NULLABLE"));
                                ((HashMap<String, String>)this).put("SS_TYPE_SCHEMA_NAME", rsProcedureMeta.getString("SS_TYPE_SCHEMA_NAME"));
                            }
                        });
                    }
                }
            }
            else {
                this.queryMetaMap = new HashMap<Integer, QueryMeta>();
                if (this.con.getServerMajorVersion() >= 11 && !st.getUseFmtOnly()) {
                    final String preparedSQL = this.con.replaceParameterMarkers(this.stmtParent.userSQL, this.stmtParent.userSQLParamPositions, this.stmtParent.inOutParam, this.stmtParent.bReturnValueSyntax);
                    try (final SQLServerCallableStatement cstmt = (SQLServerCallableStatement)this.con.prepareCall("exec sp_describe_undeclared_parameters ?")) {
                        cstmt.setNString(1, preparedSQL);
                        this.parseQueryMeta(cstmt.executeQueryInternal());
                    }
                }
                else {
                    final SQLServerFMTQuery f = new SQLServerFMTQuery(sProcString);
                    try (final SQLServerStatement stmt = (SQLServerStatement)this.con.createStatement();
                         final ResultSet rs = stmt.executeQuery(f.getFMTQuery())) {
                        this.parseFMTQueryMeta(rs.getMetaData(), f);
                    }
                }
            }
        }
        catch (final SQLServerException e) {
            throw e;
        }
        catch (final SQLException e2) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e2.getMessage(), null, false);
        }
        catch (final StringIndexOutOfBoundsException e3) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e3.getMessage(), null, false);
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        final boolean f = iface.isInstance(this);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        return t;
    }
    
    private Map<String, Object> getParameterInfo(final int param) {
        if (this.stmtParent.bReturnValueSyntax && this.isTVP) {
            return this.procMetadata.get(param - 1);
        }
        return this.procMetadata.get(param);
    }
    
    private boolean isValidParamProc(final int n) {
        return (this.stmtParent.bReturnValueSyntax && this.isTVP && this.procMetadata.size() >= n) || this.procMetadata.size() > n;
    }
    
    private boolean isValidParamQuery(final int n) {
        return null != this.queryMetaMap && this.queryMetaMap.containsKey(n);
    }
    
    private void checkParam(final int param) throws SQLServerException {
        if (null == this.procMetadata) {
            if (!this.isValidParamQuery(param)) {
                SQLServerException.makeFromDriverError(this.con, this.stmtParent, SQLServerException.getErrString("R_noMetadata"), null, false);
            }
        }
        else if (!this.isValidParamProc(param)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidParameterNumber"));
            final Object[] msgArgs = { param };
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, form.format(msgArgs), null, false);
        }
    }
    
    @Override
    public String getParameterClassName(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        try {
            if (null == this.procMetadata) {
                return this.queryMetaMap.get(param).parameterClassName;
            }
            return JDBCType.of(this.getParameterInfo(param).get("DATA_TYPE")).className();
        }
        catch (final SQLServerException e) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e.getMessage(), null, false);
            return null;
        }
    }
    
    @Override
    public int getParameterCount() throws SQLServerException {
        this.checkClosed();
        if (null == this.procMetadata) {
            return this.queryMetaMap.size();
        }
        return (this.procMetadata.size() == 0) ? 0 : (this.procMetadata.size() - 1);
    }
    
    @Override
    public int getParameterMode(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return 1;
        }
        final int n = this.getParameterInfo(param).get("COLUMN_TYPE");
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 4;
        }
        return 0;
    }
    
    @Override
    public int getParameterType(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        int parameterType = 0;
        if (null == this.procMetadata) {
            parameterType = this.queryMetaMap.get(param).parameterType;
        }
        else {
            parameterType = this.getParameterInfo(param).get("DATA_TYPE");
        }
        if (0 != parameterType) {
            switch (parameterType) {
                case -151:
                case -150: {
                    parameterType = SSType.DATETIME2.getJDBCType().asJavaSqlType();
                    break;
                }
                case -148:
                case -146: {
                    parameterType = SSType.DECIMAL.getJDBCType().asJavaSqlType();
                    break;
                }
                case -145: {
                    parameterType = SSType.CHAR.getJDBCType().asJavaSqlType();
                    break;
                }
            }
        }
        return parameterType;
    }
    
    @Override
    public String getParameterTypeName(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get(param).parameterTypeName;
        }
        return this.getParameterInfo(param).get("TYPE_NAME").toString();
    }
    
    @Override
    public int getPrecision(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get(param).precision;
        }
        return this.getParameterInfo(param).get("PRECISION");
    }
    
    @Override
    public int getScale(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (null == this.procMetadata) {
            return this.queryMetaMap.get(param).scale;
        }
        return this.getParameterInfo(param).get("SCALE");
    }
    
    @Override
    public int isNullable(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        if (this.procMetadata == null) {
            return this.queryMetaMap.get(param).isNullable;
        }
        return this.getParameterInfo(param).get("NULLABLE");
    }
    
    @Override
    public boolean isSigned(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        try {
            if (null == this.procMetadata) {
                return this.queryMetaMap.get(param).isSigned;
            }
            return JDBCType.of(this.getParameterInfo(param).get("DATA_TYPE")).isSigned();
        }
        catch (final SQLException e) {
            SQLServerException.makeFromDriverError(this.con, this.stmtParent, e.getMessage(), null, false);
            return false;
        }
    }
    
    String getTVPSchemaFromStoredProcedure(final int param) throws SQLServerException {
        this.checkClosed();
        this.checkParam(param);
        return this.getParameterInfo(param).get("SS_TYPE_SCHEMA_NAME");
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerParameterMetaData");
        baseID = new AtomicInteger(0);
    }
    
    class QueryMeta
    {
        String parameterClassName;
        int parameterType;
        String parameterTypeName;
        int precision;
        int scale;
        int isNullable;
        boolean isSigned;
        
        QueryMeta() {
            this.parameterClassName = null;
            this.parameterType = 0;
            this.parameterTypeName = null;
            this.precision = 0;
            this.scale = 0;
            this.isNullable = 2;
            this.isSigned = false;
        }
    }
}
