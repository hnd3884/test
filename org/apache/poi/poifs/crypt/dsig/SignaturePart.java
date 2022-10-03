package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import org.w3c.dom.Document;
import javax.xml.xpath.XPath;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.MarshalException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.apache.poi.EncryptedDocumentException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.w3c.dom.Node;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import org.w3c.dom.Element;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;
import org.apache.poi.ooxml.util.DocumentHelper;
import javax.xml.namespace.NamespaceContext;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import java.util.List;
import java.security.cert.X509Certificate;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.POILogger;

public class SignaturePart
{
    private static final POILogger LOG;
    private static final String XMLSEC_VALIDATE_MANIFEST = "org.jcp.xml.dsig.validateManifests";
    private final PackagePart signaturePart;
    private final SignatureConfig signatureConfig;
    private X509Certificate signer;
    private List<X509Certificate> certChain;
    
    SignaturePart(final PackagePart signaturePart, final SignatureConfig signatureConfig) {
        this.signaturePart = signaturePart;
        this.signatureConfig = signatureConfig;
    }
    
    public PackagePart getPackagePart() {
        return this.signaturePart;
    }
    
    public X509Certificate getSigner() {
        return this.signer;
    }
    
    public List<X509Certificate> getCertChain() {
        return this.certChain;
    }
    
    public SignatureDocument getSignatureDocument() throws IOException, XmlException {
        return SignatureDocument.Factory.parse(this.signaturePart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    public boolean validate() {
        final KeyInfoKeySelector keySelector = new KeyInfoKeySelector();
        final XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new XPathNSContext());
        try {
            final Document doc = DocumentHelper.readDocument(this.signaturePart.getInputStream());
            final NodeList nl = (NodeList)xpath.compile("//*[@Id]").evaluate(doc, XPathConstants.NODESET);
            for (int length = nl.getLength(), i = 0; i < length; ++i) {
                ((Element)nl.item(i)).setIdAttribute("Id", true);
            }
            final DOMValidateContext domValidateContext = new DOMValidateContext(keySelector, doc);
            domValidateContext.setProperty("org.jcp.xml.dsig.validateManifests", Boolean.TRUE);
            domValidateContext.setURIDereferencer(this.signatureConfig.getUriDereferencer());
            final XMLSignatureFactory xmlSignatureFactory = this.signatureConfig.getSignatureFactory();
            final XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
            final boolean valid = xmlSignature.validate(domValidateContext);
            if (valid) {
                this.signer = keySelector.getSigner();
                this.certChain = keySelector.getCertChain();
                this.extractConfig(doc, xmlSignature);
            }
            return valid;
        }
        catch (final IOException e) {
            final String s = "error in reading document";
            SignaturePart.LOG.log(7, new Object[] { s, e });
            throw new EncryptedDocumentException(s, (Throwable)e);
        }
        catch (final SAXException e2) {
            final String s = "error in parsing document";
            SignaturePart.LOG.log(7, new Object[] { s, e2 });
            throw new EncryptedDocumentException(s, (Throwable)e2);
        }
        catch (final XPathExpressionException e3) {
            final String s = "error in searching document with xpath expression";
            SignaturePart.LOG.log(7, new Object[] { s, e3 });
            throw new EncryptedDocumentException(s, (Throwable)e3);
        }
        catch (final MarshalException e4) {
            final String s = "error in unmarshalling the signature";
            SignaturePart.LOG.log(7, new Object[] { s, e4 });
            throw new EncryptedDocumentException(s, (Throwable)e4);
        }
        catch (final XMLSignatureException e5) {
            final String s = "error in validating the signature";
            SignaturePart.LOG.log(7, new Object[] { s, e5 });
            throw new EncryptedDocumentException(s, (Throwable)e5);
        }
    }
    
    private void extractConfig(final Document doc, final XMLSignature xmlSignature) throws XPathExpressionException {
        if (!this.signatureConfig.isUpdateConfigOnValidate()) {
            return;
        }
        this.signatureConfig.setSigningCertificateChain(this.certChain);
        this.signatureConfig.setSignatureMethodFromUri(xmlSignature.getSignedInfo().getSignatureMethod().getAlgorithm());
        final XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new XPathNSContext());
        final Map<String, Consumer<String>> m = new HashMap<String, Consumer<String>>();
        m.put("//mdssi:SignatureTime/mdssi:Value", this.signatureConfig::setExecutionTime);
        m.put("//xd:ClaimedRole", this.signatureConfig::setXadesRole);
        m.put("//dsss:SignatureComments", this.signatureConfig::setSignatureDescription);
        m.put("//xd:QualifyingProperties//xd:SignedSignatureProperties//ds:DigestMethod/@Algorithm", this.signatureConfig::setXadesDigestAlgo);
        m.put("//ds:CanonicalizationMethod", this.signatureConfig::setCanonicalizationMethod);
        for (final Map.Entry<String, Consumer<String>> me : m.entrySet()) {
            final String val = (String)xpath.compile(me.getKey()).evaluate(doc, XPathConstants.STRING);
            me.getValue().accept(val);
        }
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)SignaturePart.class);
    }
    
    private class XPathNSContext implements NamespaceContext
    {
        final Map<String, String> nsMap;
        
        private XPathNSContext() {
            this.nsMap = new HashMap<String, String>();
            SignaturePart.this.signatureConfig.getNamespacePrefixes().forEach((k, v) -> {
                final String s = this.nsMap.put(v, k);
                return;
            });
            this.nsMap.put("dsss", "http://schemas.microsoft.com/office/2006/digsig");
            this.nsMap.put("ds", "http://www.w3.org/2000/09/xmldsig#");
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            return this.nsMap.get(prefix);
        }
        
        @Override
        public Iterator getPrefixes(final String val) {
            return null;
        }
        
        @Override
        public String getPrefix(final String uri) {
            return null;
        }
    }
}
