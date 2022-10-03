package com.me.devicemanagement.framework.server.util;

import com.me.ems.framework.server.tabcomponents.core.TabComponentUtil;
import com.me.devicemanagement.framework.server.authentication.DCUserConstants;
import com.adventnet.client.view.web.WebViewAPI;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownServiceException;
import java.net.UnknownHostException;
import java.net.ProtocolException;
import java.net.PortUnreachableException;
import java.net.NoRouteToHostException;
import java.net.MalformedURLException;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.adventnet.ds.query.GroupByClause;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.client.cache.web.ClientDataObjectCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ocpsoft.pretty.time.PrettyTime;
import java.util.Locale;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.TimeZone;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import com.adventnet.i18n.I18N;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.net.URL;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewException;
import javax.naming.NamingException;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.mailmanager.MailManager;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import java.util.Date;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import javax.transaction.TransactionManager;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Persistence;
import java.util.Properties;
import java.util.logging.Logger;

public class SyMUtil
{
    protected static Logger logger;
    private static final String STANDALONE = "Stand_alone";
    private static final String PLUGIN = "Plugin";
    private static Boolean isTestMode;
    private static SyMUtil devMgmt;
    private static final Integer IS_TIME_IN_ACCESS;
    private static final String DEFALUT_TIME_FORMAT = "MMM d, yyyy hh:mm a";
    private static final String DEFALUT_DATE_FORMAT = "MMM d, yyyy";
    private static final String PRODUCTID = "productID";
    private static final String SYSTEM_PARAMS_CACHE_NAME = "SYSTEM_PARAMS_CACHE";
    protected static final String DC_SERVER_INFO_CACHE_NAME_DO = "DC_SERVER_INFO_CACHE";
    protected static final String FOS_SERVER_INFO_CACHE_NAME_DO = "FOS_SERVER_INFO_CACHE";
    public static final String VALID_DID_REGEX_PATTERN = "^[0-9]+(-[0-9]+)*(:[a-z]+)?$";
    private static Properties copyrightProps;
    
    public static SyMUtil getInstance() {
        if (SyMUtil.devMgmt == null) {
            SyMUtil.devMgmt = new SyMUtil();
        }
        return SyMUtil.devMgmt;
    }
    
    public static Persistence getPersistence() {
        return ApiFactoryProvider.getPersistenceAPI().getPersistence();
    }
    
    public static Persistence getPersistenceLite() {
        return ApiFactoryProvider.getPersistenceAPI().getPersistenceLite();
    }
    
    public static ReadOnlyPersistence getCachedPersistence() {
        return ApiFactoryProvider.getPersistenceAPI().getCachedPersistence();
    }
    
    public static ReadOnlyPersistence getReadOnlyPersistence() {
        return ApiFactoryProvider.getPersistenceAPI().getReadOnlyPersistence();
    }
    
    public static Connection getConnection() throws SQLException {
        return ApiFactoryProvider.getPersistenceAPI().getConnection();
    }
    
    public static Connection getReadOnlyConnection() throws Exception {
        return ApiFactoryProvider.getPersistenceAPI().getReadOnlyConnection();
    }
    
    public static TransactionManager getUserTransaction() {
        try {
            return DataAccess.getTransactionManager();
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getting TransactionManager. ", ex);
            return null;
        }
    }
    
    public static String getSyMParameter(final String paramKey) {
        String paramValue = (String)ApiFactoryProvider.getCacheAccessAPI().getFromHashMap("SYSTEM_PARAMS_CACHE", paramKey, 2);
        if (paramValue == null || paramValue.trim().length() <= 0) {
            paramValue = getSyMParameterFromDB(paramKey);
            ApiFactoryProvider.getCacheAccessAPI().putIntoHashMap("SYSTEM_PARAMS_CACHE", paramKey, paramValue, 2);
        }
        return paramValue;
    }
    
    public static String getSyMParameterFromDB(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SystemParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final DataObject systemParamsDO = DataAccess.get("SystemParams", criteria);
            final Row systemParamRow = systemParamsDO.getRow("SystemParams");
            if (systemParamRow == null) {
                return null;
            }
            final String paramValue = (String)systemParamRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving SyM Parameter:" + paramKey + " from DB.", ex);
            return null;
        }
    }
    
    public static void updateSyMParameter(final String paramName, final String paramValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SystemParams", "PARAM_NAME"), (Object)paramName, 0, false);
            final DataObject systemParamsDO = DataAccess.get("SystemParams", criteria);
            Row systemParamRow = systemParamsDO.getRow("SystemParams");
            if (systemParamRow == null) {
                systemParamRow = new Row("SystemParams");
                systemParamRow.set("PARAM_NAME", (Object)paramName);
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.addRow(systemParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                systemParamRow.set("PARAM_VALUE", (Object)paramValue);
                systemParamsDO.updateRow(systemParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            getPersistence().update(systemParamsDO);
            addOrUpdateSystemParamsCache(paramName, paramValue);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static void deleteSyMParameter(final String paramKey) {
        try {
            deleteSystemParamFromCache(paramKey);
            final Criteria criteria = new Criteria(Column.getColumn("SystemParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting SyM Parameter:" + paramKey + " from DB.", ex);
        }
    }
    
    public static boolean isSummaryServer() {
        return ApiFactoryProvider.getUtilAccessAPI().isSummaryServer();
    }
    
    public static boolean isProbeServer() {
        return ApiFactoryProvider.getUtilAccessAPI().isProbeServer();
    }
    
    public static boolean isStandaloneServer() {
        return !ApiFactoryProvider.getUtilAccessAPI().isSummaryServer() && !ApiFactoryProvider.getUtilAccessAPI().isProbeServer();
    }
    
    public void sendMail(final Properties mailContentProps) throws DataAccessException {
        final String mailAddress = ((Hashtable<K, String>)mailContentProps).get("EMAIL");
        if (mailAddress == null || mailAddress.trim().equals("")) {
            SyMUtil.logger.log(Level.INFO, "Mail Address is not configured for the task " + ((Hashtable<K, Object>)mailContentProps).get("TASKNAME"));
            return;
        }
        try {
            final String userName = mailAddress.substring(0, mailAddress.indexOf("@"));
            ((Hashtable<String, String>)mailContentProps).put("userName", userName);
            ((Hashtable<String, String>)mailContentProps).put("serverName", ApiFactoryProvider.getUtilAccessAPI().getServerName());
            if (mailContentProps.get("STARTTIME") != null) {
                ((Hashtable<String, String>)mailContentProps).put("STARTTIME", new Date(((Hashtable<K, Long>)mailContentProps).get("STARTTIME")).toString());
            }
            if (mailContentProps.get("COMPLETIONTIME") != null) {
                ((Hashtable<String, String>)mailContentProps).put("COMPLETIONTIME", new Date(((Hashtable<K, Long>)mailContentProps).get("COMPLETIONTIME")).toString());
            }
            final String sender = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails().get("mail.fromAddress");
            final MailDetails mailDetails = new MailDetails(null, null);
            mailDetails.fromAddress = sender;
            mailDetails.toAddress = mailContentProps.getProperty("EMAIL");
            mailDetails.subject = mailContentProps.getProperty("SUBJECT");
            mailDetails.bodyContent = MailManager.getInstance().getHtmlMailContent(mailContentProps);
            ApiFactoryProvider.getMailSettingAPI().sendMail(mailDetails);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Error occured while sending an email : ", ex);
        }
    }
    
    private static void addOrUpdateSystemParamsCache(final String paramKey, final String paramValue) {
        try {
            ApiFactoryProvider.getCacheAccessAPI().putIntoHashMap("SYSTEM_PARAMS_CACHE", paramKey, paramValue, 2);
        }
        catch (final Exception e) {
            ApiFactoryProvider.getCacheAccessAPI().removeMapKeyFromHashMap("SYSTEM_PARAMS_CACHE", paramKey, 2);
        }
    }
    
    private static void deleteSystemParamFromCache(final String paramKey) {
        ApiFactoryProvider.getCacheAccessAPI().removeMapKeyFromHashMap("SYSTEM_PARAMS_CACHE", paramKey, 2);
    }
    
    public static DataObject getErrorMessage(final int errCode) {
        DataObject errMesDO = null;
        try {
            final Table baseTable = Table.getTable("ErrorCode");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join("ErrorCode", "ErrorCause", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 1));
            query.addJoin(new Join("ErrorCode", "ErrorSolution", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 1));
            query.addJoin(new Join("ErrorCode", "ErrorReference", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 1));
            final Column col = Column.getColumn("ErrorCode", "ERROR_CODE");
            final Criteria criteria = new Criteria(col, (Object)new Integer(errCode), 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            errMesDO = getPersistence().get(query);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while retrieving error message for error code: " + errCode, ex);
        }
        return errMesDO;
    }
    
    public static long getCurrentTime() {
        final Date dt = new Date();
        final long timeval = dt.getTime();
        return timeval;
    }
    
    public static String getCurrentTimeWithDate() {
        getInstance();
        return Utils.getEventTime(getCurrentTimeInMillis());
    }
    
    public static String getDate(final long dateVal) {
        return Utils.getEventTime(dateVal);
    }
    
    public static long getDateDiff(final long startTimeInMS, final long endTimeInMS) {
        return (endTimeInMS - startTimeInMS) / 86400000L;
    }
    
    public static DataObject getData(final String tableName, final String criteriaColumn, final Object criteriaValue, final String sortColumn, final boolean sortIsAsc) throws SyMException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            query.addSelectColumn(Column.getColumn(tableName, "*"));
            if (criteriaColumn != null) {
                final Column col = Column.getColumn(tableName, criteriaColumn);
                final Criteria criteria = new Criteria(col, criteriaValue, 0, false);
                query.setCriteria(criteria);
            }
            if (sortColumn != null) {
                final Column sCol = Column.getColumn(tableName, sortColumn);
                final SortColumn sortCol = new SortColumn(sCol, sortIsAsc);
                query.addSortColumn(sortCol);
            }
            final DataObject resultDO = getPersistence().get(query);
            return resultDO;
        }
        catch (final DataAccessException ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public static Row getRowFromDO(final DataObject inputDO, final String tableName, final String matchColumn, final Object matchValue) throws SyMException {
        try {
            final Column col = Column.getColumn(tableName, matchColumn);
            final Criteria criteria = new Criteria(col, matchValue, 0, false);
            final Row resultRow = inputDO.getRow(tableName, criteria);
            return resultRow;
        }
        catch (final Exception ex) {
            throw new SyMException(1001, ex);
        }
    }
    
    public static Properties getRowAsProp(final Row row) throws SyMException {
        final Properties rowProp = new Properties();
        try {
            final List columnList = row.getColumns();
            for (int j = 0; j < columnList.size(); ++j) {
                final String columnName = columnList.get(j);
                final Object obj = row.get(columnName);
                if (row.get(columnName) != null) {
                    ((Hashtable<String, Object>)rowProp).put(columnName, obj);
                }
            }
            return rowProp;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while converting row to properties ...", ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public static String getInstallationDir() throws Exception {
        String path = null;
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            path = ApiFactoryProvider.getFileAccessAPI().getCanonicalPath(serverHome);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting Installation Directory. ", ex);
            throw ex;
        }
        return path;
    }
    
    public static String getInstallationDirName() throws Exception {
        String name = null;
        try {
            name = ApiFactoryProvider.getFileAccessAPI().getFileName(getInstallationDir());
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting Installation Directory. ", ex);
            throw ex;
        }
        return name;
    }
    
    public static String getInstallationProperty(final String key) {
        String value = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "install.conf";
            final Properties props = FileAccessUtil.readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product property: " + key, ex);
        }
        return value;
    }
    
    public static String getInstallationPropertyForDBUpdate(final String key) {
        String value = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "install.conf";
            final Properties props = FileAccessUtil.readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product property: " + key, ex);
        }
        return value;
    }
    
    public static String getProductProperty(final String key) {
        String value = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "product.conf";
            SyMUtil.logger.log(Level.FINE, "***********getProductProperty***********fname: " + fname);
            final Properties props = FileAccessUtil.readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product property: " + key, ex);
        }
        return value;
    }
    
    public static Properties getProductSettingsProperties() {
        Properties props = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "productSettings.conf";
            SyMUtil.logger.log(Level.FINE, "***********getProductSettingsProperties *********** fname: " + fname);
            props = FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product properties.", ex);
        }
        return props;
    }
    
    public static Properties getProductProperties() {
        Properties props = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "product.conf";
            SyMUtil.logger.log(Level.FINE, "***********getProductProperties *********** fname: " + fname);
            props = FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product properties.", ex);
        }
        return props;
    }
    
    public static TableNavigatorModel getTableNavigatorModel(final SelectQuery selectQuery) throws SyMException {
        try {
            if (selectQuery.getSortColumns().isEmpty()) {
                final List selectColumnsList = selectQuery.getSelectColumns();
                if (selectColumnsList.size() > 0) {
                    final SortColumn sort = new SortColumn((Column)selectColumnsList.get(0), true);
                    selectQuery.addSortColumn(sort);
                }
            }
            final CustomViewManager manager = (CustomViewManager)BeanUtil.lookup("TableViewManager");
            final CustomViewRequest cvReq = new CustomViewRequest(selectQuery);
            final ViewData vData = manager.getData(cvReq);
            final TableNavigatorModel tableModel = (TableNavigatorModel)vData.getModel();
            return tableModel;
        }
        catch (final NamingException exp) {
            SyMUtil.logger.log(Level.WARNING, "NamingException while getting table model...", exp);
            throw new SyMException(1001, exp);
        }
        catch (final CustomViewException exp2) {
            SyMUtil.logger.log(Level.WARNING, "CustomViewException while getting table model...", (Throwable)exp2);
            throw new SyMException(1001, (Throwable)exp2);
        }
        catch (final Exception exp3) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting table model...", exp3);
            throw new SyMException(1001, exp3);
        }
    }
    
    public static SortColumn getSortColumn(final SelectQuery selectQuery, final String columnName, final String ascending) {
        SortColumn sortCol = null;
        final List colList = selectQuery.getSelectColumns();
        final int size = colList.size();
        int i = 0;
        while (i < size) {
            Column column = colList.get(i);
            String sortColName = column.getColumnName();
            if (sortColName == null) {
                column = column.getColumn();
                sortColName = column.getColumnName();
            }
            final String sortColAlias = column.getColumnAlias();
            if (sortColName.equalsIgnoreCase(columnName) || (sortColAlias != null && sortColAlias.equalsIgnoreCase(columnName))) {
                if (ascending.equals("true")) {
                    sortCol = new SortColumn(column, true);
                    break;
                }
                sortCol = new SortColumn(column, false);
                break;
            }
            else {
                ++i;
            }
        }
        return sortCol;
    }
    
    public static void updateUserParamForAllUsers(final String paramName, final String paramValue) throws SyMException {
        try {
            final List<Long> allUsers = getUsers();
            final Criteria criteria = new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)paramName, 0, false);
            final DataObject userParamsDO = DataAccess.get("UserParams", criteria);
            for (final Long user : allUsers) {
                final Criteria userCriteria = new Criteria(Column.getColumn("UserParams", "USER_ACCOUNT_ID"), (Object)user, 0);
                Row userParamRow = userParamsDO.getRow("UserParams", userCriteria);
                if (userParamRow == null) {
                    userParamRow = new Row("UserParams");
                    userParamRow.set("PARAM_NAME", (Object)paramName);
                    userParamRow.set("USER_ACCOUNT_ID", (Object)user);
                    userParamRow.set("PARAM_VALUE", (Object)paramValue);
                    userParamsDO.addRow(userParamRow);
                    SyMUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
                }
                else {
                    userParamRow.set("PARAM_VALUE", (Object)paramValue);
                    userParamsDO.updateRow(userParamRow);
                    SyMUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
                }
            }
            getPersistence().update(userParamsDO);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating User Parameter", paramName);
        }
    }
    
    public static String getUserParameter(final Long userId, final String paramKey) throws SyMException {
        try {
            if (userId == null || paramKey == null) {
                throw new SyMException(1002, "Given input is null", null);
            }
            final Column userIdCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
            Criteria criteria = new Criteria(userIdCol, (Object)userId, 0);
            final Column paramNameCol = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria paramCri = new Criteria(paramNameCol, (Object)paramKey, 0);
            criteria = criteria.and(paramCri);
            final DataObject resultDO = getPersistence().get("UserParams", criteria);
            if (resultDO.isEmpty()) {
                return null;
            }
            final Row resultRow = resultDO.getRow("UserParams");
            final String paramValue = (String)resultRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving User Parameter: " + paramKey + " for userId: " + userId + " from DB.", ex);
            throw new SyMException(1002, ex);
        }
    }
    
    public static List getUsers() throws SyMException {
        final List userList = new ArrayList();
        final String baseTblName = "UserParams";
        final Table baseTable = Table.getTable(baseTblName);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        Column selCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
        selCol = selCol.distinct();
        query.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    userList.add(value);
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while retrieving target resources version: ", (Throwable)ex);
            throw new SyMException(1001, "Exception occured while getting SOM Version", (Throwable)ex);
        }
        catch (final SQLException ex2) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while retrieving target resource version : ", ex2);
            throw new SyMException(1001, "Exception occured while getting SOM Version", ex2);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException ex3) {
                throw new SyMException(1001, "Exception occured while getting SOM Status count", ex3);
            }
        }
        return userList;
    }
    
    public static void updateUserParameter(final Long userId, final String paramName, final String paramValue) throws SyMException {
        try {
            if (userId == null || paramName == null || paramValue == null) {
                throw new SyMException(1002, "Any of given input is null", null);
            }
            final Column userIdCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
            Criteria criteria = new Criteria(userIdCol, (Object)userId, 0);
            final Column paramNameCol = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria paramCri = new Criteria(paramNameCol, (Object)paramName, 0);
            criteria = criteria.and(paramCri);
            final DataObject resultDO = getPersistence().get("UserParams", criteria);
            if (resultDO.isEmpty()) {
                final Row userParamRow = new Row("UserParams");
                userParamRow.set("USER_ACCOUNT_ID", (Object)userId);
                userParamRow.set("PARAM_NAME", (Object)paramName);
                userParamRow.set("PARAM_VALUE", (Object)paramValue);
                resultDO.addRow(userParamRow);
                getPersistence().add(resultDO);
                SyMUtil.logger.log(Level.FINEST, "User Parameter added in DB:- user id: " + userId + " param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                final Row userParamRow = resultDO.getRow("UserParams");
                userParamRow.set("PARAM_VALUE", (Object)paramValue);
                resultDO.updateRow(userParamRow);
                getPersistence().update(resultDO);
                SyMUtil.logger.log(Level.FINEST, "Parameter updated in DB:- user id: " + userId + " param name: " + paramName + " param value: " + paramValue);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating User Parameter in DB:- user id: " + userId + " param name: " + paramName + " param value: " + paramValue, ex);
            throw new SyMException(1002, ex);
        }
    }
    
    public static void deleteUserParameter(final String paramKey) {
        try {
            deleteSystemParamFromCache(paramKey);
            final Criteria criteria = new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting User Parameter:" + paramKey + " from DB.", ex);
        }
    }
    
    public static void deleteUserParameter(final Long userID, final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final Criteria userCriteria = new Criteria(Column.getColumn("UserParams", "USER_ACCOUNT_ID"), (Object)userID, 0, false);
            getPersistence().delete(criteria.and(userCriteria));
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting User Parameter:" + paramKey + " from DB.", ex);
        }
    }
    
    public static void deleteUserParameters(final Long userID, final Object[] paramKeys) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)paramKeys, 8, false);
            final Criteria userCriteria = new Criteria(Column.getColumn("UserParams", "USER_ACCOUNT_ID"), (Object)userID, 0, false);
            getPersistence().delete(criteria.and(userCriteria));
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting User Parameter:" + Arrays.toString(paramKeys) + " from DB.", ex);
        }
    }
    
    public static Map<String, Integer> getUserParamStartsWith(final String paramKey) {
        final String sourceMethod = "getUserParamStartsWith";
        final Map<String, Integer> userParamCountMap = new HashMap<String, Integer>();
        try {
            final Column col = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramKey, 10, false);
            final DataObject dobj = getPersistenceLite().get("UserParams", criteria);
            final Iterator<Row> iterator = dobj.getRows("UserParams");
            if (dobj.isEmpty()) {
                return userParamCountMap;
            }
            iterator.forEachRemaining(userParamRow -> {
                final Integer n = map.merge(String.valueOf(userParamRow.get("PARAM_NAME")), 1, Integer::sum);
                return;
            });
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while fetching starts with User Parameter:" + paramKey + " from DB.", ex);
        }
        return userParamCountMap;
    }
    
    public static DataObject convertXMLToDO(final byte[] data) {
        final DataObject rDo = ApiFactoryProvider.getSecureXml2DoConverterAPI().convertXMLToDO(data);
        return rDo;
    }
    
    public static DataObject convertXMLToDO(final URL url) {
        final DataObject rDo = ApiFactoryProvider.getSecureXml2DoConverterAPI().convertXMLToDO(url);
        return rDo;
    }
    
    public static DataObject convertXMLToDO(final String xmlFileName) {
        final DataObject rDo = ApiFactoryProvider.getSecureXml2DoConverterAPI().convertXMLToDO(xmlFileName);
        return rDo;
    }
    
    public static Hashtable getServerIPs() {
        Hashtable ipHash = null;
        Row row = null;
        try {
            final DataObject serverDO = getDCServerInfoDO();
            ipHash = new Hashtable();
            if (!serverDO.isEmpty()) {
                row = serverDO.getRow("DCServerInfo");
                ipHash = new Hashtable();
                ipHash.put("primaryIP", row.get("SERVER_MAC_IPADDR"));
                String serverSecIP = (String)row.get("SERVER_SEC_IPADDR");
                if (serverSecIP == null || serverSecIP.equalsIgnoreCase("--")) {
                    serverSecIP = (String)row.get("SERVER_MAC_IPADDR");
                }
                ipHash.put("secondaryIP", serverSecIP);
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception while getting server ips...", e);
        }
        return ipHash;
    }
    
    public static String getSecondaryIP() {
        try {
            final DataObject dataObject = getDCServerInfoDO();
            if (!dataObject.isEmpty()) {
                final Row r = dataObject.getRow("DCServerInfo");
                final String existingIP = (String)r.get("SERVER_SEC_IPADDR");
                return existingIP;
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while checking for secondary ip change  ", e);
        }
        return null;
    }
    
    public static Properties getDCServerInfo() throws SyMException {
        final Properties dcServerInfoProp = new Properties();
        try {
            final DataObject dcserverInfoDO = getDCServerInfoDO();
            if (dcserverInfoDO != null && !dcserverInfoDO.isEmpty()) {
                final Row serverInfoRow = dcserverInfoDO.getFirstRow("DCServerInfo");
                final List columnList = serverInfoRow.getColumns();
                for (int no_of_columns = columnList.size(), i = 0; i < no_of_columns; ++i) {
                    ((Hashtable<String, Object>)dcServerInfoProp).put(columnList.get(i).toString().toUpperCase(), serverInfoRow.get((String)columnList.get(i)));
                }
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while getting DCServerInfo details. ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return dcServerInfoProp;
    }
    
    public static DataObject getDCServerInfoDO() throws SyMException {
        DataObject dcserverInfoDO = null;
        Boolean updateCache = Boolean.FALSE;
        SyMUtil.logger.log(Level.FINE, "GET DC SERVER INFO PROPS");
        try {
            dcserverInfoDO = getDCServerInfoDOFromCache();
            if (dcserverInfoDO == null || dcserverInfoDO.isEmpty()) {
                dcserverInfoDO = getDCServerInfoFromDB();
                updateCache = Boolean.TRUE;
            }
            if (dcserverInfoDO != null && !dcserverInfoDO.isEmpty() && updateCache) {
                SyMUtil.logger.log(Level.INFO, "UPDATE CACHE AS IT WAS EMPTY");
                addOrUpdateDCServerInfoCache(dcserverInfoDO);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while getting DCServerInfo DO. ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return dcserverInfoDO;
    }
    
    public static DataObject getFosServerInfoDO() throws SyMException {
        DataObject fosServerInfoDO = null;
        Boolean updateCache = Boolean.FALSE;
        SyMUtil.logger.log(Level.FINE, "GET FOS SERVER INFO PROPS");
        try {
            fosServerInfoDO = getFOSServerInfoDOFromCache();
            if (fosServerInfoDO == null || fosServerInfoDO.isEmpty()) {
                fosServerInfoDO = getFOSServerInfoFromDB();
                updateCache = Boolean.TRUE;
            }
            if (fosServerInfoDO != null && !fosServerInfoDO.isEmpty() && updateCache) {
                SyMUtil.logger.log(Level.INFO, "UPDATING CACHE AS IT WAS EMPTY");
                addOrUpdateFOSServerInfoCache(fosServerInfoDO);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Caught exception while getting FOSServerInfo DO. ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return fosServerInfoDO;
    }
    
    public static DataObject getFOSServerInfoDOFromCache() throws SyMException {
        DataObject fosServerInfoDO = null;
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("FOS_SERVER_INFO_CACHE");
        if (cacheObject != null) {
            fosServerInfoDO = (DataObject)cacheObject;
            SyMUtil.logger.log(Level.FINE, "Load fosServerInfoPropsFromCACHE" + fosServerInfoDO.toString());
        }
        return fosServerInfoDO;
    }
    
    public static DataObject getDCServerInfoDOFromCache() throws SyMException {
        DataObject dcServerInfoDO = null;
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("DC_SERVER_INFO_CACHE", 2);
        if (cacheObject != null) {
            dcServerInfoDO = (DataObject)cacheObject;
            SyMUtil.logger.log(Level.FINE, "Load dcServerInfoPropsFromCACHE" + dcServerInfoDO.toString());
        }
        return dcServerInfoDO;
    }
    
    public static DataObject getDCServerInfoFromDB() throws SyMException {
        DataObject dcServerInfoDO = null;
        try {
            SyMUtil.logger.log(Level.FINE, "Load dcServerInfoPropsFromDB");
            dcServerInfoDO = getPersistence().get("DCServerInfo", (Criteria)null);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving DCServer Info  from DB.", ex);
        }
        return dcServerInfoDO;
    }
    
    public static DataObject getFOSServerInfoFromDB() throws SyMException {
        DataObject fosServerInfoDO = null;
        try {
            SyMUtil.logger.log(Level.FINE, "Load fosServerInfoPropsFromDB");
            fosServerInfoDO = getPersistence().get("FOSServerDetails", (Criteria)null);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving FOSServer Info  from DB.", ex);
        }
        return fosServerInfoDO;
    }
    
    public static void addOrUpdateDCServerInfoCache(final DataObject serverInfoDO) {
        try {
            ApiFactoryProvider.getCacheAccessAPI().putCache("DC_SERVER_INFO_CACHE", serverInfoDO, 2);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while updating Cache DO", ex);
        }
    }
    
    public static void addOrUpdateFOSServerInfoCache(final DataObject fosServerInfoDO) {
        try {
            ApiFactoryProvider.getCacheAccessAPI().putCache("FOS_SERVER_INFO_CACHE", fosServerInfoDO);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception while updating FOS Cache DO", ex);
        }
    }
    
    protected static Row constructDCServerInfoRow(final Row row, final Properties props) throws Exception {
        final String serverName = ((Hashtable<K, String>)props).get("SERVER_MAC_NAME");
        if (serverName != null) {
            row.set("SERVER_MAC_NAME", (Object)serverName);
        }
        final Integer portNumber = ((Hashtable<K, Integer>)props).get("SERVER_PORT");
        if (portNumber != null) {
            row.set("SERVER_PORT", (Object)portNumber);
        }
        final String serverFQDN = ((Hashtable<K, String>)props).get("SERVER_FQDN");
        if (serverFQDN != null) {
            row.set("SERVER_FQDN", (Object)serverFQDN);
        }
        final String hostAddress = ((Hashtable<K, String>)props).get("SERVER_IPADDR");
        if (hostAddress != null) {
            row.set("SERVER_MAC_IPADDR", (Object)hostAddress);
        }
        final String agentVersion = ((Hashtable<K, String>)props).get("AGENT_VERSION");
        if (agentVersion != null) {
            row.set("AGENT_VERSION", (Object)agentVersion);
        }
        final String macagentVersion = ((Hashtable<K, String>)props).get("MAC_AGENT_VERSION");
        if (agentVersion != null) {
            row.set("MAC_AGENT_VERSION", (Object)macagentVersion);
        }
        final String linuxAgentVersion = ((Hashtable<K, String>)props).get("LINUX_AGENT_VERSION");
        if (linuxAgentVersion != null) {
            row.set("LINUX_AGENT_VERSION", (Object)linuxAgentVersion);
        }
        final String secIP = ((Hashtable<K, String>)props).get("SERVER_SEC_IPADDR");
        if (secIP != null) {
            row.set("SERVER_SEC_IPADDR", (Object)secIP);
        }
        final Integer httpsPort = ((Hashtable<K, Integer>)props).get("HTTPS_PORT");
        if (httpsPort != null) {
            row.set("HTTPS_PORT", (Object)httpsPort);
        }
        final Long serverInstanceId = ((Hashtable<K, Long>)props).get("SERVER_INSTANCE_ID");
        if (serverInstanceId != null) {
            row.set("SERVER_INSTANCE_ID", (Object)serverInstanceId);
        }
        final String osName = ((Hashtable<K, String>)props).get("OS_NAME");
        if (osName != null) {
            row.set("OS_NAME", (Object)osName);
        }
        final Long server_hash_id = ((Hashtable<K, Long>)props).get("SERVER_HASH_ID");
        if (server_hash_id != null) {
            row.set("SERVER_HASH_ID", (Object)server_hash_id);
        }
        return row;
    }
    
    public static Properties getCopyrightProps() {
        if (SyMUtil.copyrightProps == null) {
            try {
                final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "copyright.conf";
                SyMUtil.logger.log(Level.FINEST, "***********getCopyrightProps***********fname: " + fname);
                SyMUtil.copyrightProps = FileAccessUtil.readProperties(fname);
                SyMUtil.logger.log(Level.FINEST, "***********Copyright Properties*************props: " + SyMUtil.copyrightProps);
            }
            catch (final Exception ex) {
                SyMUtil.logger.log(Level.WARNING, "Caught exception while getting copyright props from copyright.conf...", ex);
            }
        }
        return SyMUtil.copyrightProps;
    }
    
    public static DataObject getEmailAddDO(final String module) throws Exception {
        Column col = Column.getColumn("EMailAddr", "MODULE");
        Criteria crit = new Criteria(col, (Object)module, 0, false);
        col = Column.getColumn("EMailAddr", "SEND_MAIL");
        crit = crit.and(new Criteria(col, (Object)Boolean.TRUE, 0));
        final DataObject mailDObj = getPersistence().get("EMailAddr", crit);
        return mailDObj;
    }
    
    public static synchronized void addOrUpdateEmailAddr(final String module, final boolean emailStatus, final String emailAddress) throws Exception {
        final String sourceMethod = "addOrUpdateEmailAddr";
        try {
            final DataObject mailDObj = getEmailAddDO(module);
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.updateRow(row);
            }
            else {
                final Row row = new Row("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("MODULE", (Object)module);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.addRow(row);
            }
            getPersistence().update(mailDObj);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occured : \n", ex);
        }
    }
    
    public static String getEMailAddress(final String module) throws Exception {
        final String sourceMethod = "getEMailAddress";
        String strEMailAddr = null;
        final DataObject dobj = getEmailAddDO(module);
        if (!dobj.isEmpty()) {
            final StringBuffer buffer = new StringBuffer();
            final Iterator iter = dobj.getRows("EMailAddr");
            while (iter.hasNext()) {
                final Row row = iter.next();
                if (buffer.length() > 0) {
                    buffer.append(",");
                }
                buffer.append((String)row.get("EMAIL_ADDR"));
            }
            strEMailAddr = buffer.toString();
        }
        else {
            SyMUtil.logger.log(Level.WARNING, sourceMethod + "DB Backup To Email address configuration is Empty ");
        }
        return strEMailAddr;
    }
    
    public static String createI18NxslFile(final String originalXSLFile, final String tempXSLFileName) {
        String tempXSLFile = "";
        FileOutputStream fout = null;
        try {
            SyMUtil.logger.log(Level.INFO, "Going to parse the XSL file ....");
            final String server_home = getInstallationDir();
            tempXSLFile = server_home + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "xsl" + File.separator + tempXSLFileName;
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(originalXSLFile), "UTF-8"));
            final File xslFileTemp = new File(tempXSLFile);
            xslFileTemp.createNewFile();
            fout = new FileOutputStream(xslFileTemp);
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fout, "UTF-8"));
            final StringBuffer mainBuffer = new StringBuffer();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (line.contains("<I18N>")) {
                    final String[] strList = line.split("<I18N>");
                    final int len = strList.length;
                    int i = 0;
                    while (i < len) {
                        final String key = strList[i];
                        ++i;
                        if (key.contains("</I18N>")) {
                            String keyPart = key.substring(0, key.indexOf("</I18N>"));
                            keyPart = keyPart.trim();
                            line = line.replace(keyPart, I18N.getMsg(keyPart, new Object[0]));
                        }
                    }
                    line = line.replaceAll("<I18N>", " ");
                    line = line.replaceAll("</I18N>", " ");
                }
                mainBuffer.append(line + "\n");
            }
            out.write(mainBuffer.toString());
            out.close();
            fout.close();
            SyMUtil.logger.log(Level.INFO, "Temporary XSL file is created successfully. File and location are " + tempXSLFile);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while parsing the xsl file .... " + e.getMessage());
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception e) {
                SyMUtil.logger.log(Level.SEVERE, "Exception while closing filestream " + e.getMessage());
            }
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception e2) {
                SyMUtil.logger.log(Level.SEVERE, "Exception while closing filestream " + e2.getMessage());
            }
        }
        return tempXSLFile;
    }
    
    public static void setTestMode(final String value) {
        SyMUtil.isTestMode = Boolean.valueOf(value);
    }
    
    public static boolean isTestMode() {
        if (SyMUtil.isTestMode == null) {
            final String dbValue = getSyMParameter("IS_TESTING_MODE");
            if (dbValue != null) {
                SyMUtil.isTestMode = Boolean.valueOf(dbValue);
            }
            else {
                SyMUtil.isTestMode = Boolean.FALSE;
                updateSyMParameter("IS_TESTING_MODE", SyMUtil.isTestMode.toString());
            }
        }
        return SyMUtil.isTestMode;
    }
    
    public static Properties getLocalesProperties() {
        Properties props = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "locales.conf";
            SyMUtil.logger.log(Level.FINE, "***********getProductProperties *********** fname: " + fname);
            props = FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting product properties.", ex);
        }
        return props;
    }
    
    public String encodeURIComponentEquivalent(String str) {
        try {
            final ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
            str = (String)((Invocable)jsEngine).invokeFunction("encodeURIComponent", str);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occoured in encodeURIComponentEquivalent....", ex);
        }
        return str;
    }
    
    public String decodeURIComponentEquivalent(String str) {
        try {
            final ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
            str = (String)((Invocable)jsEngine).invokeFunction("decodeURIComponent", str);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Exception occoured in decodeURIComponentEquivalent....", ex);
        }
        return str;
    }
    
    public static String decodeAsUTF16LE(final String encodedString) {
        final byte[] decodedByteArray = Base64.getDecoder().decode(encodedString);
        return new String(decodedByteArray, StandardCharsets.UTF_16LE);
    }
    
    public static String encodeAsUTF16LE(final String content) {
        final byte[] encodedByteArray = StandardCharsets.UTF_16LE.encode(content).array();
        return Base64.getEncoder().encodeToString(encodedByteArray);
    }
    
    public Long getLoggedInUserID() {
        Long userID = null;
        try {
            userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userID == null) {
                final String loginUserName = EventConstant.DC_SYSTEM_USER;
                userID = DMUserHandler.getUserID(loginUserName);
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting userid");
        }
        return userID;
    }
    
    public String getTheme() {
        if (DMApplicationHandler.isMdmProduct()) {
            return "dm-default";
        }
        return "sdp-blue";
    }
    
    public TreeMap getRoleList(final String licType) {
        final TreeMap tmRole = new TreeMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria cri = null;
            final Join join = new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1);
            final Join join2 = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1);
            sq.addJoin(join);
            sq.addJoin(join2);
            if (licType.equalsIgnoreCase("Standard")) {
                cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12);
            }
            else if (licType.equalsIgnoreCase("TOOLSADDON")) {
                cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"T", 12);
            }
            Criteria criteria = new Criteria(Column.getColumn("UMRole", "STATUS"), (Object)getVisibleUMRoles(), 8);
            if (!DMModuleHandler.isOSDEnabled() || CustomerInfoUtil.getInstance().isMSP()) {
                criteria = criteria.and(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)"OS Deployer", 1));
            }
            if (CustomerInfoUtil.isOSDProduct()) {
                final String[] toExcludeRoles = { "Remote Desktop Viewer", "IT Asset Manager", "Patch Manager", "Mobile Device Manager", "Auditor", "Technician" };
                final Criteria osdRoles = new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)toExcludeRoles, 9);
                criteria = ((criteria != null) ? criteria.and(osdRoles) : osdRoles);
            }
            if (cri != null) {
                sq.setCriteria(cri.and(criteria));
            }
            else {
                sq.setCriteria(criteria);
            }
            sq.addJoin(new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            final Persistence per = getPersistence();
            final DataObject dataObj = per.get(sq);
            if (!dataObj.isEmpty()) {
                final Iterator ite = dataObj.getRows("UMRole");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    final String name = (String)r.get("UM_ROLE_NAME");
                    final Long roleID = (Long)r.get("UM_ROLE_ID");
                    tmRole.put(name, roleID);
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Error occured in  getRoleList  method", e);
        }
        return tmRole;
    }
    
    public long getImmediateTimeOlderThanGivenTime(Long defaultTime, final long days, final long hours, final long minutes, final long secs) {
        if (defaultTime == null) {
            defaultTime = getCurrentTime();
        }
        defaultTime -= days * hours * minutes * secs * 1000L;
        return defaultTime;
    }
    
    public long getImmediateTimeOlderThanGivenDays(final long day) {
        final long longTime = this.getImmediateTimeOlderThanGivenTime(null, day, 24L, 60L, 60L);
        return longTime;
    }
    
    public String getProductMode() {
        if (DCPluginUtil.getInstance().isPlugin()) {
            return "Plugin";
        }
        return "Stand_alone";
    }
    
    public static long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
    
    public ArrayList<String> splitToArrayList(final CharSequence input, final String regex) {
        return this.splitToArrayList(input, regex, 0);
    }
    
    public ArrayList<String> splitToArrayList(final CharSequence input, final String regex, final int limit) {
        int index = 0;
        final boolean matchLimited = limit > 0;
        final ArrayList<String> matchList = new ArrayList<String>();
        final Pattern pattern = Pattern.compile(regex);
        final Matcher m = pattern.matcher(input);
        while (m.find()) {
            if (!matchLimited || matchList.size() < limit - 1) {
                final String match = input.subSequence(index, m.start()).toString();
                matchList.add(match);
                index = m.end();
            }
            else {
                if (matchList.size() != limit - 1) {
                    continue;
                }
                final String match = input.subSequence(index, input.length()).toString();
                matchList.add(match);
                index = m.end();
            }
        }
        if (index == 0) {
            matchList.add(input.toString());
            return matchList;
        }
        if (!matchLimited || matchList.size() < limit) {
            matchList.add(input.subSequence(index, input.length()).toString());
        }
        int resultSize = matchList.size();
        if (limit == 0) {
            while (resultSize > 0 && matchList.get(resultSize - 1).equals("")) {
                --resultSize;
            }
        }
        return (ArrayList)matchList.subList(0, resultSize);
    }
    
    public static String getParamvaluefromReq(final String[] reqParameters, final String requestedParam) {
        String value = "";
        for (int i = 0; i < reqParameters.length; ++i) {
            if (reqParameters[i].contains(requestedParam)) {
                value = reqParameters[i];
                final int length = requestedParam.length();
                value = value.substring(value.indexOf(requestedParam) + length + 1);
                break;
            }
        }
        return value;
    }
    
    public static long getLastUpdatedTime(final String taskName) {
        final String updateTblName = "LatestUpdateTime";
        try {
            final Column col = Column.getColumn(updateTblName, "TASK_NAME");
            final Criteria cri = new Criteria(col, (Object)taskName, 0);
            final DataObject updateInfoDO = getPersistence().get(updateTblName, cri);
            if (updateInfoDO.isEmpty()) {
                SyMUtil.logger.log(Level.INFO, "There is no row exists in LatestUpdateTime table for taskName: " + taskName + " Returning 0.");
                return 0L;
            }
            final Row updateRow = updateInfoDO.getFirstRow(updateTblName);
            final Long lastupdatetime = (Long)updateRow.get("LAST_UPDATED_TIME");
            return lastupdatetime;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving latest update time for given task: " + taskName, ex);
            return 0L;
        }
    }
    
    public static void updateLastUpdatedTime(final long timeVal, final String taskName) {
        final String updateTblName = "LatestUpdateTime";
        try {
            final Column col = Column.getColumn(updateTblName, "TASK_NAME");
            final Criteria cri = new Criteria(col, (Object)taskName, 0);
            DataObject updateInfoDO = getPersistence().get(updateTblName, cri);
            if (updateInfoDO == null || updateInfoDO.isEmpty()) {
                SyMUtil.logger.log(Level.FINER, "There is no row exists in LatestUpdateTime table. Going to add new one for taskName: " + taskName + " with timeVal: " + timeVal);
                updateInfoDO = getPersistence().constructDataObject();
                final Row updateRow = new Row(updateTblName);
                updateRow.set("TASK_NAME", (Object)taskName);
                updateRow.set("LAST_UPDATED_TIME", (Object)new Long(timeVal));
                updateInfoDO.addRow(updateRow);
                getPersistence().add(updateInfoDO);
            }
            else {
                SyMUtil.logger.log(Level.FINER, "Row already exists in LatestUpdateTime table. Going to update now for taskName: " + taskName + " with timeVal: " + timeVal);
                final Row updateRow = updateInfoDO.getFirstRow(updateTblName);
                updateRow.set("LAST_UPDATED_TIME", (Object)new Long(timeVal));
                updateInfoDO.updateRow(updateRow);
                getPersistence().update(updateInfoDO);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating the last update time in DB for given task: " + taskName, ex);
        }
    }
    
    public static Row addOrUpdateUserAccountSettings(Row userAccRow) throws SyMException {
        try {
            final String tableName = "UserSettings";
            final Column col = Column.getColumn(tableName, "USER_ACCOUNT_ID");
            final Criteria criteria = new Criteria(col, userAccRow.get("USER_ACCOUNT_ID"), 0);
            DataObject settingsDO = getPersistence().get(tableName, criteria);
            if (settingsDO.isEmpty()) {
                settingsDO.addRow(userAccRow);
                getPersistence().add(settingsDO);
                SyMUtil.logger.log(Level.FINEST, "Adding User Account Settings: " + userAccRow);
            }
            else {
                settingsDO.updateRow(userAccRow);
                settingsDO = getPersistence().update(settingsDO);
                userAccRow = settingsDO.getRow(tableName);
                SyMUtil.logger.log(Level.FINER, "Updating User Account Settings: " + userAccRow);
            }
            return userAccRow;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while add/update user account settings: " + userAccRow, ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public static Row getUserAccountSettings(final Long userAccId) throws SyMException {
        try {
            final String tableName = "UserSettings";
            final Column col = Column.getColumn(tableName, "USER_ACCOUNT_ID");
            final Criteria criteria = new Criteria(col, (Object)userAccId, 0);
            final DataObject settingsDO = getPersistence().get(tableName, criteria);
            Row userAccRow = null;
            if (!settingsDO.isEmpty()) {
                userAccRow = settingsDO.getRow(tableName);
            }
            return userAccRow;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving user settings for user account id: " + userAccId, ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public static Properties getTimeProperties() {
        final Properties props = new Properties();
        try {
            final Persistence per = getPersistence();
            final Criteria c = null;
            final DataObject daob = per.get("DefaultTimeFormat", c);
            final Iterator timeIter = daob.getRows("DefaultTimeFormat");
            final long currTime = getCurrentTimeInMillis();
            while (timeIter.hasNext()) {
                final Row timeRow = timeIter.next();
                ((Hashtable<String, String>)props).put(timeRow.get("TIME_FORMAT").toString(), Utils.longdateToString(currTime, timeRow.get("TIME_FORMAT").toString()));
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting time properties.", ex);
        }
        return props;
    }
    
    @Deprecated
    public static LinkedHashMap getAvailableTimeZone() {
        return getTimeZoneString(TimeZone.getAvailableIDs());
    }
    
    @Deprecated
    public static LinkedHashMap getTimeZoneString(final String[] zoneIds) {
        final LinkedHashMap timeZoneTreeMap = new LinkedHashMap();
        final Date today = new Date();
        for (int i = 0; i < zoneIds.length; ++i) {
            final TimeZone tz = TimeZone.getTimeZone(zoneIds[i]);
            final String longName = tz.getDisplayName(tz.inDaylightTime(today), 1);
            final int rawOffset = tz.getRawOffset();
            final int hour = rawOffset / 3600000;
            final int min = Math.abs(rawOffset / 60000) % 60;
            final String minutes = (min > 0) ? (min + "") : (min + "0");
            final String key = zoneIds[i];
            String value = "";
            Double range;
            if (hour >= 0) {
                value = "( GMT+" + hour + ":" + minutes + " ) " + longName + "( " + zoneIds[i] + " )";
                range = hour + min * 0.01;
            }
            else {
                value = "( GMT" + hour + ":" + minutes + " ) " + longName + "( " + zoneIds[i] + " )";
                range = hour - min * 0.01;
            }
            timeZoneTreeMap.put(key, value + "#" + range);
        }
        final List<Map.Entry<String, String>> listOfEntries = new ArrayList<Map.Entry<String, String>>(timeZoneTreeMap.entrySet());
        final Comparator<Map.Entry<String, String>> c = new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(final Map.Entry<String, String> t1, final Map.Entry<String, String> t2) {
                return Double.valueOf(t1.getValue().substring(t1.getValue().indexOf("#") + 1)).compareTo(Double.valueOf(t2.getValue().substring(t2.getValue().indexOf("#") + 1)));
            }
        };
        Collections.sort(listOfEntries, c);
        final LinkedHashMap result = new LinkedHashMap();
        for (final Map.Entry zone : listOfEntries) {
            final String val = zone.getValue();
            result.put(zone.getKey(), val.substring(0, val.indexOf("#")));
        }
        return result;
    }
    
    public static String getFormattedTimeZone(final String zoneID) {
        final Date today = new Date();
        final TimeZone tz = TimeZone.getTimeZone(zoneID);
        final String longName = tz.getDisplayName(tz.inDaylightTime(today), 1);
        final int rawOffset = tz.getRawOffset();
        final int hour = rawOffset / 3600000;
        final int min = Math.abs(rawOffset / 60000) % 60;
        final String key = zoneID;
        String value = "";
        if (hour >= 0) {
            value = "(GMT+" + hour + ":" + min + ") " + longName + " (" + zoneID + ")";
        }
        else {
            value = "(GMT" + hour + ":" + min + ") " + longName + " (" + zoneID + ")";
        }
        return value;
    }
    
    public static String getUserTimeFormat() {
        final String userTimeFormat = getUserDateTimeFormat("TIMEFORMAT");
        SyMUtil.logger.log(Level.FINEST, "return value from userTimeFormat : " + userTimeFormat);
        return userTimeFormat;
    }
    
    public static String getUserTimeFormat(final Long userID) {
        final String userTimeFormat = getUserDateTimeFormat(userID, "TIMEFORMAT");
        SyMUtil.logger.log(Level.FINEST, "return value from userTimeFormat : " + userTimeFormat);
        return userTimeFormat;
    }
    
    public static String getUserDateTimeFormat(final Long userID, final String columnName) {
        String userFormat = "";
        try {
            final Persistence persistence = getPersistence();
            final Criteria userIDCrit = new Criteria(new Column("UserSettings", "USER_ACCOUNT_ID"), (Object)userID, 0);
            final DataObject dataObject = persistence.get("UserSettings", userIDCrit);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("UserSettings");
                userFormat = row.get(columnName).toString();
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getting user date and time format...", e);
        }
        if (userFormat.isEmpty() && columnName.equalsIgnoreCase("DATEFORMAT")) {
            userFormat = getDefaultDateFormat();
        }
        else if (userFormat.isEmpty() && columnName.equalsIgnoreCase("TIMEFORMAT")) {
            userFormat = getDefaultTimeFormat();
        }
        SyMUtil.logger.log(Level.FINEST, "return value from getUserDateTimeFormat : " + userFormat);
        return userFormat;
    }
    
    public static String getUserDateTimeFormat(final String columnName) {
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            return getUserDateTimeFormat(userID, columnName);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getting user date and time format...", e);
            return columnName.equalsIgnoreCase("DATEFORMAT") ? getDefaultDateFormat() : getDefaultTimeFormat();
        }
    }
    
    public static String getUserDateFormat() {
        SyMUtil.logger.log(Level.FINE, "getUserDateFormat method is called...");
        final String userDateFormat = getUserDateTimeFormat("DATEFORMAT");
        return userDateFormat;
    }
    
    public static Properties getProductInfo() {
        final Properties productProps = new Properties();
        final Boolean isMETrackEnabled = true;
        try {
            productProps.setProperty("productVersion", getProductProperty("productversion"));
            productProps.setProperty("productID", ProductUrlLoader.getInstance().getValue("productcode"));
            productProps.setProperty("buildnumber", getProductProperty("buildnumber"));
            final LicenseProvider w = LicenseProvider.getInstance();
            final String licenseType = w.getLicenseType();
            productProps.setProperty("licenseType", licenseType);
            final String productType = w.getProductCategoryString();
            productProps.setProperty("productType", productType);
            final String licensedTo = w.getUserName();
            productProps.setProperty("licensedTo", licensedTo);
            final String installationdateInLong = getInstallationProperty("it");
            productProps.setProperty("it", installationdateInLong);
            final String language = getInstallationProperty("lang");
            productProps.setProperty("lang", language);
            String som = "N/A";
            if (isMETrackEnabled) {
                som = getInstallationProperty("som");
                final String[] somPropsArray = som.split("metrId");
                som = somPropsArray[0];
            }
            productProps.setProperty("som", som);
            final String environment = CustomerInfoUtil.isSAS() ? "cloud" : "onpremise";
            productProps.setProperty("environment", environment);
            final String sdp = getInstallationProperty("sdp");
            productProps.setProperty("sdp", sdp);
            final String currentDataBase = DBUtil.getActiveDBName();
            productProps.setProperty("db", currentDataBase);
            String mdm = "N/A";
            if (isMETrackEnabled) {
                mdm = getInstallationProperty("mdm");
            }
            productProps.setProperty("mdm", mdm);
        }
        catch (final Exception excep) {
            SyMUtil.logger.log(Level.SEVERE, "Exception occurred while getting product properties. ", excep);
        }
        return productProps;
    }
    
    public static String getDefaultTimeFormat() {
        return "MMM d, yyyy hh:mm a";
    }
    
    public static String getDefaultDateFormat() {
        return "MMM d, yyyy";
    }
    
    public static Locale getUserLocale() {
        Locale userLocale = Locale.getDefault();
        try {
            userLocale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getting user locale...", e);
        }
        return userLocale;
    }
    
    public static TimeZone getUserTimeZone() {
        TimeZone timeZone = null;
        try {
            final String timeZoneID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID();
            timeZone = TimeZone.getTimeZone(timeZoneID);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getting getUserTimeZone value... ", e);
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        return timeZone;
    }
    
    public static String getUserTimeZoneID() {
        SyMUtil.logger.log(Level.FINE, "getUserTimeZone method is called...");
        String timeZoneID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID();
        if (timeZoneID.isEmpty()) {
            timeZoneID = getDefaultTimeZoneID();
        }
        return timeZoneID;
    }
    
    public static String getDefaultTimeZoneID() {
        final TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getID();
    }
    
    public static String getUsersDateFormat(final String columnName) {
        SyMUtil.logger.log(Level.FINE, "getDateFormat method is called..." + columnName);
        String dateFormat = "";
        try {
            final Persistence per = getPersistence();
            final Criteria criteria = new Criteria(new Column("DefaultTimeFormat", "TIME_FORMAT"), (Object)columnName, 0);
            final DataObject dataObject = per.get("DefaultTimeFormat", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DefaultTimeFormat");
                dateFormat = row.get("DATE_FORMAT").toString();
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while getDateFormat...", e);
        }
        if (dateFormat.isEmpty()) {
            dateFormat = getDefaultDateFormat();
        }
        return dateFormat;
    }
    
    public static String getRawDID() {
        String actualDIDValue = getSyMParameter("Actual_DID");
        try {
            if (actualDIDValue == null || actualDIDValue.equals("")) {
                final String DID_FILE_PATH = getInstallationDir() + File.separator + "DID.conf";
                final Properties didProps = FileAccessUtil.readProperties(DID_FILE_PATH);
                if (didProps.containsKey("DID")) {
                    actualDIDValue = didProps.getProperty("DID").trim();
                    updateSyMParameter("Actual_DID", actualDIDValue);
                }
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Exception in getting raw DID Value", ex);
        }
        return actualDIDValue;
    }
    
    public static String getDIDValue() {
        final String didValue = getRawDID();
        String didStatus = getSyMParameter("DID");
        try {
            if (didStatus == null || didStatus.equals("")) {
                if (didValue == null || didValue.equals("")) {
                    didStatus = "DIDNotAvailable";
                }
                else if (didValue.length() > 50) {
                    updateSyMParameter("Actual_DID", didValue.substring(0, 50));
                    didStatus = "junkValue";
                }
                else if (didValue.contains("integ")) {
                    didStatus = "DefaultCharacters";
                }
                else if (!Pattern.matches("^[0-9]+(-[0-9]+)*(:[a-z]+)?$", didValue)) {
                    didStatus = "junkValue";
                }
                else {
                    didStatus = didValue;
                }
                updateSyMParameter("DID", didStatus);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Exception in determining DID Status", ex);
        }
        return didStatus;
    }
    
    public static boolean checkIECompatibleForAjax(final String browserDetails) {
        final String user = browserDetails.toLowerCase();
        if (user.contains("msie")) {
            final String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            final String browserVer = substring.split(" ")[1];
            if (browserVer.equals("7.0") || browserVer.equals("8.0") || browserVer.equals("9.0")) {
                return false;
            }
        }
        return true;
    }
    
    public static Properties constructRowToProps(final Row row) {
        final Properties props = new Properties();
        String colName = null;
        if (row != null) {
            final List rowColumnNameList = row.getColumns();
            final Iterator colIter = rowColumnNameList.iterator();
            while (colIter.hasNext()) {
                colName = colIter.next();
                final Object value = row.get(colName);
                if (value != null) {
                    ((Hashtable<String, Object>)props).put(colName, value);
                }
            }
        }
        return props;
    }
    
    public static String getDbLocksFilePath() {
        return System.getProperty("server.home") + File.separator + "logs" + File.separator + "Dblocks";
    }
    
    public static String getDateTimeString(final Date date) {
        final PrettyTime p = new PrettyTime();
        return p.format(date);
    }
    
    public static String getAgentMetaDataParameter(final String paramKey) {
        try {
            final Column col = Column.getColumn("AgentMetaData", "DATA_KEY");
            final Criteria criteria = new Criteria(col, (Object)paramKey, 0, false);
            final DataObject agentDO = getPersistence().get("AgentMetaData", criteria);
            if (agentDO.isEmpty()) {
                return null;
            }
            final Row sfrow = agentDO.getRow("AgentMetaData");
            final String paramValue = (String)sfrow.get("DATA_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving Agent Parameter:" + paramKey + " from DB.", ex);
            return null;
        }
    }
    
    public static Map<String, Object> jsonToMap(final JSONObject json) throws JSONException {
        return jsonToMap(json, Boolean.TRUE);
    }
    
    public static Map<String, Object> jsonToMap(final JSONObject json, final boolean convertCaseUpper) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (json != JSONObject.NULL) {
            retMap = toMap(json, convertCaseUpper);
        }
        return retMap;
    }
    
    public static Map<String, Object> toMap(final JSONObject object, final boolean convertCaseUpper) throws JSONException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            final String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = jsonArrtoList((JSONArray)value, convertCaseUpper);
            }
            else if (value instanceof JSONObject) {
                value = toMap((JSONObject)value, convertCaseUpper);
            }
            map.put(convertCaseUpper ? key.toUpperCase(Locale.ENGLISH) : key, value);
        }
        return map;
    }
    
    public static List<Object> jsonArrtoList(final JSONArray array, final boolean convertCaseUpper) throws JSONException {
        final List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = jsonArrtoList((JSONArray)value, convertCaseUpper);
            }
            else if (value instanceof JSONObject) {
                value = toMap((JSONObject)value, convertCaseUpper);
            }
            list.add(value);
        }
        return list;
    }
    
    public static List<Object> jsonArrtoList(final JSONArray array) throws JSONException {
        return jsonArrtoList(array, Boolean.FALSE);
    }
    
    public static String getServerParameter(final String paramKey) {
        final Long startTime = System.currentTimeMillis();
        try {
            final DataObject serverParamsDO = DataAccess.get("ServerParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("ServerParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final Row serverParamRow = serverParamsDO.getRow("ServerParams", criteria);
            if (serverParamRow == null) {
                return null;
            }
            final String paramValue = (String)serverParamRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while retrieving Server Parameter:" + paramKey + " from DB.", ex);
            return null;
        }
    }
    
    public static void updateServerParameter(final String paramName, final String paramValue) {
        try {
            final DataObject serverParamsDO = DataAccess.get("ServerParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("ServerParams", "PARAM_NAME"), (Object)paramName, 0, false);
            Row serverParamRow = serverParamsDO.getRow("ServerParams", criteria);
            if (serverParamRow == null) {
                serverParamRow = new Row("ServerParams");
                serverParamRow.set("PARAM_NAME", (Object)paramName);
                serverParamRow.set("PARAM_VALUE", (Object)paramValue);
                serverParamsDO.addRow(serverParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                serverParamRow.set("PARAM_VALUE", (Object)paramValue);
                serverParamsDO.updateRow(serverParamRow);
                SyMUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            getPersistence().update(serverParamsDO);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static void deleteServerParameter(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ServerParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while deleting Server Parameter:" + paramKey + " from DB.", ex);
        }
    }
    
    public static StringBuilder getReaderContent(final Reader reader) throws Exception {
        final StringBuilder strBuilder = new StringBuilder();
        try {
            int read = 0;
            final char[] chBuf = new char[500];
            while ((read = reader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            return strBuilder;
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred while fetching content for queue.. ", e);
            throw e;
        }
    }
    
    public static void setCustomParamValues(final String key) {
        SyMUtil.logger.log(Level.FINEST, "setCustSpecificValues starting");
        try {
            final String fileName = getInstallationDir() + File.separator + "conf" + File.separator + "custom-params.conf";
            final File confFile = new File(fileName);
            String value = "false";
            if (confFile.exists()) {
                final Properties properties = FileAccessUtil.readProperties(fileName);
                if (properties.containsKey(key) && properties.getProperty(key) != null) {
                    value = properties.getProperty(key);
                }
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache(key, value);
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Unable to set server FQDN Path details to cache :" + ex);
        }
    }
    
    public void clearCacheForView(final String viewName) {
        ClientDataObjectCache.clearCacheForView(viewName);
    }
    
    public static String decrypt(final String text) {
        return CryptoUtil.decrypt(text);
    }
    
    public static SelectQuery formSelectQuery(final String tableName, final Criteria whereCriteria, final ArrayList<Column> selectColumns, final ArrayList groupList, final ArrayList<SortColumn> sortColumn, final ArrayList<Join> join, final Criteria havingCriteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table(tableName));
        if (selectColumns != null) {
            selectQuery.addSelectColumns((List)selectColumns);
        }
        if (groupList != null) {
            selectQuery.setGroupByClause(new GroupByClause((List)groupList, havingCriteria));
        }
        if (sortColumn != null) {
            for (int i = 0; i < sortColumn.size(); ++i) {
                selectQuery.addSortColumn((SortColumn)sortColumn.get(i));
            }
        }
        if (whereCriteria != null) {
            selectQuery.setCriteria(whereCriteria);
        }
        if (join != null) {
            for (int i = 0; i < join.size(); ++i) {
                if (join.get(i) != null) {
                    selectQuery.addJoin((Join)join.get(i));
                }
            }
        }
        return selectQuery;
    }
    
    public static boolean isValidJSON(final String jsonData) {
        try {
            new JSONObject(jsonData);
        }
        catch (final JSONException e) {
            try {
                new JSONArray(jsonData);
            }
            catch (final JSONException ex) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isStringValid(final String string) {
        return string != null && !string.contains("null") && !string.isEmpty();
    }
    
    public static String encodeURLbodyParams(final HashMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator paramsIterator = params.entrySet().iterator();
        while (paramsIterator.hasNext()) {
            try {
                final Map.Entry pair = paramsIterator.next();
                if (pair.getKey() == null || pair.getValue() == null) {
                    continue;
                }
                final String encodedKey = URLEncoder.encode(pair.getKey(), StandardCharsets.UTF_8.toString());
                final String encodedValue = URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8.toString());
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(encodedKey);
                sb.append('=');
                sb.append(encodedValue);
            }
            catch (final UnsupportedEncodingException ex) {
                SyMUtil.logger.log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }
    
    public static DMHttpResponse executeDMHttpRequest(final DMHttpRequest dmHttpRequest) {
        final JSONObject resultJSObject = new JSONObject();
        final DMHttpClient dMHttpClient = new DMHttpClient();
        DMHttpResponse dmHttpResponse = new DMHttpResponse();
        try {
            dmHttpResponse = dMHttpClient.execute(dmHttpRequest);
        }
        catch (final MalformedURLException | NoRouteToHostException | PortUnreachableException | ProtocolException | UnknownHostException | UnknownServiceException ex) {
            try {
                resultJSObject.put("ERROR_CODE", 80007);
                dmHttpResponse.responseBodyAsString = resultJSObject.toString();
                SyMUtil.logger.log(Level.SEVERE, null, ex);
            }
            catch (final JSONException ex2) {
                SyMUtil.logger.log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        catch (final SocketException ex3) {
            try {
                resultJSObject.put("ERROR_CODE", 80007);
                dmHttpResponse.responseBodyAsString = resultJSObject.toString();
                SyMUtil.logger.log(Level.SEVERE, null, ex3);
            }
            catch (final JSONException ex2) {
                SyMUtil.logger.log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        catch (final JSONException ex4) {
            SyMUtil.logger.log(Level.SEVERE, null, (Throwable)ex4);
        }
        catch (final Exception ex5) {
            SyMUtil.logger.log(Level.SEVERE, null, ex5);
        }
        return dmHttpResponse;
    }
    
    public static boolean isStringEmpty(String str) {
        if (str != null) {
            str = str.trim();
            return str.isEmpty() || str.equalsIgnoreCase("--") || str.equalsIgnoreCase("-") || str.equalsIgnoreCase("null");
        }
        return true;
    }
    
    public static String getCurrentLogLevel() throws Exception {
        String level = null;
        try {
            level = getSyMParameter("LogLevel");
            if (level == null) {
                level = "NORMAL";
                updateSyMParameter("LogLevel", level);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting current log level. ", ex);
            throw ex;
        }
        return level;
    }
    
    public static DataObject getPersonalisedViewsForViewName(final String baseView) {
        DataObject personalisedViews = null;
        try {
            final Long originalViewId = WebViewAPI.getViewNameNo((Object)baseView);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("PersonalizedViewMap"));
            final Column originalViewCol = Column.getColumn("PersonalizedViewMap", "ORIGVIEWNAME");
            final Criteria baseViewCrit = new Criteria(originalViewCol, (Object)originalViewId, 0);
            final Join viewJoin = new Join(Table.getTable("PersonalizedViewMap"), Table.getTable("ViewConfiguration"), new String[] { "PERSVIEWNAME" }, new String[] { "VIEWNAME_NO" }, 2);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.addJoin(viewJoin);
            selectQuery.setCriteria(baseViewCrit);
            personalisedViews = getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting personalised Views for given Views. ", e);
        }
        return personalisedViews;
    }
    
    public static DataObject getColumnDetailsForColumn(final Criteria columnCriteria) {
        DataObject columnDetailsDO = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ColumnDetails"));
            final Join tableDetailsJoin = new Join("ColumnDetails", "TableDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.addJoin(tableDetailsJoin);
            selectQuery.setCriteria(columnCriteria);
            columnDetailsDO = getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting default values for Column. ", e);
        }
        return columnDetailsDO;
    }
    
    public static Integer[] getVisibleUMRoles() {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            return new Integer[] { DCUserConstants.VISIBLE_ROLE, DCUserConstants.MSP_ROLE };
        }
        return new Integer[] { DCUserConstants.VISIBLE_ROLE };
    }
    
    public boolean checkTimeFormat(final String time, final String format, final boolean is24HrFormat) {
        String regex = "^(" + format.replace("H", "h").replace("M", "m").replace("S", "s").replace("A", "a");
        if (is24HrFormat) {
            regex += "$)";
            regex = regex.replace("hh", "(?:[01]\\d|2[0-3])");
            regex = regex.replace("mm", "[0-5]\\d");
            regex = regex.replace("ss", "[0-5]\\d");
            return Pattern.compile(regex).matcher(time).matches();
        }
        regex += "$)";
        regex = regex.replace("hh", "(?:[0-1][0-2])");
        regex = regex.replace("mm", "[0-5]\\d");
        regex = regex.replace("ss", "[0-5]\\d");
        regex = regex.replace("a", "([AaPp][Mm])");
        return Pattern.compile(regex).matcher(time).matches();
    }
    
    public static Map<String, String> getProductLoaderProperties() {
        try {
            final Map<String, String> productProperties = new HashMap<String, String>();
            final String didVal = getDIDValue();
            final Properties genProps = ProductUrlLoader.getInstance().getGeneralProperites();
            final Properties productInfo = getProductInfo();
            productProperties.put("DID", didVal);
            genProps.forEach((property, i18key) -> {
                final String s = map.put(property, TabComponentUtil.getI18NMsg((String)i18key));
                return;
            });
            productInfo.forEach((property, value) -> {
                final String s2 = map2.put(property, value);
                return;
            });
            return productProperties;
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.SEVERE, "Exception while fetching productLoaderProperties ", ex);
            return Collections.emptyMap();
        }
    }
    
    static {
        SyMUtil.logger = Logger.getLogger(SyMUtil.class.getName());
        SyMUtil.isTestMode = null;
        SyMUtil.devMgmt = null;
        IS_TIME_IN_ACCESS = new Integer(1);
        SyMUtil.copyrightProps = null;
    }
}
