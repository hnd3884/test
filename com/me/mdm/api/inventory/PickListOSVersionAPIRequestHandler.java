package com.me.mdm.api.inventory;

import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import org.json.JSONArray;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import com.me.mdm.http.HttpException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class PickListOSVersionAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.getOSVersionPickList(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doGet()    >   Exception   ", (Throwable)e);
            throw new HttpException(400, null);
        }
    }
    
    private JSONObject getOSVersionPickList(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Join managedDeviceJoin = new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(resourceJoin);
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            Criteria criteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            criteria = criteria.and(selectQuery.getCriteria()).and(managedCriteria);
            selectQuery.setCriteria(criteria);
            final Column osVersionColumn = new Column("MdDeviceInfo", "OS_VERSION");
            osVersionColumn.setColumnAlias("OS_VERSION");
            final Column platformTypeColumn = new Column("ManagedDevice", "PLATFORM_TYPE");
            platformTypeColumn.setColumnAlias("PLATFORM_TYPE");
            selectQuery.addSelectColumn(osVersionColumn);
            selectQuery.addSelectColumn(platformTypeColumn);
            selectQuery.setDistinct(true);
            final JSONObject responseJSON = new JSONObject();
            final ArrayList groupByList = new ArrayList();
            groupByList.add(platformTypeColumn);
            groupByList.add(osVersionColumn);
            final GroupByClause groupByClause = new GroupByClause((List)groupByList);
            selectQuery.setGroupByClause(groupByClause);
            final JSONArray resultJSONArray = JSONUtil.getInstance().convertSimpleJSONarToJSONar(MDMUtil.executeSelectQuery(selectQuery));
            final HashSet iosPlatForm = new HashSet();
            final HashSet androidPlatform = new HashSet();
            final HashSet chromePlatform = new HashSet();
            final HashSet windowsPlatform = new HashSet();
            for (int i = 0; i < resultJSONArray.length(); ++i) {
                final JSONObject tempJSON = resultJSONArray.getJSONObject(i);
                final int platformType = tempJSON.getInt("PLATFORM_TYPE");
                String osVersion = String.valueOf(tempJSON.get("OS_VERSION"));
                if (osVersion.contains(".")) {
                    osVersion = osVersion.split("\\.")[0] + ".*";
                }
                else {
                    osVersion += "*";
                }
                switch (platformType) {
                    case 1: {
                        iosPlatForm.add(osVersion);
                        break;
                    }
                    case 2: {
                        androidPlatform.add(osVersion);
                        break;
                    }
                    case 3: {
                        windowsPlatform.add(osVersion);
                        break;
                    }
                    case 4: {
                        chromePlatform.add(osVersion);
                        break;
                    }
                }
            }
            responseJSON.put("android", (Object)new JSONArray((Collection)androidPlatform));
            responseJSON.put("ios", (Object)new JSONArray((Collection)iosPlatForm));
            responseJSON.put("windows", (Object)new JSONArray((Collection)windowsPlatform));
            responseJSON.put("chrome", (Object)new JSONArray((Collection)chromePlatform));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- getOSVersionPickList()    >   Exception   ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
