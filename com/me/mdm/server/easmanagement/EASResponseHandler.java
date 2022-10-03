package com.me.mdm.server.easmanagement;

import java.util.concurrent.TimeUnit;
import javax.transaction.TransactionManager;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class EASResponseHandler
{
    private static EASResponseHandler easResponseHandler;
    private int syncFetchedAlread;
    
    public static EASResponseHandler getInstance() {
        if (EASResponseHandler.easResponseHandler == null) {
            EASResponseHandler.easResponseHandler = new EASResponseHandler();
        }
        return EASResponseHandler.easResponseHandler;
    }
    
    private void deleteFile(final String fileName) {
        try {
            ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private EASResponseObject waitForResponse(final Long serverID, final String resultFileAddress, final int seconds) {
        final EASResponseObject easResponse = new EASResponseObject();
        easResponse.setResponseType(2);
        final String errorFileAddress = EASMgmtConstants.getExceptionTypeFileAddress(serverID);
        final File errorFile = new File(errorFileAddress);
        final File resultFile = new File(resultFileAddress);
        EASMgmt.logger.log(Level.INFO, "waiting for a maximum of {0} seconds for reponse for either {1} or {2}", new Object[] { seconds, resultFileAddress, errorFile.getName() });
        int i = 0;
        while (i < seconds * 10) {
            if (!resultFile.exists() && !errorFile.exists() && MDMApiFactoryProvider.getConditionalExchangeAccessApi().getPSSstate(serverID) != 0) {
                try {
                    Thread.sleep(100L);
                }
                catch (final InterruptedException ex) {
                    EASMgmt.logger.log(Level.SEVERE, null, ex);
                }
                ++i;
            }
            else {
                EASMgmt.logger.log(Level.INFO, "got a response in {0}/{1} resultFilexists {2} errorFilexists {3}", new Object[] { i / 10, seconds, resultFile.exists(), errorFile.exists() });
                try {
                    for (int sleepCount = 0; resultFile.length() == 0L && errorFile.length() == 0L && sleepCount < 2000; ++sleepCount) {
                        Thread.sleep(100L);
                    }
                }
                catch (final InterruptedException ex) {
                    EASMgmt.logger.log(Level.SEVERE, null, ex);
                }
                EASMgmt.logger.log(Level.INFO, "resultFile Size {0} errorFile size {1}", new Object[] { resultFile.length(), errorFile.length() });
                if (resultFile.exists() && !errorFile.exists()) {
                    easResponse.setResponseType(1);
                    break;
                }
                if (errorFile.exists()) {
                    easResponse.setResponseType(0);
                    break;
                }
                break;
            }
        }
        if ((!resultFile.exists() && !errorFile.exists()) || (resultFile.exists() && resultFile.length() == 0L) || (errorFile.exists() && errorFile.length() == 0L)) {
            easResponse.setResponseType(2);
            EASMgmt.logger.log(Level.WARNING, "did not recieve any response.. timed out .. or maybe the response was empty");
        }
        return easResponse;
    }
    
    private EASResponseObject parseResponse(final Long easServerID, final String resultFileAddress, final EASResponseObject easResponse) {
        try {
            if (easResponse.getResponseType() == 1) {
                MDMUtil.getInstance();
                easResponse.setResponseData(MDMUtil.getNormalizedJSONAndDeleteFile(new File(resultFileAddress)));
            }
            else if (easResponse.getResponseType() == 0) {
                final File errorFile = new File(EASMgmtConstants.getExceptionTypeFileAddress(easServerID));
                easResponse.setResponseType(0);
                easResponse.setResponseData(errorFile);
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return easResponse;
    }
    
    private void processResponse(final String taskType, final EASResponseObject easResponse, final Long easServerID) {
        if (easResponse.getResponseType() == 1) {
            if (taskType.equals("START_SESSION_REQUEST")) {
                final String authModeUsed = (String)easResponse.getResponseData();
                final JSONObject serverDetailsResponse = new JSONObject();
                serverDetailsResponse.put((Object)"AUTH_MODE", (Object)authModeUsed);
                serverDetailsResponse.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                EASMgmtDataHandler.getInstance().addOrUpdateEASServerDetails(serverDetailsResponse);
            }
            else if (taskType.equals("EXCHANGE_SERVER_DETAILS_REQUEST")) {
                final JSONObject serverDetailsResponse2 = (JSONObject)easResponse.getResponseData();
                serverDetailsResponse2.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                EASMgmtDataHandler.getInstance().addOrUpdateEASServerDetails(serverDetailsResponse2);
                EASMgmtDataHandler.getInstance().updateEASScheduler(true);
            }
            else if (taskType.equals("REMOVE_EAS_DEVICE")) {
                final Long esMailboxDeviceID = Long.valueOf(String.valueOf(easResponse.getResponseData()));
                EASMgmtDataHandler.getInstance().deleteEASdeviceForEAShost(esMailboxDeviceID);
            }
            else if (taskType.equals("SYNC_REQUEST") || taskType.equals("FULL_CONDITIONAL_ACCESS_REQUEST")) {
                final Object parseJSON = easResponse.getResponseData();
                if (parseJSON.getClass().equals(JSONArray.class)) {
                    final TransactionManager transactionManager = SyMUtil.getUserTransaction();
                    try {
                        final JSONArray jsArray = (JSONArray)parseJSON;
                        if (jsArray == null) {
                            return;
                        }
                        transactionManager.begin();
                        for (int j = 0; j < jsArray.size(); ++j) {
                            EASMgmtDataHandler.getInstance().addOrUpdateEASMailboxDeviceInfo(easServerID, (JSONObject)jsArray.get(j));
                            ++this.syncFetchedAlread;
                        }
                        transactionManager.commit();
                        if (taskType.equals("SYNC_REQUEST")) {
                            final JSONObject syncProps = new JSONObject();
                            syncProps.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
                            syncProps.put((Object)"REMARKS", (Object)I18N.getMsg("mdm.cea.device.fetch", new Object[] { this.syncFetchedAlread }));
                            EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(syncProps);
                        }
                    }
                    catch (final Exception ex) {
                        try {
                            transactionManager.rollback();
                        }
                        catch (final Exception ex2) {
                            EASMgmt.logger.log(Level.SEVERE, null, ex2);
                        }
                        EASMgmt.logger.log(Level.SEVERE, null, ex);
                    }
                }
                else if (parseJSON.getClass().equals(JSONObject.class)) {
                    EASMgmtDataHandler.getInstance().addOrUpdateEASMailboxDeviceInfo(easServerID, (JSONObject)parseJSON);
                }
            }
        }
        else if (easResponse.getResponseType() == 0) {
            EASMgmtErrorHandler.getInstance().handleError((File)easResponse.getResponseData(), easServerID, taskType);
        }
        else if (easResponse.getResponseType() == 2 && taskType.equals("START_SESSION_REQUEST")) {
            EASMgmtErrorHandler.getInstance().handleError(EASMgmtConstants.EAS_GENERIC_ERROR, easServerID, taskType);
        }
    }
    
    public boolean handleResponse(final JSONObject easTaskProps) {
        EASResponseObject easResponse = null;
        final String taskType = (String)easTaskProps.get((Object)"TASK_TYPE");
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        final boolean readFromFile = Boolean.parseBoolean(easTaskProps.getOrDefault((Object)"readFromFile", (Object)MDMApiFactoryProvider.getConditionalExchangeAccessApi().getReadFromFile()).toString());
        Label_0892: {
            if (taskType.equals("START_SESSION_REQUEST") || taskType.equals("EXCHANGE_SERVER_DETAILS_REQUEST") || taskType.equals("REMOVE_EAS_DEVICE")) {
                if (readFromFile) {
                    final String resultFileAddress = (String)easTaskProps.get((Object)"EXPECTED_SUCCESS_RESULT_FILE");
                    easResponse = this.waitForResponse(easServerID, resultFileAddress, 120);
                    easResponse = this.parseResponse(easServerID, resultFileAddress, easResponse);
                    if (taskType.equals("START_SESSION_REQUEST")) {
                        EASMgmt.logger.log(Level.INFO, String.valueOf(easResponse.getResponseData()));
                    }
                }
                else {
                    easResponse = (EASResponseObject)easTaskProps.get((Object)"easResponse");
                    easTaskProps.remove((Object)"easResponse");
                }
                this.processResponse(taskType, easResponse, easServerID);
                if (taskType.equals("EXCHANGE_SERVER_DETAILS_REQUEST")) {
                    if (easResponse.getResponseType() == 1) {
                        easTaskProps.put((Object)"TASK_TYPE", (Object)"SYNC_REQUEST");
                        MDMApiFactoryProvider.getConditionalExchangeAccessApi().addTaskToQueue(easTaskProps);
                    }
                    else {
                        final JSONObject syncProps = new JSONObject();
                        syncProps.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
                        syncProps.put((Object)"SYNC_STATUS", (Object)0);
                        EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(syncProps);
                    }
                }
            }
            else {
                if (!taskType.equals("SYNC_REQUEST")) {
                    if (!taskType.equals("FULL_CONDITIONAL_ACCESS_REQUEST")) {
                        break Label_0892;
                    }
                }
                try {
                    this.syncFetchedAlread = 0;
                    EASMgmt.logger.log(Level.INFO, "handling {0} request", taskType);
                    final Long syncProcessInitiationTime = System.currentTimeMillis();
                    if (readFromFile) {
                        boolean loop = true;
                        boolean errorOccured = false;
                        boolean endOfResponseFileReceived = false;
                        final String endOfResponseFileAddress = EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator + "END_OF_RESPONSE_FILE_NAME";
                        final File endOfResponseFile = new File(endOfResponseFileAddress);
                        int i = 1;
                        while (loop && !errorOccured) {
                            final String basePath = EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator;
                            final String resultFileAddress2 = basePath + "compiled" + i + ".json";
                            final int syncTimeOut = 2400;
                            easResponse = this.waitForResponse(easServerID, resultFileAddress2, syncTimeOut);
                            if (easResponse.getResponseType() == 2) {
                                loop = false;
                                easResponse = this.waitForResponse(easServerID, basePath + "hint.json", 15);
                                if (easResponse.getResponseType() == 1) {
                                    String noResponseHint = "";
                                    easResponse = this.parseResponse(easServerID, resultFileAddress2, easResponse);
                                    if (easResponse != null) {
                                        noResponseHint = String.valueOf(easResponse.getResponseData());
                                    }
                                    EASMgmt.logger.log(Level.WARNING, "did not receive response for {0}, despite waiting {1} minutes", new Object[] { noResponseHint, TimeUnit.SECONDS.toMinutes(syncTimeOut) });
                                }
                            }
                            else {
                                easResponse = this.parseResponse(easServerID, resultFileAddress2, easResponse);
                                if (easResponse.getResponseType() == 0) {
                                    errorOccured = true;
                                }
                                this.processResponse(taskType, easResponse, easServerID);
                            }
                            final String endOfResponseCheckNextResponsePath = EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator + "compiled" + (i + 1) + ".json";
                            final File nextResponseFile = new File(endOfResponseCheckNextResponsePath);
                            if (!nextResponseFile.exists() && endOfResponseFile.exists()) {
                                try {
                                    EASResponseObject debugCEAresponse = this.waitForResponse(easServerID, basePath + "debug.txt", 3);
                                    if (debugCEAresponse.getResponseType() == 1) {
                                        String debugTxt = "";
                                        debugCEAresponse = this.parseResponse(easServerID, resultFileAddress2, debugCEAresponse);
                                        if (debugCEAresponse != null) {
                                            debugTxt = String.valueOf(debugCEAresponse.getResponseData());
                                        }
                                        EASMgmt.logger.log(Level.WARNING, "cea debug : {0}", new Object[] { debugTxt });
                                    }
                                }
                                catch (final Exception ex) {
                                    EASMgmt.logger.log(Level.WARNING, "exception in reading debug logs", ex);
                                }
                                loop = false;
                                endOfResponseFileReceived = true;
                                this.deleteFile(endOfResponseFile.getAbsolutePath());
                                EASMgmt.logger.log(Level.INFO, "end of response received");
                            }
                            ++i;
                        }
                        EASMgmt.logger.log(Level.INFO, "endOfResponseReceived = {0} errorOccured = {1}", new Object[] { endOfResponseFileReceived, errorOccured });
                    }
                    else {
                        easResponse = (EASResponseObject)easTaskProps.get((Object)"easResponse");
                        this.processResponse(taskType, easResponse, easServerID);
                    }
                    this.syncFetchedAlread = 0;
                    EASMgmt.logger.log(Level.INFO, "{0} done", taskType);
                    EASMgmtDataHandler.getInstance().addOrUpdateEASManagedDeviceRel(syncProcessInitiationTime);
                    EASMgmtDataHandler.getInstance().addOrUpdateCEAmailboxGracePeriod(easServerID);
                    EASMgmtDataHandler.getInstance().mapDomainNameToCEA(easServerID);
                }
                catch (final Exception ex2) {
                    EASMgmt.logger.log(Level.SEVERE, null, ex2);
                }
            }
            try {
                return easResponse.getResponseType() == 1;
            }
            catch (final Exception ex2) {
                EASMgmt.logger.log(Level.INFO, null, ex2);
                return false;
            }
        }
    }
    
    static {
        EASResponseHandler.easResponseHandler = null;
    }
}
