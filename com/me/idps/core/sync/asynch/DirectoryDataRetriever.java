package com.me.idps.core.sync.asynch;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import com.me.idps.core.util.IdpsUtil;
import java.util.Properties;
import com.me.idps.core.factory.IdpsAccessAPI;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.idps.core.util.DirectoryUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.factory.IdpsFactoryProvider;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.HashMap;
import com.me.idps.core.util.DirQueue;

public class DirectoryDataRetriever extends DirQueue
{
    private static HashMap<Long, Thread> syncTokenThreadMap;
    private static HashMap<Long, List<Thread>> domainThreadMap;
    
    public boolean isParallelProcessingQueue() {
        return true;
    }
    
    public void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dirClientID, final JSONObject qData) throws Exception {
        final Long syncTokenID = Long.valueOf(String.valueOf(qData.get((Object)"SYNC_TOKEN_ID")));
        final Boolean doFullSync = Boolean.valueOf(String.valueOf(qData.get((Object)"doFullSync")));
        final Boolean syncAllowable = DirectorySequenceAsynchImpl.getInstance().checkDomainSyncReady(dmDomainID, dmDomainName, false);
        if (syncAllowable != null && syncAllowable) {
            final IdpsAccessAPI idpsAccessAPI = IdpsFactoryProvider.getIdpsAccessAPI(dirClientID);
            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
            final boolean isADReachable = idpsAccessAPI.isADDomainReachable(dmDomainProps);
            if (!isADReachable) {
                IDPSlogger.ASYNCH.log(Level.SEVERE, "Directory {0} is not reachable", new Object[] { dmDomainName });
                throw new Exception(dmDomainName + " is not reachable");
            }
            DirectoryUtil.getInstance().updateCredentialstatus(dmDomainID, true);
            this.handleZDopADuserSync(dirClientID, dmDomainProps);
            IDPSlogger.ASYNCH.log(Level.INFO, "Initiating sync for:{0}|{1}|{2}", new Object[] { String.valueOf(dmDomainID), dmDomainName, String.valueOf(customerID) });
            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "LAST_SYNC_INITIATED", Long.valueOf(String.valueOf(qData.get((Object)"LAST_SYNC_INITIATED"))));
            try {
                final DataFetcher dataFetcher = new DataFetcher(idpsAccessAPI, dmDomainID, dirClientID, syncTokenID, dmDomainProps, (List)DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(dmDomainID), (boolean)doFullSync);
                final FutureTask futureTask = new FutureTask(dataFetcher);
                final Thread fetchThread = new Thread(futureTask);
                this.mapDataFetcherThreadToDomain(dmDomainID, syncTokenID, fetchThread);
                IDPSlogger.ASYNCH.log(Level.INFO, "going to invoke fetch data from another thread for dmDomainID:{0},clientID:{1},syncTokenID:{2}", new Object[] { String.valueOf(dmDomainID), String.valueOf(dirClientID), String.valueOf(syncTokenID) });
                fetchThread.start();
                try {
                    final Boolean taskCompleted = futureTask.get();
                    IDPSlogger.ASYNCH.log(Level.INFO, "taskCompleted from another thread for dmDomainID:{0},clientID:{1},syncTokenID:{2}, taskCompleted:{3}", new Object[] { String.valueOf(dmDomainID), String.valueOf(dirClientID), String.valueOf(syncTokenID), String.valueOf(taskCompleted) });
                    if (taskCompleted == null || (taskCompleted != null && !taskCompleted)) {
                        throw new Exception("INTERNAL_ERROR");
                    }
                }
                catch (final ExecutionException ee) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ee);
                    final Throwable t = ee.getCause();
                    IDPSlogger.ERR.log(Level.SEVERE, null, t);
                    if (t == null) {
                        throw new Exception("INTERNAL_ERROR");
                    }
                    if (t instanceof Exception) {
                        throw (Exception)t;
                    }
                    throw new Exception("INTERNAL_ERROR");
                }
                catch (final InterruptedException ie) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ie);
                    throw new Exception("sync interrupted");
                }
            }
            catch (final Exception ex) {
                final String exMsg = ex.getMessage();
                if (SyMUtil.isStringEmpty(exMsg) || (!exMsg.contains("|NULLData|") && !exMsg.contains("|InvalidSyncToken|"))) {
                    IDPSlogger.ERR.log(Level.SEVERE, "adapter ex", ex);
                    throw new Exception(dmDomainName + " exception from adapter");
                }
                IDPSlogger.ERR.log(Level.SEVERE, "just because we can swim we should not swim in every puddle of water", ex);
            }
            finally {
                DirectorySyncThreadLocal.clearDomain();
                DirectorySyncThreadLocal.clearClientID();
                DirectorySyncThreadLocal.clearSyncToken();
            }
        }
        else {
            IDPSlogger.SYNC.log(Level.INFO, "AD domain :{0}  has a sync already ongoing", new Object[] { dmDomainName });
        }
    }
    
    private void handleZDopADuserSync(final Integer dirClientID, final Properties dmDomainProps) throws Exception {
        if (dirClientID != null && dirClientID == 201 && !IdpsUtil.isFeatureAvailable("CLOUD_OPAD_USER_HANDLING")) {
            final Set<Integer> objectsToBeSynced = new HashSet<Integer>();
            objectsToBeSynced.add(1000);
            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(dmDomainProps, objectsToBeSynced, true, false);
            IdpsUtil.updateFeatureAvailability("CLOUD_OPAD_USER_HANDLING", true);
        }
    }
    
    private void mapDataFetcherThreadToDomain(final Long dmDomainID, final Long syncTokenID, final Thread thread) {
        DirectoryDataRetriever.syncTokenThreadMap.put(syncTokenID, thread);
        List<Thread> domainDataFetchThreads = DirectoryDataRetriever.domainThreadMap.get(dmDomainID);
        if (domainDataFetchThreads == null) {
            domainDataFetchThreads = new ArrayList<Thread>();
        }
        domainDataFetchThreads.add(thread);
        DirectoryDataRetriever.domainThreadMap.put(dmDomainID, domainDataFetchThreads);
    }
    
    public boolean stopADSyncByDomainID(final Long dmDomainID) {
        final List<Thread> threads = DirectoryDataRetriever.domainThreadMap.get(dmDomainID);
        if (threads != null) {
            IDPSlogger.AUDIT.log(Level.SEVERE, "going to interrupt {0} threads associated with {1}", new Object[0]);
            for (final Thread curThread : threads) {
                if (curThread != null) {
                    curThread.interrupt();
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean stopADSyncBySyncTokenID(final Long syncTokenID) {
        final Thread thread = DirectoryDataRetriever.syncTokenThreadMap.get(syncTokenID);
        if (thread != null) {
            thread.interrupt();
            return true;
        }
        return false;
    }
    
    static {
        DirectoryDataRetriever.syncTokenThreadMap = new HashMap<Long, Thread>();
        DirectoryDataRetriever.domainThreadMap = new HashMap<Long, List<Thread>>();
    }
    
    private class DataFetcher implements Callable
    {
        Long dmDomainID;
        Long syncTokenID;
        boolean doFullSync;
        Integer dirClientID;
        List<Integer> syncObjects;
        IdpsAccessAPI idpsAccessAPI;
        Properties dmDomainProperties;
        
        private DataFetcher(final IdpsAccessAPI idpsAccessAPI, final Long dmDomainID, final Integer dirClientID, final Long syncTokenID, final Properties dmDomainProperties, final List<Integer> syncObjects, final boolean doFullSync) {
            this.dmDomainID = dmDomainID;
            this.doFullSync = doFullSync;
            this.syncTokenID = syncTokenID;
            this.syncObjects = syncObjects;
            this.dirClientID = dirClientID;
            this.idpsAccessAPI = idpsAccessAPI;
            this.dmDomainProperties = dmDomainProperties;
        }
        
        @Override
        public Object call() throws Exception {
            try {
                DirectorySyncThreadLocal.setDomainID(this.dmDomainID);
                DirectorySyncThreadLocal.setClientID(this.dirClientID);
                DirectorySyncThreadLocal.setSyncToken(this.syncTokenID);
                IDPSlogger.ASYNCH.log(Level.INFO, "fetching data from another thread using callable for dmDomainID:{0},clientID:{1},syncTokenID:{2}", new Object[] { String.valueOf(this.dmDomainID), String.valueOf(this.dirClientID), String.valueOf(this.syncTokenID) });
                this.idpsAccessAPI.fetchBulkADData(this.dmDomainProperties, this.syncObjects, this.doFullSync);
                IDPSlogger.ASYNCH.log(Level.INFO, "fetched data from another thread using callable for dmDomainID:{0},clientID:{1},syncTokenID:{2}", new Object[] { String.valueOf(this.dmDomainID), String.valueOf(this.dirClientID), String.valueOf(this.syncTokenID) });
                return true;
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                throw ex;
            }
            finally {
                DirectorySyncThreadLocal.clearDomain();
                DirectorySyncThreadLocal.clearClientID();
                DirectorySyncThreadLocal.clearSyncToken();
            }
        }
    }
}
