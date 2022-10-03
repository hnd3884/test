package org.htmlparser.tags;

import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.Attribute;
import java.util.Vector;
import org.htmlparser.Text;
import java.util.Enumeration;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.Tag;
import java.util.Hashtable;

public class AppletTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return AppletTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return AppletTag.mEndTagEnders;
    }
    
    public Hashtable createAppletParamsTable() {
        final Hashtable ret = new Hashtable();
        final NodeList kids = this.getChildren();
        if (null != kids) {
            for (int i = 0; i < kids.size(); ++i) {
                final Node node = this.children.elementAt(i);
                if (node instanceof Tag) {
                    final Tag tag = (Tag)node;
                    if (tag.getTagName().equals("PARAM")) {
                        final String paramName = tag.getAttribute("NAME");
                        if (null != paramName && 0 != paramName.length()) {
                            final String paramValue = tag.getAttribute("VALUE");
                            ret.put(paramName, paramValue);
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    public String getAppletClass() {
        return this.getAttribute("CODE");
    }
    
    public Hashtable getAppletParams() {
        return this.createAppletParamsTable();
    }
    
    public String getArchive() {
        return this.getAttribute("ARCHIVE");
    }
    
    public String getCodeBase() {
        return this.getAttribute("CODEBASE");
    }
    
    public String getParameter(final String key) {
        return this.getAppletParams().get(key);
    }
    
    public Enumeration getParameterNames() {
        return this.getAppletParams().keys();
    }
    
    public void setAppletClass(final String newAppletClass) {
        this.setAttribute("CODE", newAppletClass);
    }
    
    public void setAppletParams(final Hashtable newAppletParams) {
        NodeList kids = this.getChildren();
        if (null == kids) {
            kids = new NodeList();
        }
        else {
            int i = 0;
            while (i < kids.size()) {
                Node node = kids.elementAt(i);
                if (node instanceof Tag) {
                    if (((Tag)node).getTagName().equals("PARAM")) {
                        kids.remove(i);
                        if (i >= kids.size()) {
                            continue;
                        }
                        node = kids.elementAt(i);
                        if (!(node instanceof Text)) {
                            continue;
                        }
                        final Text string = (Text)node;
                        if (0 != string.getText().trim().length()) {
                            continue;
                        }
                        kids.remove(i);
                    }
                    else {
                        ++i;
                    }
                }
                else {
                    ++i;
                }
            }
        }
        final Enumeration e = newAppletParams.keys();
        while (e.hasMoreElements()) {
            final Vector attributes = new Vector();
            final String paramName = e.nextElement();
            final String paramValue = newAppletParams.get(paramName);
            attributes.addElement(new Attribute("PARAM", null));
            attributes.addElement(new Attribute(" "));
            attributes.addElement(new Attribute("VALUE", paramValue, '\"'));
            attributes.addElement(new Attribute(" "));
            attributes.addElement(new Attribute("NAME", paramName, '\"'));
            final Tag tag = new TagNode(null, 0, 0, attributes);
            kids.add(tag);
        }
        this.setChildren(kids);
    }
    
    public void setArchive(final String newArchive) {
        this.setAttribute("ARCHIVE", newArchive);
    }
    
    public void setCodeBase(final String newCodeBase) {
        this.setAttribute("CODEBASE", newCodeBase);
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(500);
        ret.append("Applet Tag\n");
        ret.append("**********\n");
        ret.append("Class Name = ");
        ret.append(this.getAppletClass());
        ret.append("\n");
        ret.append("Archive = ");
        ret.append(this.getArchive());
        ret.append("\n");
        ret.append("Codebase = ");
        ret.append(this.getCodeBase());
        ret.append("\n");
        final Hashtable parameters = this.getAppletParams();
        final Enumeration params = parameters.keys();
        if (null == params) {
            ret.append("No Params found.\n");
        }
        else {
            int cnt = 0;
            while (params.hasMoreElements()) {
                final String paramName = params.nextElement();
                final String paramValue = parameters.get(paramName);
                ret.append(cnt);
                ret.append(": Parameter name = ");
                ret.append(paramName);
                ret.append(", Parameter value = ");
                ret.append(paramValue);
                ret.append("\n");
                ++cnt;
            }
        }
        boolean found = false;
        final SimpleNodeIterator e = this.children();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (node instanceof Tag && ((Tag)node).getTagName().equals("PARAM")) {
                continue;
            }
            if (!found) {
                ret.append("Miscellaneous items :\n");
            }
            else {
                ret.append(" ");
            }
            found = true;
            ret.append(node.toString());
        }
        if (found) {
            ret.append("\n");
        }
        ret.append("End of Applet Tag\n");
        ret.append("*****************\n");
        return ret.toString();
    }
    
    static {
        mIds = new String[] { "APPLET" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
