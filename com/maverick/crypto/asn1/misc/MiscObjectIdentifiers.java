package com.maverick.crypto.asn1.misc;

import com.maverick.crypto.asn1.DERObjectIdentifier;

public interface MiscObjectIdentifiers
{
    public static final String netscape = "2.16.840.1.113730.1";
    public static final DERObjectIdentifier netscapeCertType = new DERObjectIdentifier("2.16.840.1.113730.1.1");
    public static final DERObjectIdentifier netscapeBaseURL = new DERObjectIdentifier("2.16.840.1.113730.1.2");
    public static final DERObjectIdentifier netscapeRevocationURL = new DERObjectIdentifier("2.16.840.1.113730.1.3");
    public static final DERObjectIdentifier netscapeCARevocationURL = new DERObjectIdentifier("2.16.840.1.113730.1.4");
    public static final DERObjectIdentifier netscapeRenewalURL = new DERObjectIdentifier("2.16.840.1.113730.1.7");
    public static final DERObjectIdentifier netscapeCApolicyURL = new DERObjectIdentifier("2.16.840.1.113730.1.8");
    public static final DERObjectIdentifier netscapeSSLServerName = new DERObjectIdentifier("2.16.840.1.113730.1.12");
    public static final DERObjectIdentifier netscapeCertComment = new DERObjectIdentifier("2.16.840.1.113730.1.13");
    public static final String verisign = "2.16.840.1.113733.1";
    public static final DERObjectIdentifier verisignCzagExtension = new DERObjectIdentifier("2.16.840.1.113733.1.6.3");
}
