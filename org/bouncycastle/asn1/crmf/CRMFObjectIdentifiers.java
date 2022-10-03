package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface CRMFObjectIdentifiers
{
    public static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
    public static final ASN1ObjectIdentifier id_pkip = CRMFObjectIdentifiers.id_pkix.branch("5");
    public static final ASN1ObjectIdentifier id_regCtrl = CRMFObjectIdentifiers.id_pkip.branch("1");
    public static final ASN1ObjectIdentifier id_regCtrl_regToken = CRMFObjectIdentifiers.id_regCtrl.branch("1");
    public static final ASN1ObjectIdentifier id_regCtrl_authenticator = CRMFObjectIdentifiers.id_regCtrl.branch("2");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiPublicationInfo = CRMFObjectIdentifiers.id_regCtrl.branch("3");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiArchiveOptions = CRMFObjectIdentifiers.id_regCtrl.branch("4");
    public static final ASN1ObjectIdentifier id_ct_encKeyWithID = PKCSObjectIdentifiers.id_ct.branch("21");
}
