package com.adventnet.sym.server.mdm.android.payload.transform;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.profiles.screenlayout.ScreenPageLayoutObject;
import java.util.Iterator;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutTableObject;
import java.util.List;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.android.payload.AndroidScreenLayoutPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidScreenLayoutPayload implements DO2AndroidPayload
{
    private Logger logger;
    
    public DO2AndroidScreenLayoutPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidScreenLayoutPayload payload = null;
        try {
            payload = new AndroidScreenLayoutPayload("1.0", "com.mdm.mobiledevice.homescreencustomiztion", "HomeScreenCustomization");
            final Row screenLayoutRow = dataObject.getRow("ScreenLayout");
            final Long screenLayoutId = (Long)screenLayoutRow.get("SCREEN_LAYOUT_ID");
            this.handleScreenLayoutSetting(dataObject, payload);
            this.handlePage(screenLayoutId, dataObject, payload);
            this.handleDock(screenLayoutId, dataObject, payload);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in create payload");
        }
        return payload;
    }
    
    private void handleDock(final Long screenLayoutId, final DataObject dataObject, final AndroidScreenLayoutPayload payload) throws Exception {
        final Criteria pageTypeCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)2, 0);
        final List<Long> pageIds = this.getPageIdsForScreenLayoutId(screenLayoutId, dataObject);
        final Criteria pageIdCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)pageIds.toArray(), 8);
        final Row dockPageRow = dataObject.getRow("ScreenLayoutPageDetails", pageTypeCriteria.and(pageIdCriteria));
        if (dockPageRow != null) {
            payload.setDockPageArray();
            final ScreenLayoutTableObject screenLayoutTableObject = ScreenLayoutHandler.getScreenLayoutTableObject();
            this.addEachPage(dockPageRow, dataObject, payload, screenLayoutTableObject);
        }
    }
    
    private void handlePage(final Long screenLayoutId, final DataObject dataObject, final AndroidScreenLayoutPayload payload) throws Exception {
        final Criteria pageTypeCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)1, 0);
        final List<Long> pageIds = this.getPageIdsForScreenLayoutId(screenLayoutId, dataObject);
        final Criteria pageIdCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)pageIds.toArray(), 8);
        payload.setScreenPageArray();
        final Iterator pageIterator = dataObject.getRows("ScreenLayoutPageDetails", pageTypeCriteria.and(pageIdCriteria));
        final ScreenLayoutTableObject screenLayoutTableObject = ScreenLayoutHandler.getScreenLayoutTableObject();
        while (pageIterator.hasNext()) {
            final Row pageRow = pageIterator.next();
            this.addEachPage(pageRow, dataObject, payload, screenLayoutTableObject);
        }
    }
    
    private void addEachPage(final Row pageRow, final DataObject dataObject, final AndroidScreenLayoutPayload homeScreenLayoutPayload, final ScreenLayoutTableObject tableObject) throws Exception {
        final Long pageId = (Long)pageRow.get("PAGE_ID");
        final int pageNo = (int)pageRow.get("PAGE_NO");
        final int pageType = (int)pageRow.get("PAGE_TYPE");
        final boolean isFolder = tableObject.getPageTableName().equalsIgnoreCase("FolderPage");
        final Criteria criteria = new Criteria(new Column(tableObject.getScreenPageToPageLayoutTableName(), "PAGE_ID"), (Object)pageId, 0);
        final Iterator pageIterator = dataObject.getRows(tableObject.getScreenPageLayoutTableName(), criteria);
        final ScreenPageLayoutObject object = new ScreenPageLayoutObject(pageIterator);
        if (pageType == 1) {
            homeScreenLayoutPayload.setPageLayoutArray(pageNo, isFolder);
        }
        final Criteria screenLayoutIdCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutTableName(), "PAGE_LAYOUT_ID"), (Object)object.getScreenPageLayoutIds().toArray(), 8);
        int position = 0;
        for (int i = 0; i <= object.getMaxRow(); ++i) {
            final Criteria rowCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutTableName(), "PAGE_LAYOUT_ROW"), (Object)i, 0);
            for (int j = 0; j <= object.getMaxColumn(); ++j) {
                final Criteria columnCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutTableName(), "PAGE_LAYOUT_COLUMN"), (Object)j, 0);
                final Row pageLayoutColumn = dataObject.getRow(tableObject.getScreenPageLayoutTableName(), screenLayoutIdCriteria.and(rowCriteria).and(columnCriteria));
                if (pageLayoutColumn != null) {
                    final int layoutType = (int)pageLayoutColumn.get("PAGE_LAYOUT_TYPE");
                    final Long pageLayoutId = (Long)pageLayoutColumn.get("PAGE_LAYOUT_ID");
                    switch (layoutType) {
                        case 1: {
                            final String bundleIdentifier = this.getAppBundleId(dataObject, pageLayoutId, tableObject);
                            homeScreenLayoutPayload.setAppDetails(position, bundleIdentifier, isFolder);
                            break;
                        }
                        case 2: {
                            final String url = this.getWebClipUrl(dataObject, pageLayoutId, tableObject);
                            homeScreenLayoutPayload.setWebclipDetails(position, url, isFolder);
                            break;
                        }
                        case 3: {
                            final Row folderRow = this.getFolderRow(dataObject, pageLayoutId, tableObject);
                            final String folderName = (String)folderRow.get("FOLDER_NAME");
                            homeScreenLayoutPayload.setFolderDetails(position, folderName);
                            this.handleFolderPages(dataObject, folderRow, homeScreenLayoutPayload);
                            break;
                        }
                    }
                    ++position;
                }
                else {
                    this.logger.log(Level.INFO, "No row for position({0},{1})", new Object[] { i, j });
                }
            }
        }
    }
    
    private String getAppBundleId(final DataObject dataObject, final Long pageLayoutId, final ScreenLayoutTableObject tableObject) throws DataAccessException {
        final Criteria appPageCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutToAppTableName(), "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        final Row appGroupRow = dataObject.getRow(tableObject.getAppGroupTableName(), appPageCriteria);
        final String bundleIdentifier = (String)appGroupRow.get("IDENTIFIER");
        return bundleIdentifier;
    }
    
    private String getWebClipUrl(final DataObject dataObject, final Long pageLayoutId, final ScreenLayoutTableObject tableObject) throws DataAccessException {
        final Criteria webClipCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutToWebClipTableName(), "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        final Row webClipRow = dataObject.getRow(tableObject.getWebClipPolicyTableName(), webClipCriteria);
        final String url = (String)webClipRow.get("WEBCLIP_URL");
        return url;
    }
    
    private Row getFolderRow(final DataObject dataObject, final Long pageLayoutId, final ScreenLayoutTableObject tableObject) throws DataAccessException {
        final Criteria folderCriteria = new Criteria(new Column(tableObject.getScreenPageLayoutToFolderTableName(), "PAGE_LAYOUT_ID"), (Object)pageLayoutId, 0);
        return dataObject.getRow(tableObject.getScreenPageLayoutToFolderTableName(), folderCriteria);
    }
    
    private void handleFolderPages(final DataObject dataObject, final Row folderRow, final AndroidScreenLayoutPayload payload) throws Exception {
        final Long folderId = (Long)folderRow.get("FOLDER_ID");
        final Criteria folderCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "FOLDER_ID"), (Object)folderId, 0);
        payload.setFolderLayoutPages();
        final Iterator pageIterator = dataObject.getRows("FolderPage", folderCriteria);
        final ScreenLayoutTableObject tableObject = ScreenLayoutHandler.getScreenLayoutFolderTableObject();
        while (pageIterator.hasNext()) {
            final Row folderPageRow = pageIterator.next();
            this.addEachPage(folderPageRow, dataObject, payload, tableObject);
        }
    }
    
    private List<Long> getPageIdsForScreenLayoutId(final Long screenLayoutId, final DataObject dataObject) throws DataAccessException {
        final List<Long> pageIds = new ArrayList<Long>();
        final Criteria screenLayoutCriteria = new Criteria(new Column("ScreenLayoutToPageRelation", "SCREEN_LAYOUT_ID"), (Object)screenLayoutId, 0);
        final Iterator iterator = dataObject.getRows("ScreenLayoutToPageRelation", screenLayoutCriteria);
        while (iterator.hasNext()) {
            final Row pageRow = iterator.next();
            final Long pageId = (Long)pageRow.get("PAGE_ID");
            pageIds.add(pageId);
        }
        return pageIds;
    }
    
    private void handleScreenLayoutSetting(final DataObject dataObject, final AndroidScreenLayoutPayload payload) throws Exception {
        final Row screenSettingRow = dataObject.getRow("ScreenLayoutSettings");
        final String textColour = (String)screenSettingRow.get("TEXT_COLOUR");
        final int iconSize = (int)screenSettingRow.get("ICON_SIZE");
        final Integer textSize = (Integer)screenSettingRow.get("TEXT_SIZE");
        final int orientationType = (int)screenSettingRow.get("ORIENTATION_TYPE");
        final boolean allowUserChangePosition = (boolean)screenSettingRow.get("ALLOW_CHANGE_APP_POSITION");
        final Long pageId = (Long)screenSettingRow.get("SUBSEQUENT_APP_PAGE_ID");
        payload.setOrientationType(orientationType);
        payload.setIconSize(iconSize);
        payload.setUserToChangePosition(allowUserChangePosition);
        if (!MDMStringUtils.isEmpty(textColour)) {
            payload.setTextColour(textColour);
        }
        if (textSize != null) {
            payload.setTextSize(textSize);
        }
        if (pageId != null) {
            final Criteria pageCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)pageId, 0);
            final Row screenPages = dataObject.getRow("ScreenLayoutPageDetails", pageCriteria);
            if (screenPages == null) {
                final Criteria pageToFolderCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "PAGE_ID"), (Object)pageId, 0);
                final Row folderRow = dataObject.getRow("ScreenPageLayoutToFolderRel", pageToFolderCriteria);
                final String folderName = (String)folderRow.get("FOLDER_NAME");
                payload.setPlaceSubsequentAppFolder(folderName);
            }
            else {
                final Integer pageNo = (Integer)screenPages.get("PAGE_NO");
                payload.setPlaceSubsequentAppPage(pageNo);
            }
        }
    }
}
