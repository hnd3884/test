package org.apache.catalina.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.Writer;
import java.io.PrintWriter;

public class DOMWriter
{
    private static final String PRINTWRITER_ENCODING = "UTF8";
    @Deprecated
    protected final PrintWriter out;
    @Deprecated
    protected final boolean canonical;
    
    public DOMWriter(final Writer writer) {
        this(writer, true);
    }
    
    @Deprecated
    public DOMWriter(final Writer writer, final boolean canonical) {
        this.out = new PrintWriter(writer);
        this.canonical = canonical;
    }
    
    @Deprecated
    public static String getWriterEncoding() {
        return "UTF8";
    }
    
    public void print(final Node node) {
        if (node == null) {
            return;
        }
        final int type = node.getNodeType();
        switch (type) {
            case 9: {
                if (!this.canonical) {
                    this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                }
                this.print(((Document)node).getDocumentElement());
                this.out.flush();
                break;
            }
            case 1: {
                this.out.print('<');
                this.out.print(node.getLocalName());
                final Attr[] arr$;
                final Attr[] attrs = arr$ = this.sortAttributes(node.getAttributes());
                for (final Attr attr : arr$) {
                    this.out.print(' ');
                    this.out.print(attr.getLocalName());
                    this.out.print("=\"");
                    this.out.print(this.normalize(attr.getNodeValue()));
                    this.out.print('\"');
                }
                this.out.print('>');
                this.printChildren(node);
                break;
            }
            case 5: {
                if (this.canonical) {
                    this.printChildren(node);
                    break;
                }
                this.out.print('&');
                this.out.print(node.getLocalName());
                this.out.print(';');
                break;
            }
            case 4: {
                if (this.canonical) {
                    this.out.print(this.normalize(node.getNodeValue()));
                    break;
                }
                this.out.print("<![CDATA[");
                this.out.print(node.getNodeValue());
                this.out.print("]]>");
                break;
            }
            case 3: {
                this.out.print(this.normalize(node.getNodeValue()));
                break;
            }
            case 7: {
                this.out.print("<?");
                this.out.print(node.getLocalName());
                final String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.out.print(' ');
                    this.out.print(data);
                }
                this.out.print("?>");
                break;
            }
        }
        if (type == 1) {
            this.out.print("</");
            this.out.print(node.getLocalName());
            this.out.print('>');
        }
        this.out.flush();
    }
    
    private void printChildren(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            for (int len = children.getLength(), i = 0; i < len; ++i) {
                this.print(children.item(i));
            }
        }
    }
    
    @Deprecated
    protected Attr[] sortAttributes(final NamedNodeMap attrs) {
        if (attrs == null) {
            return new Attr[0];
        }
        final int len = attrs.getLength();
        final Attr[] array = new Attr[len];
        for (int i = 0; i < len; ++i) {
            array[i] = (Attr)attrs.item(i);
        }
        for (int i = 0; i < len - 1; ++i) {
            String name = null;
            name = array[i].getLocalName();
            int index = i;
            for (int j = i + 1; j < len; ++j) {
                String curName = null;
                curName = array[j].getLocalName();
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
    
    @Deprecated
    protected String normalize(final String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder str = new StringBuilder();
        for (int len = s.length(), i = 0; i < len; ++i) {
            final char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    continue;
                }
                case '>': {
                    str.append("&gt;");
                    continue;
                }
                case '&': {
                    str.append("&amp;");
                    continue;
                }
                case '\"': {
                    str.append("&quot;");
                    continue;
                }
                case '\n':
                case '\r': {
                    if (this.canonical) {
                        str.append("&#");
                        str.append(Integer.toString(ch));
                        str.append(';');
                        continue;
                    }
                    break;
                }
            }
            str.append(ch);
        }
        return str.toString();
    }
}
