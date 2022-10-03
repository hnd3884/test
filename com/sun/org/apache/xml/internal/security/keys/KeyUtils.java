package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.security.PublicKey;
import java.io.PrintStream;

public final class KeyUtils
{
    private KeyUtils() {
    }
    
    public static void prinoutKeyInfo(final KeyInfo keyInfo, final PrintStream printStream) throws XMLSecurityException {
        for (int i = 0; i < keyInfo.lengthKeyName(); ++i) {
            printStream.println("KeyName(" + i + ")=\"" + keyInfo.itemKeyName(i).getKeyName() + "\"");
        }
        for (int j = 0; j < keyInfo.lengthKeyValue(); ++j) {
            final PublicKey publicKey = keyInfo.itemKeyValue(j).getPublicKey();
            printStream.println("KeyValue Nr. " + j);
            printStream.println(publicKey);
        }
        for (int k = 0; k < keyInfo.lengthMgmtData(); ++k) {
            printStream.println("MgmtData(" + k + ")=\"" + keyInfo.itemMgmtData(k).getMgmtData() + "\"");
        }
        for (int l = 0; l < keyInfo.lengthX509Data(); ++l) {
            final X509Data itemX509Data = keyInfo.itemX509Data(l);
            printStream.println("X509Data(" + l + ")=\"" + (itemX509Data.containsCertificate() ? "Certificate " : "") + (itemX509Data.containsIssuerSerial() ? "IssuerSerial " : "") + "\"");
        }
    }
}
