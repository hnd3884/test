package org.bouncycastle.mail.smime.handlers;

import javax.mail.internet.MimeBodyPart;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;

public class pkcs7_mime extends PKCS7ContentHandler
{
    private static final ActivationDataFlavor ADF;
    private static final DataFlavor[] DFS;
    
    public pkcs7_mime() {
        super(pkcs7_mime.ADF, pkcs7_mime.DFS);
    }
    
    static {
        ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/pkcs7-mime", "Encrypted Data");
        DFS = new DataFlavor[] { pkcs7_mime.ADF };
    }
}
