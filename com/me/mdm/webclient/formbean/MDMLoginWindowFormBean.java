package com.me.mdm.webclient.formbean;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class MDMLoginWindowFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "MacLoginWindow", "CONFIG_DATA_ITEM_ID");
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "MacLoginWindowSettings", "CONFIG_DATA_ITEM_ID");
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "MacScreenSaverSettings", "CONFIG_DATA_ITEM_ID");
            }
        }
        catch (final Exception e) {
            MDMLoginWindowFormBean.logger.log(Level.SEVERE, "Exception while adding the lock screen information", e);
            throw new PayloadException("PAY0003");
        }
    }
}
