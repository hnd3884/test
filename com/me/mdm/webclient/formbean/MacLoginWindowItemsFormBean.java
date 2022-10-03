package com.me.mdm.webclient.formbean;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Criteria;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MacLoginWindowItemsFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "MacLoginWindowItemSettings", "CONFIG_DATA_ITEM_ID");
                final JSONArray loginWindowArray = dynaForm.optJSONArray("MacLoginWindowItems");
                this.modifyLoginItems(loginWindowArray, dataObject);
            }
        }
        catch (final Exception e) {
            MacLoginWindowItemsFormBean.logger.log(Level.SEVERE, "Exception in mac login window", e);
            throw new SyMException();
        }
    }
    
    private void modifyLoginItems(final JSONArray array, final DataObject dataObject) throws Exception {
        final Object configDataItemId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configDataItemId instanceof UniqueValueHolder)) {
            dataObject.deleteRows("MacLoginWindowItems", new Criteria(new Column("MacLoginWindowItems", "CONFIG_DATA_ITEM_ID"), configDataItemId, 0));
        }
        for (int i = 0; array != null && i < array.length(); ++i) {
            final JSONObject loginObject = array.getJSONObject(i);
            final Row loginItemRow = new Row("MacLoginWindowItems");
            loginItemRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
            loginItemRow.set("ITEM_PATH", loginObject.get("ITEM_PATH"));
            loginItemRow.set("HIDE_ITEM", loginObject.get("HIDE_ITEM"));
            dataObject.addRow(loginItemRow);
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = configDOFromDB.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object clonedConfigDataItemId = cloneConfigDO.getValue("MacLoginWindowItemSettings", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        MacLoginWindowItemsFormBean.logger.log(Level.FINE, " configDOFromDB {0}", configDOFromDB);
        final Criteria configDataItemCriteria = new Criteria(new Column("MacLoginWindowItems", "CONFIG_DATA_ITEM_ID"), configDataItemId, 0);
        final Iterator iterator = configDOFromDB.getRows("MacLoginWindowItems", configDataItemCriteria);
        while (iterator.hasNext()) {
            final Row existingItemsRow = iterator.next();
            final Row itemsRow = new Row("MacLoginWindowItems");
            this.cloneRow(existingItemsRow, itemsRow, "ITEM_ID");
            itemsRow.set("CONFIG_DATA_ITEM_ID", clonedConfigDataItemId);
            cloneConfigDO.addRow(itemsRow);
        }
        MacLoginWindowItemsFormBean.logger.log(Level.FINE, " cloneConfigDO {0}", cloneConfigDO);
    }
}
