package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import java.math.BigInteger;
import java.util.Arrays;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.KeyFactory;
import java.io.IOException;
import javax.xml.crypto.MarshalException;
import java.security.PublicKey;
import java.security.spec.ECParameterSpec;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.security.interfaces.ECPublicKey;
import java.security.Key;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import java.security.spec.ECPoint;
import java.security.spec.ECField;
import java.security.spec.EllipticCurve;
import java.security.spec.ECFieldFp;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;

public class ECKeyValue extends Signature11ElementProxy implements KeyValueContent
{
    private static final Curve SECP256R1;
    private static final Curve SECP384R1;
    private static final Curve SECP521R1;
    
    private static Curve initializeCurve(final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final int n) {
        return new Curve(s, s2, new EllipticCurve(new ECFieldFp(bigInt(s3)), bigInt(s4), bigInt(s5)), new ECPoint(bigInt(s6), bigInt(s7)), bigInt(s8), n);
    }
    
    public ECKeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public ECKeyValue(final Document document, final Key key) throws IllegalArgumentException {
        super(document);
        this.addReturnToSelf();
        if (!(key instanceof ECPublicKey)) {
            throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", new Object[] { "ECKeyValue", key.getClass().getName() }));
        }
        final ECParameterSpec params = ((ECPublicKey)key).getParams();
        final String curveOid = getCurveOid(params);
        if (curveOid == null) {
            throw new IllegalArgumentException("Invalid ECParameterSpec");
        }
        final Element elementInSignature11Space = XMLUtils.createElementInSignature11Space(this.getDocument(), "NamedCurve");
        elementInSignature11Space.setAttributeNS(null, "URI", "urn:oid:" + curveOid);
        this.appendSelf(elementInSignature11Space);
        this.addReturnToSelf();
        final String encodeToString = XMLUtils.encodeToString(encodePoint(((ECPublicKey)key).getW(), params.getCurve()));
        final Element elementInSignature11Space2 = XMLUtils.createElementInSignature11Space(this.getDocument(), "PublicKey");
        elementInSignature11Space2.appendChild(this.getDocument().createTextNode(encodeToString));
        this.appendSelf(elementInSignature11Space2);
        this.addReturnToSelf();
    }
    
    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            final Element firstChildElement = getFirstChildElement(this.getElement());
            if (firstChildElement == null) {
                throw new MarshalException("KeyValue must contain at least one type");
            }
            if ("ECParameters".equals(firstChildElement.getLocalName()) && "http://www.w3.org/2009/xmldsig11#".equals(firstChildElement.getNamespaceURI())) {
                throw new UnsupportedOperationException("ECParameters not supported");
            }
            if (!"NamedCurve".equals(firstChildElement.getLocalName()) || !"http://www.w3.org/2009/xmldsig11#".equals(firstChildElement.getNamespaceURI())) {
                throw new MarshalException("Invalid ECKeyValue");
            }
            String attributeNS = null;
            if (firstChildElement.hasAttributeNS(null, "URI")) {
                attributeNS = firstChildElement.getAttributeNS(null, "URI");
            }
            if (!attributeNS.startsWith("urn:oid:")) {
                throw new MarshalException("Invalid NamedCurve URI");
            }
            final ECParameterSpec ecParameterSpec = getECParameterSpec(attributeNS.substring("urn:oid:".length()));
            if (ecParameterSpec == null) {
                throw new MarshalException("Invalid curve OID");
            }
            final Element nextSiblingElement = getNextSiblingElement(firstChildElement, "PublicKey", "http://www.w3.org/2009/xmldsig11#");
            ECPoint decodePoint;
            try {
                decodePoint = decodePoint(XMLUtils.decode(XMLUtils.getFullTextChildrenFromNode(nextSiblingElement)), ecParameterSpec.getCurve());
            }
            catch (final IOException ex) {
                throw new MarshalException("Invalid EC Point", ex);
            }
            return KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(decodePoint, ecParameterSpec));
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new XMLSecurityException(ex2);
        }
        catch (final InvalidKeySpecException ex3) {
            throw new XMLSecurityException(ex3);
        }
        catch (final MarshalException ex4) {
            throw new XMLSecurityException(ex4);
        }
    }
    
    @Override
    public String getBaseLocalName() {
        return "ECKeyValue";
    }
    
    private static Element getFirstChildElement(final Node node) {
        Node node2;
        for (node2 = node.getFirstChild(); node2 != null && node2.getNodeType() != 1; node2 = node2.getNextSibling()) {}
        return (Element)node2;
    }
    
    private static Element getNextSiblingElement(final Node node, final String s, final String s2) throws MarshalException {
        return verifyElement(getNextSiblingElement(node), s, s2);
    }
    
    private static Element getNextSiblingElement(final Node node) {
        Node node2;
        for (node2 = node.getNextSibling(); node2 != null && node2.getNodeType() != 1; node2 = node2.getNextSibling()) {}
        return (Element)node2;
    }
    
    private static Element verifyElement(final Element element, final String s, final String s2) throws MarshalException {
        if (element == null) {
            throw new MarshalException("Missing " + s + " element");
        }
        final String localName = element.getLocalName();
        final String namespaceURI = element.getNamespaceURI();
        if (!localName.equals(s) || (namespaceURI == null && s2 != null) || (namespaceURI != null && !namespaceURI.equals(s2))) {
            throw new MarshalException("Invalid element name: " + namespaceURI + ":" + localName + ", expected " + s2 + ":" + s);
        }
        return element;
    }
    
    private static String getCurveOid(final ECParameterSpec ecParameterSpec) {
        Curve curve;
        if (matchCurve(ecParameterSpec, ECKeyValue.SECP256R1)) {
            curve = ECKeyValue.SECP256R1;
        }
        else if (matchCurve(ecParameterSpec, ECKeyValue.SECP384R1)) {
            curve = ECKeyValue.SECP384R1;
        }
        else {
            if (!matchCurve(ecParameterSpec, ECKeyValue.SECP521R1)) {
                return null;
            }
            curve = ECKeyValue.SECP521R1;
        }
        return curve.getObjectId();
    }
    
    private static boolean matchCurve(final ECParameterSpec ecParameterSpec, final Curve curve) {
        return curve.getCurve().getField().getFieldSize() == ecParameterSpec.getCurve().getField().getFieldSize() && curve.getCurve().equals(ecParameterSpec.getCurve()) && curve.getGenerator().equals(ecParameterSpec.getGenerator()) && curve.getOrder().equals(ecParameterSpec.getOrder()) && curve.getCofactor() == ecParameterSpec.getCofactor();
    }
    
    private static ECPoint decodePoint(final byte[] array, final EllipticCurve ellipticCurve) throws IOException {
        if (array.length == 0 || array[0] != 4) {
            throw new IOException("Only uncompressed point format supported");
        }
        final int n = (array.length - 1) / 2;
        if (n != ellipticCurve.getField().getFieldSize() + 7 >> 3) {
            throw new IOException("Point does not match field size");
        }
        return new ECPoint(new BigInteger(1, Arrays.copyOfRange(array, 1, 1 + n)), new BigInteger(1, Arrays.copyOfRange(array, n + 1, n + 1 + n)));
    }
    
    private static byte[] encodePoint(final ECPoint ecPoint, final EllipticCurve ellipticCurve) {
        final int n = ellipticCurve.getField().getFieldSize() + 7 >> 3;
        final byte[] trimZeroes = trimZeroes(ecPoint.getAffineX().toByteArray());
        final byte[] trimZeroes2 = trimZeroes(ecPoint.getAffineY().toByteArray());
        if (trimZeroes.length > n || trimZeroes2.length > n) {
            throw new RuntimeException("Point coordinates do not match field size");
        }
        final byte[] array = new byte[1 + (n << 1)];
        array[0] = 4;
        System.arraycopy(trimZeroes, 0, array, n - trimZeroes.length + 1, trimZeroes.length);
        System.arraycopy(trimZeroes2, 0, array, array.length - trimZeroes2.length, trimZeroes2.length);
        return array;
    }
    
    private static byte[] trimZeroes(final byte[] array) {
        int n;
        for (n = 0; n < array.length - 1 && array[n] == 0; ++n) {}
        if (n == 0) {
            return array;
        }
        return Arrays.copyOfRange(array, n, array.length);
    }
    
    private static ECParameterSpec getECParameterSpec(final String s) {
        if (s.equals(ECKeyValue.SECP256R1.getObjectId())) {
            return ECKeyValue.SECP256R1;
        }
        if (s.equals(ECKeyValue.SECP384R1.getObjectId())) {
            return ECKeyValue.SECP384R1;
        }
        if (s.equals(ECKeyValue.SECP521R1.getObjectId())) {
            return ECKeyValue.SECP521R1;
        }
        return null;
    }
    
    private static BigInteger bigInt(final String s) {
        return new BigInteger(s, 16);
    }
    
    static {
        SECP256R1 = initializeCurve("secp256r1 [NIST P-256, X9.62 prime256v1]", "1.2.840.10045.3.1.7", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 1);
        SECP384R1 = initializeCurve("secp384r1 [NIST P-384]", "1.3.132.0.34", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", "B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", "3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973", 1);
        SECP521R1 = initializeCurve("secp521r1 [NIST P-521]", "1.3.132.0.35", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC", "0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00", "00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66", "011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409", 1);
    }
    
    static final class Curve extends ECParameterSpec
    {
        private final String name;
        private final String oid;
        
        Curve(final String name, final String oid, final EllipticCurve ellipticCurve, final ECPoint ecPoint, final BigInteger bigInteger, final int n) {
            super(ellipticCurve, ecPoint, bigInteger, n);
            this.name = name;
            this.oid = oid;
        }
        
        private String getName() {
            return this.name;
        }
        
        private String getObjectId() {
            return this.oid;
        }
    }
}
