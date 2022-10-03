package com.me.mdm.server.webclips;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.me.mdm.server.profiles.screenlayout.ScreenLayoutHandler;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.me.mdm.server.profiles.ProfilePayloadMapping;

public class WebClipsMapping extends ProfilePayloadMapping
{
    public WebClipsMapping(final String baseTableName, final String baseTableColumn, final boolean unConfigurePayload, final boolean deleteRow) {
        this.tableName = baseTableName;
        this.columnName = baseTableColumn;
        this.baseTable = "WebClipPolicies";
        this.baseTableColumn = "WEBCLIP_POLICY_ID";
        this.cfgDataItemTable = this.tableName;
        this.unConfigurePayload = unConfigurePayload;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.deleteRow = deleteRow;
    }
    
    public WebClipsMapping(final List<Join> webClipsJoin, final String cfgTable, final HashMap criteriaColumn, final Boolean isDeleteRow, final String tableName, final String tableColumn, final boolean modifyClonedDO) {
        this.joinList = webClipsJoin;
        this.baseTable = "WebClipPolicies";
        this.baseTableColumn = "WEBCLIP_POLICY_ID";
        this.unConfigurePayload = Boolean.FALSE;
        this.cfgDataItemTable = cfgTable;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.criteriaTableColumns = criteriaColumn;
        this.deleteRow = isDeleteRow;
        this.tableName = tableName;
        this.columnName = tableColumn;
        this.modifyClonedDO = modifyClonedDO;
    }
    
    @Override
    public void modifyClonedDO(final DataObject clonedDO) throws DataAccessException {
        final String tableName = this.getTableName();
        final List tables = clonedDO.getTableNames();
        if (tableName.equalsIgnoreCase("ScreenLayoutSettings") && tables.contains(tableName)) {
            final Row screenLayoutRow = clonedDO.getRow("ScreenLayoutSettings");
            final Long screenLayoutId = (Long)screenLayoutRow.get("SCREEN_LAYOUT_ID");
            final DataObject screenDO = new ScreenLayoutHandler().getScreenLayoutDO(screenLayoutId);
            clonedDO.merge(screenDO);
        }
    }
    
    @Override
    public int customDeleteRow(final DataObject dataObject, final Row row, final Integer configId) throws Exception {
        final String tableName = row.getTableName();
        if (tableName.equalsIgnoreCase("WebClipToConfigRel")) {
            final Long configDataItemId = (Long)row.get("CONFIG_DATA_ITEM_ID");
            boolean singleWebAppKiosk = false;
            if (configId == 183) {
                final Row applockRow = dataObject.getRow("AppLockPolicy", new Criteria(new Column("AppLockPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                final int kioskMode = (int)applockRow.get("KIOSK_MODE");
                if (kioskMode == 3) {
                    singleWebAppKiosk = true;
                }
            }
            else if (configId == 557) {
                final Row androidKioskRow = dataObject.getRow("AndroidKioskPolicy", new Criteria(new Column("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                final int kioskMode = (int)androidKioskRow.get("KIOSK_MODE");
                if (kioskMode == 3) {
                    singleWebAppKiosk = true;
                }
            }
            if (singleWebAppKiosk) {
                return 1;
            }
        }
        return 0;
    }
}
