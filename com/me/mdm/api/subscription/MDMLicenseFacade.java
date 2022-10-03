package com.me.mdm.api.subscription;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;
import org.json.JSONObject;

public class MDMLicenseFacade
{
    public void moveToFreeEdition(final JSONObject temp) {
        final Logger logger = Logger.getLogger(MDMLicenseFacade.class.getName());
        try {
            final boolean freeEditionDetailCleared = MDMApiFactoryProvider.getMDMUtilAPI().isClearedDetailsForFreeEdition(String.valueOf(temp.get("mobile_device_ids")));
            logger.log(Level.INFO, "Free Edition Details Cleared ?  : ", freeEditionDetailCleared);
            if (freeEditionDetailCleared) {
                SyMUtil.updateSyMParameter("free_edition_computer_defined", "true");
                LicenseProvider.getInstance().setFreeEditionConfiguredStatus();
            }
            final String eventRemarks = "dc.admin.license.event.change";
            final String remarksArg = I18N.getMsg("dc.license.edtion.free_edition", new Object[0]);
            DCEventLogUtil.getInstance().addEvent(121, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, eventRemarks, (Object)remarksArg, true, (Long)null);
        }
        catch (final JSONException e) {
            logger.log(Level.SEVERE, "Exception in getting mobileDeviceIds", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e2) {
            logger.log(Level.SEVERE, "Exception in adding event for moving the customer to free edition", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
