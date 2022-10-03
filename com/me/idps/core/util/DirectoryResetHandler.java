package com.me.idps.core.util;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.upgrade.AzureOAuth210902;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.idps.core.oauth.OauthUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Set;
import java.util.Properties;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.JSONObject;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.List;
import java.sql.Connection;

public class DirectoryResetHandler
{
    private static DirectoryResetHandler directoryResetHandler;
    
    public static DirectoryResetHandler getInstance() {
        if (DirectoryResetHandler.directoryResetHandler == null) {
            DirectoryResetHandler.directoryResetHandler = new DirectoryResetHandler();
        }
        return DirectoryResetHandler.directoryResetHandler;
    }
    
    private void clearTable(final Connection connection, final List<String> listOfTablesToBeCleared) {
        for (int i = 0; i < listOfTablesToBeCleared.size(); ++i) {
            final String tableName = listOfTablesToBeCleared.get(i);
            try {
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td != null) {
                    final PrimaryKeyDefinition pkd = td.getPrimaryKey();
                    final List<String> pkColNames = pkd.getColumnList();
                    Criteria criteria = null;
                    for (int j = 0; pkColNames != null && j < pkColNames.size(); ++j) {
                        final String curCol = pkColNames.get(j);
                        final Criteria curCri = new Criteria(Column.getColumn(tableName, curCol), (Object)null, 1);
                        if (criteria != null) {
                            criteria = criteria.and(curCri);
                        }
                        else {
                            criteria = curCri;
                        }
                    }
                    if (criteria != null) {
                        deleteQuery.setCriteria(criteria);
                        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
                    }
                }
            }
            catch (final Exception ex1) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in clearing " + tableName, ex1);
            }
        }
    }
    
    private void resetDirSync(final Connection connection, final Long customerID, final boolean hardReset) throws Exception {
        try {
            DirectorySequenceAsynchImpl.getInstance().suspendSyncTokens(connection, null, null, null);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in suspending sync tokens", ex);
        }
        try {
            DirectorySequenceAsynchImpl.getInstance().clearSuspendedSyncTokens(null);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in clearing sync tokens data", ex);
        }
        this.clearTable(connection, new ArrayList<String>(Arrays.asList("AdProcQueueData", "AdRetreiverQueueData", "AdAsyncQueueData", "AdTempQueueData", "AdCoreDBQueueData", "DirectoryCollateRequest")));
        if (hardReset) {
            try {
                final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                dirProdImplRequest.eventType = IdpEventConstants.CUSTOM_OPS;
                dirProdImplRequest.args = new Object[] { "CHECK_FOR_CG_CLEANUP", connection };
                DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in checking dir synced cg dupl", ex);
            }
            this.clearTable(connection, new ArrayList<String>(Arrays.asList("DirObjAttrValue", "DirectoryResourceRel", "DirectoryUser", "DirectoryUserData", "MDMADUsersTemp", "ADSyncSettings", "DirObjTempAttrValue", "DirObjTempAttrLargeValue", "DirectorySyncMemberRelTemp", "DirectorySyncTemp", "DirectoryGroupMemberRel")));
        }
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DMDomainSyncDetails");
            updateQuery.setCriteria(new Criteria(Column.getColumn("DMDomainSyncDetails", "SYNC_STATUS"), (Object)String.valueOf(921), 0, false));
            updateQuery.setUpdateColumn("FETCH_STATUS", (Object)921);
            DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in reseting sync progress", e);
        }
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DMDomainSyncDetails");
            updateQuery.setCriteria(new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)921, 1));
            updateQuery.setUpdateColumn("FETCH_STATUS", (Object)901);
            DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in reseting sync progress", e);
        }
        if (hardReset) {
            try {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectoryMetrics");
                updateQuery.setCriteria(new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)"*ERR*", 2, false));
                updateQuery.setUpdateColumn("VALUE", (Object)String.valueOf(0));
                DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in reseting err count", ex);
            }
            DirectoryUtil.getInstance().updateDirectoryMetrics();
        }
        IdpsFactoryProvider.getIdpsProdEnvAPI().reset(connection, customerID, hardReset);
        this.validateSyncObjects(connection);
    }
    
    private void validateSyncObjects(final Connection connection) throws Exception {
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.eventType = IdpEventConstants.CUSTOM_OPS;
        dirProdImplRequest.args = new Object[] { "REM_DEFAULT_AZURE_DEVICE", connection };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        final List<Properties> dmDomainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(null);
        for (int i = 0; i < dmDomainProps.size(); ++i) {
            final Properties dmDomainProp = dmDomainProps.get(i);
            final Integer dmDomainClient = ((Hashtable<K, Integer>)dmDomainProp).get("CLIENT_ID");
            try {
                final Set<Integer> defaultSyncObjTypes = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClient).getDefaultSyncObjectTypes();
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(dmDomainProp, defaultSyncObjTypes, true, false);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void doAzureMamHandling() {
        try {
            final Criteria azureMamCri = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TYPE"), (Object)203, 0);
            final DataObject azureMamDObj = SyMUtil.getPersistenceLite().get("OauthTokens", azureMamCri);
            if (azureMamDObj != null && azureMamDObj.containsTable("OauthTokens")) {
                final Iterator itr = azureMamDObj.getRows("OauthTokens");
                while (itr != null && itr.hasNext()) {
                    final Row row = itr.next();
                    if (row != null) {
                        final Long oauthTokenID = (Long)row.get("OAUTH_TOKEN_ID");
                        if (oauthTokenID == null) {
                            continue;
                        }
                        try {
                            OauthUtil.getInstance().fetchAccessTokenFromOauthId(oauthTokenID);
                        }
                        catch (final Exception ex) {
                            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        catch (final Exception ex2) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
        }
    }
    
    public void reset() {
        this.reset(false);
    }
    
    public void reset(final boolean fromScheduler) {
        IDPSlogger.SYNC.log(Level.INFO, "starting directory sync reset handling...");
        try {
            boolean missedMessageHandling = false;
            try {
                missedMessageHandling = Boolean.parseBoolean(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("AzureOAuth210902", 2)));
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int index = 0; customerIDs != null && index < customerIDs.length; ++index) {
                final Long customerID = customerIDs[index];
                if (missedMessageHandling) {
                    new AzureOAuth210902().handleUpgrade(customerID);
                }
                if (fromScheduler) {
                    final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                    dirProdImplRequest.eventType = IdpEventConstants.SCHEDULER_SPREAD_ADJUST;
                    dirProdImplRequest.args = new Object[] { customerID, "ADUsersSyncScheduler", "MDMADSyncLogger", true, -1L, false };
                    DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                }
                boolean hardReset = IdpsUtil.isFeatureAvailable("HARD_RESET");
                final boolean hardResetInCache = Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("HARD_RESET", 2)));
                hardReset |= hardResetInCache;
                IDPSlogger.SYNC.log(Level.INFO, "{0} reset for {1}", new Object[] { hardReset ? "Hard" : "Soft", String.valueOf(customerID) });
                if (hardReset) {
                    IdpsUtil.updateFeatureAvailability("HARD_RESET", false);
                    ApiFactoryProvider.getCacheAccessAPI().removeCache("HARD_RESET", 2);
                }
                Connection connection = null;
                try {
                    connection = RelationalAPI.getInstance().getConnection();
                    this.resetDirSync(connection, customerID, hardReset);
                    IDPSlogger.SYNC.log(Level.INFO, "done directory sync reset handling...");
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, "could not do reset handling", ex2);
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                    }
                }
                finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                    catch (final Exception ex3) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex3);
                    }
                }
                if (hardReset) {
                    final boolean stopped = IdpsFactoryProvider.getIdpsProdEnvAPI().checkAndStopADSyncSchduler(customerID);
                    if (!stopped) {
                        IdpsFactoryProvider.getIdpsProdEnvAPI().startADSyncScheduler(customerID);
                        IDPSlogger.SYNC.log(Level.INFO, ".. enabled directory sync scheduler for cust{0}", new String[] { String.valueOf(customerID) });
                    }
                    else {
                        IDPSlogger.SYNC.log(Level.INFO, ".. disabled directory sync scheduler for cust{0}", new String[] { String.valueOf(customerID) });
                    }
                }
                boolean doFullSync = hardReset;
                if (IdpsUtil.isFeatureAvailable("PERPETUAL_DIFF_SYNC")) {
                    doFullSync = true;
                }
                DirectoryUtil.getInstance().syncAllDomains(customerID, doFullSync);
                IDPSlogger.SYNC.log(Level.INFO, ".. initiated {0} sync for all directories of cust{1}", new String[] { hardReset ? "full" : "diff", String.valueOf(customerID) });
                this.doAzureMamHandling();
            }
        }
        catch (final Exception ex4) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex4);
        }
        finally {
            this.saveLazyResetHandlingDoneAt();
        }
        IDPSlogger.SYNC.log(Level.INFO, "exiting directory sync reset handling...");
    }
    
    private void saveLazyResetHandlingDoneAt() {
        ApiFactoryProvider.getCacheAccessAPI().putCache("lastLazyResetHandlingDoneAt", (Object)System.currentTimeMillis(), 2, 86400);
    }
    
    public void doLazyResetHandling() {
        boolean pass = true;
        final Object obj = ApiFactoryProvider.getCacheAccessAPI().getCache("lastLazyResetHandlingDoneAt", 2);
        IDPSlogger.ASYNCH.log(Level.INFO, "doLazyResetHandling obj:{0}", new Object[] { obj });
        if (obj != null) {
            try {
                final Long lastLazyResetHandlingDoneAt = Long.valueOf(String.valueOf(obj));
                final long timeDiff = System.currentTimeMillis() - lastLazyResetHandlingDoneAt;
                final long timeDiffInDays = TimeUnit.MILLISECONDS.toDays(timeDiff);
                IDPSlogger.ASYNCH.log(Level.INFO, "doLazyResetHandling timeDiff:{0}, timeDiffInDays:{1}", new Object[] { String.valueOf(timeDiff), timeDiffInDays });
                if (timeDiffInDays < 1L) {
                    pass = false;
                }
            }
            catch (final Exception e) {
                IDPSlogger.ERR.log(Level.WARNING, "could not parse lastLazyResetHandlingDoneAt");
            }
        }
        if (pass) {
            DirectoryUtil.getInstance().initiateResetHandling();
        }
    }
    
    static {
        DirectoryResetHandler.directoryResetHandler = null;
    }
}
