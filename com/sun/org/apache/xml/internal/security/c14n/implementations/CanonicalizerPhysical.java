package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import org.w3c.dom.DOMException;
import java.util.Iterator;
import java.io.OutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Map;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Node;
import java.util.Set;

public class CanonicalizerPhysical extends CanonicalizerBase
{
    public CanonicalizerPhysical() {
        super(true);
    }
    
    @Override
    public byte[] engineCanonicalizeXPathNodeSet(final Set<Node> set, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s, final boolean b) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    protected void outputAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        if (element.hasAttributes()) {
            final TreeSet set = new TreeSet((Comparator<? super E>)CanonicalizerPhysical.COMPARE);
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                set.add(attributes.item(i));
            }
            final OutputStream writer = this.getWriter();
            for (final Attr attr : set) {
                CanonicalizerBase.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, map);
            }
        }
    }
    
    protected void outputAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    protected void circumventBugIfNeeded(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
    }
    
    @Override
    protected void handleParent(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
    }
    
    @Override
    public final String engineGetURI() {
        return "http://santuario.apache.org/c14n/physical";
    }
    
    @Override
    public final boolean engineGetIncludeComments() {
        return true;
    }
    
    @Override
    protected void outputPItoWriter(final ProcessingInstruction processingInstruction, final OutputStream outputStream, final int n) throws IOException {
        super.outputPItoWriter(processingInstruction, outputStream, 0);
    }
    
    @Override
    protected void outputCommentToWriter(final Comment comment, final OutputStream outputStream, final int n) throws IOException {
        super.outputCommentToWriter(comment, outputStream, 0);
    }
}
