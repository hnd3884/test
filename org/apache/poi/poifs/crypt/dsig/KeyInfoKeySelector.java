package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.util.POILogFactory;
import java.security.Key;
import java.util.Iterator;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.List;
import org.apache.poi.util.POILogger;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.KeySelector;

public class KeyInfoKeySelector extends KeySelector implements KeySelectorResult
{
    private static final POILogger LOG;
    private List<X509Certificate> certChain;
    
    public KeyInfoKeySelector() {
        this.certChain = new ArrayList<X509Certificate>();
    }
    
    @Override
    public KeySelectorResult select(final KeyInfo keyInfo, final Purpose purpose, final AlgorithmMethod method, final XMLCryptoContext context) throws KeySelectorException {
        KeyInfoKeySelector.LOG.log(1, new Object[] { "select key" });
        if (null == keyInfo) {
            throw new KeySelectorException("no ds:KeyInfo present");
        }
        final List<XMLStructure> keyInfoContent = keyInfo.getContent();
        this.certChain.clear();
        for (final XMLStructure keyInfoStructure : keyInfoContent) {
            if (!(keyInfoStructure instanceof X509Data)) {
                continue;
            }
            final X509Data x509Data = (X509Data)keyInfoStructure;
            final List<?> x509DataList = x509Data.getContent();
            for (final Object x509DataObject : x509DataList) {
                if (!(x509DataObject instanceof X509Certificate)) {
                    continue;
                }
                final X509Certificate certificate = (X509Certificate)x509DataObject;
                KeyInfoKeySelector.LOG.log(1, new Object[] { "certificate", certificate.getSubjectX500Principal() });
                this.certChain.add(certificate);
            }
        }
        if (this.certChain.isEmpty()) {
            throw new KeySelectorException("No key found!");
        }
        return this;
    }
    
    @Override
    public Key getKey() {
        return this.certChain.isEmpty() ? null : this.certChain.get(0).getPublicKey();
    }
    
    public X509Certificate getSigner() {
        return this.certChain.isEmpty() ? null : this.certChain.get(0);
    }
    
    public List<X509Certificate> getCertChain() {
        return this.certChain;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)KeyInfoKeySelector.class);
    }
}
