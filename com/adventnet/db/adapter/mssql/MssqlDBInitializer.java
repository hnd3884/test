package com.adventnet.db.adapter.mssql;

import com.adventnet.ds.query.DataSet;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DBInitializer;

public class MssqlDBInitializer extends DBInitializer
{
    private static final Logger OUT;
    private String database;
    
    public MssqlDBInitializer() {
        this.database = null;
    }
    
    public String getDBName() throws SQLException {
        if (this.database == null) {
            this.setDBName();
        }
        return this.database;
    }
    
    private void setDBName() {
        this.database = RelationalAPI.getInstance().getDBAdapter().getDBProps().getProperty("DBName");
    }
    
    @Override
    public String getVersion() throws Exception {
        String version = null;
        Connection conn = null;
        try {
            final RelationalAPI relApi = RelationalAPI.getInstance();
            conn = relApi.getConnection();
            version = relApi.getDBAdapter().getDBSystemProperty(conn, "productversion");
            if (version != null && !version.equals("")) {
                if (!version.endsWith(".")) {
                    version += '.';
                }
                final int accuracy = 2;
                int splitIndex = 0;
                for (int i = 0; i < 2; ++i) {
                    final int tempIndex = version.indexOf(46, splitIndex);
                    if (tempIndex == -1) {
                        break;
                    }
                    splitIndex = tempIndex + 1;
                }
                version = version.substring(0, splitIndex - 1);
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return version;
    }
    
    @Override
    public boolean isServerStarted() throws IOException {
        final Properties props = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        return this.isServerStarted(Integer.parseInt(props.getProperty("Port")), props.getProperty("Server"));
    }
    
    protected Map<String, String> getDBFilesLocation() {
        final Map<String, String> map = new HashMap<String, String>();
        final String query = "SELECT physical_name, type_desc FROM sys.database_files";
        try (final Connection conn = RelationalAPI.getInstance().getConnection();
             final DataSet ds = RelationalAPI.getInstance().executeQuery(query, conn)) {
            while (ds.next()) {
                map.put(ds.getAsString(1), ds.getAsString(2));
            }
        }
        catch (final Exception e) {
            MssqlDBInitializer.OUT.log(Level.SEVERE, "Exception while getting location of data and log files :: " + e);
        }
        return map;
    }
    
    @Override
    public String getDBDataDirectory() {
        return this.getDBFilesLocation().toString();
    }
    
    @Override
    public byte getDBArchitecture() throws Exception {
        Connection conn = null;
        byte arch = -1;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            final String version = RelationalAPI.getInstance().getDBAdapter().getDBSystemProperty(conn, "edition");
            if (version.contains("64")) {
                arch = 64;
            }
            else if (version.contains("32")) {
                arch = 32;
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return arch;
    }
    
    static {
        OUT = Logger.getLogger(MssqlDBInitializer.class.getName());
    }
}
