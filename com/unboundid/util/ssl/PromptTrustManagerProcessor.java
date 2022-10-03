package com.unboundid.util.ssl;

import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.IPAddressArgumentValueValidator;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.unboundid.ldap.sdk.RDN;
import java.util.Iterator;
import java.net.InetAddress;
import com.unboundid.util.ssl.cert.KeyUsageExtension;
import com.unboundid.util.ssl.cert.BasicConstraintsExtension;
import com.unboundid.util.ssl.cert.CertException;
import com.unboundid.util.Debug;
import com.unboundid.util.ssl.cert.SubjectAlternativeNameExtension;
import com.unboundid.util.ssl.cert.ExtendedKeyUsageID;
import com.unboundid.util.ssl.cert.ExtendedKeyUsageExtension;
import com.unboundid.util.ssl.cert.X509CertificateExtension;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.util.ObjectPair;
import java.util.List;
import java.util.Map;
import com.unboundid.util.ssl.cert.X509Certificate;

final class PromptTrustManagerProcessor
{
    private PromptTrustManagerProcessor() {
    }
    
    static ObjectPair<Boolean, List<String>> shouldPrompt(final String cacheKey, final X509Certificate[] chain, final boolean isServerChain, final boolean examineValidityDates, final Map<String, Boolean> acceptedCertificates, final List<String> expectedServerAddresses) {
        boolean outsideValidityWindow = false;
        final List<String> warningMessages = new ArrayList<String>(5);
        final long currentTime = System.currentTimeMillis();
        for (int i = 0; i < chain.length; ++i) {
            if (!chain[i].isWithinValidityWindow(currentTime)) {
                outsideValidityWindow = true;
                String identifier;
                if (i == 0) {
                    if (isServerChain) {
                        identifier = SSLMessages.WARN_PROMPT_PROCESSOR_LABEL_SERVER.get();
                    }
                    else {
                        identifier = SSLMessages.WARN_PROMPT_PROCESSOR_LABEL_CLIENT.get();
                    }
                }
                else {
                    identifier = SSLMessages.WARN_PROMPT_PROCESSOR_LABEL_ISSUER.get();
                }
                if (currentTime > chain[i].getNotAfterTime()) {
                    final long expiredSecondsAgo = Math.round((currentTime - chain[i].getNotAfterTime()) / 1000.0);
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CERT_EXPIRED.get(identifier, String.valueOf(chain[i].getSubjectDN()), formatDate(chain[i].getNotAfterDate()), StaticUtils.secondsToHumanReadableDuration(expiredSecondsAgo)));
                }
                else {
                    final long secondsUntilValid = Math.round((chain[i].getNotBeforeTime() - currentTime) / 1000.0);
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CERT_NOT_YET_VALID.get(identifier, String.valueOf(chain[i].getSubjectDN()), formatDate(chain[i].getNotBeforeDate()), StaticUtils.secondsToHumanReadableDuration(secondsUntilValid)));
                }
            }
        }
        SubjectAlternativeNameExtension san = null;
        for (final X509CertificateExtension extension : chain[0].getExtensions()) {
            if (extension instanceof ExtendedKeyUsageExtension) {
                final ExtendedKeyUsageExtension eku = (ExtendedKeyUsageExtension)extension;
                if (isServerChain) {
                    if (eku.getKeyPurposeIDs().contains(ExtendedKeyUsageID.TLS_SERVER_AUTHENTICATION.getOID())) {
                        continue;
                    }
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_EKU_MISSING_SERVER_AUTH.get(chain[0].getSubjectDN()));
                }
                else {
                    if (eku.getKeyPurposeIDs().contains(ExtendedKeyUsageID.TLS_CLIENT_AUTHENTICATION.getOID())) {
                        continue;
                    }
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_EKU_MISSING_CLIENT_AUTH.get(chain[0].getSubjectDN()));
                }
            }
            else {
                if (!(extension instanceof SubjectAlternativeNameExtension)) {
                    continue;
                }
                san = (SubjectAlternativeNameExtension)extension;
            }
        }
        if (chain.length == 1) {
            if (chain[0].isSelfSigned()) {
                warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CERT_IS_SELF_SIGNED.get());
                try {
                    chain[0].verifySignature(chain[0]);
                }
                catch (final CertException ce) {
                    Debug.debugException(ce);
                    warningMessages.add(ce.getMessage());
                }
            }
            else {
                warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CHAIN_NOT_COMPLETE.get(chain[0].getSubjectDN()));
            }
        }
        else {
            for (int j = 1; j < chain.length; ++j) {
                if (chain[j].isIssuerFor(chain[j - 1])) {
                    try {
                        chain[j - 1].verifySignature(chain[j]);
                    }
                    catch (final CertException ce2) {
                        Debug.debugException(ce2);
                        warningMessages.add(ce2.getMessage());
                    }
                }
                else {
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CHAIN_ISSUER_MISMATCH.get(chain[j].getSubjectDN(), chain[j - 1].getSubjectDN()));
                }
                BasicConstraintsExtension bc = null;
                KeyUsageExtension ku = null;
                for (final X509CertificateExtension extension2 : chain[j].getExtensions()) {
                    if (extension2 instanceof BasicConstraintsExtension) {
                        bc = (BasicConstraintsExtension)extension2;
                    }
                    else {
                        if (!(extension2 instanceof KeyUsageExtension)) {
                            continue;
                        }
                        ku = (KeyUsageExtension)extension2;
                    }
                }
                if (bc == null) {
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_NO_BC_EXTENSION.get(chain[j].getSubjectDN()));
                }
                else if (!bc.isCA()) {
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_BC_NOT_CA.get(chain[j].getSubjectDN()));
                }
                else if (bc.getPathLengthConstraint() != null && j - 1 > bc.getPathLengthConstraint()) {
                    if (bc.getPathLengthConstraint() == 0) {
                        warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_BC_DISALLOWED_INTERMEDIATE.get(chain[j].getSubjectDN()));
                    }
                    else {
                        warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_BC_TOO_MANY_INTERMEDIATES.get(chain[j].getSubjectDN(), bc.getPathLengthConstraint(), j - 1));
                    }
                }
                if (ku != null && !ku.isKeyCertSignBitSet()) {
                    warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_KU_NO_KEY_CERT_SIGN.get(chain[j].getSubjectDN()));
                }
            }
            if (chain[chain.length - 1].isSelfSigned()) {
                try {
                    chain[chain.length - 1].verifySignature(chain[chain.length - 1]);
                }
                catch (final CertException ce) {
                    Debug.debugException(ce);
                    warningMessages.add(ce.getMessage());
                }
            }
            else {
                warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_CHAIN_NOT_COMPLETE.get(chain[chain.length - 1].getSubjectDN()));
            }
        }
        if (isServerChain && expectedServerAddresses != null && !expectedServerAddresses.isEmpty()) {
            boolean hasAllowedAddress = false;
            final StringBuilder addressBuffer = new StringBuilder();
            for (final RDN rdn : chain[0].getSubjectDN().getRDNs()) {
                final String[] names = rdn.getAttributeNames();
                for (int k = 0; k < names.length; ++k) {
                    if (names[k].equalsIgnoreCase("cn") || names[k].equalsIgnoreCase("commonName") || names[k].equalsIgnoreCase("2.5.4.3")) {
                        final String cnValue = rdn.getAttributeValues()[k];
                        final String lowerCNValue = StaticUtils.toLowerCase(cnValue);
                        if (isHostnameOrIPAddress(lowerCNValue)) {
                            commaAppend(addressBuffer, cnValue);
                            if (isAllowedHostnameOrIPAddress(lowerCNValue, expectedServerAddresses)) {
                                hasAllowedAddress = true;
                                break;
                            }
                        }
                    }
                }
                if (hasAllowedAddress) {
                    break;
                }
            }
            if (!hasAllowedAddress && san != null) {
                for (final String dnsName : san.getDNSNames()) {
                    commaAppend(addressBuffer, dnsName);
                    if (isAllowedHostnameOrIPAddress(dnsName, expectedServerAddresses)) {
                        hasAllowedAddress = true;
                        break;
                    }
                }
                if (!hasAllowedAddress) {
                    for (final InetAddress ipAddress : san.getIPAddresses()) {
                        commaAppend(addressBuffer, ipAddress.getHostAddress());
                        if (isAllowedIPAddress(ipAddress, expectedServerAddresses)) {
                            hasAllowedAddress = true;
                            break;
                        }
                    }
                }
            }
            if (!hasAllowedAddress) {
                if (addressBuffer.length() != 0) {
                    if (addressBuffer.indexOf(",") > 0) {
                        warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_MULTIPLE_ADDRESSES_NOT_MATCHED.get(chain[0].getSubjectDN(), addressBuffer));
                    }
                    else {
                        warningMessages.add(SSLMessages.WARN_PROMPT_PROCESSOR_SINGLE_ADDRESS_NOT_MATCHED.get(chain[0].getSubjectDN(), addressBuffer));
                    }
                }
            }
        }
        final Boolean acceptedEvenWithBadValidity = acceptedCertificates.get(cacheKey);
        if (acceptedEvenWithBadValidity == null) {
            return new ObjectPair<Boolean, List<String>>(Boolean.TRUE, warningMessages);
        }
        if (acceptedEvenWithBadValidity) {
            return new ObjectPair<Boolean, List<String>>(Boolean.FALSE, warningMessages);
        }
        return new ObjectPair<Boolean, List<String>>(outsideValidityWindow, warningMessages);
    }
    
    static String formatDate(final Date d) {
        final String dateFormatString = "EEEE, MMMM d, yyyy";
        final String formattedDate = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d);
        final String timeFormatString = "hh:mm:ss aa z";
        final String formattedTime = new SimpleDateFormat("hh:mm:ss aa z").format(d);
        return SSLMessages.WARN_PROMPT_PROCESSOR_DATE_TIME.get(formattedDate, formattedTime);
    }
    
    static boolean isHostnameOrIPAddress(final String s) {
        if (s.isEmpty()) {
            return false;
        }
        if (IPAddressArgumentValueValidator.isValidNumericIPAddress(s)) {
            return true;
        }
        boolean lastWasPeriod = false;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c >= 'a' && c <= 'z') {
                lastWasPeriod = false;
            }
            else if (c >= '0' && c <= '9') {
                if (i == 0 || lastWasPeriod) {
                    return false;
                }
                lastWasPeriod = false;
            }
            else if (c == '.') {
                if (i == 0 || lastWasPeriod) {
                    return false;
                }
                lastWasPeriod = true;
            }
            else if (c == '*') {
                if (i > 0) {
                    return false;
                }
                if (s.length() == 1 || s.charAt(1) != '.') {
                    return false;
                }
                lastWasPeriod = false;
            }
        }
        return !lastWasPeriod;
    }
    
    private static boolean isAllowedHostnameOrIPAddress(final String s, final List<String> expectedAddresses) {
        if (IPAddressArgumentValueValidator.isValidNumericIPAddress(s)) {
            try {
                final InetAddress ip = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(s);
                for (final String expectedAddress : expectedAddresses) {
                    if (IPAddressArgumentValueValidator.isValidNumericIPAddress(expectedAddress) && ip.equals(LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(expectedAddress))) {
                        return true;
                    }
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        for (final String expectedAddress2 : expectedAddresses) {
            if (s.equalsIgnoreCase(expectedAddress2)) {
                return true;
            }
            if (!s.startsWith("*.")) {
                continue;
            }
            final int periodPos = expectedAddress2.indexOf(46);
            if (periodPos <= 0) {
                continue;
            }
            final String endOfS = s.substring(2);
            final String endOfExpectedAddress = expectedAddress2.substring(periodPos + 1);
            if (endOfS.equalsIgnoreCase(endOfExpectedAddress)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isAllowedIPAddress(final InetAddress a, final List<String> expectedAddresses) {
        for (final String s : expectedAddresses) {
            try {
                if (IPAddressArgumentValueValidator.isValidNumericIPAddress(s) && a.equals(LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(s))) {
                    return true;
                }
                continue;
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return false;
    }
    
    private static void commaAppend(final StringBuilder b, final String s) {
        if (b.length() > 0) {
            b.append(", ");
        }
        b.append(s);
    }
}
