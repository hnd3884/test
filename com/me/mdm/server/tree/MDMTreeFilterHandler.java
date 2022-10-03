package com.me.mdm.server.tree;

import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MDMTreeFilterHandler
{
    public JSONObject getPlatformFilterGroup() {
        final JSONObject platformFilterGroup = new JSONObject();
        platformFilterGroup.put((Object)"FILTER_TYPE", (Object)1);
        platformFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.mdm.device_mgmt.platform");
        final JSONArray modelMemberArray = this.getPlatformFilterMemberArray();
        platformFilterGroup.put((Object)"FILTER_MEMBERS", (Object)modelMemberArray);
        return platformFilterGroup;
    }
    
    private JSONArray getPlatformFilterMemberArray() {
        final JSONArray modelMemberArray = new JSONArray();
        final HashMap platformTypeListMap = MDMUtil.getInstance().getPlatformTypeMap();
        final Iterator iter = platformTypeListMap.entrySet().iterator();
        JSONObject modelMemberObj = null;
        while (iter.hasNext()) {
            final Map.Entry platformObj = iter.next();
            modelMemberObj = new JSONObject();
            modelMemberObj.put((Object)"FILTER_MEMBER_ID", platformObj.getKey());
            modelMemberObj.put((Object)"FILTER_MEMBER_NAME", platformObj.getValue());
            modelMemberArray.add((Object)modelMemberObj);
        }
        return modelMemberArray;
    }
    
    public JSONObject getModelFilterGroup() {
        final JSONObject modelFilterGroup = new JSONObject();
        modelFilterGroup.put((Object)"FILTER_TYPE", (Object)2);
        modelFilterGroup.put((Object)"FILTER_NAME", (Object)"mdm.inv.common.MODEL");
        final JSONArray modelMemberArray = this.getModelFilterMemberArray();
        modelFilterGroup.put((Object)"FILTER_MEMBERS", (Object)modelMemberArray);
        return modelFilterGroup;
    }
    
    private JSONArray getModelFilterMemberArray() {
        final JSONArray modelMemberArray = new JSONArray();
        final HashMap modelTypeListMap = MDMUtil.getInstance().getMDMDeviceTypeMap();
        final Iterator modelTypeItr = modelTypeListMap.entrySet().iterator();
        JSONObject modelMemberObj = null;
        while (modelTypeItr.hasNext()) {
            final Map.Entry modelTypeObj = modelTypeItr.next();
            modelMemberObj = new JSONObject();
            modelMemberObj.put((Object)"FILTER_MEMBER_ID", modelTypeObj.getKey());
            modelMemberObj.put((Object)"FILTER_MEMBER_NAME", modelTypeObj.getValue());
            modelMemberArray.add((Object)modelMemberObj);
        }
        return modelMemberArray;
    }
    
    public JSONObject getOSVersionByPlatform(final int platformType) {
        final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        return this.getOSVersionFilterGroup(platformCri);
    }
    
    public JSONObject getAllOSVersion() {
        return this.getOSVersionFilterGroup(null);
    }
    
    private JSONObject getOSVersionFilterGroup(final Criteria platformCri) {
        final JSONObject osVerFilterGroup = new JSONObject();
        osVerFilterGroup.put((Object)"FILTER_TYPE", (Object)3);
        osVerFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.common.OS_VERSION");
        final JSONArray osVerMemberArray = this.getOSVerFilterMemberArray(platformCri);
        osVerFilterGroup.put((Object)"FILTER_MEMBERS", (Object)osVerMemberArray);
        return osVerFilterGroup;
    }
    
    private JSONArray getOSVerFilterMemberArray(final Criteria platformCri) {
        final JSONArray osVerMemberArray = new JSONArray();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery osVerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Join managedDeviceJoin = new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            osVerQuery.addJoin(managedDeviceJoin);
            osVerQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            osVerQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            osVerQuery.setCriteria(platformCri);
            final GroupByColumn groupByOSCol = new GroupByColumn(new Column("MdDeviceInfo", "OS_VERSION"), true);
            final GroupByColumn groupByPlat = new GroupByColumn(new Column("ManagedDevice", "PLATFORM_TYPE"), true);
            final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
            groupByList.add(groupByOSCol);
            groupByList.add(groupByPlat);
            final GroupByClause groupByClause = new GroupByClause((List)groupByList);
            osVerQuery.setGroupByClause(groupByClause);
            ds = DMDataSetWrapper.executeQuery((Object)osVerQuery);
            JSONObject osVerObj = null;
            String osVersion = null;
            while (ds.next()) {
                osVersion = (String)ds.getValue("OS_VERSION");
                final int platformName = (int)ds.getValue("PLATFORM_TYPE");
                osVerObj = new JSONObject();
                osVerObj.put((Object)"FILTER_MEMBER_ID", (Object)osVersion);
                osVerObj.put((Object)"FILTER_MEMBER_NAME", (Object)osVersion);
                osVerObj.put((Object)"PLATFORM_NAME", (Object)platformName);
                osVerMemberArray.add((Object)osVerObj);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMTreeFilterHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return osVerMemberArray;
    }
    
    public JSONArray getOSVerRangeFilterMemberArray(final int platformType) {
        JSONArray osVerMemberArray = null;
        switch (platformType) {
            case 1: {
                osVerMemberArray = this.getiOSVerFilterMemberArray();
                break;
            }
            case 2: {
                osVerMemberArray = this.getAndroidVerFilterMemberArray();
                break;
            }
            case 3: {
                osVerMemberArray = this.getWindowsPhoneVerFilterMemberArray();
                break;
            }
        }
        return osVerMemberArray;
    }
    
    private JSONArray getiOSVerFilterMemberArray() {
        final JSONArray osVerMemberArray = new JSONArray();
        JSONObject osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1001);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_ios_less_8");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"1-4-7");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1002);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_ios_8");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"1-8.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1003);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_ios_9");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"1-9.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1004);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_ios_10");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"1-10.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        return osVerMemberArray;
    }
    
    private JSONArray getAndroidVerFilterMemberArray() {
        final JSONArray osVerMemberArray = new JSONArray();
        JSONObject osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1101);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_android_less_5");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"2-2-4");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1102);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_android_5");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"2-5.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1103);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_android_6");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"2-6.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1104);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_android_7");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"2-7.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        return osVerMemberArray;
    }
    
    private JSONArray getWindowsPhoneVerFilterMemberArray() {
        final JSONArray osVerMemberArray = new JSONArray();
        JSONObject osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1201);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_windows_less_10");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"3-7-9");
        osVerMemberArray.add((Object)osVerMemberObj);
        osVerMemberObj = new JSONObject();
        osVerMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1202);
        osVerMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.ver_windows_10");
        osVerMemberObj.put((Object)"FILTER_MEMBER_CRITERIA", (Object)"3-10.*");
        osVerMemberArray.add((Object)osVerMemberObj);
        return osVerMemberArray;
    }
    
    private JSONObject getOSVerCriteriaMap() {
        final JSONObject osVerCriteriaMap = new JSONObject();
        osVerCriteriaMap.put((Object)1001, (Object)"1-4-7");
        osVerCriteriaMap.put((Object)1002, (Object)"1-8.*");
        osVerCriteriaMap.put((Object)1003, (Object)"1-9.*");
        osVerCriteriaMap.put((Object)1004, (Object)"1-10.*");
        osVerCriteriaMap.put((Object)1101, (Object)"2-2-4");
        osVerCriteriaMap.put((Object)1102, (Object)"2-5.*");
        osVerCriteriaMap.put((Object)1103, (Object)"2-6.*");
        osVerCriteriaMap.put((Object)1104, (Object)"2-7.*");
        osVerCriteriaMap.put((Object)1201, (Object)"3-7-9");
        osVerCriteriaMap.put((Object)1202, (Object)"3-10.*");
        return osVerCriteriaMap;
    }
}
