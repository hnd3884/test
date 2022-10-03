package com.me.mdm.agent.servlets.dep;

import java.security.cert.X509Certificate;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.dd.plist.Base64;
import com.me.mdm.certificate.CertificateHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONArray;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class IOSACCertificateServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    
    public IOSACCertificateServlet() {
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
            final JSONArray jarray = new JSONArray();
            if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() != 2) {
                final X509Certificate certificate = CertificateHandler.getInstance().getAppropriateCertificate();
                if (certificate != null) {
                    final byte[] encoded = certificate.getEncoded();
                    final String encodedBytes = Base64.encodeBytes(encoded);
                    jarray.put((Object)encodedBytes);
                }
            }
            response.setContentType("application/json;charset=UTF8");
            DMSecurityLogger.info(this.logger, "IOSACCertificateServlet", "doPost", "IOSACCertificateServlet Response Data: {0}", (Object)jarray.toString());
            response.getWriter().println(jarray.toString());
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in IOSACCertificateServlet.. ", exp);
        }
    }
}
