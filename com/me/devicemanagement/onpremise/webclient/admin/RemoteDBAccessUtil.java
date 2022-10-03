package com.me.devicemanagement.onpremise.webclient.admin;

import java.util.Iterator;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.PgsqlHbaConfUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class RemoteDBAccessUtil
{
    private Logger logger;
    public final int remoteAccess_DB_READ = 1;
    public final int remoteAccess_DB_REVOKE = 2;
    private static RemoteDBAccessUtil remoteDBAccessUtil;
    public boolean isDemoMode;
    
    public RemoteDBAccessUtil() {
        this.logger = Logger.getLogger("RemoteDBAccessLog");
        this.isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
    }
    
    public static RemoteDBAccessUtil getInstance() {
        if (RemoteDBAccessUtil.remoteDBAccessUtil == null) {
            RemoteDBAccessUtil.remoteDBAccessUtil = new RemoteDBAccessUtil();
        }
        return RemoteDBAccessUtil.remoteDBAccessUtil;
    }
    
    public Boolean accessPrivilage(final String machineName, final String dbName) {
        Boolean accessResult = false;
        try {
            final String dbHome = System.getProperty("db.home");
            PgsqlHbaConfUtil util = null;
            if (!this.isDemoMode && dbName.equalsIgnoreCase("postgres")) {
                if (PgsqlHbaConfUtil.isPgHbaTempEnabled()) {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba_Temp.conf");
                }
                else {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba.conf");
                }
                util.grantAccessForHost(machineName, "medc");
            }
            this.logger.log(Level.INFO, "DB Access privilege is granted successfully to " + machineName);
            this.disableAutoPortChangeHandling(Boolean.TRUE);
            accessResult = true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while giving  accessPrivilege" + ex);
        }
        return accessResult;
    }
    
    public Boolean resetPrivilage(final String machineName, final String dbName) {
        Boolean resetPrivilage = false;
        PgsqlHbaConfUtil util = null;
        try {
            final DataObject dobj = this.getRemoteDBComputerDO(machineName.trim());
            final String dbHome = System.getProperty("db.home");
            if (dbName.equalsIgnoreCase("postgres")) {
                if (PgsqlHbaConfUtil.isPgHbaTempEnabled()) {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba_Temp.conf");
                }
                else {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba.conf");
                }
                util.revokeAccessForHost(machineName);
                this.logger.log(Level.INFO, "Reset machine Name is " + machineName + " Privilege = reset");
                this.checkDisableAutoPortChangeHandling();
                resetPrivilage = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while reset the privilege" + ex);
        }
        return resetPrivilage;
    }
    
    private void checkDisableAutoPortChangeHandling() throws Exception {
        List computerList = new ArrayList();
        PgsqlHbaConfUtil util = null;
        final String dbHome = System.getProperty("db.home");
        final String[] excludeLocalhostFromList = { "127.0.0.1/32", "localhost", "::1/128" };
        if (PgsqlHbaConfUtil.isPgHbaTempEnabled()) {
            util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba_Temp.conf");
        }
        else {
            util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba.conf");
        }
        final List<String> excludeList = new ArrayList<String>(excludeLocalhostFromList.length);
        for (final String s : excludeLocalhostFromList) {
            excludeList.add(s);
        }
        computerList = util.getGrantedHostList();
        if (!computerList.isEmpty()) {
            computerList.removeAll(excludeList);
        }
        if (computerList.size() <= 0) {
            this.disableAutoPortChangeHandling(Boolean.FALSE);
        }
    }
    
    private void disableAutoPortChangeHandling(final boolean isDisable) throws Exception {
        final String systemPropertyFileName = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
        final Properties systemProperties = FileAccessUtil.readProperties(systemPropertyFileName);
        if (systemProperties != null && systemProperties.containsKey("ignore.db.auto.port.detect.handling")) {
            systemProperties.setProperty("ignore.db.auto.port.detect.handling", String.valueOf(isDisable));
        }
        FileAccessUtil.storeProperties(systemProperties, systemPropertyFileName, false);
    }
    
    private DataObject getRemoteDBComputerDO(final String machineName) {
        DataObject dobj = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RemoteDBComputer"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria c1 = new Criteria(Column.getColumn("RemoteDBComputer", "COMPUTER_NAME"), (Object)machineName, 0, false);
            query.setCriteria(c1);
            dobj = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting RemoteDBComputer DO " + e);
        }
        return dobj;
    }
    
    public void updateRemoteDBAccessComputer(String machinename, final int accessType) throws Exception {
        try {
            if (machinename != null) {
                machinename = machinename.trim();
                final DataObject dobj = this.getRemoteDBComputerDO(machinename);
                this.logger.log(Level.FINE, "Data Object  " + dobj);
                if (dobj.isEmpty()) {
                    final Row row = new Row("RemoteDBComputer");
                    row.set("COMPUTER_NAME", (Object)machinename);
                    row.set("OPERATION_TYPE", (Object)new Integer(accessType));
                    dobj.addRow(row);
                    this.logger.log(Level.FINE, "New Row  " + row);
                    SyMUtil.getPersistence().add(dobj);
                }
                else {
                    final Row row = dobj.getRow("RemoteDBComputer");
                    row.set("OPERATION_TYPE", (Object)new Integer(accessType));
                    dobj.updateRow(row);
                    this.logger.log(Level.FINE, "Updated Row  " + row);
                    SyMUtil.getPersistence().update(dobj);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while adding row in RemoteDBComputer " + e);
        }
    }
    
    public String getDBName() {
        String dbName = null;
        try {
            final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties dbProps = FileAccessUtil.readProperties(fname);
            if (dbProps != null) {
                final String url = dbProps.getProperty("url");
                this.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + fname);
                if (url != null) {
                    if (url.toLowerCase().contains("mysql")) {
                        dbName = "mysql";
                    }
                    else if (url.toLowerCase().contains("postgresql")) {
                        dbName = "pgsql";
                    }
                    else {
                        dbName = "mssql";
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while getting dbname...");
        }
        return dbName;
    }
    
    public List getComputerListfromDB(List computerList) {
        try {
            this.logger.log(Level.INFO, "Inside the getremoteDBAccessCompList() method...");
            final String dbName = DBUtil.getActiveDBName();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RemoteDBComputer"));
            final Criteria c1 = new Criteria(Column.getColumn("RemoteDBComputer", "OPERATION_TYPE"), (Object)1, 0);
            query.setCriteria(c1);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            if (dbName.equalsIgnoreCase("mysql")) {
                final DataObject dobj = SyMUtil.getPersistence().get(query);
                if (!dobj.isEmpty() && dobj.containsTable("RemoteDBComputer")) {
                    final Iterator iterator = dobj.getRows("RemoteDBComputer");
                    computerList = new ArrayList();
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final String computername = (String)row.get("COMPUTER_NAME");
                        this.logger.log(Level.INFO, "Computer Name " + computername);
                        if (computername != null) {
                            computerList.add(computername);
                        }
                    }
                }
            }
            else if (dbName.equalsIgnoreCase("postgres")) {
                final String dbHome = System.getProperty("db.home");
                final String[] excludeLocalhostFromList = { "127.0.0.1/32", "localhost", "::1/128" };
                PgsqlHbaConfUtil util = null;
                if (PgsqlHbaConfUtil.isPgHbaTempEnabled()) {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba_Temp.conf");
                }
                else {
                    util = new PgsqlHbaConfUtil(dbHome + "\\data\\pg_hba.conf");
                }
                final List<String> excludeList = new ArrayList<String>(excludeLocalhostFromList.length);
                for (final String s : excludeLocalhostFromList) {
                    excludeList.add(s);
                }
                computerList = util.getGrantedHostList();
                if (!computerList.isEmpty()) {
                    computerList.removeAll(excludeList);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while getting remoteDB Access computerList " + ex);
        }
        return computerList;
    }
    
    static {
        RemoteDBAccessUtil.remoteDBAccessUtil = null;
    }
}
