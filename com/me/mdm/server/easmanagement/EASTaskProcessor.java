package com.me.mdm.server.easmanagement;

import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.easmanagement.pss.PSSException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.IOException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import org.json.simple.JSONArray;
import com.me.idps.core.util.IdpsUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.mdm.server.easmanagement.pss.PSSScriptGenerator;
import com.me.mdm.server.easmanagement.pss.PSSCMDGenerator;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.queue.MemoryOnlyDCQueueDataProcessor;

public class EASTaskProcessor extends MemoryOnlyDCQueueDataProcessor
{
    private boolean easServerDetailsRequest(final JSONObject easVitals, final JSONObject exServerDetails) {
        final Long serverID = (Long)easVitals.get((Object)"EAS_SERVER_ID");
        final String serverFqdn = (String)exServerDetails.get((Object)"CONNECTION_URI");
        String serverDetailsCommands = PSSCMDGenerator.getInstance().getEASServerDetailsScript(serverID);
        if (EASMgmtDataHandler.isExchangeOnlineURI(serverFqdn)) {
            serverDetailsCommands = PSSCMDGenerator.getInstance().getEODetailsScript(serverID);
        }
        PSSScriptGenerator.getInstance().writeCommandsToScript(serverID, serverDetailsCommands);
        easVitals.put((Object)"EXPECTED_SUCCESS_RESULT_FILE", (Object)EASMgmtConstants.getEASserverDetailsResultFile(serverID));
        final JSONObject easPropsJSON = new JSONObject();
        easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)serverID);
        easPropsJSON.put((Object)"TASK_TYPE", (Object)"ACKNOWLEDGEMENT");
        easPropsJSON.put((Object)"ACKNOWLEDGEMENT_TYPE", (Object)"EXCHANGE_SERVER_DETAILS_REQUEST");
        easPropsJSON.put((Object)"RESPONSE", (Object)easVitals);
        EASMgmt.getInstance().addTaskToQueue(easPropsJSON);
        return true;
    }
    
    private boolean fullSyncRequest(final JSONObject syncProps, final JSONObject exServerDetails) {
        final Long easServerID = (Long)syncProps.get((Object)"EAS_SERVER_ID");
        final JSONObject ceaSyncDetails = new JSONObject();
        ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
        ceaSyncDetails.put((Object)"SYNC_STATUS", (Object)1);
        ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg("mdm.cea.mailbox.fetch"));
        EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(ceaSyncDetails);
        final Integer version = (Integer)exServerDetails.get((Object)"EXCHANGE_SERVER_VERSION");
        EASMgmt.logger.log(Level.INFO, "serverID = {0} version= {1}", new Object[] { easServerID, version });
        final String syncScript = PSSCMDGenerator.getInstance().getFullSyncScript(easServerID, version);
        PSSScriptGenerator.getInstance().writeCommandsToScript(easServerID, syncScript);
        final JSONObject easPropsJSON = new JSONObject();
        easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
        easPropsJSON.put((Object)"TASK_TYPE", (Object)"ACKNOWLEDGEMENT");
        easPropsJSON.put((Object)"ACKNOWLEDGEMENT_TYPE", (Object)"SYNC_REQUEST");
        easPropsJSON.put((Object)"RESPONSE", (Object)syncProps);
        EASMgmt.getInstance().addTaskToQueue(easPropsJSON);
        return true;
    }
    
    private boolean conditionalAccessResquest(final JSONObject easTaskProps, final JSONObject exServerDetails) throws DataAccessException {
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        final Integer exchangeServerVersion = (Integer)exServerDetails.get((Object)"EXCHANGE_SERVER_VERSION");
        boolean response = true;
        final boolean readFromFile = Boolean.parseBoolean(easTaskProps.getOrDefault((Object)"readFromFile", (Object)MDMApiFactoryProvider.getConditionalExchangeAccessApi().getReadFromFile()).toString());
        if (readFromFile) {
            final JSONObject policyJSON = EASMgmtDataHandler.getInstance().getPolicyDeviatingCombination(easServerID);
            EASMgmt.logger.log(Level.INFO, "easTaskProps : {0}", new Object[] { IdpsUtil.getPrettyJSON(easTaskProps) });
            EASMgmt.logger.log(Level.INFO, "policy Details : {0}", new Object[] { IdpsUtil.getPrettyJSON(policyJSON) });
            final Boolean needToPerformConditionalAccess = (Boolean)policyJSON.get((Object)"NEED_TO_PERFORM_CONDITIONAL_ACCESS");
            final JSONArray jsArray = (JSONArray)policyJSON.get((Object)"INAPPROPRIATE_DEVICE_ACCESS_STATES");
            if (jsArray != null) {
                Boolean sendGraceMails = false;
                final long dispatchTime = (long)policyJSON.getOrDefault((Object)"DISPATCHED_TIME", (Object)(-1L));
                final Long customerID = (Long)policyJSON.get((Object)"CUSTOMER_ID");
                if (easTaskProps.containsKey((Object)"SEND_GRACE_MAILS")) {
                    sendGraceMails = (Boolean)easTaskProps.get((Object)"SEND_GRACE_MAILS");
                }
                JSONArray userConditionalAccessJSArray = new JSONArray();
                for (int i = 0; i < jsArray.size(); ++i) {
                    final JSONObject jsObject = (JSONObject)jsArray.get(i);
                    userConditionalAccessJSArray.add((Object)jsObject);
                    if ((i % 10 == 0 && i != 0) || i == jsArray.size() - 1) {
                        final JSONObject userConditionalAccessJSONObject = new JSONObject();
                        userConditionalAccessJSONObject.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                        Label_0554: {
                            if (needToPerformConditionalAccess) {
                                try {
                                    EASMgmtDataHandler.getInstance().addOrUpdateCEAaudit(easServerID, userConditionalAccessJSArray);
                                    final long currentmillis = System.currentTimeMillis();
                                    final String fileAddress = EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator + String.valueOf(currentmillis);
                                    try {
                                        ApiFactoryProvider.getFileAccessAPI().writeFile(fileAddress, userConditionalAccessJSArray.toJSONString().getBytes());
                                    }
                                    catch (final IOException ex) {
                                        EASMgmt.logger.log(Level.SEVERE, null, ex);
                                    }
                                    finally {
                                        final String fileContent = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(fileAddress);
                                        EASMgmt.logger.log(Level.INFO, "combinations deviating policy {0}", fileContent);
                                    }
                                    userConditionalAccessJSONObject.put((Object)"fileAddress", (Object)fileAddress);
                                    final String conditionalAccessScript = PSSCMDGenerator.getInstance().getConditionalAccessScript(userConditionalAccessJSONObject, exchangeServerVersion);
                                    PSSScriptGenerator.getInstance().writeCommandsToScript(easServerID, conditionalAccessScript);
                                    final boolean batchResp = EASResponseHandler.getInstance().handleResponse(easTaskProps);
                                    if (batchResp) {
                                        EASMailHandler.getInstance().sendMails(customerID, easServerID, sendGraceMails, userConditionalAccessJSArray);
                                    }
                                    response &= batchResp;
                                    break Label_0554;
                                }
                                catch (final Exception ex2) {
                                    EASMgmt.logger.log(Level.SEVERE, "exception occured while performing codnitional access", ex2);
                                    break;
                                }
                            }
                            EASMailHandler.getInstance().sendMails(customerID, easServerID, sendGraceMails, userConditionalAccessJSArray);
                        }
                        userConditionalAccessJSArray = new JSONArray();
                    }
                }
                final JSONObject easPropsJSON = new JSONObject();
                easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                easPropsJSON.put((Object)"TASK_TYPE", (Object)"ACKNOWLEDGEMENT");
                easPropsJSON.put((Object)"ACKNOWLEDGEMENT_TYPE", (Object)"FULL_CONDITIONAL_ACCESS_REQUEST");
                EASMgmt.getInstance().addTaskToQueue(easPropsJSON);
                EASMgmt.logger.log(Level.INFO, "conditional access request handled");
                if (dispatchTime != -1L) {
                    this.logger.log(Level.INFO, "Deleting entries having dispatch time : {0}", dispatchTime);
                    SyMUtil.getPersistence().delete(new Criteria(Column.getColumn("EASUnSelectedMailBoxes", "DISPATCHED_TIME"), (Object)dispatchTime, 0));
                }
                else {
                    this.logger.log(Level.INFO, "Dispatch time not present in JSON");
                }
            }
        }
        else {
            MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easTaskProps);
        }
        return response;
    }
    
    private boolean processCEApolicyRemoval(final JSONObject easTaskProps) {
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        try {
            EASMgmtDataHandler.getInstance().deleteCEApolicy(easServerID);
            MDMUtil.deleteSyMParameter("ROLLBACK_BLOCKED_DEVICES");
        }
        catch (final DataAccessException e) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)e);
            return false;
        }
        return true;
    }
    
    private boolean processCEARemoval(final JSONObject easTaskProps) {
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        try {
            final JSONObject ceaSyncDetails = new JSONObject();
            ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
            ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg("mdm.cea.removing"));
            EASMgmtDataHandler.getInstance().deleteCEA(easServerID);
        }
        catch (final DataAccessException e) {
            EASMgmt.logger.log(Level.SEVERE, null, (Throwable)e);
            return false;
        }
        return true;
    }
    
    private boolean processEnrollment(final JSONObject enrollmentProps, final JSONObject exServerDetails) throws Exception {
        if (exServerDetails.get((Object)"EAS_POLICY_ID") == null) {
            return true;
        }
        final Long easServerID = (Long)enrollmentProps.get((Object)"EAS_SERVER_ID");
        EASMgmt.getInstance().handleContionalAccess(easServerID, null);
        return true;
    }
    
    private boolean processCEADeviceRemoval(final JSONObject deleteDeviceTaskProps, final JSONObject exchangeServerDetails) {
        try {
            final String deviceGUID = (String)deleteDeviceTaskProps.get((Object)"GUID");
            final Long esServerID = (Long)deleteDeviceTaskProps.get((Object)"EAS_SERVER_ID");
            final Long esMailboxDeviceID = (Long)deleteDeviceTaskProps.get((Object)"EAS_MAILBOX_DEVICE_ID");
            if (!SyMUtil.isStringEmpty(deviceGUID) && esServerID != null && esMailboxDeviceID != null) {
                final Integer exchangeServerVersion = (Integer)exchangeServerDetails.get((Object)"EXCHANGE_SERVER_VERSION");
                final String expectedResultFileAddress = EASMgmtConstants.getDedicatedFolderPath(esServerID) + File.separator + System.currentTimeMillis();
                deleteDeviceTaskProps.put((Object)"EXPECTED_SUCCESS_RESULT_FILE", (Object)expectedResultFileAddress);
                final String deleteDeviceScript = PSSCMDGenerator.getInstance().getDeleteDeviceScript(exchangeServerVersion, deviceGUID, esMailboxDeviceID, expectedResultFileAddress);
                PSSScriptGenerator.getInstance().writeCommandsToScript(esServerID, deleteDeviceScript);
                final JSONObject easPropsJSON = new JSONObject();
                easPropsJSON.put((Object)"EAS_SERVER_ID", (Object)esServerID);
                easPropsJSON.put((Object)"TASK_TYPE", (Object)"ACKNOWLEDGEMENT");
                easPropsJSON.put((Object)"ACKNOWLEDGEMENT_TYPE", (Object)"REMOVE_EAS_DEVICE");
                easPropsJSON.put((Object)"RESPONSE", (Object)deleteDeviceTaskProps);
                EASMgmt.getInstance().addTaskToQueue(easPropsJSON);
            }
        }
        catch (final Exception e) {
            EASMgmt.logger.log(Level.SEVERE, null, e);
        }
        return false;
    }
    
    public void processData(final DCQueueData qData) {
        final JSONObject easTaskProps = (JSONObject)qData.queueData;
        String taskType = (String)easTaskProps.get((Object)"TASK_TYPE");
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        EASMgmt.logger.log(Level.INFO, "olo taskType = {0} for server {1}", new Object[] { taskType, easServerID });
        final boolean readFromFile = MDMApiFactoryProvider.getConditionalExchangeAccessApi().getReadFromFile();
        easTaskProps.put((Object)"readFromFile", (Object)readFromFile);
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().incrementTaskList(easServerID);
        try {
            final JSONObject exServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(true);
            MDMApiFactoryProvider.getConditionalExchangeAccessApi().initiateSession(easTaskProps, exServerDetails);
            boolean taskStatus = true;
            Long taskResourceID = easServerID;
            String remarkArgs = (String)exServerDetails.get((Object)"CONNECTION_URI");
            final String s = taskType;
            switch (s) {
                case "EXCHANGE_SERVER_DETAILS_REQUEST": {
                    taskStatus &= this.easServerDetailsRequest(easTaskProps, exServerDetails);
                    break;
                }
                case "ENROLLMENT_TASK": {
                    taskStatus &= this.processEnrollment(easTaskProps, exServerDetails);
                    taskResourceID = (Long)easTaskProps.get((Object)"RESOURCE_ID");
                    remarkArgs = ManagedDeviceHandler.getInstance().getDeviceName(taskResourceID);
                    break;
                }
                case "SYNC_REQUEST": {
                    taskStatus &= this.fullSyncRequest(easTaskProps, exServerDetails);
                    break;
                }
                case "FULL_CONDITIONAL_ACCESS_REQUEST": {
                    taskStatus &= this.conditionalAccessResquest(easTaskProps, exServerDetails);
                    break;
                }
                case "CEA_POLICY_REMOVAL": {
                    taskStatus &= this.processCEApolicyRemoval(easTaskProps);
                    break;
                }
                case "CEA_REMOVAL": {
                    this.processCEARemoval(easTaskProps);
                    break;
                }
                case "REMOVE_EAS_DEVICE": {
                    taskStatus &= this.processCEADeviceRemoval(easTaskProps, exServerDetails);
                    break;
                }
                case "ACKNOWLEDGEMENT": {
                    taskType = (String)easTaskProps.get((Object)"ACKNOWLEDGEMENT_TYPE");
                    final String status = (String)easTaskProps.getOrDefault((Object)"response_status", (Object)"Acknowledged");
                    if ("Error".equals(status)) {
                        this.logger.log(Level.SEVERE, "Error is executing command! {0}", easTaskProps.getOrDefault((Object)"ErrorMsg", (Object)""));
                        throw new PSSException();
                    }
                    boolean response = true;
                    if (easTaskProps.get((Object)"RESPONSE") != null) {
                        response = EASResponseHandler.getInstance().handleResponse((JSONObject)easTaskProps.get((Object)"RESPONSE"));
                    }
                    final String s2 = taskType;
                    switch (s2) {
                        case "SYNC_REQUEST": {
                            final JSONObject syncProps = (JSONObject)easTaskProps.get((Object)"RESPONSE");
                            if (response && syncProps.containsKey((Object)"LAST_ATTEMPTED_SYNC_TASK")) {
                                syncProps.put((Object)"LAST_SUCCESSFUL_SYNC_TASK", syncProps.get((Object)"LAST_ATTEMPTED_SYNC_TASK"));
                            }
                            syncProps.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
                            syncProps.put((Object)"REMARKS", (Object)"Finished Syncing.. Processing data");
                            syncProps.put((Object)"SYNC_STATUS", (Object)0);
                            EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(syncProps);
                            if (response) {
                                final Boolean sendGraceMails = (Boolean)syncProps.get((Object)"SEND_GRACE_MAILS");
                                final JSONObject jsObject = new JSONObject();
                                jsObject.put((Object)"SEND_GRACE_MAILS", (Object)sendGraceMails);
                                EASMgmt.getInstance().handleContionalAccess(easServerID, jsObject);
                                break;
                            }
                            break;
                        }
                        case "FULL_CONDITIONAL_ACCESS_REQUEST": {
                            try {
                                final JSONObject easPolicyStatus = new JSONObject();
                                easPolicyStatus.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                                easPolicyStatus.put((Object)"POLICY_STATUS", (Object)EASMgmtConstants.POLICY_ENFORCEMENT_DONE);
                                EASMgmtDataHandler.getInstance().addorUpdateEASPolicy(easPolicyStatus, false);
                            }
                            catch (final Exception ex) {
                                EASMgmt.logger.log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                        case "REMOVE_EAS_DEVICE": {
                            this.processCEADeviceRemoval(easTaskProps, exServerDetails);
                            break;
                        }
                        case "CEA_POLICY_REMOVAL": {
                            this.processCEApolicyRemoval(easTaskProps);
                            break;
                        }
                        case "CEA_REMOVAL": {
                            this.processCEARemoval(easTaskProps);
                            break;
                        }
                    }
                    break;
                }
            }
            final int eventID = EASMgmtDataHandler.getInstance().getEventIDforTaskType(taskType);
            if (eventID != 0) {
                final String remarks = EASMgmtDataHandler.getInstance().getTaskRemark(eventID, taskStatus);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventID, taskResourceID, (String)easTaskProps.get((Object)"CURRENTLY_LOGGED_IN_USER"), remarks, remarkArgs, (Long)exServerDetails.get((Object)"CUSTOMER_ID"));
            }
        }
        catch (final PSSException ex2) {
            EASMgmt.logger.log(Level.INFO, ex2.getMessage());
            EASMgmtErrorHandler.getInstance().handleError((Integer)null, easServerID, taskType);
        }
        catch (final Exception ex3) {
            EASMgmt.logger.log(Level.SEVERE, "exception occured executing a task.. decrementing the task list", ex3);
            EASMgmtErrorHandler.getInstance().handleError(EASMgmtConstants.EAS_GENERIC_ERROR, easServerID, taskType);
        }
        MDMApiFactoryProvider.getConditionalExchangeAccessApi().decrementTaskList(easServerID);
    }
}
