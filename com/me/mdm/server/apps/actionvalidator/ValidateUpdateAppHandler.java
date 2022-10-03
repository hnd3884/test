package com.me.mdm.server.apps.actionvalidator;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ValidateUpdateAppHandler
{
    private static Logger logger;
    
    public JSONObject validateData(final JSONObject data) {
        final JSONArray responseArray = new JSONArray();
        JSONObject validatedResponseObject = null;
        JSONObject responseData = new JSONObject();
        try {
            final ValidateAppDataHandler appDataHandler = new ValidateAppDataHandler();
            final JSONObject object = appDataHandler.validateDataForRequest(data);
            if (object == null) {
                responseData = new JSONObject();
                responseData.put("Status", (Object)"Failed");
                return responseData;
            }
            final SelectQuery query = this.getSelectQuery(object);
            final DataObject dataObject = this.getDataObject(query);
            final List<String> updateProcessors = this.processorForUpdate();
            final List failedList = new ArrayList();
            object.put("Handler", 1);
            for (final String processer : updateProcessors) {
                final AppActionValidator processorObject = (AppActionValidator)Class.forName(processer).newInstance();
                final JSONObject responseObject = processorObject.validate(object, dataObject);
                final Object failedObject = responseObject.opt("FailedList");
                final List failed = (failedObject != null) ? ((List)failedObject) : new ArrayList();
                failedList.addAll(failed);
                responseObject.remove("FailedList");
                if (responseObject.length() != 0) {
                    responseArray.add((Object)responseObject);
                }
            }
            final Object[] collection = (Object[])object.opt("collectionList");
            final List<Long> successList = new ArrayList<Long>((Collection<? extends Long>)Arrays.asList(collection));
            successList.removeAll(failedList);
            if (successList != null & !successList.isEmpty()) {
                final Long[] success = successList.toArray(new Long[successList.size()]);
                object.put("collectionList", (Object)success);
                final JSONObject successObject = appDataHandler.addSuccessCollection(object, dataObject);
                responseArray.add((Object)successObject);
            }
            validatedResponseObject = new JSONObject();
            validatedResponseObject.put("ValidationResult", (Collection)responseArray);
            responseData.put("Response", (Object)validatedResponseObject);
            responseData.put("Status", (Object)"Acknowledge");
        }
        catch (final Exception e) {
            ValidateUpdateAppHandler.logger.log(Level.SEVERE, "Exception while processing Update request processing", e);
            try {
                responseData.put("Status", (Object)"Failed");
            }
            catch (final JSONException e2) {
                ValidateUpdateAppHandler.logger.log(Level.SEVERE, "Exception while processing Update request processing", (Throwable)e2);
            }
        }
        return responseData;
    }
    
    private List<String> processorForUpdate() {
        final List<String> updateProcessors = new ArrayList<String>();
        updateProcessors.add("com.me.mdm.server.apps.actionvalidator.IOSKioskAppUpdateValidator");
        return updateProcessors;
    }
    
    private SelectQuery getSelectQuery(final JSONObject object) {
        final ValidateAppDataHandler handler = new ValidateAppDataHandler();
        final SelectQuery selectQuery = handler.getBaseQuery(object);
        this.addProfilejoin(selectQuery, object.optString("ProfileTable"));
        return selectQuery;
    }
    
    private void addProfilejoin(final SelectQuery selectQuery, final String collectionTable) {
        selectQuery.addJoin(new Join(collectionTable, "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        selectQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
    }
    
    private DataObject getDataObject(final SelectQuery query) throws Exception {
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        return dataObject;
    }
    
    static {
        ValidateUpdateAppHandler.logger = Logger.getLogger("MDMLogger");
    }
}
