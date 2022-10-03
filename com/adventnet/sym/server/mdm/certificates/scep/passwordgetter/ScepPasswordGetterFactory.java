package com.adventnet.sym.server.mdm.certificates.scep.passwordgetter;

import java.util.NoSuchElementException;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1.DigicertPasswordGetter;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.adcs.AdcsPasswordGetter;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import java.util.logging.Logger;

public class ScepPasswordGetterFactory
{
    private static final Logger LOGGER;
    
    private ScepPasswordGetterFactory() {
    }
    
    public static ScepPasswordGetter getScepPasswordGetter(final ScepServerType serverType) {
        ScepPasswordGetterFactory.LOGGER.log(Level.INFO, "ScepPasswordGetter: The provided server type is {0}", serverType);
        if (serverType == ScepServerType.ADCS) {
            return new AdcsPasswordGetter();
        }
        if (serverType == ScepServerType.DIGICERT) {
            return new DigicertPasswordGetter();
        }
        throw new NoSuchElementException("Server type :" + serverType.type);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
