package com.lowagie.text.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;

public class XmlDomWriter
{
    protected PrintWriter fOut;
    protected boolean fCanonical;
    protected boolean fXML11;
    
    public XmlDomWriter() {
    }
    
    public XmlDomWriter(final boolean canonical) {
        this.fCanonical = canonical;
    }
    
    public void setCanonical(final boolean canonical) {
        this.fCanonical = canonical;
    }
    
    public void setOutput(final OutputStream stream, String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = "UTF8";
        }
        final Writer writer = new OutputStreamWriter(stream, encoding);
        this.fOut = new PrintWriter(writer);
    }
    
    public void setOutput(final Writer writer) {
        this.fOut = (PrintWriter)((writer instanceof PrintWriter) ? writer : new PrintWriter(writer));
    }
    
    public void write(final Node node) {
        if (node == null) {
            return;
        }
        final short type = node.getNodeType();
        switch (type) {
            case 9: {
                final Document document = (Document)node;
                this.fXML11 = false;
                if (!this.fCanonical) {
                    if (this.fXML11) {
                        this.fOut.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
                    }
                    else {
                        this.fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }
                    this.fOut.flush();
                    this.write(document.getDoctype());
                }
                this.write(document.getDocumentElement());
                break;
            }
            case 10: {
                final DocumentType doctype = (DocumentType)node;
                this.fOut.print("<!DOCTYPE ");
                this.fOut.print(doctype.getName());
                final String publicId = doctype.getPublicId();
                final String systemId = doctype.getSystemId();
                if (publicId != null) {
                    this.fOut.print(" PUBLIC '");
                    this.fOut.print(publicId);
                    this.fOut.print("' '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                }
                else if (systemId != null) {
                    this.fOut.print(" SYSTEM '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                }
                final String internalSubset = doctype.getInternalSubset();
                if (internalSubset != null) {
                    this.fOut.println(" [");
                    this.fOut.print(internalSubset);
                    this.fOut.print(']');
                }
                this.fOut.println('>');
                break;
            }
            case 1: {
                this.fOut.print('<');
                this.fOut.print(node.getNodeName());
                final Attr[] attrs = this.sortAttributes(node.getAttributes());
                for (int i = 0; i < attrs.length; ++i) {
                    final Attr attr = attrs[i];
                    this.fOut.print(' ');
                    this.fOut.print(attr.getNodeName());
                    this.fOut.print("=\"");
                    this.normalizeAndPrint(attr.getNodeValue(), true);
                    this.fOut.print('\"');
                }
                this.fOut.print('>');
                this.fOut.flush();
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    this.write(child);
                }
                break;
            }
            case 5: {
                if (this.fCanonical) {
                    for (Node child2 = node.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
                        this.write(child2);
                    }
                    break;
                }
                this.fOut.print('&');
                this.fOut.print(node.getNodeName());
                this.fOut.print(';');
                this.fOut.flush();
                break;
            }
            case 4: {
                if (this.fCanonical) {
                    this.normalizeAndPrint(node.getNodeValue(), false);
                }
                else {
                    this.fOut.print("<![CDATA[");
                    this.fOut.print(node.getNodeValue());
                    this.fOut.print("]]>");
                }
                this.fOut.flush();
                break;
            }
            case 3: {
                this.normalizeAndPrint(node.getNodeValue(), false);
                this.fOut.flush();
                break;
            }
            case 7: {
                this.fOut.print("<?");
                this.fOut.print(node.getNodeName());
                final String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.fOut.print(' ');
                    this.fOut.print(data);
                }
                this.fOut.print("?>");
                this.fOut.flush();
                break;
            }
            case 8: {
                if (!this.fCanonical) {
                    this.fOut.print("<!--");
                    final String comment = node.getNodeValue();
                    if (comment != null && comment.length() > 0) {
                        this.fOut.print(comment);
                    }
                    this.fOut.print("-->");
                    this.fOut.flush();
                    break;
                }
                break;
            }
        }
        if (type == 1) {
            this.fOut.print("</");
            this.fOut.print(node.getNodeName());
            this.fOut.print('>');
            this.fOut.flush();
        }
    }
    
    protected Attr[] sortAttributes(final NamedNodeMap attrs) {
        final int len = (attrs != null) ? attrs.getLength() : 0;
        final Attr[] array = new Attr[len];
        for (int i = 0; i < len; ++i) {
            array[i] = (Attr)attrs.item(i);
        }
        for (int i = 0; i < len - 1; ++i) {
            String name = array[i].getNodeName();
            int index = i;
            for (int j = i + 1; j < len; ++j) {
                final String curName = array[j].getNodeName();
                if (curName.compareTo(name) < 0) {
                    name = curName;
                    index = j;
                }
            }
            if (index != i) {
                final Attr temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        }
        return array;
    }
    
    protected void normalizeAndPrint(final String s, final boolean isAttValue) {
        for (int len = (s != null) ? s.length() : 0, i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            this.normalizeAndPrint(c, isAttValue);
        }
    }
    
    protected void normalizeAndPrint(final char c, final boolean isAttValue) {
        switch (c) {
            case '<': {
                this.fOut.print("&lt;");
                return;
            }
            case '>': {
                this.fOut.print("&gt;");
                return;
            }
            case '&': {
                this.fOut.print("&amp;");
                return;
            }
            case '\"': {
                if (isAttValue) {
                    this.fOut.print("&quot;");
                    return;
                }
                this.fOut.print("\"");
                return;
            }
            case '\r': {
                this.fOut.print("&#xD;");
                return;
            }
            case '\n': {
                if (this.fCanonical) {
                    this.fOut.print("&#xA;");
                    return;
                }
                break;
            }
        }
        if ((this.fXML11 && ((c >= '\u0001' && c <= '\u001f' && c != '\t' && c != '\n') || (c >= '\u007f' && c <= '\u009f') || c == '\u2028')) || (isAttValue && (c == '\t' || c == '\n'))) {
            this.fOut.print("&#x");
            this.fOut.print(Integer.toHexString(c).toUpperCase());
            this.fOut.print(";");
        }
        else {
            this.fOut.print(c);
        }
    }
}
