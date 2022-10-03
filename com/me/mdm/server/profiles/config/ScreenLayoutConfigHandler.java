package com.me.mdm.server.profiles.config;

import java.util.List;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutTableObject;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class ScreenLayoutConfigHandler extends DefaultConfigHandler
{
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final long configDataItemId = configJSON.getLong("payload_id");
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            for (int index = 0; index < templateConfigProperties.length(); ++index) {
                final JSONObject templateConfigJSON = templateConfigProperties.getJSONObject(index);
                final String name = templateConfigJSON.getString("name");
                if (name.equals("ScreenLayout")) {
                    final JSONObject screenLayoutJSON = new JSONObject();
                    configJSON.put(this.getSubConfigProperties(templateConfigProperties, name).getString("alias"), (Object)screenLayoutJSON);
                    final JSONArray propertyArray = templateConfigJSON.getJSONArray("properties");
                    this.getScreenLayoutConfigJSON(screenLayoutJSON, propertyArray, dataObject, configDataItemId);
                }
                else if (name.equals("ScreenLayoutSettings")) {
                    final JSONObject screenLayoutSettingJSON = new JSONObject();
                    configJSON.put(this.getSubConfigProperties(templateConfigProperties, name).getString("alias"), (Object)screenLayoutSettingJSON);
                    final JSONArray propertyArray = templateConfigJSON.getJSONArray("properties");
                    this.getScreenLayoutSettingJSON(screenLayoutSettingJSON, propertyArray, dataObject, configDataItemId);
                }
            }
        }
    }
    
    private void getScreenLayoutConfigJSON(final JSONObject screenLayoutJSON, final JSONArray templateConfigArray, final DataObject dataObject, final Long configDataItemId) throws Exception {
        final JSONObject layoutNamePropertyJSON = this.getSubConfigProperties(templateConfigArray, "LAYOUT_NAME");
        final JSONObject pagePropertyJSON = this.getSubConfigProperties(templateConfigArray, "ScreenLayoutPageDetails");
        final Criteria configDataItemCriteria = new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        final Row screenLayoutRow = dataObject.getRow("ScreenLayout", configDataItemCriteria);
        if (screenLayoutRow != null) {
            final Long screenLayoutId = (Long)screenLayoutRow.get("SCREEN_LAYOUT_ID");
            final Criteria screenLayoutCriteria = new Criteria(new Column("ScreenLayoutToPageRelation", "SCREEN_LAYOUT_ID"), (Object)screenLayoutId, 0);
            final Iterator screenLayoutPages = dataObject.getRows("ScreenLayoutPageDetails", screenLayoutCriteria);
            final ScreenLayoutTableObject screenLayoutTableObject = ScreenLayoutHandler.getScreenLayoutTableObject();
            this.getPageDetails(screenLayoutTableObject, screenLayoutPages, screenLayoutJSON, dataObject, pagePropertyJSON);
        }
    }
    
    private void getPageDetails(final ScreenLayoutTableObject tableObject, final Iterator screenLayoutPages, final JSONObject layoutJSON, final DataObject dataObject, final JSONObject templateConfigJSON) throws Exception {
        final JSONArray pagePropertyArray = templateConfigJSON.getJSONArray("properties");
        final JSONObject screenPageLayoutJSONObject = this.getSubConfigProperties(pagePropertyArray, "ScreenPageLayout");
        final JSONArray pageObjectArray = new JSONArray();
        layoutJSON.put(templateConfigJSON.getString("alias"), (Object)pageObjectArray);
        while (screenLayoutPages.hasNext()) {
            final JSONObject pageObject = new JSONObject();
            pageObjectArray.put((Object)pageObject);
            final Row pageRow = screenLayoutPages.next();
            final Long pageId = (Long)pageRow.get("PAGE_ID");
            pageObject.put(this.getSubConfigProperties(pagePropertyArray, "PAGE_NO").getString("alias"), pageRow.get("PAGE_NO"));
            pageObject.put(this.getSubConfigProperties(pagePropertyArray, "PAGE_TYPE").getString("alias"), pageRow.get("PAGE_TYPE"));
            this.getScreenPageLayout(pageId, dataObject, screenPageLayoutJSONObject, pageObject, tableObject);
        }
    }
    
    protected void getScreenPageLayout(final Long pageId, final DataObject dataObject, final JSONObject screenPageLayoutTemplateJSONObject, final JSONObject pageObject, final ScreenLayoutTableObject tableObject) throws Exception {
        final Criteria pageCriteria = new Criteria(new Column(tableObject.getScreenPageToPageLayoutTableName(), "PAGE_ID"), (Object)pageId, 0);
        final Iterator pageLayoutIterator = dataObject.getRows(tableObject.getScreenPageLayoutTableName(), pageCriteria);
        final JSONArray screenPageLayoutArray = new JSONArray();
        pageObject.put(screenPageLayoutTemplateJSONObject.getString("alias"), (Object)screenPageLayoutArray);
        final JSONArray screenPageLayoutTemplateArray = screenPageLayoutTemplateJSONObject.getJSONArray("properties");
        while (pageLayoutIterator.hasNext()) {
            final JSONObject screenPageLayoutObject = new JSONObject();
            screenPageLayoutArray.put((Object)screenPageLayoutObject);
            final Row screenPageLayoutRow = pageLayoutIterator.next();
            final Long pageLayoutId = (Long)screenPageLayoutRow.get("PAGE_LAYOUT_ID");
            final Integer payloadType = (Integer)screenPageLayoutRow.get("PAGE_LAYOUT_TYPE");
            screenPageLayoutObject.put(this.getSubConfigProperties(screenPageLayoutTemplateArray, "PAGE_LAYOUT_ROW").getString("alias"), screenPageLayoutRow.get("PAGE_LAYOUT_ROW"));
            screenPageLayoutObject.put(this.getSubConfigProperties(screenPageLayoutTemplateArray, "PAGE_LAYOUT_COLUMN").getString("alias"), screenPageLayoutRow.get("PAGE_LAYOUT_COLUMN"));
            screenPageLayoutObject.put(this.getSubConfigProperties(screenPageLayoutTemplateArray, "PAGE_LAYOUT_TYPE").getString("alias"), (Object)payloadType);
            switch (payloadType) {
                case 1: {
                    this.getAppDetails(dataObject, pageLayoutId, screenPageLayoutTemplateArray, screenPageLayoutObject, tableObject);
                    continue;
                }
                case 3: {
                    this.getFolderDetails(dataObject, pageLayoutId, screenPageLayoutTemplateArray, screenPageLayoutObject);
                    continue;
                }
                case 2: {
                    this.getWebClipDetails(dataObject, pageLayoutId, screenPageLayoutTemplateArray, screenPageLayoutObject, tableObject);
                    continue;
                }
            }
        }
    }
    
    protected void getAppDetails(final DataObject dataObject, final Long pageLayoutId, final JSONArray templateConfig, final JSONObject screenPageObject, final ScreenLayoutTableObject tableObject) throws Exception {
        final Criteria pageLayoutCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutToAppTableName(), "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        final Row appRow = dataObject.getRow(tableObject.getScreenPageLayoutToAppTableName(), pageLayoutCriteria);
        final Long appGroupId = (Long)appRow.get("APP_GROUP_ID");
        final Criteria appGroupCriteria = new Criteria(new Column(tableObject.getAppGroupTableName(), "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Row appGroupRow = dataObject.getRow(tableObject.getAppGroupTableName(), appGroupCriteria);
        final String appName = (String)appGroupRow.get("GROUP_DISPLAY_NAME");
        final String bundleIdentifier = (String)appGroupRow.get("IDENTIFIER");
        final Row packageAppDataRow = dataObject.getRow(tableObject.getPackageToAppData(), appGroupRow);
        if (packageAppDataRow != null) {
            Object displayLocalImage = packageAppDataRow.get("DISPLAY_IMAGE_LOC");
            if (!MDMStringUtils.isEmpty(String.valueOf(displayLocalImage))) {
                displayLocalImage = this.constructFileUrl(displayLocalImage);
                screenPageObject.put(this.getSubConfigProperties(templateConfig, "DISPLAY_IMAGE_LOC").getString("alias"), displayLocalImage);
            }
        }
        screenPageObject.put(this.getSubConfigProperties(templateConfig, "APP_GROUP_ID").getString("alias"), (Object)appGroupId);
        screenPageObject.put(this.getSubConfigProperties(templateConfig, "GROUP_DISPLAY_NAME").getString("alias"), (Object)appName);
        screenPageObject.put("identifier", (Object)bundleIdentifier);
    }
    
    protected void getWebClipDetails(final DataObject dataObject, final Long pageLayoutId, final JSONArray templateConfig, final JSONObject screenPageObject, final ScreenLayoutTableObject tableObject) throws Exception {
        final Criteria pageLayoutCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutToWebClipTableName(), "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        final Row webclipRowRel = dataObject.getRow(tableObject.getScreenPageLayoutToWebClipTableName(), pageLayoutCriteria);
        final Long webclipPolicyId = (Long)webclipRowRel.get("WEBCLIP_POLICY_ID");
        final Criteria webclipPolicyCriteria = new Criteria(new Column(tableObject.getWebClipPolicyTableName(), "WEBCLIP_POLICY_ID"), (Object)webclipPolicyId, 0);
        final Row webClipRow = dataObject.getRow(tableObject.getWebClipPolicyTableName(), webclipPolicyCriteria);
        final String iconFileName = (String)webClipRow.get("ICON_FILE_NAME");
        if (!MDMStringUtils.isEmpty(iconFileName)) {
            webClipRow.set("ICON_FILE_NAME", new WebClipsFacade().getWebClipUrl(iconFileName));
        }
        this.addAliasForRowInConfigJSON(webClipRow, templateConfig, screenPageObject);
    }
    
    protected void getFolderDetails(final DataObject dataObject, final Long pageLayoutId, final JSONArray templateConfig, final JSONObject screenPageObject) throws Exception {
        final Criteria pageLayoutCriteria = new Criteria(new Column("ScreenPageLayoutToFolderRel", "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        final Row folderRow = dataObject.getRow("ScreenPageLayoutToFolderRel", pageLayoutCriteria);
        screenPageObject.put(this.getSubConfigProperties(templateConfig, "FOLDER_NAME").getString("alias"), folderRow.get("FOLDER_NAME"));
        final Long folderId = (Long)folderRow.get("FOLDER_ID");
        final Criteria folderCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "FOLDER_ID"), (Object)folderId, 0);
        final Iterator pageLayoutIterator = dataObject.getRows("FolderPage", folderCriteria);
        final ScreenLayoutTableObject tableObject = ScreenLayoutHandler.getScreenLayoutFolderTableObject();
        this.getPageDetails(tableObject, pageLayoutIterator, screenPageObject, dataObject, this.getSubConfigProperties(templateConfig, "ScreenLayoutPageDetails"));
    }
    
    protected void getScreenLayoutSettingJSON(final JSONObject screenLayoutSettingJSON, final JSONArray templateConfigArray, final DataObject dataObject, final Long configDataItemId) throws Exception {
        final Criteria configDataItemCriteria = new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        final Row settingsRow = dataObject.getRow("ScreenLayoutSettings", configDataItemCriteria);
        if (settingsRow != null) {
            final List columns = settingsRow.getColumns();
            for (int i = 0; i < columns.size(); ++i) {
                final String columnName = columns.get(i);
                if (columnName.equalsIgnoreCase("SUBSEQUENT_APP_PAGE_ID")) {
                    final Long subsequentPageId = (Long)settingsRow.get("SUBSEQUENT_APP_PAGE_ID");
                    if (subsequentPageId != null) {
                        this.handleSubsequentAppId(screenLayoutSettingJSON, templateConfigArray, dataObject, subsequentPageId);
                    }
                }
                else if (!columnName.equalsIgnoreCase("CONFIG_DATA_ITEM_ID") && !columnName.equalsIgnoreCase("SCREEN_LAYOUT_ID")) {
                    final JSONObject property = this.getSubConfigProperties(templateConfigArray, columnName);
                    final Object columnValue = settingsRow.get(columnName);
                    if (property != null && String.valueOf(property.get("alias")) != null && columnValue != null) {
                        screenLayoutSettingJSON.put(property.getString("alias"), columnValue);
                    }
                }
            }
        }
    }
    
    protected void handleSubsequentAppId(final JSONObject screenLayoutSettingJSON, final JSONArray templateConfigArray, final DataObject dataObject, final Long subsequentPageId) throws Exception {
        final Criteria pageCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)subsequentPageId, 0);
        Row screenPages = dataObject.getRow("ScreenLayoutPageDetails", pageCriteria);
        if (screenPages == null) {
            final Criteria folderPageCriteria = new Criteria(new Column("FolderPage", "PAGE_ID"), (Object)subsequentPageId, 0);
            screenPages = dataObject.getRow("FolderPage", folderPageCriteria);
            final Long pageId = (Long)screenPages.get("PAGE_ID");
            final Criteria pageToFolderCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "PAGE_ID"), (Object)pageId, 0);
            final Row folderRow = dataObject.getRow("ScreenPageLayoutToFolderRel", pageToFolderCriteria);
            screenLayoutSettingJSON.put(this.getSubConfigProperties(templateConfigArray, "FOLDER_NAME").getString("alias"), folderRow.get("FOLDER_NAME"));
        }
        else {
            final Integer pageNo = (Integer)screenPages.get("PAGE_NO");
            screenLayoutSettingJSON.put(this.getSubConfigProperties(templateConfigArray, "PAGE_NO").getString("alias"), (Object)pageNo);
        }
    }
}
