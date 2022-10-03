package com.me.mdm.server.webclips;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadMapping;
import java.util.List;

public class WebClipsPolicyHandler
{
    private List<ProfilePayloadMapping> profilePayloadMappings;
    private HashMap unConfigureMap;
    
    public WebClipsPolicyHandler() {
        this.profilePayloadMappings = new ArrayList<ProfilePayloadMapping>();
        this.unConfigureMap = new HashMap();
        this.profilePayloadMappings.add(new WebClipsMapping("WebClipToConfigRel", "WEBCLIP_POLICY_ID", true, true));
        final Table folderPage = new Table("ScreenLayoutPageDetails", "FolderPage");
        final Table folderPageToPageLayout = new Table("ScreenPageToPageLayout", "FolderPageToPageRel");
        final Table folderPageLayout = new Table("ScreenPageLayout", "FolderScreenPageLayout");
        final Table folderPageLayoutWebClip = new Table("ScreenPageLayoutToWebClipRel", "FolderPageLayoutWebclip");
        final List<Join> homeScreenJoin = new ArrayList<Join>();
        homeScreenJoin.add(new Join("ConfigDataItem", "ScreenLayoutSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenLayoutSettings", "ScreenLayout", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenPageLayout", "ScreenPageLayoutToWebClipRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenPageLayout", "ScreenPageLayoutToFolderRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join("ScreenPageLayoutToFolderRel", "ScreenPageLayoutFolderToPageRel", new String[] { "FOLDER_ID" }, new String[] { "FOLDER_ID" }, 1));
        homeScreenJoin.add(new Join(new Table("ScreenPageLayoutFolderToPageRel"), folderPage, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        homeScreenJoin.add(new Join(folderPage, folderPageToPageLayout, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        homeScreenJoin.add(new Join(folderPageToPageLayout, folderPageLayout, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        homeScreenJoin.add(new Join(folderPageLayout, folderPageLayoutWebClip, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        final HashMap criteriaColumns = new HashMap();
        criteriaColumns.put("ScreenPageLayoutToWebClipRel", "WEBCLIP_POLICY_ID");
        criteriaColumns.put("FolderPageLayoutWebclip", "WEBCLIP_POLICY_ID");
        this.profilePayloadMappings.add(new WebClipsMapping(homeScreenJoin, "ScreenLayoutSettings", criteriaColumns, true, "ScreenLayoutSettings", "SCREEN_LAYOUT_ID", true));
        final List webClipPolicyTable = new ArrayList();
        webClipPolicyTable.add(182);
        webClipPolicyTable.add(560);
        this.unConfigureMap.put("WebClipToConfigRel", webClipPolicyTable);
    }
    
    public List<ProfilePayloadMapping> getProfilePayloadMappings() {
        return this.profilePayloadMappings;
    }
    
    public HashMap getUnConfigureMap() {
        return this.unConfigureMap;
    }
    
    public void handleModifyWebClips(final List reDistributeList, final List webClipPolicyIds, final Long customerId, final Long userId) throws Exception {
        new ProfilePayloadOperator(this.profilePayloadMappings, this.unConfigureMap).rePublishPayloadProfiles(webClipPolicyIds, customerId, userId, reDistributeList, false);
    }
    
    public void handleDeleteWebClips(final List webClipPolicyIds, final Long customerId, final Long userId) throws Exception {
        new ProfilePayloadOperator(this.profilePayloadMappings, this.unConfigureMap).performPayloadOperation(webClipPolicyIds, customerId, userId, -1L, true, true);
    }
    
    public JSONObject isWebClipInProfile(final List<Long> webClipPolicyIds) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery configQuery = new ProfilePayloadOperator(this.profilePayloadMappings, this.unConfigureMap).getCertConfigSelectQuery(webClipPolicyIds);
            final SelectQuery deliveredColumnConfigQuery = (SelectQuery)configQuery.clone();
            configQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            configQuery.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final SelectQuery columnQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            final Column column = new Column("ProfileToCollection", "COLLECTION_ID").distinct();
            columnQuery.addSelectColumn(column);
            final Criteria recentCriteria = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileToColln", "COLLECTION_ID"), (Object)null, 1)));
            columnQuery.setCriteria(recentCriteria);
            final DerivedColumn profileCollectionColumn = new DerivedColumn("profileCollection", columnQuery);
            final DerivedColumn configDataColumn = new DerivedColumn("configColumn", deliveredColumnConfigQuery);
            final Criteria profileCollectionCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)profileCollectionColumn, 8);
            final Criteria configDataCriteria = new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataColumn, 8);
            configQuery.setCriteria(profileCollectionCriteria.and(configDataCriteria));
            final List<Column> removalColumns = configQuery.getSelectColumns();
            for (final Column removeColumn : removalColumns) {
                configQuery.removeSelectColumn(removeColumn);
            }
            int columnName = 0;
            for (final ProfilePayloadMapping mapping : this.profilePayloadMappings) {
                final List<Column> columns = mapping.getColumns();
                for (final Column column2 : columns) {
                    column2.setColumnAlias(String.valueOf(columnName));
                    ++columnName;
                    configQuery.addSelectColumn(column2);
                }
            }
            configQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)configQuery);
            while (dataSetWrapper.next()) {
                final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
                for (int i = 0; i < columnName; ++i) {
                    final Long webClipPolicyId = (Long)dataSetWrapper.getValue(String.valueOf(i));
                    if (webClipPolicyId != null) {
                        if (jsonObject.has(String.valueOf(webClipPolicyId))) {
                            final JSONArray profileIds = jsonObject.getJSONArray(String.valueOf(webClipPolicyId));
                            profileIds.put((Object)profileId);
                        }
                        else {
                            final JSONArray profileIds = new JSONArray();
                            profileIds.put((Object)profileId);
                            jsonObject.put(String.valueOf(webClipPolicyId), (Object)profileIds);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in get profiles", e);
        }
        return jsonObject;
    }
    
    public JSONArray getProfilesForWebClipID(final Long webclipPolicyId) {
        final List<Long> webClipList = new ArrayList<Long>();
        webClipList.add(webclipPolicyId);
        final JSONObject inProfileIds = this.isWebClipInProfile(webClipList);
        if (inProfileIds.has(String.valueOf(webclipPolicyId))) {
            return inProfileIds.getJSONArray(String.valueOf(webclipPolicyId));
        }
        return new JSONArray();
    }
    
    public void deleteWebClip(final List<Long> webClipIds) throws Exception {
        for (final Long webClipId : webClipIds) {
            final String webClipPath = ProfileUtil.getProfileWebClipsFolderPath(webClipId);
            if (ApiFactoryProvider.getFileAccessAPI().deleteDirectory(webClipPath)) {
                Logger.getLogger("MDMConfigLogger").log(Level.INFO, "Deleted webclip for {0}", new Object[] { webClipId });
            }
        }
    }
}
