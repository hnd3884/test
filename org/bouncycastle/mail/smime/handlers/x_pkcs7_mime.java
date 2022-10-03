package org.bouncycastle.mail.smime.handlers;

import javax.mail.internet.MimeBodyPart;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;

public class x_pkcs7_mime extends PKCS7ContentHandler
{
    private static final ActivationDataFlavor ADF;
    private static final DataFlavor[] DFS;
    
    public x_pkcs7_mime() {
        super(x_pkcs7_mime.ADF, x_pkcs7_mime.DFS);
    }
    
    static {
        ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/x-pkcs7-mime", "Encrypted Data");
        DFS = new DataFlavor[] { x_pkcs7_mime.ADF };
    }
}
