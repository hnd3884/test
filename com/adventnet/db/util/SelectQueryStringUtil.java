package com.adventnet.db.util;

import com.adventnet.ds.query.SelectQueryImpl;
import com.zoho.mickey.api.SQLStringAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.db.adapter.SQLGenerator;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.HashMap;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.Map;
import java.util.logging.Logger;
import com.adventnet.ds.query.util.SASCachePlugin;
import java.util.regex.Pattern;

public class SelectQueryStringUtil
{
    private static Pattern varPat;
    private static SASCachePlugin cache;
    private static final Logger LOGGER;
    
    public static boolean containsKey(final String queryKey) {
        final Map temp = getTableFromCache("SelectSQLString", 2, 3);
        return temp != null && temp.containsKey(queryKey);
    }
    
    public static String getSQLString(final String queryKey) {
        final Map temp = getTableFromCache("SelectSQLString", 2, 3);
        if (temp == null) {
            return null;
        }
        String sql = temp.get(queryKey);
        if (sql != null) {
            return sql;
        }
        sql = getQueryFromDB(queryKey);
        if (sql != null) {
            temp.put(queryKey, sql);
        }
        return sql;
    }
    
    private static String getQueryFromDB(final String queryKey) {
        String sql = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            pstmt = conn.prepareStatement("SELECT SELECT_SQL from SelectSQLString where QUERY_KEY = ?");
            pstmt.setString(1, queryKey);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sql = rs.getString(1);
            }
        }
        catch (final Exception exp) {
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {
                SelectQueryStringUtil.LOGGER.log(Level.INFO, "Exception occurred while closing db resources - {0}", ex);
            }
        }
        return sql;
    }
    
    public static String[] getTemplatesForKey(final String queryKey) {
        final Object[] arr = getParsedinfo(queryKey);
        if (arr == null) {
            return null;
        }
        return (String[])arr[1];
    }
    
    public boolean isTemplatesPresent(final String sqlString) {
        return sqlString != null && sqlString.matches(SelectQueryStringUtil.varPat.pattern());
    }
    
    public static String replaceAllTemplatesForKey(final String key) throws QueryConstructionException {
        final Object[] arr = getParsedinfo(key);
        if (arr == null) {
            throw new QueryConstructionException("No SelectQuery defined for this key " + key + " in SelectSQLString Table");
        }
        return replaceTemplates((String[])arr[0], (String[])arr[1], (boolean[])arr[2], null);
    }
    
    public static String replaceAllTemplatesForSQL(final String sqlString, final Map templateValues) throws QueryConstructionException {
        final Object[] arr = getParsedinfoForSQL(sqlString);
        return replaceTemplates((String[])arr[0], (String[])arr[1], (boolean[])arr[2], templateValues);
    }
    
    public static String replaceAllTemplatesForKey(final String key, final Object[] templateValues) throws QueryConstructionException {
        final Object[] arr = getParsedinfo(key);
        if (arr == null) {
            throw new QueryConstructionException("No SelectQuery defined for this key " + key + " in SelectSQLString Table");
        }
        final String[] templates = (String[])arr[1];
        final boolean[] isSysHandler = (boolean[])arr[2];
        final HashMap map = new HashMap(templates.length);
        int j = 0;
        for (int i = 0; i < templates.length; ++i) {
            if (!isSysHandler[i]) {
                try {
                    map.put(templates[i], templateValues[j++]);
                }
                catch (final ArrayIndexOutOfBoundsException e) {
                    throw new QueryConstructionException("Either templateValues[] array is not specified properly OR System templates are not properly defined in SystemTemplateHandler table for the queryKey " + key);
                }
            }
        }
        return replaceTemplates((String[])arr[0], templates, isSysHandler, map);
    }
    
    public static String replaceAllTemplatesForKey(final String key, final Map templateValues) throws QueryConstructionException {
        final Object[] arr = getParsedinfo(key);
        if (arr == null) {
            throw new QueryConstructionException("No SelectQuery defined for this key " + key + " in SelectSQLString Table");
        }
        return replaceTemplates((String[])arr[0], (String[])arr[1], (boolean[])arr[2], templateValues);
    }
    
    private static Map getTableFromCache(final String tableName, final int keyIndex, final int valueIndex) {
        Map handlerMap = null;
        try {
            if (PersistenceInitializer.onSAS()) {
                handlerMap = (Map)SelectQueryStringUtil.cache.get(tableName);
            }
            else {
                handlerMap = (Map)CacheManager.getCacheRepository().getFromCache(tableName);
            }
            if (handlerMap != null) {
                return handlerMap;
            }
            try {
                handlerMap = new HashMap();
                final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
                final DataObject dobj = persistence.get(tableName, (Criteria)null);
                final Iterator ite = dobj.getRows(tableName);
                while (ite.hasNext()) {
                    final Row temp = ite.next();
                    final String key = (String)temp.get(keyIndex);
                    final String value = (String)temp.get(valueIndex);
                    if (key != null || value != null) {
                        handlerMap.put(key, value);
                    }
                }
            }
            catch (final Exception e) {
                return null;
            }
            if (PersistenceInitializer.onSAS()) {
                SelectQueryStringUtil.cache.put(tableName, handlerMap);
            }
            else {
                CacheManager.getCacheRepository().addToCache(tableName, handlerMap);
            }
        }
        catch (final Exception exc) {
            return null;
        }
        return handlerMap;
    }
    
    private static Object[] getParsedinfo(final String queryKey) {
        try {
            if (queryKey == null) {
                return null;
            }
            final String key = "SelectSQLString:" + queryKey.trim();
            Object[] parsedInfo = null;
            if (PersistenceInitializer.onSAS()) {
                parsedInfo = (Object[])SelectQueryStringUtil.cache.get(key);
            }
            else {
                parsedInfo = (Object[])CacheManager.getCacheRepository().getFromCache(key);
            }
            if (parsedInfo != null) {
                return parsedInfo;
            }
            final String sql = getSQLString(queryKey);
            if (sql == null) {
                return null;
            }
            parsedInfo = getParsedinfoForSQL(sql);
            if (PersistenceInitializer.onSAS()) {
                SelectQueryStringUtil.cache.put(key, parsedInfo);
            }
            else {
                CacheManager.getCacheRepository().addToCache(key, parsedInfo);
            }
            return parsedInfo;
        }
        catch (final Exception exc) {
            throw new RuntimeException(exc);
        }
    }
    
    private static Object[] getParsedinfoForSQL(final String sql) {
        final String[] staticList = SelectQueryStringUtil.varPat.split(sql);
        final List variables = new ArrayList(staticList.length);
        final Matcher mat = SelectQueryStringUtil.varPat.matcher(sql);
        while (mat.find()) {
            variables.add(mat.group(1).intern().trim());
        }
        final int size = variables.size();
        final boolean[] isSystemHandler = new boolean[size];
        for (int i = 0; i < size; ++i) {
            if (isSystemTemplate(variables.get(i))) {
                isSystemHandler[i] = true;
            }
            else {
                isSystemHandler[i] = false;
            }
        }
        final Object[] parsedInfo = { staticList, variables.toArray(new String[variables.size()]), isSystemHandler };
        return parsedInfo;
    }
    
    private static String getValueFromHandler(final String template, Map templateValues) {
        String value = null;
        String key = template;
        SelectQueryTemplateHandler sysHandler = null;
        final int index = template.indexOf(58);
        if (index > -1) {
            key = template.substring(0, index).trim();
            if (templateValues == null) {
                templateValues = new HashMap(1);
            }
            templateValues.put(key, template.substring(index + 1).trim());
        }
        try {
            final String className = getSystemHandlerClassName(key);
            if ("NULL".equalsIgnoreCase(className)) {
                value = "";
            }
            else {
                sysHandler = (SelectQueryTemplateHandler)Class.forName(className).newInstance();
                value = sysHandler.getTemplateValue(key, templateValues);
            }
        }
        catch (final Exception e) {
            SelectQueryStringUtil.LOGGER.log(Level.SEVERE, "Exception while getting value from handler", e);
            return null;
        }
        return value;
    }
    
    private static SQLGenerator getSQLGenerator() throws QueryConstructionException {
        return RelationalAPI.getInstance().getDBAdapter().getSQLGenerator();
    }
    
    private static void fillColumnName(final Criteria cr) {
        if (cr == null) {
            return;
        }
        fillColumnName(cr.getLeftCriteria());
        fillColumnName(cr.getRightCriteria());
        final Column column = cr.getColumn();
        if (column == null || column.getColumnName() != null) {
            return;
        }
        QueryUtil.setType(column.getTableAlias(), column);
    }
    
    private static String getValueFromObject(final Object temp) throws QueryConstructionException {
        String value = null;
        if (temp instanceof Criteria) {
            fillColumnName((Criteria)temp);
            value = getSQLGenerator().formWhereClause((Criteria)temp);
        }
        else if (temp instanceof Join) {
            final ArrayList joins = new ArrayList(1);
            joins.add(temp);
            value = getSQLGenerator().formJoinString(joins, null);
        }
        else if (temp instanceof List) {
            if (((List)temp).get(0) instanceof Column) {
                value = getSQLGenerator().formSelectClause((List)temp);
            }
            else if (((List)temp).get(0) instanceof Join) {
                value = getSQLGenerator().formJoinString((List)temp, null);
            }
            else if (temp instanceof Column) {
                final List li = new ArrayList();
                li.add(temp);
                value = getSQLGenerator().formSelectClause((List)temp);
            }
            else {
                value = String.valueOf(temp);
            }
        }
        else if (temp instanceof SortColumn) {
            value = "ORDER BY " + getOrderByClause((SortColumn)temp);
        }
        else if (temp instanceof GroupByClause) {
            value = getSQLGenerator().getGroupByClause((GroupByClause)temp, null);
        }
        else {
            value = String.valueOf(temp);
        }
        return value;
    }
    
    private static String replaceTemplates(final String[] constantParts, final String[] templateParts, final boolean[] isSysHandler, final Map templateValues) throws QueryConstructionException {
        if (constantParts == null || templateParts == null) {
            return null;
        }
        final StringBuilder buff = new StringBuilder();
        boolean isWhereAdded = false;
        Range range = null;
        for (int i = 0; i < constantParts.length; ++i) {
            buff.append(constantParts[i]);
            if (i < templateParts.length) {
                final String template = templateParts[i];
                String value = null;
                if (isSysHandler[i]) {
                    value = getValueFromHandler(template, templateValues);
                }
                else {
                    if (templateValues == null) {
                        continue;
                    }
                    final Object temp = templateValues.get(template);
                    if (temp == null) {
                        continue;
                    }
                    if (temp instanceof SelectQueryTemplateHandler) {
                        value = ((SelectQueryTemplateHandler)temp).getTemplateValue(template, templateValues);
                    }
                    else {
                        if (temp instanceof Range) {
                            range = (Range)temp;
                            continue;
                        }
                        value = getValueFromObject(temp);
                    }
                }
                if (value == null) {
                    throw new QueryConstructionException("Problem while forming the value part of the template " + template);
                }
                if (value.length() > 1) {
                    value = doCriteriaHandling(template, value, isWhereAdded);
                }
                if (!isWhereAdded && (template.startsWith("CRITERIA") || template.startsWith("SAS"))) {
                    isWhereAdded = true;
                }
                buff.append(value);
            }
        }
        if (range != null) {
            return SQLStringAPI.getInstance().getSQLForSelectWithRange(buff.toString(), range);
        }
        return buff.toString();
    }
    
    private static String doCriteriaHandling(final String template, String value, final boolean isWhereAdded) {
        if (template == null || value == null) {
            return value;
        }
        if (template.startsWith("AND_CRITERIA") || template.startsWith("AND_SAS")) {
            value = "AND " + value.trim();
        }
        else if (template.startsWith("OR_CRITERIA") || template.startsWith("OR_SAS")) {
            value = "OR " + value.trim();
        }
        else if (template.startsWith("CRITERIA") || template.startsWith("SAS")) {
            if (isWhereAdded) {
                value = " AND " + value.trim();
            }
            else {
                value = " WHERE " + value.trim();
            }
        }
        return value;
    }
    
    public static boolean isSystemTemplate(final String template) {
        final Map map = getTableFromCache("SystemTemplateHandler", 2, 3);
        return map.containsKey(template);
    }
    
    private static String getSystemHandlerClassName(final String key) throws QueryConstructionException {
        if (key == null) {
            return null;
        }
        final Map map = getTableFromCache("SystemTemplateHandler", 2, 3);
        if (map == null) {
            throw new QueryConstructionException("Problem while accessing the table SystemTemplateHandler");
        }
        return map.get(key);
    }
    
    private static String getOrderByClause(final SortColumn sortcolumn) throws QueryConstructionException {
        String orderbyClause = null;
        final String columnName = getSQLGenerator().getDBSpecificColumnName(sortcolumn.getColumnName());
        final String tableName = getSQLGenerator().getDBSpecificTableName(sortcolumn.getTableAlias());
        orderbyClause = tableName + "." + columnName;
        if (!sortcolumn.isAscending()) {
            orderbyClause += " DESC";
        }
        return orderbyClause;
    }
    
    static {
        SelectQueryStringUtil.varPat = Pattern.compile("\\$\\s*\\{([^\\}]*)\\s*\\}");
        SelectQueryStringUtil.cache = SASCachePlugin.getSASCachePluginImpl();
        LOGGER = Logger.getLogger(SelectQueryImpl.class.getName());
    }
    
    public interface SelectQueryTemplateHandler
    {
        String getTemplateValue(final String p0, final Map p1);
    }
}
