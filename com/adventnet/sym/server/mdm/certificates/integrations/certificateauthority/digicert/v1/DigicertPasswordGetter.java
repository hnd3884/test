package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1;

import com.adventnet.sym.server.mdm.MDMProxy;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Set;
import javax.net.ssl.SSLContext;
import java.security.PrivateKey;
import mdm.integrations.certificateauthority.digicert.v1.DigicertClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.security.cert.Certificate;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordRequestStatus;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCaDbHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scepserver.digicert.DigicertScepServer;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import java.util.Map;
import java.util.List;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.certificates.scep.passwordgetter.ScepPasswordGetter;

public class DigicertPasswordGetter implements ScepPasswordGetter
{
    private final Logger logger;
    
    public DigicertPasswordGetter() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public Map<Long, PasswordResponse> getPasswordsFromScepServer(final ScepServer scepServer, final List<Long> resourceList) {
        try {
            final String certificateOID = ((DigicertScepServer)scepServer).certificateOID;
            final long raCertificateId = ((DigicertScepServer)scepServer).raCertificateId;
            final long csrId = ((DigicertScepServer)scepServer).csrId;
            this.logger.log(Level.INFO, "DigicertPasswordGetter: DATA-IN: Certificate OID: {0}, RA Cert Id: {1}, CSR Id: {2}", new Object[] { certificateOID, raCertificateId, csrId });
            final Certificate[] raCertificateChain = ThirdPartyCaDbHandler.getRaCertificate(scepServer.getCustomerId(), raCertificateId);
            final boolean isRaCertificateValid = CertificateUtil.isX509CertificateValid(raCertificateChain);
            if (!isRaCertificateValid) {
                this.logger.log(Level.WARNING, "DigicertPasswordGetter: RA certificate expired or not yet valid: RA Cert Id: {0}", new Object[] { raCertificateId });
                return this.constructMapWithFailureMessage(resourceList, PasswordRequestStatus.RA_CERT_EXPIRED);
            }
            final PrivateKey privateKey = ThirdPartyCaDbHandler.getPrivateKeyForCsr(scepServer.getCustomerId(), csrId);
            final SSLContext sslContext = ThirdPartyCAUtil.createCustomSslContext(null, raCertificateChain, privateKey);
            final Map<String, Object> proxyDetails = this.getProxyDetails();
            final Map<Long, Map<String, String>> eligibleUsers = ManagedUserHandler.getManagedUserDetailsForManagedDevices(resourceList);
            final List<Long> usersWithoutProperDetails = this.filterUsersWithoutProperDetails(eligibleUsers);
            eligibleUsers.keySet().removeAll(usersWithoutProperDetails);
            final Map<Long, PasswordResponse> usersWithResponse = new HashMap<Long, PasswordResponse>();
            final List<Long> unknownErrorUsersList = new ArrayList<Long>();
            if (eligibleUsers.size() > 0) {
                final Map<Long, Map<String, String>> resourceToDigicertResponseMap = DigicertClient.getInstance().createUserPasscodes((Map)proxyDetails, sslContext, (Map)eligibleUsers, certificateOID);
                if (resourceToDigicertResponseMap != null) {
                    usersWithResponse.putAll(this.getPasswordResponseMap(resourceToDigicertResponseMap));
                    eligibleUsers.keySet().removeAll(usersWithResponse.keySet());
                    final Set<Long> usersWithoutAnyResponse = eligibleUsers.keySet();
                    unknownErrorUsersList.addAll(usersWithoutAnyResponse);
                }
                else {
                    usersWithResponse.putAll(this.constructMapWithFailureMessage(new ArrayList<Long>(eligibleUsers.keySet()), PasswordRequestStatus.UNKNOWN_ERROR));
                }
            }
            final Map<Long, PasswordResponse> invalidSeatIdUsersMap = this.constructMapWithFailureMessage(usersWithoutProperDetails, PasswordRequestStatus.SEAT_ID_INVALID);
            usersWithResponse.putAll(invalidSeatIdUsersMap);
            final Map<Long, PasswordResponse> unknownErrorUsersMap = this.constructMapWithFailureMessage(unknownErrorUsersList, PasswordRequestStatus.UNKNOWN_ERROR);
            usersWithResponse.putAll(unknownErrorUsersMap);
            return usersWithResponse;
        }
        catch (final Exception e) {
            final String eMessage = "Exception while getting passwords from digicert scep: " + resourceList;
            this.logger.log(Level.SEVERE, eMessage, e);
            return null;
        }
    }
    
    private List<Long> filterUsersWithoutProperDetails(final Map<Long, Map<String, String>> userDetailsList) {
        final List<Long> usersWithoutProperDetails = new ArrayList<Long>();
        final Set<Map.Entry<Long, Map<String, String>>> userDetailsSet = userDetailsList.entrySet();
        for (final Map.Entry<Long, Map<String, String>> userDetail : userDetailsSet) {
            final Map<String, String> detail = userDetail.getValue();
            if (MDMStringUtils.isEmpty(detail.get("EMAIL_ADDRESS"))) {
                usersWithoutProperDetails.add(userDetail.getKey());
            }
        }
        return usersWithoutProperDetails;
    }
    
    private Map<Long, PasswordResponse> getPasswordResponseMap(final Map<Long, Map<String, String>> resourceToPasswordMap) {
        final HashMap<Long, PasswordResponse> resourceToPasswordResponseMap = new HashMap<Long, PasswordResponse>();
        for (final Map.Entry<Long, Map<String, String>> passwordEntry : resourceToPasswordMap.entrySet()) {
            final Long resource = passwordEntry.getKey();
            if (resource != null) {
                final Map<String, String> digicertPasswordResponse = passwordEntry.getValue();
                final PasswordResponse passwordResponse = DigicertResponseHandler.getPasswordResponse(digicertPasswordResponse);
                resourceToPasswordResponseMap.put(resource, passwordResponse);
            }
        }
        return resourceToPasswordResponseMap;
    }
    
    private Map<String, Object> getProxyDetails() throws Exception {
        this.logger.log(Level.INFO, "DigicertPasswordGetter: Getting Proxy details");
        final MDMProxy mdmProxy = ThirdPartyCAUtil.getMdmProxy();
        final Map<String, Object> proxyDetails = new HashMap<String, Object>();
        if (mdmProxy != null) {
            this.logger.log(Level.INFO, "DigicertPasswordGetter: Proxy details: {0}", new Object[] { mdmProxy.getProxyServerHost() });
            proxyDetails.put("PROXY_HOST", mdmProxy.getProxyServerHost());
            proxyDetails.put("PROXY_PORT", mdmProxy.getProxyServerPort());
            proxyDetails.put("PROXY_USER_NAME", mdmProxy.getProxyUsername());
            proxyDetails.put("PROXY_PASSWORD", mdmProxy.getProxyPassword());
        }
        else {
            proxyDetails.put("PROXY_HOST", "");
            proxyDetails.put("PROXY_PORT", "");
            proxyDetails.put("PROXY_USER_NAME", "");
            proxyDetails.put("PROXY_PASSWORD", "");
        }
        return proxyDetails;
    }
    
    private Map<Long, PasswordResponse> constructMapWithFailureMessage(final List<Long> resourceList, final PasswordRequestStatus status) {
        final Map<Long, PasswordResponse> passwordResponseMap = new HashMap<Long, PasswordResponse>();
        for (final Long resource : resourceList) {
            final PasswordResponse passwordResponse = new PasswordResponse(status, null);
            passwordResponseMap.put(resource, passwordResponse);
        }
        return passwordResponseMap;
    }
}
