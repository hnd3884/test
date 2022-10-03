package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ICAOObjectIdentifiers
{
    public static final ASN1ObjectIdentifier id_icao = new ASN1ObjectIdentifier("2.23.136");
    public static final ASN1ObjectIdentifier id_icao_mrtd = ICAOObjectIdentifiers.id_icao.branch("1");
    public static final ASN1ObjectIdentifier id_icao_mrtd_security = ICAOObjectIdentifiers.id_icao_mrtd.branch("1");
    public static final ASN1ObjectIdentifier id_icao_ldsSecurityObject = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("1");
    public static final ASN1ObjectIdentifier id_icao_cscaMasterList = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("2");
    public static final ASN1ObjectIdentifier id_icao_cscaMasterListSigningKey = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("3");
    public static final ASN1ObjectIdentifier id_icao_documentTypeList = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("4");
    public static final ASN1ObjectIdentifier id_icao_aaProtocolObject = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("5");
    public static final ASN1ObjectIdentifier id_icao_extensions = ICAOObjectIdentifiers.id_icao_mrtd_security.branch("6");
    public static final ASN1ObjectIdentifier id_icao_extensions_namechangekeyrollover = ICAOObjectIdentifiers.id_icao_extensions.branch("1");
}
