package com.me.devicemanagement.framework.webclient.admin;

import java.util.Arrays;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Locale;
import com.adventnet.db.util.SelectQueryStringUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.client.components.table.web.TableDatasetModel;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import java.util.logging.Level;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.Utils;
import java.io.FileWriter;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.client.components.sql.SQLQueryAPI;
import javax.swing.table.TableModel;
import java.util.logging.Logger;

public class DBQueryExecutorHandler
{
    private static Logger logger;
    
    public static TableModel getTableModel(final String query, final String countSql) throws Exception {
        return (TableModel)SQLQueryAPI.getAsTableModel(query, countSql);
    }
    
    public static TableModel executeUserQuery(final String query) throws Exception {
        TableModel tm = null;
        try {
            SyMLogger.info(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "executeUserQuery", "Modified User Query is : \n" + query);
            tm = getTableModel(query, null);
            if (tm != null && tm.getRowCount() > 0) {
                final String server_home = SyMUtil.getInstallationDir();
                final String directories = "DC/DBQueryExecutor";
                final String filepath = server_home + "/work/" + directories;
                final boolean success = new File(filepath).mkdirs();
                final String filename = server_home + "/work/DC/DBQueryExecutor/QueryResult.csv";
                final File filepj = new File(filename);
                if (!filepj.exists()) {
                    filepj.createNewFile();
                }
                final FileWriter writer = new FileWriter(filepj, false);
                final int len = tm.getColumnCount();
                SyMLogger.info(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "executeUserQuery", "Starting to write resultset into the CSV file");
                for (int i = 0; i < len; ++i) {
                    tm.getColumnName(i);
                    writer.append((CharSequence)(tm.getColumnName(i) + ""));
                    if (i != len - 1) {
                        writer.append((CharSequence)",");
                    }
                    else {
                        writer.append((CharSequence)"\n");
                    }
                }
                for (int i = 0; i < tm.getRowCount(); ++i) {
                    for (int leng = tm.getColumnCount(), j = 0; j < leng; ++j) {
                        final String colName = tm.getColumnName(j);
                        final String strDate = "";
                        if (colName.toUpperCase().endsWith("_DATE_FORMAT")) {
                            if (tm.getValueAt(i, j) != null) {
                                final Long val = (Long)tm.getValueAt(i, j);
                                if (val > 0L) {
                                    writer.append((CharSequence)("\"" + Utils.getEventTime(val) + "\""));
                                }
                            }
                        }
                        else {
                            final String data = tm.getValueAt(i, j) + "";
                            if (data.contains(",")) {
                                writer.append((CharSequence)("\"" + tm.getValueAt(i, j) + "\""));
                            }
                            else {
                                writer.append((CharSequence)(tm.getValueAt(i, j) + ""));
                            }
                        }
                        if (j != leng - 1) {
                            writer.append((CharSequence)",");
                        }
                        else {
                            writer.append((CharSequence)"\n");
                        }
                    }
                }
                writer.flush();
                writer.close();
                SyMLogger.info(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "executeUserQuery", "Successfully  write the date  into the CSV file ");
            }
        }
        catch (final Exception e) {
            String errorMessage = "Unable to getting the result. Please Check the Query. ";
            errorMessage = "Exception is " + e;
            SyMLogger.error(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "executeUserQuery", "Exception occured. while executing query. query is \n  : " + query, e);
            throw e;
        }
        return tm;
    }
    
    public static String excuteUserUpdateQuery(final String query) throws Exception {
        String successMessage = "";
        try {
            SyMLogger.info(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "excuteUserUpdateQuery", "Going to execute query ....  " + query);
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = RelationalAPI.getInstance().getConnection();
                stmt = conn.createStatement();
                stmt.execute(query);
                successMessage = "Sucessfully Executed the query";
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
        catch (final Exception e) {
            final String errorMessage = "Unable to Execute the query. Please Check the Query    " + e;
            SyMLogger.error(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "excuteUserUpdateQuery", "Exception occured. while executing query. query is \n  : " + query, e);
            throw e;
        }
        return successMessage;
    }
    
    public static String exportCSV() {
        String csvString = "";
        try {
            FileInputStream fis = null;
            BufferedInputStream bistream = null;
            DataInputStream dis = null;
            try {
                final String server_home = SyMUtil.getInstallationDir();
                final String filename = server_home + "/work/DC/DBQueryExecutor/QueryResult.csv";
                final File file = new File(filename);
                fis = new FileInputStream(file);
                bistream = new BufferedInputStream(fis);
                dis = new DataInputStream(bistream);
                while (dis.available() != 0) {
                    csvString = csvString + dis.readLine() + "\n";
                }
            }
            catch (final Exception e) {
                SyMLogger.error(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "exportCSV", "Exception occured. while exporting CSV\n  : ", e);
            }
            finally {
                if (fis != null) {
                    fis.close();
                }
                if (bistream != null) {
                    bistream.close();
                }
                if (dis != null) {
                    dis.close();
                }
            }
        }
        catch (final Exception e2) {
            SyMLogger.error(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "exportCSV", "Exception occured. while exporting CSV\n  : ", e2);
        }
        return csvString;
    }
    
    public static String modifyQuery(String query) throws Exception {
        SyMLogger.info(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "modifyQuery", " User Query before modification : \n" + query);
        try {
            if (query != null) {
                String firstPart = "";
                Long dateInLong;
                for (String dateValue = ""; query.indexOf("DATE_TO_LONG") != -1; query = query.replaceFirst("DATE_TO_LONG", ""), query = query.replaceFirst(dateValue, dateInLong.toString())) {
                    firstPart = query.substring(query.indexOf("DATE_TO_LONG"), query.length());
                    dateValue = firstPart.substring(firstPart.indexOf("(", 0) + 1, firstPart.indexOf(")", 0));
                    final DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    final Date date = format1.parse(dateValue.trim());
                    dateInLong = date.getTime();
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DBQueryExecutorHandler.logger, "DBQueryExecutorHandler", "modifyQuery", "Exception occured. while modifying query \n  : ", e);
            throw e;
        }
        return query;
    }
    
    private static long getCount(final String countSql, final Connection con) throws SQLException, QueryConstructionException {
        DataSet ds = null;
        long totalCount = -1L;
        if (countSql != null) {
            try {
                ds = RelationalAPI.getInstance().executeQuery(countSql, con);
                if (ds.next()) {
                    totalCount = (long)ds.getValue(1);
                }
            }
            catch (final Exception e) {
                DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured while executing count query : " + countSql, e);
                throw e;
            }
            finally {
                if (ds != null) {
                    try {
                        ds.close();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        return totalCount;
    }
    
    private static Range getAvailableRangeForNormalCount(final Range range, final long totalCount, final boolean isNoCount) {
        final int pageLen = range.getNumberOfObjects();
        int startIndex = range.getStartIndex();
        if (totalCount == 0L) {
            startIndex = 0;
        }
        if (startIndex > totalCount) {
            final int viewLength = Math.max(isNoCount ? (pageLen - 1) : pageLen, 10);
            int pageNum = (int)totalCount / viewLength;
            if ((int)totalCount % viewLength == 0) {
                --pageNum;
            }
            startIndex = pageNum * viewLength + 1;
        }
        return new Range(startIndex, pageLen);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final String countSql, final ViewContext viewCtx, boolean isCount, Range range, final String sumsql, final boolean isUpdate) throws Exception {
        boolean fetchCountOnly = false;
        final boolean isNoCount = !isCount;
        boolean fetchPrevPage = false;
        String no_of_attempt_to_fetchPrevPage = null;
        if (viewCtx != null) {
            fetchCountOnly = Boolean.TRUE.equals(viewCtx.getTransientState("fetchCountOnly"));
            fetchPrevPage = "true".equalsIgnoreCase(viewCtx.getRequest().getParameter("fetchPrevPage"));
            if (sqlQuery == null && !fetchCountOnly) {
                throw new ClientException(ClientErrorCodes.SQL_QUERY_NULL);
            }
            if (countSql == null && (!isNoCount || fetchCountOnly || fetchPrevPage)) {
                throw new ClientException(ClientErrorCodes.SQL_QUERY_NULL_FETCHCOUNT);
            }
            no_of_attempt_to_fetchPrevPage = viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage");
        }
        final TableDatasetModel tdm = new TableDatasetModel();
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        long totalCount = -1L;
        Connection con = null;
        try {
            if (isUpdate) {
                con = relationalAPI.getConnection();
            }
            else {
                con = DBUtil.getConnection("READ_ONLY");
            }
            final Map templateValues = new HashMap();
            relationalAPI.getDBAdapter().getSQLGenerator().fillUserDataRange(templateValues);
            String updatedSqlWithRange = null;
            String templateReplacedSQL = null;
            if (sqlQuery != null) {
                templateReplacedSQL = SelectQueryStringUtil.replaceAllTemplatesForSQL(sqlQuery, templateValues);
            }
            final int no_of_times_to_fetch_prevPage = (no_of_attempt_to_fetchPrevPage != null) ? Integer.parseInt(no_of_attempt_to_fetchPrevPage) : 2;
            int iteration_count = 0;
            while (iteration_count <= no_of_times_to_fetch_prevPage) {
                if (isCount && countSql != null) {
                    totalCount = getCount(SelectQueryStringUtil.replaceAllTemplatesForSQL(countSql, templateValues), con);
                    tdm.setTotalRecordsCount(totalCount);
                }
                if (fetchCountOnly) {
                    return tdm;
                }
                final HashMap totalSumMap = tdm.getTotalSumMap();
                updateTotalSumMap(totalSumMap, templateValues, con, viewCtx, sumsql);
                int startIndex = 0;
                int viewLength = 0;
                if (range != null) {
                    if (totalCount != -1L) {
                        range = getAvailableRangeForNormalCount(range, totalCount, isNoCount);
                    }
                    startIndex = range.getStartIndex();
                    viewLength = range.getNumberOfObjects();
                }
                if (templateReplacedSQL.toLowerCase(Locale.ENGLISH).contains("union")) {
                    updatedSqlWithRange = RelationalAPI.getInstance().getDBAdapter().getSQLModifier().getSQLForUnionWithRange(templateReplacedSQL, range);
                }
                else {
                    updatedSqlWithRange = RelationalAPI.getInstance().getDBAdapter().getSQLModifier().getSQLForSelectWithRange(templateReplacedSQL, range);
                }
                tdm.setStartIndex((long)startIndex);
                tdm.setPageLength((long)viewLength);
                DataSet ds = null;
                try {
                    ds = relationalAPI.executeQuery(updatedSqlWithRange, con);
                    tdm.updateModel(viewCtx, ds);
                }
                finally {
                    if (ds != null) {
                        try {
                            ds.close();
                        }
                        catch (final Exception ex) {}
                    }
                }
                if (isNoCount && viewLength > 0 && tdm.getFetchedRecordsCount() == viewLength) {
                    tdm.setEndIndex(tdm.getEndIndex() - 1L);
                }
                if (!isNoCount || !fetchPrevPage || tdm.getFetchedRecordsCount() != 0L || startIndex <= 1) {
                    break;
                }
                startIndex = Math.max(startIndex - viewLength + 1, 1);
                range = new Range(startIndex, viewLength);
                if (++iteration_count != no_of_times_to_fetch_prevPage) {
                    continue;
                }
                isCount = true;
            }
        }
        catch (final Exception e) {
            DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", e);
            throw e;
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception ex2) {}
            }
        }
        if (sumsql != null) {
            setViewSumMapInModel(tdm, viewCtx);
        }
        return tdm;
    }
    
    private static void updateTotalSumMap(final HashMap totalSumMap, final Map templateValues, final Connection con, final ViewContext viewCtx, final String sumsql) throws Exception {
        ResultSet rs = null;
        Statement st = null;
        try {
            if (sumsql != null) {
                final String sql = SelectQueryStringUtil.replaceAllTemplatesForSQL(sumsql, templateValues);
                st = con.createStatement();
                rs = st.executeQuery(sql);
                BigDecimal sum = BigDecimal.ZERO;
                if (rs.next()) {
                    final String[] sumcols = viewCtx.getModel().getFeatureValue("SUMCOLS").toString().split(",");
                    for (int noOfSumCols = sumcols.length, i = 1; i <= noOfSumCols; ++i) {
                        sum = rs.getBigDecimal(i);
                        totalSumMap.put(sumcols[i - 1], sum);
                    }
                }
            }
        }
        catch (final Exception ex) {
            DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", ex);
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex) {
                    DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", ex);
                }
            }
            if (st != null) {
                try {
                    st.close();
                }
                catch (final Exception ex) {
                    DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", ex);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex2) {
                    DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", ex2);
                }
            }
            if (st != null) {
                try {
                    st.close();
                }
                catch (final Exception ex2) {
                    DBQueryExecutorHandler.logger.log(Level.WARNING, "Exception occuured ", ex2);
                }
            }
        }
    }
    
    private static void setViewSumMapInModel(final TableDatasetModel tdm, final ViewContext vc) throws Exception {
        final HashMap map = tdm.getViewSumMap();
        if (vc.getModel().getFeatureValue("VIEWSUMCOLS") != null) {
            final String[] viewSumCols = vc.getModel().getFeatureValue("VIEWSUMCOLS").toString().split(",");
            final int rowcount = tdm.getRowCount();
            final int colcount = tdm.getColumnCount();
            if (rowcount <= 0 || colcount < viewSumCols.length) {
                DBQueryExecutorHandler.logger.log(Level.FINER, "row count is {0}, column count is {1} viewsumcols is {2} hence returning", new Object[] { rowcount, colcount, Arrays.asList(viewSumCols) });
                return;
            }
            for (int i = 0; i < viewSumCols.length; ++i) {
                final String colAlias = viewSumCols[i];
                final int colIndex = tdm.getColumnIndex(colAlias);
                if (colIndex == -1) {
                    throw new Exception("unknown column " + colAlias + " given");
                }
                BigDecimal sum = BigDecimal.ZERO;
                for (int j = 0; j < rowcount; ++j) {
                    final Object val = tdm.getValueAt(j, colIndex);
                    if (val != null && !val.equals("")) {
                        if (val instanceof BigDecimal) {
                            sum.add((BigDecimal)val);
                        }
                        else {
                            final Double el = Double.parseDouble(val.toString());
                            final BigDecimal bd = new BigDecimal(el.toString());
                            sum = sum.add(bd);
                        }
                    }
                }
                map.put(colAlias, sum);
            }
        }
    }
    
    static {
        DBQueryExecutorHandler.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
