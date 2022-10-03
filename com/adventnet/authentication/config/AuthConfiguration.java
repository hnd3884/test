package com.adventnet.authentication.config;

import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.login.Configuration;

public class AuthConfiguration extends Configuration
{
    private static Logger logger;
    private List<String> pamConfigTables;
    private Map<String, AppConfigurationEntry[]> authRuleNameVsappConfig;
    private String service;
    
    public AuthConfiguration(final String serviceName) {
        this.pamConfigTables = null;
        this.authRuleNameVsappConfig = new HashMap<String, AppConfigurationEntry[]>();
        this.service = null;
        AuthConfiguration.logger.log(Level.FINEST, "initialized");
        (this.pamConfigTables = new ArrayList<String>(3)).add("AaaService");
        this.pamConfigTables.add("AaaPamConf");
        this.pamConfigTables.add("AaaPamModule");
        this.service = serviceName;
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String authRuleName) {
        AuthConfiguration.logger.log(Level.FINEST, "getAppConfigurationEntry invoked for authRuleName : {0}", authRuleName);
        AppConfigurationEntry[] appConfigEntries = this.authRuleNameVsappConfig.get(authRuleName);
        if (appConfigEntries == null || appConfigEntries.length == 0) {
            appConfigEntries = constructAppConfigEntries(authRuleName, this.service);
        }
        return appConfigEntries;
    }
    
    private static AppConfigurationEntry[] constructAppConfigEntries(final String authRuleName, final String service) {
        AppConfigurationEntry[] appConfigEntries = null;
        try {
            final Properties authNameVsClassName = new Properties();
            final Map<Long, String> authIDVsName = new HashMap<Long, String>();
            final Map<Long, String> authIDVsClassName = new HashMap<Long, String>();
            final DataObject pamModuleDO = DataAccess.get("AaaPamModule", (Criteria)null);
            AuthConfiguration.logger.log(Level.FINE, "pamModuleDO :: {0}", pamModuleDO);
            Iterator iterator = pamModuleDO.getRows("AaaPamModule", (Criteria)null);
            while (iterator.hasNext()) {
                final Row pamModuleRow = iterator.next();
                final Long authId = (Long)pamModuleRow.get(1);
                final String authName = (String)pamModuleRow.get(2);
                final String authClass = (String)pamModuleRow.get(4);
                authNameVsClassName.setProperty(authName, authClass);
                authIDVsName.put(authId, authName);
                authIDVsClassName.put(authId, authClass);
            }
            AuthConfiguration.logger.log(Level.FINE, "authNameVsClassName :: {0}", authNameVsClassName);
            AuthConfiguration.logger.log(Level.FINE, "authIDVsClassName :: {0}", authIDVsClassName);
            AuthConfiguration.logger.log(Level.FINE, "authIDVsName :: {0}", authIDVsName);
            final List<AppConfigurationEntry> list = new ArrayList<AppConfigurationEntry>();
            list.add(new AppConfigurationEntry(authNameVsClassName.getProperty(authRuleName), getLoginModuleControlFlag("REQUIRED"), new HashMap<String, Object>(1, 0.25f)));
            final List<String> tables = new ArrayList<String>();
            tables.add("AaaPamConf");
            tables.add("AaaService");
            final Criteria cri = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)((service != null) ? service : "System"), 0);
            final SelectQuery sq = QueryConstructor.get((List)tables, cri);
            sq.addSortColumn(new SortColumn(Column.getColumn("AaaPamConf", "EXECORDER"), true));
            final DataObject pamConfDO = DataAccess.get(sq);
            AuthConfiguration.logger.log(Level.FINE, "pamConfDO :: {0}", pamConfDO);
            iterator = pamConfDO.getRows("AaaPamConf", (Criteria)null);
            while (iterator.hasNext()) {
                final Row pamConfRow = iterator.next();
                final Long id = (Long)pamConfRow.get(2);
                list.add(new AppConfigurationEntry(authIDVsClassName.get(id), getLoginModuleControlFlag((String)pamConfRow.get(3)), new HashMap<String, Object>(1, 0.25f)));
            }
            appConfigEntries = list.toArray(new AppConfigurationEntry[0]);
        }
        catch (final Exception e) {
            e.printStackTrace();
            AuthConfiguration.logger.log(Level.SEVERE, "Exception occurred while creating AppConfigurationEntry from DB.");
        }
        return appConfigEntries;
    }
    
    @Override
    public void refresh() {
        this.authRuleNameVsappConfig = new HashMap<String, AppConfigurationEntry[]>();
    }
    
    private static AppConfigurationEntry.LoginModuleControlFlag getLoginModuleControlFlag(final String flag) {
        AppConfigurationEntry.LoginModuleControlFlag controlFlag = null;
        if (flag.equalsIgnoreCase("REQUIRED")) {
            controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
        }
        else if (flag.equalsIgnoreCase("REQUISITE")) {
            controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
        }
        else if (flag.equalsIgnoreCase("SUFFICIENT")) {
            controlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
        }
        else if (flag.equalsIgnoreCase("OPTIONAL")) {
            controlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        }
        else {
            AuthConfiguration.logger.log(Level.WARNING, "control flag : {0} is invalid, assuming it as OPTIONAL");
            controlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        }
        return controlFlag;
    }
    
    static {
        AuthConfiguration.logger = Logger.getLogger(AuthConfiguration.class.getName());
    }
}
