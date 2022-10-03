package org.apache.poi.poifs.crypt.dsig.services;

import org.apache.poi.util.POILogFactory;
import java.io.OutputStream;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData;
import org.apache.xml.security.signature.XMLSignatureInput;
import java.util.TreeMap;
import javax.xml.crypto.dsig.TransformException;
import org.apache.poi.ooxml.util.DocumentHelper;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.Data;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTRelationshipReference;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.RelationshipReferenceDocument;
import org.w3.x2000.x09.xmldsig.TransformDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import java.util.Collection;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import org.apache.poi.util.POILogger;
import java.util.List;
import javax.xml.crypto.dsig.TransformService;

public class RelationshipTransformService extends TransformService
{
    public static final String TRANSFORM_URI = "http://schemas.openxmlformats.org/package/2006/RelationshipTransform";
    private final List<String> sourceIds;
    private static final POILogger LOG;
    
    public RelationshipTransformService() {
        RelationshipTransformService.LOG.log(1, new Object[] { "constructor" });
        this.sourceIds = new ArrayList<String>();
    }
    
    public static synchronized void registerDsigProvider() {
        final String dsigProvider = "POIXmlDsigProvider";
        if (Security.getProperty("POIXmlDsigProvider") == null) {
            final Provider p = new Provider("POIXmlDsigProvider", 1.0, "POIXmlDsigProvider") {
                static final long serialVersionUID = 1L;
            };
            p.put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform", RelationshipTransformService.class.getName());
            p.put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform MechanismType", "DOM");
            Security.addProvider(p);
        }
    }
    
    @Override
    public void init(final TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        RelationshipTransformService.LOG.log(1, new Object[] { "init(params)" });
        if (!(params instanceof RelationshipTransformParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        final RelationshipTransformParameterSpec relParams = (RelationshipTransformParameterSpec)params;
        this.sourceIds.addAll(relParams.sourceIds);
    }
    
    @Override
    public void init(final XMLStructure parent, final XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        RelationshipTransformService.LOG.log(1, new Object[] { "init(parent,context)" });
        RelationshipTransformService.LOG.log(1, new Object[] { "parent java type: " + parent.getClass().getName() });
        final DOMStructure domParent = (DOMStructure)parent;
        final Node parentNode = domParent.getNode();
        try {
            final TransformDocument transDoc = TransformDocument.Factory.parse(parentNode, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            final XmlObject[] xoList = transDoc.getTransform().selectChildren(RelationshipReferenceDocument.type.getDocumentElementName());
            if (xoList.length == 0) {
                RelationshipTransformService.LOG.log(5, new Object[] { "no RelationshipReference/@SourceId parameters present" });
            }
            for (final XmlObject xo : xoList) {
                final String sourceId = ((CTRelationshipReference)xo).getSourceId();
                RelationshipTransformService.LOG.log(1, new Object[] { "sourceId: ", sourceId });
                this.sourceIds.add(sourceId);
            }
        }
        catch (final XmlException e) {
            throw new InvalidAlgorithmParameterException((Throwable)e);
        }
    }
    
    @Override
    public void marshalParams(final XMLStructure parent, final XMLCryptoContext context) throws MarshalException {
        RelationshipTransformService.LOG.log(1, new Object[] { "marshallParams(parent,context)" });
        final DOMStructure domParent = (DOMStructure)parent;
        final Element parentNode = (Element)domParent.getNode();
        final Document doc = parentNode.getOwnerDocument();
        for (final String sourceId : this.sourceIds) {
            final Element el = doc.createElementNS("http://schemas.openxmlformats.org/package/2006/digital-signature", "mdssi:RelationshipReference");
            el.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", "http://schemas.openxmlformats.org/package/2006/digital-signature");
            el.setAttribute("SourceId", sourceId);
            parentNode.appendChild(el);
        }
    }
    
    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        RelationshipTransformService.LOG.log(1, new Object[] { "getParameterSpec" });
        return null;
    }
    
    @Override
    public Data transform(final Data data, final XMLCryptoContext context) throws TransformException {
        RelationshipTransformService.LOG.log(1, new Object[] { "transform(data,context)" });
        RelationshipTransformService.LOG.log(1, new Object[] { "data java type: " + data.getClass().getName() });
        final OctetStreamData octetStreamData = (OctetStreamData)data;
        RelationshipTransformService.LOG.log(1, new Object[] { "URI: " + octetStreamData.getURI() });
        final InputStream octetStream = octetStreamData.getOctetStream();
        Document doc;
        try {
            doc = DocumentHelper.readDocument(octetStream);
        }
        catch (final Exception e) {
            throw new TransformException(e.getMessage(), e);
        }
        final Element root = doc.getDocumentElement();
        final NodeList nl = root.getChildNodes();
        final TreeMap<String, Element> rsList = new TreeMap<String, Element>();
        for (int i = nl.getLength() - 1; i >= 0; --i) {
            final Node n = nl.item(i);
            if ("Relationship".equals(n.getLocalName())) {
                final Element el = (Element)n;
                final String id = el.getAttribute("Id");
                if (this.sourceIds.contains(id)) {
                    final String targetMode = el.getAttribute("TargetMode");
                    if (targetMode == null || targetMode.isEmpty()) {
                        el.setAttribute("TargetMode", "Internal");
                    }
                    rsList.put(id, el);
                }
            }
            root.removeChild(n);
        }
        for (final Element el2 : rsList.values()) {
            root.appendChild(el2);
        }
        RelationshipTransformService.LOG.log(1, new Object[] { "# Relationship elements: ", rsList.size() });
        return (Data)new ApacheNodeSetData(new XMLSignatureInput((Node)root));
    }
    
    @Override
    public Data transform(final Data data, final XMLCryptoContext context, final OutputStream os) throws TransformException {
        RelationshipTransformService.LOG.log(1, new Object[] { "transform(data,context,os)" });
        return null;
    }
    
    @Override
    public boolean isFeatureSupported(final String feature) {
        RelationshipTransformService.LOG.log(1, new Object[] { "isFeatureSupported(feature)" });
        return false;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)RelationshipTransformService.class);
    }
    
    public static class RelationshipTransformParameterSpec implements TransformParameterSpec
    {
        List<String> sourceIds;
        
        public RelationshipTransformParameterSpec() {
            this.sourceIds = new ArrayList<String>();
        }
        
        public void addRelationshipReference(final String relationshipId) {
            this.sourceIds.add(relationshipId);
        }
        
        public boolean hasSourceIds() {
            return !this.sourceIds.isEmpty();
        }
    }
}
