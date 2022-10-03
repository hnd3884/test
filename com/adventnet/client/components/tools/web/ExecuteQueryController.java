package com.adventnet.client.components.tools.web;

import com.adventnet.client.view.web.WebViewAPI;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.Vector;
import com.adventnet.db.api.RelationalAPI;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.logging.Level;
import com.zoho.mickey.api.SQLStringAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.view.web.DefaultViewController;

public class ExecuteQueryController extends DefaultViewController
{
    private static Logger logger;
    private int rowCount;
    
    public ExecuteQueryController() {
        this.rowCount = 500;
    }
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        String dbName = PersistenceInitializer.getConfigurationValue("DSAdapter");
        if ("mds".equals(dbName)) {
            dbName = "mysql";
        }
        String query = "show tables";
        String task = "select";
        task = query.substring(0, 6);
        Hashtable hash = null;
        int rowsAffected = 0;
        String message = "";
        final HttpServletRequest request = viewCtx.getRequest();
        ExecuteQueryController.logger.finest("Execute Query : " + request.getParameter("execute"));
        if (request.getParameter("execute") != null) {
            query = request.getParameter("query");
            boolean validate = true;
            ExecuteQueryController.logger.finest("Query to be executed : " + query);
            if (request.getParameter("query") != null) {
                query = query.trim();
            }
            if (query.endsWith(";")) {
                query = query.substring(0, query.length() - 1);
            }
            if (query.length() < 6) {
                validate = false;
                message = "Error in query";
            }
            if (validate) {
                task = query.substring(0, 6);
                ExecuteQueryController.logger.finest("Task is : " + task);
                try {
                    final String firstFour = task.substring(0, 4);
                    if (task.equalsIgnoreCase("update") || task.equalsIgnoreCase("insert")) {
                        rowsAffected = this.executeUpdate(query);
                    }
                    else {
                        if (task.equalsIgnoreCase("delete")) {
                            return;
                        }
                        if (task.equalsIgnoreCase("select")) {
                            final String limitStr = request.getParameter("limit");
                            final String startIdxStr = request.getParameter("from");
                            int startIndex = 1;
                            int limit = -1;
                            if (startIdxStr != null) {
                                startIndex = Integer.parseInt(startIdxStr);
                            }
                            if (limitStr != null) {
                                limit = Integer.parseInt(limitStr);
                            }
                            Range range = null;
                            if (limit > 0) {
                                range = new Range(startIndex, limit);
                            }
                            query = SQLStringAPI.getInstance().getSQLForSelectWithRange(query, range);
                            ExecuteQueryController.logger.log(Level.FINER, "query constructed with range {1} is {0}", new Object[] { query, range });
                        }
                        hash = this.queryDB(query);
                        ExecuteQueryController.logger.finest("Result of query execution : " + hash);
                        if (hash == null || hash.size() == 0) {
                            message = "Error while executing the query. Please refer the logs for more details";
                        }
                    }
                }
                catch (final Exception exp1) {
                    message = "Error executing query : " + exp1.getMessage();
                    ExecuteQueryController.logger.log(Level.SEVERE, "Exception while executing query", exp1);
                }
            }
        }
        request.setAttribute("message", (Object)message);
        request.setAttribute("task", (Object)task);
        request.setAttribute("query", (Object)query);
        request.setAttribute("hash", (Object)hash);
        request.setAttribute("rowsAffected", (Object)("" + rowsAffected));
    }
    
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
        }
        catch (final Exception e) {
            ExecuteQueryController.logger.log(Level.SEVERE, "Exception in getting connection", e);
        }
        return conn;
    }
    
    private Hashtable queryDB(String qryStr) throws Exception {
        if (qryStr == null) {
            return null;
        }
        if (qryStr.equals("show tables") || qryStr.equals("show status") || qryStr.equals("show variables") || qryStr.equals("show databases") || qryStr.startsWith("select") || qryStr.startsWith("desc") || qryStr.startsWith("\\d") || qryStr.startsWith("\\c")) {
            Connection conn = null;
            ResultSet rs = null;
            Statement stmt = null;
            final Hashtable returnHash = new Hashtable();
            final Vector dataVector = new Vector();
            final Vector headerVector = new Vector();
            final Vector headerTableNameVector = new Vector();
            returnHash.put("header", headerVector);
            returnHash.put("headerTableName", headerTableNameVector);
            returnHash.put("tabular", dataVector);
            try {
                conn = this.getConnection();
                stmt = conn.createStatement();
                qryStr = IAMEncoder.encodeSQL(qryStr);
                rs = stmt.executeQuery(qryStr);
                final ResultSetMetaData rsMetaData = rs.getMetaData();
                final int columnCount = rsMetaData.getColumnCount();
                final boolean[] isPasswordField = new boolean[columnCount];
                for (int i = 1; i <= columnCount; ++i) {
                    final String header = rsMetaData.getColumnLabel(i).trim();
                    headerVector.addElement(header);
                    isPasswordField[i - 1] = (header.equalsIgnoreCase("passwd") || header.equalsIgnoreCase("password") || header.equalsIgnoreCase("enablepassword"));
                    final String headerTableName = rsMetaData.getTableName(i).trim();
                    headerTableNameVector.addElement(headerTableName);
                }
                int rowsReturned = 0;
                while (rs.next()) {
                    if (rowsReturned > this.rowCount) {
                        ExecuteQueryController.logger.log(Level.FINER, "Number of rows exceeds {0}. Please use limit to ensure that no more than {1} rows are returned", new Object[] { new Integer(this.rowCount), new Integer(this.rowCount) });
                    }
                    final Vector tempVect = new Vector();
                    for (int ii = 1; ii <= columnCount; ++ii) {
                        if (isPasswordField[ii - 1]) {
                            tempVect.addElement("********");
                        }
                        else {
                            final String str = rs.getString(ii);
                            tempVect.addElement(str);
                        }
                    }
                    dataVector.addElement(tempVect);
                    ++rowsReturned;
                }
            }
            catch (final Exception ee) {
                returnHash.put("errorMessage", ee.getMessage());
                ExecuteQueryController.logger.log(Level.SEVERE, "Exception while executing query", ee);
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
            return returnHash;
        }
        return null;
    }
    
    public int executeUpdate(String query) throws Exception {
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        query = IAMEncoder.encodeSQL(query);
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            count = stmt.executeUpdate(query);
        }
        catch (final Exception e) {
            ExecuteQueryController.logger.log(Level.FINER, e.getMessage());
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return count;
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    static {
        ExecuteQueryController.logger = Logger.getLogger(ExecuteQueryController.class.getName());
    }
}
