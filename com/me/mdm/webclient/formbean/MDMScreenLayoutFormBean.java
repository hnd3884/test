package com.me.mdm.webclient.formbean;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutHandler;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MDMScreenLayoutFormBean extends MDMDefaultFormBean
{
    public void addScreenLayout(final JSONObject dynaForm, final DataObject dataObject, final JSONObject multipleConfigForm) throws Exception {
        final JSONObject screenLayoutJSON = dynaForm.getJSONObject("ScreenLayout");
        JSONObject screenLayoutSettingJSON = dynaForm.optJSONObject("ScreenLayoutSettings");
        if (screenLayoutSettingJSON == null) {
            screenLayoutSettingJSON = new JSONObject();
        }
        final Long collectionId = multipleConfigForm.getLong("COLLECTION_ID");
        final Long customerId = dynaForm.getLong("CUSTOMER_ID");
        final String layoutName = multipleConfigForm.getString("PROFILE_NAME");
        screenLayoutJSON.put("LAYOUT_NAME", (Object)(layoutName + "-" + collectionId));
        screenLayoutJSON.put("CUSTOMER_ID", (Object)customerId);
        final ScreenLayoutHandler handler = this.getScreenLayoutHandler();
        final Object screenLayoutId = handler.addOrModifyScreenLayout(screenLayoutJSON, dataObject);
        this.handleScreenLayoutSetting(screenLayoutSettingJSON, screenLayoutId, dataObject, multipleConfigForm);
    }
    
    protected ScreenLayoutHandler getScreenLayoutHandler() {
        return new ScreenLayoutHandler();
    }
    
    protected void handleScreenLayoutSetting(final JSONObject layoutSetting, final Object screenLayoutId, final DataObject dataObject, final JSONObject multipleConfigForm) throws Exception {
        this.getPageIdForSubsequentApp(dataObject, layoutSetting);
        layoutSetting.put("SCREEN_LAYOUT_ID", screenLayoutId);
        this.modifyDetails(dataObject, layoutSetting, multipleConfigForm, "ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID");
    }
    
    private void getPageIdForSubsequentApp(final DataObject dataObject, final JSONObject layoutSetting) throws DataAccessException {
        final Integer pageNo = layoutSetting.optInt("PAGE_NO", -1);
        final String folderName = layoutSetting.optString("FOLDER_NAME");
        Object pageId = null;
        if (pageNo != -1 && MDMStringUtils.isEmpty(folderName)) {
            final Criteria criteria = new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_NO"), (Object)pageNo, 0);
            final Iterator iterator = dataObject.getRows("ScreenLayoutPageDetails", criteria);
            final Row pageRow = this.getUVHRow(iterator, "PAGE_ID");
            if (pageRow != null) {
                pageId = pageRow.get("PAGE_ID");
                layoutSetting.put("SUBSEQUENT_APP_PAGE_ID", pageId);
            }
        }
        else if (pageNo == -1 && !MDMStringUtils.isEmpty(folderName)) {
            final Criteria folderNameCriteria = new Criteria(new Column("ScreenPageLayoutToFolderRel", "FOLDER_NAME"), (Object)folderName, 0);
            final Iterator iterator = dataObject.getRows("ScreenPageLayoutFolderToPageRel", folderNameCriteria);
            final Row folderRow = this.getUVHRow(iterator, "FOLDER_ID");
            if (folderRow != null) {
                final Object folderId = folderRow.get("FOLDER_ID");
                final Criteria folderIdCriteria = new Criteria(new Column("ScreenPageLayoutFolderToPageRel", "FOLDER_ID"), folderId, 0);
                final Iterator iterator2 = dataObject.getRows("ScreenLayoutPageDetails", folderIdCriteria);
                final Row folderPageRow = this.getGreaterPageNumberRow(iterator2);
                pageId = folderPageRow.get("PAGE_ID");
                layoutSetting.put("SUBSEQUENT_APP_PAGE_ID", pageId);
            }
        }
        else {
            MDMScreenLayoutFormBean.logger.log(Level.INFO, "No setting is configured for subsequent app");
        }
    }
    
    private Row getUVHRow(final Iterator iterator, final String columnName) {
        Row row = null;
        while (iterator.hasNext()) {
            final Row iteratorRow = iterator.next();
            final Object folderID = iteratorRow.get(columnName);
            if (!(folderID instanceof Long)) {
                row = iteratorRow;
                break;
            }
        }
        return row;
    }
    
    private Row getGreaterPageNumberRow(final Iterator iterator) {
        Row row = null;
        final int pageMax = 0;
        while (iterator.hasNext()) {
            final Row pageRow = iterator.next();
            final int pageNo = (int)pageRow.get("PAGE_NO");
            if (pageMax < pageNo) {
                row = pageRow;
            }
        }
        return row;
    }
}
