package org.bouncycastle.tsp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface TSPAlgorithms
{
    public static final ASN1ObjectIdentifier MD5 = PKCSObjectIdentifiers.md5;
    public static final ASN1ObjectIdentifier SHA1 = OIWObjectIdentifiers.idSHA1;
    public static final ASN1ObjectIdentifier SHA224 = NISTObjectIdentifiers.id_sha224;
    public static final ASN1ObjectIdentifier SHA256 = NISTObjectIdentifiers.id_sha256;
    public static final ASN1ObjectIdentifier SHA384 = NISTObjectIdentifiers.id_sha384;
    public static final ASN1ObjectIdentifier SHA512 = NISTObjectIdentifiers.id_sha512;
    public static final ASN1ObjectIdentifier RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128;
    public static final ASN1ObjectIdentifier RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160;
    public static final ASN1ObjectIdentifier RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256;
    public static final ASN1ObjectIdentifier GOST3411 = CryptoProObjectIdentifiers.gostR3411;
    public static final ASN1ObjectIdentifier GOST3411_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256;
    public static final ASN1ObjectIdentifier GOST3411_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512;
    public static final ASN1ObjectIdentifier SM3 = GMObjectIdentifiers.sm3;
    public static final Set ALLOWED = new HashSet(Arrays.asList(TSPAlgorithms.SM3, TSPAlgorithms.GOST3411, TSPAlgorithms.GOST3411_2012_256, TSPAlgorithms.GOST3411_2012_512, TSPAlgorithms.MD5, TSPAlgorithms.SHA1, TSPAlgorithms.SHA224, TSPAlgorithms.SHA256, TSPAlgorithms.SHA384, TSPAlgorithms.SHA512, TSPAlgorithms.RIPEMD128, TSPAlgorithms.RIPEMD160, TSPAlgorithms.RIPEMD256));
}
