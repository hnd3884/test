package com.me.ems.onpremise.summaryserver.summary.authentication;

import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import java.util.Iterator;
import java.util.Hashtable;
import com.me.ems.onpremise.summaryserver.common.authentication.ProbeHSKeyGenerator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.UUID;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.InetAddress;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeAuthUtil
{
    public static Logger logger;
    private static ProbeAuthUtil instance;
    private static HashMap<Long, String> probeKeysCache;
    
    public static HashMap<Long, String> getProbeKeysCache() {
        return ProbeAuthUtil.probeKeysCache;
    }
    
    public static void setProbeKeysCache(final HashMap<Long, String> probeKeysCache) {
        ProbeAuthUtil.probeKeysCache = probeKeysCache;
    }
    
    public static ProbeAuthUtil getInstance() {
        if (ProbeAuthUtil.instance == null) {
            populateProbeKeysCache();
            ProbeAuthUtil.instance = new ProbeAuthUtil();
        }
        return ProbeAuthUtil.instance;
    }
    
    public String getProbeAuthKey(final Long probeID) {
        final String authKey = ProbeAuthUtil.probeKeysCache.get(probeID);
        return (authKey != null) ? authKey : this.getProbeKeyFromDB(probeID);
    }
    
    public static void updateProbeKeysCache(final Long probeId, final String authKey) {
        ProbeAuthUtil.probeKeysCache.put(probeId, authKey);
    }
    
    public JSONObject storeAuthKeys(final Long probeId, final Map apiKeyDetails) {
        final JSONObject jsonObject = new JSONObject();
        String summaryServerAuthKey = "";
        try {
            final Row probeApiRow = new Row("ProbeApiKeyDetails");
            probeApiRow.set("PROBE_ID", (Object)probeId);
            probeApiRow.set("PROBE_API_KEY", apiKeyDetails.get("probeAuthKey"));
            probeApiRow.set("GENERATED_BY", apiKeyDetails.get("apiKeyGeneratedBy"));
            probeApiRow.set("GENERATED_TIME", apiKeyDetails.get("apiKeyGeneratedOn"));
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(probeApiRow);
            SyMUtil.getPersistence().add(dataObject);
            summaryServerAuthKey = this.generateAPIKey();
            final Row summaryApiRow = new Row("SummaryServerApiKeyDetails");
            summaryApiRow.set("PROBE_ID", (Object)probeId);
            summaryApiRow.set("SUMMARY_API_KEY", (Object)summaryServerAuthKey);
            summaryApiRow.set("GENERATED_BY", apiKeyDetails.get("apiKeyGeneratedBy"));
            summaryApiRow.set("GENERATED_TIME", apiKeyDetails.get("apiKeyGeneratedOn"));
            final DataObject dataObject2 = (DataObject)new WritableDataObject();
            dataObject2.addRow(summaryApiRow);
            SyMUtil.getPersistence().add(dataObject2);
            jsonObject.put("probeId", (Object)probeId);
            jsonObject.put("probeAuthKey", apiKeyDetails.get("probeAuthKey"));
            jsonObject.put("summaryServerAuthKey", (Object)summaryServerAuthKey);
            jsonObject.put("apiKeyGeneratedBy", apiKeyDetails.get("apiKeyGeneratedBy"));
            jsonObject.put("summaryServerIp", (Object)InetAddress.getLocalHost().getHostAddress());
            jsonObject.put("timeZone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            jsonObject.put("version", (Object)SyMUtil.getProductProperty("buildnumber"));
            final HashMap probeDetail = this.getProbeDetails(probeId);
            jsonObject.put("probeName", probeDetail.get("probeName"));
            jsonObject.put("probeDesc", probeDetail.get("probeDesc"));
            return jsonObject;
        }
        catch (final Exception ex) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while adding Probe api key Details", ex);
            return null;
        }
    }
    
    public String generateAPIKey() {
        return UUID.randomUUID().toString().toUpperCase();
    }
    
    public Long validateInstallationKey(final String key) {
        Long probeId = -1L;
        try {
            final Criteria criteria = new Criteria(new Column("ProbeInstallationKeyDetails", "INSTALLATION_KEY"), (Object)key, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeInstallationKeyDetails"));
            sq.addSelectColumn(new Column("ProbeInstallationKeyDetails", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            final Row installKeyRow = dataObject.getRow("ProbeInstallationKeyDetails");
            if (installKeyRow == null) {
                return null;
            }
            if (!(boolean)installKeyRow.get("IS_VALID")) {
                return probeId;
            }
            probeId = (Long)installKeyRow.get("PROBE_ID");
            return probeId;
        }
        catch (final DataAccessException ex) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while getting Probe installation key Details", (Throwable)ex);
            return null;
        }
    }
    
    public String getProbeHandShakekey() {
        ProbeHSKeyGenerator.getInstance();
        final String lastGeneratedKey = ProbeHSKeyGenerator.getLastGeneratedKey();
        if (!lastGeneratedKey.equals("") && lastGeneratedKey != null) {
            ProbeHSKeyGenerator.getInstance();
            final Hashtable<String, Long> keyMap = ProbeHSKeyGenerator.getKeyMap();
            final Long timeInMillis = keyMap.get(lastGeneratedKey);
            if (timeInMillis != null && System.currentTimeMillis() - timeInMillis < 150000.0) {
                return lastGeneratedKey;
            }
        }
        ProbeHSKeyGenerator.getInstance();
        return ProbeHSKeyGenerator.getKey();
    }
    
    public static HashMap populateProbeKeysCache() {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeApiKeyDetails"));
            sq.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> probeKeyRows = dataObject.getRows("ProbeApiKeyDetails");
                while (probeKeyRows.hasNext()) {
                    final Row apiKeyRow = probeKeyRows.next();
                    final String probeAuthKey = (String)apiKeyRow.get("PROBE_API_KEY");
                    final Long probeID = (Long)apiKeyRow.get("PROBE_ID");
                    ProbeAuthUtil.probeKeysCache.put(probeID, probeAuthKey);
                }
            }
        }
        catch (final Exception e) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while populateProbeKeysCache", e);
        }
        return ProbeAuthUtil.probeKeysCache;
    }
    
    public String getProbeKeyFromDB(final Long probeID) {
        String probeAuthKey = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeApiKeyDetails"));
            sq.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
            sq.setCriteria(new Criteria(Column.getColumn("ProbeApiKeyDetails", "PROBE_ID"), (Object)probeID, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row apiKeyRow = dataObject.getRow("ProbeApiKeyDetails");
                if (apiKeyRow != null) {
                    probeAuthKey = (String)apiKeyRow.get("PROBE_API_KEY");
                    ProbeAuthUtil.probeKeysCache.put(probeID, probeAuthKey);
                }
            }
        }
        catch (final Exception e) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while getting api key for probe id " + probeID, e);
        }
        return probeAuthKey;
    }
    
    public HashMap generateProbeInstallationKeyDetails(final Long probeId) {
        final HashMap installMap = new HashMap();
        String key = null;
        try {
            key = this.generateAPIKey();
            final Row probeInstallRow = new Row("ProbeInstallationKeyDetails");
            probeInstallRow.set("PROBE_ID", (Object)probeId);
            probeInstallRow.set("INSTALLATION_KEY", (Object)key);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(probeInstallRow);
            SyMUtil.getPersistence().add(dataObject);
            ProbeAuthUtil.logger.log(Level.INFO, "PROBE ADDED SUCCESSFULLY IN PROBE INSTALLATION KEY DETAILS for {0}", new String[] { probeId.toString() });
        }
        catch (final DataAccessException ex) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while adding Probe installation key Details for probe Id " + probeId, (Throwable)ex);
            return null;
        }
        installMap.put("probeID", probeId);
        installMap.put("installationKey", key);
        installMap.put("downloadUrl", ProbeDetailsService.getProbeDownloadUrl());
        return installMap;
    }
    
    private HashMap getProbeDetails(final Long probeId) {
        final HashMap probeDetail = new HashMap();
        try {
            final Criteria criteria = new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row probeDetailRow = dataObject.getRow("ProbeDetails");
                if (probeDetailRow != null) {
                    probeDetail.put("probeName", probeDetailRow.get("PROBE_NAME"));
                    probeDetail.put("probeDesc", probeDetailRow.get("PROBE_DESCRIPTION"));
                }
            }
        }
        catch (final Exception e) {
            ProbeAuthUtil.logger.log(Level.SEVERE, "Exception while getting probe details", e);
        }
        return probeDetail;
    }
    
    static {
        ProbeAuthUtil.logger = Logger.getLogger("probeActionsLogger");
        ProbeAuthUtil.instance = null;
        ProbeAuthUtil.probeKeysCache = new HashMap<Long, String>();
    }
}
