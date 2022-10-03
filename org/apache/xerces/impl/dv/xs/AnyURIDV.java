package org.apache.xerces.impl.dv.xs;

import java.io.UnsupportedEncodingException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.URI;

public class AnyURIDV extends TypeValidator
{
    private static final URI BASE_URI;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!validationContext.getTypeValidatorHelper().isXMLSchema11()) {
            try {
                if (s.length() != 0) {
                    new URI(AnyURIDV.BASE_URI, encode(s));
                }
            }
            catch (final URI.MalformedURIException ex) {
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "anyURI" });
            }
        }
        return s;
    }
    
    private static String encode(final String s) {
        int n = s.length();
        final StringBuffer sb = new StringBuffer(n * 3);
        int i;
        for (i = 0; i < n; ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= '\u0080') {
                break;
            }
            if (AnyURIDV.gNeedEscaping[char1]) {
                sb.append('%');
                sb.append(AnyURIDV.gAfterEscaping1[char1]);
                sb.append(AnyURIDV.gAfterEscaping2[char1]);
            }
            else {
                sb.append(char1);
            }
        }
        if (i < n) {
            byte[] bytes;
            try {
                bytes = s.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException ex) {
                return s;
            }
            n = bytes.length;
            for (final byte b : bytes) {
                if (b < 0) {
                    final int n2 = b + 256;
                    sb.append('%');
                    sb.append(AnyURIDV.gHexChs[n2 >> 4]);
                    sb.append(AnyURIDV.gHexChs[n2 & 0xF]);
                }
                else if (AnyURIDV.gNeedEscaping[b]) {
                    sb.append('%');
                    sb.append(AnyURIDV.gAfterEscaping1[b]);
                    sb.append(AnyURIDV.gAfterEscaping2[b]);
                }
                else {
                    sb.append((char)b);
                }
            }
        }
        if (sb.length() != n) {
            return sb.toString();
        }
        return s;
    }
    
    static {
        URI base_URI = null;
        try {
            base_URI = new URI("abc://def.ghi.jkl");
        }
        catch (final URI.MalformedURIException ex) {}
        BASE_URI = base_URI;
        AnyURIDV.gNeedEscaping = new boolean[128];
        AnyURIDV.gAfterEscaping1 = new char[128];
        AnyURIDV.gAfterEscaping2 = new char[128];
        AnyURIDV.gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0; i <= 31; ++i) {
            AnyURIDV.gNeedEscaping[i] = true;
            AnyURIDV.gAfterEscaping1[i] = AnyURIDV.gHexChs[i >> 4];
            AnyURIDV.gAfterEscaping2[i] = AnyURIDV.gHexChs[i & 0xF];
        }
        AnyURIDV.gNeedEscaping[127] = true;
        AnyURIDV.gAfterEscaping1[127] = '7';
        AnyURIDV.gAfterEscaping2[127] = 'F';
        for (final char c : new char[] { ' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '~', '`' }) {
            AnyURIDV.gNeedEscaping[c] = true;
            AnyURIDV.gAfterEscaping1[c] = AnyURIDV.gHexChs[c >> 4];
            AnyURIDV.gAfterEscaping2[c] = AnyURIDV.gHexChs[c & '\u000f'];
        }
    }
}
