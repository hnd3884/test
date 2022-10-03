package com.adventnet.sym.server.mdm.ios.payload.transform;

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
import com.adventnet.sym.server.mdm.ios.payload.HomeScreenLayoutPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2HomeScreenLayoutPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2HomeScreenLayoutPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] iosPayloads = { null };
        final HomeScreenLayoutPayload homeScreenLayoutPayload = new HomeScreenLayoutPayload(1, "MDM", "com.apple.homescreenlayout", "Homescreen Layout");
        try {
            final Row screenLayoutRow = dataObject.getRow("ScreenLayout");
            final Long screenLayoutId = (Long)screenLayoutRow.get("SCREEN_LAYOUT_ID");
            this.handlePage(screenLayoutId, dataObject, homeScreenLayoutPayload);
            this.handleDock(screenLayoutId, dataObject, homeScreenLayoutPayload);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in create payload");
        }
        iosPayloads[0] = homeScreenLayoutPayload;
        return iosPayloads;
    }
    
    private void handleDock(final Long screenLayoutId, final DataObject dataObject, final HomeScreenLayoutPayload payload) throws Exception {
        final Criteria pageTypeCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)2, 0);
        final List<Long> pageIds = this.getPageIdsForScreenLayoutId(screenLayoutId, dataObject);
        final Criteria pageIdCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)pageIds.toArray(), 8);
        final Row dockPageRow = dataObject.getRow("ScreenLayoutPageDetails", pageTypeCriteria.and(pageIdCriteria));
        if (dockPageRow != null) {
            final ScreenLayoutTableObject screenLayoutTableObject = ScreenLayoutHandler.getScreenLayoutTableObject();
            this.addEachPage(dockPageRow, dataObject, payload, screenLayoutTableObject);
        }
    }
    
    private void handlePage(final Long screenLayoutId, final DataObject dataObject, final HomeScreenLayoutPayload payload) throws Exception {
        final Criteria pageTypeCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)1, 0);
        final List<Long> pageIds = this.getPageIdsForScreenLayoutId(screenLayoutId, dataObject);
        final Criteria pageIdCriteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_ID"), (Object)pageIds.toArray(), 8);
        final Iterator countIterator = dataObject.getRows("ScreenLayoutPageDetails", pageTypeCriteria.and(pageIdCriteria));
        payload.setScreenPageArray(this.getCountForIterator(countIterator));
        final Iterator pageIterator = dataObject.getRows("ScreenLayoutPageDetails", pageTypeCriteria.and(pageIdCriteria));
        final ScreenLayoutTableObject screenLayoutTableObject = ScreenLayoutHandler.getScreenLayoutTableObject();
        while (pageIterator.hasNext()) {
            final Row pageRow = pageIterator.next();
            this.addEachPage(pageRow, dataObject, payload, screenLayoutTableObject);
        }
    }
    
    private void addEachPage(final Row pageRow, final DataObject dataObject, final HomeScreenLayoutPayload homeScreenLayoutPayload, final ScreenLayoutTableObject tableObject) throws Exception {
        final Long pageId = (Long)pageRow.get("PAGE_ID");
        final int pageNo = (int)pageRow.get("PAGE_NO");
        final int pageType = (int)pageRow.get("PAGE_TYPE");
        final boolean isFolder = tableObject.getPageTableName().equalsIgnoreCase("FolderPage");
        final Criteria criteria = new Criteria(new Column(tableObject.getScreenPageToPageLayoutTableName(), "PAGE_ID"), (Object)pageId, 0);
        final Iterator pageIterator = dataObject.getRows(tableObject.getScreenPageLayoutTableName(), criteria);
        final ScreenPageLayoutObject object = new ScreenPageLayoutObject(pageIterator);
        if (pageType == 1) {
            homeScreenLayoutPayload.setPageLayoutArray(pageNo, object.getTotalCount(), isFolder);
        }
        else {
            homeScreenLayoutPayload.setDockPageArray(object.getTotalCount());
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
    
    private void handleFolderPages(final DataObject dataObject, final Row folderRow, final HomeScreenLayoutPayload payload) throws Exception {
        final Long folderId = (Long)folderRow.get("FOLDER_ID");
        final Criteria folderCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "FOLDER_ID"), (Object)folderId, 0);
        final Iterator countIterator = dataObject.getRows("FolderPage", folderCriteria);
        payload.setFolderLayoutPages(this.getCountForIterator(countIterator));
        final Iterator pageIterator = dataObject.getRows("FolderPage", folderCriteria);
        final ScreenLayoutTableObject tableObject = ScreenLayoutHandler.getScreenLayoutFolderTableObject();
        while (pageIterator.hasNext()) {
            final Row folderPageRow = pageIterator.next();
            this.addEachPage(folderPageRow, dataObject, payload, tableObject);
        }
    }
    
    private int getCountForIterator(final Iterator iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            ++count;
            iterator.next();
        }
        return count;
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
}
