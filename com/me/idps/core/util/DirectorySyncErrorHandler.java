package com.me.idps.core.util;

import java.util.Hashtable;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;
import java.util.Properties;
import java.util.HashMap;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.sql.Connection;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.adventnet.db.api.RelationalAPI;
import org.json.simple.JSONObject;

public class DirectorySyncErrorHandler
{
    private static DirectorySyncErrorHandler directorySyncErrorHandler;
    
    public static DirectorySyncErrorHandler getInstance() {
        if (DirectorySyncErrorHandler.directorySyncErrorHandler == null) {
            DirectorySyncErrorHandler.directorySyncErrorHandler = new DirectorySyncErrorHandler();
        }
        return DirectorySyncErrorHandler.directorySyncErrorHandler;
    }
    
    private void handleErrorStep(final Long dmDomainID, final Long customerID, final String domainName, final String errorStep) {
        try {
            switch (errorStep) {
                case "suspendSyncTokens": {
                    final JSONObject taskDetails = new JSONObject();
                    taskDetails.put((Object)"DM_DOMAIN_ID", (Object)dmDomainID);
                    Connection connection = null;
                    try {
                        connection = RelationalAPI.getInstance().getConnection();
                        DirectorySequenceAsynchImpl.getInstance().suspendSyncTokens(connection, taskDetails, null, null);
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                        if (connection != null) {
                            try {
                                connection.close();
                            }
                            catch (final Exception ex) {
                                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex);
                            }
                        }
                    }
                    finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            }
                            catch (final Exception ex2) {
                                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex2);
                            }
                        }
                    }
                    break;
                }
                case "markSyncFailed": {
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "FETCH_STATUS", 901);
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", "");
                    IDPSlogger.AUDIT.log(Level.SEVERE, "{0} {1} {2} sync marked as failed", new Object[] { domainName, String.valueOf(customerID), String.valueOf(dmDomainID) });
                    break;
                }
            }
        }
        catch (final Exception ex3) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in handling directory sync error for:{0}", new Object[] { domainName });
            IDPSlogger.ERR.log(Level.SEVERE, null, ex3);
        }
    }
    
    public void handleError(Long dmDomainID, Long customerID, String domainName, Integer dmDomainClientID, final Throwable thrown, String errorType) {
        Properties dmDomainProps = null;
        if (dmDomainID != null) {
            dmDomainID = Long.valueOf(String.valueOf(dmDomainID));
            dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
        }
        else if (!SyMUtil.isStringEmpty(domainName) && customerID != null && dmDomainClientID != null) {
            customerID = Long.valueOf(String.valueOf(customerID));
            dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, dmDomainClientID);
        }
        if (dmDomainProps != null) {
            domainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
            dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
            dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
        }
        if (dmDomainID != null) {
            IDPSlogger.ERR.log(Level.SEVERE, "encountered error while syncing for :{0}", new Object[] { String.valueOf(domainName) });
            IDPSlogger.ERR.log(Level.SEVERE, "thrown", thrown);
            this.handleErrorStep(dmDomainID, customerID, domainName, "markSyncFailed");
            this.handleErrorStep(dmDomainID, customerID, domainName, "suspendSyncTokens");
            if (!SyMUtil.isStringEmpty(errorType)) {
                if (errorType.equalsIgnoreCase("DOMAIN_DUPLICATION_COUNT") || errorType.contains("INPUT_USER_NAME_DUPL_ERROR")) {
                    if (errorType.equalsIgnoreCase("DOMAIN_DUPLICATION_COUNT")) {
                        IDPSlogger.ERR.log(Level.SEVERE, "since domain {0} of cust {1} with domain_id {2} was present as a wg, deleted wg and syncing op ad with op_ad_domain_id {3}", new Object[] { String.valueOf(domainName), String.valueOf(customerID), String.valueOf(dmDomainID), String.valueOf(dmDomainID) });
                    }
                    final String key = "SYNC_AGAIN_DUE_TO_ERR" + String.valueOf(dmDomainClientID);
                    final boolean alreadySyncedAgainDueToErr = Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(key, 2)));
                    IDPSlogger.AUDIT.log(Level.INFO, "alreadySyncedAgainDueToErr : {0} , {1}", new Object[] { key, alreadySyncedAgainDueToErr });
                    if (!alreadySyncedAgainDueToErr) {
                        ApiFactoryProvider.getCacheAccessAPI().putCache(key, (Object)Boolean.TRUE, 2, 86400);
                        DirectoryUtil.getInstance().syncDomain(dmDomainProps, true);
                    }
                    else {
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(key, 2);
                    }
                }
                else {
                    if (errorType.equalsIgnoreCase("DOMAIN_UNREACHABLE_ERROR")) {
                        DirectoryUtil.getInstance().updateCredentialstatus(dmDomainID, false);
                    }
                    IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).handleError(dmDomainProps, thrown, errorType);
                }
            }
            else {
                errorType = "INTERNAL_ERROR";
                try {
                    final HashMap failureMap = new HashMap();
                    failureMap.put("REMARKS", "");
                    failureMap.put("SYNC_STATUS", "INTERNAL_ERROR");
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, failureMap);
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                }
            }
            try {
                final JSONObject qData = new JSONObject();
                qData.put((Object)"SOURCE", (Object)"DirectorySyncErrorHandler");
                DirectoryUtil.getInstance().addTaskToQueue("adEvent-task", DMDomainDataHandler.getInstance().getDomainById(dmDomainID), qData);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception occurred invvoking directorye event listeners from erro handling", ex);
            }
        }
        else {
            IDPSlogger.ERR.log(Level.SEVERE, "cannot do error handling:{0},{1},{2}", new Object[] { String.valueOf(dmDomainID), String.valueOf(domainName), String.valueOf(customerID) });
            IDPSlogger.ERR.log(Level.SEVERE, "thrown is", thrown);
            errorType = "INTERNAL_ERROR";
        }
        this.handleMEtracking(customerID, errorType);
        try {
            if (IdpsUtil.isFeatureAvailable("PERPETUAL_DIFF_SYNC")) {
                IDPSlogger.ERR.log(Level.SEVERE, "perpetual diff sync enabled and sync failed hence disabling this feature");
                IdpsUtil.updateFeatureAvailability("PERPETUAL_DIFF_SYNC", false);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleMEtracking(final Long customerID, final String errorType) {
        if (DirectoryMetricConstants.getTrackingKeys().contains(errorType)) {
            DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, errorType, 1);
        }
    }
    
    static {
        DirectorySyncErrorHandler.directorySyncErrorHandler = null;
    }
}
