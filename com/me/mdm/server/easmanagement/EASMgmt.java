package com.me.mdm.server.easmanagement;

import com.me.mdm.server.metracker.METrackParamManager;
import org.json.simple.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Map;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class EASMgmt
{
    private static EASMgmt easMgmt;
    public static Logger logger;
    
    public static EASMgmt getInstance() {
        if (EASMgmt.easMgmt == null) {
            EASMgmt.easMgmt = new EASMgmt();
        }
        return EASMgmt.easMgmt;
    }
    
    public String getCurrentlyLoggedInUserName() {
        try {
            return MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
            return "---";
        }
    }
    
    public void addTaskToQueue(final JSONObject qData) {
        try {
            qData.put((Object)"CURRENTLY_LOGGED_IN_USER", (Object)this.getCurrentlyLoggedInUserName());
            final DCQueue queue = DCQueueHandler.getQueue("eas-task");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = qData;
            queue.addToQueue(queueData);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public JSONObject getCEAdetails(final JSONObject requestDetails) {
        final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        exchangeServerDetails.put((Object)"bulkAssignEasUsers", (Object)MDMFeatureParamsHandler.getInstance().isFeatureEnabled("bulkAssignEasUsers"));
        final Long esServerID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
        boolean getBasicDetails = false;
        boolean getPSsessionStatus = false;
        boolean getSelectedMailboxes = false;
        boolean getCEArollbackDate = false;
        if (requestDetails.containsKey((Object)"EASServerDetails")) {
            getBasicDetails = Boolean.valueOf((String)requestDetails.get((Object)"EASServerDetails"));
        }
        if (requestDetails.containsKey((Object)"SESSION_STATUS")) {
            getPSsessionStatus = Boolean.valueOf((String)requestDetails.get((Object)"SESSION_STATUS"));
        }
        if (requestDetails.containsKey((Object)"EASSelectedMailbox")) {
            getSelectedMailboxes = Boolean.valueOf((String)requestDetails.get((Object)"EASSelectedMailbox"));
        }
        if (requestDetails.containsKey((Object)"CEAAudit")) {
            getCEArollbackDate = Boolean.valueOf((String)requestDetails.get((Object)"CEAAudit"));
        }
        final JSONObject response = new JSONObject();
        if (getBasicDetails) {
            response.putAll((Map)exchangeServerDetails);
        }
        if (getPSsessionStatus) {
            response.put((Object)"SESSION_STATUS", (Object)MDMApiFactoryProvider.getConditionalExchangeAccessApi().getPSSstate(esServerID));
        }
        if (getSelectedMailboxes) {
            response.putAll((Map)EASMgmtDataHandler.getInstance().getPolicySelectedOrExcludedMailboxes(exchangeServerDetails));
        }
        if (getCEArollbackDate) {
            String rollbackStartDate = EASMgmtDataHandler.getInstance().getCEArollbackInstalledDate();
            MDMUtil.getInstance();
            if (MDMUtil.isStringValid(rollbackStartDate)) {
                rollbackStartDate = " " + MDMI18N.getI18Nmsg("dc.rep.customReport.after") + " <span style=\"font-weight: bold;\">" + rollbackStartDate + "</span> ";
            }
            else {
                rollbackStartDate = " ";
            }
            response.put((Object)"CEAAudit", (Object)rollbackStartDate);
        }
        return response;
    }
    
    public void handleSyncRequest(final JSONObject requestDetails) {
        EASMgmt.logger.log(Level.INFO, "request Details : {0}", requestDetails.toString());
        final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        final Long easServerID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
        if (easServerID == null) {
            return;
        }
        Boolean sendGraceMails = Boolean.FALSE;
        if (requestDetails.containsKey((Object)"SEND_GRACE_MAILS")) {
            sendGraceMails = (Boolean)requestDetails.get((Object)"SEND_GRACE_MAILS");
        }
        final Long syncAttemptTime = System.currentTimeMillis();
        final JSONObject ceaSyncDetails = new JSONObject();
        ceaSyncDetails.put((Object)"ERROR_CODE", (Object)(-1));
        ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
        ceaSyncDetails.put((Object)"SYNC_STATUS", (Object)2);
        ceaSyncDetails.put((Object)"LAST_ATTEMPTED_SYNC_TASK", (Object)syncAttemptTime);
        ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg("mdm.cea.config.progress", new Object[] { exchangeServerDetails.get((Object)"CONNECTION_URI") }));
        EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(ceaSyncDetails);
        final JSONObject easPropsJSON = new JSONObject();
        easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
        easPropsJSON.put((Object)"SEND_GRACE_MAILS", (Object)sendGraceMails);
        easPropsJSON.put((Object)"install_exo_v2", requestDetails.getOrDefault((Object)"install_exo_v2", (Object)false));
        easPropsJSON.put((Object)"TASK_TYPE", (Object)"EXCHANGE_SERVER_DETAILS_REQUEST");
        easPropsJSON.put((Object)"LAST_ATTEMPTED_SYNC_TASK", (Object)syncAttemptTime);
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easPropsJSON);
    }
    
    public void handleEnrollment(final JSONObject easPropsJSON) throws SyMException, DataAccessException {
        EASMgmt.logger.log(Level.INFO, "request Details : {0}", easPropsJSON.toString());
        final Long customerID = (Long)easPropsJSON.get((Object)"CUSTOMER_ID");
        final String enrolledUserDomainName = (String)easPropsJSON.get((Object)"DOMAIN");
        final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProp(enrolledUserDomainName, customerID, (List)new ArrayList(Arrays.asList(3, 2)));
        final String adDomainName = dmDomainProps.getProperty("AD_DOMAIN_NAME");
        final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        final String easADdomainName = (String)exchangeServerDetails.get((Object)"DOMAIN");
        if (SyMUtil.isStringValid(easADdomainName) && easADdomainName.equalsIgnoreCase(adDomainName)) {
            final Long esServerID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
            easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)esServerID);
            easPropsJSON.put((Object)"CURRENTLY_LOGGED_IN_USER", (Object)this.getCurrentlyLoggedInUserName());
            easPropsJSON.put((Object)"TASK_TYPE", (Object)"ENROLLMENT_TASK");
            getInstance().addTaskToQueue(easPropsJSON);
        }
    }
    
    public void handleContionalAccess(final Long easServerID, final JSONObject jsObject) {
        try {
            final JSONObject easPolicyStatus = new JSONObject();
            easPolicyStatus.put((Object)"EAS_SERVER_ID", (Object)easServerID);
            easPolicyStatus.put((Object)"POLICY_STATUS", (Object)EASMgmtConstants.POLICY_APPLICATION_INPROGRESS);
            EASMgmtDataHandler.getInstance().addorUpdateEASPolicy(easPolicyStatus, false);
            final JSONObject easPropsJSON = new JSONObject();
            easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
            easPropsJSON.put((Object)"TASK_TYPE", (Object)"FULL_CONDITIONAL_ACCESS_REQUEST");
            if (jsObject != null && jsObject.containsKey((Object)"SEND_GRACE_MAILS")) {
                easPropsJSON.put((Object)"SEND_GRACE_MAILS", (Object)(boolean)jsObject.get((Object)"SEND_GRACE_MAILS"));
            }
            getInstance().addTaskToQueue(easPropsJSON);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void configCEApolicy(final JSONObject easPolicyJSON) {
        EASMgmt.logger.log(Level.INFO, "request Details : {0}", easPolicyJSON.toString());
        try {
            Long easServerID = null;
            final Long[] unselectedMailboxes = null;
            final String taskType = (String)easPolicyJSON.get((Object)"TASK_TYPE");
            if (easPolicyJSON.containsKey((Object)"EAS_SERVER_ID")) {
                easServerID = Long.valueOf((String)easPolicyJSON.get((Object)"EAS_SERVER_ID"));
            }
            if (easServerID == null) {
                easServerID = (Long)EASMgmtDataHandler.getInstance().getExchangeServerDetails(false).get((Object)"EAS_SERVER_ID");
            }
            easPolicyJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
            List selectedMailboxs = null;
            if (easPolicyJSON.containsKey((Object)"EASSelectedMailbox")) {
                final Object selectedMailBoxObject = easPolicyJSON.get((Object)"EASSelectedMailbox");
                if (selectedMailBoxObject instanceof String) {
                    selectedMailboxs = MDMUtil.getInstance().parseStringForElements((String)selectedMailBoxObject);
                }
                else if (selectedMailBoxObject instanceof ArrayList) {
                    selectedMailboxs = (List)selectedMailBoxObject;
                }
                final JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < selectedMailboxs.size(); ++i) {
                    jsonArray.add(selectedMailboxs.get(i));
                }
                easPolicyJSON.put((Object)"EASSelectedMailbox", (Object)jsonArray);
            }
            String statusRemarks = "mdm.cea.applying.policy";
            boolean isRevert = true;
            if (easPolicyJSON.containsKey((Object)"ROLLBACK_BLOCKED_DEVICES") && Boolean.valueOf((String)easPolicyJSON.get((Object)"ROLLBACK_BLOCKED_DEVICES")).equals(Boolean.FALSE)) {
                isRevert = false;
            }
            if (taskType.equalsIgnoreCase("Delete")) {
                EASMgmtDataHandler.getInstance().getUnselectedMailboxesOnCEApolicyRemoval(easServerID, isRevert);
                statusRemarks = "mdm.cea.removing.policy";
            }
            else if (taskType.equalsIgnoreCase("Add") || taskType.equalsIgnoreCase("Update")) {
                EASMgmtDataHandler.getInstance().addorUpdateEASPolicy(easPolicyJSON, true);
            }
            final JSONObject ceaSyncDetails = new JSONObject();
            ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
            ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg(statusRemarks));
            EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(ceaSyncDetails);
            if (Boolean.valueOf((String)easPolicyJSON.get((Object)"ROLLBACK_BLOCKED_DEVICES")).equals(Boolean.FALSE)) {
                METrackParamManager.incrementMETrackParams("ROLLBACK_BLOCKED_DEVICES");
            }
            getInstance().handleContionalAccess(easServerID, null);
            if (taskType.equalsIgnoreCase("Delete")) {
                final JSONObject easPropsJSON = new JSONObject();
                easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                easPropsJSON.put((Object)"TASK_TYPE", (Object)"CEA_POLICY_REMOVAL");
                MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easPropsJSON);
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeDeviceFromEAShost(final JSONObject easDeviceDetails) {
        EASMgmt.logger.log(Level.INFO, "request Details : {0}", easDeviceDetails.toString());
        try {
            final Long esServerID = Long.valueOf((String)easDeviceDetails.get((Object)"EAS_SERVER_ID"));
            final Long esMailboxDeviceID = Long.valueOf((String)easDeviceDetails.get((Object)"EAS_MAILBOX_DEVICE_ID"));
            final JSONObject easPropsJSON = new JSONObject();
            easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)esServerID);
            easPropsJSON.put((Object)"TASK_TYPE", (Object)"REMOVE_EAS_DEVICE");
            easPropsJSON.putAll((Map)EASMgmtDataHandler.getInstance().getMailboxDeviceDetailsAndMarkDevice(esMailboxDeviceID, 1));
            MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easPropsJSON);
            METrackParamManager.incrementMETrackParams("REMOVE_EAS_DEVICE");
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeCEA(final JSONObject exchangeServerDetails) {
        EASMgmt.logger.log(Level.INFO, "request Details : {0}", exchangeServerDetails.toString());
        final Long esServerID = Long.valueOf(String.valueOf(exchangeServerDetails.get((Object)"EAS_SERVER_ID")));
        final JSONObject existingExchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        Long existingExchangeServerID = null;
        if (existingExchangeServerDetails.containsKey((Object)"EAS_SERVER_ID")) {
            existingExchangeServerID = (Long)existingExchangeServerDetails.get((Object)"EAS_SERVER_ID");
        }
        if (existingExchangeServerID == null || esServerID == null || !existingExchangeServerID.equals(esServerID)) {
            return;
        }
        this.configCEApolicy(exchangeServerDetails);
        final JSONObject easPropsJSON = new JSONObject();
        easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)esServerID);
        easPropsJSON.put((Object)"TASK_TYPE", (Object)"CEA_REMOVAL");
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easPropsJSON);
    }
    
    public void handleServerStartStop() {
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().closeAllSessions();
        final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        final Long esServerID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
        if (esServerID != null) {
            final JSONObject easSyncStatus = new JSONObject();
            easSyncStatus.put((Object)"EAS_Sync_Status_ID", (Object)esServerID);
            easSyncStatus.put((Object)"SYNC_STATUS", (Object)0);
            EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(easSyncStatus);
            final Long updatedBy = MDMUtil.getAdminUserId();
            if (updatedBy != null && esServerID != null) {
                final JSONObject easPolicyStatus = new JSONObject();
                easPolicyStatus.put((Object)"UPDATED_BY", (Object)updatedBy);
                easPolicyStatus.put((Object)"EAS_SERVER_ID", (Object)esServerID);
                easPolicyStatus.put((Object)"POLICY_STATUS", (Object)EASMgmtConstants.POLICY_ENFORCEMENT_DONE);
                EASMgmtDataHandler.getInstance().addorUpdateEASPolicy(easPolicyStatus, false);
            }
        }
    }
    
    static {
        EASMgmt.easMgmt = null;
        EASMgmt.logger = Logger.getLogger("EASMgmtLogger");
    }
}
