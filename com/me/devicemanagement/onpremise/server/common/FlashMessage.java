package com.me.devicemanagement.onpremise.server.common;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import java.util.TreeSet;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import java.util.HashSet;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.onpremise.server.util.NotifyUpdatesUtil;
import java.util.Iterator;
import java.util.Date;
import java.io.FileWriter;
import java.util.Collection;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import java.util.Properties;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Set;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class FlashMessage
{
    protected JSONObject updatesJSON;
    protected JSONObject serverJSON;
    protected UpdatesAnalyzer updatesAnalyzer;
    protected int updatesJSONDownloadStatus;
    private static Logger logger;
    public static final String BUILD_NUMBER_ON_WEBSITE = "BuildNumberOnWebSite";
    public String latest_build_version;
    
    protected FlashMessage() {
        this.updatesJSON = new JSONObject();
        this.serverJSON = new JSONObject();
        this.updatesAnalyzer = null;
        this.updatesJSONDownloadStatus = 0;
        this.latest_build_version = new String();
        this.updatesAnalyzer = ((this.updatesAnalyzer == null) ? new UpdatesAnalyzer() : this.updatesAnalyzer);
        this.updatesJSONDownloadStatus = 0;
    }
    
    public void downloadAndInitialise() {
        checkAndClearVersionMsg();
        final String enableChecksumValidation = ProductUrlLoader.getInstance().getValue("enableChecksumValidation", Boolean.FALSE.toString());
        final String updatesJSONLoc = this.downloadUpdatesJSON(Boolean.parseBoolean(enableChecksumValidation));
        if (updatesJSONLoc != null) {
            final JSONParser jsonParser = new JSONParser();
            try {
                final File updatesJSONFile = new File(updatesJSONLoc);
                if (updatesJSONFile.exists()) {
                    final FileReader updatesReader = new FileReader(updatesJSONFile);
                    this.updatesJSON = (JSONObject)jsonParser.parse((Reader)updatesReader);
                    updatesReader.close();
                    final Set uniqueUpdateKeys = this.updatesAnalyzer.getUniqueConditionKeys(this.updatesJSON);
                    this.serverJSON = this.updatesAnalyzer.constructServerJSON(uniqueUpdateKeys);
                    final String versionContentLabel = this.isLargeNetworkCustomer() ? "LargeNetwork" : "VersionMsgContent";
                    final JSONObject versionMsgContent = (JSONObject)this.updatesJSON.get((Object)versionContentLabel);
                    final String version = (String)versionMsgContent.get((Object)"Version");
                    final String existingVersion = UpdatesParamUtil.getUpdParameter("UPDATE_MSG_VERSION");
                    if (existingVersion == null || !existingVersion.equalsIgnoreCase(version)) {
                        ResetUpdateParams();
                    }
                    this.checkAndShowMsg();
                }
            }
            catch (final Exception e) {
                FlashMessage.logger.log(Level.SEVERE, "Exception in FlashMessage:downloadAndInitialise()", e);
            }
        }
        if (this.updatesJSONDownloadStatus == 0 || this.updatesJSONDownloadStatus == 10010) {
            this.updateUpdatesConfPropsForPPMHandling(this.updatesJSON);
        }
        else {
            final String flashMsgFailedCount = UpdatesParamUtil.getUpdParameter("FLASH_MSG_FAILED_COUNT");
            Integer failedCount = (flashMsgFailedCount != null) ? Integer.valueOf(flashMsgFailedCount) : 0;
            ++failedCount;
            UpdatesParamUtil.updateUpdParams("FLASH_MSG_FAILED_COUNT", failedCount.toString());
        }
    }
    
    public void checkAndShowMsg() {
        FlashMessage.logger.log(Level.INFO, "Going to Check For Version Message");
        String versionLabel = null;
        String stopVersionMsg = "false";
        String stopFlashMsg = "false";
        String stopEOSMsg = "false";
        final Properties localProps = SyMUtil.getProductProperties();
        final String localMajorVer = localProps.getProperty("major_version");
        final int lMaj = Integer.parseInt(localMajorVer);
        final String localMinorVer = localProps.getProperty("minor_version");
        final int lMin = Integer.parseInt(localMinorVer);
        final String localSPVer = localProps.getProperty("sp_version");
        final int lSP = Integer.parseInt(localSPVer);
        final String localHFVer = localProps.getProperty("hf_version");
        final int lHF = Integer.parseInt(localHFVer);
        final String localPatchVer = localProps.getProperty("patch_version");
        int lPat = 0;
        if (localPatchVer != null) {
            lPat = Integer.parseInt(localPatchVer);
        }
        final boolean isLargeCustomer = this.isLargeNetworkCustomer();
        final String versionContentLabel = isLargeCustomer ? "LargeNetwork" : "VersionMsgContent";
        final JSONObject versionMsgContent = (JSONObject)this.updatesJSON.get((Object)versionContentLabel);
        final String remoteMajor = (String)versionMsgContent.get((Object)"MajorVersion");
        final int rMaj = Integer.parseInt(remoteMajor);
        final String remoteMinor = (String)versionMsgContent.get((Object)"MinorVersion");
        final int rMin = Integer.parseInt(remoteMinor);
        final String remoteSP = (String)versionMsgContent.get((Object)"ServicePack");
        final int rSP = Integer.parseInt(remoteSP);
        final String remoteHF = (String)versionMsgContent.get((Object)"HotFix");
        final int rHF = Integer.parseInt(remoteHF);
        final String remotePatch = (String)versionMsgContent.get((Object)"PatchVersion");
        int rPat = 0;
        if (remotePatch != null) {
            rPat = Integer.parseInt(remotePatch);
        }
        if (remotePatch != null) {
            this.latest_build_version = remoteMajor + "." + remoteSP + "." + remoteHF + "." + remotePatch;
        }
        else {
            this.latest_build_version = remoteMajor + "." + remoteSP + "." + remoteHF;
        }
        if (rMaj > lMaj || rMin > lMin) {
            versionLabel = "MajorVersion";
        }
        else if (rMaj == lMaj && rSP > lSP) {
            versionLabel = "ServicePack";
        }
        else if (rMaj == lMaj && rSP == lSP && rHF > lHF) {
            versionLabel = "HotFix";
        }
        else if (rPat != 0 && lPat != 0) {
            if (rMaj == lMaj && rSP == lSP && rHF == lHF && rPat > lPat) {
                versionLabel = "PatchVersion";
            }
        }
        else {
            FlashMessage.logger.log(Level.INFO, "No version Diffs Found");
        }
        final JSONObject stopMsgContent = (JSONObject)this.updatesJSON.get((Object)"StopMsgs");
        if (stopMsgContent != null) {
            stopVersionMsg = (String)(stopMsgContent.containsKey((Object)versionLabel) ? stopMsgContent.get((Object)versionLabel) : stopVersionMsg);
            stopFlashMsg = (String)(stopMsgContent.containsKey((Object)"FlashMsg") ? stopMsgContent.get((Object)"FlashMsg") : stopFlashMsg);
            stopEOSMsg = (String)(stopMsgContent.containsKey((Object)"EndOfSupport") ? stopMsgContent.get((Object)"EndOfSupport") : stopEOSMsg);
        }
        UpdatesParamUtil.updateUpdParams("STOP_VERSION_MESSAGE", stopVersionMsg);
        UpdatesParamUtil.updateUpdParams("STOP_FLASH_MESSAGE", stopFlashMsg);
        UpdatesParamUtil.updateUpdParams("STOP_EOS_MESSAGE", stopEOSMsg);
        if (stopVersionMsg.equalsIgnoreCase("False")) {
            this.checkForVersionMessage(versionLabel, localProps);
        }
        if (stopEOSMsg.equalsIgnoreCase("False")) {
            this.checkForEOLMessage(isLargeCustomer);
        }
        else {
            resetEOSUpdParams();
        }
        if (stopFlashMsg.equalsIgnoreCase("False")) {
            if (this.downloadFlashMessageJSON()) {
                this.checkAndDownloadFlashMessage(true);
            }
            else {
                try {
                    final String flashMessagFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "flashMessage.json";
                    FileUtil.deleteFileOrFolder(new File(flashMessagFileName));
                }
                catch (final Exception e) {
                    FlashMessage.logger.log(Level.SEVERE, "Exception while deleting local Flash Message JSON File", e);
                }
            }
        }
    }
    
    public boolean downloadFlashMessageJSON() {
        final JSONArray flashMessageArray = (JSONArray)this.updatesJSON.get((Object)"FlashMsg");
        JSONObject flashMessageLoc = new JSONObject();
        final JSONObject flashMessageServer = new JSONObject();
        JSONArray closedKeys = null;
        final JSONArray flashMsgKeys = new JSONArray();
        final JSONObject startDates = new JSONObject();
        final JSONArray priorityMsgKeys = new JSONArray();
        try {
            final String flashMessagFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "flashMessage.json";
            final File flasgMsgOutFile = new File(flashMessagFileName);
            if (flasgMsgOutFile.exists()) {
                final JSONParser jsonParser = new JSONParser();
                final FileReader flashMessageReader = new FileReader(flasgMsgOutFile);
                flashMessageLoc = (JSONObject)jsonParser.parse((Reader)flashMessageReader);
                closedKeys = (JSONArray)flashMessageLoc.get((Object)"ClosedKeys");
            }
            if (closedKeys == null) {
                closedKeys = new JSONArray();
            }
            for (int i = 0; i < flashMessageArray.size(); ++i) {
                final JSONObject flashMessage = (JSONObject)flashMessageArray.get(i);
                final String msgKey = (String)flashMessage.get((Object)"MSG_KEY");
                if (msgKey != null) {
                    final String isHighPriority = (String)flashMessage.get((Object)"isHighPriority");
                    if (isHighPriority != null && isHighPriority.equalsIgnoreCase("true")) {
                        priorityMsgKeys.add((Object)msgKey);
                    }
                    else {
                        flashMsgKeys.add((Object)msgKey);
                    }
                    if (!closedKeys.contains((Object)msgKey)) {
                        flashMessageServer.put((Object)msgKey, (Object)flashMessage);
                        final String startDateStr = (String)flashMessage.get((Object)"StartDate");
                        final String endDateStr = (String)flashMessage.get((Object)"EndDate");
                        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        if (startDateStr != null && startDateStr.trim().length() != 0 && endDateStr != null && endDateStr.trim().length() != 0) {
                            try {
                                final Date startDate = sdf.parse(startDateStr);
                                startDates.put((Object)startDate.getTime(), (Object)startDateStr);
                            }
                            catch (final Exception e) {
                                FlashMessage.logger.log(Level.INFO, "Inalid or no date for flash message with msg key " + msgKey);
                            }
                        }
                    }
                }
            }
            closedKeys.retainAll((Collection)flashMsgKeys);
            final Set<String> uniqueUpdateKeys = this.updatesAnalyzer.getUniqueConditionKeys(this.updatesJSON);
            final JSONArray uniqueUpdateKeysArray = new JSONArray();
            for (final String uniqueUpdateKey : uniqueUpdateKeys) {
                uniqueUpdateKeysArray.add((Object)uniqueUpdateKey);
            }
            flashMessageServer.put((Object)"UniqueUpdateKeys", (Object)uniqueUpdateKeysArray);
            flashMessageServer.put((Object)"FlashMsgKeys", (Object)flashMsgKeys);
            flashMessageServer.put((Object)"ClosedKeys", (Object)closedKeys);
            flashMessageServer.put((Object)"StartDates", (Object)startDates);
            if (priorityMsgKeys.size() != 0) {
                flashMessageServer.put((Object)"PriorityMsgKeys", (Object)priorityMsgKeys);
            }
            final FileWriter file = new FileWriter(flasgMsgOutFile);
            file.write(flashMessageServer.toJSONString());
            file.flush();
            file.close();
            return true;
        }
        catch (final Exception e2) {
            FlashMessage.logger.log(Level.SEVERE, "Exception while Downloading Flash Message JSON:downloadFlashMessageJSON()", e2);
            return false;
        }
    }
    
    public String downloadUpdatesJSON() {
        return this.downloadUpdatesJSON(false);
    }
    
    public String downloadUpdatesJSON(final boolean validateChecksum) {
        final String updatesCheckerURL = ProductUrlLoader.getInstance().getValue("updates_check_url");
        if (updatesCheckerURL == null || updatesCheckerURL.trim().length() == 0) {
            return null;
        }
        try {
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "updates.json";
            final File outFile = new File(outFileName);
            if (outFile.exists()) {
                outFile.delete();
            }
            final String proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
            if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                final boolean isUpdatesJSON = Boolean.TRUE;
                this.updatesJSONDownloadStatus = this.copyRemoteFileUsingHTTP(updatesCheckerURL, outFileName, isUpdatesJSON, validateChecksum);
            }
            else {
                FlashMessage.logger.log(Level.INFO, "Proxy is Not Defined, Hence couldn't download Updates JSON");
            }
            return outFileName;
        }
        catch (final Exception e) {
            FlashMessage.logger.log(Level.SEVERE, "Exception while Downloading Updates JSON", e);
            return null;
        }
    }
    
    public void checkForVersionMessage(final String versionLabel, final Properties localProps) {
        FlashMessage.logger.log(Level.INFO, "Inside checkForVersionMessage");
        final boolean isLargeCustomer = this.isLargeNetworkCustomer();
        final String versionContentLabel = isLargeCustomer ? "LargeNetwork" : "VersionMsgContent";
        final JSONObject versionMsgContent = (JSONObject)this.updatesJSON.get((Object)versionContentLabel);
        final String version = (String)versionMsgContent.get((Object)"Version");
        boolean isConditionMatched = Boolean.FALSE;
        if (versionLabel != null) {
            FlashMessage.logger.log(Level.INFO, versionLabel + " Diffs Found");
            isConditionMatched = this.checkConditionAndShowVersionMsg(versionLabel, isLargeCustomer);
        }
        if (!isConditionMatched) {
            FlashMessage.logger.log(Level.WARNING, "FlashMessage: No conditions met while checking updates...");
            FlashMessage.logger.log(Level.INFO, "Remote Updates data: " + versionMsgContent + "\n Local Updates data: " + localProps);
        }
        UpdatesParamUtil.updateUpdParams("UPDATE_MSG_VERSION", version);
    }
    
    public void checkForEOLMessage(final boolean isLargeCustomer) {
        final String versionContentLabel = isLargeCustomer ? "LargeNetwork" : "VersionMsgContent";
        final JSONObject versionMsgContent = (JSONObject)this.updatesJSON.get((Object)versionContentLabel);
        final JSONObject endOfSupportJSON = (JSONObject)versionMsgContent.get((Object)"EndOfSupport");
        if (endOfSupportJSON == null) {
            return;
        }
        final JSONObject eosPopupAlertJSON = (JSONObject)endOfSupportJSON.get((Object)"PopupAlert");
        final JSONObject eosVHAlertJSON = (JSONObject)endOfSupportJSON.get((Object)"VersionHoverAlert");
        final JSONObject eosTBAlertJSON = (JSONObject)endOfSupportJSON.get((Object)"TopBannerAlert");
        final JSONObject eosSupportAlertJSON = (JSONObject)endOfSupportJSON.get((Object)"SupportAlert");
        final Map<String, Long> eolDetailMap = this.getEOLDetail(endOfSupportJSON);
        boolean isSupportEnded = false;
        String eolDate = null;
        String eosPopupTitleLabel = null;
        String eosPopupMsgAdminLabel = null;
        String eosPopupMsgTechLabel = null;
        String eosPopupTitle = null;
        String eosPopupMsgAdmin = null;
        String eosPopupMsgTech = null;
        String eosVHMsgAdminLabel = null;
        String eosVHMsgTechLabel = null;
        String eosVHMsgAdmin = null;
        String eosVHMsgTech = null;
        String eosTBMsgAdminLabel = null;
        String eosTBMsgTechLabel = null;
        String eosTBMsgAdmin = null;
        String eosTBMsgTech = null;
        String eosSupportTitleLabel = null;
        String eosSupportMsgLabel = null;
        String eosSupportTitle = null;
        String eosSupportMsg = null;
        if (!eolDetailMap.isEmpty() && eolDetailMap.containsKey("EOLDate")) {
            eolDate = String.valueOf(eolDetailMap.get("EOLDate"));
            isSupportEnded = (Long.parseLong(eolDate) <= SyMUtil.getCurrentTimeInMillis());
            if (!isSupportEnded) {
                eosPopupTitleLabel = "Title_Before";
                eosPopupMsgAdminLabel = "Msg_Before_Admin";
                eosPopupMsgTechLabel = "Msg_Before_Tech";
                eosVHMsgAdminLabel = "Msg_Before_Admin";
                eosVHMsgTechLabel = "Msg_Before_Tech";
                eosTBMsgAdminLabel = "Msg_Before_Admin";
                eosTBMsgTechLabel = "Msg_Before_Tech";
                eosSupportTitleLabel = "Title_Before";
                eosSupportMsgLabel = "Msg_Before";
            }
            else {
                eosPopupTitleLabel = "Title_After";
                eosPopupMsgAdminLabel = "Msg_After_Admin";
                eosPopupMsgTechLabel = "Msg_After_Tech";
                eosVHMsgAdminLabel = "Msg_After_Admin";
                eosVHMsgTechLabel = "Msg_After_Tech";
                eosTBMsgAdminLabel = "Msg_After_Admin";
                eosTBMsgTechLabel = "Msg_After_Tech";
                eosSupportTitleLabel = "Title_After";
                eosSupportMsgLabel = "Msg_After";
            }
            eosPopupTitle = this.replaceTemplateValues((String)eosPopupAlertJSON.get((Object)eosPopupTitleLabel), eolDetailMap);
            eosPopupMsgAdmin = this.replaceTemplateValues((String)eosPopupAlertJSON.get((Object)eosPopupMsgAdminLabel), eolDetailMap);
            eosPopupMsgTech = this.replaceTemplateValues((String)eosPopupAlertJSON.get((Object)eosPopupMsgTechLabel), eolDetailMap);
            eosVHMsgAdmin = this.replaceTemplateValues((String)eosVHAlertJSON.get((Object)eosVHMsgAdminLabel), eolDetailMap);
            eosVHMsgTech = this.replaceTemplateValues((String)eosVHAlertJSON.get((Object)eosVHMsgTechLabel), eolDetailMap);
            eosTBMsgAdmin = this.replaceTemplateValues((String)eosTBAlertJSON.get((Object)eosTBMsgAdminLabel), eolDetailMap);
            eosTBMsgTech = this.replaceTemplateValues((String)eosTBAlertJSON.get((Object)eosTBMsgTechLabel), eolDetailMap);
            eosSupportTitle = this.replaceTemplateValues((String)eosSupportAlertJSON.get((Object)eosSupportTitleLabel), eolDetailMap);
            eosSupportMsg = this.replaceTemplateValues((String)eosSupportAlertJSON.get((Object)eosSupportMsgLabel), eolDetailMap);
        }
        this.updateUpdParamsIfNotEmpty("EOS_POPUP_TITLE", eosPopupTitle);
        this.updateUpdParamsIfNotEmpty("EOS_POPUP_MSG_ADMIN", eosPopupMsgAdmin);
        this.updateUpdParamsIfNotEmpty("EOS_POPUP_MSG_TECH", eosPopupMsgTech);
        this.updateUpdParamsIfNotEmpty("EOS_VH_MSG_ADMIN", eosVHMsgAdmin);
        this.updateUpdParamsIfNotEmpty("EOS_VH_MSG_TECH", eosVHMsgTech);
        this.updateUpdParamsIfNotEmpty("EOS_TB_MSG_ADMIN", eosTBMsgAdmin);
        this.updateUpdParamsIfNotEmpty("EOS_TB_MSG_TECH", eosTBMsgTech);
        this.updateUpdParamsIfNotEmpty("EOS_SUPPORT_TITLE", eosSupportTitle);
        this.updateUpdParamsIfNotEmpty("EOS_SUPPORT_MSG", eosSupportMsg);
        this.updateUpdParamsIfNotEmpty("EOL_DATE", eolDate);
        try {
            NotifyUpdatesUtil.getInstance().deleteLoginIdsFromEosNotifyTable();
        }
        catch (final Exception ex) {
            FlashMessage.logger.log(Level.SEVERE, "Exception caught while deleting login ids from NotifiedUserForEOS table: ", ex);
        }
    }
    
    private Map<String, Long> getEOLDetail(final JSONObject endOfSupportJSON) {
        final Map<String, Long> eolDetailMap = new HashMap<String, Long>();
        try {
            final JSONObject eolDetailsJSON = (JSONObject)endOfSupportJSON.get((Object)"EOLVersion");
            if (eolDetailsJSON != null) {
                final TreeMap<Integer, Long> eolDetailsMap = new TreeMap<Integer, Long>();
                final Set<Map.Entry> entrySet = eolDetailsJSON.entrySet();
                for (final Map.Entry element : entrySet) {
                    final Integer key = Integer.parseInt(element.getKey());
                    final Long value = Long.parseLong(element.getValue());
                    eolDetailsMap.put(key, value);
                }
                final Integer currentVersion = DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
                eolDetailMap.put("ServerVersion", (long)currentVersion);
                eolDetailMap.put("MaxEOLVersion", (long)eolDetailsMap.lastKey());
                if (eolDetailsMap.containsKey(currentVersion)) {
                    eolDetailMap.put("EOLDate", eolDetailsMap.get(currentVersion));
                }
                else {
                    final Integer higherVersion = eolDetailsMap.higherKey(currentVersion);
                    if (higherVersion != null) {
                        eolDetailMap.put("EOLDate", eolDetailsMap.get(higherVersion));
                    }
                }
            }
        }
        catch (final Exception ex) {
            FlashMessage.logger.log(Level.SEVERE, "Exception while checking support EOL {0}", ex);
        }
        return eolDetailMap;
    }
    
    public boolean checkConditionAndShowVersionMsg(final String versionLabel, final boolean isLargeCustomer) {
        FlashMessage.logger.log(Level.FINE, "Going to CheckConditionAndShowVersionMsg");
        final JSONArray versionMsgArray = (JSONArray)this.updatesJSON.get((Object)versionLabel);
        final String versionContentLabel = isLargeCustomer ? "LargeNetwork" : "VersionMsgContent";
        final JSONObject versionMsgContent = (JSONObject)this.updatesJSON.get((Object)versionContentLabel);
        String updateMsgTitle = null;
        String updateMsg = null;
        String downloadURL = null;
        boolean isConditionMatched = Boolean.FALSE;
        String updateMsgPriority = null;
        for (int i = 0; i < versionMsgArray.size(); ++i) {
            final JSONObject versionMsg = (JSONObject)versionMsgArray.get(i);
            isConditionMatched = this.updatesAnalyzer.compareJSONS(versionMsg, this.serverJSON);
            if (isConditionMatched) {
                switch (versionLabel) {
                    case "MajorVersion": {
                        updateMsgTitle = (String)versionMsgContent.get((Object)"MajorVersion_Title");
                        updateMsg = (String)versionMsgContent.get((Object)"MajorVersion_MSG");
                        downloadURL = (String)versionMsgContent.get((Object)"MajorVersion_URL");
                        updateMsgPriority = (String)versionMsgContent.get((Object)"MajorVersion_Priority");
                        break;
                    }
                    case "ServicePack": {
                        updateMsgTitle = (String)versionMsgContent.get((Object)"SP_Title");
                        updateMsg = (String)versionMsgContent.get((Object)"SP_MSG");
                        downloadURL = (String)versionMsgContent.get((Object)"SP_URL");
                        updateMsgPriority = (String)versionMsgContent.get((Object)"SP_Priority");
                        break;
                    }
                    case "HotFix": {
                        updateMsgTitle = (String)versionMsgContent.get((Object)"HF_Title");
                        updateMsg = (String)versionMsgContent.get((Object)"HF_MSG");
                        downloadURL = (String)versionMsgContent.get((Object)"HF_URL");
                        updateMsgPriority = (String)versionMsgContent.get((Object)"HF_Priority");
                        break;
                    }
                    case "PatchVersion": {
                        updateMsgTitle = (String)versionMsgContent.get((Object)"Patch_Title");
                        updateMsg = (String)versionMsgContent.get((Object)"Patch_MSG");
                        downloadURL = (String)versionMsgContent.get((Object)"Patch_URL");
                        updateMsgPriority = (String)versionMsgContent.get((Object)"Patch_Priority");
                        break;
                    }
                }
                if (downloadURL != null) {
                    if (downloadURL.contains("?")) {
                        downloadURL = downloadURL + "&buildNumber=" + com.me.devicemanagement.framework.server.util.SyMUtil.getProductProperty("productversion");
                    }
                    else {
                        downloadURL = downloadURL + "?buildNumber=" + com.me.devicemanagement.framework.server.util.SyMUtil.getProductProperty("productversion");
                    }
                }
                this.updateUpdParamsIfNotEmpty("PRODUCT_UPDATE_MSG", updateMsg);
                this.updateUpdParamsIfNotEmpty("PRODUCT_UPDATE_MSG_TITLE", updateMsgTitle);
                this.updateUpdParamsIfNotEmpty("UPDATE_DOWNLOAD_URL", downloadURL);
                this.updateUpdParamsIfNotEmpty("UPDATE_MESSAGE_PRIORITY", updateMsgPriority);
                UpdatesParamUtil.updateUpdParams("UPDATE_VERSION_TYPE", versionLabel);
                UpdatesParamUtil.updateUpdParams("UPDATE_VERSION", (String)versionMsgContent.get((Object)versionLabel));
                UpdatesParamUtil.updateUpdParams("LATEST_BUILD_VERSION", this.latest_build_version);
                if (versionLabel.equalsIgnoreCase("MajorVersion")) {
                    UpdatesParamUtil.updateUpdParams("UPDATE_VERSION_2", (String)versionMsgContent.get((Object)"MinorVersion"));
                }
                FlashMessage.logger.log(Level.INFO, "Version Msg to be shown is: Title" + updateMsgTitle + " Message:" + updateMsg + "URL:" + downloadURL);
                try {
                    NotifyUpdatesUtil.getInstance().deleteLoginIdsFromNotifyTable();
                }
                catch (final Exception ex) {
                    FlashMessage.logger.info("Exception caught while deleting login ids from NotifiedUserforUpdates table: " + ex);
                }
                this.checkAndShowRuleBasedMsg(versionMsg);
                return isConditionMatched;
            }
        }
        return isConditionMatched;
    }
    
    private void updateUpdParamsIfNotEmpty(final String paramName, final String paramValue) {
        if (paramValue != null && paramValue.trim().length() > 0) {
            UpdatesParamUtil.updateUpdParams(paramName, paramValue);
        }
    }
    
    private String replaceTemplateValues(String msg, final Map<String, Long> eolDetailMap) {
        if (!eolDetailMap.isEmpty()) {
            if (eolDetailMap.containsKey("EOLDate")) {
                final Date date = new Date(eolDetailMap.get("EOLDate"));
                final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                final String strDate = formatter.format(date);
                msg = msg.replaceAll("<EOLDATE>", strDate);
            }
            if (eolDetailMap.containsKey("ServerVersion")) {
                msg = msg.replaceAll("<CUSTOMERBUILDNUMBER>", String.valueOf(eolDetailMap.get("ServerVersion")));
            }
            if (eolDetailMap.containsKey("MaxEOLVersion")) {
                msg = msg.replaceAll("<MAXEOLVERSION>", String.valueOf(eolDetailMap.get("MaxEOLVersion")));
            }
            msg = msg.replaceAll("<SERVICEPACKURL>", String.valueOf(UpdatesParamUtil.getUpdParameter("UPDATE_DOWNLOAD_URL")));
        }
        return msg;
    }
    
    public void checkAndDownloadFlashMessage(final boolean isJSONUpdated) {
        FlashMessage.logger.log(Level.INFO, "Going to CheckAndDownloadFlashMessage");
        JSONObject flashMessageLoc = new JSONObject();
        try {
            final String flashMessagFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "flashMessage.json";
            final File flasgMsgOutFile = new File(flashMessagFileName);
            if (flasgMsgOutFile.exists()) {
                final JSONParser jsonParser = new JSONParser();
                final FileReader flashMessageReader = new FileReader(flasgMsgOutFile);
                flashMessageLoc = (JSONObject)jsonParser.parse((Reader)flashMessageReader);
                boolean shownPriorityMsg = false;
                final Set<String> uniqueKeys = new HashSet<String>((Collection<? extends String>)flashMessageLoc.get((Object)"UniqueUpdateKeys"));
                if (uniqueKeys != null) {
                    this.serverJSON = this.updatesAnalyzer.constructServerJSON(uniqueKeys);
                }
                final ArrayList<String> msgKeys = (ArrayList<String>)flashMessageLoc.get((Object)"FlashMsgKeys");
                final ArrayList<String> priorityMsgKeys = (ArrayList<String>)flashMessageLoc.get((Object)"PriorityMsgKeys");
                if (priorityMsgKeys != null) {
                    shownPriorityMsg = this.downloadFlashMessage(priorityMsgKeys, flashMessageLoc, false, isJSONUpdated);
                }
                if (msgKeys != null && !shownPriorityMsg && !this.downloadFlashMessage(msgKeys, flashMessageLoc, false, isJSONUpdated)) {
                    UpdatesParamUtil.updateUpdParams("FLASH_NEWS_DISABLE", "true");
                    final String nextStartDate = this.getNextFlashMsgStartDate(flashMessageLoc, new Date());
                    if (nextStartDate != null) {
                        this.updateFlashMessageSchedule(nextStartDate, isJSONUpdated);
                    }
                }
            }
        }
        catch (final Exception e) {
            FlashMessage.logger.log(Level.SEVERE, "Exception in FlashMessage:checkAndDownloadFlashMessage()", e);
        }
    }
    
    public boolean downloadFlashMessage(final ArrayList<String> msgKeys, final JSONObject flashMessageLoc, final boolean isSecondary, final boolean isJSONUpdated) {
        try {
            final ArrayList<String> secondarymsgKeys = new ArrayList<String>();
            for (final String msgKey : msgKeys) {
                final JSONObject flashMessage = (JSONObject)flashMessageLoc.get((Object)msgKey);
                if (flashMessage != null) {
                    final String flashVersion = (String)flashMessage.get((Object)"Version");
                    final String existingMsgKey = UpdatesParamUtil.getUpdParameter("FLASH_NEWS_MSG_KEY");
                    final boolean isConditionMatching = this.updatesAnalyzer.compareJSONS(flashMessage, this.serverJSON);
                    final ChecksumProvider.ChecksumType checksumType = ChecksumProvider.ChecksumType.getChecksumType((String)flashMessage.get((Object)"checksumtype"));
                    final String flashHtmlFile = (String)flashMessage.get((Object)"FileName");
                    final String flashHtmlChecksum = (String)flashMessage.getOrDefault((Object)"filechecksum", (Object)"");
                    final String flashImage = (String)flashMessage.get((Object)"Images");
                    final String imageChecksum = (String)flashMessage.getOrDefault((Object)"imagechecksum", (Object)"");
                    final String flashShow = (String)flashMessage.get((Object)"MSG_SHOW");
                    final String startDateStr = (String)flashMessage.get((Object)"StartDate");
                    final String endDateStr = (String)flashMessage.get((Object)"EndDate");
                    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    final String todayStr = sdf.format(new Date());
                    final Date today = sdf.parse(todayStr);
                    boolean isStartDateValid = false;
                    boolean isEndDateValid = false;
                    Label_0397: {
                        if (startDateStr != null && startDateStr.trim().length() != 0 && endDateStr != null && endDateStr.trim().length() != 0 && !isSecondary) {
                            try {
                                final Date startDate = sdf.parse(startDateStr);
                                if (today.compareTo(startDate) >= 0) {
                                    isStartDateValid = true;
                                }
                                if (endDateStr.trim().length() != 0) {
                                    final Date endDate = sdf.parse(endDateStr);
                                    if (today.compareTo(endDate) < 0) {
                                        isEndDateValid = true;
                                    }
                                }
                                break Label_0397;
                            }
                            catch (final Exception e) {
                                FlashMessage.logger.log(Level.INFO, "Invalid Date format or No Date for version " + msgKey + ", so pushing it as secondary message");
                                secondarymsgKeys.add(msgKey);
                                continue;
                            }
                        }
                        if (!isSecondary) {
                            secondarymsgKeys.add(msgKey);
                            continue;
                        }
                    }
                    if (msgKey.equalsIgnoreCase(existingMsgKey) && isConditionMatching && flashShow.equalsIgnoreCase("true") && ((isStartDateValid && isEndDateValid) || isSecondary)) {
                        if (isSecondary && isJSONUpdated) {
                            final String nextStartDate = this.getNextFlashMsgStartDate(flashMessageLoc, new Date());
                            if (nextStartDate != null) {
                                this.updateFlashMessageSchedule(nextStartDate, isJSONUpdated);
                            }
                        }
                        return true;
                    }
                    if (!msgKey.equalsIgnoreCase(existingMsgKey) && isConditionMatching && flashShow.equalsIgnoreCase("true") && ((isStartDateValid && isEndDateValid) || isSecondary)) {
                        if (this.downloadFlashMsgFilesWithChecksumValidation(checksumType, flashHtmlFile, flashHtmlChecksum, flashImage, imageChecksum)) {
                            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_VERSION", flashVersion);
                            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_MSG_KEY", msgKey);
                            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_MSG_SHOW", flashShow);
                            FlashMessage.logger.log(Level.INFO, "Going to Show Flash Message of MSG KEY:" + msgKey + " HTML:" + flashHtmlFile + "Image:" + flashImage);
                            UpdatesParamUtil.updateUpdParams("FLASH_NEWS_DISABLE", "false");
                            if (isStartDateValid && isEndDateValid) {
                                this.updateFlashMessageSchedule(endDateStr, isJSONUpdated);
                            }
                            else if (isSecondary) {
                                final String nextStartDate = this.getNextFlashMsgStartDate(flashMessageLoc, today);
                                if (nextStartDate != null) {
                                    this.updateFlashMessageSchedule(nextStartDate, isJSONUpdated);
                                }
                            }
                            if (flashMessage.keySet().contains("Mobile_Notification")) {
                                MobileMarketingNotification.generateMobileNotification(flashMessage.get((Object)"Mobile_Notification").toString(), msgKey);
                            }
                            return true;
                        }
                        final String flashMsgImgFailedCount = UpdatesParamUtil.getUpdParameter("FLASH_MSG_IMG_FAILED_COUNT");
                        Integer failedCount = (flashMsgImgFailedCount != null) ? Integer.valueOf(flashMsgImgFailedCount) : 0;
                        ++failedCount;
                        UpdatesParamUtil.updateUpdParams("FLASH_MSG_IMG_FAILED_COUNT", failedCount.toString());
                        if (flashMessageLoc.keySet().contains("Mobile_Notification")) {
                            MobileMarketingNotification.generateMobileNotification(flashMessage.get((Object)"Mobile_Notification").toString(), msgKey);
                        }
                        return false;
                    }
                    else {
                        if ((!msgKey.equalsIgnoreCase(existingMsgKey) || !isConditionMatching || !flashShow.equalsIgnoreCase("false")) && (isEndDateValid || !msgKey.equalsIgnoreCase(existingMsgKey) || isSecondary)) {
                            continue;
                        }
                        FlashMessage.logger.log(Level.INFO, "Going to Hide Flash Message of MSG KEY:" + msgKey + "HTML:" + flashHtmlFile + "Image:" + flashImage);
                        UpdatesParamUtil.updateUpdParams("FLASH_NEWS_MSG_SHOW", flashShow);
                        UpdatesParamUtil.updateUpdParams("FLASH_NEWS_DISABLE", "true");
                    }
                }
            }
            if (!isSecondary && secondarymsgKeys.size() != 0 && this.downloadFlashMessage(secondarymsgKeys, flashMessageLoc, true, isJSONUpdated)) {
                return true;
            }
        }
        catch (final Exception e2) {
            FlashMessage.logger.log(Level.SEVERE, "Exception in FlashMessage:downloadFlashMessage()", e2);
        }
        return false;
    }
    
    public void updateFlashMessageSchedule(final String dateStr, final boolean isJSONUpdated) {
        FlashMessage.logger.log(Level.INFO, "Creating schedule for next flash message update at " + dateStr);
        final HashMap flashMessageUpdateProps = FlashMessageUpdateTaskUtil.getInstance().constructDefaultTaskProps();
        final String[] dateTime = dateStr.split(" ");
        flashMessageUpdateProps.put("date", dateTime[0]);
        flashMessageUpdateProps.put("time", dateTime[1]);
        if (isJSONUpdated) {
            FlashMessageUpdateTaskUtil.getInstance().deleteFlashMessageUpdateTask("Flash_Message_Update_Schedule_1");
            FlashMessageUpdateTaskUtil.getInstance().deleteFlashMessageUpdateTask("Flash_Message_Update_Schedule_2");
        }
        final Long taskId = ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule("Flash_Message_Update_Schedule_1");
        if (taskId != null) {
            flashMessageUpdateProps.put("schedulerName", "Flash_Message_Update_Schedule_2");
            flashMessageUpdateProps.put("taskName", "FlashMessageUpdateTask2");
        }
        ApiFactoryProvider.getSchedulerAPI().createScheduler(flashMessageUpdateProps);
    }
    
    public String getNextFlashMsgStartDate(final JSONObject flashMessageLoc, final Date today) {
        final JSONObject startDates = (JSONObject)flashMessageLoc.get((Object)"StartDates");
        String startDateStr = null;
        final TreeSet<String> dateKeys = new TreeSet<String>(startDates.keySet());
        final Long todayTime = today.getTime();
        for (final String startDateTime : dateKeys) {
            if (Long.parseLong(startDateTime) > todayTime) {
                startDateStr = (String)startDates.get((Object)startDateTime);
                break;
            }
        }
        return startDateStr;
    }
    
    public void flashMessageStatusUpdate() {
        FlashMessage.logger.log(Level.INFO, "Updating  Flash Message Status");
        final String msgKey = UpdatesParamUtil.getUpdParameter("FLASH_NEWS_MSG_KEY");
        JSONObject flashMessageLoc = new JSONObject();
        try {
            final String flashMessagFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "flashMessage.json";
            final File flasgMsgOutFile = new File(flashMessagFileName);
            if (flasgMsgOutFile.exists()) {
                final JSONParser jsonParser = new JSONParser();
                final FileReader flashMessageReader = new FileReader(flasgMsgOutFile);
                flashMessageLoc = (JSONObject)jsonParser.parse((Reader)flashMessageReader);
            }
            flashMessageLoc.remove((Object)msgKey);
            final JSONArray closedKeys = (JSONArray)flashMessageLoc.get((Object)"ClosedKeys");
            if (closedKeys != null) {
                closedKeys.add((Object)msgKey);
                flashMessageLoc.put((Object)"ClosedKeys", (Object)closedKeys);
                final FileWriter file = new FileWriter(flasgMsgOutFile);
                file.write(flashMessageLoc.toJSONString());
                file.flush();
                file.close();
            }
        }
        catch (final Exception e) {
            FlashMessage.logger.log(Level.SEVERE, "Exception in updating FlashMessage:flashMessageStatusUpdate()", e);
        }
    }
    
    public boolean downloadFlashMsgFilesWithChecksumValidation(final ChecksumProvider.ChecksumType checksumType, final String flashHtmlFile, final String flashHtmlChecksum, final String falshImages, final String flashImageChecksumsCommaSeparated) {
        FlashMessage.logger.log(Level.FINE, "Starting to Download FlashMsgFiles");
        try {
            boolean downloadSucc = Boolean.TRUE;
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "flashmsg" + File.separator;
            final FileUtil fileUtil = new FileUtil();
            fileUtil.deleteFolderFiles(new File(outFileName));
            String inFileURL = ProductUrlLoader.getInstance().getValue("updates_check_url");
            inFileURL = inFileURL.replace(inFileURL.substring(inFileURL.lastIndexOf("/") + 1), "flashmsg/");
            final String[] imageList = falshImages.split(",");
            final String htmlpath = inFileURL + flashHtmlFile;
            final String outHtmlPath = outFileName + "flashMsg.html";
            final boolean isUpdatesJSON = Boolean.FALSE;
            this.copyRemoteFileUsingHTTP(htmlpath, outHtmlPath, isUpdatesJSON);
            if (!flashHtmlChecksum.isEmpty() && !ChecksumProvider.getInstance().ValidateFileCheckSum(outHtmlPath, flashHtmlChecksum, checksumType.getValue())) {
                FlashMessage.logger.log(Level.WARNING, "FlashMessage: Checksumvalidation failed for Flash Html file");
                downloadSucc = Boolean.FALSE;
            }
            else {
                downloadSucc = new File(outFileName).exists();
            }
            if (downloadSucc) {
                final String[] imageChecksumList = flashImageChecksumsCommaSeparated.split(",");
                final boolean isChecksumValid = !flashImageChecksumsCommaSeparated.isEmpty() && imageChecksumList.length == imageList.length;
                for (int i = 0; i < imageList.length; ++i) {
                    this.copyRemoteFileUsingHTTP(inFileURL + imageList[i], outFileName + imageList[i], isUpdatesJSON);
                    downloadSucc = new File(outFileName + imageList[i]).exists();
                    if (isChecksumValid) {
                        final String imageChecksum = imageChecksumList[i];
                        if (imageChecksum != null && !ChecksumProvider.getInstance().ValidateFileCheckSum(outFileName + imageList[i], imageChecksum, checksumType.getValue())) {
                            FlashMessage.logger.log(Level.WARNING, "FlashMessage: Checksumvalidation failed for Flash image file");
                            downloadSucc = false;
                        }
                    }
                    if (!downloadSucc) {
                        break;
                    }
                }
            }
            if (!downloadSucc) {
                FlashMessage.logger.log(Level.WARNING, "Download of Flash Message Files Failed", flashHtmlFile);
                fileUtil.deleteFolderFiles(new File(outFileName));
            }
            return downloadSucc;
        }
        catch (final Exception ex) {
            FlashMessage.logger.log(Level.WARNING, "FlashMessage: Caught exception while downloadFlashMsgFiles", ex);
            try {
                final String outFileName = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "flashmsg" + File.separator;
                new FileUtil().deleteFolderFiles(new File(outFileName));
            }
            catch (final Exception e) {
                FlashMessage.logger.log(Level.WARNING, "FlashMessage: Caught exception while downloadFlashMsgFiles & delete file ", ex);
            }
            return Boolean.FALSE;
        }
    }
    
    public boolean downloadFlashMsgFiles(final String flashHtmlFile, final String falshImages) {
        return this.downloadFlashMsgFilesWithChecksumValidation(ChecksumProvider.ChecksumType.SHA_256, flashHtmlFile, "", falshImages, "");
    }
    
    public int copyRemoteFileUsingHTTP(final String updChkURL, final String outFileName, final boolean isUpdatesJSON) throws Exception {
        return this.copyRemoteFileUsingHTTP(updChkURL, outFileName, isUpdatesJSON, false);
    }
    
    public int copyRemoteFileUsingHTTP(final String updChkURL, final String outFileName, final boolean isUpdatesJSON, final boolean isChecksumValidationRequired) throws Exception {
        try {
            FlashMessage.logger.log(Level.INFO, "Going to access the URL : " + updChkURL + " to find out the latest updates.");
            FlashMessage.logger.log(Level.INFO, "Destination File : " + outFileName);
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
            ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
            final String lastModifiedTime = UpdatesParamUtil.getUpdParameter("updateIfModifiedSinceAt");
            if (isUpdatesJSON && lastModifiedTime != null) {
                ((Hashtable<String, String>)headers).put("If-Modified-Since", lastModifiedTime);
            }
            DownloadStatus downloadStatus;
            if (isChecksumValidationRequired && isUpdatesJSON) {
                final DMSDownloadUtil downloadUtil = DMSDownloadUtil.getInstance();
                downloadStatus = downloadUtil.downloadRequestedFileForComponent("Server", "ServerUpdates", outFileName, (Properties)null, headers).getDownloadStatus();
            }
            else {
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                downloadStatus = downloadMgr.downloadFile(updChkURL, outFileName, (Properties)null, headers, new SSLValidationType[0]);
            }
            final int statusCode = downloadStatus.getStatus();
            if (statusCode == 0) {
                FlashMessage.logger.log(Level.INFO, "Successfully Downloaded the  File : " + updChkURL);
            }
            else {
                FlashMessage.logger.log(Level.INFO, " Download Failed for  File : " + updChkURL + "with ErrorCode:" + statusCode);
            }
            if (isUpdatesJSON && statusCode == 0) {
                UpdatesParamUtil.updateUpdParams("updateIfModifiedSinceAt", downloadStatus.getLastModifiedTime());
            }
            if ((isUpdatesJSON && statusCode == 0) || statusCode == 10010) {
                UpdatesParamUtil.updateUpdParams("updatesLastModifiedAt", System.currentTimeMillis() + "");
            }
            return statusCode;
        }
        catch (final Exception ee) {
            FlashMessage.logger.log(Level.INFO, "Exception while Downloading File From" + updChkURL);
            return 10008;
        }
    }
    
    public JSONObject getOtherProps() {
        return (JSONObject)this.updatesJSON.get((Object)"OtherProps");
    }
    
    public void checkAndShowRuleBasedMsg(final JSONObject versionMsg) {
        FlashMessage.logger.log(Level.FINE, "Going to Apply Rules for Version Message");
        try {
            final JSONObject rulesObj = (JSONObject)this.updatesJSON.get((Object)"Rules");
            final JSONArray rulesArray = (JSONArray)rulesObj.get((Object)"RuleConditions");
            Long noInCondn = null;
            final Set<String> uniqueRuleKeys = new HashSet<String>();
            final String divideByKey = this.getDivideByKey();
            final String divideByServerKey = this.getDivideByServerKey();
            String divideByValue = null;
            if (divideByKey != null) {
                divideByValue = (String)rulesObj.get((Object)divideByKey);
            }
            this.updatesAnalyzer.getUniqueConditionKeys(rulesObj, uniqueRuleKeys, "RuleConditions");
            final JSONObject serverRulesJSON = this.updatesAnalyzer.constructServerJSON(uniqueRuleKeys);
            if (divideByValue != null && !divideByValue.equalsIgnoreCase("all")) {
                final Object conditionValue = this.updatesAnalyzer.fetchServerValueFor(divideByServerKey);
                if (conditionValue != null) {
                    final String dataType = this.updatesAnalyzer.fetchDataTypeFor(divideByServerKey);
                    final JSONObject compDetails = new JSONObject();
                    compDetails.put((Object)"Value", conditionValue);
                    compDetails.put((Object)"DataType", (Object)dataType);
                    serverRulesJSON.put((Object)divideByServerKey, (Object)compDetails);
                }
                final JSONArray conditionArray = (JSONArray)versionMsg.get((Object)"Condition");
                for (int i = 0; i < conditionArray.size(); ++i) {
                    final JSONObject condition = (JSONObject)conditionArray.get(i);
                    final String conditionKey = (String)condition.get((Object)"Key");
                    if (conditionKey.equalsIgnoreCase(divideByServerKey)) {
                        final Object conditionVal = condition.get((Object)"Value");
                        if (!conditionVal.toString().equalsIgnoreCase("all")) {
                            noInCondn = Long.valueOf(conditionVal.toString());
                            noInCondn /= (Long)Integer.parseInt(divideByValue);
                        }
                    }
                }
            }
            boolean isRuleMatched = Boolean.TRUE;
            for (int j = 0; j < rulesArray.size(); ++j) {
                final JSONObject rule = (JSONObject)rulesArray.get(j);
                final JSONArray conditionArray2 = (JSONArray)rule.get((Object)"Condition");
                if (noInCondn != null) {
                    final JSONObject divideByCondn = new JSONObject();
                    divideByCondn.put((Object)"Key", (Object)divideByServerKey);
                    divideByCondn.put((Object)"Comparator", (Object)"lessthanequal");
                    divideByCondn.put((Object)"Value", (Object)(noInCondn * (j + 1)));
                    conditionArray2.add((Object)divideByCondn);
                }
                isRuleMatched = this.updatesAnalyzer.compareJSONS(rule, serverRulesJSON);
                if (isRuleMatched) {
                    FlashMessage.logger.log(Level.INFO, "Will Be Showing Message in", j + " Day(s)");
                    UpdatesParamUtil.updateUpdParams("showVersionMsgAfter", j + "");
                    UpdatesParamUtil.updateUpdParams("isVersionMsgAvailable", "True");
                    if (j == 0) {
                        FlashMessage.logger.log(Level.INFO, "Showing VersionMessage Immediately");
                        UpdatesParamUtil.updateUpdParams("showVersionMsg", "True");
                    }
                    else {
                        UpdatesParamUtil.updateUpdParams("showVersionMsg", "False");
                    }
                    return;
                }
            }
            if (rulesArray.size() < 1) {
                UpdatesParamUtil.updateUpdParams("showVersionMsg", "True");
            }
            if (!isRuleMatched) {
                FlashMessage.logger.log(Level.INFO, "Will be Showing VersionMessage after" + rulesArray.size() + "  Days");
                UpdatesParamUtil.updateUpdParams("showVersionMsgAfter", rulesArray.size() + "");
                UpdatesParamUtil.updateUpdParams("showVersionMsg", "False");
                UpdatesParamUtil.updateUpdParams("isVersionMsgAvailable", "True");
            }
        }
        catch (final Exception e) {
            FlashMessage.logger.log(Level.SEVERE, "Exception while Applying Rules for VersionMessage", e);
        }
    }
    
    public static void decrementShowAfterCounter() {
        FlashMessage.logger.log(Level.FINE, "Going to Decrement showAfter of VersionMessage");
        final String showAfter = UpdatesParamUtil.getUpdParameter("showVersionMsgAfter");
        if (showAfter != null && !showAfter.trim().equals("0")) {
            int showMsgAfter = Integer.parseInt(showAfter);
            --showMsgAfter;
            UpdatesParamUtil.updateUpdParams("showVersionMsgAfter", showMsgAfter + "");
            if (showMsgAfter == 0) {
                FlashMessage.logger.log(Level.INFO, "Going to show VersionMessage as showAfter counter Reached 0");
                UpdatesParamUtil.updateUpdParams("showVersionMsg", "True");
            }
        }
    }
    
    public boolean isLargeNetworkCustomer() {
        FlashMessage.logger.log(Level.FINE, "Going to check if the Customer is an MSP or LargeNetwork");
        return Boolean.FALSE;
    }
    
    public void updateUpdatesConfPropsForPPMHandling(final JSONObject updsJSON) {
        final Properties updateProps = new Properties();
        final Map<String, String> largeNwKeyMap = this.fetchLargeNwKeyMap();
        if (!updsJSON.isEmpty()) {
            final JSONObject largeNWJSON = (JSONObject)updsJSON.get((Object)"LargeNetwork");
            final Set<String> largeNwKeySet = largeNwKeyMap.keySet();
            String buildNumberonWebSite = new String();
            for (final String key : largeNwKeySet) {
                final String largeNwValue = (String)largeNWJSON.get((Object)key);
                if (largeNwValue != null) {
                    ((Hashtable<String, String>)updateProps).put(largeNwKeyMap.get(key), largeNwValue);
                }
            }
            buildNumberonWebSite = this.latest_build_version.replace(".", "");
            ((Hashtable<String, String>)updateProps).put("BuildNumberOnWebSite", buildNumberonWebSite);
            final String hotfixDownloadURL = UpdatesParamUtil.getUpdParameter("UPDATE_DOWNLOAD_URL");
            if (hotfixDownloadURL != null) {
                ((Hashtable<String, String>)updateProps).put("download-URL", hotfixDownloadURL);
            }
        }
        final String lastModified = UpdatesParamUtil.getUpdParameter("updatesLastModifiedAt");
        if (lastModified != null) {
            ((Hashtable<String, String>)updateProps).put("updatesLastModifiedAt", lastModified);
        }
        try {
            final String updatesConfFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "updates.conf";
            if (!new File(updatesConfFileName).getParentFile().exists()) {
                FlashMessage.logger.log(Level.INFO, "Is user-conf folder created : " + new File(updatesConfFileName).getParentFile().mkdir());
            }
            FlashMessage.logger.log(Level.FINEST, "Updates conf file Name :" + updatesConfFileName);
            if (!updateProps.isEmpty()) {
                FileAccessUtil.storeProperties(updateProps, updatesConfFileName, true);
                FlashMessage.logger.log(Level.INFO, "Had written the the Properties " + updateProps + "in " + updatesConfFileName);
            }
        }
        catch (final Exception ex) {
            FlashMessage.logger.log(Level.WARNING, "Exception while writing props in updates.conf file. ", ex);
        }
    }
    
    public Map fetchLargeNwKeyMap() {
        final Map<String, String> largeNwKeyMap = new HashMap<String, String>();
        largeNwKeyMap.put("BuildNumber", "applicable-build-number");
        return largeNwKeyMap;
    }
    
    public String getDivideByKey() {
        return null;
    }
    
    public String getDivideByServerKey() {
        return null;
    }
    
    public static void checkAndClearVersionMsg() {
        FlashMessage.logger.log(Level.INFO, "Going to Check and Clear VersionMessage Details");
        final String updatePramsType = UpdatesParamUtil.getUpdParameter("UPDATE_VERSION_TYPE");
        if (updatePramsType != null) {
            final Properties localProps = SyMUtil.getProductProperties();
            String localVersion = null;
            String localVersion2 = null;
            final String s = updatePramsType;
            switch (s) {
                case "MajorVersion": {
                    localVersion = localProps.getProperty("major_version");
                    localVersion2 = localProps.getProperty("minor_version");
                    break;
                }
                case "ServicePack": {
                    localVersion = localProps.getProperty("sp_version");
                    break;
                }
                case "HotFix": {
                    localVersion = localProps.getProperty("hf_version");
                    break;
                }
                case "PatchVersion": {
                    localVersion = localProps.getProperty("patch_version");
                    break;
                }
            }
            if (localVersion != null) {
                String remoteVersion = UpdatesParamUtil.getUpdParameter("UPDATE_VERSION");
                remoteVersion = ((remoteVersion != null) ? remoteVersion.trim() : remoteVersion);
                localVersion = localVersion.trim();
                if (remoteVersion != null && Integer.parseInt(localVersion) >= Integer.parseInt(remoteVersion)) {
                    if (updatePramsType.equalsIgnoreCase("MajorVersion")) {
                        String remoteVersion2 = UpdatesParamUtil.getUpdParameter("UPDATE_VERSION_2");
                        remoteVersion2 = ((remoteVersion2 != null) ? remoteVersion2.trim() : remoteVersion2);
                        if (!localVersion2.equalsIgnoreCase(remoteVersion2)) {
                            return;
                        }
                    }
                    ResetUpdateParams();
                }
            }
        }
    }
    
    public static void ResetUpdateParams() {
        UpdatesParamUtil.deleteUpdParameter("PRODUCT_UPDATE_MSG_TITLE");
        UpdatesParamUtil.deleteUpdParameter("PRODUCT_UPDATE_MSG");
        UpdatesParamUtil.deleteUpdParameter("UPDATE_DOWNLOAD_URL");
        UpdatesParamUtil.deleteUpdParameter("UPDATE_MESSAGE_PRIORITY");
        UpdatesParamUtil.deleteUpdParameter("isVersionMsgAvailable");
        UpdatesParamUtil.deleteUpdParameter("showVersionMsgAfter");
        UpdatesParamUtil.deleteUpdParameter("showVersionMsg");
        UpdatesParamUtil.deleteUpdParameter("UPDATE_VERSION_TYPE");
        UpdatesParamUtil.deleteUpdParameter("UPDATE_VERSION_2");
        UpdatesParamUtil.deleteUpdParameter("UPDATE_VERSION");
        UpdatesParamUtil.deleteUpdParameter("LATEST_BUILD_VERSION");
        resetEOSUpdParams();
        FlashMessage.logger.log(Level.INFO, "Successfully Cleared VersionMessage Details");
    }
    
    private static void resetEOSUpdParams() {
        UpdatesParamUtil.deleteUpdParameter("EOS_POPUP_TITLE");
        UpdatesParamUtil.deleteUpdParameter("EOS_POPUP_MSG_ADMIN");
        UpdatesParamUtil.deleteUpdParameter("EOS_POPUP_MSG_TECH");
        UpdatesParamUtil.deleteUpdParameter("EOL_DATE");
        UpdatesParamUtil.deleteUpdParameter("EOS_VH_MSG_ADMIN");
        UpdatesParamUtil.deleteUpdParameter("EOS_VH_MSG_TECH");
        UpdatesParamUtil.deleteUpdParameter("EOS_TB_MSG_ADMIN");
        UpdatesParamUtil.deleteUpdParameter("EOS_TB_MSG_TECH");
        UpdatesParamUtil.deleteUpdParameter("EOS_SUPPORT_MSG");
        UpdatesParamUtil.deleteUpdParameter("EOS_SUPPORT_TITLE");
    }
    
    static {
        FlashMessage.logger = Logger.getLogger(FlashMessage.class.getName());
    }
}
