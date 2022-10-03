package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.scepserver.digicert.DigicertScepServerHandlerImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScepServerHandlerFactory
{
    private static final Logger LOGGER;
    
    private ScepServerHandlerFactory() {
    }
    
    public static ScepServerHandler getScepServerHandler(final ScepServerType serverType) {
        ScepServerHandlerFactory.LOGGER.log(Level.INFO, "ScepServerHandler: Getting SCEP server handler for type: {0}", new Object[] { serverType.type });
        if (serverType == ScepServerType.DIGICERT) {
            return new DigicertScepServerHandlerImpl();
        }
        throw new APIHTTPException("COM0005", new Object[] { "Server type" });
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
