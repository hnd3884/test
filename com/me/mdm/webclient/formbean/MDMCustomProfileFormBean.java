package com.me.mdm.webclient.formbean;

import com.me.mdm.server.profiles.CustomProfileHandler;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.payload.PayloadException;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MDMCustomProfileFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                final JSONObject customProfileJSON = this.getCustomProfileJSON(dynaForm);
                final List<String> payloadList = this.getPayloadList(dynaForm);
                final Object customProfileId = this.getCustomProfileHandler().addCustomProfile(customProfileJSON, dataObject, payloadList);
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                dynaForm.put("CUSTOM_PROFILE_ID", customProfileId);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "CustomProfileToCfgDataItem", "CONFIG_DATA_ITEM_ID");
            }
        }
        catch (final JSONException e) {
            MDMCustomProfileFormBean.logger.log(Level.SEVERE, "JSON Exception", (Throwable)e);
            throw new SyMException();
        }
        catch (final PayloadException e2) {
            throw e2;
        }
        catch (final DataAccessException e3) {
            MDMCustomProfileFormBean.logger.log(Level.SEVERE, "Exception while getting file details", (Throwable)e3);
            throw new SyMException();
        }
        catch (final Exception e4) {
            MDMCustomProfileFormBean.logger.log(Level.SEVERE, "Exception while modifying config data item", e4);
            throw new SyMException();
        }
    }
    
    protected JSONObject getCustomProfileJSON(final JSONObject dynaForm) throws JSONException {
        final JSONObject customProfileJSON = new JSONObject();
        final String filePath = dynaForm.optString("CUSTOM_PROFILE_PATH");
        final Long customerId = dynaForm.optLong("CUSTOMER_ID");
        customProfileJSON.put("CUSTOM_PROFILE_PATH", (Object)filePath);
        customProfileJSON.put("CUSTOMER_ID", (Object)customerId);
        return customProfileJSON;
    }
    
    protected List<String> getPayloadList(final JSONObject dynaform) throws PayloadException {
        return new ArrayList<String>();
    }
    
    protected CustomProfileHandler getCustomProfileHandler() {
        return new CustomProfileHandler();
    }
}
