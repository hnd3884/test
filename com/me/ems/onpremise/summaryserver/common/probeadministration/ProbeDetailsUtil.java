package com.me.ems.onpremise.summaryserver.common.probeadministration;

import java.util.Hashtable;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.ems.onpremise.summaryserver.probe.probeadministration.SummaryServerReachabilityChecker;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.me.ems.onpremise.server.core.ProxySettingsUtil;
import com.me.devicemanagement.framework.server.util.Encoder;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.net.InetAddress;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;

public class ProbeDetailsUtil implements ProbeDetailsAPI
{
    public static Logger logger;
    private static String probeAuthKey;
    private static HashMap currentProbeDetailsCache;
    
    public static String getProbeAuthKey() {
        return ProbeDetailsUtil.probeAuthKey;
    }
    
    public static void setProbeAuthKey(final String probeAuthKey) {
        ProbeDetailsUtil.probeAuthKey = probeAuthKey;
    }
    
    public static HashMap getApiKeyDetails(final Criteria crit) {
        final HashMap apiDetails = new HashMap();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeDetails"));
            final Join join1 = new Join("ProbeDetails", "ProbeApiKeyDetails", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            selectQuery.addJoin(join1);
            final Join join2 = new Join("ProbeDetails", "SummaryServerApiKeyDetails", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            selectQuery.addJoin(join2);
            selectQuery.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
            selectQuery.addSelectColumn(new Column("ProbeDetails", "*"));
            selectQuery.addSelectColumn(new Column("SummaryServerApiKeyDetails", "*"));
            if (crit != null) {
                selectQuery.setCriteria(crit);
            }
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row probeDetailRow = dataObject.getRow("ProbeDetails");
                final Long probeId = (Long)probeDetailRow.get("PROBE_ID");
                final Row probeApiRow = dataObject.getRow("ProbeApiKeyDetails", new Criteria(new Column("ProbeApiKeyDetails", "PROBE_ID"), (Object)probeId, 0));
                final Row ssApiRow = dataObject.getRow("SummaryServerApiKeyDetails", new Criteria(new Column("SummaryServerApiKeyDetails", "PROBE_ID"), (Object)probeId, 0));
                apiDetails.put("probeServerAuthKeyGeneratedBy", probeApiRow.get("GENERATED_BY"));
                apiDetails.put("probeServerAuthKeyGeneratedOn", probeApiRow.get("GENERATED_TIME"));
                apiDetails.put("summaryServerAuthKeyGeneratedBy", ssApiRow.get("GENERATED_BY"));
                apiDetails.put("summaryServerAuthKeyGeneratedOn", ssApiRow.get("GENERATED_TIME"));
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception while getting api key details", e);
        }
        return apiDetails;
    }
    
    public HashMap getCurrentProbeServerDetail() {
        if (ProbeDetailsUtil.currentProbeDetailsCache.isEmpty()) {
            try {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeApiKeyDetails"));
                sq.addJoin(new Join("ProbeApiKeyDetails", "ProbeDetails", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2));
                sq.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
                sq.addSelectColumn(new Column("ProbeDetails", "*"));
                final DataObject dataObject = SyMUtil.getPersistenceLite().get(sq);
                if (!dataObject.isEmpty()) {
                    final Row probeDetailRow = dataObject.getRow("ProbeDetails");
                    if (probeDetailRow != null) {
                        ProbeDetailsUtil.currentProbeDetailsCache.put("PROBE_ID", probeDetailRow.get("PROBE_ID"));
                        ProbeDetailsUtil.currentProbeDetailsCache.put("PROBE_NAME", probeDetailRow.get("PROBE_NAME"));
                        ProbeDetailsUtil.currentProbeDetailsCache.put("PROBE_DESCRIPTION", probeDetailRow.get("PROBE_DESCRIPTION"));
                    }
                }
            }
            catch (final Exception e) {
                ProbeDetailsUtil.logger.log(Level.SEVERE, "Error occured while getting current probe detail", e);
            }
        }
        return ProbeDetailsUtil.currentProbeDetailsCache;
    }
    
    public Long getCurrentProbeID() {
        return this.getCurrentProbeServerDetail().get("PROBE_ID");
    }
    
    public String getSummaryServerBaseURL() {
        String summaryServerUrl = "";
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("SummaryServerInfo"));
            sq.addSelectColumn(new Column("SummaryServerInfo", "*"));
            final DataObject dataObject = SyMUtil.getPersistenceLite().get(sq);
            if (!dataObject.isEmpty()) {
                final Row summaryServerInfoRow = dataObject.getFirstRow("SummaryServerInfo");
                if (summaryServerInfoRow != null) {
                    summaryServerUrl = summaryServerUrl + summaryServerInfoRow.get("PROTOCOL") + "://" + summaryServerInfoRow.get("HOST") + ":" + summaryServerInfoRow.get("PORT") + "/";
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Error occured while getting SummaryServerUrl ", e);
        }
        return summaryServerUrl;
    }
    
    public static String getProbeServerUrl(final Long probeId) {
        String probeServerUrl = "";
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeServerInfo"));
            sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
            sq.setCriteria(new Criteria(new Column("ProbeServerInfo", "PROBE_ID"), (Object)probeId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row probeServerInfo = dataObject.getFirstRow("ProbeServerInfo");
                if (probeServerInfo != null) {
                    probeServerUrl = probeServerUrl + probeServerInfo.get("PROTOCOL") + "://" + probeServerInfo.get("HOST") + ":" + probeServerInfo.get("PORT") + "/";
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Error occured while getting SummaryServerUrl ", e);
        }
        return probeServerUrl;
    }
    
    public String getProbeName(final Long probeId) {
        String probeName = "";
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.setCriteria(new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row probeDetailRow = dataObject.getFirstRow("ProbeDetails");
                if (probeDetailRow != null) {
                    probeName = (String)probeDetailRow.get("PROBE_NAME");
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Error occured while getting probe name ", e);
        }
        return probeName;
    }
    
    public HashMap getProbeLiveStatusDetails(final Long probeId) {
        return LiveStatusUpdateUtil.getProbeLiveStatusDetails(probeId);
    }
    
    public Integer getProbeLiveStatus(final Long probeId) {
        return Integer.parseInt(LiveStatusUpdateUtil.getProbeLiveStatusDetails(probeId).get("STATUS").toString());
    }
    
    public static boolean isDiskSpaceLow(final long freeSpaceInBytes) {
        try {
            long minimumRequiredFreeSpace = 3221225472L;
            final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
            final Properties properties = FileAccessUtil.readProperties(systemPropertiesPath);
            if (properties.getProperty("diskcheck.max") != null && properties.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                final Long minDiskSpace = Long.valueOf(properties.getProperty("diskcheck.max"));
                final String byteType = properties.getProperty("diskcheck.bytetype");
                if (byteType.trim().equalsIgnoreCase("gb")) {
                    minimumRequiredFreeSpace = minDiskSpace * 1024L * 1024L * 1024L;
                }
                else if (byteType.trim().equalsIgnoreCase("mb")) {
                    minimumRequiredFreeSpace = minDiskSpace * 1024L * 1024L;
                }
            }
            ProbeDetailsUtil.logger.log(Level.INFO, "Free space available = " + freeSpaceInBytes / 1048576L + " MB." + "Free space required = " + minimumRequiredFreeSpace / 1048576L + " MB.");
            if (freeSpaceInBytes < minimumRequiredFreeSpace) {
                return true;
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while checking disk space availability..", e);
        }
        return false;
    }
    
    public void updateProbeDetailsInProbeProperties() {
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final File file = new File(serverHome);
            final Long freeSpaceInGB = file.getFreeSpace() / 1073741824L;
            final Long totalSpaceInGB = file.getTotalSpace() / 1073741824L;
            final String ipAddress = InetAddress.getLocalHost().getHostAddress();
            ProbePropertyUtil.updateProbeProperty("IPADDRESS", ipAddress);
            ProbePropertyUtil.updateProbeProperty("FREE_SPACE", freeSpaceInGB.toString());
            ProbePropertyUtil.updateProbeProperty("TOTAL_SPACE", totalSpaceInGB.toString());
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.INFO, "Exception occured while updating probe properties", e);
        }
    }
    
    public static void storeProxyDetails(final Properties properties) {
        try {
            final String proxyDetailsConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "proxyDetails.conf";
            final Path confPath = Paths.get(proxyDetailsConf, new String[0]);
            if (!Files.exists(confPath, new LinkOption[0])) {
                Files.createFile(confPath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            ProbeDetailsUtil.logger.log(Level.INFO, " after store key here here");
            FileAccessUtil.storeProperties(properties, proxyDetailsConf, false);
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception while storing proxy details", e);
        }
    }
    
    public static void updateProxyDetailsInDB() {
        try {
            final String proxyPropsConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "proxyDetails.conf";
            final File file = new File(proxyPropsConf);
            if (!file.exists()) {
                return;
            }
            final Properties probeDetailsProps = FileAccessUtil.readProperties(proxyPropsConf);
            if (probeDetailsProps.getProperty("proxyPass") != null) {
                final String password = Encoder.convertToNewBase(probeDetailsProps.getProperty("proxyPass"));
                ((Hashtable<String, String>)probeDetailsProps).put("password", password);
                ((Hashtable<String, String>)probeDetailsProps).put("userName", probeDetailsProps.getProperty("proxyUser"));
            }
            final Map probeDetailsMap = new HashMap();
            for (final String name : probeDetailsProps.stringPropertyNames()) {
                probeDetailsMap.put(name, probeDetailsProps.getProperty(name));
            }
            probeDetailsMap.putIfAbsent("ftp_same_as_http", false);
            probeDetailsMap.put("proxyType", 2);
            probeDetailsMap.put("proxyPort", Integer.parseInt(probeDetailsMap.get("proxyPort")));
            ProxySettingsUtil.saveProxyConfig(probeDetailsMap);
            ProbeDetailsUtil.logger.info("Successfully written proxy probs to db");
            ProbeDetailsUtil.logger.log(Level.INFO, "SUCCESSFULY WRRITTEN proxy props to db");
            FileAccessUtil.deleteFile(proxyPropsConf);
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while writing proxy props to db", e);
        }
    }
    
    public List<Map> getAllProbeDetails() {
        final List<Map> probeList = new ArrayList<Map>();
        final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
        probeDetails.forEach((key, value) -> {
            final Map probeDetail = new HashMap();
            probeDetail.put("probeID", key);
            probeDetail.put("probeName", ((HashMap)value).get("PROBE_NAME"));
            list.add(probeDetail);
            return;
        });
        return probeList;
    }
    
    public HashMap getSummaryServerDetails() {
        final HashMap summaryServerDetails = new HashMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("SummaryServerInfo"));
            sq.addSelectColumn(new Column("SummaryServerInfo", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row summaryServerInfoRow = dataObject.getFirstRow("SummaryServerInfo");
                if (summaryServerInfoRow != null) {
                    summaryServerDetails.put("port", summaryServerInfoRow.get("PORT") + "");
                    summaryServerDetails.put("host", summaryServerInfoRow.get("HOST"));
                    summaryServerDetails.put("protocol", summaryServerInfoRow.get("PROTOCOL"));
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while getting summary server details", e);
        }
        return summaryServerDetails;
    }
    
    public Boolean isValidProbeAuthKey(final String authKey) {
        try {
            if (authKey != null && !"".equals(authKey) && ProbeDetailsUtil.probeAuthKey != null && !"".equals(ProbeDetailsUtil.probeAuthKey) && authKey.equals(ProbeDetailsUtil.probeAuthKey)) {
                return true;
            }
            final Criteria criteria = new Criteria(new Column("ProbeApiKeyDetails", "PROBE_API_KEY"), (Object)authKey, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeApiKeyDetails"));
            sq.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("ProbeApiKeyDetails");
                if (row != null) {
                    ProbeDetailsUtil.probeAuthKey = authKey;
                    return true;
                }
                return false;
            }
            else {
                ProbeDetailsUtil.logger.log(Level.INFO, "No entries found for dObj probeapikeydetails ");
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while validating probe auth key", e);
        }
        return false;
    }
    
    public Boolean isValidProbeAuthKey(final ContainerRequestContext containerRequestContext) {
        final String probeAuthKey = containerRequestContext.getHeaderString("ProbeAuthorization");
        if (probeAuthKey != null && this.isValidProbeAuthKey(probeAuthKey)) {
            return true;
        }
        return false;
    }
    
    public static String getProbeAuthKeyFromDB() {
        String probeAPIKey = "";
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeApiKeyDetails"));
            sq.addSelectColumn(new Column("ProbeApiKeyDetails", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ProbeApiKeyDetails");
                probeAPIKey = (ProbeDetailsUtil.probeAuthKey = (String)row.get("PROBE_API_KEY"));
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while getting summary auth key", e);
        }
        return probeAPIKey;
    }
    
    public Map getSummaryServerAPIKeyDetails() {
        String summaryServerAPIKey = "";
        final Map authKeyMap = new HashMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("SummaryServerApiKeyDetails"));
            sq.addSelectColumn(new Column("SummaryServerApiKeyDetails", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("SummaryServerApiKeyDetails");
                summaryServerAPIKey = (String)row.get("SUMMARY_API_KEY");
                final Long probeId = (Long)row.get("PROBE_ID");
                authKeyMap.put("probeId", probeId.toString());
                authKeyMap.put("summaryServerAuthKey", summaryServerAPIKey);
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while getting summary auth key", e);
        }
        return authKeyMap;
    }
    
    public boolean isValidSummaryServerAuthKey(final String apiKey, final Long probeId) {
        try {
            Criteria criteria = new Criteria(new Column("SummaryServerApiKeyDetails", "SUMMARY_API_KEY"), (Object)apiKey, 0);
            final Criteria idCriteria = new Criteria(new Column("SummaryServerApiKeyDetails", "PROBE_ID"), (Object)probeId, 0);
            criteria = criteria.and(idCriteria);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("SummaryServerApiKeyDetails"));
            sq.addSelectColumn(new Column("SummaryServerApiKeyDetails", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("SummaryServerApiKeyDetails");
                return row != null;
            }
            ProbeDetailsUtil.logger.log(Level.INFO, "No entries found for dObj summary server api keydetails ");
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception occurred while validating auth key", e);
        }
        return false;
    }
    
    public boolean isValidSummaryServerAuthKey(final ContainerRequestContext containerRequestContext) {
        final String ssAuthKey = containerRequestContext.getHeaderString("SummaryAuthorization");
        final String probeID = containerRequestContext.getHeaderString("probeId");
        return ssAuthKey != null && probeID != null && this.isValidSummaryServerAuthKey(ssAuthKey, Long.parseLong(probeID));
    }
    
    public HashMap getSummaryServerLiveStatusDetails() {
        return LiveStatusUpdateUtil.getSummaryServerLiveStatusDetails();
    }
    
    public Integer getSummaryServerLiveStatus() {
        return Integer.parseInt(LiveStatusUpdateUtil.getSummaryServerLiveStatusDetails().get("STATUS").toString());
    }
    
    public HashMap checkAndUpdateSummaryServerLiveStatus() {
        return SummaryServerReachabilityChecker.checkAndUpdateLiveStatus();
    }
    
    public void updateIpAddr(final Long probeID, final String ipAddress) {
        try {
            if (this.isIpAddressChanged(probeID, ipAddress)) {
                UpdateQuery updateQuery;
                if (probeID != null) {
                    final ProbeUtil probeUtil = ProbeUtil.getInstance();
                    final HashMap probeDetail = ProbeUtil.getAllProbeDetailsCache().get(probeID);
                    if (probeDetail != null) {
                        probeDetail.put("IPADDRESS", ipAddress);
                        ProbeUtil.getAllProbeDetailsCache().put(probeID, probeDetail);
                    }
                    updateQuery = (UpdateQuery)new UpdateQueryImpl("ProbeServerInfo");
                    updateQuery.setCriteria(new Criteria(new Column("ProbeServerInfo", "PROBE_ID"), (Object)probeID, 0));
                    updateQuery.setUpdateColumn("IPADDRESS", (Object)ipAddress);
                    DataAccess.update(updateQuery);
                }
                else {
                    ApiFactoryProvider.getCacheAccessAPI().putCache("summaryServerIp", (Object)ipAddress, 2);
                    updateQuery = (UpdateQuery)new UpdateQueryImpl("SummaryServerInfo");
                    updateQuery.setUpdateColumn("IPADDRESS", (Object)ipAddress);
                }
                DataAccess.update(updateQuery);
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception while updating ip addr for probe", e);
        }
    }
    
    private boolean isIpAddressChanged(final Long probeID, final String ipAddress) {
        try {
            String ipAddrFromCache = "";
            if (probeID != null) {
                final HashMap probeDetail = ProbeUtil.getInstance().getAllProbeDetails().get(probeID);
                if (probeDetail != null) {
                    ipAddrFromCache = probeDetail.get("IPADDRESS");
                }
            }
            else {
                ipAddrFromCache = getSummaryServerIp();
            }
            return ipAddrFromCache == "" || ipAddrFromCache == null || !ipAddress.equals(ipAddrFromCache);
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception while checking ip address", e);
            return false;
        }
    }
    
    public static String getSummaryServerIp() {
        String ipAddr = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("summaryServerIp");
        try {
            if (ipAddr == null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryServerInfo"));
                selectQuery.addSelectColumn(new Column("SummaryServerInfo", "*"));
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("SummaryServerInfo");
                    if (row != null) {
                        ipAddr = (String)row.get("IPADDRESS");
                        ApiFactoryProvider.getCacheAccessAPI().putCache("summaryServerIp", (Object)ipAddr, 2);
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsUtil.logger.log(Level.SEVERE, "Exception while getting summary server ip address", e);
        }
        return ipAddr;
    }
    
    public List<HashMap> getProbeDetails(final Criteria criteria) {
        return ProbeDetailsService.getProbeDetails(criteria);
    }
    
    static {
        ProbeDetailsUtil.logger = Logger.getLogger("probeActionsLogger");
        ProbeDetailsUtil.probeAuthKey = null;
        ProbeDetailsUtil.currentProbeDetailsCache = new HashMap();
    }
}
