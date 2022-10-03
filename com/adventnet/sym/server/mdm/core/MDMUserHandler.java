package com.adventnet.sym.server.mdm.core;

import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashSet;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.List;

public class MDMUserHandler
{
    public HashMap getUserIdsBasedOnType(final List userList, final boolean excludeTrashUsers) {
        final HashMap map = new HashMap();
        try {
            final SelectQuery managedUserSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            final SelectQuery directoryUserSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
            Criteria managedUserCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userList.toArray(), 8);
            if (excludeTrashUsers) {
                managedUserCriteria = managedUserCriteria.and(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1));
            }
            final Criteria directoryUserCriteria = new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)userList.toArray(), 8);
            managedUserSQ.setCriteria(managedUserCriteria);
            directoryUserSQ.setCriteria(directoryUserCriteria);
            managedUserSQ.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            directoryUserSQ.addSelectColumn(Column.getColumn("DirResRel", "RESOURCE_ID"));
            directoryUserSQ.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            final Set mdmManagedUser = new HashSet();
            final Set directoryManagedUser = new HashSet();
            DataObject obj = MDMUtil.getPersistence().get(managedUserSQ);
            Iterator rowIterator = obj.getRows("ManagedUser");
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final Long resourceId = (Long)row.get("MANAGED_USER_ID");
                mdmManagedUser.add(resourceId);
            }
            obj = MDMUtil.getPersistence().get(directoryUserSQ);
            rowIterator = obj.getRows("DirResRel");
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                directoryManagedUser.add(resourceId);
            }
            map.put(1, new ArrayList(mdmManagedUser));
            map.put(2, new ArrayList(directoryManagedUser));
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return map;
    }
    
    public HashMap getUserIdsBasedOnType(final List userList) {
        return this.getUserIdsBasedOnType(userList, true);
    }
    
    public HashMap getDirectoryUserIdsBasedOnType(final List userList) {
        final HashMap map = new HashMap();
        try {
            final SelectQuery directoryUserSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
            final Criteria directoryUserCriteria = new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)userList.toArray(), 8);
            directoryUserSQ.setCriteria(directoryUserCriteria);
            directoryUserSQ.addSelectColumn(Column.getColumn("DirResRel", "RESOURCE_ID"));
            directoryUserSQ.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            final DataObject obj = MDMUtil.getPersistence().get(directoryUserSQ);
            final Iterator itr = obj.getRows("DirResRel");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                final int type = 1;
                if (map.containsKey(type)) {
                    map.get(type).add(resourceId);
                }
                else {
                    final Set userSet = new HashSet();
                    userSet.add(resourceId);
                    map.put(type, userSet);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return map;
    }
    
    public void updateDeletedUserContact(final Long userID, final Long loginID) {
        try {
            Logger.getLogger("MDMLogger").log(Level.INFO, "Inside updateDeletedUserContact");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUserContactInfo"));
            sq.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userID, 0));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
                if (loginID != null && loginID.equals(defaultAdminUVHLoginID)) {
                    final Row userRow = dataObject.getFirstRow("AaaContactInfo");
                    userRow.set("EMAILID", (Object)"");
                    userRow.set("LANDLINE", (Object)"");
                    dataObject.updateRow(userRow);
                    DataAccess.update(dataObject);
                    Logger.getLogger("MDMLogger").log(Level.INFO, "AAAContactInfo Table IS updated for the user :{0}", userID);
                }
                else {
                    final Row userContactRow = dataObject.getFirstRow("AaaContactInfo");
                    dataObject.deleteRow(userContactRow);
                    DataAccess.update(dataObject);
                    Logger.getLogger("MDMLogger").log(Level.INFO, "AAAUserContactInfo Table IS deteled for the user :{0}", userID);
                }
            }
            else {
                Logger.getLogger("MDMLogger").log(Level.WARNING, "AAAUserContactInfo Table IS Empty for the user :{0}", userID);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.WARNING, "Exception while deleting the user details!");
        }
    }
    
    public DataObject getNotifyConfiguredForEmailDO(final List<String> email) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("EMailAddr"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "EMAIL_ADDR"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "EMAIL_ADDR_ID"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "MODULE"));
        Criteria criteria = new Criteria(Column.getColumn("EMailAddr", "EMAIL_ADDR"), (Object)email.toArray(), 8);
        criteria = criteria.and(new Criteria(Column.getColumn("EMailAddr", "MODULE"), (Object)"Mdm", 12, false));
        sq.setCriteria(criteria);
        final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
        return DO;
    }
    
    public JSONObject getNotifyConfiguredForEmail(final String email) throws Exception {
        final JSONObject json = new JSONObject();
        boolean depConfig = false;
        boolean vppConfig = false;
        boolean knoxConfig = false;
        boolean blacklistConfig = false;
        boolean apnsConfig = false;
        boolean scheduleReportConfig = false;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("EMailAddr"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "EMAIL_ADDR"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "EMAIL_ADDR_ID"));
        sq.addSelectColumn(Column.getColumn("EMailAddr", "MODULE"));
        Criteria criteria = new Criteria(Column.getColumn("EMailAddr", "EMAIL_ADDR"), (Object)email, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("EMailAddr", "MODULE"), (Object)"Mdm", 12, false));
        sq.setCriteria(criteria);
        final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
        if (!DO.isEmpty()) {
            final Iterator itr = DO.getRows("EMailAddr");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String module = (String)row.get("MODULE");
                if (module.equalsIgnoreCase("Mdm-VppAppAssgn")) {
                    vppConfig = true;
                }
                else if (module.equalsIgnoreCase("Mdm-DEP")) {
                    depConfig = true;
                }
                else if (module.equalsIgnoreCase("Mdm-Knox")) {
                    knoxConfig = true;
                }
                else {
                    if (!module.equalsIgnoreCase("Mdm-BlackListApp")) {
                        continue;
                    }
                    blacklistConfig = true;
                }
            }
        }
        final Object apnsobject = DBUtil.getValueFromDB("APNSCertificateDetails", "EMAIL_ADDRESS", (Object)email, "EMAIL_ADDRESS");
        if (apnsobject != null) {
            apnsConfig = true;
        }
        final Object reportsobjet = DBUtil.getValueFromDB("ScheduleRepTask", "EMAIL_ADDRESS", (Object)email, "EMAIL_ADDRESS");
        if (reportsobjet != null) {
            scheduleReportConfig = true;
        }
        json.put("depConfig", depConfig);
        json.put("vppConfig", vppConfig);
        json.put("knoxConfig", knoxConfig);
        json.put("blacklistConfig", blacklistConfig);
        json.put("apnsConfig", apnsConfig);
        json.put("scheduleReportConfig", scheduleReportConfig);
        return json;
    }
}
