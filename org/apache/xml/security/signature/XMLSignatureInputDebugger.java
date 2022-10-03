package org.apache.xml.security.signature;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Comparator;
import java.util.Arrays;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import org.apache.xml.security.c14n.helper.AttrCompare;
import java.io.Writer;
import org.w3c.dom.Document;
import java.util.Set;

public class XMLSignatureInputDebugger
{
    private Set _xpathNodeSet;
    private Set _inclusiveNamespaces;
    private Document _doc;
    private Writer _writer;
    static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n";
    static final String HTMLSuffix = "</pre></body></html>";
    static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
    static final String HTMLExcludeSuffix = "</span>";
    static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
    static final String HTMLIncludeSuffix = "</span>";
    static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
    static final String HTMLIncludedInclusiveNamespaceSuffix = "</span>";
    static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
    static final String HTMLExcludedInclusiveNamespaceSuffix = "</span>";
    private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    static final AttrCompare ATTR_COMPARE;
    
    private XMLSignatureInputDebugger() {
        this._doc = null;
        this._writer = null;
    }
    
    public XMLSignatureInputDebugger(final XMLSignatureInput xmlSignatureInput) {
        this._doc = null;
        this._writer = null;
        if (!xmlSignatureInput.isNodeSet()) {
            this._xpathNodeSet = null;
        }
        else {
            this._xpathNodeSet = xmlSignatureInput._inputNodeSet;
        }
    }
    
    public XMLSignatureInputDebugger(final XMLSignatureInput xmlSignatureInput, final Set inclusiveNamespaces) {
        this(xmlSignatureInput);
        this._inclusiveNamespaces = inclusiveNamespaces;
    }
    
    public String getHTMLRepresentation() throws XMLSignatureException {
        if (this._xpathNodeSet == null || this._xpathNodeSet.size() == 0) {
            return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n<blink>no node set, sorry</blink></pre></body></html>";
        }
        this._doc = XMLUtils.getOwnerDocument(this._xpathNodeSet.iterator().next());
        try {
            this._writer = new StringWriter();
            this.canonicalizeXPathNodeSet(this._doc);
            this._writer.close();
            return this._writer.toString();
        }
        catch (final IOException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        finally {
            this._xpathNodeSet = null;
            this._doc = null;
            this._writer = null;
        }
    }
    
    private void canonicalizeXPathNodeSet(final Node node) throws XMLSignatureException, IOException {
        switch (node.getNodeType()) {
            case 2:
            case 6:
            case 11:
            case 12: {
                throw new XMLSignatureException("empty");
            }
            case 9: {
                this._writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n");
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    this.canonicalizeXPathNodeSet(node2);
                }
                this._writer.write("</pre></body></html>");
                break;
            }
            case 8: {
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                final int positionRelativeToDocumentElement = this.getPositionRelativeToDocumentElement(node);
                if (positionRelativeToDocumentElement == 1) {
                    this._writer.write("\n");
                }
                this.outputCommentToWriter((Comment)node);
                if (positionRelativeToDocumentElement == -1) {
                    this._writer.write("\n");
                }
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                    break;
                }
                this._writer.write("</span>");
                break;
            }
            case 7: {
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                final int positionRelativeToDocumentElement2 = this.getPositionRelativeToDocumentElement(node);
                if (positionRelativeToDocumentElement2 == 1) {
                    this._writer.write("\n");
                }
                this.outputPItoWriter((ProcessingInstruction)node);
                if (positionRelativeToDocumentElement2 == -1) {
                    this._writer.write("\n");
                }
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                    break;
                }
                this._writer.write("</span>");
                break;
            }
            case 3:
            case 4: {
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                this.outputTextToWriter(node.getNodeValue());
                for (Node node3 = node.getNextSibling(); node3 != null && (node3.getNodeType() == 3 || node3.getNodeType() == 4); node3 = node3.getNextSibling()) {
                    this.outputTextToWriter(node3.getNodeValue());
                }
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                    break;
                }
                this._writer.write("</span>");
                break;
            }
            case 1: {
                final Element element = (Element)node;
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                this._writer.write("&lt;");
                this._writer.write(element.getTagName());
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                }
                else {
                    this._writer.write("</span>");
                }
                final NamedNodeMap attributes = element.getAttributes();
                final int length = attributes.getLength();
                final Object[] array = new Object[length];
                for (int i = 0; i < length; ++i) {
                    array[i] = attributes.item(i);
                }
                Arrays.sort(array, XMLSignatureInputDebugger.ATTR_COMPARE);
                final Object[] array2 = array;
                for (int j = 0; j < length; ++j) {
                    final Attr attr = (Attr)array2[j];
                    final boolean contains = this._xpathNodeSet.contains(attr);
                    final boolean contains2 = this._inclusiveNamespaces.contains(attr.getName());
                    if (contains) {
                        if (contains2) {
                            this._writer.write("<span class=\"INCLUDEDINCLUSIVENAMESPACE\">");
                        }
                        else {
                            this._writer.write("<span class=\"INCLUDED\">");
                        }
                    }
                    else if (contains2) {
                        this._writer.write("<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">");
                    }
                    else {
                        this._writer.write("<span class=\"EXCLUDED\">");
                    }
                    this.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue());
                    if (contains) {
                        if (contains2) {
                            this._writer.write("</span>");
                        }
                        else {
                            this._writer.write("</span>");
                        }
                    }
                    else if (contains2) {
                        this._writer.write("</span>");
                    }
                    else {
                        this._writer.write("</span>");
                    }
                }
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                this._writer.write("&gt;");
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                }
                else {
                    this._writer.write("</span>");
                }
                for (Node node4 = node.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                    this.canonicalizeXPathNodeSet(node4);
                }
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("<span class=\"INCLUDED\">");
                }
                else {
                    this._writer.write("<span class=\"EXCLUDED\">");
                }
                this._writer.write("&lt;/");
                this._writer.write(element.getTagName());
                this._writer.write("&gt;");
                if (this._xpathNodeSet.contains(node)) {
                    this._writer.write("</span>");
                    break;
                }
                this._writer.write("</span>");
                break;
            }
        }
    }
    
    private int getPositionRelativeToDocumentElement(final Node node) {
        if (node == null) {
            return 0;
        }
        final Document ownerDocument = node.getOwnerDocument();
        if (node.getParentNode() != ownerDocument) {
            return 0;
        }
        final Element documentElement = ownerDocument.getDocumentElement();
        if (documentElement == null) {
            return 0;
        }
        if (documentElement == node) {
            return 0;
        }
        for (Node nextSibling = node; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            if (nextSibling == documentElement) {
                return -1;
            }
        }
        return 1;
    }
    
    private void outputAttrToWriter(final String s, final String s2) throws IOException {
        this._writer.write(" ");
        this._writer.write(s);
        this._writer.write("=\"");
        for (int length = s2.length(), i = 0; i < length; ++i) {
            final char char1 = s2.charAt(i);
            switch (char1) {
                case 38: {
                    this._writer.write("&amp;amp;");
                    break;
                }
                case 60: {
                    this._writer.write("&amp;lt;");
                    break;
                }
                case 34: {
                    this._writer.write("&amp;quot;");
                    break;
                }
                case 9: {
                    this._writer.write("&amp;#x9;");
                    break;
                }
                case 10: {
                    this._writer.write("&amp;#xA;");
                    break;
                }
                case 13: {
                    this._writer.write("&amp;#xD;");
                    break;
                }
                default: {
                    this._writer.write(char1);
                    break;
                }
            }
        }
        this._writer.write("\"");
    }
    
    private void outputPItoWriter(final ProcessingInstruction processingInstruction) throws IOException {
        if (processingInstruction == null) {
            return;
        }
        this._writer.write("&lt;?");
        final String target = processingInstruction.getTarget();
        for (int length = target.length(), i = 0; i < length; ++i) {
            final char char1 = target.charAt(i);
            switch (char1) {
                case 13: {
                    this._writer.write("&amp;#xD;");
                    break;
                }
                case 32: {
                    this._writer.write("&middot;");
                    break;
                }
                case 10: {
                    this._writer.write("&para;\n");
                    break;
                }
                default: {
                    this._writer.write(char1);
                    break;
                }
            }
        }
        final String data = processingInstruction.getData();
        final int length2 = data.length();
        if (length2 > 0) {
            this._writer.write(" ");
            for (int j = 0; j < length2; ++j) {
                final char char2 = data.charAt(j);
                switch (char2) {
                    case 13: {
                        this._writer.write("&amp;#xD;");
                        break;
                    }
                    default: {
                        this._writer.write(char2);
                        break;
                    }
                }
            }
        }
        this._writer.write("?&gt;");
    }
    
    private void outputCommentToWriter(final Comment comment) throws IOException {
        if (comment == null) {
            return;
        }
        this._writer.write("&lt;!--");
        final String data = comment.getData();
        for (int length = data.length(), i = 0; i < length; ++i) {
            final char char1 = data.charAt(i);
            switch (char1) {
                case 13: {
                    this._writer.write("&amp;#xD;");
                    break;
                }
                case 32: {
                    this._writer.write("&middot;");
                    break;
                }
                case 10: {
                    this._writer.write("&para;\n");
                    break;
                }
                default: {
                    this._writer.write(char1);
                    break;
                }
            }
        }
        this._writer.write("--&gt;");
    }
    
    private void outputTextToWriter(final String s) throws IOException {
        if (s == null) {
            return;
        }
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 38: {
                    this._writer.write("&amp;amp;");
                    break;
                }
                case 60: {
                    this._writer.write("&amp;lt;");
                    break;
                }
                case 62: {
                    this._writer.write("&amp;gt;");
                    break;
                }
                case 13: {
                    this._writer.write("&amp;#xD;");
                    break;
                }
                case 32: {
                    this._writer.write("&middot;");
                    break;
                }
                case 10: {
                    this._writer.write("&para;\n");
                    break;
                }
                default: {
                    this._writer.write(char1);
                    break;
                }
            }
        }
    }
    
    static {
        ATTR_COMPARE = new AttrCompare();
    }
}
