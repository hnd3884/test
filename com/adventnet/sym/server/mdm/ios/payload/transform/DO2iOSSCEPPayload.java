package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.inv.CertificateConstants;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import java.util.Set;
import org.apache.commons.collections.MultiMap;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.Iterator;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.NSSet;
import java.util.List;
import com.dd.plist.NSArray;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import org.apache.commons.collections.MultiHashMap;
import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.certificates.scep.DynamicScepServer;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSSCEPPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2iOSSCEPPayload implements DO2Payload
{
    private Logger logger;
    private static final String KEYTYPE = "RSA";
    
    public DO2iOSSCEPPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSSCEPPayload payload = null;
        final IOSSCEPPayload[] payloadArray = { null };
        Iterator iterator = null;
        try {
            iterator = dataObject.getRows("SCEPConfigurations");
            while (iterator.hasNext()) {
                final Row payloadRow = iterator.next();
                final long scepConfigId = (long)payloadRow.get("SCEP_CONFIG_ID");
                final String URL = (String)payloadRow.get("URL");
                final String name = (String)payloadRow.get("NAME");
                final String subject = (String)payloadRow.get("SUBJECT");
                String challenge = (String)payloadRow.get("CHALLENGE_ENCRYPTED");
                final int challengeType = (int)payloadRow.get("CHALLENGE_TYPE");
                final int keySize = (int)payloadRow.get("KEY_SIZE");
                final int keyUsage = (int)payloadRow.get("KEY_USAGE");
                final Long retries = (Long)payloadRow.get("RETRIES");
                final Long retryDelay = (Long)payloadRow.get("RETRY_DELAY");
                final int subjectAltNameType = (int)payloadRow.get("SUBJECT_ALTNAME_TYPE");
                final String subjectAltNameValue = (String)payloadRow.get("SUBJECT_ALTNAME_VALUE");
                final String ntPricipal = (String)payloadRow.get("NT_PRINCIPAL");
                final String caFingerPrint = (String)payloadRow.get("CA_FINGER_PRINT");
                long KeySizeValue = 1024L;
                final ScepServer scepServer = DynamicScepServer.getScepServerForScepId(scepConfigId);
                payload = new IOSSCEPPayload(1, "MDM", "com.mdm.mobiledevice.SCEP", "SCEP Profile Configuration");
                if (challengeType == 1) {
                    if (challenge != null && !challenge.isEmpty()) {
                        challenge = PayloadSecretFieldsHandler.getInstance().constructPayloadSCEPChallenge(Long.toString(scepConfigId));
                        payload.setChallenge(challenge);
                    }
                }
                else if (challengeType == 2) {
                    payload.setChallenge("%challenge_password%" + scepConfigId);
                }
                payload.setKeyType("RSA");
                if (keyUsage != 0) {
                    payload.setKeyUsage(keyUsage);
                }
                if (keySize == 1) {
                    KeySizeValue = 2048L;
                }
                payload.setKeysize(KeySizeValue);
                if (!MDMStringUtils.isEmpty(name)) {
                    payload.setName(name);
                }
                else {
                    try {
                        final Criteria criteria = new Criteria(new Column("SCEPServerToTemplate", "SCEP_CONFIG_ID"), (Object)scepConfigId, 0);
                        final Row scepServerTemplateRow = dataObject.getRow("SCEPServerToTemplate", criteria);
                        if (scepServerTemplateRow != null) {
                            final long serverId = (long)scepServerTemplateRow.get("SCEP_SERVER_ID");
                            final Criteria criteria2 = new Criteria(new Column("SCEPServers", "SERVER_ID"), (Object)serverId, 0);
                            final Row scepServersRow = dataObject.getRow("SCEPServers", criteria2);
                            if (scepServersRow != null) {
                                final String serverName = (String)scepServersRow.get("SERVER_NAME");
                                payload.setName(serverName);
                            }
                        }
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, e, () -> "Exception while getting scep server name for ios scep: " + n);
                    }
                }
                if (retries != null && retries > 0L) {
                    payload.setRetries(retries);
                }
                if (retryDelay != null && retryDelay > 0L) {
                    payload.setRetryDelay(retryDelay);
                }
                if (subjectAltNameType != 0) {
                    final NSDictionary subAltNameDict = new NSDictionary();
                    if (subjectAltNameValue != null && !subjectAltNameValue.isEmpty()) {
                        if (ntPricipal != null && !ntPricipal.isEmpty()) {
                            subAltNameDict.put("ntPrincipalName", (Object)ntPricipal);
                        }
                        String subAltKey = null;
                        if (subjectAltNameType == 1) {
                            subAltKey = "rfc822Name";
                        }
                        else if (subjectAltNameType == 2) {
                            subAltKey = "dnsName";
                        }
                        else if (subjectAltNameType == 3) {
                            subAltKey = "uniformResourceIdentifier";
                        }
                        if (subAltKey != null) {
                            subAltNameDict.put(subAltKey, (Object)subjectAltNameValue);
                        }
                    }
                    payload.setSubjectAltName(subAltNameDict);
                }
                if (URL != null && !URL.isEmpty()) {
                    payload.setURL(URL);
                }
                final MultiMap map = (MultiMap)new MultiHashMap();
                try {
                    if (subject != null && !subject.isEmpty()) {
                        final LdapName subjectValue = new LdapName(subject);
                        final List<Rdn> rdnList = subjectValue.getRdns();
                        for (final Rdn rdn : rdnList) {
                            map.put((Object)rdn.getType(), (Object)rdn.getValue().toString());
                        }
                    }
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
                this.setSubjectIdentifierAttribute(scepServer, map);
                final NSArray subjectOID = new NSArray(map.keySet().size());
                final Set<String> rdnKeys = map.keySet();
                int i = 0;
                for (String key : rdnKeys) {
                    final List<String> oidList = (List<String>)map.get((Object)key);
                    final NSArray OIDPair = new NSArray(oidList.size());
                    int j = 0;
                    for (final String oid : oidList) {
                        final NSSet OIDSet = new NSSet();
                        key = (key.equalsIgnoreCase("E") ? "1.2.840.113549.1.9.1" : key);
                        key = (key.equalsIgnoreCase("DC") ? "0.9.2342.19200300.100.1.25" : key);
                        OIDSet.addObject((NSObject)new NSString(key));
                        OIDSet.addObject((NSObject)new NSString(oid));
                        OIDPair.setValue(j, (Object)OIDSet);
                        ++j;
                    }
                    subjectOID.setValue(i, (Object)OIDPair);
                    ++i;
                }
                payload.setSubject(subjectOID);
            }
            payload.setPayloadContent();
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in scep payload", ex2);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private void setSubjectIdentifierAttribute(final ScepServer scepServer, final MultiMap map) {
        if (scepServer == null) {
            return;
        }
        final ScepServerType scepServerType = scepServer.getServerType();
        if (scepServerType == ScepServerType.ADCS) {
            map.put((Object)CertificateConstants.SUBJECT_SERIAL_NUMBER, (Object)"%profileId%.%resourceid%");
        }
        else if (scepServerType == ScepServerType.DIGICERT) {
            map.put((Object)CertificateConstants.SUBJECT_UNIQUE_IDENTIFIER, (Object)"%profileId%.%resourceid%");
        }
    }
}
