package org.bouncycastle.asn1.isismtt;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ISISMTTObjectIdentifiers
{
    public static final ASN1ObjectIdentifier id_isismtt = new ASN1ObjectIdentifier("1.3.36.8");
    public static final ASN1ObjectIdentifier id_isismtt_cp = ISISMTTObjectIdentifiers.id_isismtt.branch("1");
    public static final ASN1ObjectIdentifier id_isismtt_cp_accredited = ISISMTTObjectIdentifiers.id_isismtt_cp.branch("1");
    public static final ASN1ObjectIdentifier id_isismtt_at = ISISMTTObjectIdentifiers.id_isismtt.branch("3");
    public static final ASN1ObjectIdentifier id_isismtt_at_dateOfCertGen = ISISMTTObjectIdentifiers.id_isismtt_at.branch("1");
    public static final ASN1ObjectIdentifier id_isismtt_at_procuration = ISISMTTObjectIdentifiers.id_isismtt_at.branch("2");
    public static final ASN1ObjectIdentifier id_isismtt_at_admission = ISISMTTObjectIdentifiers.id_isismtt_at.branch("3");
    public static final ASN1ObjectIdentifier id_isismtt_at_monetaryLimit = ISISMTTObjectIdentifiers.id_isismtt_at.branch("4");
    public static final ASN1ObjectIdentifier id_isismtt_at_declarationOfMajority = ISISMTTObjectIdentifiers.id_isismtt_at.branch("5");
    public static final ASN1ObjectIdentifier id_isismtt_at_iCCSN = ISISMTTObjectIdentifiers.id_isismtt_at.branch("6");
    public static final ASN1ObjectIdentifier id_isismtt_at_PKReference = ISISMTTObjectIdentifiers.id_isismtt_at.branch("7");
    public static final ASN1ObjectIdentifier id_isismtt_at_restriction = ISISMTTObjectIdentifiers.id_isismtt_at.branch("8");
    public static final ASN1ObjectIdentifier id_isismtt_at_retrieveIfAllowed = ISISMTTObjectIdentifiers.id_isismtt_at.branch("9");
    public static final ASN1ObjectIdentifier id_isismtt_at_requestedCertificate = ISISMTTObjectIdentifiers.id_isismtt_at.branch("10");
    public static final ASN1ObjectIdentifier id_isismtt_at_namingAuthorities = ISISMTTObjectIdentifiers.id_isismtt_at.branch("11");
    public static final ASN1ObjectIdentifier id_isismtt_at_certInDirSince = ISISMTTObjectIdentifiers.id_isismtt_at.branch("12");
    public static final ASN1ObjectIdentifier id_isismtt_at_certHash = ISISMTTObjectIdentifiers.id_isismtt_at.branch("13");
    public static final ASN1ObjectIdentifier id_isismtt_at_nameAtBirth = ISISMTTObjectIdentifiers.id_isismtt_at.branch("14");
    public static final ASN1ObjectIdentifier id_isismtt_at_additionalInformation = ISISMTTObjectIdentifiers.id_isismtt_at.branch("15");
    public static final ASN1ObjectIdentifier id_isismtt_at_liabilityLimitationFlag = new ASN1ObjectIdentifier("0.2.262.1.10.12.0");
}
