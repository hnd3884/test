package com.me.mdm.server.profiles.font;

import com.me.mdm.server.profiles.ProfilePayloadMapping;

public class FontMapping extends ProfilePayloadMapping
{
    public FontMapping(final String baseTableName, final String baseTableColumn, final boolean unConfigurePayload, final boolean deleteRow) {
        this.tableName = baseTableName;
        this.columnName = baseTableColumn;
        this.baseTable = "FontDetails";
        this.baseTableColumn = "FONT_ID";
        this.cfgDataItemTable = this.tableName;
        this.unConfigurePayload = unConfigurePayload;
        this.cfgColumn = "CONFIG_DATA_ITEM_ID";
        this.deleteRow = deleteRow;
    }
}
