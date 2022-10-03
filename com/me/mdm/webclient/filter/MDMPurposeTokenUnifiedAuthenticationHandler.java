package com.me.mdm.webclient.filter;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.ServletException;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMPurposeTokenUnifiedAuthenticationHandler extends MDMUserUnifiedAuthenticationHandler
{
    public Logger deviceDataLog;
    
    public MDMPurposeTokenUnifiedAuthenticationHandler() {
        this.deviceDataLog = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    @Override
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        DeviceRequest devicerequest = null;
        try {
            devicerequest = AuthenticationHandlerUtil.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            final String encAPIKey = request.getParameter("encapiKey");
            if (!MDMStringUtils.isEmpty(encAPIKey)) {
                final List<Integer> purposes = MDMApiFactoryProvider.getMDMUtilAPI().getAllowedPurpose(request.getRequestURI());
                for (final Integer purpose : purposes) {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("RECEIVED_TOKEN", (Object)encAPIKey);
                    jsonObject.put("PURPOSE_KEY", (Object)purpose);
                    jsonObject.put("CUSTOMER_ID", (Object)this.getCustomerIDForPurposeToken(request, devicerequest));
                    if (MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().validateAPIKey(jsonObject)) {
                        return true;
                    }
                }
                final Long erid = Long.valueOf(request.getParameter("erid"));
                if (erid != -1L) {
                    final JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                    jsonObject2.put("encapiKey", (Object)encAPIKey);
                    if (MDMDeviceAPIKeyGenerator.getInstance().validateAPIKey(jsonObject2)) {
                        return true;
                    }
                }
            }
        }
        catch (final Exception e) {
            this.deviceDataLog.log(Level.SEVERE, "Exception in Unified PurposeToken authentication: ", e);
            try {
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException ex) {
                this.deviceDataLog.log(Level.SEVERE, "IOException Occurred :", e);
            }
        }
        this.deviceDataLog.log(Level.INFO, "UnAuthenticated request in Unified Device authentication: {0}, {1}", new Object[] { devicerequest, request.getRequestURI() });
        throw new IAMSecurityException("UNAUTHORISED");
    }
    
    protected Long getCustomerIDForPurposeToken(final HttpServletRequest request, final DeviceRequest deviceRequest) throws Exception {
        Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        if (customerID == null) {
            final String encAPIKey = request.getParameter("encapiKey");
            customerID = (Long)DBUtil.getValueFromDB("MDMPurposeToken", "PURPOSE_TOKEN", (Object)encAPIKey, "CUSTOMER_ID");
        }
        return customerID;
    }
}
