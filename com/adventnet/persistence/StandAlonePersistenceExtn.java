package com.adventnet.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;

public class StandAlonePersistenceExtn extends StandAlonePersistence
{
    protected void startServerSettings() throws Exception {
        System.setProperty("server.dir", StandAlonePersistenceExtn.serverHome = System.getProperty("server.home"));
        System.setProperty("server.conf", StandAlonePersistenceExtn.serverHome + "/conf");
        System.setProperty("tier-type", "BE");
        this.startDB();
        final DataObject dataObject = this.getModuleDO();
        final Iterator iterator = dataObject.getRows("Module");
        Row row = null;
        while (iterator.hasNext()) {
            row = iterator.next();
            this.loadModule(row.get("MODULENAME").toString());
        }
        this.populateServerStatus();
        updateBuildDetailsInDB();
    }
    
    public void startServer() throws Exception {
        try {
            this.startServerSettings();
        }
        finally {
            this.stopDB();
        }
    }
    
    protected static void updateBuildDetailsInDB() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
            selectQuery.addSelectColumn(Column.getColumn("DCServerBuildHistory", "*"));
            DataObject resultDO = DataAccess.get(selectQuery);
            final long currTime = System.currentTimeMillis();
            if (resultDO.isEmpty()) {
                final String tblName = "DCServerBuildHistory";
                final Row bhRow = new Row(tblName);
                bhRow.set("BUILD_NUMBER", (Object)Integer.parseInt(getCurrentBuildNumber()));
                bhRow.set("BUILD_DETECTED_AT", (Object)new Long(-1L));
                bhRow.set("BUILD_DETECTED_AT_STR", (Object)"--");
                bhRow.set("REMARKS", (Object)("addedBy=StandAlonePersistenceExtn, addedTime=" + getDateStr(currTime)));
                resultDO.addRow(bhRow);
                resultDO = DataAccess.add(resultDO);
                ConsoleOut.println("Build details added in DB: " + resultDO);
            }
        }
        catch (final Exception ex) {
            ConsoleOut.println("Caught exception while updating the build details in DB. Exception is " + ex);
            ex.printStackTrace();
        }
    }
    
    protected static String getCurrentBuildNumber() {
        String bnumber = null;
        try {
            final String fname = StandAlonePersistenceExtn.serverHome + File.separator + "conf" + File.separator + "product.conf";
            ConsoleOut.println("Path of product.conf file: " + fname);
            final Properties props = FileAccessUtil.readProperties(fname);
            bnumber = props.getProperty("buildnumber");
            ConsoleOut.println("Build number retrieved from product.conf is: " + bnumber);
        }
        catch (final Exception ex) {
            ConsoleOut.println("Caught exception while reading the build number. Exception is " + ex);
            ex.printStackTrace();
        }
        return bnumber;
    }
    
    protected static String getDateStr(final long dateVal) {
        final Date date = new Date(dateVal);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
        return dateFormat.format(date);
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final StandAlonePersistenceExtn standAloneObj = new StandAlonePersistenceExtn();
            standAloneObj.startServer();
            System.exit(0);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
