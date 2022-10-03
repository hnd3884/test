package com.microsoft.sqlserver.jdbc.dns;

import javax.naming.NamingException;
import java.util.Set;
import javax.naming.NameNotFoundException;

public final class DNSKerberosLocator
{
    private DNSKerberosLocator() {
    }
    
    public static boolean isRealmValid(String realmName) throws NamingException {
        if (realmName == null || realmName.length() < 2) {
            return false;
        }
        if (realmName.charAt(0) == '.') {
            realmName = realmName.substring(1);
        }
        try {
            final Set<DNSRecordSRV> records = DNSUtilities.findSrvRecords("_kerberos._udp." + realmName);
            return !records.isEmpty();
        }
        catch (final NameNotFoundException wrongDomainException) {
            return false;
        }
    }
}
