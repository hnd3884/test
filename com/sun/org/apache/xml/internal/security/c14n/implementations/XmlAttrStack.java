package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.net.URI;
import java.util.Iterator;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Collection;
import org.w3c.dom.Attr;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class XmlAttrStack
{
    private static final Logger LOG;
    private int currentLevel;
    private int lastlevel;
    private XmlsStackElement cur;
    private List<XmlsStackElement> levels;
    private boolean c14n11;
    
    public XmlAttrStack(final boolean c14n11) {
        this.currentLevel = 0;
        this.lastlevel = 0;
        this.levels = new ArrayList<XmlsStackElement>();
        this.c14n11 = c14n11;
    }
    
    void push(final int currentLevel) {
        this.currentLevel = currentLevel;
        if (this.currentLevel == -1) {
            return;
        }
        this.cur = null;
        while (this.lastlevel >= this.currentLevel) {
            this.levels.remove(this.levels.size() - 1);
            final int size = this.levels.size();
            if (size == 0) {
                this.lastlevel = 0;
                return;
            }
            this.lastlevel = this.levels.get(size - 1).level;
        }
    }
    
    void addXmlnsAttr(final Attr attr) {
        if (this.cur == null) {
            this.cur = new XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.levels.add(this.cur);
            this.lastlevel = this.currentLevel;
        }
        this.cur.nodes.add(attr);
    }
    
    void getXmlnsAttr(final Collection<Attr> collection) {
        int i = this.levels.size() - 1;
        if (this.cur == null) {
            this.cur = new XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.lastlevel = this.currentLevel;
            this.levels.add(this.cur);
        }
        boolean b = false;
        if (i == -1) {
            b = true;
        }
        else {
            final XmlsStackElement xmlsStackElement = this.levels.get(i);
            if (xmlsStackElement.rendered && xmlsStackElement.level + 1 == this.currentLevel) {
                b = true;
            }
        }
        if (b) {
            collection.addAll(this.cur.nodes);
            this.cur.rendered = true;
            return;
        }
        final HashMap hashMap = new HashMap();
        if (this.c14n11) {
            final ArrayList list = new ArrayList();
            boolean b2 = true;
            while (i >= 0) {
                final XmlsStackElement xmlsStackElement2 = this.levels.get(i);
                if (xmlsStackElement2.rendered) {
                    b2 = false;
                }
                final Iterator<Attr> iterator = xmlsStackElement2.nodes.iterator();
                while (iterator.hasNext() && b2) {
                    final Attr attr = iterator.next();
                    if (attr.getLocalName().equals("base") && !xmlsStackElement2.rendered) {
                        list.add(attr);
                    }
                    else {
                        if (hashMap.containsKey(attr.getName())) {
                            continue;
                        }
                        hashMap.put(attr.getName(), attr);
                    }
                }
                --i;
            }
            if (!list.isEmpty()) {
                final Iterator<Attr> iterator2 = collection.iterator();
                String value = null;
                Attr attr2 = null;
                while (iterator2.hasNext()) {
                    final Attr attr3 = iterator2.next();
                    if (attr3.getLocalName().equals("base")) {
                        value = attr3.getValue();
                        attr2 = attr3;
                        break;
                    }
                }
                for (final Attr attr4 : list) {
                    if (value == null) {
                        value = attr4.getValue();
                        attr2 = attr4;
                    }
                    else {
                        try {
                            value = joinURI(attr4.getValue(), value);
                        }
                        catch (final URISyntaxException ex) {
                            XmlAttrStack.LOG.log(Level.FINE, ex.getMessage(), ex);
                        }
                    }
                }
                if (value != null && value.length() != 0) {
                    attr2.setValue(value);
                    collection.add(attr2);
                }
            }
        }
        else {
            while (i >= 0) {
                for (final Attr attr5 : this.levels.get(i).nodes) {
                    if (!hashMap.containsKey(attr5.getName())) {
                        hashMap.put(attr5.getName(), attr5);
                    }
                }
                --i;
            }
        }
        this.cur.rendered = true;
        collection.addAll(hashMap.values());
    }
    
    private static String joinURI(String string, final String s) throws URISyntaxException {
        String scheme = null;
        String authority = null;
        String path = "";
        String query = null;
        if (string != null) {
            if (string.endsWith("..")) {
                string += "/";
            }
            final URI uri = new URI(string);
            scheme = uri.getScheme();
            authority = uri.getAuthority();
            path = uri.getPath();
            query = uri.getQuery();
        }
        final URI uri2 = new URI(s);
        String scheme2 = uri2.getScheme();
        final String authority2 = uri2.getAuthority();
        final String path2 = uri2.getPath();
        final String query2 = uri2.getQuery();
        if (scheme2 != null && scheme2.equals(scheme)) {
            scheme2 = null;
        }
        String s2;
        String s3;
        String s4;
        String s5;
        if (scheme2 != null) {
            s2 = scheme2;
            s3 = authority2;
            s4 = removeDotSegments(path2);
            s5 = query2;
        }
        else {
            if (authority2 != null) {
                s3 = authority2;
                s4 = removeDotSegments(path2);
                s5 = query2;
            }
            else {
                if (path2.length() == 0) {
                    s4 = path;
                    if (query2 != null) {
                        s5 = query2;
                    }
                    else {
                        s5 = query;
                    }
                }
                else {
                    if (path2.startsWith("/")) {
                        s4 = removeDotSegments(path2);
                    }
                    else {
                        String s6;
                        if (authority != null && path.length() == 0) {
                            s6 = "/" + path2;
                        }
                        else {
                            final int lastIndex = path.lastIndexOf(47);
                            if (lastIndex == -1) {
                                s6 = path2;
                            }
                            else {
                                s6 = path.substring(0, lastIndex + 1) + path2;
                            }
                        }
                        s4 = removeDotSegments(s6);
                    }
                    s5 = query2;
                }
                s3 = authority;
            }
            s2 = scheme;
        }
        return new URI(s2, s3, s4, s5, null).toString();
    }
    
    private static String removeDotSegments(final String s) {
        XmlAttrStack.LOG.log(Level.FINE, "STEP OUTPUT BUFFER\t\tINPUT BUFFER");
        String s2;
        for (s2 = s; s2.indexOf("//") > -1; s2 = s2.replaceAll("//", "/")) {}
        StringBuilder sb = new StringBuilder();
        if (s2.charAt(0) == '/') {
            sb.append("/");
            s2 = s2.substring(1);
        }
        printStep("1 ", sb.toString(), s2);
        while (s2.length() != 0) {
            if (s2.startsWith("./")) {
                s2 = s2.substring(2);
                printStep("2A", sb.toString(), s2);
            }
            else if (s2.startsWith("../")) {
                s2 = s2.substring(3);
                if (!sb.toString().equals("/")) {
                    sb.append("../");
                }
                printStep("2A", sb.toString(), s2);
            }
            else if (s2.startsWith("/./")) {
                s2 = s2.substring(2);
                printStep("2B", sb.toString(), s2);
            }
            else if (s2.equals("/.")) {
                s2 = s2.replaceFirst("/.", "/");
                printStep("2B", sb.toString(), s2);
            }
            else if (s2.startsWith("/../")) {
                s2 = s2.substring(3);
                if (sb.length() == 0) {
                    sb.append("/");
                }
                else if (sb.toString().endsWith("../")) {
                    sb.append("..");
                }
                else if (sb.toString().endsWith("..")) {
                    sb.append("/..");
                }
                else {
                    final int lastIndex = sb.lastIndexOf("/");
                    if (lastIndex == -1) {
                        sb = new StringBuilder();
                        if (s2.charAt(0) == '/') {
                            s2 = s2.substring(1);
                        }
                    }
                    else {
                        sb = sb.delete(lastIndex, sb.length());
                    }
                }
                printStep("2C", sb.toString(), s2);
            }
            else if (s2.equals("/..")) {
                s2 = s2.replaceFirst("/..", "/");
                if (sb.length() == 0) {
                    sb.append("/");
                }
                else if (sb.toString().endsWith("../")) {
                    sb.append("..");
                }
                else if (sb.toString().endsWith("..")) {
                    sb.append("/..");
                }
                else {
                    final int lastIndex2 = sb.lastIndexOf("/");
                    if (lastIndex2 == -1) {
                        sb = new StringBuilder();
                        if (s2.charAt(0) == '/') {
                            s2 = s2.substring(1);
                        }
                    }
                    else {
                        sb = sb.delete(lastIndex2, sb.length());
                    }
                }
                printStep("2C", sb.toString(), s2);
            }
            else if (s2.equals(".")) {
                s2 = "";
                printStep("2D", sb.toString(), s2);
            }
            else if (s2.equals("..")) {
                if (!sb.toString().equals("/")) {
                    sb.append("..");
                }
                s2 = "";
                printStep("2D", sb.toString(), s2);
            }
            else {
                int index = s2.indexOf(47);
                int index2;
                if (index == 0) {
                    index2 = s2.indexOf(47, 1);
                }
                else {
                    index2 = index;
                    index = 0;
                }
                String s3;
                if (index2 == -1) {
                    s3 = s2.substring(index);
                    s2 = "";
                }
                else {
                    s3 = s2.substring(index, index2);
                    s2 = s2.substring(index2);
                }
                sb.append(s3);
                printStep("2E", sb.toString(), s2);
            }
        }
        if (sb.toString().endsWith("..")) {
            sb.append("/");
            printStep("3 ", sb.toString(), s2);
        }
        return sb.toString();
    }
    
    private static void printStep(final String s, final String s2, final String s3) {
        if (XmlAttrStack.LOG.isLoggable(Level.FINE)) {
            XmlAttrStack.LOG.log(Level.FINE, " " + s + ":   " + s2);
            if (s2.length() == 0) {
                XmlAttrStack.LOG.log(Level.FINE, "\t\t\t\t" + s3);
            }
            else {
                XmlAttrStack.LOG.log(Level.FINE, "\t\t\t" + s3);
            }
        }
    }
    
    static {
        LOG = Logger.getLogger(XmlAttrStack.class.getName());
    }
    
    static class XmlsStackElement
    {
        int level;
        boolean rendered;
        List<Attr> nodes;
        
        XmlsStackElement() {
            this.rendered = false;
            this.nodes = new ArrayList<Attr>();
        }
    }
}
