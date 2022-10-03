package com.adventnet.persistence;

import com.adventnet.mfw.ConsoleOut;
import com.adventnet.mfw.Starter;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.io.File;
import com.zoho.conf.Configuration;

public class StandAlonePersistence implements StandAlone
{
    public static String serverHome;
    private static String server_home;
    
    @Override
    public void startDB() throws Exception {
        PersistenceInitializer.initialize(Configuration.getString("server.conf"));
    }
    
    public DataObject getModuleDO() throws Exception {
        if (PersistenceInitializer.isColdStart()) {
            DataAccess.add(Xml2DoConverter.transform(new File(StandAlonePersistence.serverHome + "/conf/module.xml").toURL()));
        }
        final SelectQuery selectQuery = new SelectQueryImpl(Table.getTable("Module"));
        selectQuery.addSelectColumn(Column.getColumn("Module", "*"));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("Module", "MODULEORDER"), true));
        final DataObject dataObject = DataAccess.get(selectQuery);
        System.out.println("dataObject " + dataObject);
        return dataObject;
    }
    
    @Override
    public void loadModule(final String moduleName) throws Exception {
        if (PersistenceInitializer.isColdStart()) {
            PersistenceInitializer.addModule(moduleName);
            print(moduleName, "POPULATED");
        }
        else {
            PersistenceInitializer.loadModule(moduleName);
            print(moduleName, "LOADED");
        }
    }
    
    @Override
    public void populateServerStatus() throws Exception {
        String serverName;
        try {
            serverName = InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException uke) {
            serverName = "localhost";
        }
        final DataObject serverStatusDO = DataAccess.get("ServerStatus", (Criteria)null);
        if (serverStatusDO.getRow("ServerStatus") == null) {
            this.insertHostName(serverName);
        }
        else {
            boolean hostFound = false;
            final Iterator<?> iterator = serverStatusDO.getRows("ServerStatus");
            while (iterator.hasNext()) {
                final Row serverStatusRow = (Row)iterator.next();
                if (!hostFound && this.isHostSame((String)serverStatusRow.get("SERVERNAME"), serverName)) {
                    hostFound = true;
                }
            }
            if (!hostFound) {
                this.insertHostName(serverName);
            }
        }
    }
    
    private boolean isHostSame(final String serverNamePresent, final String serverName) {
        if (serverNamePresent.equalsIgnoreCase(serverName)) {
            return true;
        }
        if (serverNamePresent.contains(".") && !serverName.contains(".")) {
            return serverNamePresent.startsWith(serverName + ".");
        }
        return !serverNamePresent.contains(".") && serverName.contains(".") && serverName.startsWith(serverNamePresent + ".");
    }
    
    private void insertHostName(final String serverName) throws DataAccessException {
        final DataObject serverStatusDO = DataAccess.constructDataObject();
        final Row serverStatusRow = new Row("ServerStatus");
        serverStatusRow.set("SERVERID", new Long(1L));
        serverStatusRow.set("SERVERNAME", serverName);
        serverStatusRow.set("STATUS", new Integer(3));
        serverStatusDO.addRow(serverStatusRow);
        DataAccess.add(serverStatusDO);
    }
    
    @Override
    public void stopDB() throws Exception {
        PersistenceInitializer.stopDB();
    }
    
    @Override
    public void startServer() throws Exception {
        try {
            Starter.loadSystemProperties();
            Configuration.setString("server.dir", StandAlonePersistence.serverHome = StandAlonePersistence.server_home);
            Configuration.setString("server.conf", StandAlonePersistence.serverHome + "/conf");
            ConsoleOut.println("Started StandAlonePersistence...");
            if (ConcurrentStartupUtil.isConcurrentTableCreationEnabled()) {
                throw new Exception("Concurrent table creation is not supported for runStandAlone. Kindly disable the system property  'concurrent.tablecreation' and try again");
            }
            this.startDB();
            this.prePopulation();
            final DataObject dataObject = this.getModuleDO();
            final Iterator iterator = dataObject.getRows("Module");
            Row row = null;
            String modName = null;
            while (iterator.hasNext()) {
                row = iterator.next();
                modName = row.get("MODULENAME").toString();
                ConsoleOut.print(modName);
                this.loadModule(modName);
            }
            this.populateServerStatus();
            ConsoleOut.println("Completed StandAlonePersistence...");
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private static void print(final String moduleName, final String status) {
        for (int i = moduleName.length(); i < 50; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[" + status + "]");
    }
    
    @Override
    public void runStandAlone(final String... args) {
        try {
            try {
                this.startServer();
                this.postPopulation();
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
            finally {
                this.stopDB();
            }
            System.exit(0);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occurred while populating: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void postPopulation() throws Exception {
    }
    
    @Override
    public void prePopulation() throws Exception {
    }
    
    static {
        StandAlonePersistence.serverHome = null;
        StandAlonePersistence.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
