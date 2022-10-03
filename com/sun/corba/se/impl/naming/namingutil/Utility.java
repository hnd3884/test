package com.sun.corba.se.impl.naming.namingutil;

import org.omg.CORBA.DATA_CONVERSION;
import java.io.StringWriter;
import com.sun.corba.se.impl.logging.NamingSystemException;

class Utility
{
    private static NamingSystemException wrapper;
    
    static String cleanEscapes(final String s) {
        final StringWriter stringWriter = new StringWriter();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 != '%') {
                stringWriter.write(char1);
            }
            else {
                ++i;
                final int hex = hexOf(s.charAt(i));
                ++i;
                stringWriter.write((char)(hex * 16 + hexOf(s.charAt(i))));
            }
        }
        return stringWriter.toString();
    }
    
    static int hexOf(final char c) {
        final int n = c - '0';
        if (n >= 0 && n <= 9) {
            return n;
        }
        final int n2 = c - 'a' + 10;
        if (n2 >= 10 && n2 <= 15) {
            return n2;
        }
        final int n3 = c - 'A' + 10;
        if (n3 >= 10 && n3 <= 15) {
            return n3;
        }
        throw new DATA_CONVERSION();
    }
    
    static void validateGIOPVersion(final IIOPEndpointInfo iiopEndpointInfo) {
        if (iiopEndpointInfo.getMajor() > 1 || iiopEndpointInfo.getMinor() > 2) {
            throw Utility.wrapper.insBadAddress();
        }
    }
    
    static {
        Utility.wrapper = NamingSystemException.get("naming");
    }
}
