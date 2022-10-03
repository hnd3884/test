package com.lowagie.text.pdf;

public interface TSAClient
{
    int getTokenSizeEstimate();
    
    byte[] getTimeStampToken(final PdfPKCS7 p0, final byte[] p1) throws Exception;
}
