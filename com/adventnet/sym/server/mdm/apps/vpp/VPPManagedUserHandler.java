package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.UUID;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPAPIRequestGenerator;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import java.util.Set;
import java.util.Properties;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.List;
import java.util.logging.Logger;

public class VPPManagedUserHandler
{
    public Logger logger;
    private String className;
    private static VPPManagedUserHandler vppUserHandler;
    
    public VPPManagedUserHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.className = VPPManagedUserHandler.class.getName();
    }
    
    public static VPPManagedUserHandler getInstance() {
        if (VPPManagedUserHandler.vppUserHandler == null) {
            VPPManagedUserHandler.vppUserHandler = new VPPManagedUserHandler();
        }
        return VPPManagedUserHandler.vppUserHandler;
    }
    
    public List registerUserList(final List resourceIdList, final Long customerId, final Long businessStoreID) throws Exception {
        final String methodName = "registerUserList";
        HashMap<Long, Properties> userMap = this.addVppManagedUser(resourceIdList, businessStoreID);
        SyMLogger.info(this.logger, this.className, methodName, " Newly associated user details " + userMap);
        if (!userMap.isEmpty()) {
            userMap = this.registerUserListToVPP(userMap, customerId, businessStoreID);
            this.updateVppManagedUser(userMap, businessStoreID);
        }
        final Set notRegisteredUserSet = userMap.keySet();
        final List notRegisteredUserList = new ArrayList(notRegisteredUserSet);
        return notRegisteredUserList;
    }
    
    public Boolean retierUserToVPP(final Long vppUserId, final Long businessStoreID, final Long customerId) {
        Boolean isSuccess = false;
        Properties prop = new Properties();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getRetireUserCommand(vppUserId);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for retireUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "retireUserSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for retireUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "retireUserSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in retierUserToVPP ", e);
            ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 5);
            ((Hashtable<String, String>)prop).put("REMARKS", "dc.mdm.app.retier_vpp.problem_remarks");
        }
        final HashMap userMap = new HashMap();
        userMap.put(vppUserId, prop);
        if (userMap != null && !userMap.isEmpty()) {
            this.updateVppManagedUser(userMap, businessStoreID);
        }
        final int managedStatus = ((Hashtable<K, Integer>)prop).get("MANAGED_STATUS");
        if (managedStatus == 3) {
            isSuccess = true;
        }
        return isSuccess;
    }
    
    @Deprecated
    private HashMap registerUserListToVPP(final HashMap<Long, Properties> userMap, final Long customerId) {
        final Set resSet = userMap.keySet();
        return userMap;
    }
    
    private HashMap registerUserListToVPP(final HashMap<Long, Properties> userMap, final Long customerId, final Long businessStoreID) {
        final Set resSet = userMap.keySet();
        for (final Long resId : resSet) {
            Properties prop = userMap.get(resId);
            prop = this.registerUserToVPP(((Hashtable<K, String>)prop).get("VPP_CLIENT_USER_ID"), customerId, businessStoreID);
            userMap.put(resId, prop);
        }
        return userMap;
    }
    
    private Properties registerUserToVPP(final String clientUserIdStr, final Long customerId, final Long businessStoreID) {
        Properties prop = new Properties();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getRegisterUserCommand(clientUserIdStr, customerId);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for registerUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "registerUserSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for registerUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "registerUserSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in registerUserToVPP ", e);
            ((Hashtable<String, Integer>)prop).put("MANAGED_STATUS", 4);
            ((Hashtable<String, String>)prop).put("REMARKS", e.getMessage());
        }
        return prop;
    }
    
    public String getInvitationCode(final Long businessStoreID, final Long managedUserID) {
        String invitataionCode = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppUser"));
            selectQuery.addJoin(new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "INVITATION_CODE"));
            final Criteria resCriteria = new Criteria(new Column("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)managedUserID, 0);
            final Criteria businessCriteria = new Criteria(new Column("MdBusinessStoreToVppRel", "TOKEN_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(resCriteria.and(businessCriteria));
            final DataObject userDo = MDMUtil.getPersistence().get(selectQuery);
            if (!userDo.isEmpty()) {
                final Row userRow = userDo.getFirstRow("MdVppUser");
                invitataionCode = (String)userRow.get("INVITATION_CODE");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getInvitationCode", e);
        }
        return invitataionCode;
    }
    
    public DataObject getVppUserDO(final Criteria criteria) {
        DataObject vppUserDo = (DataObject)new WritableDataObject();
        try {
            final SelectQuery sQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsQuery();
            sQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppUser", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 1));
            sQuery.addJoin(new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 1));
            sQuery.addJoin(new Join("MdManagedUserToVppUserRel", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("MdVppUser", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdManagedUserToVppUserRel", "*"));
            sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            sQuery.setCriteria(criteria);
            vppUserDo = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppUserDO", e);
        }
        return vppUserDo;
    }
    
    @Deprecated
    private HashMap addVppManagedUser(final List<Long> resourceIdList) {
        final HashMap<Long, Properties> notRegisteredUserDetails = new HashMap<Long, Properties>();
        try {
            final Criteria resCriteria = new Criteria(new Column("MdVPPManagedUser", "MANAGED_USER_ID"), (Object)resourceIdList.toArray(), 8);
            final DataObject vppUserDo = MDMUtil.getPersistence().get("MdVPPManagedUser", resCriteria);
            final Iterator resIter = resourceIdList.iterator();
            final DataObject finalDO = MDMUtil.getPersistence().constructDataObject();
            while (resIter.hasNext()) {
                final Long resId = resIter.next();
                Row vppUserRow = vppUserDo.getRow("MdVPPManagedUser", new Criteria(new Column("MdVPPManagedUser", "MANAGED_USER_ID"), (Object)resId, 0));
                if (vppUserRow == null) {
                    final String clientUserId = this.getUniqueId();
                    vppUserRow = new Row("MdVPPManagedUser");
                    vppUserRow.set("MANAGED_USER_ID", (Object)resId);
                    vppUserRow.set("VPP_CLIENT_USER_ID", (Object)clientUserId);
                    vppUserRow.set("MANAGED_STATUS", (Object)0);
                    vppUserRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
                    finalDO.addRow(vppUserRow);
                    final Properties prop = new Properties();
                    ((Hashtable<String, String>)prop).put("VPP_CLIENT_USER_ID", clientUserId);
                    notRegisteredUserDetails.put(resId, prop);
                }
                else {
                    final Integer managedStatus = (Integer)vppUserRow.get("MANAGED_STATUS");
                    if (managedStatus == 1 || managedStatus == 2) {
                        continue;
                    }
                    final Properties prop = new Properties();
                    ((Hashtable<String, Object>)prop).put("VPP_CLIENT_USER_ID", vppUserRow.get("VPP_CLIENT_USER_ID"));
                    notRegisteredUserDetails.put(resId, prop);
                }
            }
            if (!finalDO.isEmpty()) {
                MDMUtil.getPersistence().add(finalDO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding VPPManagedUser table ", e);
        }
        return notRegisteredUserDetails;
    }
    
    private HashMap addVppManagedUser(final List<Long> resourceIdList, final Long businessStoreID) {
        final HashMap<Long, Properties> notRegisteredUserDetails = new HashMap<Long, Properties>();
        try {
            final Criteria businessCriteria = new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final DataObject vppUserDo = this.getVppUserDO(businessCriteria);
            final Join managedUserJoin = new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2);
            final Iterator resIter = resourceIdList.iterator();
            if (!vppUserDo.isEmpty()) {
                while (resIter.hasNext()) {
                    final Long resId = resIter.next();
                    final Criteria userIDCrit = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)resId, 0);
                    Row vppUserRow = vppUserDo.getRow("MdVppUser", userIDCrit, managedUserJoin);
                    final Row tokenRow = vppUserDo.getFirstRow("MdVPPTokenDetails");
                    final Long tokenID = (Long)tokenRow.get("TOKEN_ID");
                    if (vppUserRow == null) {
                        final String clientUserId = this.getUniqueId();
                        vppUserRow = new Row("MdVppUser");
                        vppUserRow.set("VPP_CLIENT_USER_ID", (Object)clientUserId);
                        vppUserRow.set("MANAGED_STATUS", (Object)0);
                        vppUserRow.set("TOKEN_ID", (Object)tokenID);
                        vppUserRow.set("IDENTIFIER", (Object)0);
                        vppUserRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
                        vppUserDo.addRow(vppUserRow);
                        final Row vppToManageduserRow = new Row("MdManagedUserToVppUserRel");
                        vppToManageduserRow.set("VPP_USER_ID", vppUserRow.get("VPP_USER_ID"));
                        vppToManageduserRow.set("MANAGED_USER_ID", (Object)resId);
                        vppUserDo.addRow(vppToManageduserRow);
                        MDMUtil.getPersistence().update(vppUserDo);
                        final Properties prop = new Properties();
                        ((Hashtable<String, String>)prop).put("VPP_CLIENT_USER_ID", clientUserId);
                        notRegisteredUserDetails.put(resId, prop);
                    }
                    else {
                        final Integer managedStatus = (Integer)vppUserRow.get("MANAGED_STATUS");
                        if (managedStatus == 1 || managedStatus == 2) {
                            continue;
                        }
                        final Properties prop2 = new Properties();
                        ((Hashtable<String, Object>)prop2).put("VPP_CLIENT_USER_ID", vppUserRow.get("VPP_CLIENT_USER_ID"));
                        notRegisteredUserDetails.put(resId, prop2);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding VPPManagedUser table ", e);
        }
        return notRegisteredUserDetails;
    }
    
    private String getUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    @Deprecated
    private void updateVppManagedUser(final HashMap<Long, Properties> userMap, final String criteriaColName) {
        final Set resId = userMap.keySet();
        final Criteria resCriteria = new Criteria(new Column("MdVPPManagedUser", criteriaColName), (Object)resId.toArray(), 8);
        try {
            final DataObject vppUserDo = MDMUtil.getPersistence().get("MdVPPManagedUser", resCriteria);
            final Iterator item = vppUserDo.getRows("MdVPPManagedUser");
            while (item.hasNext()) {
                final Row userRow = item.next();
                final Long userId = (Long)userRow.get(criteriaColName);
                final Properties prop = userMap.get(userId);
                final Long vppUserId = ((Hashtable<K, Long>)prop).get("VPP_USER_ID");
                if (vppUserId != null) {
                    userRow.set("VPP_USER_ID", (Object)vppUserId);
                }
                final Integer managedStatus = ((Hashtable<K, Integer>)prop).get("MANAGED_STATUS");
                if (managedStatus != null) {
                    userRow.set("MANAGED_STATUS", (Object)managedStatus);
                }
                final String invitationCode = ((Hashtable<K, String>)prop).get("INVITATION_CODE");
                if (invitationCode != null) {
                    userRow.set("INVITATION_CODE", (Object)invitationCode);
                }
                final String invitationUrl = ((Hashtable<K, String>)prop).get("INVITATION_URL");
                if (invitationCode != null) {
                    userRow.set("INVITATION_URL", (Object)invitationUrl);
                }
                final String remarks = ((Hashtable<K, String>)prop).get("REMARKS");
                if (remarks != null) {
                    userRow.set("REMARKS", (Object)remarks);
                }
                vppUserDo.updateRow(userRow);
            }
            MDMUtil.getPersistence().update(vppUserDo);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while update VppManagedUser table ", e);
        }
    }
    
    private void updateVppManagedUser(final HashMap<Long, Properties> userMap, final Long businessStoreID) {
        final Set resIds = userMap.keySet();
        try {
            final List resourcelist = new ArrayList();
            resourcelist.addAll(resIds);
            final Criteria businessCriteria = new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria managedUserCriteria = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)resourcelist.toArray(), 8);
            final DataObject vppUserDo = this.getVppUserDO(businessCriteria.and(managedUserCriteria));
            if (!vppUserDo.isEmpty()) {
                final Iterator item = vppUserDo.getRows("MdVppUser");
                final Iterator userIter = vppUserDo.getRows("ManagedUser");
                while (item.hasNext()) {
                    final Row vppUserRow = item.next();
                    final Row userRow = userIter.next();
                    final Long userId = (Long)userRow.get("MANAGED_USER_ID");
                    final Properties prop = userMap.get(userId);
                    final Integer managedStatus = ((Hashtable<K, Integer>)prop).get("MANAGED_STATUS");
                    if (managedStatus != null) {
                        vppUserRow.set("MANAGED_STATUS", (Object)managedStatus);
                    }
                    final String identifier = ((Hashtable<K, String>)prop).get("VPP_USER_ID");
                    if (identifier != null) {
                        vppUserRow.set("IDENTIFIER", (Object)identifier);
                    }
                    final String invitationCode = ((Hashtable<K, String>)prop).get("INVITATION_CODE");
                    if (invitationCode != null) {
                        vppUserRow.set("INVITATION_CODE", (Object)invitationCode);
                    }
                    final String invitationUrl = ((Hashtable<K, String>)prop).get("INVITATION_URL");
                    if (invitationCode != null) {
                        vppUserRow.set("INVITATION_URL", (Object)invitationUrl);
                    }
                    final String remarks = ((Hashtable<K, String>)prop).get("REMARKS");
                    if (remarks != null) {
                        vppUserRow.set("REMARKS", (Object)remarks);
                    }
                    vppUserDo.updateRow(vppUserRow);
                }
                MDMUtil.getPersistence().update(vppUserDo);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while update VppManagedUser table ", e);
        }
    }
    
    public List getVPPManagedUserDetails(final Criteria criteria) {
        final List userDetails = new ArrayList();
        try {
            final DataObject vppUserDo = MDMUtil.getPersistence().get("MdVPPManagedUser", criteria);
            final Iterator item = vppUserDo.getRows("MdVPPManagedUser");
            while (item.hasNext()) {
                final Row userRow = item.next();
                final Properties prop = new Properties();
                ((Hashtable<String, Object>)prop).put("MANAGED_USER_ID", userRow.get("MANAGED_USER_ID"));
                ((Hashtable<String, Object>)prop).put("VPP_USER_ID", userRow.get("VPP_USER_ID"));
                ((Hashtable<String, Object>)prop).put("VPP_CLIENT_USER_ID", userRow.get("VPP_CLIENT_USER_ID"));
                ((Hashtable<String, Object>)prop).put("MANAGED_STATUS", userRow.get("MANAGED_STATUS"));
                ((Hashtable<String, Object>)prop).put("INVITATION_CODE", userRow.get("INVITATION_CODE"));
                ((Hashtable<String, Object>)prop).put("INVITATION_URL", userRow.get("INVITATION_URL"));
                userDetails.add(prop);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while getVPPManagedUserDetails ", e);
        }
        return userDetails;
    }
    
    public void updateVPPUsersStatus(final Long businessStoreID, final String sToken, final Long customerId, final String batchToken) {
        HashMap<String, Object> responseMap = null;
        HashMap<Long, Properties> userMap = null;
        try {
            final String sinceModifiedToken = this.getUserSinceModifiedToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getVPPUsersCommand(batchToken, sinceModifiedToken);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for getUsersSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "getUsersSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for getUsersSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            responseMap = (HashMap)VPPResponseProcessor.getInstance().processResponse(responseJSON, "getUsersSrvUrl");
            if (responseMap != null) {
                userMap = responseMap.get("DETAILS_MAP");
                if (userMap != null && !userMap.isEmpty()) {
                    this.updateVppManagedUser(userMap, businessStoreID);
                }
                final String currentBatchToken = responseMap.get("batchToken");
                if (currentBatchToken != null) {
                    this.updateVPPUsersStatus(businessStoreID, sToken, customerId, currentBatchToken);
                }
                else {
                    final String currentSinceModifiedToken = responseMap.get("sinceModifiedToken");
                    if (currentSinceModifiedToken != null) {
                        this.setUserSinceModifiedToken(businessStoreID, currentSinceModifiedToken);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding updateVPPUsersStatus ", e);
        }
    }
    
    public String getUserSinceModifiedToken(final Long businessStoreID) {
        String sinceModifiedToken = null;
        try {
            final SelectQuery sinceModifiedTokenQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsQuery();
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "USER_SINCE_MODIFIED_TOKEN"));
            final Criteria businessStoreCriteria = new Criteria(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            sinceModifiedTokenQuery.setCriteria(businessStoreCriteria);
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sinceModifiedTokenQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("MdVPPTokenDetails");
                if (row != null) {
                    sinceModifiedToken = (String)row.get("USER_SINCE_MODIFIED_TOKEN");
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getUserSinceModifiedToken : ", (Throwable)ex);
        }
        return sinceModifiedToken;
    }
    
    public void setUserSinceModifiedToken(final Long businessStoreID, final String sinceModifiedToken) {
        try {
            final SelectQuery sinceModifiedTokenQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsQuery();
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "USER_SINCE_MODIFIED_TOKEN"));
            final Criteria businessStoreCriteria = new Criteria(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            sinceModifiedTokenQuery.setCriteria(businessStoreCriteria);
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sinceModifiedTokenQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("MdVPPTokenDetails");
                if (row != null) {
                    row.set("USER_SINCE_MODIFIED_TOKEN", (Object)sinceModifiedToken);
                    dataObject.updateRow(row);
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in setUserSinceModifiedToken : ", (Throwable)ex);
        }
    }
    
    public Properties getVPPUserManagedStatus(final Long vppUserId) {
        Properties prop = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppUser"));
            selectQuery.addJoin(new Join("MdVppUser", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "S_TOKEN"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdVppUser", "VPP_USER_ID"), (Object)vppUserId, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row vppUserRow = dataObject.getFirstRow("MdVppUser");
                final Long userIdentifier = (Long)vppUserRow.get("IDENTIFIER");
                final Row tokenRow = dataObject.getFirstRow("MdVPPTokenDetails");
                final String sToken = (String)tokenRow.get("S_TOKEN");
                final Row businessStoreRow = dataObject.getFirstRow("ManagedBusinessStore");
                final Long businessStoreID = (Long)businessStoreRow.get("BUSINESSSTORE_ID");
                final String command = new VPPAPIRequestGenerator(sToken).getVPPUserCommand(userIdentifier);
                final String dummyCommand = command.replace(sToken, "*****");
                this.logger.log(Level.INFO, "Request for getUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
                final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "getUserSrvUrl", sToken, businessStoreID);
                this.logger.log(Level.INFO, "Response for getUserSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
                prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "getUserSrvUrl");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in getVPPUserManagedStatus ", e);
        }
        return prop;
    }
    
    public Long getVPPUserId(final long userId) {
        Long vppUserId = null;
        try {
            vppUserId = (Long)DBUtil.getValueFromDB("MdVPPManagedUser", "MANAGED_USER_ID", (Object)userId, "VPP_USER_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in getVPPUserId ", e);
        }
        return vppUserId;
    }
    
    public Properties getVppUserIDToBusinessStoreMap(final Long managedUserID, final Long customerID) {
        final Properties vppUserProps = new Properties();
        DMDataSetWrapper dataSetWrapper = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            selectQuery.addJoin(new Join("ManagedUser", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria managedUserCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserID, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.setCriteria(managedUserCriteria.and(customerCriteria));
            dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSetWrapper != null) {
                while (dataSetWrapper.next()) {
                    final Long identifier = (Long)dataSetWrapper.getValue("IDENTIFIER");
                    final Long businessStoreID = (Long)dataSetWrapper.getValue("BUSINESSSTORE_ID");
                    ((Hashtable<Long, Long>)vppUserProps).put(identifier, businessStoreID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppUserToBusinessStoreMap");
        }
        return vppUserProps;
    }
    
    public Properties changeKeyToManagedUser(final Properties vppUserProp) {
        final Properties managedUserProp = new Properties();
        final Set vppUserIdSet = vppUserProp.keySet();
        final Criteria cVppUserId = new Criteria(new Column("MdVPPManagedUser", "VPP_USER_ID"), (Object)vppUserIdSet.toArray(), 8);
        final List userList = this.getVPPManagedUserDetails(cVppUserId);
        for (final Properties userProp : userList) {
            final Long vppUserId = ((Hashtable<K, Long>)userProp).get("VPP_USER_ID");
            final Long userId = ((Hashtable<K, Long>)userProp).get("MANAGED_USER_ID");
            ((Hashtable<Long, Object>)managedUserProp).put(userId, ((Hashtable<K, Object>)vppUserProp).get(vppUserId));
        }
        return managedUserProp;
    }
    
    public boolean checkIfUserNotAssociatedForVppApp(final Long deviceID, final Long appGroupID) {
        boolean status = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdVppAssetToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria deviceIDCriteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceID, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria userNotAssociatedCriteria = new Criteria(Column.getColumn("MdVppUser", "MANAGED_STATUS"), (Object)2, 1);
            selectQuery.setCriteria(deviceIDCriteria.and(appCriteria).and(userNotAssociatedCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            final DataObject vppUserDo = MDMUtil.getPersistence().get(selectQuery);
            if (!vppUserDo.isEmpty()) {
                status = true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkIfUserRegisteredForVppApp", e);
        }
        return status;
    }
    
    public String getInvitationURL(final Long deviceID, final Long appGroupID, final Long customerId) {
        String invitationURL = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdVppAssetToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "*"));
            final Criteria cDeviceIdInAppCatalog = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceID, 0);
            final Criteria cDeviceIdMappedToUser = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceID, 0);
            final Criteria appCriteria = new Criteria(new Column("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0);
            selectQuery.setCriteria(cDeviceIdInAppCatalog.and(cDeviceIdMappedToUser).and(appCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row vppUserRow = dataObject.getFirstRow("MdVppUser");
                final Integer vppUserStatus = (Integer)vppUserRow.get("MANAGED_STATUS");
                if (vppUserStatus != 2) {
                    final Long vppUserId = (Long)vppUserRow.get("VPP_USER_ID");
                    final Properties prop = this.getVPPUserManagedStatus(vppUserId);
                    final Integer crntStatus = ((Hashtable<K, Integer>)prop).get("MANAGED_STATUS");
                    if (crntStatus == 2) {
                        vppUserRow.set("MANAGED_STATUS", (Object)crntStatus);
                        vppUserRow.set("ITS_ID_HASH", ((Hashtable<K, Object>)prop).get("ITS_ID_HASH"));
                        dataObject.updateRow(vppUserRow);
                        MDMUtil.getPersistence().update(dataObject);
                        return "registeredAlready";
                    }
                    final String invitationcode = (String)vppUserRow.get("INVITATION_CODE");
                    if (invitationcode != null) {
                        invitationURL = VPPAppAPIRequestHandler.getInstance().getServiceUrl("invitationEmailUrl");
                        invitationURL = invitationURL.replace("%inviteCode%", invitationcode);
                    }
                }
                else if (vppUserStatus == 2) {
                    invitationURL = "registeredAlready";
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getInvitationURL", e);
        }
        return invitationURL;
    }
    
    public void clearAppLicenseForUser(final Long businessStoreID, final Long managedUserID) {
        final Properties assetLicenseCountMap = new Properties();
        try {
            this.logger.log(Level.INFO, "Starting to remove vpp user associations for businessStoreID {0}", new Object[] { businessStoreID });
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVppAssetToVppUserRel", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            final Join vppToManagedUserJoin = new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2);
            selectQuery.addJoin(vppToManagedUserJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToVppUserRel", "*"));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria managedUserCriteria = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)managedUserID, 0);
            selectQuery.setCriteria(businessStoreCriteria.and(managedUserCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            Long vppUserID = null;
            final Iterator iter = dataObject.getRows("MdVppAssetToVppUserRel");
            while (iter.hasNext()) {
                final Row assetToUserRow = iter.next();
                final Long assetID = (Long)assetToUserRow.get("VPP_ASSET_ID");
                if (vppUserID == null) {
                    vppUserID = (Long)assetToUserRow.get("VPP_USER_ID");
                }
                Integer count = ((Hashtable<K, Integer>)assetLicenseCountMap).get(assetID);
                if (count == null) {
                    count = 1;
                }
                else {
                    ++count;
                }
                dataObject.deleteRow(assetToUserRow);
                this.logger.log(Level.INFO, "Asset To User Row deleted: {0}", new Object[] { assetToUserRow });
                ((Hashtable<Long, Integer>)assetLicenseCountMap).put(assetID, count);
            }
            final Row vppUserRow = dataObject.getRow("MdVppUser", new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)managedUserID, 0), vppToManagedUserJoin);
            if (vppUserRow != null) {
                dataObject.deleteRow(vppUserRow);
            }
            this.updateLicenseCountForAssets(dataObject, assetLicenseCountMap);
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in clearAppLicenseForUser", e);
        }
    }
    
    private void updateLicenseCountForAssets(final DataObject dataObject, final Properties assetToLicenseCountMap) throws DataAccessException {
        for (final Long assetID : ((Hashtable<Object, V>)assetToLicenseCountMap).keySet()) {
            final Row assetRow = dataObject.getRow("MdVppAsset", new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
            int assignedLicenseCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT");
            int availableLicenseCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT");
            if (availableLicenseCount < 0) {
                this.logger.log(Level.WARNING, "Incorrect Asset Data: Available Licenses Count is less than zero for asset: {0}. Hence, ignoring update of licenses count", new Object[] { assetID });
            }
            else {
                availableLicenseCount += ((Hashtable<K, Integer>)assetToLicenseCountMap).get(assetID);
                assignedLicenseCount -= ((Hashtable<K, Integer>)assetToLicenseCountMap).get(assetID);
                assetRow.set("AVAILABLE_LICENSE_COUNT", (Object)availableLicenseCount);
                assetRow.set("ASSIGNED_LICENSE_COUNT", (Object)assignedLicenseCount);
                dataObject.updateRow(assetRow);
                this.logger.log(Level.INFO, "Updated Asset Row: {0}", new Object[] { assetRow });
            }
        }
    }
    
    public DataObject getVppUserToManagedUserDO(final Criteria criteria) {
        DataObject vppAssociatedUserDO = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            selectQuery.addJoin(new Join("ManagedUser", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppUser", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdManagedUserToVppUserRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.setCriteria(criteria);
            vppAssociatedUserDO = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppUserToManagedUserDO", e);
        }
        return vppAssociatedUserDO;
    }
    
    static {
        VPPManagedUserHandler.vppUserHandler = null;
    }
}
