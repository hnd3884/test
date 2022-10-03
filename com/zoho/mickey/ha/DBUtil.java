package com.zoho.mickey.ha;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.mfw.Starter;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil
{
    private static final Logger LOG;
    private static HAConfig config;
    
    public static void initialize(final HAConfig configuration) {
        if (DBUtil.config == null) {
            DBUtil.config = configuration;
        }
        else {
            DBUtil.LOG.log(Level.INFO, "DBUtil.config is already initialized.. hence ignoring");
        }
    }
    
    protected static void addEntry(final String ipaddr) throws DataAccessException {
        addEntry(ipaddr, "alive");
    }
    
    public static void addEntry(final String ipaddr, final String status) throws DataAccessException {
        final DataObject dobj = DataAccess.get("FOSNodeDetails", (Criteria)null);
        assertEntry(dobj);
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        final Row entry = dobj.getRow("FOSNodeDetails", c);
        if (entry == null) {
            DBUtil.LOG.log(Level.FINER, "Adding entry for IP [ {0} ]", new Object[] { ipaddr });
            final Row r = new Row("FOSNodeDetails");
            r.set("IP", (Object)ipaddr);
            r.set("COUNTER", (Object)0);
            r.set("STATUS", (Object)status);
            r.set("REPLICATION_STATE", (Object)"none");
            r.set("BUILD_NUMBER", (Object)DBUtil.config.versionHandler().getCurrentBuildNumber());
            dobj.addRow(r);
        }
        else {
            DBUtil.LOG.log(Level.FINER, "Updating entry for IP [ {0} ]", new Object[] { ipaddr });
            entry.set(3, (Object)status);
            dobj.updateRow(entry);
        }
        DataAccess.update(dobj);
    }
    
    protected static void assertBuildNumber() throws DataAccessException {
        final DataObject dobj = getMaxBuildNumberDO();
        DBUtil.LOG.log(Level.FINER, " Build number check : {0}", new Object[] { dobj });
        if (dobj.isEmpty()) {
            return;
        }
        final Long latestBuildNumber = Long.parseLong(dobj.getFirstValue("FOSNodeDetails", "BUILD_NUMBER").toString());
        DBUtil.LOG.log(Level.FINE, "HA config: " + DBUtil.config);
        final long currentBuildNumber = DBUtil.config.versionHandler().getCurrentBuildNumber();
        final Criteria cr = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)DBUtil.config.ipaddr(), 0);
        if (dobj.getRow("FOSNodeDetails", cr) == null) {
            DBUtil.LOG.log(Level.FINER, "Latest build number in DB : {0}  Current build Number by version Handler {1}", new Object[] { latestBuildNumber, currentBuildNumber });
            if (currentBuildNumber == latestBuildNumber) {
                DBUtil.LOG.log(Level.INFO, "Pulling has been done. Hence updating the build number");
                updateBuildNumber(DBUtil.config.ipaddr(), latestBuildNumber);
            }
            else {
                if (currentBuildNumber < latestBuildNumber) {
                    throw new IllegalStateException("Other node in the system has higher build number [" + latestBuildNumber + "]. Pull the changes from the other node using Replication script and restart the product ");
                }
                deleteServerStatusEntry();
                throw new IllegalStateException("Invalid scenario. Cannot add the node with high build number than latestbuildnumber into HA system");
            }
        }
        else if (DBUtil.config.versionHandler().getCurrentBuildNumber() > latestBuildNumber) {
            throw new IllegalStateException("Build number returned by version handler is greater than DB's value. This may be due to PPM revert failure");
        }
    }
    
    private static void assertEntry(final DataObject dobj) throws DataAccessException {
        final Iterator it = dobj.getRows("FOSNodeDetails");
        int totalCount = 0;
        while (it.hasNext()) {
            final Row row = it.next();
            final String status = (String)row.get("STATUS");
            if (!row.get("IP").toString().equals(DBUtil.config.ipaddr())) {
                ++totalCount;
            }
        }
        DBUtil.LOG.log(Level.FINER, "Number of nodes present in HA system :{0}", new Object[] { totalCount });
        if (totalCount > 1) {
            deleteServerStatusEntry();
            throw new IllegalStateException("Already two nodes present in the HA System with alive or serving state");
        }
    }
    
    private static void deleteServerStatusEntry() throws DataAccessException {
        final long serverId = Starter.getServerInstance().getServerID();
        DBUtil.LOG.log(Level.INFO, "Deleting host entry {0} in server status table. serverid: {0}", new Object[] { serverId });
        final Criteria cr = new Criteria(new Column("ServerStatus", "SERVERID"), (Object)serverId, 0);
        DataAccess.delete("ServerStatus", cr);
    }
    
    public static DataObject getMaxBuildNumberDO() throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        sq.addSelectColumn(new Column("FOSNodeDetails", "*"));
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        final Column col = new Column("FOSNodeDetails", "BUILD_NUMBER").maximum();
        subQuery.addSelectColumn(col);
        final Column dc = (Column)new DerivedColumn("BUILD_NUMBER", subQuery);
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "BUILD_NUMBER"), (Object)dc, 0);
        sq.setCriteria(c);
        return DataAccess.get(sq);
    }
    
    public static void removeEntry(final String ipaddr, final String hostName) throws DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        DBUtil.LOG.log(Level.INFO, "Deleteing entry of ip:[ {0} ] in HA table", new Object[] { ipaddr });
        DataAccess.delete("FOSNodeDetails", c);
        DBUtil.LOG.log(Level.INFO, "Deleting host entry {0} in server table", new Object[] { ipaddr });
        final Criteria cr = new Criteria(new Column("ServerStatus", "SERVERNAME"), (Object)hostName, 0);
        DataAccess.delete("ServerStatus", cr);
    }
    
    private static void checkInconsistency(final DataObject dobj) throws DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "STATUS"), (Object)"serving", 0);
        final Iterator it = dobj.getRows("FOSNodeDetails", c);
        int servingCnt = 0;
        while (it.hasNext()) {
            it.next();
            ++servingCnt;
        }
        if (servingCnt > 1) {
            throw new IllegalStateException("Two nodes cannot be in serving state..");
        }
    }
    
    protected static void updateCounter(final String ipaddr) throws InterruptedException {
        int retryCount = 0;
        while (true) {
            try {
                final DataObject dobj = DataAccess.get("FOSNodeDetails", (Criteria)null);
                checkInconsistency(dobj);
                final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
                final Row row = dobj.getRow("FOSNodeDetails", c);
                if (row == null) {
                    throw new IllegalStateException("Row not found for current ip :[" + ipaddr + "]. for updating counter");
                }
                if (row.get("STATUS").equals("down")) {
                    throw new IllegalStateException("Cannot update counter value for the row with 'down' status");
                }
                final long val = (long)row.get(2);
                DBUtil.LOG.log(Level.FINEST, "Updating heart beat. Value : [ {0} ]", new Object[] { val });
                row.set(2, (Object)(val + 1L));
                dobj.updateRow(row);
                DataAccess.update(dobj);
                DBUtil.LOG.log(Level.FINEST, "Row fetched::[ {0} ]", new Object[] { row.toString() });
            }
            catch (final DataAccessException exp) {
                if (++retryCount >= DBUtil.config.dbFailureRetryCount()) {
                    throw new RuntimeException("Updating counter in Table :: FOSNodeDetails failed after retryng " + DBUtil.config.dbFailureRetryCount() + " times.", (Throwable)exp);
                }
                DBUtil.LOG.log(Level.SEVERE, "Updating counter in HA table failed, Retrying after {0} seconds", new Object[] { DBUtil.config.dbRetryInterval() });
                Thread.sleep(DBUtil.config.dbRetryInterval() * 1000);
                continue;
            }
            break;
        }
    }
    
    protected static String getPeerStatus(final String peerIP) throws InterruptedException, DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)peerIP, 0);
        final DataObject d = getHADetails(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get(3).toString();
    }
    
    public static String getReplicationState(final String peerIP) throws InterruptedException, DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)peerIP, 0);
        final DataObject d = getHADetails(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get("REPLICATION_STATE").toString();
    }
    
    protected static long getCounterValue(final String ipaddr) throws DataAccessException, InterruptedException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        final DataObject d = getHADetails(c);
        if (d.isEmpty()) {
            return -1L;
        }
        return (long)d.getRow("FOSNodeDetails").get(2);
    }
    
    public static List getHealthStatus(final String ipaddr) throws HAException {
        try {
            final List<String> status = new ArrayList<String>();
            final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
            final DataObject d = getHADetails(c);
            if (d.isEmpty()) {
                status.add(null);
                status.add("-1");
            }
            else {
                status.add(d.getRow("FOSNodeDetails").get(3).toString());
                status.add(d.getRow("FOSNodeDetails").get(2).toString());
            }
            return status;
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new HAException(HAErrorCode.ERROR_GETTING_HEALTHSTATUS_FROM_DB, exp.getMessage());
        }
    }
    
    protected static String getServingNodeIP() throws InterruptedException, DataAccessException {
        Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)DBUtil.config.ipaddr(), 1);
        c = c.and(new Criteria(new Column("FOSNodeDetails", "STATUS"), (Object)"serving", 0));
        final DataObject d = getHADetails(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get(1).toString();
    }
    
    protected static String getOtherNodeIP(final boolean aliveNodesOnly) throws InterruptedException, DataAccessException, HAException {
        Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)DBUtil.config.ipaddr(), 1);
        if (aliveNodesOnly) {
            final Criteria c2 = new Criteria(new Column("FOSNodeDetails", "STATUS"), (Object)"alive", 0);
            Criteria c3 = new Criteria(new Column("FOSNodeDetails", "STATUS"), (Object)"serving", 0);
            c3 = c3.or(c2);
            c = c3.and(c);
        }
        final DataObject d = getHADetails(c);
        if (d.isEmpty()) {
            return null;
        }
        if (d.size("FOSNodeDetails") > 1) {
            throw new HAException(HAErrorCode.ERROR_MISC, "more than one (alive)node is returned :: {0}" + new Object[] { d });
        }
        return d.getRow("FOSNodeDetails").get("IP").toString();
    }
    
    protected static String getLastServingIP() throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        sq.addSelectColumn(new Column("FOSNodeDetails", "*"));
        final SortColumn sc = new SortColumn("FOSNodeDetails", "LASTSERVINGTIME", false, false, Boolean.valueOf(false));
        sq.addSortColumn(sc);
        final DataObject dobj = DataAccess.get(sq);
        DBUtil.LOG.log(Level.FINER, " Last serving ip : {0}", new Object[] { dobj });
        if (dobj.getRow("FOSNodeDetails") != null) {
            return (String)dobj.getRow("FOSNodeDetails").get("IP");
        }
        return null;
    }
    
    public static DataObject getHADetails(final Criteria c) throws InterruptedException {
        int retryCount = 0;
        try {
            return DataAccess.get("FOSNodeDetails", c);
        }
        catch (final Exception exp) {
            if (++retryCount >= DBUtil.config.dbFailureRetryCount()) {
                throw new RuntimeException("Get DO operation in Table :: FOSNodeDetails failed after retryng " + DBUtil.config.dbFailureRetryCount() + " times.", exp);
            }
            DBUtil.LOG.log(Level.SEVERE, "Get DO operation from HA table failed. Retrying after {0} seconds", new Object[] { DBUtil.config.dbRetryInterval() });
            Thread.sleep(DBUtil.config.dbRetryInterval() * 1000);
            return DataAccess.get("FOSNodeDetails", c);
        }
    }
    
    public static void updateBuildNumber(final String ipaddr, final Long number) throws DataAccessException {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        query.setCriteria(c);
        query.setUpdateColumn("BUILD_NUMBER", (Object)number);
        DBUtil.LOG.log(Level.FINER, "Update status for ip:[ {0} ] build_number: [ {1} ]", new Object[] { ipaddr, number });
        DataAccess.update(query);
    }
    
    public static void updateServingTime(final String ipaddr) throws DataAccessException {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        query.setCriteria(c1);
        final Column updCol = (Column)Column.createFunction("SYSDATE", new Object[0]);
        updCol.setType(93);
        DBUtil.LOG.log(Level.FINER, "Updating serving time for IPaddr :[ {0} ]  updcol :[ {1} ]", new Object[] { ipaddr, updCol });
        query.setUpdateColumn("LASTSERVINGTIME", (Object)updCol);
        DataAccess.update(query);
    }
    
    public static void updateReplState(final String ipaddr, final String status) throws DataAccessException {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        query.setCriteria(c1);
        query.setUpdateColumn("REPLICATION_STATE", (Object)status);
        DBUtil.LOG.log(Level.FINER, "updating replstate for ip :[ {0} ] status:[ {1} ]", new Object[] { ipaddr, status });
        DataAccess.update(query);
    }
    
    public static void updateStatus(final String ipaddr, final String status) throws DataAccessException {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)ipaddr, 0);
        query.setCriteria(c1);
        query.setUpdateColumn("STATUS", (Object)status);
        DBUtil.LOG.log(Level.FINER, "updating status for ip :[ {0} ] status:[ {1} ]", new Object[] { ipaddr, status });
        DataAccess.update(query);
    }
    
    static {
        LOG = Logger.getLogger(DBUtil.class.getName());
    }
}
