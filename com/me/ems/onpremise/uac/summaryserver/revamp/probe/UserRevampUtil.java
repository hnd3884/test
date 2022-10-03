package com.me.ems.onpremise.uac.summaryserver.revamp.probe;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.List;
import java.util.Arrays;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

public class UserRevampUtil
{
    private static final String OLD_ID = "oldId";
    private static final String NEW_ID = "newId";
    String adminUserIdPattern;
    String adminLoginIdPattern;
    String dcUserIdPattern;
    String sdpUserIdPattern;
    String dummyUserIdPattern;
    String dummyLoginIdPattern;
    Map<Long, Long> newUserIds;
    Map<Long, Long> newLoginIds;
    Map<Object, Object> userStatus;
    Long oldAdminUserId;
    Long ssAdminUserId;
    Long oldDCUserId;
    Long ssDCUserId;
    Long oldSDPUserId;
    Long ssSDPUserId;
    Long oldDummyUserId;
    Long ssDummyUserId;
    Long oldAdminLoginId;
    Long ssAdminLoginId;
    Long oldDummyLoginId;
    Long ssDummyLoginId;
    private static final Logger REVAMP_LOGGER;
    
    public UserRevampUtil(final Map<String, Map<Object, Object>> oldIdToNewIdMap) {
        this.adminUserIdPattern = "AaaUser:user_id:0".replace(":", "-");
        this.adminLoginIdPattern = "AaaLogin:login_id:0".replace(":", "-");
        this.dcUserIdPattern = "AaaUser:user_id:1".replace(":", "-");
        this.sdpUserIdPattern = "AaaUser:user_id:2".replace(":", "-");
        this.dummyUserIdPattern = "AaaUser:user_id:3".replace(":", "-");
        this.dummyLoginIdPattern = "AaaLogin:login_id:3".replace(":", "-");
        this.newUserIds = new HashMap<Long, Long>();
        this.newLoginIds = new HashMap<Long, Long>();
        this.oldAdminUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.adminUserIdPattern).get("oldId")));
        this.ssAdminUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.adminUserIdPattern).get("newId")));
        this.oldDCUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dcUserIdPattern).get("oldId")));
        this.ssDCUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dcUserIdPattern).get("newId")));
        this.oldSDPUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.sdpUserIdPattern).get("oldId")));
        this.ssSDPUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.sdpUserIdPattern).get("newId")));
        this.oldDummyUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dummyUserIdPattern).get("oldId")));
        this.ssDummyUserId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dummyUserIdPattern).get("newId")));
        this.newUserIds.put(this.oldAdminUserId, this.ssAdminUserId);
        this.newUserIds.put(this.oldDCUserId, this.ssDCUserId);
        this.newUserIds.put(this.oldSDPUserId, this.ssSDPUserId);
        this.newUserIds.put(this.oldDummyUserId, this.ssDummyUserId);
        this.oldAdminLoginId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.adminLoginIdPattern).get("oldId")));
        this.ssAdminLoginId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.adminLoginIdPattern).get("newId")));
        this.oldDummyLoginId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dummyLoginIdPattern).get("oldId")));
        this.ssDummyLoginId = Long.parseLong(String.valueOf(oldIdToNewIdMap.get(this.dummyLoginIdPattern).get("newId")));
        this.newLoginIds.put(this.oldAdminLoginId, this.ssAdminLoginId);
        this.newLoginIds.put(this.oldDummyLoginId, this.ssDummyLoginId);
        this.userStatus = oldIdToNewIdMap.get("userStatus");
    }
    
    public void repopulateDefaultUsers() throws Exception {
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Domain Name Change for Old Default Users Begins");
        DefaultUsersAndRolesRevampUtil.updateTable("AaaLogin", "DOMAINNAME", "-", "DOMAINNAME", "old");
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Domain Name Changed for Old Default Users Successfully\n");
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Default Users with SS Ids Creation Begins");
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row aaaUserAdmin = new Row("AaaUser");
        aaaUserAdmin.set("USER_ID", (Object)this.ssAdminUserId);
        aaaUserAdmin.set("FIRST_NAME", (Object)"admin");
        aaaUserAdmin.set("CREATEDTIME", (Object)System.currentTimeMillis());
        final Row aaaUserDC = new Row("AaaUser");
        aaaUserDC.set("USER_ID", (Object)this.ssDCUserId);
        aaaUserDC.set("FIRST_NAME", (Object)"DC-SYSTEM-USER");
        aaaUserDC.set("CREATEDTIME", (Object)System.currentTimeMillis());
        final Row aaaUserSDP = new Row("AaaUser");
        aaaUserSDP.set("USER_ID", (Object)this.ssSDPUserId);
        aaaUserSDP.set("FIRST_NAME", (Object)"SDP-SYSTEM-USER");
        aaaUserSDP.set("CREATEDTIME", (Object)System.currentTimeMillis());
        final Row aaaUserDummy = new Row("AaaUser");
        aaaUserDummy.set("USER_ID", (Object)this.ssDummyUserId);
        aaaUserDummy.set("FIRST_NAME", (Object)"dummy");
        aaaUserDummy.set("CREATEDTIME", (Object)System.currentTimeMillis());
        dataObject.addRow(aaaUserAdmin);
        dataObject.addRow(aaaUserDC);
        dataObject.addRow(aaaUserSDP);
        dataObject.addRow(aaaUserDummy);
        final Row aaaLoginAdmin = new Row("AaaLogin");
        aaaLoginAdmin.set("LOGIN_ID", (Object)this.ssAdminLoginId);
        aaaLoginAdmin.set("USER_ID", (Object)this.ssAdminUserId);
        aaaLoginAdmin.set("NAME", (Object)"admin");
        final Row aaaLoginDummy = new Row("AaaLogin");
        aaaLoginDummy.set("LOGIN_ID", (Object)this.ssDummyLoginId);
        aaaLoginDummy.set("USER_ID", (Object)this.ssDummyUserId);
        aaaLoginDummy.set("NAME", (Object)"dummy");
        dataObject.addRow(aaaLoginAdmin);
        dataObject.addRow(aaaLoginDummy);
        final Row newAdminUserRow = new Row("AaaUserStatus");
        newAdminUserRow.set("USER_ID", (Object)this.ssAdminUserId);
        newAdminUserRow.set("STATUS", this.userStatus.get(this.ssAdminUserId.toString()));
        newAdminUserRow.set("UPDATEDTIME", (Object)System.currentTimeMillis());
        dataObject.addRow(newAdminUserRow);
        final Row newDcUserRow = new Row("AaaUserStatus");
        newDcUserRow.set("USER_ID", (Object)this.ssDCUserId);
        newDcUserRow.set("STATUS", this.userStatus.get(this.ssDCUserId.toString()));
        newDcUserRow.set("UPDATEDTIME", (Object)System.currentTimeMillis());
        dataObject.addRow(newDcUserRow);
        final Row newSdpUserRow = new Row("AaaUserStatus");
        newSdpUserRow.set("USER_ID", (Object)this.ssSDPUserId);
        newSdpUserRow.set("STATUS", this.userStatus.get(this.ssSDPUserId.toString()));
        newSdpUserRow.set("UPDATEDTIME", (Object)System.currentTimeMillis());
        dataObject.addRow(newSdpUserRow);
        final Row newDummyUserRow = new Row("AaaUserStatus");
        newDummyUserRow.set("USER_ID", (Object)this.ssDummyUserId);
        newDummyUserRow.set("STATUS", this.userStatus.get(this.ssDummyUserId.toString()));
        newDummyUserRow.set("UPDATEDTIME", (Object)System.currentTimeMillis());
        dataObject.addRow(newDummyUserRow);
        SyMUtil.getPersistence().add(dataObject);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Default Users with SS Ids Created Successfully\n");
        this.updateDependantTables();
    }
    
    private void updateDependantTables() throws Exception {
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Dependency Table Data Revamp Action Begins\n\n");
        final List<String> aaaUserExcludeChildTables = Arrays.asList("AaaUserStatus", "AaaLogin");
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "---------------AAALOGIN DEPENDENCY TABLE CHANGES--------------");
        final Map<String, List<String>> aaaLoginChildTables = DefaultUsersAndRolesRevampUtil.getChildTableColumns("AaaLogin", "LOGIN_ID", null);
        aaaLoginChildTables.put("TechnicianScopeRel", Arrays.asList("TECH_ID"));
        DefaultUsersAndRolesRevampUtil.updateChildTables(aaaLoginChildTables, this.newLoginIds);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "---------------AAAUSER DEPENDENCY TABLE CHANGES---------------");
        final Map<String, List<String>> aaaUserChildTables = DefaultUsersAndRolesRevampUtil.getChildTableColumns("AaaUser", "USER_ID", aaaUserExcludeChildTables);
        DefaultUsersAndRolesRevampUtil.updateChildTables(aaaUserChildTables, this.newUserIds);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Dependency Table Data Revamp Action Completed Successfully\n");
        this.deleteOldUsers();
    }
    
    private void deleteOldUsers() throws DataAccessException {
        final String[] oldUserIdsArray = { this.oldAdminUserId + "", this.oldDCUserId + "", this.oldSDPUserId + "", this.oldDummyUserId + "" };
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Old Default Users Delete Action Begins");
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaUser");
        final Criteria delCrit = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)oldUserIdsArray, 8);
        deleteQuery.setCriteria(delCrit);
        DataAccess.delete(deleteQuery);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Old Default Users Deleted Successfully");
    }
    
    public void disableOldUsers() throws DataAccessException {
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Old Default Users Disable Action Begins");
        final String[] oldIdArray = { this.oldAdminUserId + "", this.oldDCUserId + "", this.oldSDPUserId + "" };
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserStatus"));
        selectQuery.addSelectColumn(new Column("AaaUserStatus", "USER_ID"));
        selectQuery.addSelectColumn(new Column("AaaUserStatus", "STATUS"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AaaUserStatus", "USER_ID"), (Object)oldIdArray, 8));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("AaaUserStatus");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                row.set("STATUS", (Object)"DISABLED");
                dataObject.updateRow(row);
            }
        }
        SyMUtil.getPersistence().update(dataObject);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Old Default Users Disabled Successfully");
    }
    
    public void updateUserUVHValues(final Map<String, Map<Object, Object>> oldIdToNewIdMap) throws DataAccessException {
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "UVH Values Update Action Begins");
        final String[] tableNames = { "AaaUser", "AaaLogin" };
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Updating Users UVH Values..............");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
        selectQuery.addSelectColumn(new Column("UVHValues", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)tableNames, 8));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("UVHValues");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String pattern = (row.get("PATTERN") + "").replace(":", "-");
                row.set("GENVALUES", oldIdToNewIdMap.get(pattern).get("newId"));
                dataObject.updateRow(row);
            }
        }
        DataAccess.update(dataObject);
        UserRevampUtil.REVAMP_LOGGER.log(Level.INFO, "UVH Values Update Action Completed Successfully\n");
    }
    
    static {
        REVAMP_LOGGER = Logger.getLogger("ProbeServerRevampLogger");
    }
}
