package org.apache.poi.poifs.crypt.dsig.facets;

import org.apache.poi.util.POILogFactory;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import org.w3c.dom.NodeList;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import java.util.function.BiConsumer;
import org.w3c.dom.Node;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import java.security.Key;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfo;
import java.util.List;
import java.util.Collection;
import java.security.KeyException;
import javax.xml.crypto.XMLStructure;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.apache.poi.util.POILogger;

public class KeyInfoSignatureFacet extends SignatureFacet
{
    private static final POILogger LOG;
    
    @Override
    public void postSign(final Document document) throws MarshalException {
        KeyInfoSignatureFacet.LOG.log(1, new Object[] { "postSign" });
        final NodeList nl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Object");
        final Node nextSibling = (nl.getLength() == 0) ? null : nl.item(0);
        final KeyInfoFactory keyInfoFactory = this.signatureConfig.getKeyInfoFactory();
        final List<Object> x509DataObjects = new ArrayList<Object>();
        final X509Certificate signingCertificate = this.signatureConfig.getSigningCertificateChain().get(0);
        final List<XMLStructure> keyInfoContent = new ArrayList<XMLStructure>();
        if (this.signatureConfig.isIncludeKeyValue()) {
            KeyValue keyValue;
            try {
                keyValue = keyInfoFactory.newKeyValue(signingCertificate.getPublicKey());
            }
            catch (final KeyException e) {
                throw new RuntimeException("key exception: " + e.getMessage(), e);
            }
            keyInfoContent.add(keyValue);
        }
        if (this.signatureConfig.isIncludeIssuerSerial()) {
            x509DataObjects.add(keyInfoFactory.newX509IssuerSerial(signingCertificate.getIssuerX500Principal().toString(), signingCertificate.getSerialNumber()));
        }
        if (this.signatureConfig.isIncludeEntireCertificateChain()) {
            x509DataObjects.addAll(this.signatureConfig.getSigningCertificateChain());
        }
        else {
            x509DataObjects.add(signingCertificate);
        }
        if (!x509DataObjects.isEmpty()) {
            final X509Data x509Data = keyInfoFactory.newX509Data(x509DataObjects);
            keyInfoContent.add(x509Data);
        }
        final KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoContent);
        final DOMKeyInfo domKeyInfo = (DOMKeyInfo)keyInfo;
        final Key key = new Key() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public String getAlgorithm() {
                return null;
            }
            
            @Override
            public byte[] getEncoded() {
                return null;
            }
            
            @Override
            public String getFormat() {
                return null;
            }
        };
        final Element n = document.getDocumentElement();
        final DOMSignContext domSignContext = (nextSibling == null) ? new DOMSignContext(key, n) : new DOMSignContext(key, n, nextSibling);
        this.signatureConfig.getNamespacePrefixes().forEach(domSignContext::putNamespacePrefix);
        final DOMStructure domStructure = new DOMStructure(n);
        domKeyInfo.marshal((XMLStructure)domStructure, (XMLCryptoContext)domSignContext);
        if (nextSibling != null) {
            final NodeList kiNl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
            if (kiNl.getLength() != 1) {
                throw new RuntimeException("KeyInfo wasn't set");
            }
            nextSibling.getParentNode().insertBefore(kiNl.item(0), nextSibling);
        }
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)KeyInfoSignatureFacet.class);
    }
}
