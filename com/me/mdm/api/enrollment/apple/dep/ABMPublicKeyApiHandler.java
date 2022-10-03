package com.me.mdm.api.enrollment.apple.dep;

import java.io.OutputStream;
import java.io.InputStream;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.adep.ABMPublicKeyFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ABMPublicKeyApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String pemPath = ABMPublicKeyFacade.getInstance().getPublicKeyPathForDEPToken(apiRequest.toJSONObject());
            apiRequest.httpServletResponse.setContentType("application/x-pem-file");
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=MEMDMCertificate.pem");
            final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(pemPath);
            int read = 0;
            final byte[] bytes = new byte[4096];
            final OutputStream os = (OutputStream)apiRequest.httpServletResponse.getOutputStream();
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
            os.close();
            is.close();
            return null;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while downloading PEM through Api..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while downloading PEM through Api..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
