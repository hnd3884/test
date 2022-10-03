package com.me.mdm.server.easmanagement.pss;

import com.me.mdm.server.easmanagement.EASResponseHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.me.mdm.server.easmanagement.EASMgmtErrorHandler;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.easmanagement.EASMgmtConstants;
import com.me.mdm.server.easmanagement.EASMgmt;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.util.HashMap;

public class PSSMgmt
{
    private static PSSMgmt pssProcessHandler;
    private HashMap<Long, PSSProcess> processSessionMap;
    
    public PSSMgmt() {
        this.processSessionMap = new HashMap<Long, PSSProcess>();
    }
    
    public static PSSMgmt getInstance() {
        if (PSSMgmt.pssProcessHandler == null) {
            PSSMgmt.pssProcessHandler = new PSSMgmt();
        }
        return PSSMgmt.pssProcessHandler;
    }
    
    private static void pipe(final InputStream from, final Logger logger, final Level level) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(from));
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                logger.log(level, line);
            }
        }
        catch (final IOException e) {
            logger.log(Level.SEVERE, e.toString());
            try {
                from.close();
            }
            catch (final IOException e) {
                logger.log(Level.SEVERE, e.toString());
            }
        }
        finally {
            try {
                from.close();
            }
            catch (final IOException e2) {
                logger.log(Level.SEVERE, e2.toString());
            }
        }
    }
    
    public static void pipe(final Process process) {
        EASMgmt.logger.log(Level.INFO, "---------------------------------------------");
        EASMgmt.logger.log(Level.INFO, "flushing session stdout logs");
        pipe(process.getInputStream(), EASMgmt.logger, Level.INFO);
        EASMgmt.logger.log(Level.INFO, "flushing session stdout logs done");
        EASMgmt.logger.log(Level.INFO, "---------------------------------------------");
        EASMgmt.logger.log(Level.INFO, "flushing session stderr logs");
        pipe(process.getErrorStream(), EASMgmt.logger, Level.SEVERE);
        EASMgmt.logger.log(Level.INFO, "flushing session stderr logs done");
        EASMgmt.logger.log(Level.INFO, "---------------------------------------------");
    }
    
    private PSSProcess getPSSProcess(final Long easServerID) {
        if (easServerID != null) {
            if (!getInstance().processSessionMap.containsKey(easServerID)) {
                getInstance().processSessionMap.put(easServerID, new PSSProcess(easServerID));
            }
            return getInstance().processSessionMap.get(easServerID);
        }
        return null;
    }
    
    private boolean resolveFQDN(final String fqdn) {
        Boolean fqdnResolved = Boolean.FALSE;
        final String exceptionTypeFilePath = EASMgmtConstants.getExceptionTypeFileAddress(Long.valueOf(0L));
        final String successResponseFilePath = EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)) + File.separator + String.valueOf(System.currentTimeMillis());
        final String script = PSSCMDGenerator.getInstance().getFQDNresolveScript(fqdn, successResponseFilePath, exceptionTypeFilePath);
        PSSScriptGenerator.getInstance().writeCommandsToScript(Long.valueOf(0L), script);
        final String taskScriptFileAddress = EASMgmtConstants.getTaskScriptFileAddress(Long.valueOf(0L));
        try {
            final Process process = MDMApiFactoryProvider.getMDMUtilAPI().exec("PowerShell -ExecutionPolicy Unrestricted -NoLogo -noninteractive -command \". '" + taskScriptFileAddress + "'\" ");
            final File exceptionTypeFile = new File(exceptionTypeFilePath);
            final File successResponseFile = new File(successResponseFilePath);
            for (int i = 0; i < 600; ++i) {
                if (successResponseFile.exists() || exceptionTypeFile.exists()) {
                    EASMgmt.logger.log(Level.INFO, "got a response in {0}/{1} responseFilexists {2}  exceptionFileExists {3}", new Object[] { i / 10, 60, successResponseFile.exists(), exceptionTypeFile.exists() });
                    break;
                }
                Thread.sleep(100L);
            }
            process.destroy();
            if (successResponseFile.exists()) {
                successResponseFile.delete();
                fqdnResolved = Boolean.TRUE;
            }
            if (exceptionTypeFile.exists()) {
                exceptionTypeFile.delete();
            }
        }
        catch (final InterruptedException ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for fqdn resolve");
                }
            }
            catch (final Exception ex2) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex2);
            }
        }
        finally {
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for fqdn resolve");
                }
            }
            catch (final Exception ex3) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex3);
            }
        }
        EASMgmt.logger.log(Level.INFO, "fqdn resolved {0}", fqdnResolved);
        return fqdnResolved;
    }
    
    public synchronized boolean startSession(final JSONObject easVitalDetailsJSON) throws PSSException {
        boolean response = false;
        final Long easServerID = (Long)easVitalDetailsJSON.get((Object)"EAS_SERVER_ID");
        final String serverFqdn = (String)easVitalDetailsJSON.get((Object)"CONNECTION_URI");
        if (easServerID == null) {
            EASMgmt.logger.log(Level.SEVERE, "easServerID was null..can't start session for a null server");
            return false;
        }
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            final Integer pssState = pssProcess.getPSSstate();
            EASMgmt.logger.log(Level.INFO, "called upon to start server. current pssState = {0}", pssState);
            if (pssState == 0) {
                EASMgmt.logger.log(Level.INFO, "starting.. session is dead. pssState = {0}", pssState);
                response = getInstance().isPowerShellVersionSupported(serverFqdn, easServerID);
                if (!response) {
                    EASMgmtErrorHandler.getInstance().handleError(EASMgmtConstants.PS_VERSION_ERROR_ID, easServerID, "START_SESSION_REQUEST");
                    return false;
                }
                final boolean isFQDNreolvable = this.resolveFQDN(serverFqdn);
                if (!isFQDNreolvable) {
                    EASMgmtErrorHandler.getInstance().handleError(EASMgmtConstants.UNRESOLVABLE_FQDN_ERROR_ID, easServerID, "START_SESSION_REQUEST");
                    return false;
                }
                response = pssProcess.startSession(easVitalDetailsJSON);
            }
            else {
                EASMgmt.logger.log(Level.WARNING, "not starting.. one session is not dead = {0}", pssState);
                if (pssProcess.getPSSstate() == 1) {
                    EASMgmt.logger.log(Level.INFO, "session is live");
                    response = true;
                }
                else {
                    response = false;
                    EASMgmt.logger.log(Level.SEVERE, "should not have come here.. fatal!!! session is in between dead and alive");
                    pssProcess.closeSession();
                }
            }
        }
        if (!response) {
            throw new PSSException();
        }
        return true;
    }
    
    public synchronized void closeSession(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            if (pssProcess.getPSSstate() == 0) {
                EASMgmt.logger.log(Level.INFO, "session Dead anyway.. just doing safe close pssState={0}", pssProcess.getPSSstate());
                pssProcess.safeClose();
            }
            else {
                EASMgmt.logger.log(Level.INFO, "going to close Session pssState = {0}", pssProcess.getPSSstate());
                pssProcess.closeSession();
            }
        }
        getInstance().processSessionMap.remove(easServerID);
    }
    
    public synchronized void updateSessionActivity(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            pssProcess.lastSessionActivity = System.currentTimeMillis();
        }
    }
    
    public synchronized boolean isSessionInactive(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            final Long lastSessionActivity = pssProcess.lastSessionActivity;
            if (lastSessionActivity != null) {
                final Long currentTime = System.currentTimeMillis();
                final Long timeCheck = currentTime - lastSessionActivity - 10000L;
                EASMgmt.logger.log(Level.FINE, "last session activity = {0} current Time= {1} timeCheck = {2} taskCount= {3}", new Object[] { lastSessionActivity, currentTime, timeCheck, pssProcess.taskCount });
                return (timeCheck > 0L && pssProcess.taskCount == 0) || getInstance().getPSSstate(easServerID) == 0;
            }
        }
        return true;
    }
    
    public synchronized int getPSSstate(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            return pssProcess.getPSSstate();
        }
        return 0;
    }
    
    public synchronized void incrementTaskList(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            pssProcess.taskCount++;
            EASMgmt.logger.log(Level.INFO, "taskList for Server={0} incremented", easServerID);
        }
    }
    
    public synchronized void decrementTaskList(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            if (pssProcess.taskCount > 0) {
                pssProcess.taskCount--;
            }
            EASMgmt.logger.log(Level.INFO, "taskList for Server={0} decremented ", easServerID);
        }
    }
    
    public synchronized int getPendingTaskCount(final Long easServerID) {
        final PSSProcess pssProcess = getInstance().getPSSProcess(easServerID);
        if (pssProcess != null) {
            return pssProcess.taskCount;
        }
        return 0;
    }
    
    public synchronized HashSet<Long> getPSSSessionSet() {
        EASMgmt.logger.log(Level.FINE, "!getting the session list!");
        final Set<Long> pssSet = getInstance().processSessionMap.keySet();
        final Iterator pssIterator = pssSet.iterator();
        final HashSet<Long> clonedPSSSet = new HashSet<Long>();
        while (pssIterator.hasNext()) {
            clonedPSSSet.add(pssIterator.next());
        }
        return clonedPSSSet;
    }
    
    public synchronized void closeAllSessions() {
        final Set<Long> serverSessionMap = getInstance().getPSSSessionSet();
        if (serverSessionMap != null) {
            for (final Long easServerID : serverSessionMap) {
                final PSSProcess tempPSSProcess = getInstance().getPSSProcess(easServerID);
                tempPSSProcess.safeClose();
            }
        }
    }
    
    public synchronized boolean isPowerShellVersionSupported(final String serverFQDN, final Long esServerID) {
        boolean requirementsMet = false;
        final String script = PSSCMDGenerator.getInstance().getPowerShellVersionCheckScript();
        PSSScriptGenerator.getInstance().writeCommandsToScript(Long.valueOf(0L), script);
        final String taskScriptFileAddress = EASMgmtConstants.getTaskScriptFileAddress(Long.valueOf(0L));
        try {
            final Process process = MDMApiFactoryProvider.getMDMUtilAPI().exec("PowerShell -ExecutionPolicy Unrestricted -NoLogo -noninteractive -command \". '" + taskScriptFileAddress + "'\" ");
            final File majorResponseFile = new File(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)) + File.separator + "majorVersion");
            final File minorResponseFile = new File(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)) + File.separator + "minorVersion");
            for (int i = 0; i < 600; ++i) {
                if (majorResponseFile.exists() && minorResponseFile.exists()) {
                    EASMgmt.logger.log(Level.INFO, "got a response in {0}/{1} majorResponseFile {2} minorResponseFile {3}", new Object[] { i / 10, 60, majorResponseFile.exists(), minorResponseFile.exists() });
                    for (int sleepCount = 0; majorResponseFile.length() == 0L && minorResponseFile.length() == 0L && sleepCount <= 10; ++sleepCount) {
                        Thread.sleep(100L);
                    }
                    break;
                }
                Thread.sleep(100L);
            }
            process.destroy();
            final String majorVersion = MDMUtil.getNormalizedStringAndDeleteFile(majorResponseFile);
            final String minorVersion = MDMUtil.getNormalizedStringAndDeleteFile(minorResponseFile);
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(majorResponseFile.getAbsolutePath());
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, "exception occured deleting file", ex);
            }
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(minorResponseFile.getAbsolutePath());
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, "exception occured deleting file", ex);
            }
            final int majorVersionNum = Integer.valueOf(majorVersion);
            final int minorVersionNum = Integer.valueOf(minorVersion);
            final JSONObject exchangeServerDetails = new JSONObject();
            exchangeServerDetails.put((Object)"PS_VERSION", (Object)majorVersion);
            exchangeServerDetails.put((Object)"EAS_SERVER_ID", (Object)esServerID);
            EASMgmtDataHandler.getInstance().addOrUpdateEASServerDetails(exchangeServerDetails);
            EASMgmt.logger.log(Level.INFO, "majorVersionNum = {0}, minorVersionNume = {1}", new Object[] { majorVersionNum, minorVersionNum });
            if (EASMgmtDataHandler.isExchangeOnlineURI(serverFQDN)) {
                if (majorVersionNum == EASMgmtConstants.EO_MAJOR_POWERSHELL_REQUIREMENT) {
                    if (minorVersionNum >= EASMgmtConstants.EO_MINOR_POWERSHELL_REQUIREMENT) {
                        requirementsMet = true;
                    }
                }
                else if (majorVersionNum > EASMgmtConstants.EO_MAJOR_POWERSHELL_REQUIREMENT) {
                    requirementsMet = true;
                }
            }
            else if (majorVersionNum >= EASMgmtConstants.MIN_POWERSHELL_REQUIREMENT) {
                requirementsMet = true;
            }
        }
        catch (final InterruptedException ex2) {
            EASMgmt.logger.log(Level.SEVERE, null, ex2);
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for the ps version check");
                }
            }
            catch (final Exception ex3) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex3);
            }
        }
        finally {
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for the ps version check");
                }
            }
            catch (final Exception ex4) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex4);
            }
        }
        return requirementsMet;
    }
    
    public synchronized boolean installEXOV2Module(final Long ceaServerID) {
        final long nonEasServerID = 0L;
        int responseCounter = 0;
        boolean installedEXOV2Module = false;
        final String basePath = EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(nonEasServerID)) + File.separator;
        final List<String> responseFiles = new ArrayList<String>(Arrays.asList("step1", "step2", "step3", "END_OF_RESPONSE_FILE_NAME"));
        final String script = PSSCMDGenerator.getInstance().getEXOV2ModuleInstallationScript(nonEasServerID, basePath, (List)responseFiles);
        PSSScriptGenerator.getInstance().writeCommandsToScript(Long.valueOf(nonEasServerID), script);
        final String taskScriptFileAddress = EASMgmtConstants.getTaskScriptFileAddress(Long.valueOf(nonEasServerID));
        try {
            final long waitDuration = TimeUnit.MINUTES.toSeconds(5L);
            File responseFile = new File(basePath + responseFiles.get(responseCounter));
            final File errorFile = new File(EASMgmtConstants.getExceptionTypeFileAddress(Long.valueOf(nonEasServerID)));
            final Process process = MDMApiFactoryProvider.getMDMUtilAPI().exec("PowerShell -ExecutionPolicy Unrestricted -NoLogo -noninteractive -command \". '" + taskScriptFileAddress + "'\" ");
            for (int i = 0; i < waitDuration; ++i) {
                if (!responseFile.exists() && !errorFile.exists()) {
                    EASMgmt.logger.log(Level.INFO, "awaiting response for {0} or {1}", new Object[] { responseFiles.get(responseCounter), errorFile.getName() });
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1L));
                }
                else if (responseFile.exists()) {
                    final String fileContent = MDMUtil.getNormalizedStringAndDeleteFile(responseFile);
                    final JSONObject ceaSyncDetails = new JSONObject();
                    ceaSyncDetails.put((Object)"EAS_Sync_Status_ID", (Object)ceaServerID);
                    ceaSyncDetails.put((Object)"REMARKS", (Object)MDMI18N.getI18Nmsg(fileContent, new Object[] { String.valueOf(responseCounter * (100 / responseFiles.size())) }));
                    EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(ceaSyncDetails);
                    EASMgmt.logger.log(Level.INFO, "got a response in {0}/{1} responseFile {2}", new Object[] { i, 600, responseFile.exists() });
                    if (++responseCounter == responseFiles.size()) {
                        installedEXOV2Module = true;
                        break;
                    }
                    responseFile = new File(basePath + responseFiles.get(responseCounter));
                }
                else if (errorFile.exists()) {
                    EASMgmt.logger.log(Level.INFO, "got a response in {0}/{1} errorFile {2}", new Object[] { i, 600, errorFile.exists() });
                    try {
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(errorFile.getAbsolutePath());
                    }
                    catch (final Exception ex) {
                        EASMgmt.logger.log(Level.SEVERE, "exception occured deleting file", ex);
                    }
                    break;
                }
            }
            pipe(process);
            process.destroy();
        }
        catch (final InterruptedException ex2) {
            EASMgmt.logger.log(Level.SEVERE, null, ex2);
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for the ps version check");
                }
            }
            catch (final Exception ex3) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex3);
            }
        }
        finally {
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)))) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(Long.valueOf(0L)));
                    EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for the ps version check");
                }
            }
            catch (final Exception ex4) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex4);
            }
        }
        return installedEXOV2Module;
    }
    
    static {
        PSSMgmt.pssProcessHandler = null;
    }
    
    class PSSProcess
    {
        private int pssState;
        private int taskCount;
        private Long easServerID;
        private Process process;
        private Long lastSessionActivity;
        
        PSSProcess(final Long serverID) {
            this.taskCount = 0;
            this.lastSessionActivity = null;
            this.easServerID = serverID;
            this.pssState = 0;
        }
        
        private void setPSSstate(final Integer pssState) {
            this.pssState = pssState;
        }
        
        private boolean isPSSProcessTerminated() {
            boolean pssProcessTerminated = true;
            try {
                if (this.process != null) {
                    final int exitValue = this.process.exitValue();
                    EASMgmt.logger.log(Level.INFO, "process exit value = {0}", exitValue);
                }
                else {
                    EASMgmt.logger.log(Level.INFO, "process not even initialized yet");
                }
            }
            catch (final IllegalThreadStateException exception) {
                pssProcessTerminated = false;
            }
            return pssProcessTerminated;
        }
        
        private void validatePSSstate() {
            if (this.pssState == 1) {
                final boolean pssProcessTerminated = this.isPSSProcessTerminated();
                if (!pssProcessTerminated) {
                    final File sessionScriptFile = new File(EASMgmtConstants.getSessionScriptFilePath(this.easServerID));
                    if (sessionScriptFile.exists()) {
                        EASMgmt.logger.log(Level.SEVERE, "pssState was set to true and script File is still availbale.. setting pssstate to dead session");
                    }
                }
                else {
                    EASMgmt.logger.log(Level.INFO, "process was terminated. Setting pssState to be dead");
                    this.setPSSstate(0);
                    this.safeClose();
                }
            }
            if (this.pssState == 0) {
                try {
                    final String sessionFolderPath = EASMgmtConstants.getDedicatedFolderPath(this.easServerID);
                    if (ApiFactoryProvider.getFileAccessAPI().isDirectory(sessionFolderPath)) {
                        ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(this.easServerID));
                        EASMgmt.logger.log(Level.WARNING, "deleted the dedicated folder for the exchange server, as it was not closed properly the last time!!!!");
                    }
                }
                catch (final Exception ex) {
                    EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex);
                }
            }
        }
        
        private Integer getPSSstate() {
            this.validatePSSstate();
            return this.pssState;
        }
        
        private boolean initiateSession(final String easAdminEmailAddress, final String easAdminPassword, final String connectionUri, final String authentication) {
            this.setPSSstate(2);
            final String sessionFolderPath = EASMgmtConstants.getDedicatedFolderPath(this.easServerID);
            final String sessionScriptFilePath = EASMgmtConstants.getSessionScriptFilePath(this.easServerID);
            final String timeStampFileAddress = sessionFolderPath + File.separator + String.valueOf(System.currentTimeMillis());
            try {
                try {
                    if (ApiFactoryProvider.getFileAccessAPI().isDirectory(sessionFolderPath)) {
                        ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(this.easServerID));
                        EASMgmt.logger.log(Level.WARNING, "deleted the dedicated folder for the exchange server, as it was not closed properly the last time!!!!");
                    }
                }
                catch (final Exception ex) {
                    EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex);
                }
                if (!ApiFactoryProvider.getFileAccessAPI().isDirectory(sessionFolderPath)) {
                    ApiFactoryProvider.getFileAccessAPI().createDirectory(sessionFolderPath);
                }
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, ex.toString());
            }
            final String sessionInitiationScript = PSSCMDGenerator.getInstance().getSessionScriptFor(this.easServerID, easAdminPassword, easAdminEmailAddress, connectionUri, authentication, timeStampFileAddress);
            PSSScriptGenerator.getInstance().writeCommandsToScript(this.easServerID, sessionInitiationScript, sessionScriptFilePath, false);
            boolean response = false;
            boolean proccessStarted = false;
            try {
                EASMgmt.logger.log(Level.INFO, "creating a new session, PowerShell -ExecutionPolicy Bypass -NoLogo -noninteractive -command \". ''{0}''\" ", sessionScriptFilePath);
                this.process = MDMApiFactoryProvider.getMDMUtilAPI().exec("PowerShell -ExecutionPolicy Bypass -NoLogo -noninteractive -command \". '" + sessionScriptFilePath + "'\" ");
                proccessStarted = true;
            }
            catch (final Exception ex2) {
                EASMgmtErrorHandler.getInstance().handleError(EASMgmtConstants.EAS_GENERIC_ERROR, this.easServerID, "START_SESSION_REQUEST");
                EASMgmt.logger.log(Level.SEVERE, "exception occured when trying to create a process", ex2);
            }
            if (proccessStarted) {
                final JSONObject responseParamsJSON = new JSONObject();
                responseParamsJSON.put((Object)"EAS_SERVER_ID", (Object)this.easServerID);
                responseParamsJSON.put((Object)"TASK_TYPE", (Object)"START_SESSION_REQUEST");
                responseParamsJSON.put((Object)"readFromFile", (Object)MDMApiFactoryProvider.getConditionalExchangeAccessApi().getReadFromFile());
                responseParamsJSON.put((Object)"EXPECTED_SUCCESS_RESULT_FILE", (Object)timeStampFileAddress);
                response = EASResponseHandler.getInstance().handleResponse(responseParamsJSON);
            }
            if (response) {
                this.setPSSstate(1);
                this.lastSessionActivity = System.currentTimeMillis();
            }
            else {
                this.closeSession();
                this.setPSSstate(0);
            }
            return response;
        }
        
        private boolean startSession(final JSONObject easServerDetailsJSON) {
            return this.initiateSession((String)easServerDetailsJSON.get((Object)"EAS_ADMIN_EMAIL"), (String)easServerDetailsJSON.get((Object)"EAS_ADMIN_PASSWORD"), (String)easServerDetailsJSON.get((Object)"CONNECTION_URI"), (String)easServerDetailsJSON.get((Object)"AUTH_MODE"));
        }
        
        private void safeClose() {
            final File sourceFile = new File(EASMgmtConstants.getTaskScriptFileAddress(this.easServerID));
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
            final File scriptFile = new File(EASMgmtConstants.getSessionScriptFilePath(this.easServerID));
            if (scriptFile.exists()) {
                scriptFile.delete();
            }
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(EASMgmtConstants.getDedicatedFolderPath(this.easServerID));
                EASMgmt.logger.log(Level.INFO, "deleted the dedicated folder for the exchange server{0}", this.easServerID);
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, "faced an error while trying to delete the folder directory", ex);
            }
            if (this.process != null) {
                this.process.destroy();
                PSSMgmt.pipe(this.process);
                this.process = null;
                EASMgmt.logger.log(Level.INFO, "POWERSHELL SESSION PROCESS FOR {0} ENDED AND DESTROYED", this.easServerID);
            }
            this.setPSSstate(0);
        }
        
        private void closeSession() {
            final String closeSessionScript = PSSCMDGenerator.getInstance().closeSessionScript();
            PSSScriptGenerator.getInstance().writeCommandsToScript(this.easServerID, closeSessionScript, false);
            final File sourceFile = new File(EASMgmtConstants.getTaskScriptFileAddress(this.easServerID));
            for (int i = 0; i < 20 && this.getPSSstate() != 0 && sourceFile.exists(); ++i) {
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {
                    EASMgmt.logger.log(Level.SEVERE, null, ex);
                }
            }
            this.setPSSstate(0);
            this.safeClose();
        }
    }
}
