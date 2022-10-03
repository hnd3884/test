package com.adventnet.sym.server.mdm.certificates.scep.request;

import java.util.Iterator;
import java.util.EnumSet;
import org.jscep.transport.response.Capability;
import java.util.Set;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.response.ScepResponse;
import java.util.logging.Logger;

public class GetCaCapsRequestHandler implements ScepRequestHandler
{
    private final Logger logger;
    final MdmScepRequest mdmScepRequest;
    
    public GetCaCapsRequestHandler(final MdmScepRequest mdmScepRequest) {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        this.mdmScepRequest = mdmScepRequest;
    }
    
    @Override
    public ScepResponse handleRequest() {
        final Set<Capability> capabilities = this.getCACapabilities();
        this.logger.log(Level.INFO, "GetCaCapsRequestHandler: Returning CA capabilities {0} for {1}", new Object[] { capabilities, this.mdmScepRequest.getEnrollmentRequestId() });
        return this.getCACapsResponse(capabilities);
    }
    
    public Set<Capability> getCACapabilities() {
        return EnumSet.of(Capability.AES, Capability.TRIPLE_DES, Capability.SHA_1, Capability.SHA_256, Capability.SHA_512, Capability.POST_PKI_OPERATION, Capability.RENEWAL, Capability.SCEP_STANDARD);
    }
    
    public ScepResponse getCACapsResponse(final Set<Capability> capabilities) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Capability capability : capabilities) {
            stringBuilder.append(capability.toString());
            stringBuilder.append("\n");
        }
        final ScepResponse scepResponse = new ScepResponse();
        scepResponse.setContentType("text/plain");
        scepResponse.setResponse(stringBuilder.toString().getBytes());
        return scepResponse;
    }
}
