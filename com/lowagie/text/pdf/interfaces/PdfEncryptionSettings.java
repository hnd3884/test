package com.lowagie.text.pdf.interfaces;

import java.security.cert.Certificate;
import com.lowagie.text.DocumentException;

public interface PdfEncryptionSettings
{
    void setEncryption(final byte[] p0, final byte[] p1, final int p2, final int p3) throws DocumentException;
    
    void setEncryption(final Certificate[] p0, final int[] p1, final int p2) throws DocumentException;
}
