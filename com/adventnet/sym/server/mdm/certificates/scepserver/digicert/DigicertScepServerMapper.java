package com.adventnet.sym.server.mdm.certificates.scepserver.digicert;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DigicertScepServerMapper
{
    private static final Logger LOGGER;
    
    private DigicertScepServerMapper() {
    }
    
    public static DigicertScepServer convertJson(final JSONObject json) {
        DigicertScepServerMapper.LOGGER.log(Level.INFO, "DigicertScepServerMapper: Converting json to Digicert SCEP server: {0}", new Object[] { json.getString("profile_oid") });
        final long raCertId = json.getLong("ra_certificate_id");
        final long csrId = json.getLong("csr_id");
        final String certificateOID = json.getString("profile_oid");
        return new DigicertScepServer(raCertId, csrId, certificateOID);
    }
    
    public static void toJson(final DigicertScepServer scepServer, final JSONObject json) {
        DigicertScepServerMapper.LOGGER.log(Level.INFO, "DigicertScepServerMapper: Converting Digicert SCEP server: {0} to json", new Object[] { scepServer.getScepServerId() });
        json.put("ra_certificate_id", scepServer.raCertificateId);
        json.put("profile_oid", (Object)scepServer.certificateOID);
        json.put("csr_id", scepServer.csrId);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
