package com.unboundid.ldap.listener;

import java.util.Iterator;
import java.util.Set;
import com.unboundid.ldap.sdk.NameResolver;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import java.io.InputStream;
import com.unboundid.util.ssl.cert.ManageCertificates;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.RDN;
import java.net.InetAddress;
import java.util.Collection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.Base64;
import java.security.SecureRandom;
import com.unboundid.util.ssl.cert.CertException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.io.File;
import com.unboundid.util.ObjectPair;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SelfSignedCertificateGenerator
{
    private SelfSignedCertificateGenerator() {
    }
    
    public static ObjectPair<File, char[]> generateTemporarySelfSignedCertificate(final String toolName, final String keyStoreType) throws CertException {
        File keyStoreFile;
        try {
            keyStoreFile = File.createTempFile("temp-keystore-", ".jks");
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(ListenerMessages.ERR_SELF_SIGNED_CERT_GENERATOR_CANNOT_CREATE_FILE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        keyStoreFile.delete();
        final SecureRandom random = new SecureRandom();
        final byte[] randomBytes = new byte[50];
        random.nextBytes(randomBytes);
        final String keyStorePIN = Base64.encode(randomBytes);
        generateSelfSignedCertificate(toolName, keyStoreFile, keyStorePIN, keyStoreType, "server-cert");
        return new ObjectPair<File, char[]>(keyStoreFile, keyStorePIN.toCharArray());
    }
    
    public static void generateSelfSignedCertificate(final String toolName, final File keyStoreFile, final String keyStorePIN, final String keyStoreType, final String alias) throws CertException {
        final NameResolver nameResolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        final Set<InetAddress> localAddresses = StaticUtils.getAllLocalAddresses(nameResolver);
        final Set<String> canonicalHostNames = StaticUtils.getAvailableCanonicalHostNames(nameResolver, localAddresses);
        DN subjectDN;
        if (localAddresses.isEmpty()) {
            subjectDN = new DN(new RDN[] { new RDN("CN", toolName) });
        }
        else {
            subjectDN = new DN(new RDN[] { new RDN("CN", nameResolver.getCanonicalHostName(localAddresses.iterator().next())), new RDN("OU", toolName) });
        }
        final long oneDayAgoTime = System.currentTimeMillis() - 86400000L;
        final Date oneDayAgoDate = new Date(oneDayAgoTime);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        final String yesterdayTimeStamp = dateFormatter.format(oneDayAgoDate);
        final ArrayList<String> argList = new ArrayList<String>(30);
        argList.add("generate-self-signed-certificate");
        argList.add("--keystore");
        argList.add(keyStoreFile.getAbsolutePath());
        argList.add("--keystore-password");
        argList.add(keyStorePIN);
        argList.add("--keystore-type");
        argList.add(keyStoreType);
        argList.add("--alias");
        argList.add(alias);
        argList.add("--subject-dn");
        argList.add(subjectDN.toString());
        argList.add("--days-valid");
        argList.add("3650");
        argList.add("--validityStartTime");
        argList.add(yesterdayTimeStamp);
        argList.add("--key-algorithm");
        argList.add("RSA");
        argList.add("--key-size-bits");
        argList.add("2048");
        argList.add("--signature-algorithm");
        argList.add("SHA256withRSA");
        for (final String hostName : canonicalHostNames) {
            argList.add("--subject-alternative-name-dns");
            argList.add(hostName);
        }
        for (final InetAddress address : localAddresses) {
            argList.add("--subject-alternative-name-ip-address");
            argList.add(StaticUtils.trimInterfaceNameFromHostAddress(address.getHostAddress()));
        }
        argList.add("--key-usage");
        argList.add("digitalSignature");
        argList.add("--key-usage");
        argList.add("keyEncipherment");
        argList.add("--extended-key-usage");
        argList.add("server-auth");
        argList.add("--extended-key-usage");
        argList.add("client-auth");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ResultCode resultCode = ManageCertificates.main(null, output, output, (String[])argList.toArray(StaticUtils.NO_STRINGS));
        if (resultCode != ResultCode.SUCCESS) {
            throw new CertException(ListenerMessages.ERR_SELF_SIGNED_CERT_GENERATOR_ERROR_GENERATING_CERT.get(StaticUtils.toUTF8String(output.toByteArray())));
        }
    }
}
