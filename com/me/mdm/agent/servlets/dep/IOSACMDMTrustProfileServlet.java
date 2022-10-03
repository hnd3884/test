package com.me.mdm.agent.servlets.dep;

import java.io.OutputStream;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.ConfigurationPayload;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.ios.payload.CertificatePayload;
import com.adventnet.sym.server.mdm.ios.payload.transform.PayloadIdentifierConstants;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class IOSACMDMTrustProfileServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    
    public IOSACMDMTrustProfileServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            ConfigurationPayload cfgPayload = null;
            NSObject certificateNSObject = null;
            NSArray nsarray = null;
            final String organizationName = "MDM";
            final CertificatePayload hostCertPayload = new CertificatePayload(1, organizationName, PayloadIdentifierConstants.MDM_DEP_TRUST_ROOT_CERTIFICATE_IDENTIFIER, "MDM Trust Root Certificate");
            hostCertPayload.setPayloadType("com.apple.security.root");
            final String serverCertificateFilePath = ApiFactoryProvider.getUtilAccessAPI().getTrustRootCertificateFilePath();
            hostCertPayload.setPayloadContent(serverCertificateFilePath);
            hostCertPayload.getPayloadDict().put("PayloadDisplayName", (Object)"MDM Trust Root Certificate");
            certificateNSObject = (NSObject)hostCertPayload.getPayloadDict();
            nsarray = new NSArray(new NSObject[] { certificateNSObject });
            cfgPayload = new ConfigurationPayload(1, organizationName, PayloadIdentifierConstants.MDM_DEP_TRUST_PROFILE_IDENTIFIER, "MDM Trusted Profile");
            cfgPayload.setPayloadContent(nsarray);
            final String toXMLPropertyList = cfgPayload.getPayloadDict().toXMLPropertyList();
            response.setContentType("application/x-apple-aspen-config");
            response.setHeader("Content-Disposition", "attachment;filename=mdm.mobileconfig");
            final OutputStream os = (OutputStream)response.getOutputStream();
            os.write(toXMLPropertyList.getBytes());
            os.flush();
            os.close();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in IOSACMDMTrustProfileServlet..", exp);
        }
    }
}
