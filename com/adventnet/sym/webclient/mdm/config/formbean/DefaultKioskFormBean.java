package com.adventnet.sym.webclient.mdm.config.formbean;

import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.webclient.formbean.MDMScreenLayoutFormBean;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class DefaultKioskFormBean extends MDMDefaultFormBean
{
    protected void addScreenLayout(final DataObject dataObject, final JSONObject dynaJSON, final JSONObject multipleConfigForm) throws Exception {
        if (dynaJSON.has("ScreenLayout") && dynaJSON.getJSONObject("ScreenLayout").length() > 1) {
            new MDMScreenLayoutFormBean().addScreenLayout(dynaJSON, dataObject, multipleConfigForm);
        }
        else {
            dataObject.deleteRows("ScreenLayoutSettings", (Criteria)null);
        }
    }
    
    protected void addWebClipsRel(final DataObject dataObject, final JSONObject dynaJSON, final Object configDataItemID) throws Exception {
        if (dataObject.containsTable("WebClipToConfigRel") && !(configDataItemID instanceof UniqueValueHolder)) {
            final Criteria criteria = new Criteria(new Column("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID"), configDataItemID, 0);
            dataObject.deleteRows("WebClipToConfigRel", criteria);
        }
        if (dynaJSON.has("WebClipPolicies")) {
            final JSONArray webClipPolicy = dynaJSON.getJSONArray("WebClipPolicies");
            for (int i = 0; i < webClipPolicy.length(); ++i) {
                final JSONObject webClipJson = webClipPolicy.getJSONObject(i);
                final Long webClipId = webClipJson.getLong("WEBCLIP_POLICY_ID");
                final Row webClipRow = new Row("WebClipToConfigRel");
                webClipRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                webClipRow.set("WEBCLIP_POLICY_ID", (Object)webClipId);
                dataObject.addRow(webClipRow);
            }
        }
    }
}
