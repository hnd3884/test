package com.me.mdm.agent.servlets.ios;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.webclient.filter.AuthenticationHandlerUtil;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.webclient.mdm.IOSServerServlet;

public class MacServerServlet extends IOSServerServlet
{
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.className = "MACServerServlet";
        this.handleMultiUserServerServlet(request, response, deviceRequest);
    }
    
    public void handleMultiUserServerServlet(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) {
        MacServerServlet.logger.log(Level.INFO, "{0} => (PUT) Received request from APPLE ", this.className);
        MacServerServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "{0} => (PUT) Received request from APPLE ", this.className);
        String strData = null;
        HashMap requestedHashPlist = null;
        String strUDID = null;
        Long customerID = null;
        try {
            if (deviceRequest == null) {
                MacServerServlet.logger.log(Level.WARNING, "Device Request null in {0}", MacServerServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, MacServerServlet.logger);
            }
            strData = (String)deviceRequest.deviceRequestData;
            MacServerServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "{1} => (PUT) Received data : {0}", new Object[] { strData, this.className });
            strData = AuthenticationHandlerUtil.sanitizeXML(strData);
            requestedHashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
            strUDID = requestedHashPlist.get("UDID");
        }
        catch (final Exception ex) {
            MacServerServlet.logger.log(Level.WARNING, "MACServerServlet => (PUT) Exception occurred : {0}", ex);
        }
        if (requestedHashPlist != null) {
            final String strStatus = requestedHashPlist.get("Status");
            if (strStatus != null && strStatus.equals("Idle") && requestedHashPlist.containsKey("UserID")) {
                try {
                    final String sEnrollmentRequestIDStr = request.getParameter("erid");
                    Long enrollmentRequestId = null;
                    if (sEnrollmentRequestIDStr != null) {
                        enrollmentRequestId = Long.parseLong(sEnrollmentRequestIDStr);
                    }
                    customerID = MDMTermsHandler.getInstance().getCustomerIdforErid(enrollmentRequestId);
                    final NSDictionary idleData = (NSDictionary)DMSecurityUtil.parsePropertyList(strData.getBytes("UTF-8"));
                    idleData.put("CommandUUID", (Object)"ManagedUserLoginUpdate");
                    MacServerServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "{1} => (PUT)  User Idle data received : {0}", new Object[] { strData, this.className });
                    this.addToQueue(customerID, strUDID, idleData.toXMLPropertyList(), 107);
                }
                catch (final Exception ex2) {
                    MacServerServlet.logger.log(Level.WARNING, "MACServerServlet => (PUT) Exception occurred while adding UserIdle to Queue : {0}", ex2);
                }
            }
            else {
                super.processRequest(request, response, deviceRequest);
            }
        }
        else {
            super.processRequest(request, response, deviceRequest);
        }
    }
}
