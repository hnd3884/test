package com.sun.org.apache.xml.internal.security.algorithms;

import org.w3c.dom.Element;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;

public abstract class SignatureAlgorithmSpi
{
    protected abstract String engineGetURI();
    
    protected abstract String engineGetJCEAlgorithmString();
    
    protected abstract String engineGetJCEProviderName();
    
    protected abstract void engineUpdate(final byte[] p0) throws XMLSignatureException;
    
    protected abstract void engineUpdate(final byte p0) throws XMLSignatureException;
    
    protected abstract void engineUpdate(final byte[] p0, final int p1, final int p2) throws XMLSignatureException;
    
    protected abstract void engineInitSign(final Key p0) throws XMLSignatureException;
    
    protected abstract void engineInitSign(final Key p0, final SecureRandom p1) throws XMLSignatureException;
    
    protected abstract void engineInitSign(final Key p0, final AlgorithmParameterSpec p1) throws XMLSignatureException;
    
    protected abstract byte[] engineSign() throws XMLSignatureException;
    
    protected abstract void engineInitVerify(final Key p0) throws XMLSignatureException;
    
    protected abstract boolean engineVerify(final byte[] p0) throws XMLSignatureException;
    
    protected abstract void engineSetParameter(final AlgorithmParameterSpec p0) throws XMLSignatureException;
    
    protected void engineGetContextFromElement(final Element element) {
    }
    
    protected abstract void engineSetHMACOutputLength(final int p0) throws XMLSignatureException;
    
    public void reset() {
    }
}
