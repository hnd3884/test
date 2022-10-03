package com.me.idps.core.sync.db;

import java.util.logging.Logger;
import java.util.Properties;
import java.util.Iterator;
import java.util.Set;
import com.me.idps.core.util.DirectoryQueryutil;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.WritableDataObject;
import com.me.idps.core.util.IdpsJSONutil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.me.idps.core.util.DirectoryAttributeConstants;
import com.me.idps.core.util.DirectoryUtil;
import org.json.simple.JSONObject;
import com.me.idps.core.sync.synch.DirSingletonQueue;

public class DirectoryTempDataHandler extends DirSingletonQueue
{
    public boolean isParallelProcessingQueue() {
        return true;
    }
    
    private Integer getParsedToken(final String token) {
        switch (token) {
            case "add": {
                return 1;
            }
            case "remove": {
                return 2;
            }
            default: {
                return null;
            }
        }
    }
    
    private JSONObject parseKeyForDetails(final String key) throws DataAccessException {
        final Long attrID = DirectoryUtil.getInstance().getAttrID(key);
        if (attrID != null) {
            final JSONObject keyDetails = new JSONObject();
            final Integer attrType = DirectoryAttributeConstants.getAttrType(attrID);
            final Integer attrDataType = DirectoryAttributeConstants.getAttrDataType(attrID);
            final String[] tokens = key.split(":");
            if (tokens != null && tokens.length == 2) {
                final Integer tokenInt = this.getParsedToken(tokens[1]);
                if (tokenInt != null) {
                    keyDetails.put((Object)"KEY_DETAIL", (Object)new Integer(tokenInt));
                }
            }
            keyDetails.put((Object)"ATTR_ID", (Object)attrID);
            keyDetails.put((Object)"ATTR_TYPE", (Object)attrType);
            keyDetails.put((Object)"ATTR_DATA_TYPE", (Object)attrDataType);
            return keyDetails;
        }
        return null;
    }
    
    private Row getDirObjTempRegIntValRow(final Object tempID, final Object attrID, final Integer value, final Long addedAt) {
        final Row dirObjTempRegIntValRow = new Row("DirObjTmpRegIntVal");
        dirObjTempRegIntValRow.set("TEMP_ID", tempID);
        dirObjTempRegIntValRow.set("ATTR_ID", attrID);
        dirObjTempRegIntValRow.set("TEMP_VALUE", (Object)value);
        dirObjTempRegIntValRow.set("ADDED_AT", (Object)addedAt);
        return dirObjTempRegIntValRow;
    }
    
    private Row getDirObjTempRegStrValRow(final Object tempID, final Object attrID, final String value, final Long addedAt) {
        final Row dirObjTempRegStrValRow = new Row("DirObjTmpRegStrVal");
        dirObjTempRegStrValRow.set("TEMP_ID", tempID);
        dirObjTempRegStrValRow.set("ATTR_ID", attrID);
        dirObjTempRegStrValRow.set("TEMP_VALUE", (Object)value);
        dirObjTempRegStrValRow.set("ADDED_AT", (Object)addedAt);
        return dirObjTempRegStrValRow;
    }
    
    private Row getDirObjTempArrStrVal(final Object tempID, final Object attrID, final String valueStr, final Integer dirResType, final Long attrAddedAt, final Long valAddedAt, final Object keyDetail) {
        final Row dirObjTempArrStrValRow = new Row("DirObjTmpArrStrVal");
        dirObjTempArrStrValRow.set("TEMP_ID", tempID);
        dirObjTempArrStrValRow.set("ATTR_ID", attrID);
        dirObjTempArrStrValRow.set("TEMP_VALUE", (Object)valueStr);
        dirObjTempArrStrValRow.set("VAL_ADDED_AT", (Object)valAddedAt);
        dirObjTempArrStrValRow.set("ATTR_ADDED_AT", (Object)attrAddedAt);
        dirObjTempArrStrValRow.set("DIR_RESOURCE_TYPE", (Object)dirResType);
        if (keyDetail != null) {
            dirObjTempArrStrValRow.set("KEY_DETAIL", (Object)Integer.valueOf(String.valueOf(keyDetail)));
        }
        return dirObjTempArrStrValRow;
    }
    
    private Row getDirObjTempRegValRow(final Object tempID, final Object attrID, final int attrDataType, final Object value, final Long attrAddedAt) {
        Row row = null;
        final String valueStr = String.valueOf(value);
        if (attrDataType == 2) {
            row = this.getDirObjTempRegIntValRow(tempID, attrID, Integer.valueOf(valueStr), attrAddedAt);
        }
        else if (attrDataType == 1) {
            row = this.getDirObjTempRegStrValRow(tempID, attrID, valueStr, attrAddedAt);
        }
        return row;
    }
    
    private long addDirObjTempAttrValRow(final DataObject dobj, final Object tempID, final Object attrID, final JSONObject keyDetails, final Object value, final long attrAddedAt, long valAddedAt, final long startedAt, final JSONObject timeStampAllocationObj) throws DataAccessException {
        final int attrType = (int)keyDetails.get((Object)"ATTR_TYPE");
        final int attrDataType = (int)keyDetails.get((Object)"ATTR_DATA_TYPE");
        final Object keyDetail = keyDetails.getOrDefault((Object)"KEY_DETAIL", (Object)null);
        switch (attrType) {
            case 1: {
                dobj.addRow(this.getDirObjTempRegValRow(tempID, attrID, attrDataType, value, attrAddedAt));
                timeStampAllocationObj.put((Object)String.valueOf(valAddedAt - startedAt), value);
                break;
            }
            case 2: {
                final JSONArray jsArray = (JSONArray)value;
                if (jsArray != null && !jsArray.isEmpty()) {
                    for (int j = 0; j < jsArray.size(); ++j) {
                        Integer dirResType = null;
                        String curElementValue = null;
                        final Object curValue = jsArray.get(j);
                        if (curValue instanceof JSONObject) {
                            final JSONObject curJSobject = (JSONObject)curValue;
                            curElementValue = (String)curJSobject.get((Object)"TEMP_VALUE");
                            dirResType = Integer.valueOf(String.valueOf(curJSobject.get((Object)"DIR_RESOURCE_TYPE")));
                        }
                        else {
                            curElementValue = String.valueOf(curValue);
                        }
                        if (!SyMUtil.isStringEmpty(curElementValue)) {
                            ++valAddedAt;
                            dobj.addRow(this.getDirObjTempArrStrVal(tempID, attrID, curElementValue, dirResType, attrAddedAt, valAddedAt, keyDetail));
                            timeStampAllocationObj.put((Object)String.valueOf(valAddedAt - startedAt), (Object)String.valueOf(curValue));
                        }
                    }
                    break;
                }
                ++valAddedAt;
                dobj.addRow(this.getDirObjTempArrStrVal(tempID, attrID, null, null, attrAddedAt, valAddedAt, keyDetail));
                timeStampAllocationObj.put((Object)String.valueOf(valAddedAt - startedAt), (Object)"NULL");
                break;
            }
        }
        return valAddedAt;
    }
    
    private void addDirObjTempRow(final DataObject dobj, final Long pkID, final JSONObject adObjProps, final int chunkResType, final Long syncToken, final long addedAt, final long batchStartedAt, final JSONObject timeStampAllocationObj) throws DataAccessException {
        final String objGUID = String.valueOf(IdpsJSONutil.opt(adObjProps, "objectGUID", null));
        final int dirResType = Integer.valueOf(String.valueOf(IdpsJSONutil.opt(adObjProps, "RESOURCE_TYPE", chunkResType)));
        final Row dirObjTempRow = new Row("DirObjTmp");
        dirObjTempRow.set("GUID", (Object)objGUID);
        dirObjTempRow.set("TEMP_ID", (Object)pkID);
        dirObjTempRow.set("ADDED_AT", (Object)addedAt);
        dirObjTempRow.set("SYNC_TOKEN_ID", (Object)syncToken);
        dirObjTempRow.set("OBJECT_TYPE", (Object)dirResType);
        dobj.addRow(dirObjTempRow);
        timeStampAllocationObj.put((Object)String.valueOf(addedAt - batchStartedAt), (Object)objGUID);
        adObjProps.remove((Object)"objectGUID");
        adObjProps.remove((Object)"RESOURCE_TYPE");
    }
    
    private Long addDirectorydataIntoTemp(final JSONArray adObjDetailsFromAD, final Long customerID, final Long dmDomainID, final Long syncTokenID, Long pkStart, final Long pkEnd, final int resType, final Long batchCanStartFrom, final JSONObject timeStampAllocationObj, final Long timeStampAllocation) throws Exception {
        int baseSize = adObjDetailsFromAD.size();
        int invalidCount = 0;
        long batchEndedAt;
        final long batchStartedAt = batchEndedAt = batchCanStartFrom;
        int totalTempSize = 0;
        int totalTempRegIntSize = 0;
        int totalTempRegStrSize = 0;
        int totalTempArrStrSize = 0;
        DataObject dobj = (DataObject)new WritableDataObject();
        for (int i = 0; i < adObjDetailsFromAD.size(); ++i) {
            if (i != 0) {
                ++batchEndedAt;
            }
            final JSONObject adObjProps = (JSONObject)adObjDetailsFromAD.get(i);
            this.addDirObjTempRow(dobj, pkStart, adObjProps, resType, syncTokenID, batchEndedAt, batchStartedAt, timeStampAllocationObj);
            final Set<String> keys = adObjProps.keySet();
            for (final String key : keys) {
                final JSONObject keyDetails = this.parseKeyForDetails(key);
                if (keyDetails != null && !keyDetails.isEmpty()) {
                    ++batchEndedAt;
                    timeStampAllocationObj.put((Object)String.valueOf(batchEndedAt - batchStartedAt), (Object)key);
                    final Object attrID = keyDetails.get((Object)"ATTR_ID");
                    final long valAddedAt = new Long(batchEndedAt);
                    batchEndedAt = this.addDirObjTempAttrValRow(dobj, pkStart, attrID, keyDetails, adObjProps.get((Object)key), batchEndedAt, valAddedAt, batchStartedAt, timeStampAllocationObj);
                }
            }
            final int tempSize = Math.max(0, dobj.size("DirObjTmp"));
            final int tempRegIntSize = Math.max(0, dobj.size("DirObjTmpRegIntVal"));
            final int tempRegStrSize = Math.max(0, dobj.size("DirObjTmpRegStrVal"));
            final int tempArrStrSize = Math.max(0, dobj.size("DirObjTmpArrStrVal"));
            baseSize -= tempSize;
            if (tempSize >= 10000 || tempRegIntSize >= 10000 || tempRegStrSize >= 10000 || tempArrStrSize >= 10000 || i == adObjDetailsFromAD.size() - 1) {
                totalTempSize += tempSize;
                totalTempRegIntSize += tempRegIntSize;
                totalTempRegStrSize += tempRegStrSize;
                totalTempArrStrSize += tempArrStrSize;
                invalidCount += DirectoryTempDataValidator.getInstance().validateData(dobj);
                if (pkStart <= pkEnd) {
                    int retry = 1;
                    if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                        retry = 10;
                    }
                    int k = 0;
                    while (k < retry) {
                        try {
                            SyMUtil.getPersistenceLite().add(dobj);
                        }
                        catch (final Exception ex) {
                            final String exMsg = ex.getMessage();
                            if (IdpsUtil.isStringEmpty(exMsg) || !exMsg.contains("was deadlocked on lock resources with another process and has been chosen as the deadlock victim. Rerun the transaction")) {
                                throw ex;
                            }
                            if (k == retry - 1) {
                                IDPSlogger.ERR.log(Level.SEVERE, "ms sql deadlock could not be resolved despite trying {0} re-runs", new Object[] { retry });
                                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                                DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, "MS_SQL_DEADLOCK_ERROR_COUNT", 1);
                                throw ex;
                            }
                            IDPSlogger.ERR.log(Level.SEVERE, "ms sql deadlock requires re-run", ex);
                            ++k;
                            continue;
                        }
                        break;
                    }
                    dobj = (DataObject)new WritableDataObject();
                }
            }
            if (i < adObjDetailsFromAD.size() - 1) {
                ++pkStart;
            }
        }
        if (pkStart != (long)pkEnd) {
            IDPSlogger.ERR.log(Level.SEVERE, "pk start {0} | pk end {1}", new Object[] { String.valueOf(pkStart), String.valueOf(pkEnd) });
            throw new Exception("PK_EXCEEDED_ERROR");
        }
        if (baseSize > 0) {
            IDPSlogger.ASYNCH.log(Level.SEVERE, "dropped data while inserting into temp:{0};", new Object[] { String.valueOf(baseSize) });
        }
        if (invalidCount > 0) {
            IDPSlogger.ASYNCH.log(Level.SEVERE, "invalid data while inserting into temp:{0}", new Object[] { String.valueOf(invalidCount) });
        }
        final JSONObject tempInjectSummary = new JSONObject();
        tempInjectSummary.put((Object)"DirObjTmp_COUNT", (Object)totalTempSize);
        tempInjectSummary.put((Object)"DirObjTmpRegIntVal_COUNT", (Object)totalTempRegIntSize);
        tempInjectSummary.put((Object)"DirObjTmpRegStrVal_COUNT", (Object)totalTempRegStrSize);
        tempInjectSummary.put((Object)"DirObjTmpArrStrVal_COUNT", (Object)totalTempArrStrSize);
        tempInjectSummary.put((Object)"time_stamp_allocation", (Object)String.valueOf(timeStampAllocation));
        IDPSlogger.ASYNCH.log(Level.INFO, IdpsUtil.getPrettyJSON(tempInjectSummary));
        DirectoryQueryutil.getInstance().incrementDbOpsMetric(dmDomainID, syncTokenID, "DirectoryTempDataHandler", 1, totalTempSize + totalTempRegIntSize + totalTempRegStrSize + totalTempArrStrSize);
        return batchEndedAt;
    }
    
    public void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        final String rawDataFilePath = (String)qData.get((Object)"FILE_PATHS");
        final String validatorAllocationPath = (String)qData.get((Object)"validatorAllocationPath");
        final int chunkResType = Integer.valueOf(String.valueOf(qData.get((Object)"OBJECT_TYPE")));
        try {
            final JSONObject injectorAllocation = new JSONObject();
            final JSONArray rawData = (JSONArray)DirectoryUtil.getInstance().readFile(rawDataFilePath);
            final int numOfPKsrequired = Integer.valueOf(String.valueOf(qData.get((Object)"PRE_PROCESSED_COUNT")));
            final long[] pkRange = this.allocatePKs(numOfPKsrequired);
            final Long timeStampStart = Long.valueOf(String.valueOf(qData.get((Object)"time_stamp_start")));
            final Long timeStampExpectedCompletion = Long.valueOf(String.valueOf(qData.get((Object)"time_stamp_end")));
            final Long timeStampAllocation = timeStampExpectedCompletion - timeStampStart;
            final Long syncTokenID = Long.valueOf(String.valueOf(qData.get((Object)"SYNC_TOKEN_ID")));
            final Long pkStart = pkRange[0];
            final Long pkEnd = pkRange[1];
            final Long batchEndedAt = this.addDirectorydataIntoTemp(rawData, customerID, dmDomainID, syncTokenID, pkStart, pkEnd, chunkResType, timeStampStart, injectorAllocation, timeStampAllocation);
            final Long bathInsertedAt = System.currentTimeMillis();
            if (batchEndedAt != (long)timeStampExpectedCompletion) {
                throw new Exception("allocated range violated");
            }
            final int lastCount = (int)DBUtil.getValueFromDB("DirectorySyncDetails", "SYNC_TOKEN_ID", (Object)syncTokenID, "LAST_COUNT");
            if (lastCount > 0) {
                final JSONObject taskDetails = DirectoryUtil.getInstance().getNewTaskDetails(qData);
                taskDetails.put((Object)"SYNC_TOKEN_ID", (Object)syncTokenID);
                taskDetails.put((Object)"LATEST_BATCH_PROCESSED_AT", (Object)bathInsertedAt);
                taskDetails.put((Object)"TASK_TYPE", (Object)"tempInjected");
                DirectoryUtil.getInstance().addTaskToQueue("adProc-task", null, taskDetails);
            }
        }
        finally {
            DirectoryUtil.getInstance().deleteFile(rawDataFilePath);
            DirectoryUtil.getInstance().deleteFile(validatorAllocationPath);
        }
    }
    
    @Override
    protected Logger getLogger() {
        return IDPSlogger.SYNC;
    }
    
    @Override
    protected TABLE getTablePKGen() {
        return TABLE.DIROBJTMPTBL;
    }
}
