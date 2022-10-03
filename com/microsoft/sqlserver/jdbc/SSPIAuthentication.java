package com.microsoft.sqlserver.jdbc;

import java.util.regex.Matcher;
import java.util.Locale;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.naming.NamingException;
import com.microsoft.sqlserver.jdbc.dns.DNSKerberosLocator;
import java.net.IDN;
import java.util.regex.Pattern;

abstract class SSPIAuthentication
{
    private static final Pattern SPN_PATTERN;
    private RealmValidator validator;
    
    abstract byte[] generateClientContext(final byte[] p0, final boolean[] p1) throws SQLServerException;
    
    abstract void releaseClientContext();
    
    private String makeSpn(final SQLServerConnection con, final String server, final int port) {
        final StringBuilder spn = new StringBuilder("MSSQLSvc/");
        if (con.serverNameAsACE()) {
            spn.append(IDN.toASCII(server));
        }
        else {
            spn.append(server);
        }
        spn.append(":");
        spn.append(port);
        return spn.toString();
    }
    
    private RealmValidator getRealmValidator() {
        if (null != this.validator) {
            return this.validator;
        }
        return this.validator = new RealmValidator() {
            @Override
            public boolean isRealmValid(final String realm) {
                try {
                    return DNSKerberosLocator.isRealmValid(realm);
                }
                catch (final NamingException err) {
                    return false;
                }
            }
        };
    }
    
    private String findRealmFromHostname(final RealmValidator realmValidator, final String hostname) {
        if (hostname == null) {
            return null;
        }
        int index = 0;
        while (index != -1 && index < hostname.length() - 2) {
            final String realm = hostname.substring(index);
            if (realmValidator.isRealmValid(realm)) {
                return realm.toUpperCase();
            }
            index = hostname.indexOf(".", index + 1);
            if (-1 == index) {
                continue;
            }
            ++index;
        }
        return null;
    }
    
    String enrichSpnWithRealm(final String spn, final boolean allowHostnameCanonicalization) {
        if (spn == null) {
            return spn;
        }
        final Matcher m = SSPIAuthentication.SPN_PATTERN.matcher(spn);
        if (!m.matches()) {
            return spn;
        }
        if (m.group(3) != null) {
            return spn;
        }
        String dnsName = m.group(1);
        final String portOrInstance = m.group(2);
        final RealmValidator realmValidator = this.getRealmValidator();
        String realm = this.findRealmFromHostname(realmValidator, dnsName);
        if (realm == null && allowHostnameCanonicalization) {
            try {
                final String canonicalHostName = InetAddress.getByName(dnsName).getCanonicalHostName();
                realm = this.findRealmFromHostname(realmValidator, canonicalHostName);
                dnsName = canonicalHostName;
            }
            catch (final UnknownHostException ex) {}
        }
        if (realm == null) {
            return spn;
        }
        final StringBuilder sb = new StringBuilder("MSSQLSvc/");
        sb.append(dnsName).append(":").append(portOrInstance).append("@").append(realm.toUpperCase(Locale.ENGLISH));
        return sb.toString();
    }
    
    String getSpn(final SQLServerConnection con) {
        if (null == con || null == con.activeConnectionProperties) {
            return null;
        }
        final String userSuppliedServerSpn = con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_SPN.toString());
        String spn;
        if (null != userSuppliedServerSpn) {
            if (con.serverNameAsACE()) {
                final int slashPos = userSuppliedServerSpn.indexOf("/");
                spn = userSuppliedServerSpn.substring(0, slashPos + 1) + IDN.toASCII(userSuppliedServerSpn.substring(slashPos + 1));
            }
            else {
                spn = userSuppliedServerSpn;
            }
        }
        else {
            spn = this.makeSpn(con, con.currentConnectPlaceHolder.getServerName(), con.currentConnectPlaceHolder.getPortNumber());
        }
        return this.enrichSpnWithRealm(spn, null == userSuppliedServerSpn);
    }
    
    static {
        SPN_PATTERN = Pattern.compile("MSSQLSvc/(.*):([^:@]+)(@.+)?", 2);
    }
    
    interface RealmValidator
    {
        boolean isRealmValid(final String p0);
    }
}
