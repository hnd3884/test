package com.adventnet.sym.server.mdm.certificates.scep.request;

import org.jscep.transaction.OperationFailureException;
import org.jscep.transaction.FailInfo;
import java.util.logging.Level;
import org.jscep.transport.request.Operation;
import java.util.logging.Logger;

public class ScepServerFactory
{
    private static final Logger LOGGER;
    
    public static ScepRequestHandler getRequestHandler(final MdmScepRequest scepRequest) throws Exception {
        if (scepRequest.getOperation() == Operation.GET_CA_CAPS) {
            ScepServerFactory.LOGGER.log(Level.INFO, "ScepServerFactory: Operation is GET_CA_CAPS for {0}", new Object[] { scepRequest.getEnrollmentRequestId() });
            return new GetCaCapsRequestHandler(scepRequest);
        }
        if (scepRequest.getOperation() == Operation.GET_CA_CERT) {
            ScepServerFactory.LOGGER.log(Level.INFO, "ScepServerFactory: Operation is GET_CA_CERT for {0}", new Object[] { scepRequest.getEnrollmentRequestId() });
            return new GetCaCertRequestHandler(scepRequest);
        }
        if (scepRequest.getOperation() == Operation.PKI_OPERATION) {
            ScepServerFactory.LOGGER.log(Level.INFO, "ScepServerFactory: Operation is PKI_OPERATION for {0}", new Object[] { scepRequest.getEnrollmentRequestId() });
            return new PkiOperationRequestHandler(scepRequest);
        }
        ScepServerFactory.LOGGER.log(Level.INFO, "ScepServerFactory: Operation unknown for {0}", new Object[] { scepRequest.getEnrollmentRequestId() });
        throw new OperationFailureException(FailInfo.badRequest);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
    }
}
