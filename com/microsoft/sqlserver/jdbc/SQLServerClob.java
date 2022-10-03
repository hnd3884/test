package com.microsoft.sqlserver.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.sql.Clob;

public class SQLServerClob extends SQLServerClobBase implements Clob
{
    private static final long serialVersionUID = 2872035282200133865L;
    private static final Logger logger;
    
    @Deprecated
    public SQLServerClob(final SQLServerConnection connection, final String data) {
        super(connection, data, (null == connection) ? null : connection.getDatabaseCollation(), SQLServerClob.logger, null);
        if (null == data) {
            throw new NullPointerException(SQLServerException.getErrString("R_cantSetNull"));
        }
    }
    
    SQLServerClob(final SQLServerConnection connection) {
        super(connection, "", connection.getDatabaseCollation(), SQLServerClob.logger, null);
    }
    
    SQLServerClob(final BaseInputStream stream, final TypeInfo typeInfo) throws SQLServerException, UnsupportedEncodingException {
        super(null, stream, typeInfo.getSQLCollation(), SQLServerClob.logger, typeInfo);
    }
    
    @Override
    final JDBCType getJdbcType() {
        return JDBCType.CLOB;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerClob");
    }
}
