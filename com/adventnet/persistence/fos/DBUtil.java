package com.adventnet.persistence.fos;

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
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil
{
    private static final Logger LOG;
    private static FOSConfig config;
    
    public static void initialize(final FOSConfig configuration) {
        if (DBUtil.config == null) {
            DBUtil.config = configuration;
        }
        else {
            DBUtil.LOG.log(Level.INFO, "DBUtil.config is already initialized.. hence ignoring");
        }
    }
    
    protected static void addentry(final String ipaddr) throws DataAccessException {
        final DataObject dobj = DataAccess.get("FOSNodeDetails", (Criteria)null);
        assertEntry(dobj);
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        final Row entry = dobj.getRow("FOSNodeDetails", c);
        if (entry == null) {
            DBUtil.LOG.log(Level.FINER, "Adding entry for IP [ {0} ]", new Object[] { ipaddr });
            final Row r = new Row("FOSNodeDetails");
            r.set("IP", ipaddr);
            r.set("COUNTER", 0);
            r.set("STATUS", "alive");
            r.set("REPLICATION_STATE", "none");
            r.set("BUILD_NUMBER", DBUtil.config.versionHandler().getCurrentBuildNumber());
            dobj.addRow(r);
        }
        else {
            DBUtil.LOG.log(Level.FINER, "Updating entry for IP [ {0} ]", new Object[] { ipaddr });
            entry.set(3, "alive");
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
        DBUtil.LOG.log(Level.INFO, "FOS config: " + DBUtil.config);
        final long currentBuildNumber = DBUtil.config.versionHandler().getCurrentBuildNumber();
        final Criteria cr = new Criteria(new Column("FOSNodeDetails", "IP"), DBUtil.config.ipaddr(), 0);
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
                throw new IllegalStateException("Invalid scenario. Cannot add the node with hidh build number than latestbuildnumber into FOS system");
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
        DBUtil.LOG.log(Level.FINER, "Number of nodes present in fos system :{0}", new Object[] { totalCount });
        if (totalCount > 1) {
            deleteServerStatusEntry();
            throw new IllegalStateException("Already two nodes present in the FOS System with alive or serving state");
        }
    }
    
    private static void deleteServerStatusEntry() throws DataAccessException {
        final long serverId = Starter.getServerInstance().getServerID();
        DBUtil.LOG.log(Level.INFO, "Deleting host entry {0} in server status table. serverid: {0}", new Object[] { serverId });
        final Criteria cr = new Criteria(new Column("ServerStatus", "SERVERID"), serverId, 0);
        DataAccess.delete("ServerStatus", cr);
    }
    
    public static DataObject getMaxBuildNumberDO() throws DataAccessException {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        sq.addSelectColumn(new Column("FOSNodeDetails", "*"));
        final SelectQuery subQuery = new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        final Column col = new Column("FOSNodeDetails", "BUILD_NUMBER").maximum();
        subQuery.addSelectColumn(col);
        final Column dc = new DerivedColumn("BUILD_NUMBER", subQuery);
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "BUILD_NUMBER"), dc, 0);
        sq.setCriteria(c);
        return DataAccess.get(sq);
    }
    
    protected static void removeEntry(final String ipaddr, final String hostName) throws DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        DBUtil.LOG.log(Level.INFO, "Deleteing entry of ip:[ {0} ] in fos table", new Object[] { ipaddr });
        DataAccess.delete("FOSNodeDetails", c);
        DBUtil.LOG.log(Level.INFO, "Deleting host entry {0} in server table", new Object[] { ipaddr });
        final Criteria cr = new Criteria(new Column("ServerStatus", "SERVERNAME"), hostName, 0);
        DataAccess.delete("ServerStatus", cr);
    }
    
    private static void checkInconsistency(final DataObject dobj) throws DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "STATUS"), "serving", 0);
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
                final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
                final Row row = dobj.getRow("FOSNodeDetails", c);
                if (row == null) {
                    throw new IllegalStateException("Row not found for current ip :[" + ipaddr + "]. for updating counter");
                }
                if (row.get("STATUS").equals("down")) {
                    throw new IllegalStateException("Cannot update counter value for the row with 'down' status");
                }
                final long val = (long)row.get(2);
                DBUtil.LOG.log(Level.FINEST, "Updating heart beat. Value : [ {0} ]", new Object[] { val });
                row.set(2, val + 1L);
                dobj.updateRow(row);
                DataAccess.update(dobj);
                DBUtil.LOG.log(Level.FINEST, "Row fetched::[ {0} ]", new Object[] { row.toString() });
            }
            catch (final DataAccessException exp) {
                if (++retryCount >= DBUtil.config.dbFailureRetryCount()) {
                    throw new RuntimeException("Updating counter in Table :: FOSNodeDetails failed after retryng " + DBUtil.config.dbFailureRetryCount() + " times.", exp);
                }
                DBUtil.LOG.log(Level.SEVERE, "Updating counter in fos table failed, Retrying after {0} seconds", new Object[] { DBUtil.config.dbRetryInterval() });
                Thread.sleep(DBUtil.config.dbRetryInterval() * 1000);
                continue;
            }
            break;
        }
    }
    
    protected static String getPeerStatus(final String peerIP) throws InterruptedException, DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), peerIP, 0);
        final DataObject d = getFOSDO(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get(3).toString();
    }
    
    protected static String getReplicationState(final String peerIP) throws InterruptedException, DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), peerIP, 0);
        final DataObject d = getFOSDO(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get("REPLICATION_STATE").toString();
    }
    
    protected static long getCounterValue(final String ipaddr) throws DataAccessException, InterruptedException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        final DataObject d = getFOSDO(c);
        if (d.isEmpty()) {
            return -1L;
        }
        return (long)d.getRow("FOSNodeDetails").get(2);
    }
    
    public static List getHealthStatus(final String ipaddr) throws FOSException {
        try {
            final List<String> status = new ArrayList<String>();
            final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
            final DataObject d = getFOSDO(c);
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
            throw new FOSException(FOSErrorCode.ERROR_GETTING_HEALTHSTATUS_FROM_DB, exp.getMessage());
        }
    }
    
    protected static String getServingNodeIP() throws InterruptedException, DataAccessException {
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "STATUS"), "serving", 0);
        final DataObject d = getFOSDO(c);
        if (d.isEmpty()) {
            return null;
        }
        return d.getRow("FOSNodeDetails").get(1).toString();
    }
    
    protected static String getOtherNodeIP(final boolean aliveNodesOnly) throws InterruptedException, DataAccessException, FOSException {
        Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), DBUtil.config.ipaddr(), 1);
        if (aliveNodesOnly) {
            final Criteria c2 = new Criteria(new Column("FOSNodeDetails", "STATUS"), "alive", 0);
            Criteria c3 = new Criteria(new Column("FOSNodeDetails", "STATUS"), "serving", 0);
            c3 = c3.or(c2);
            c = c3.and(c);
        }
        final DataObject d = getFOSDO(c);
        if (d.isEmpty()) {
            return null;
        }
        if (d.size("FOSNodeDetails") > 1) {
            throw new FOSException(FOSErrorCode.ERROR_MISC, "more than one (alive)node is returned :: {0}" + new Object[] { d });
        }
        return d.getRow("FOSNodeDetails").get("IP").toString();
    }
    
    protected static String getLastServingIP() throws DataAccessException {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable("FOSNodeDetails"));
        sq.addSelectColumn(new Column("FOSNodeDetails", "*"));
        final SortColumn sc = new SortColumn("FOSNodeDetails", "LASTSERVINGTIME", false);
        sq.addSortColumn(sc);
        final DataObject dobj = DataAccess.get(sq);
        DBUtil.LOG.log(Level.FINER, " Last serving ip : {0}", new Object[] { dobj });
        if (dobj.getRow("FOSNodeDetails") != null) {
            return (String)dobj.getRow("FOSNodeDetails").get("IP");
        }
        return null;
    }
    
    protected static DataObject getFOSDO(final Criteria c) throws InterruptedException {
        int retryCount = 0;
        try {
            return DataAccess.get("FOSNodeDetails", c);
        }
        catch (final Exception exp) {
            if (++retryCount >= DBUtil.config.dbFailureRetryCount()) {
                throw new RuntimeException("Get DO operation in Table :: FOSNodeDetails failed after retryng " + DBUtil.config.dbFailureRetryCount() + " times.", exp);
            }
            DBUtil.LOG.log(Level.SEVERE, "Get DO operation from fos table failed. Retrying after {0} seconds", new Object[] { DBUtil.config.dbRetryInterval() });
            Thread.sleep(DBUtil.config.dbRetryInterval() * 1000);
            return DataAccess.get("FOSNodeDetails", c);
        }
    }
    
    public static void updateBuildNumber(final String ipaddr, final Long number) throws DataAccessException {
        final UpdateQuery query = new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        query.setCriteria(c);
        query.setUpdateColumn("BUILD_NUMBER", number);
        DBUtil.LOG.log(Level.FINER, "Update status for ip:[ {0} ] build_number: [ {1} ]", new Object[] { ipaddr, number });
        DataAccess.update(query);
    }
    
    public static void updateServingTime(final String ipaddr) throws DataAccessException {
        final UpdateQuery query = new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        query.setCriteria(c1);
        final Column updCol = Column.createFunction("SYSDATE", new Object[0]);
        updCol.setType(93);
        DBUtil.LOG.log(Level.FINER, "Updating serving time for IPaddr :[ {0} ]  updcol :[ {1} ]", new Object[] { ipaddr, updCol });
        query.setUpdateColumn("LASTSERVINGTIME", updCol);
        DataAccess.update(query);
    }
    
    protected static void updateReplState(final String ipaddr, final String status) throws DataAccessException {
        final UpdateQuery query = new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        query.setCriteria(c1);
        query.setUpdateColumn("REPLICATION_STATE", status);
        DBUtil.LOG.log(Level.FINER, "updating replstate for ip :[ {0} ] status:[ {1} ]", new Object[] { ipaddr, status });
        DataAccess.update(query);
    }
    
    protected static void updateStatus(final String ipaddr, final String status) throws DataAccessException {
        final UpdateQuery query = new UpdateQueryImpl("FOSNodeDetails");
        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), ipaddr, 0);
        query.setCriteria(c1);
        query.setUpdateColumn("STATUS", status);
        DBUtil.LOG.log(Level.FINER, "updating status for ip :[ {0} ] status:[ {1} ]", new Object[] { ipaddr, status });
        DataAccess.update(query);
    }
    
    static {
        LOG = Logger.getLogger(DBUtil.class.getName());
    }
}
