package com.adventnet.sym.server.mdm.config;

import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.tree.MDMTreeFilterHandler;

public class MDMProfileAppFilterHandler extends MDMTreeFilterHandler
{
    private static MDMProfileAppFilterHandler mdmProfileAppFilter;
    public Logger logger;
    
    public MDMProfileAppFilterHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static MDMProfileAppFilterHandler getInstance() {
        if (MDMProfileAppFilterHandler.mdmProfileAppFilter == null) {
            MDMProfileAppFilterHandler.mdmProfileAppFilter = new MDMProfileAppFilterHandler();
        }
        return MDMProfileAppFilterHandler.mdmProfileAppFilter;
    }
    
    public JSONObject getCategoryFilterGroup(final int platformType) {
        final JSONObject appCategoryFilterGroup = new JSONObject();
        appCategoryFilterGroup.put((Object)"FILTER_TYPE", (Object)6);
        appCategoryFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.inv.cat.CATEGORY");
        final JSONArray appCategoryMemberArr = this.getCategoryFilterMemberArray(platformType);
        appCategoryFilterGroup.put((Object)"FILTER_MEMBERS", (Object)appCategoryMemberArr);
        return appCategoryFilterGroup;
    }
    
    public JSONObject getAppTypeFilterGroup(final int platformType) {
        final JSONObject appTypeFilterGroup = new JSONObject();
        appTypeFilterGroup.put((Object)"FILTER_TYPE", (Object)8);
        appTypeFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.mdm.actionlog.appmgmt.appType");
        final JSONArray appTypeMemberArr = this.getAppTypeMemberArray(platformType);
        appTypeFilterGroup.put((Object)"FILTER_MEMBERS", (Object)appTypeMemberArr);
        return appTypeFilterGroup;
    }
    
    public JSONObject getAppLicenseTypeFilterGroup() {
        final JSONObject appLicenseTypeFilterGroup = new JSONObject();
        appLicenseTypeFilterGroup.put((Object)"FILTER_TYPE", (Object)7);
        appLicenseTypeFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.common.LICENSE_TYPE");
        final JSONArray appLicenseTypeMemberArr = new JSONArray();
        JSONObject appLicenseTypeMemberObj = new JSONObject();
        appLicenseTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)701);
        appLicenseTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.free_apps");
        appLicenseTypeMemberArr.add((Object)appLicenseTypeMemberObj);
        appLicenseTypeMemberObj = new JSONObject();
        appLicenseTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)702);
        appLicenseTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.paid_apps");
        appLicenseTypeMemberArr.add((Object)appLicenseTypeMemberObj);
        appLicenseTypeFilterGroup.put((Object)"FILTER_MEMBERS", (Object)appLicenseTypeMemberArr);
        return appLicenseTypeFilterGroup;
    }
    
    public JSONArray getAppTypeMemberArray(final int platformType) {
        JSONArray appTypeMemberArr = null;
        JSONObject appTypeMemberObj = null;
        try {
            appTypeMemberArr = new JSONArray();
            switch (platformType) {
                case 0: {
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)2000);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.store_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)2001);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.enterprise_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    break;
                }
                case 1: {
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)1);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.store_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)2);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.enterprise_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    break;
                }
                case 2: {
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)3);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.store_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)4);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.enterprise_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    break;
                }
                case 3: {
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)5);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.store_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)6);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.app.enterprise_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    appTypeMemberObj = new JSONObject();
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)7);
                    appTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"mdm.appmgmt.windows.msi_apps");
                    appTypeMemberArr.add((Object)appTypeMemberObj);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppTypeMemberArray {0}", ex);
        }
        return appTypeMemberArr;
    }
    
    public JSONArray getCategoryFilterMemberArray(final int platformType) {
        JSONArray appCategoryMemberArr = null;
        try {
            final SelectQuery categoryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCategory"));
            final Join groupCategoryJoin = new Join("AppCategory", "MdAppGroupCategoryRel", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2);
            categoryQuery.addJoin(groupCategoryJoin);
            categoryQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_ID"));
            categoryQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_LABEL"));
            final Criteria platformTypeCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platformType, 0);
            categoryQuery.setCriteria(platformTypeCri);
            final DataObject appCategoryDObj = MDMUtil.getPersistence().get(categoryQuery);
            final Iterator appCategoryIterator = appCategoryDObj.getRows("AppCategory");
            JSONObject appCategoryMemberObj = null;
            Row appCategoryRow = null;
            appCategoryMemberArr = new JSONArray();
            while (appCategoryIterator.hasNext()) {
                appCategoryRow = appCategoryIterator.next();
                appCategoryMemberObj = new JSONObject();
                appCategoryMemberObj.put((Object)"FILTER_MEMBER_ID", appCategoryRow.get("APP_CATEGORY_ID"));
                appCategoryMemberObj.put((Object)"FILTER_MEMBER_NAME", appCategoryRow.get("APP_CATEGORY_LABEL"));
                appCategoryMemberArr.add((Object)appCategoryMemberObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getCategoryFilterMemberArray {0}", ex);
        }
        return appCategoryMemberArr;
    }
    
    public Criteria getFilterCriteria(final JSONArray filterTreeJSON) {
        final Iterator filterItr = filterTreeJSON.iterator();
        JSONObject filterJSON = null;
        Criteria filterCri = null;
        int filterType = -1;
        long filterMemberId = -1L;
        Criteria categoryCri = null;
        Criteria licenseTypeCri = null;
        Criteria appTypeCri = null;
        Criteria platformTypeCri = null;
        while (filterItr.hasNext()) {
            filterJSON = filterItr.next();
            filterType = Integer.parseInt((String)filterJSON.get((Object)"FILTER_TYPE"));
            filterMemberId = Long.parseLong((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
            switch (filterType) {
                case 6: {
                    final Criteria categoryNewCri = this.getCategoryCriteria(filterMemberId);
                    categoryCri = ((categoryCri == null) ? categoryNewCri : categoryCri.or(categoryNewCri));
                    continue;
                }
                case 7: {
                    final Criteria licenseTypeNewCri = this.getLicenseTypeCriteria(filterMemberId);
                    licenseTypeCri = ((licenseTypeCri == null) ? licenseTypeNewCri : licenseTypeCri.or(licenseTypeNewCri));
                    continue;
                }
                case 8: {
                    final Criteria appTypeNewCri = this.getappTypeCriteria(filterMemberId);
                    appTypeCri = ((appTypeCri == null) ? appTypeNewCri : appTypeCri.or(appTypeNewCri));
                    continue;
                }
                case 1: {
                    final Criteria appTypeNewCri = this.getPlatformTypeCriteria(filterMemberId);
                    platformTypeCri = ((platformTypeCri == null) ? appTypeNewCri : platformTypeCri.or(appTypeNewCri));
                    continue;
                }
            }
        }
        filterCri = ((filterCri == null) ? categoryCri : filterCri.and(categoryCri));
        filterCri = ((filterCri == null) ? licenseTypeCri : filterCri.and(licenseTypeCri));
        filterCri = ((filterCri == null) ? appTypeCri : filterCri.and(appTypeCri));
        filterCri = ((filterCri == null) ? platformTypeCri : filterCri.and(platformTypeCri));
        return filterCri;
    }
    
    public Criteria getProfileFilterCriteria(final JSONArray filterTreeJSON) {
        final Iterator filterItr = filterTreeJSON.iterator();
        JSONObject filterJSON = null;
        Criteria filterCri = null;
        int filterType = -1;
        long filterMemberId = -1L;
        Criteria platformTypeCri = null;
        while (filterItr.hasNext()) {
            filterJSON = filterItr.next();
            filterType = Integer.parseInt((String)filterJSON.get((Object)"FILTER_TYPE"));
            filterMemberId = Long.parseLong((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
            switch (filterType) {
                case 1: {
                    final Criteria appTypeNewCri = this.getPlatformTypeCriteria(filterMemberId);
                    platformTypeCri = ((platformTypeCri == null) ? appTypeNewCri : platformTypeCri.or(appTypeNewCri));
                    continue;
                }
            }
        }
        filterCri = ((filterCri == null) ? platformTypeCri : filterCri.and(platformTypeCri));
        return filterCri;
    }
    
    private Criteria getCategoryCriteria(final long categoryId) {
        final Criteria modelTypeCri = new Criteria(Column.getColumn("MdAppGroupCategoryRel", "APP_CATEGORY_ID"), (Object)categoryId, 0);
        return modelTypeCri;
    }
    
    private Criteria getPlatformTypeCriteria(final long categoryId) {
        final Criteria modelTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)categoryId, 0);
        return modelTypeCri;
    }
    
    private Criteria getLicenseTypeCriteria(final long licenseType) {
        final boolean isPaidApp = licenseType == 702L;
        final Criteria licenseTypeCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)isPaidApp, 0);
        return licenseTypeCri;
    }
    
    private Criteria getappTypeCriteria(final long appType) {
        final Criteria appTypeCri = MDMUtil.getInstance().getPackageTypeCriteria(appType);
        return appTypeCri;
    }
    
    public JSONObject getProfilePlatformFilterGroup(final int profileType) {
        final JSONObject deviceFilterGroup = new JSONObject();
        deviceFilterGroup.put((Object)"FILTER_TYPE", (Object)1);
        deviceFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.mdm.device_mgmt.platform");
        final JSONArray modelMemberArray = this.getPlatformFilterMemberArray(profileType);
        deviceFilterGroup.put((Object)"FILTER_MEMBERS", (Object)modelMemberArray);
        return deviceFilterGroup;
    }
    
    private JSONArray getPlatformFilterMemberArray(final int profileType) {
        final JSONArray modelMemberArray = new JSONArray();
        try {
            final SelectQuery platformTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            platformTypeQuery.setCriteria(new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)profileType, 0));
            platformTypeQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            platformTypeQuery.setDistinct(true);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)platformTypeQuery);
            while (ds.next()) {
                final JSONObject modelMemberObj = new JSONObject();
                final int platformType = (int)ds.getValue("PLATFORM_TYPE");
                modelMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)platformType);
                modelMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)MDMUtil.getInstance().getPlatformName(platformType));
                modelMemberArray.add((Object)modelMemberObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occoured in getPlatformTypeMap....", ex);
        }
        return modelMemberArray;
    }
    
    public JSONObject getDocTagTypeFilterGroup(final long customerID) throws Exception {
        final JSONObject appTypeFilterGroup = new JSONObject();
        appTypeFilterGroup.put((Object)"FILTER_TYPE", (Object)1);
        appTypeFilterGroup.put((Object)"FILTER_NAME", (Object)"mdm.doc.tag.type");
        final JSONArray appTypeMemberArr = this.getTagTypeMemberArray(customerID);
        appTypeFilterGroup.put((Object)"FILTER_MEMBERS", (Object)appTypeMemberArr);
        return appTypeFilterGroup;
    }
    
    public JSONObject getDocTypeFilterGroup() throws Exception {
        final JSONObject appTypeFilterGroup = new JSONObject();
        appTypeFilterGroup.put((Object)"FILTER_TYPE", (Object)2);
        appTypeFilterGroup.put((Object)"FILTER_NAME", (Object)"mdm.doc.type");
        final JSONArray appTypeMemberArr = this.getDocTypeMemberArray();
        appTypeFilterGroup.put((Object)"FILTER_MEMBERS", (Object)appTypeMemberArr);
        return appTypeFilterGroup;
    }
    
    public JSONArray getTagTypeMemberArray(final long customerID) throws Exception {
        JSONArray tagTypeMemberArr = null;
        JSONObject tagTypeMemberObj = null;
        tagTypeMemberArr = new JSONArray();
        final Iterator<Row> iterator = DBUtil.getRowsFromDB("DocumentTags", "CUSTOMER_ID", (Object)customerID);
        while (iterator != null && iterator.hasNext()) {
            final Row docTagRow = iterator.next();
            tagTypeMemberObj = new JSONObject();
            tagTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)docTagRow.get("TAG_ID"));
            tagTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)docTagRow.get("TAG_NAME"));
            tagTypeMemberArr.add((Object)tagTypeMemberObj);
        }
        return tagTypeMemberArr;
    }
    
    public JSONArray getDocTypeMemberArray() {
        final JSONArray docTypeMemberArr = new JSONArray();
        for (int i = 1; i < 28; ++i) {
            final String docExtn = DocMgmtDataHandler.getInstance().getDocExtention(i);
            final JSONObject docTypeMemberObj = new JSONObject();
            docTypeMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)i);
            docTypeMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)docExtn.substring(1).toUpperCase());
            docTypeMemberArr.add((Object)docTypeMemberObj);
        }
        return docTypeMemberArr;
    }
    
    static {
        MDMProfileAppFilterHandler.mdmProfileAppFilter = null;
    }
}
