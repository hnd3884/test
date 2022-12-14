package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface X509AttributeIdentifiers
{
    @Deprecated
    public static final ASN1ObjectIdentifier RoleSyntax = new ASN1ObjectIdentifier("2.5.4.72");
    public static final ASN1ObjectIdentifier id_pe_ac_auditIdentity = X509ObjectIdentifiers.id_pe.branch("4");
    public static final ASN1ObjectIdentifier id_pe_aaControls = X509ObjectIdentifiers.id_pe.branch("6");
    public static final ASN1ObjectIdentifier id_pe_ac_proxying = X509ObjectIdentifiers.id_pe.branch("10");
    public static final ASN1ObjectIdentifier id_ce_targetInformation = X509ObjectIdentifiers.id_ce.branch("55");
    public static final ASN1ObjectIdentifier id_aca = X509ObjectIdentifiers.id_pkix.branch("10");
    public static final ASN1ObjectIdentifier id_aca_authenticationInfo = X509AttributeIdentifiers.id_aca.branch("1");
    public static final ASN1ObjectIdentifier id_aca_accessIdentity = X509AttributeIdentifiers.id_aca.branch("2");
    public static final ASN1ObjectIdentifier id_aca_chargingIdentity = X509AttributeIdentifiers.id_aca.branch("3");
    public static final ASN1ObjectIdentifier id_aca_group = X509AttributeIdentifiers.id_aca.branch("4");
    public static final ASN1ObjectIdentifier id_aca_encAttrs = X509AttributeIdentifiers.id_aca.branch("6");
    public static final ASN1ObjectIdentifier id_at_role = new ASN1ObjectIdentifier("2.5.4.72");
    public static final ASN1ObjectIdentifier id_at_clearance = new ASN1ObjectIdentifier("2.5.1.5.55");
}
