package com.me.mdm.server.apps.actionvalidator;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class ValidateAppDataHandler
{
    public JSONObject addSuccessCollection(final JSONObject object, final DataObject dataObject) throws Exception {
        final JSONArray appArray = new JSONArray();
        final JSONObject responseObject = new JSONObject();
        final List collectionList = Arrays.asList((Long[])object.opt("collectionList"));
        for (final Object collectionId : collectionList) {
            final List tableList = new ArrayList();
            tableList.add("MdAppToCollection");
            tableList.add("MdAppToGroupRel");
            tableList.add("MdAppGroupDetails");
            final Criteria appCollectionCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), collectionId, 0);
            final Row appCollectionRow = dataObject.getRow("MdAppToCollection", appCollectionCriteria);
            if (appCollectionRow != null) {
                final DataObject appDataObject = dataObject.getDataObject(tableList, appCollectionRow);
                final Iterator appIterator = appDataObject.getRows("MdAppGroupDetails");
                while (appIterator.hasNext()) {
                    final Row appRow = appIterator.next();
                    final String appName = (String)appRow.get("GROUP_DISPLAY_NAME");
                    final JSONObject appCollectionJSON = new JSONObject();
                    appCollectionJSON.put("CollectionID", (Object)collectionId);
                    appCollectionJSON.put("AppName", (Object)appName);
                    appArray.put((Object)appCollectionJSON);
                }
            }
        }
        if (appArray.length() != 0) {
            responseObject.put("AppColln", (Object)appArray);
            responseObject.put("Status", (Object)"Acknowledge");
        }
        return responseObject;
    }
    
    public SelectQuery getBaseQuery(final JSONObject object) {
        final Long[] collectionList = (Long[])object.opt("collectionList");
        final Long[] resourceList = (Long[])object.opt("resourceList");
        final String collectionTableName = object.optString("ProfileTable");
        final String resourceTableName = object.optString("ResourceTable");
        final boolean isGroup = object.optBoolean("isGroup");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", resourceTableName, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        String collectionTableColumn = "RESOURCE_ID";
        if (isGroup) {
            collectionTableColumn = "GROUP_ID";
        }
        selectQuery.addJoin(new Join(resourceTableName, collectionTableName, new String[] { "RESOURCE_ID" }, new String[] { collectionTableColumn }, 2));
        selectQuery.addJoin(new Join(resourceTableName, "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria criteria = null;
        final Criteria collectionCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionList, 8);
        final Criteria resourceForCollectionCriteria = new Criteria(new Column(resourceTableName, "RESOURCE_ID"), (Object)resourceList, 8);
        final Criteria disassociatedProfile = new Criteria(new Column(collectionTableName, "MARKED_FOR_DELETE"), (Object)false, 0);
        criteria = collectionCriteria.and(disassociatedProfile);
        if (resourceList != null) {
            criteria = criteria.and(resourceForCollectionCriteria);
        }
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        return selectQuery;
    }
    
    public JSONObject validateDataForRequest(final JSONObject data) throws JSONException {
        final String collectionIds = data.optString("collectionIds");
        final String resourceIds = data.optString("resourceIds");
        final boolean isGroup = data.optBoolean("isGroup");
        final boolean isAPI = data.optBoolean("isAPI");
        Long[] resourceId;
        Long[] collectionId;
        if (isAPI) {
            final JSONArray resourceIdsArray = data.optJSONArray("resourceIds");
            final List<Long> resourceIdsList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIdsArray);
            resourceId = resourceIdsList.toArray(new Long[resourceIdsList.size()]);
            final JSONArray collectionIdsArray = data.optJSONArray("collectionIds");
            final List<Long> collectionIdsList = JSONUtil.getInstance().convertLongJSONArrayTOList(collectionIdsArray);
            collectionId = collectionIdsList.toArray(new Long[collectionIdsList.size()]);
        }
        else {
            collectionId = MDMUtil.getInstance().decodeMDMMemberIds(collectionIds);
            resourceId = MDMUtil.getInstance().decodeMDMMemberIds(resourceIds);
        }
        if (collectionId != null) {
            final JSONObject requestObject = new JSONObject();
            requestObject.put("collectionList", (Object)collectionId);
            requestObject.put("resourceList", (Object)resourceId);
            requestObject.put("isGroup", isGroup);
            String collectionTableName = "RecentProfileForResource";
            String resourceTableName = "MdAppCatalogToResource";
            if (isGroup) {
                collectionTableName = "RecentProfileForGroup";
                resourceTableName = "MdAppCatalogToGroup";
            }
            requestObject.put("ProfileTable", (Object)collectionTableName);
            requestObject.put("ResourceTable", (Object)resourceTableName);
            return requestObject;
        }
        return null;
    }
}
