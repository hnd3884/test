package com.me.mdm.agent.servlets.ios;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.webclient.mdm.IOSCheckInServlet;

public class IOSLegacyCheckInServlet extends IOSCheckInServlet
{
    @Override
    protected DeviceRequest validateRequest(final HttpServletRequest request, final HttpServletResponse response) {
        DeviceRequest devicerequest = null;
        try {
            Boolean allowRequest = Boolean.FALSE;
            String sUDID = null;
            Long erid = null;
            Long customerId = null;
            if (request.getContentType() != null && request.getContentType().contains("application/x-apple-aspen-mdm")) {
                devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
                final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist((String)devicerequest.deviceRequestData);
                sUDID = hashPlist.get("UDID");
                erid = Long.valueOf(request.getParameter("erid"));
                final Long custIDFromParam = Long.valueOf(request.getParameter("customerId"));
                customerId = MDMiOSEntrollmentUtil.getInstance().optCustomerIdForErid(erid, custIDFromParam);
                final JSONObject json = new JSONObject();
                json.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                json.put("CUSTOMER_ID", (Object)customerId);
                json.put("UDID", (Object)sUDID);
                allowRequest = MDMiOSEntrollmentUtil.getInstance().validateLegacyServletRequest(json);
            }
            if (!allowRequest) {
                this.deviceDataLog.log(Level.SEVERE, "Forbidden request from agent : {0} URI :  {1} PARAMS : {2}", new Object[] { devicerequest, SecurityUtil.getRequestPath(request), request.getQueryString() });
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
                throw new RuntimeException("Improper Authentication! ERID, UDID & Customer ID doesnt match");
            }
        }
        catch (final Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("Improper Authentication")) {
                throw (RuntimeException)e;
            }
            this.deviceDataLog.log(Level.SEVERE, "Exception in IOS Legacy CheckIn servlet when checking a request ", e);
        }
        return devicerequest;
    }
}
