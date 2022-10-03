package org.apache.xml.security.encryption;

import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.Element;
import java.util.Iterator;

public interface AgreementMethod
{
    byte[] getKANonce();
    
    void setKANonce(final byte[] p0);
    
    Iterator getAgreementMethodInformation();
    
    void addAgreementMethodInformation(final Element p0);
    
    void revoveAgreementMethodInformation(final Element p0);
    
    KeyInfo getOriginatorKeyInfo();
    
    void setOriginatorKeyInfo(final KeyInfo p0);
    
    KeyInfo getRecipientKeyInfo();
    
    void setRecipientKeyInfo(final KeyInfo p0);
    
    String getAlgorithm();
}
