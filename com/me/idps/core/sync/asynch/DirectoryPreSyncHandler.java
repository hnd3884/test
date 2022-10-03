package com.me.idps.core.sync.asynch;

import java.util.Hashtable;
import com.me.idps.core.util.DataPoster;
import java.util.Properties;
import com.me.idps.core.sync.db.DirectoryTempDataValidator;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DirectoryUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import org.json.simple.JSONObject;

class DirectoryPreSyncHandler
{
    private static DirectoryPreSyncHandler directoryPreSyncHandler;
    
    static DirectoryPreSyncHandler getInstance() {
        if (DirectoryPreSyncHandler.directoryPreSyncHandler == null) {
            DirectoryPreSyncHandler.directoryPreSyncHandler = new DirectoryPreSyncHandler();
        }
        return DirectoryPreSyncHandler.directoryPreSyncHandler;
    }
    
    void preSync(final JSONObject qNode) throws Exception {
        final MutableInt fileSize = new MutableInt(0);
        final MutableInt fileReadTimeTaken = new MutableInt(0);
        final MutableInt fileDeleteTimeTaken = new MutableInt(0);
        final String rawFilePath = (String)qNode.get((Object)"DirResRel");
        final JSONObject rawData = (JSONObject)DirectoryUtil.getInstance().readAndDeleteFile(rawFilePath, fileReadTimeTaken, fileDeleteTimeTaken, fileSize);
        JSONArray directoryData = (JSONArray)rawData.get((Object)"DirResRel");
        final Long dmDomainID = Long.valueOf(String.valueOf(qNode.get((Object)"DOMAIN_ID")));
        final Integer resType = Integer.valueOf(String.valueOf(qNode.get((Object)"DIR_RESOURCE_TYPE")));
        final Integer dmDomainClientID = Integer.valueOf(String.valueOf(qNode.get((Object)"CLIENT_ID")));
        final Long syncTokenID = Long.valueOf(String.valueOf(qNode.get((Object)"SYNC_TOKEN_ID")));
        Boolean isLastList = Boolean.valueOf(String.valueOf(qNode.get((Object)"LAST_COUNT")));
        Boolean isFirstList = Boolean.valueOf(String.valueOf(qNode.get((Object)"FIRST_COUNT")));
        boolean doPreSync = true;
        final String sourceHint = String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("DirectorySyncDetails_SOURCE_" + dmDomainID + "_" + syncTokenID, 2));
        if (!SyMUtil.isStringEmpty(sourceHint) && sourceHint.equalsIgnoreCase("DISABLE_RES_TYPE_SYNC")) {
            doPreSync = false;
        }
        if (doPreSync) {
            final Properties processedSyncInput = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).preSyncOperations(qNode, resType, directoryData, isFirstList, isLastList);
            isLastList = ((Hashtable<K, Boolean>)processedSyncInput).get("LAST_COUNT");
            isFirstList = ((Hashtable<K, Boolean>)processedSyncInput).get("FIRST_COUNT");
            directoryData = ((Hashtable<K, JSONArray>)processedSyncInput).get("DirResRel");
        }
        if (isLastList) {
            DirectorySequenceAsynchImpl.getInstance().incrementLastCount(syncTokenID);
        }
        if (isFirstList) {
            DirectorySequenceAsynchImpl.getInstance().incrementFirstCount(syncTokenID);
        }
        final JSONObject filteredDataDetails = DirectoryTempDataValidator.getInstance().filterInvalidData(directoryData, dmDomainClientID, resType);
        final JSONArray filteredData = (JSONArray)filteredDataDetails.get((Object)"DirResRel");
        final JSONObject taskDetails = DirectoryUtil.getInstance().getNewTaskDetails(qNode);
        taskDetails.put((Object)"OBJECT_TYPE", (Object)resType);
        taskDetails.put((Object)"DirResRel", (Object)filteredData);
        taskDetails.put((Object)"FILE_READ_SIZE", (Object)String.valueOf(fileSize));
        taskDetails.put((Object)"FILE_DELETE_SIZE", (Object)String.valueOf(fileSize));
        taskDetails.put((Object)"FILE_READ_TIME_TAKEN", (Object)String.valueOf(fileReadTimeTaken));
        taskDetails.put((Object)"FILE_DELETE_TIME_TAKEN", (Object)String.valueOf(fileDeleteTimeTaken));
        taskDetails.put((Object)"TASK_TYPE", (Object)"updateTally");
        taskDetails.put((Object)"SYNC_TOKEN_ID", qNode.get((Object)"SYNC_TOKEN_ID"));
        if (filteredDataDetails.containsKey((Object)"validatorAllocationPath")) {
            taskDetails.put((Object)"validatorAllocationPath", filteredDataDetails.get((Object)"validatorAllocationPath"));
        }
        taskDetails.put((Object)"PRE_PROCESSED_COUNT", (Object)filteredData.size());
        taskDetails.put((Object)"CURRENT_BATCH_POSTED_AT", qNode.get((Object)"CURRENT_BATCH_POSTED_AT"));
        taskDetails.put((Object)"PREVIOUS_BATCH_COMPLETED_AT", filteredDataDetails.get((Object)"PREVIOUS_BATCH_COMPLETED_AT"));
        new DataPosterImpl(taskDetails).run();
    }
    
    static {
        DirectoryPreSyncHandler.directoryPreSyncHandler = null;
    }
    
    private class DataPosterImpl extends DataPoster
    {
        @Override
        protected byte[] handlePre() {
            final JSONArray filteredData = (JSONArray)this.taskDetails.get((Object)"DirResRel");
            this.taskDetails.remove((Object)"DirResRel");
            return filteredData.toString().getBytes();
        }
        
        @Override
        protected void handlePost(final String dataFilePath, final int timeTaken, final int bytes) throws Exception {
            final JSONArray writeSize = (JSONArray)this.taskDetails.get((Object)"FILE_WRITE_SIZE");
            final JSONArray writeTimeTaken = (JSONArray)this.taskDetails.get((Object)"FILE_WRITE_TIME_TAKEN");
            writeSize.add((Object)bytes);
            writeTimeTaken.add((Object)timeTaken);
            this.taskDetails.put((Object)"FILE_PATHS", (Object)dataFilePath);
            this.taskDetails.put((Object)"FILE_WRITE_SIZE", (Object)writeSize);
            this.taskDetails.put((Object)"FILE_WRITE_TIME_TAKEN", (Object)writeTimeTaken);
            DirectoryUtil.getInstance().addTaskToQueue("adProc-task", null, this.taskDetails);
        }
        
        protected DataPosterImpl(final JSONObject taskDetails) {
            super(taskDetails);
        }
    }
}
