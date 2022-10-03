package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.adventnet.sym.server.mdm.certificates.scepserver.digicert.DigicertScepServer;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.sym.server.mdm.certificates.scepserver.digicert.DigicertScepServerMapper;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ScepServerMapper
{
    private static final Logger LOGGER;
    
    private ScepServerMapper() {
    }
    
    public static ScepServer convertJson(final long customerId, final JSONObject msgBody) throws DataAccessException {
        ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: Converting incoming JSON for customer: {0}", new Object[] { customerId });
        final ScepServerType scepServerType = ScepServerUtil.getServerType(msgBody.getInt("type"));
        ScepServer scepServer;
        if (scepServerType == ScepServerType.DIGICERT) {
            ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: Getting Digicert server: {0}", new Object[] { customerId });
            scepServer = DigicertScepServerMapper.convertJson(msgBody);
        }
        else {
            scepServer = new ScepServer();
        }
        scepServer.setServerName(msgBody.getString("server_name"));
        scepServer.setServerUrl(msgBody.getString("url"));
        scepServer.setServerType(scepServerType);
        scepServer.setCustomerId(customerId);
        CredentialCertificate caCertificate = null;
        if (msgBody.has("ca_certificate_id") && msgBody.optLong("ca_certificate_id") != 0L) {
            ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: Adding CA cert details: {0}", new Object[] { msgBody.getLong("ca_certificate_id") });
            caCertificate = ProfileCertificateUtil.getCACertDetails(customerId, msgBody.getLong("ca_certificate_id"));
        }
        scepServer.setCertificate(caCertificate);
        return scepServer;
    }
    
    public static JSONObject toJson(final ScepServer scepServer) {
        ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: Converting SCEP server: {0} to json", new Object[] { scepServer.getScepServerId() });
        final JSONObject json = new JSONObject();
        json.put("server_id", (Object)scepServer.getScepServerId());
        json.put("server_name", (Object)scepServer.getServerName());
        json.put("type", scepServer.getServerType().type);
        json.put("url", (Object)scepServer.getServerUrl());
        if (scepServer.getCertificate() != null) {
            ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: CA certificate {0} present for SCEP server: {1}", new Object[] { scepServer.getCertificate(), scepServer.getScepServerId() });
            json.put("ca_certificate_id", (Object)scepServer.getCertificate().getCertificateId());
            json.put("ca_finger_print", (Object)scepServer.getCertificate().getCertificateThumbprint());
        }
        if (scepServer.getServerType() == ScepServerType.DIGICERT) {
            ScepServerMapper.LOGGER.log(Level.INFO, "ScepServerMapper: Converting Digicert SCEP server: {0}", new Object[] { scepServer.getScepServerId() });
            DigicertScepServerMapper.toJson((DigicertScepServer)scepServer, json);
        }
        return json;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
