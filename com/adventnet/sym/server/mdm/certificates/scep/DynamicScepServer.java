package com.adventnet.sym.server.mdm.certificates.scep;

import java.util.Map;
import com.adventnet.sym.server.mdm.certificates.scep.passwordgetter.ScepPasswordGetter;
import java.util.Collection;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCaDbHandler;
import com.adventnet.sym.server.mdm.certificates.scep.passwordgetter.ScepPasswordGetterFactory;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerManager;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scepserver.adcs.AdcsScepServer;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.logging.Logger;

public class DynamicScepServer
{
    private static final Logger LOGGER;
    
    private DynamicScepServer() {
    }
    
    public static boolean isPasscodeRetrievalAllowed(final ScepServer scepServer) {
        if (scepServer.getServerType() == ScepServerType.ADCS) {
            final int challengeType = ((AdcsScepServer)scepServer).challengeType;
            DynamicScepServer.LOGGER.log(Level.INFO, "SCEP server: {0}, challenge type: {1}", new Object[] { scepServer.getScepServerId(), challengeType });
            return challengeType == 2;
        }
        return scepServer.getServerType() == ScepServerType.DIGICERT;
    }
    
    public static ScepServer getScepServerForScepId(final long scepId) throws DataAccessException {
        final DataObject dataObject = DynamicScepServerDB.getScepServerDetailsForScepConfig(scepId);
        DynamicScepServer.LOGGER.log(Level.INFO, "Getting Scep server for Scep id: {0}", new Object[] { scepId });
        if (!dataObject.isEmpty()) {
            final Row scepServerRow = dataObject.getRow("SCEPServers");
            final long serverId = (long)scepServerRow.get("SERVER_ID");
            final int serverType = (int)scepServerRow.get("TYPE");
            final long customerId = (long)scepServerRow.get("CUSTOMER_ID");
            DynamicScepServer.LOGGER.log(Level.INFO, " SCEP server details: Server Id: {0}, Server type: {1}", new Object[] { serverId, serverType });
            ScepServer scepServer;
            if (serverType == ScepServerType.ADCS.type) {
                scepServer = getAdcsScepServer(dataObject);
                scepServer.setScepServerId(serverId);
                scepServer.setCustomerId(customerId);
                scepServer.setServerType(ScepServerType.ADCS);
                scepServer.setServerName((String)scepServerRow.get("SERVER_NAME"));
                scepServer.setServerUrl((String)scepServerRow.get("URL"));
                if (scepServerRow.get("CA_CERTIFICATE_ID") != null) {
                    final long caCertId = (long)scepServerRow.get("CA_CERTIFICATE_ID");
                    final CredentialCertificate certificate = ProfileCertificateUtil.getCACertDetails(customerId, caCertId);
                    scepServer.setCertificate(certificate);
                }
            }
            else {
                scepServer = ScepServerManager.getScepServer(customerId, serverId);
            }
            return scepServer;
        }
        DynamicScepServer.LOGGER.log(Level.INFO, "No SCEP server found for SCEP id: {0}", new Object[] { scepId });
        return null;
    }
    
    private static ScepServer getAdcsScepServer(final DataObject dataObject) throws DataAccessException {
        DynamicScepServer.LOGGER.log(Level.INFO, " Getting ADCS server details");
        final Row scepConfigRow = dataObject.getRow("SCEPConfigurations");
        AdcsScepServer scepServer;
        if ((int)scepConfigRow.get("CHALLENGE_TYPE") == 2) {
            DynamicScepServer.LOGGER.log(Level.INFO, " Challenge type: {0}", new Object[] { 2 });
            scepServer = new AdcsScepServer(2);
            final Row adcsScepRow = dataObject.getRow("ScepDyChallengeCredentials");
            scepServer.setAdminUrl((String)adcsScepRow.get("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL"));
            scepServer.setAdminUsername((String)adcsScepRow.get("SCEP_ADMIN_CHALLENGE_USERNAME"));
            scepServer.setAdminPassword((String)adcsScepRow.get("SCEP_ADMIN_CHALLENGE_PASSWORD"));
        }
        else {
            DynamicScepServer.LOGGER.log(Level.INFO, " Challenge type: {0}", new Object[] { 1 });
            scepServer = new AdcsScepServer(1);
        }
        return scepServer;
    }
    
    public static List<Long> obtainAndStorePasswords(final Long collectionId, final List<Long> resources, final Long scepId, final ScepServer scepServer) throws DataAccessException {
        final List<Long> notApplicableDevices = new ArrayList<Long>();
        final ScepPasswordGetter passwordGetter = ScepPasswordGetterFactory.getScepPasswordGetter(scepServer.getServerType());
        final Map<Long, PasswordResponse> passwordResponseMap = passwordGetter.getPasswordsFromScepServer(scepServer, resources);
        if (passwordResponseMap != null) {
            DynamicScepServer.LOGGER.log(Level.INFO, "Passwords obtained for scep : {0}", new Object[] { scepId });
            ThirdPartyCaDbHandler.addOrUpdateUserPasscodes(passwordResponseMap, scepId);
            if (!passwordResponseMap.isEmpty()) {
                DynamicScepServer.LOGGER.log(Level.INFO, "Some problem occurred for particular resources : {0}", new Object[] { scepId });
                DynamicScepRemarks.updateCustomRemarks(collectionId, passwordResponseMap);
                notApplicableDevices.addAll(passwordResponseMap.keySet());
                return notApplicableDevices;
            }
        }
        else {
            DynamicScepServer.LOGGER.log(Level.INFO, "Some problem occurred overall while getting passwords for scep : {0}", new Object[] { scepId });
            DynamicScepRemarks.updateFailureRemarks(resources, collectionId, PasswordRequestStatus.UNKNOWN_ERROR);
            notApplicableDevices.addAll(resources);
        }
        return notApplicableDevices;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
