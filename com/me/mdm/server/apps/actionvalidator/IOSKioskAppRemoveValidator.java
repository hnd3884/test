package com.me.mdm.server.apps.actionvalidator;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import java.util.Arrays;
import com.adventnet.i18n.I18N;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class IOSKioskAppRemoveValidator implements AppActionValidator
{
    @Override
    public JSONObject validate(final JSONObject requestObject, final DataObject dataObject) throws Exception {
        final JSONObject object = this.iOSKioskProcessor(requestObject, dataObject);
        final Object failedObject = object.opt("FailedList");
        final List failed = (failedObject != null) ? ((List)failedObject) : new ArrayList();
        if (failed.size() > 0) {
            object.put("I18NRemark", (Object)I18N.getMsg("mdm.apps.ios.kioskRemove_notAllowed", new Object[0]));
        }
        return object;
    }
    
    public JSONObject iOSKioskProcessor(final JSONObject requestObject, final DataObject dataObject) throws Exception {
        final List kioskCollectionList = new ArrayList();
        final Long[] collection = (Long[])requestObject.opt("collectionList");
        final List<Long> collectionList = Arrays.asList(collection);
        final String profileTableName = requestObject.optString("ProfileTable");
        final JSONArray appArray = new JSONArray();
        final JSONObject object = new JSONObject();
        for (int i = 0; i < collectionList.size(); ++i) {
            final Long collectionId = collectionList.get(i);
            final Criteria collectionCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final List tableName = new ArrayList();
            tableName.add("MdAppGroupDetails");
            tableName.add("MdAppToGroupRel");
            tableName.add("MdAppToCollection");
            final Row appDetailsRow = dataObject.getRow("MdAppToCollection", collectionCriteria);
            if (appDetailsRow != null) {
                final DataObject appDataObject = dataObject.getDataObject(tableName, appDetailsRow);
                final Iterator appIterator = appDataObject.getRows("MdAppGroupDetails");
                while (appIterator.hasNext()) {
                    final Row row = appIterator.next();
                    final String appName = (String)row.get("GROUP_DISPLAY_NAME");
                    final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                    final Criteria appGroupCriteria = new Criteria(new Column("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appGroupId, 0);
                    final List kioskTableName = new ArrayList();
                    kioskTableName.add("AppLockPolicy");
                    kioskTableName.add(profileTableName);
                    kioskTableName.add("CfgDataToCollection");
                    kioskTableName.add("ConfigDataItem");
                    kioskTableName.add("Resource");
                    final Row appLockRow = dataObject.getRow("AppLockPolicyApps", appGroupCriteria);
                    final Long configDataItem = (appLockRow != null) ? ((Long)appLockRow.get("CONFIG_DATA_ITEM_ID")) : null;
                    if (configDataItem != null) {
                        final Criteria singleAppCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)"1", 0);
                        final Criteria configCriteria = new Criteria(new Column("AppLockPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItem, 0);
                        final Row kioskRow = dataObject.getRow("AppLockPolicy", singleAppCriteria.and(configCriteria));
                        if (kioskRow == null) {
                            continue;
                        }
                        final DataObject kioskDataObject = dataObject.getDataObject(kioskTableName, kioskRow);
                        final Iterator kioskIterator = kioskDataObject.getRows(profileTableName);
                        if (!kioskIterator.hasNext()) {
                            continue;
                        }
                        kioskCollectionList.add(collectionId);
                        final JSONObject appCollectionJSON = new JSONObject();
                        appCollectionJSON.put("CollectionID", (Object)collectionId);
                        appCollectionJSON.put("AppName", (Object)appName);
                        appArray.put((Object)appCollectionJSON);
                    }
                }
            }
        }
        if (appArray.length() != 0) {
            object.put("AppColln", (Object)appArray);
            object.put("Status", (Object)"Failed");
        }
        object.put("FailedList", (Object)kioskCollectionList);
        return object;
    }
}
