package com.adventnet.sym.webclient.mdm.config;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class ManagedDomainFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionorder = dynaActionForm.length;
        try {
            for (int i = 0; i < executionorder; ++i) {
                final JSONObject dynaForm = dynaActionForm[i];
                if (!dataObject.containsTable("ConfigDataItem")) {
                    this.insertConfigDataItem(dynaForm, dataObject, i);
                }
                final Object urldetails = dynaForm.get("URL_DETAILS");
                JSONArray jsonobj;
                if (urldetails instanceof String) {
                    jsonobj = new JSONArray((String)urldetails);
                }
                else {
                    jsonobj = (JSONArray)urldetails;
                }
                this.modifyURLDETAILS(jsonobj, dataObject);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting dynaform to DO", e);
            throw new SyMException(1002, e.getCause());
        }
    }
    
    private void deleteExistingURLDetails(final DataObject dataObject, final Object configDataItem) {
        try {
            final Object[] urlDetails = this.getSpecificColumnValue(dataObject, "ManagedWebDomainPolicy", "URL_DETAILS_ID", configDataItem);
            dataObject.deleteRows("ManagedWebDomainURLDetails", new Criteria(new Column("ManagedWebDomainURLDetails", "URL_DETAILS_ID"), (Object)urlDetails, 8));
            dataObject.deleteRows("ManagedWebDomainPolicy", (Criteria)null);
        }
        catch (final Exception e) {
            ManagedDomainFormBean.logger.log(Level.WARNING, "Exception occured in deleting existing url in managed domain", e);
        }
    }
    
    private void modifyURLDETAILS(final JSONArray jsonobj, final DataObject dataObject) throws JSONException, DataAccessException {
        final Object configid = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configid instanceof UniqueValueHolder)) {
            this.deleteExistingURLDetails(dataObject, configid);
        }
        for (int i = 0; i < jsonobj.length(); ++i) {
            final JSONObject json = (JSONObject)jsonobj.get(i);
            final String url = String.valueOf(json.get("URL"));
            if (url != null) {
                final Row Managedurlrow = new Row("ManagedWebDomainURLDetails");
                Managedurlrow.set("URL", (Object)url);
                final Row Manageddomaindetailrow = new Row("ManagedWebDomainPolicy");
                Manageddomaindetailrow.set("URL_DETAILS_ID", Managedurlrow.get("URL_DETAILS_ID"));
                Manageddomaindetailrow.set("CONFIG_DATA_ITEM_ID", configid);
                dataObject.addRow(Managedurlrow);
                dataObject.addRow(Manageddomaindetailrow);
            }
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        this.insertClonedConfigDataItem(configID, configDOFromDB, cloneConfigDO);
        final Object clonedConfigDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        ManagedDomainFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        final Object configDataItemId = configDOFromDB.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Iterator iterator = configDOFromDB.getRows("ManagedWebDomainPolicy", new Criteria(new Column("ManagedWebDomainPolicy", "CONFIG_DATA_ITEM_ID"), configDataItemId, 0));
        final List<Long> urlidList = new ArrayList<Long>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long urlId = (Long)row.get("URL_DETAILS_ID");
            urlidList.add(urlId);
        }
        final Long[] urlArray = new Long[urlidList.size()];
        urlidList.toArray(urlArray);
        final Iterator it = configDOFromDB.getRows("ManagedWebDomainURLDetails", new Criteria(new Column("ManagedWebDomainURLDetails", "URL_DETAILS_ID"), (Object)urlArray, 8));
        while (it.hasNext()) {
            final Row urlDetailsRow = it.next();
            final Row clonedRow = new Row("ManagedWebDomainURLDetails");
            final Object newPolicyId = this.cloneRow(urlDetailsRow, clonedRow, "URL_DETAILS_ID");
            final Row restrictionRow = new Row("ManagedWebDomainPolicy");
            restrictionRow.set("URL_DETAILS_ID", newPolicyId);
            restrictionRow.set("CONFIG_DATA_ITEM_ID", clonedConfigDataItemId);
            ManagedDomainFormBean.logger.log(Level.INFO, "clonedRow {0}", clonedRow);
            cloneConfigDO.addRow(clonedRow);
            cloneConfigDO.addRow(restrictionRow);
        }
        ManagedDomainFormBean.logger.log(Level.INFO, " cloneConfigDO {0}", cloneConfigDO);
    }
}
