package com.me.mdm.webclient.formbean;

import com.me.mdm.server.profiles.font.FontDetailsHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class FontPayloadFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                Object fontId = -1;
                if (dynaForm.has("FONT_ID")) {
                    fontId = dynaForm.getLong("FONT_ID");
                }
                else {
                    fontId = this.getFontHandler().addFontDetails(dynaForm, dataObject);
                }
                dynaForm.put("FONT_ID", fontId);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "CfgDataItemToFontRel", "CONFIG_DATA_ITEM_ID");
            }
        }
        catch (final PayloadException e) {
            throw e;
        }
        catch (final Exception e2) {
            FontPayloadFormBean.logger.log(Level.SEVERE, "Exception in mac login window", e2);
            throw new SyMException();
        }
    }
    
    protected FontDetailsHandler getFontHandler() {
        return new FontDetailsHandler();
    }
}
