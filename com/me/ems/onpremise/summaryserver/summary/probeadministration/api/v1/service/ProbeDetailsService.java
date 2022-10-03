package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service;

import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class ProbeDetailsService
{
    protected static Logger logger;
    
    public static List<HashMap> getProbeDetails(final Criteria criteria) {
        final List<HashMap> probesList = new ArrayList<HashMap>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeDetails"));
            final Join join = new Join("ProbeDetails", "ProbeServerInfo", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join);
            final Join join_extn = new Join("ProbeDetails", "ProbeDetailsExtn", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join_extn);
            final Join status_join = new Join("ProbeDetails", "ProbeLiveStatus", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(status_join);
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
            sq.addSelectColumn(new Column("ProbeDetailsExtn", "*"));
            sq.addSelectColumn(new Column("ProbeLiveStatus", "*"));
            if (criteria != null) {
                sq.setCriteria(criteria);
            }
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginId != null) {
                ProbeUsersUtil.probeUserCriteria(sq, loginId);
            }
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            if (!dobj.isEmpty()) {
                final Iterator iterator = dobj.getRows("ProbeDetails");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final HashMap probeDetail = new HashMap();
                    final Long probeId = (Long)row.get("PROBE_ID");
                    final Row probeStatusRow = dobj.getRow("ProbeLiveStatus", new Criteria(new Column("ProbeLiveStatus", "PROBE_ID"), (Object)probeId, 0));
                    if (probeStatusRow != null) {
                        probeDetail.put("probeID", row.get("PROBE_ID"));
                        probeDetail.put("probeName", row.get("PROBE_NAME"));
                        probeDetail.put("status", (int)probeStatusRow.get("STATUS"));
                        probeDetail.put("lastContactTime", DateTimeUtil.longdateToString((long)probeStatusRow.get("LAST_CONTACTED_TIME"), timeFormat));
                        probeDetail.put("remarks", probeStatusRow.get("REMARKS"));
                        probesList.add(probeDetail);
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occurred while getting probe details", e);
        }
        return probesList;
    }
    
    public Map isProbeNameUnique(final String probeName) {
        final HashMap responseMap = new HashMap();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeDetails"));
            final Criteria criteria = new Criteria(new Column("ProbeDetails", "PROBE_NAME"), (Object)probeName, 0, false);
            query.setCriteria(criteria);
            query.addSelectColumn(new Column("ProbeDetails", "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                responseMap.put("isProbeNameUnique", false);
                return responseMap;
            }
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occured while verifying unique probe name", e);
            responseMap.put("isProbeNameUnique", false);
            return responseMap;
        }
        responseMap.put("isProbeNameUnique", true);
        return responseMap;
    }
    
    public HashMap getInstallationDetails(final Long probeId) {
        final HashMap responseMap = new HashMap();
        try {
            responseMap.put("installationKey", this.getInstallationKey(probeId));
            responseMap.put("downloadUrl", getProbeDownloadUrl());
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occured while getting probe installation details due to ", e);
        }
        return responseMap;
    }
    
    public static String getProbeDownloadUrl() {
        String dloadUrl = "";
        try {
            final String downloadConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "probeDownloadProps.conf";
            final Properties downloadProps = FileAccessUtil.readProperties(downloadConf);
            dloadUrl = downloadProps.getProperty("url");
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occured while getting probe download url details due to ", e);
        }
        return dloadUrl;
    }
    
    public String getInstallationKey(final Long probeId) {
        String installationKey = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeInstallationKeyDetails"));
            final Criteria criteria = new Criteria(new Column("ProbeInstallationKeyDetails", "PROBE_ID"), (Object)probeId, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(new Column("ProbeInstallationKeyDetails", "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Row probeInstallRow = dobj.getRow("ProbeInstallationKeyDetails");
                installationKey = (String)probeInstallRow.get("INSTALLATION_KEY");
            }
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occured while getting probe installation key due to ", e);
        }
        return installationKey;
    }
    
    public HashMap isProbeAdded() throws DataAccessException {
        final HashMap responseMap = new HashMap();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeDetails"));
            query.setCriteria((Criteria)null);
            query.addSelectColumn(new Column("ProbeDetails", "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                responseMap.put("isProbeAdded", true);
            }
            else {
                responseMap.put("isProbeAdded", false);
            }
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception occured while verifying whether probe added", e);
            throw e;
        }
        return responseMap;
    }
    
    public ArrayList getNotInstalledProbes() {
        final ArrayList list = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeDetails"));
            final Join join = new Join("ProbeDetails", "ProbeServerInfo", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 1);
            sq.addJoin(join);
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
            final DataObject dObj = SyMUtil.getPersistence().get(sq);
            if (!dObj.isEmpty()) {
                final Iterator iterator = dObj.getRows("ProbeDetails");
                while (iterator.hasNext()) {
                    final Row probeDetailRow = iterator.next();
                    final Row probeServerRow = dObj.getRow("ProbeServerInfo", new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeDetailRow.get("PROBE_ID"), 0));
                    if (probeServerRow == null) {
                        list.add(probeDetailRow.get("PROBE_NAME"));
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception while getting not installed Probe details", e);
        }
        return list;
    }
    
    public HashMap getApiKeyDetails(final Long probeId) {
        final HashMap apiDetails = ProbeDetailsUtil.getApiKeyDetails(new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0));
        try {
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            apiDetails.put("probeServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(apiDetails.get("probeServerAuthKeyGeneratedBy"))));
            apiDetails.put("summaryServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(apiDetails.get("summaryServerAuthKeyGeneratedBy"))));
            apiDetails.put("probeServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)apiDetails.get("probeServerAuthKeyGeneratedOn"), timeFormat));
            apiDetails.put("summaryServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)apiDetails.get("summaryServerAuthKeyGeneratedOn"), timeFormat));
        }
        catch (final Exception e) {
            ProbeDetailsService.logger.log(Level.SEVERE, "Exception while getting api key details", e);
        }
        return apiDetails;
    }
    
    public HashMap getProbeServerUrl(final Long probeId) {
        final HashMap<String, String> hashMap;
        final HashMap probeServerUrlInfo = hashMap = new HashMap<String, String>();
        final String s = "probeServerUrl";
        new ProbeDetailsUtil();
        hashMap.put(s, ProbeDetailsUtil.getProbeServerUrl(probeId));
        return probeServerUrlInfo;
    }
    
    public static Criteria getProbeCriteria(final List probeIDs) {
        return new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeIDs.toArray(), 8);
    }
    
    static {
        ProbeDetailsService.logger = Logger.getLogger(ProbeDetailsService.class.getName());
    }
}
