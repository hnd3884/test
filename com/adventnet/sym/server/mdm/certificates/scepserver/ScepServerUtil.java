package com.adventnet.sym.server.mdm.certificates.scepserver;

import java.util.HashMap;
import java.util.Map;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScepServerUtil
{
    private static final Logger LOGGER;
    
    private ScepServerUtil() {
    }
    
    public static ScepServerType getServerType(final int serverType) {
        ScepServerUtil.LOGGER.log(Level.INFO, "ScepServerUtil: Getting server type for : {0}", new Object[] { serverType });
        if (serverType == ScepServerType.GENERIC.type) {
            return ScepServerType.GENERIC;
        }
        if (serverType == ScepServerType.DIGICERT.type) {
            return ScepServerType.DIGICERT;
        }
        if (serverType == ScepServerType.ADCS.type) {
            return ScepServerType.ADCS;
        }
        if (serverType == ScepServerType.EJBCA.type) {
            return ScepServerType.EJBCA;
        }
        throw new APIHTTPException("COM0005", new Object[0]);
    }
    
    public static Map<Integer, String> getScepServersAndTypes() {
        final HashMap<Integer, String> scepServersAndTypes = new HashMap<Integer, String>();
        scepServersAndTypes.put(ScepServerType.GENERIC.type, "GENERIC");
        scepServersAndTypes.put(ScepServerType.DIGICERT.type, "DIGICERT");
        scepServersAndTypes.put(ScepServerType.ADCS.type, "ADCS");
        scepServersAndTypes.put(ScepServerType.EJBCA.type, "EJBCA");
        return scepServersAndTypes;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
