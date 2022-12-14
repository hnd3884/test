package org.bouncycastle.asn1.anssi;

import org.bouncycastle.asn1.x9.X9ECPoint;
import java.util.Enumeration;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import java.util.Hashtable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;

public class ANSSINamedCurves
{
    static X9ECParametersHolder FRP256v1;
    static final Hashtable objIds;
    static final Hashtable curves;
    static final Hashtable names;
    
    private static ECCurve configureCurve(final ECCurve ecCurve) {
        return ecCurve;
    }
    
    private static BigInteger fromHex(final String s) {
        return new BigInteger(1, Hex.decode(s));
    }
    
    static void defineCurve(final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier, final X9ECParametersHolder x9ECParametersHolder) {
        ANSSINamedCurves.objIds.put(Strings.toLowerCase(s), asn1ObjectIdentifier);
        ANSSINamedCurves.names.put(asn1ObjectIdentifier, s);
        ANSSINamedCurves.curves.put(asn1ObjectIdentifier, x9ECParametersHolder);
    }
    
    public static X9ECParameters getByName(final String s) {
        final ASN1ObjectIdentifier oid = getOID(s);
        return (oid == null) ? null : getByOID(oid);
    }
    
    public static X9ECParameters getByOID(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final X9ECParametersHolder x9ECParametersHolder = ANSSINamedCurves.curves.get(asn1ObjectIdentifier);
        return (x9ECParametersHolder == null) ? null : x9ECParametersHolder.getParameters();
    }
    
    public static ASN1ObjectIdentifier getOID(final String s) {
        return ANSSINamedCurves.objIds.get(Strings.toLowerCase(s));
    }
    
    public static String getName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return ANSSINamedCurves.names.get(asn1ObjectIdentifier);
    }
    
    public static Enumeration getNames() {
        return ANSSINamedCurves.names.elements();
    }
    
    static {
        ANSSINamedCurves.FRP256v1 = new X9ECParametersHolder() {
            @Override
            protected X9ECParameters createParameters() {
                final BigInteger access$000 = fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C03");
                final BigInteger access$2 = fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C00");
                final BigInteger access$3 = fromHex("EE353FCA5428A9300D4ABA754A44C00FDFEC0C9AE4B1A1803075ED967B7BB73F");
                final byte[] array = null;
                final BigInteger access$4 = fromHex("F1FD178C0B3AD58F10126DE8CE42435B53DC67E140D2BF941FFDD459C6D655E1");
                final BigInteger value = BigInteger.valueOf(1L);
                final ECCurve access$5 = configureCurve(new ECCurve.Fp(access$000, access$2, access$3, access$4, value));
                return new X9ECParameters(access$5, new X9ECPoint(access$5, Hex.decode("04B6B3D4C356C139EB31183D4749D423958C27D2DCAF98B70164C97A2DD98F5CFF6142E0F7C8B204911F9271F0F3ECEF8C2701C307E8E4C9E183115A1554062CFB")), access$4, value, array);
            }
        };
        objIds = new Hashtable();
        curves = new Hashtable();
        names = new Hashtable();
        defineCurve("FRP256v1", ANSSIObjectIdentifiers.FRP256v1, ANSSINamedCurves.FRP256v1);
    }
}
