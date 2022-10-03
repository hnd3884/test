package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Integers;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.ec.ECFieldElement;
import java.io.OutputStream;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.util.Hashtable;

public class TlsECCUtils
{
    public static final Integer EXT_elliptic_curves;
    public static final Integer EXT_ec_point_formats;
    private static final String[] CURVE_NAMES;
    
    public static void addSupportedEllipticCurvesExtension(final Hashtable hashtable, final int[] array) throws IOException {
        hashtable.put(TlsECCUtils.EXT_elliptic_curves, createSupportedEllipticCurvesExtension(array));
    }
    
    public static void addSupportedPointFormatsExtension(final Hashtable hashtable, final short[] array) throws IOException {
        hashtable.put(TlsECCUtils.EXT_ec_point_formats, createSupportedPointFormatsExtension(array));
    }
    
    public static int[] getSupportedEllipticCurvesExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsECCUtils.EXT_elliptic_curves);
        return (int[])((extensionData == null) ? null : readSupportedEllipticCurvesExtension(extensionData));
    }
    
    public static short[] getSupportedPointFormatsExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsECCUtils.EXT_ec_point_formats);
        return (short[])((extensionData == null) ? null : readSupportedPointFormatsExtension(extensionData));
    }
    
    public static byte[] createSupportedEllipticCurvesExtension(final int[] array) throws IOException {
        if (array == null || array.length < 1) {
            throw new TlsFatalAlert((short)80);
        }
        return TlsUtils.encodeUint16ArrayWithUint16Length(array);
    }
    
    public static byte[] createSupportedPointFormatsExtension(short[] append) throws IOException {
        if (append == null || !Arrays.contains(append, (short)0)) {
            append = Arrays.append(append, (short)0);
        }
        return TlsUtils.encodeUint8ArrayWithUint8Length(append);
    }
    
    public static int[] readSupportedEllipticCurvesExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (uint16 < 2 || (uint16 & 0x1) != 0x0) {
            throw new TlsFatalAlert((short)50);
        }
        final int[] uint16Array = TlsUtils.readUint16Array(uint16 / 2, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return uint16Array;
    }
    
    public static short[] readSupportedPointFormatsExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final short uint8 = TlsUtils.readUint8(byteArrayInputStream);
        if (uint8 < 1) {
            throw new TlsFatalAlert((short)50);
        }
        final short[] uint8Array = TlsUtils.readUint8Array(uint8, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        if (!Arrays.contains(uint8Array, (short)0)) {
            throw new TlsFatalAlert((short)47);
        }
        return uint8Array;
    }
    
    public static String getNameOfNamedCurve(final int n) {
        return isSupportedNamedCurve(n) ? TlsECCUtils.CURVE_NAMES[n - 1] : null;
    }
    
    public static ECDomainParameters getParametersForNamedCurve(final int n) {
        final String nameOfNamedCurve = getNameOfNamedCurve(n);
        if (nameOfNamedCurve == null) {
            return null;
        }
        X9ECParameters x9ECParameters = CustomNamedCurves.getByName(nameOfNamedCurve);
        if (x9ECParameters == null) {
            x9ECParameters = ECNamedCurveTable.getByName(nameOfNamedCurve);
            if (x9ECParameters == null) {
                return null;
            }
        }
        return new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
    }
    
    public static boolean hasAnySupportedNamedCurves() {
        return TlsECCUtils.CURVE_NAMES.length > 0;
    }
    
    public static boolean containsECCCipherSuites(final int[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (isECCCipherSuite(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isECCCipherSuite(final int n) {
        switch (n) {
            case 49153:
            case 49154:
            case 49155:
            case 49156:
            case 49157:
            case 49158:
            case 49159:
            case 49160:
            case 49161:
            case 49162:
            case 49163:
            case 49164:
            case 49165:
            case 49166:
            case 49167:
            case 49168:
            case 49169:
            case 49170:
            case 49171:
            case 49172:
            case 49173:
            case 49174:
            case 49175:
            case 49176:
            case 49177:
            case 49187:
            case 49188:
            case 49189:
            case 49190:
            case 49191:
            case 49192:
            case 49193:
            case 49194:
            case 49195:
            case 49196:
            case 49197:
            case 49198:
            case 49199:
            case 49200:
            case 49201:
            case 49202:
            case 49203:
            case 49204:
            case 49205:
            case 49206:
            case 49207:
            case 49208:
            case 49209:
            case 49210:
            case 49211:
            case 49266:
            case 49267:
            case 49268:
            case 49269:
            case 49270:
            case 49271:
            case 49272:
            case 49273:
            case 49286:
            case 49287:
            case 49288:
            case 49289:
            case 49290:
            case 49291:
            case 49292:
            case 49293:
            case 49306:
            case 49307:
            case 49324:
            case 49325:
            case 49326:
            case 49327:
            case 52392:
            case 52393:
            case 52396:
            case 65282:
            case 65283:
            case 65284:
            case 65285:
            case 65300:
            case 65301: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean areOnSameCurve(final ECDomainParameters ecDomainParameters, final ECDomainParameters ecDomainParameters2) {
        return ecDomainParameters != null && ecDomainParameters.equals(ecDomainParameters2);
    }
    
    public static boolean isSupportedNamedCurve(final int n) {
        return n > 0 && n <= TlsECCUtils.CURVE_NAMES.length;
    }
    
    public static boolean isCompressionPreferred(final short[] array, final short n) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            final short n2 = array[i];
            if (n2 == 0) {
                return false;
            }
            if (n2 == n) {
                return true;
            }
        }
        return false;
    }
    
    public static byte[] serializeECFieldElement(final int n, final BigInteger bigInteger) throws IOException {
        return BigIntegers.asUnsignedByteArray((n + 7) / 8, bigInteger);
    }
    
    public static byte[] serializeECPoint(final short[] array, final ECPoint ecPoint) throws IOException {
        final ECCurve curve = ecPoint.getCurve();
        boolean b = false;
        if (ECAlgorithms.isFpCurve(curve)) {
            b = isCompressionPreferred(array, (short)1);
        }
        else if (ECAlgorithms.isF2mCurve(curve)) {
            b = isCompressionPreferred(array, (short)2);
        }
        return ecPoint.getEncoded(b);
    }
    
    public static byte[] serializeECPublicKey(final short[] array, final ECPublicKeyParameters ecPublicKeyParameters) throws IOException {
        return serializeECPoint(array, ecPublicKeyParameters.getQ());
    }
    
    public static BigInteger deserializeECFieldElement(final int n, final byte[] array) throws IOException {
        if (array.length != (n + 7) / 8) {
            throw new TlsFatalAlert((short)50);
        }
        return new BigInteger(1, array);
    }
    
    public static ECPoint deserializeECPoint(final short[] array, final ECCurve ecCurve, final byte[] array2) throws IOException {
        if (array2 == null || array2.length < 1) {
            throw new TlsFatalAlert((short)47);
        }
        short n = 0;
        switch (array2[0]) {
            case 2:
            case 3: {
                if (ECAlgorithms.isF2mCurve(ecCurve)) {
                    n = 2;
                    break;
                }
                if (ECAlgorithms.isFpCurve(ecCurve)) {
                    n = 1;
                    break;
                }
                throw new TlsFatalAlert((short)47);
            }
            case 4: {
                n = 0;
                break;
            }
            default: {
                throw new TlsFatalAlert((short)47);
            }
        }
        if (n != 0 && (array == null || !Arrays.contains(array, n))) {
            throw new TlsFatalAlert((short)47);
        }
        return ecCurve.decodePoint(array2);
    }
    
    public static ECPublicKeyParameters deserializeECPublicKey(final short[] array, final ECDomainParameters ecDomainParameters, final byte[] array2) throws IOException {
        try {
            return new ECPublicKeyParameters(deserializeECPoint(array, ecDomainParameters.getCurve(), array2), ecDomainParameters);
        }
        catch (final RuntimeException ex) {
            throw new TlsFatalAlert((short)47, ex);
        }
    }
    
    public static byte[] calculateECDHBasicAgreement(final ECPublicKeyParameters ecPublicKeyParameters, final ECPrivateKeyParameters ecPrivateKeyParameters) {
        final ECDHBasicAgreement ecdhBasicAgreement = new ECDHBasicAgreement();
        ecdhBasicAgreement.init(ecPrivateKeyParameters);
        return BigIntegers.asUnsignedByteArray(ecdhBasicAgreement.getFieldSize(), ecdhBasicAgreement.calculateAgreement(ecPublicKeyParameters));
    }
    
    public static AsymmetricCipherKeyPair generateECKeyPair(final SecureRandom secureRandom, final ECDomainParameters ecDomainParameters) {
        final ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
        ecKeyPairGenerator.init(new ECKeyGenerationParameters(ecDomainParameters, secureRandom));
        return ecKeyPairGenerator.generateKeyPair();
    }
    
    public static ECPrivateKeyParameters generateEphemeralClientKeyExchange(final SecureRandom secureRandom, final short[] array, final ECDomainParameters ecDomainParameters, final OutputStream outputStream) throws IOException {
        final AsymmetricCipherKeyPair generateECKeyPair = generateECKeyPair(secureRandom, ecDomainParameters);
        writeECPoint(array, ((ECPublicKeyParameters)generateECKeyPair.getPublic()).getQ(), outputStream);
        return (ECPrivateKeyParameters)generateECKeyPair.getPrivate();
    }
    
    static ECPrivateKeyParameters generateEphemeralServerKeyExchange(final SecureRandom secureRandom, final int[] array, final short[] array2, final OutputStream outputStream) throws IOException {
        int n = -1;
        if (array == null) {
            n = 23;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                final int n2 = array[i];
                if (NamedCurve.isValid(n2) && isSupportedNamedCurve(n2)) {
                    n = n2;
                    break;
                }
            }
        }
        ECDomainParameters ecDomainParameters = null;
        if (n >= 0) {
            ecDomainParameters = getParametersForNamedCurve(n);
        }
        else if (Arrays.contains(array, 65281)) {
            ecDomainParameters = getParametersForNamedCurve(23);
        }
        else if (Arrays.contains(array, 65282)) {
            ecDomainParameters = getParametersForNamedCurve(10);
        }
        if (ecDomainParameters == null) {
            throw new TlsFatalAlert((short)80);
        }
        if (n < 0) {
            writeExplicitECParameters(array2, ecDomainParameters, outputStream);
        }
        else {
            writeNamedECParameters(n, outputStream);
        }
        return generateEphemeralClientKeyExchange(secureRandom, array2, ecDomainParameters, outputStream);
    }
    
    public static ECPublicKeyParameters validateECPublicKey(final ECPublicKeyParameters ecPublicKeyParameters) throws IOException {
        return ecPublicKeyParameters;
    }
    
    public static int readECExponent(final int n, final InputStream inputStream) throws IOException {
        final BigInteger ecParameter = readECParameter(inputStream);
        if (ecParameter.bitLength() < 32) {
            final int intValue = ecParameter.intValue();
            if (intValue > 0 && intValue < n) {
                return intValue;
            }
        }
        throw new TlsFatalAlert((short)47);
    }
    
    public static BigInteger readECFieldElement(final int n, final InputStream inputStream) throws IOException {
        return deserializeECFieldElement(n, TlsUtils.readOpaque8(inputStream));
    }
    
    public static BigInteger readECParameter(final InputStream inputStream) throws IOException {
        return new BigInteger(1, TlsUtils.readOpaque8(inputStream));
    }
    
    public static ECDomainParameters readECParameters(final int[] array, final short[] array2, final InputStream inputStream) throws IOException {
        try {
            switch (TlsUtils.readUint8(inputStream)) {
                case 1: {
                    checkNamedCurve(array, 65281);
                    final BigInteger ecParameter = readECParameter(inputStream);
                    final BigInteger ecFieldElement = readECFieldElement(ecParameter.bitLength(), inputStream);
                    final BigInteger ecFieldElement2 = readECFieldElement(ecParameter.bitLength(), inputStream);
                    final byte[] opaque8 = TlsUtils.readOpaque8(inputStream);
                    final BigInteger ecParameter2 = readECParameter(inputStream);
                    final BigInteger ecParameter3 = readECParameter(inputStream);
                    final ECCurve.Fp fp = new ECCurve.Fp(ecParameter, ecFieldElement, ecFieldElement2, ecParameter2, ecParameter3);
                    return new ECDomainParameters(fp, deserializeECPoint(array2, fp, opaque8), ecParameter2, ecParameter3);
                }
                case 2: {
                    checkNamedCurve(array, 65282);
                    final int uint16 = TlsUtils.readUint16(inputStream);
                    final short uint17 = TlsUtils.readUint8(inputStream);
                    if (!ECBasisType.isValid(uint17)) {
                        throw new TlsFatalAlert((short)47);
                    }
                    final int ecExponent = readECExponent(uint16, inputStream);
                    int ecExponent2 = -1;
                    int ecExponent3 = -1;
                    if (uint17 == 2) {
                        ecExponent2 = readECExponent(uint16, inputStream);
                        ecExponent3 = readECExponent(uint16, inputStream);
                    }
                    final BigInteger ecFieldElement3 = readECFieldElement(uint16, inputStream);
                    final BigInteger ecFieldElement4 = readECFieldElement(uint16, inputStream);
                    final byte[] opaque9 = TlsUtils.readOpaque8(inputStream);
                    final BigInteger ecParameter4 = readECParameter(inputStream);
                    final BigInteger ecParameter5 = readECParameter(inputStream);
                    final ECCurve.F2m f2m = (uint17 == 2) ? new ECCurve.F2m(uint16, ecExponent, ecExponent2, ecExponent3, ecFieldElement3, ecFieldElement4, ecParameter4, ecParameter5) : new ECCurve.F2m(uint16, ecExponent, ecFieldElement3, ecFieldElement4, ecParameter4, ecParameter5);
                    return new ECDomainParameters(f2m, deserializeECPoint(array2, f2m, opaque9), ecParameter4, ecParameter5);
                }
                case 3: {
                    final int uint18 = TlsUtils.readUint16(inputStream);
                    if (!NamedCurve.refersToASpecificNamedCurve(uint18)) {
                        throw new TlsFatalAlert((short)47);
                    }
                    checkNamedCurve(array, uint18);
                    return getParametersForNamedCurve(uint18);
                }
                default: {
                    throw new TlsFatalAlert((short)47);
                }
            }
        }
        catch (final RuntimeException ex) {
            throw new TlsFatalAlert((short)47, ex);
        }
    }
    
    private static void checkNamedCurve(final int[] array, final int n) throws IOException {
        if (array != null && !Arrays.contains(array, n)) {
            throw new TlsFatalAlert((short)47);
        }
    }
    
    public static void writeECExponent(final int n, final OutputStream outputStream) throws IOException {
        writeECParameter(BigInteger.valueOf(n), outputStream);
    }
    
    public static void writeECFieldElement(final ECFieldElement ecFieldElement, final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque8(ecFieldElement.getEncoded(), outputStream);
    }
    
    public static void writeECFieldElement(final int n, final BigInteger bigInteger, final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque8(serializeECFieldElement(n, bigInteger), outputStream);
    }
    
    public static void writeECParameter(final BigInteger bigInteger, final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque8(BigIntegers.asUnsignedByteArray(bigInteger), outputStream);
    }
    
    public static void writeExplicitECParameters(final short[] array, final ECDomainParameters ecDomainParameters, final OutputStream outputStream) throws IOException {
        final ECCurve curve = ecDomainParameters.getCurve();
        if (ECAlgorithms.isFpCurve(curve)) {
            TlsUtils.writeUint8((short)1, outputStream);
            writeECParameter(curve.getField().getCharacteristic(), outputStream);
        }
        else {
            if (!ECAlgorithms.isF2mCurve(curve)) {
                throw new IllegalArgumentException("'ecParameters' not a known curve type");
            }
            final int[] exponentsPresent = ((PolynomialExtensionField)curve.getField()).getMinimalPolynomial().getExponentsPresent();
            TlsUtils.writeUint8((short)2, outputStream);
            final int n = exponentsPresent[exponentsPresent.length - 1];
            TlsUtils.checkUint16(n);
            TlsUtils.writeUint16(n, outputStream);
            if (exponentsPresent.length == 3) {
                TlsUtils.writeUint8((short)1, outputStream);
                writeECExponent(exponentsPresent[1], outputStream);
            }
            else {
                if (exponentsPresent.length != 5) {
                    throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
                }
                TlsUtils.writeUint8((short)2, outputStream);
                writeECExponent(exponentsPresent[1], outputStream);
                writeECExponent(exponentsPresent[2], outputStream);
                writeECExponent(exponentsPresent[3], outputStream);
            }
        }
        writeECFieldElement(curve.getA(), outputStream);
        writeECFieldElement(curve.getB(), outputStream);
        TlsUtils.writeOpaque8(serializeECPoint(array, ecDomainParameters.getG()), outputStream);
        writeECParameter(ecDomainParameters.getN(), outputStream);
        writeECParameter(ecDomainParameters.getH(), outputStream);
    }
    
    public static void writeECPoint(final short[] array, final ECPoint ecPoint, final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque8(serializeECPoint(array, ecPoint), outputStream);
    }
    
    public static void writeNamedECParameters(final int n, final OutputStream outputStream) throws IOException {
        if (!NamedCurve.refersToASpecificNamedCurve(n)) {
            throw new TlsFatalAlert((short)80);
        }
        TlsUtils.writeUint8((short)3, outputStream);
        TlsUtils.checkUint16(n);
        TlsUtils.writeUint16(n, outputStream);
    }
    
    static {
        EXT_elliptic_curves = Integers.valueOf(10);
        EXT_ec_point_formats = Integers.valueOf(11);
        CURVE_NAMES = new String[] { "sect163k1", "sect163r1", "sect163r2", "sect193r1", "sect193r2", "sect233k1", "sect233r1", "sect239k1", "sect283k1", "sect283r1", "sect409k1", "sect409r1", "sect571k1", "sect571r1", "secp160k1", "secp160r1", "secp160r2", "secp192k1", "secp192r1", "secp224k1", "secp224r1", "secp256k1", "secp256r1", "secp384r1", "secp521r1", "brainpoolP256r1", "brainpoolP384r1", "brainpoolP512r1" };
    }
}
