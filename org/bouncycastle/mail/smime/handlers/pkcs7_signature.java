package org.bouncycastle.mail.smime.handlers;

import javax.mail.internet.MimeBodyPart;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;

public class pkcs7_signature extends PKCS7ContentHandler
{
    private static final ActivationDataFlavor ADF;
    private static final DataFlavor[] DFS;
    
    public pkcs7_signature() {
        super(pkcs7_signature.ADF, pkcs7_signature.DFS);
    }
    
    static {
        ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/pkcs7-signature", "Signature");
        DFS = new DataFlavor[] { pkcs7_signature.ADF };
    }
}
