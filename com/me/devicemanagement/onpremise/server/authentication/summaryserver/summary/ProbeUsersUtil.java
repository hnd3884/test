package com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Logger;

public class ProbeUsersUtil
{
    private static final Logger LOGGER;
    
    private ProbeUsersUtil() {
    }
    
    public static void addProbeUsers(final Long loginID, final String[] probeList) throws DataAccessException {
        try {
            if (probeList != null && probeList.length > 0) {
                final WritableDataObject probeUsersDO = new WritableDataObject();
                for (final String probeId : probeList) {
                    final Row probeUsers = new Row("ProbeUsers");
                    probeUsers.set("LOGIN_ID", (Object)loginID);
                    probeUsers.set("PROBE_ID", (Object)Long.parseLong(probeId));
                    probeUsers.set("PROBE_STATE", (Object)0);
                    probeUsersDO.addRow(probeUsers);
                }
                SyMUtil.getPersistence().add((DataObject)probeUsersDO);
            }
        }
        catch (final DataAccessException ex) {
            ProbeUsersUtil.LOGGER.log(Level.SEVERE, "Exception Occurred in ProbeUsersUtil.addProbeUsers()");
            throw ex;
        }
    }
    
    public static void updateProbeUsers(final Long loginID, final String[] probeList) throws DataAccessException {
        try {
            if (probeList != null && probeList.length > 0) {
                final Criteria probeUserCriteria = new Criteria(new Column("ProbeUsers", "LOGIN_ID"), (Object)loginID, 0);
                SyMUtil.getPersistence().delete(probeUserCriteria);
                final WritableDataObject probeUsersDO = new WritableDataObject();
                for (final String probeId : probeList) {
                    final Row probeUsers = new Row("ProbeUsers");
                    probeUsers.set("LOGIN_ID", (Object)loginID);
                    probeUsers.set("PROBE_ID", (Object)probeId);
                    probeUsers.set("PROBE_STATE", (Object)0);
                    probeUsersDO.addRow(probeUsers);
                }
                SyMUtil.getPersistence().add((DataObject)probeUsersDO);
            }
        }
        catch (final Exception ex) {
            ProbeUsersUtil.LOGGER.log(Level.SEVERE, "Exception Occurred in ProbeUsersUtil.updateProbeUsers()");
            throw ex;
        }
    }
    
    public static List<Long> getProbeIdsForLoginId(final Long loginId) {
        final List<Long> probeIds = new ArrayList<Long>();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("ProbeUsers"));
            final Criteria criteria = new Criteria(Column.getColumn("ProbeUsers", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get((SelectQuery)selectQuery);
            final Iterator<Row> iterator = dataObject.getRows("ProbeUsers");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                probeIds.add(Long.parseLong(row.get("PROBE_ID").toString()));
            }
        }
        catch (final Exception ex) {
            ProbeUsersUtil.LOGGER.log(Level.SEVERE, ex, () -> "Exception while fetching mapped Probe Ids for login Id: " + n);
        }
        return probeIds;
    }
    
    public static int getTechniciansCountForProbe(final Long probeId) {
        int techCount = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProbeUsers"));
            selectQuery.addJoin(new Join("ProbeUsers", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("ProbeUsers", "*"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("AaaUser", "*"));
            selectQuery.addSelectColumn(new Column("AaaUserStatus", "*"));
            final Criteria probeCriteria = new Criteria(new Column("ProbeUsers", "PROBE_ID"), (Object)probeId, 0);
            final Criteria statusCriteria = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"DISABLED", 1);
            selectQuery.setCriteria(probeCriteria.and(statusCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            techCount = (dataObject.isEmpty() ? techCount : dataObject.size("ProbeUsers"));
        }
        catch (final Exception exception) {
            ProbeUsersUtil.LOGGER.log(Level.WARNING, "Exception while getting count of Users in a Probe", exception);
        }
        return techCount;
    }
    
    public static List<Long> getLoginIdsForProbeId(final Long probeId) {
        final List<Long> technicians = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProbeUsers"));
            selectQuery.addJoin(new Join("ProbeUsers", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("ProbeUsers", "*"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("AaaUser", "*"));
            selectQuery.addSelectColumn(new Column("AaaUserStatus", "*"));
            final Criteria probeCriteria = new Criteria(new Column("ProbeUsers", "PROBE_ID"), (Object)probeId, 0);
            final Criteria statusCriteria = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"DISABLED", 1);
            selectQuery.setCriteria(probeCriteria.and(statusCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> iterator = dataObject.getRows("ProbeUsers");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                technicians.add(Long.parseLong(row.get("LOGIN_ID") + ""));
            }
        }
        catch (final Exception exception) {
            ProbeUsersUtil.LOGGER.log(Level.WARNING, "Exception while getting Users in the Probe", exception);
        }
        return technicians;
    }
    
    public List<Long> getAllProbeUsers() throws DataAccessException {
        final int ALL_PROBE_SCOPE = 1;
        return getProbeScopeBasedUsers(1);
    }
    
    public List<Long> getSpecificProbeUsers() throws DataAccessException {
        final int SPECIFIC_PROBE_SCOPE = 2;
        return getProbeScopeBasedUsers(2);
    }
    
    public static List<Long> getProbeScopeBasedUsers(final int probeScope) throws DataAccessException {
        final List<Long> probeUsers = new ArrayList<Long>();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("AaaLoginExtn"));
            selectQuery.addJoin(new Join("AaaLoginExtn", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            final Criteria probeScopeCriteria = new Criteria(Column.getColumn("AaaLoginExtn", "PROBE_SCOPE"), (Object)probeScope, 0);
            final Criteria activeUserCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
            selectQuery.setCriteria(probeScopeCriteria.and(activeUserCriteria));
            selectQuery.addSelectColumn(new Column("AaaLoginExtn", "*"));
            final DataObject dataObject = DataAccess.get((SelectQuery)selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("AaaLoginExtn");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    probeUsers.add(Long.parseLong(row.get("LOGIN_ID") + ""));
                }
            }
        }
        catch (final Exception ex) {
            ProbeUsersUtil.LOGGER.log(Level.SEVERE, () -> "Exception while fetching Users with scope : " + n);
            throw ex;
        }
        return probeUsers;
    }
    
    public static boolean isUserManagingAllProbes(final Long loginID) {
        return DMUserHandler.isUserInRole(loginID, "All_Managed_Probes");
    }
    
    public static void probeUserCriteria(final SelectQuery selectQuery, final Long loginId) {
        selectQuery.addJoin(new Join("ProbeDetails", "ProbeUsers", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2));
        selectQuery.addJoin(new Join("ProbeUsers", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        final Criteria probeCriteria = new Criteria(new Column("ProbeUsers", "LOGIN_ID"), (Object)loginId, 0);
        final Criteria statusCriteria = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"DISABLED", 1);
        final Criteria criteria = selectQuery.getCriteria();
        if (criteria != null) {
            selectQuery.setCriteria(criteria.and(probeCriteria.and(statusCriteria)));
        }
        else {
            selectQuery.setCriteria(probeCriteria.and(statusCriteria));
        }
    }
    
    public static int getUserProbeScopeType(final long loginId) throws Exception {
        return (int)DBUtil.getValueFromDB("AaaLoginExtn", "LOGIN_ID", (Object)loginId, "PROBE_SCOPE");
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
