package com.adventnet.db.migration.handler;

import com.zoho.conf.tree.ConfTreeBuilder;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.Locale;
import com.zoho.conf.tree.ConfTree;
import java.util.logging.Logger;

public class NonMickeyTableHandlerUtil
{
    private static final Logger LOGGER;
    private static ConfTree confTree;
    private static String confFilePath;
    
    public static String getSQLForCreate(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "create", tableName, dbName);
    }
    
    public static String getSQLForSelect(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "select", tableName, dbName);
    }
    
    public static String getSQLForInsert(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "insert", tableName, dbName);
    }
    
    public static String getSQLForUpdate(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "update", tableName, dbName);
    }
    
    public static String getSQLForIndexKey(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "create.indexkey", tableName, dbName);
    }
    
    public static String getSQLForUniqueKey(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "create.uniquekey", tableName, dbName);
    }
    
    public static String getSQLForPrimaryKey(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "create.primarykey", tableName, dbName);
    }
    
    public static String getSQLForForeignKey(final String tableName, final String dbName) {
        return getSQLFor(NonMickeyTableHandlerUtil.confTree, "create.foreignkey", tableName, dbName);
    }
    
    public static String getSQLFor(final ConfTree confTree, final String keyPattern, final String tableName, final String dbName) {
        if (confTree == null) {
            return null;
        }
        String retString = null;
        if (dbName != null) {
            ConfTree subTree = confTree.getSubTree(dbName.toLowerCase(Locale.ENGLISH) + "." + keyPattern, true);
            retString = getMatchedStatement(subTree, tableName);
            if (retString == null) {
                subTree = confTree.getSubTree(keyPattern, true);
                retString = getMatchedStatement(subTree, tableName);
            }
        }
        else {
            final ConfTree subTree = confTree.getSubTree(keyPattern, true);
            retString = getMatchedStatement(subTree, tableName);
        }
        if (retString != null) {
            retString = retString.replaceAll("@tablename@", tableName);
        }
        return retString;
    }
    
    private static String getMatchedStatement(final ConfTree subTree, final String tableName) {
        String retString = null;
        String key = null;
        if (subTree == null) {
            return null;
        }
        final Iterator iterator = subTree.keySet().iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            final Pattern pattern = Pattern.compile(key, 2);
            NonMickeyTableHandlerUtil.LOGGER.fine(key + " isEquals " + tableName);
            if (pattern.matcher(tableName).matches()) {
                retString = subTree.get(key);
                break;
            }
        }
        return retString;
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultNonMickeyTablesHandler.class.getName());
        NonMickeyTableHandlerUtil.confFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "sql.conf";
        try {
            final File sqlConf = new File(NonMickeyTableHandlerUtil.confFilePath);
            if (!sqlConf.exists()) {
                NonMickeyTableHandlerUtil.LOGGER.info("sql.conf file not found...");
            }
            else {
                NonMickeyTableHandlerUtil.confTree = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(NonMickeyTableHandlerUtil.confFilePath)).build();
                NonMickeyTableHandlerUtil.LOGGER.info("sql.conf file is initialized");
            }
        }
        catch (final Exception e) {
            NonMickeyTableHandlerUtil.LOGGER.severe("Exception occurred while parsing sql.conf" + e.getMessage());
            e.printStackTrace();
        }
    }
}
