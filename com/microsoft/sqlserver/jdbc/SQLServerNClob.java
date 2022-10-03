package com.microsoft.sqlserver.jdbc;

import java.io.Reader;
import java.sql.Clob;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.SQLException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.sql.NClob;

public final class SQLServerNClob extends SQLServerClobBase implements NClob
{
    private static final long serialVersionUID = 3593610902551842327L;
    private static final Logger logger;
    
    SQLServerNClob(final SQLServerConnection connection) {
        super(connection, "", connection.getDatabaseCollation(), SQLServerNClob.logger, null);
        this.setDefaultCharset(StandardCharsets.UTF_16LE);
    }
    
    SQLServerNClob(final BaseInputStream stream, final TypeInfo typeInfo) {
        super(null, stream, typeInfo.getSQLCollation(), SQLServerNClob.logger, typeInfo);
        this.setDefaultCharset(StandardCharsets.UTF_16LE);
    }
    
    @Override
    public InputStream getAsciiStream() throws SQLException {
        this.fillFromStream();
        return super.getAsciiStream();
    }
    
    @Override
    final JDBCType getJdbcType() {
        return JDBCType.NCLOB;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerNClob");
    }
}
