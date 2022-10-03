package com.me.mdm.server.profiles.screenlayout;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class ScreenLayoutHandler
{
    public static final int APP_LAYOUT_TYPE = 1;
    public static final int WEBCLIP_LAYOUT_TYPE = 2;
    public static final int FOLDER_LAYOUT_TYPE = 3;
    public static final int SCREEN_PAGE_TYPE = 1;
    public static final int DOCK_PAGE_TYPE = 2;
    private boolean isAdded;
    
    public ScreenLayoutHandler() {
        this.isAdded = false;
    }
    
    public Object addScreenLayout(final JSONObject layoutJSON, final DataObject dataObject) throws Exception {
        final String layoutName = layoutJSON.getString("LAYOUT_NAME");
        final DataObject screenLayoutDO = (DataObject)new WritableDataObject();
        final Long customerId = layoutJSON.getLong("CUSTOMER_ID");
        final Row screenLayoutRow = new Row("ScreenLayout");
        screenLayoutRow.set("LAYOUT_NAME", (Object)layoutName);
        screenLayoutRow.set("CUSTOMER_ID", (Object)customerId);
        screenLayoutDO.addRow(screenLayoutRow);
        final Object layoutId = screenLayoutRow.get("SCREEN_LAYOUT_ID");
        this.addScreenLayoutToPageRelation(layoutJSON, screenLayoutDO, layoutId);
        dataObject.merge(screenLayoutDO);
        return layoutId;
    }
    
    private void addScreenLayoutToPageRelation(final JSONObject layoutJSON, final DataObject dataObject, final Object screenLayoutId) throws Exception {
        final List<Object> screenPages = this.addPageDetails(dataObject, layoutJSON);
        for (final Object pageId : screenPages) {
            final Row screenLayoutPageRow = new Row("ScreenLayoutToPageRelation");
            screenLayoutPageRow.set("PAGE_ID", pageId);
            screenLayoutPageRow.set("SCREEN_LAYOUT_ID", screenLayoutId);
            dataObject.addRow(screenLayoutPageRow);
        }
        this.verifyPageType(dataObject, screenLayoutId);
    }
    
    private List<Object> addPageDetails(final DataObject dataObject, final JSONObject pageJSON) throws Exception {
        final List<Object> pageIds = new ArrayList<Object>();
        final JSONArray pages = pageJSON.getJSONArray("ScreenLayoutPageDetails");
        for (int i = 0; i < pages.length(); ++i) {
            final JSONObject pagesObject = pages.getJSONObject(i);
            final Integer pageNO = pagesObject.getInt("PAGE_NO");
            final Integer pageType = pagesObject.getInt("PAGE_TYPE");
            final Row pageRow = new Row("ScreenLayoutPageDetails");
            pageRow.set("PAGE_NO", (Object)pageNO);
            pageRow.set("PAGE_TYPE", (Object)pageType);
            dataObject.addRow(pageRow);
            final JSONArray screenPageLayout = pagesObject.getJSONArray("ScreenPageLayout");
            final Object pageId = pageRow.get("PAGE_ID");
            for (int j = 0; j < screenPageLayout.length(); ++j) {
                final JSONObject layoutJSON = screenPageLayout.getJSONObject(j);
                final Object pageLayoutId = this.addScreenPageLayout(dataObject, layoutJSON, pageId);
                final Row screenPageRel = new Row("ScreenPageToPageLayout");
                screenPageRel.set("PAGE_LAYOUT_ID", pageLayoutId);
                screenPageRel.set("PAGE_ID", pageId);
                dataObject.addRow(screenPageRel);
            }
            pageIds.add(pageId);
        }
        return pageIds;
    }
    
    protected void verifyPageType(final DataObject dataObject, final Object screenLayoutId) throws Exception {
        final Criteria screenLayoutCriteria = new Criteria(new Column("ScreenLayout", "SCREEN_LAYOUT_ID"), screenLayoutId, 0);
        final Row screenLayoutRow = dataObject.getRow("ScreenLayout", screenLayoutCriteria);
        final List tableList = new ArrayList();
        tableList.add("ScreenLayout");
        tableList.add("ScreenLayoutToPageRelation");
        tableList.add("ScreenLayoutPageDetails");
        tableList.add("ScreenPageToPageLayout");
        final DataObject tempDO = dataObject.getDataObject(tableList, screenLayoutRow);
        final Criteria screenPageCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)1, 0);
        final Row pageTypeRow = tempDO.getRow("ScreenLayoutPageDetails", screenPageCriteria);
        final Row pageRow = tempDO.getRow("ScreenPageToPageLayout", pageTypeRow);
        if (pageRow == null) {
            throw new PayloadException("PAY0002");
        }
        final Criteria dockCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)2, 0);
        final int size = MDMDBUtil.getDOSize(tempDO, "ScreenLayoutPageDetails", dockCriteria);
        if (size > 1) {
            throw new PayloadException("PAY0004");
        }
    }
    
    private Object addScreenPageLayout(final DataObject dataObject, final JSONObject layoutJSON, final Object pageId) throws Exception {
        final Integer layoutRow = layoutJSON.getInt("PAGE_LAYOUT_ROW");
        final Integer layoutColumn = layoutJSON.getInt("PAGE_LAYOUT_COLUMN");
        final Integer pageLayoutType = layoutJSON.getInt("PAGE_LAYOUT_TYPE");
        if (this.checkPositionFilledInPage(pageId, dataObject, layoutRow, layoutColumn)) {
            throw new PayloadException("PAY0004");
        }
        final Row screenPageRow = new Row("ScreenPageLayout");
        screenPageRow.set("PAGE_LAYOUT_COLUMN", (Object)layoutColumn);
        screenPageRow.set("PAGE_LAYOUT_ROW", (Object)layoutRow);
        screenPageRow.set("PAGE_LAYOUT_TYPE", (Object)pageLayoutType);
        dataObject.addRow(screenPageRow);
        final Object pageLayoutId = screenPageRow.get("PAGE_LAYOUT_ID");
        switch (pageLayoutType) {
            case 1: {
                this.addAppPageLayout(dataObject, layoutJSON, pageLayoutId);
                break;
            }
            case 2: {
                this.addWebClipsLayout(dataObject, layoutJSON, pageLayoutId);
                break;
            }
            case 3: {
                this.addFolderLayout(dataObject, layoutJSON, pageLayoutId);
                break;
            }
        }
        return screenPageRow.get("PAGE_LAYOUT_ID");
    }
    
    protected void addAppPageLayout(final DataObject dataObject, final JSONObject layoutJSON, final Object pageLayoutId) throws Exception {
        final Object appGroupId = layoutJSON.get("APP_GROUP_ID");
        final Criteria appGroupIdCriteria = new Criteria(new Column("ScreenPageLayoutToAppRel", "APP_GROUP_ID"), appGroupId, 0);
        final Row appRow = dataObject.getRow("ScreenPageLayoutToAppRel", appGroupIdCriteria);
        if (appRow != null) {
            throw new PayloadException("PAY0004");
        }
        final Row appRelRow = new Row("ScreenPageLayoutToAppRel");
        appRelRow.set("PAGE_LAYOUT_ID", pageLayoutId);
        appRelRow.set("APP_GROUP_ID", appGroupId);
        dataObject.addRow(appRelRow);
    }
    
    protected void addWebClipsLayout(final DataObject dataObject, final JSONObject layoutJSON, final Object pageLayoutId) throws Exception {
        final Object webClipPolicyId = layoutJSON.has("WEBCLIP_POLICY_ID") ? layoutJSON.get("WEBCLIP_POLICY_ID") : Long.valueOf(-1L);
        final Criteria webClipIdCriteria = new Criteria(new Column("ScreenPageLayoutToWebClipRel", "WEBCLIP_POLICY_ID"), webClipPolicyId, 0);
        final Row webClipRow = dataObject.getRow("ScreenPageLayoutToWebClipRel", webClipIdCriteria);
        if (webClipRow != null) {
            throw new PayloadException("PAY0004");
        }
        final Row webRelRow = new Row("ScreenPageLayoutToWebClipRel");
        webRelRow.set("PAGE_LAYOUT_ID", pageLayoutId);
        webRelRow.set("WEBCLIP_POLICY_ID", webClipPolicyId);
        dataObject.addRow(webRelRow);
    }
    
    protected void addFolderLayout(final DataObject dataObject, final JSONObject layoutJSON, final Object pageLayoutId) throws Exception {
        final String folderName = layoutJSON.getString("FOLDER_NAME");
        final Row folderRow = new Row("ScreenPageLayoutToFolderRel");
        folderRow.set("PAGE_LAYOUT_ID", pageLayoutId);
        folderRow.set("FOLDER_NAME", (Object)folderName);
        dataObject.addRow(folderRow);
        final List<Object> pageObjects = this.addPageDetails(dataObject, layoutJSON);
        final Object folderId = folderRow.get("FOLDER_ID");
        for (final Object pageId : pageObjects) {
            final Row folderToPageRow = new Row("ScreenPageLayoutFolderToPageRel");
            folderToPageRow.set("FOLDER_ID", folderId);
            folderToPageRow.set("PAGE_ID", pageId);
            dataObject.addRow(folderToPageRow);
        }
    }
    
    public Object modifyScreenLayout(final JSONObject layoutJSON, final DataObject dataObject) throws Exception {
        final String layoutName = layoutJSON.getString("LAYOUT_NAME");
        final Long screenLayoutId = layoutJSON.getLong("SCREEN_LAYOUT_ID");
        final Criteria screenLayoutCriteria = new Criteria(new Column("ScreenLayout", "SCREEN_LAYOUT_ID"), (Object)screenLayoutId, 0);
        final Row screenLayoutRow = dataObject.getRow("ScreenLayout", screenLayoutCriteria);
        screenLayoutRow.set("LAYOUT_NAME", (Object)layoutName);
        dataObject.deleteRows("ScreenLayoutToPageRelation", screenLayoutCriteria);
        final List screenLayoutTable = new ArrayList();
        screenLayoutTable.add("ScreenLayout");
        final DataObject screenLayoutDO = dataObject.getDataObject(screenLayoutTable, screenLayoutRow);
        this.addScreenLayoutToPageRelation(layoutJSON, screenLayoutDO, screenLayoutId);
        dataObject.merge(screenLayoutDO);
        return screenLayoutId;
    }
    
    public Object addOrModifyScreenLayout(final JSONObject layoutJSON, final DataObject dataObject) throws Exception {
        final String layoutName = layoutJSON.getString("LAYOUT_NAME");
        final Criteria layoutNameCriteria = new Criteria(new Column("ScreenLayout", "LAYOUT_NAME"), (Object)layoutName, 0);
        final Row layoutRow = dataObject.getRow("ScreenLayout", layoutNameCriteria);
        if (layoutRow == null) {
            this.isAdded = true;
            return this.addScreenLayout(layoutJSON, dataObject);
        }
        final Long screenLayoutId = (Long)layoutRow.get("SCREEN_LAYOUT_ID");
        layoutJSON.put("SCREEN_LAYOUT_ID", (Object)screenLayoutId);
        return this.modifyScreenLayout(layoutJSON, dataObject);
    }
    
    private boolean checkPositionFilledInPage(final Object pageId, final DataObject dataObject, final int row, final int column) throws Exception {
        final Criteria pageCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), pageId, 0);
        final Row screenLayoutRow = dataObject.getRow("ScreenLayoutPageDetails", pageCriteria);
        final List tableList = new ArrayList();
        tableList.add("ScreenPageLayout");
        tableList.add("ScreenPageToPageLayout");
        final DataObject tempDO = dataObject.getDataObject(tableList, screenLayoutRow);
        final Criteria pageRowAndColumnCriteria = new Criteria(new Column("ScreenPageLayout", "PAGE_LAYOUT_ROW"), (Object)row, 0).and(new Criteria(new Column("ScreenPageLayout", "PAGE_LAYOUT_COLUMN"), (Object)column, 0));
        final Row positionRow = tempDO.getRow("ScreenPageLayout", pageRowAndColumnCriteria);
        return positionRow != null;
    }
    
    public DataObject getScreenLayoutDO(final Long screenLayoutId) throws DataAccessException {
        final Table folderPage = new Table("ScreenLayoutPageDetails", "FolderPage");
        final Table folderPageToPageLayout = new Table("ScreenPageToPageLayout", "FolderPageToPageRel");
        final Table folderPageLayout = new Table("ScreenPageLayout", "FolderScreenPageLayout");
        final Table folderPageAppLayout = new Table("ScreenPageLayoutToAppRel", "FolderPageLayoutApp");
        final Table folderPageLayoutWebClip = new Table("ScreenPageLayoutToWebClipRel", "FolderPageLayoutWebclip");
        final Table folderWebclipPolicies = new Table("WebClipPolicies", "FolderWebclipPolicies");
        final Table folderAppGroupDetail = new Table("MdAppGroupDetails", "FolderAppGroupDetails");
        final Table folderAppGroupPackage = new Table("MdPackageToAppData", "FolderAppPackageToAppData");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ScreenLayout"));
        query.addJoin(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
        query.addJoin(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        query.addJoin(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        query.addJoin(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToAppRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayoutToAppRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToWebClipRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayoutToWebClipRel", "WebClipPolicies", new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayout", "ScreenPageLayoutToFolderRel", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join("ScreenPageLayoutToFolderRel", "ScreenPageLayoutFolderToPageRel", new String[] { "FOLDER_ID" }, new String[] { "FOLDER_ID" }, 1));
        query.addJoin(new Join(new Table("ScreenPageLayoutFolderToPageRel"), folderPage, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        query.addJoin(new Join(folderPage, folderPageToPageLayout, new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        query.addJoin(new Join(folderPageToPageLayout, folderPageLayout, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join(folderPageLayout, folderPageAppLayout, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join(folderPageAppLayout, folderAppGroupDetail, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        query.addJoin(new Join(folderPageLayout, folderPageLayoutWebClip, new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
        query.addJoin(new Join(folderPageLayoutWebClip, folderWebclipPolicies, new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 1));
        query.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        query.addJoin(new Join(folderAppGroupDetail, folderAppGroupPackage, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        query.addSelectColumn(new Column((String)null, "*"));
        query.setCriteria(new Criteria(new Column("ScreenLayout", "SCREEN_LAYOUT_ID"), (Object)screenLayoutId, 0));
        return MDMUtil.getPersistenceLite().get(query);
    }
    
    public static ScreenLayoutTableObject getScreenLayoutFolderTableObject() {
        return new ScreenLayoutTableObject("FolderPage", "FolderPageToPageRel", "FolderScreenPageLayout", "FolderPageLayoutApp", "ScreenPageLayoutToFolderRel", "FolderPageLayoutWebclip", "FolderAppGroupDetails", "FolderWebclipPolicies", "FolderAppPackageToAppData");
    }
    
    public static ScreenLayoutTableObject getScreenLayoutTableObject() {
        return new ScreenLayoutTableObject("ScreenLayoutPageDetails", "ScreenPageToPageLayout", "ScreenPageLayout", "ScreenPageLayoutToAppRel", "ScreenPageLayoutToFolderRel", "ScreenPageLayoutToWebClipRel", "screenLayoutAppGroup", "screenLayoutWebClipPolicies", "ScreenLayoutAppPackageToAppData");
    }
}
