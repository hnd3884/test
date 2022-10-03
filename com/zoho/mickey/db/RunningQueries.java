package com.zoho.mickey.db;

import java.util.Properties;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import java.sql.Connection;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.db.adapter.DBAdapter;
import com.zoho.mickey.db.mysql.MysqlRunningQueries;
import com.zoho.mickey.db.postgres.PostgresRunningQueries;
import com.zoho.mickey.db.mssql.MssqlRunningQueries;
import com.adventnet.db.api.RelationalAPI;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RunningQueries
{
    private static final Logger LOGGER;
    
    public static synchronized void dumpInformation() {
        final String className = getClassName();
        if (className != null && !className.isEmpty()) {
            try {
                final RunningQueries runningQueries = (RunningQueries)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                runningQueries.dumpQueryInformation();
            }
            catch (final InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
                RunningQueries.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        else {
            RunningQueries.LOGGER.severe("Unknown DB Specified");
        }
    }
    
    private static String getClassName() {
        final DBAdapter dbAdapter = RelationalAPI.getInstance().getDBAdapter();
        String className = dbAdapter.getDBProps().getProperty("RunningQueries");
        if (className == null) {
            final String dbType = dbAdapter.getDBType();
            switch (dbType) {
                case "mssql": {
                    className = MssqlRunningQueries.class.getName();
                    break;
                }
                case "postgres": {
                    className = PostgresRunningQueries.class.getName();
                    break;
                }
                case "mysql": {
                    className = MysqlRunningQueries.class.getName();
                    break;
                }
            }
        }
        return className;
    }
    
    protected void dumpInformation(final JSONObject jsonObject) {
        RunningQueries.LOGGER.fine(jsonObject.toString());
        final String lineSeparator = System.lineSeparator();
        final StringBuilder sb = new StringBuilder();
        for (final Object key : jsonObject.keySet()) {
            final JSONObject table = jsonObject.getJSONObject((String)key);
            if (table.length() > 0) {
                final String tableHeader = key.toString().replaceAll("_", " ");
                sb.append(lineSeparator);
                sb.append(new TableLogger(tableHeader, table).getTableContent());
                sb.append(lineSeparator);
            }
        }
        sb.append(lineSeparator).append(lineSeparator);
        RunningQueries.LOGGER.info(sb.toString());
    }
    
    protected JSONObject getAsJson(final String query, final Connection conn) throws SQLException {
        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(query)) {
            final ResultSetMetaData metadata = rs.getMetaData();
            final int count = metadata.getColumnCount();
            final JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i <= count; ++i) {
                jsonArray.put((Object)metadata.getColumnName(i));
            }
            jsonObject.put("columns", (Object)jsonArray);
            jsonArray = new JSONArray();
            while (rs.next()) {
                final JSONArray row = new JSONArray();
                for (int j = 1; j <= count; ++j) {
                    final String string = rs.getString(j);
                    row.put((Object)StringUtils.trimToNull(string));
                }
                jsonArray.put((Object)row);
            }
            jsonObject.put("data", (Object)jsonArray);
            return jsonObject;
        }
    }
    
    protected Connection getConnection() throws SQLException {
        final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        return DriverManager.getConnection(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
    }
    
    public abstract void dumpQueryInformation() throws SQLException;
    
    static {
        LOGGER = Logger.getLogger(RunningQueries.class.getName());
    }
}
