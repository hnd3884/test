package com.me.mdm.webclient.formbean;

import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MDMWebClipsFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                Object webClipPolicyId = null;
                if (dynaForm.has("WEBCLIP_POLICY_ID") && !dynaForm.has("WEBCLIP_URL")) {
                    webClipPolicyId = dynaForm.getLong("WEBCLIP_POLICY_ID");
                }
                else {
                    dynaForm.put("WEBCLIP_NAME", (Object)dynaForm.getString("WEBCLIP_LABEL"));
                    Row webClipRow;
                    if (dataObject.containsTable("WebClipPolicies")) {
                        webClipRow = dataObject.getRow("WebClipPolicies");
                        new WebClipsFacade().modifyWebClipRow(webClipRow, dynaForm);
                        dataObject.updateRow(webClipRow);
                    }
                    else {
                        final DataObject webCLipAddObject = (DataObject)new WritableDataObject();
                        webClipRow = new WebClipsFacade().addWebClipPolicies(webCLipAddObject, dynaForm);
                        dataObject.updateBlindly(webClipRow);
                    }
                    webClipPolicyId = webClipRow.get("WEBCLIP_POLICY_ID");
                }
                dynaForm.put("WEBCLIP_POLICY_ID", webClipPolicyId);
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "WebClipToConfigRel", "CONFIG_DATA_ITEM_ID");
            }
        }
        catch (final Exception e) {
            throw new SyMException();
        }
    }
}
