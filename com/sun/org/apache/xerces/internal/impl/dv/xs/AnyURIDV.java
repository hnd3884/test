package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.io.UnsupportedEncodingException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.URI;

public class AnyURIDV extends TypeValidator
{
    private static final URI BASE_URI;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            if (content.length() != 0) {
                final String encoded = encode(content);
                new URI(AnyURIDV.BASE_URI, encoded);
            }
        }
        catch (final URI.MalformedURIException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "anyURI" });
        }
        return content;
    }
    
    private static String encode(final String anyURI) {
        final int len = anyURI.length();
        final StringBuffer buffer = new StringBuffer(len * 3);
        int i;
        for (i = 0; i < len; ++i) {
            final int ch = anyURI.charAt(i);
            if (ch >= 128) {
                break;
            }
            if (AnyURIDV.gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(AnyURIDV.gAfterEscaping1[ch]);
                buffer.append(AnyURIDV.gAfterEscaping2[ch]);
            }
            else {
                buffer.append((char)ch);
            }
        }
        if (i < len) {
            byte[] bytes = null;
            try {
                bytes = anyURI.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                return anyURI;
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int ch = b + 256;
                    buffer.append('%');
                    buffer.append(AnyURIDV.gHexChs[ch >> 4]);
                    buffer.append(AnyURIDV.gHexChs[ch & 0xF]);
                }
                else if (AnyURIDV.gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(AnyURIDV.gAfterEscaping1[b]);
                    buffer.append(AnyURIDV.gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        if (buffer.length() != len) {
            return buffer.toString();
        }
        return anyURI;
    }
    
    static {
        URI uri = null;
        try {
            uri = new URI("abc://def.ghi.jkl");
        }
        catch (final URI.MalformedURIException ex) {}
        BASE_URI = uri;
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
        for (final char ch : new char[] { ' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '~', '`' }) {
            AnyURIDV.gNeedEscaping[ch] = true;
            AnyURIDV.gAfterEscaping1[ch] = AnyURIDV.gHexChs[ch >> 4];
            AnyURIDV.gAfterEscaping2[ch] = AnyURIDV.gHexChs[ch & '\u000f'];
        }
    }
}
