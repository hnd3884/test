package com.me.mdm.onpremise.api.settings;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.dbtuning.DBPostgresOptimizationConstants;
import com.me.mdm.api.APIUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.dbtuning.DBPostgresOptimizationUtil;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DBPostgresOptimizationFacade
{
    public Logger logger;
    
    public DBPostgresOptimizationFacade() {
        this.logger = Logger.getLogger("DbOptimizationLog");
    }
    
    public JSONObject getDBPostgresOptimizationDetails() {
        final JSONObject resultJson = new JSONObject();
        Float currentMemoryValueDB = 0.0f;
        Float currentMemoryPercent2DecimalPt = 0.0f;
        Float currtMaxMemoryFile2DeciPt = 0.0f;
        Float currtMemoryFilePct2DeciPt = 0.0f;
        Float maxMemoryValue2DecimalPt = 0.0f;
        Float sysRAMMemInGB2DecimalPt = 0.0f;
        Float memoryPercent2DecimalPt = 0.0f;
        Float defaultMemoryPercent2DecimalPt = 0.0f;
        Float maxMemoryPct0Dcpt = 0.0f;
        String featureErrorMsg = "";
        Boolean ispgExtFileMissing = false;
        Boolean ispgExtFileCorrupted = false;
        Boolean isWmiFailure = false;
        Boolean requiredRAMFailure = false;
        Boolean appliedRAMExceedsSys = false;
        Boolean isDBFileMisMatch = false;
        Boolean resetOption = false;
        Boolean disableFeature = false;
        try {
            HashMap postgresExtMap = new HashMap();
            postgresExtMap = DBPostgresOptimizationUtil.getInstance().getComputedRAMDetails();
            sysRAMMemInGB2DecimalPt = Float.valueOf(String.valueOf(postgresExtMap.get("systemRamMemInGB")));
            memoryPercent2DecimalPt = Float.valueOf(String.valueOf(postgresExtMap.get("memoryPercent2DcPt")));
            defaultMemoryPercent2DecimalPt = Float.valueOf(String.valueOf(postgresExtMap.get("defaultMemPercent2DcPt")));
            maxMemoryPct0Dcpt = Float.valueOf(String.valueOf(postgresExtMap.get("maxMemoryPct0Dcpt")));
            maxMemoryValue2DecimalPt = Float.valueOf(String.valueOf(postgresExtMap.get("maxMemoryValue2DcPt")));
            currtMaxMemoryFile2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currtMaxMemoryFile2DeciPt")));
            currtMemoryFilePct2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemFilePct2DcPt")));
            currentMemoryValueDB = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemoryValueDB")));
            currentMemoryPercent2DecimalPt = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemDBPct2DcPt")));
            ispgExtFileMissing = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileMissing")));
            ispgExtFileCorrupted = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileCorrupted")));
            isWmiFailure = Boolean.valueOf(String.valueOf(postgresExtMap.get("isWmiFailure")));
            requiredRAMFailure = Boolean.valueOf(String.valueOf(postgresExtMap.get("requiredRAMFailure")));
            appliedRAMExceedsSys = Boolean.valueOf(String.valueOf(postgresExtMap.get("appliedRAMExceedsSys")));
            isDBFileMisMatch = Boolean.valueOf(String.valueOf(postgresExtMap.get("isDBFileMisMatch")));
            resetOption = Boolean.valueOf(String.valueOf(postgresExtMap.get("resetOption")));
            if (currentMemoryValueDB.equals(0.0f)) {
                currentMemoryValueDB = currtMaxMemoryFile2DeciPt;
            }
            if (currentMemoryPercent2DecimalPt.equals(0.0f)) {
                currentMemoryPercent2DecimalPt = currtMemoryFilePct2DeciPt;
            }
            if (!ispgExtFileMissing) {
                if (!isWmiFailure && sysRAMMemInGB2DecimalPt > 0.0f) {
                    if (!ispgExtFileCorrupted) {
                        if (!requiredRAMFailure) {
                            if (appliedRAMExceedsSys) {
                                featureErrorMsg = I18N.getMsg("dc.common.dboptimize.exceed_sys_memory", new Object[0]);
                            }
                            if (isDBFileMisMatch) {
                                if (!featureErrorMsg.isEmpty()) {
                                    featureErrorMsg += "\n";
                                }
                                featureErrorMsg += I18N.getMsg("dc.admin.dboptimize.file_db_mismatch", new Object[0]);
                            }
                        }
                        else {
                            disableFeature = true;
                            featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.RAM_insufficient", new Object[0]);
                        }
                    }
                    else {
                        disableFeature = true;
                        featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.file_corrupted", new Object[0]);
                    }
                }
                else {
                    disableFeature = true;
                    featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.WMI_failure", new Object[0]);
                }
                MessageProvider.getInstance().hideMessage("PGCONF_FILE_MISSING");
            }
            else {
                disableFeature = true;
                MessageProvider.getInstance().unhideMessage("PGCONF_FILE_MISSING");
                featureErrorMsg = I18N.getMsg("dc.common.dboptimize.file_missing", new Object[0]);
            }
            final String profileMinMemory = SyMUtil.getServerParameter("postgresMinValue");
            if (profileMinMemory != null && !profileMinMemory.equals("0")) {
                resultJson.put("MinRequirement", (Object)profileMinMemory);
            }
            else {
                resultJson.put("MinRequirement", (Object)"4");
            }
            resultJson.put("system_ram_memory", (Object)sysRAMMemInGB2DecimalPt);
            resultJson.put("memory_percent", (Object)memoryPercent2DecimalPt);
            resultJson.put("default_memory_percent", (Object)defaultMemoryPercent2DecimalPt);
            resultJson.put("maxmemory_percent", (Object)maxMemoryPct0Dcpt);
            resultJson.put("maxmemory_value", (Object)maxMemoryValue2DecimalPt);
            resultJson.put("currentmemory_percent", (Object)currentMemoryPercent2DecimalPt);
            resultJson.put("currentmemory_value", (Object)currentMemoryValueDB);
            resultJson.put("show_reset", (Object)resetOption);
            resultJson.put("disable_update", (Object)disableFeature);
            resultJson.put("warning_message", (Object)featureErrorMsg);
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting DB Optimization details...", ex);
            throw new APIHTTPException("DBO001", new Object[0]);
        }
    }
    
    public JSONObject saveDBPostgresOptimizationDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject body = jsonObject.getJSONObject("msg_body");
        final Properties dbPostgresProps = new Properties();
        final JSONObject resultJson = new JSONObject();
        String result = "failed";
        Long sysUpdateTime = null;
        Float sysMemoryValue = null;
        Float newMemoryValue = null;
        Float newMemoryPercent = null;
        Float defaultMemPercent2DeciPt = 0.0f;
        Float maxMemoryPct0Dcpt = 0.0f;
        Float sysRAMMemFromDB = 0.0f;
        Float currentMemoryValueDB = 0.0f;
        Float currentMemoryDBPercent = 0.0f;
        Float currtMaxMemoryFile2DeciPt = 0.0f;
        Float currtMemoryFilePct2DeciPt = 0.0f;
        Long userID = null;
        Boolean resetPostgresDBTuning = false;
        Boolean resetFileSuccess = null;
        Boolean resetDBSuccess = null;
        Boolean ispgExtFileCorrupted = null;
        Boolean isDBFileMisMatch = null;
        try {
            userID = APIUtil.getUserID(jsonObject);
            HashMap postgresExtMap = new HashMap();
            postgresExtMap = DBPostgresOptimizationUtil.getInstance().getComputedRAMDetails();
            defaultMemPercent2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("defaultMemPercent2DcPt")));
            maxMemoryPct0Dcpt = Float.valueOf(String.valueOf(postgresExtMap.get("maxMemoryPct0Dcpt")));
            currtMaxMemoryFile2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currtMaxMemoryFile2DeciPt")));
            currtMemoryFilePct2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemFilePct2DcPt")));
            sysRAMMemFromDB = Float.valueOf(String.valueOf(postgresExtMap.get("sysRAMMemFromDB")));
            currentMemoryValueDB = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemoryValueDB")));
            currentMemoryDBPercent = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemDBPct2DcPt")));
            ispgExtFileCorrupted = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileCorrupted")));
            isDBFileMisMatch = Boolean.valueOf(String.valueOf(postgresExtMap.get("isDBFileMisMatch")));
            if (currentMemoryValueDB.equals(0.0f)) {
                currentMemoryValueDB = currtMaxMemoryFile2DeciPt;
            }
            if (currentMemoryDBPercent.equals(0.0f)) {
                currentMemoryDBPercent = currtMemoryFilePct2DeciPt;
            }
            resetPostgresDBTuning = body.optBoolean("reset_content");
            if (resetPostgresDBTuning) {
                resetDBSuccess = DBPostgresOptimizationUtil.getInstance().deletePostgresServer("postgres", currentMemoryValueDB, currentMemoryDBPercent, DBPostgresOptimizationConstants.DEFAULT_MEMORY_VALUE, defaultMemPercent2DeciPt);
                if (resetDBSuccess) {
                    resetFileSuccess = DBPostgresOptimizationUtil.getInstance().resetdbPostgresServerProps();
                    if (resetFileSuccess) {
                        result = "restart";
                        MessageProvider.getInstance().hideMessage("PGCONF_RAM_EXCEEDS_SYSRAM");
                        MessageProvider.getInstance().unhideMessage("REQUIRED_SERVICE_RESTART_POSTGRES");
                        final String profileMinMemory = SyMUtil.getServerParameter("postgresMinValue");
                        if (profileMinMemory != null && !profileMinMemory.equals("0")) {
                            DBPostgresOptimizationUtil.getInstance().updatedbPostgresServerProps(Float.valueOf(Float.parseFloat(profileMinMemory)));
                        }
                    }
                }
            }
            else {
                sysUpdateTime = System.currentTimeMillis();
                sysMemoryValue = Float.valueOf(body.optString("system_ram_memory"));
                newMemoryValue = Float.valueOf(body.optString("currentmemory_value"));
                newMemoryPercent = Float.valueOf(body.optString("currentmemory_percent"));
                if (newMemoryPercent <= maxMemoryPct0Dcpt && newMemoryValue >= DBPostgresOptimizationConstants.DEFAULT_MEMORY_VALUE) {
                    if (!newMemoryValue.equals(currentMemoryValueDB) || !sysMemoryValue.equals(sysRAMMemFromDB) || isDBFileMisMatch || ispgExtFileCorrupted) {
                        final Boolean fileUpdatedStatus = DBPostgresOptimizationUtil.getInstance().updatedbPostgresServerProps(newMemoryValue);
                        if (fileUpdatedStatus) {
                            this.logger.log(Level.INFO, "updateDBPostgresOptimization: Old Values in DB before Updating: (old) System RAM In DB:  {0} (old) Max Allocated RAM In DB:  {1}", new Object[] { sysRAMMemFromDB, currentMemoryValueDB });
                            ((Hashtable<String, String>)dbPostgresProps).put("SYSTEM_TOTAL_MEMORY", String.valueOf(sysMemoryValue));
                            ((Hashtable<String, String>)dbPostgresProps).put("MEMORY_ALLOCATED", String.valueOf(newMemoryValue));
                            ((Hashtable<String, String>)dbPostgresProps).put("MEMORY_ALLOCATED_TIME", String.valueOf(sysUpdateTime));
                            ((Hashtable<String, String>)dbPostgresProps).put("DC_USER_ID", String.valueOf(userID));
                            DBPostgresOptimizationUtil.getInstance().addOrUpdateDbPostgresOptmize("postgres", dbPostgresProps, currentMemoryValueDB, currentMemoryDBPercent, newMemoryValue, newMemoryPercent, defaultMemPercent2DeciPt);
                            this.logger.log(Level.INFO, "updateDBPostgresOptimization: Updating the DbOptimization Table  with Props:  {0}", dbPostgresProps);
                            result = "restart";
                            MessageProvider.getInstance().hideMessage("PGCONF_RAM_EXCEEDS_SYSRAM");
                            MessageProvider.getInstance().unhideMessage("REQUIRED_SERVICE_RESTART_POSTGRES");
                        }
                        else {
                            this.logger.log(Level.INFO, "updateDBPostgresOptimization: Error while Updating the Postgres_ext.conf File with the given Props:  {0}", dbPostgresProps);
                        }
                    }
                    else {
                        result = "success";
                        this.logger.log(Level.INFO, "updateDBPostgresOptimization: no Change in values for Updating the DbOptimization Table for the given Props:  {0}", dbPostgresProps);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "updateDBPostgresOptimization: Maximum/Minumum Percentange Allowed is Violated while Updating the DbOptimization Table with the given Props:  {0}", dbPostgresProps);
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving DB Optimization", ex);
            throw new APIHTTPException("DBO002", new Object[0]);
        }
        return resultJson.put("result", (Object)result);
    }
}
