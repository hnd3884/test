package com.adventnet.sym.server.mdm.certificates.scep.request;

import java.security.cert.CertificateEncodingException;
import java.io.InputStream;
import org.jscep.transaction.OperationFailureException;
import org.jscep.transaction.FailInfo;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.certificates.MdmCertAuthUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.response.ScepResponse;
import java.util.logging.Logger;

public class GetCaCertRequestHandler implements ScepRequestHandler
{
    private final Logger logger;
    final MdmScepRequest mdmScepRequest;
    
    public GetCaCertRequestHandler(final MdmScepRequest mdmScepRequest) {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        this.mdmScepRequest = mdmScepRequest;
    }
    
    @Override
    public ScepResponse handleRequest() throws OperationFailureException {
        try {
            this.logger.log(Level.INFO, "GetCaCertRequestHandler: Handling request for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            final String rootCaCertificatePath = MdmCertAuthUtil.Scep.getScepRootCACertificatePath(this.mdmScepRequest.getCustomerId());
            final InputStream certificateStream = ApiFactoryProvider.getFileAccessAPI().readFile(rootCaCertificatePath);
            final X509Certificate x509Certificate = (X509Certificate)CertificateUtil.convertInputStreamToX509CertificateChain(certificateStream)[0];
            final ScepResponse scepResponse = this.getCaCertificateResponse(x509Certificate);
            this.logger.log(Level.INFO, "GetCaCertRequestHandler: CA certificate obtained for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            return scepResponse;
        }
        catch (final Exception e) {
            final String exMessage = "GetCaCertRequestHandler: Exception while returning ca cert " + this.mdmScepRequest.getEnrollmentRequestId();
            this.logger.log(Level.SEVERE, exMessage, e);
            throw new OperationFailureException(FailInfo.badRequest);
        }
    }
    
    private ScepResponse getCaCertificateResponse(final X509Certificate certificate) throws CertificateEncodingException {
        final ScepResponse scepResponse = new ScepResponse();
        scepResponse.setContentType("application/x-x509-ca-cert");
        final byte[] bytes = certificate.getEncoded();
        scepResponse.setResponse(bytes);
        return scepResponse;
    }
}
