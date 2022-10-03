package com.me.mdm.webclient.formbean;

import org.json.JSONException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DirectoryBindPolicyFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            super.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
            for (final JSONObject dynaForm : dynaActionForm) {
                if (dynaForm.has("ADMINPRIVILEGEADGROUP")) {
                    final String jsonString = String.valueOf(dynaForm.get("ADMINPRIVILEGEADGROUP"));
                    JSONArray jsonObj = null;
                    if (!MDMStringUtils.isEmpty(jsonString)) {
                        jsonObj = new JSONArray(jsonString);
                        this.modifySubTableDetails(jsonObj, dataObject, "ADBindPrivilegeGroup", "GROUP_NAME");
                    }
                }
                if (dynaForm.has("ADBINDOU")) {
                    final String jsonString = String.valueOf(dynaForm.get("ADBINDOU"));
                    final JSONArray jsonObj = new JSONArray();
                    jsonObj.put((Object)jsonString);
                    this.modifySubTableDetails(jsonObj, dataObject, "ADBindOU", "OU");
                }
            }
        }
        catch (final Exception exp) {
            DirectoryBindPolicyFormBean.logger.log(Level.SEVERE, "Exception occured at DirectoryBindPolicyFormBean", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        DirectoryBindPolicyFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        Iterator it = configDOFromDB.getRows("ADBindPrivilegeGroup");
        while (it.hasNext()) {
            final Row adminRow = it.next();
            final Row clonedRow = new Row("ADBindPrivilegeGroup");
            final Object newPolicyId = this.cloneRow(adminRow, clonedRow, "PRIVILEGE_GROUP_ID");
            clonedRow.set("PRIVILEGE_GROUP_ID", newPolicyId);
            clonedRow.set("CFG_DATA_ITEM", configDataItemId);
            cloneConfigDO.addRow(clonedRow);
        }
        it = configDOFromDB.getRows("ADBindOU");
        while (it.hasNext()) {
            final Row adminRow = it.next();
            final Row clonedRow = new Row("ADBindOU");
            final Object newPolicyId = this.cloneRow(adminRow, clonedRow, "BIND_OU_ID");
            clonedRow.set("BIND_OU_ID", newPolicyId);
            clonedRow.set("CFG_DATA_ITEM", configDataItemId);
            cloneConfigDO.addRow(clonedRow);
        }
        DirectoryBindPolicyFormBean.logger.log(Level.INFO, " cloneConfigDO {0}", cloneConfigDO);
    }
    
    private void modifySubTableDetails(final JSONArray jsonArray, final DataObject dataObject, final String subtableName, final String column) throws JSONException, DataAccessException {
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            dataObject.deleteRows(subtableName, (Criteria)null);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final String value = (String)jsonArray.get(i);
            final Row row = new Row(subtableName);
            row.set(column, (Object)value);
            row.set("CFG_DATA_ITEM", configId);
            dataObject.addRow(row);
        }
    }
    
    static {
        DirectoryBindPolicyFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
