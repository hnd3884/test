package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.ldap.sdk.RDN;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPDN
{
    private LDAPDN() {
    }
    
    public static String normalize(final String dn) {
        try {
            return DN.normalize(dn);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return StaticUtils.toLowerCase(dn.trim());
        }
    }
    
    public static String[] explodeDN(final String dn, final boolean noTypes) {
        try {
            final RDN[] rdns = new DN(dn).getRDNs();
            final String[] rdnStrings = new String[rdns.length];
            for (int i = 0; i < rdns.length; ++i) {
                if (noTypes) {
                    final StringBuilder buffer = new StringBuilder();
                    for (final String s : rdns[i].getAttributeValues()) {
                        if (buffer.length() > 0) {
                            buffer.append('+');
                        }
                        buffer.append(s);
                    }
                    rdnStrings[i] = buffer.toString();
                }
                else {
                    rdnStrings[i] = rdns[i].toString();
                }
            }
            return rdnStrings;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new String[] { dn };
        }
    }
    
    public static String[] explodeRDN(final String rdn, final boolean noTypes) {
        try {
            final RDN rdnObject = new RDN(rdn);
            final String[] values = rdnObject.getAttributeValues();
            if (noTypes) {
                return values;
            }
            final String[] names = rdnObject.getAttributeNames();
            final String[] returnStrs = new String[names.length];
            for (int i = 0; i < names.length; ++i) {
                returnStrs[i] = names[i] + '=' + values[i];
            }
            return returnStrs;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new String[] { rdn };
        }
    }
    
    public static boolean equals(final String dn1, final String dn2) {
        try {
            return DN.equals(dn1, dn2);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
}
