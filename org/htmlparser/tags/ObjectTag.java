package org.htmlparser.tags;

import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.Attribute;
import java.util.Vector;
import org.htmlparser.nodes.TextNode;
import java.util.Enumeration;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.Tag;
import java.util.Hashtable;

public class ObjectTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return ObjectTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return ObjectTag.mEndTagEnders;
    }
    
    public Hashtable createObjectParamsTable() {
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
                            ret.put(paramName.toUpperCase(), paramValue);
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    public String getObjectClassId() {
        return this.getAttribute("CLASSID");
    }
    
    public String getObjectCodeBase() {
        return this.getAttribute("CODEBASE");
    }
    
    public String getObjectCodeType() {
        return this.getAttribute("CODETYPE");
    }
    
    public String getObjectData() {
        return this.getAttribute("DATA");
    }
    
    public String getObjectHeight() {
        return this.getAttribute("HEIGHT");
    }
    
    public String getObjectStandby() {
        return this.getAttribute("STANDBY");
    }
    
    public String getObjectType() {
        return this.getAttribute("TYPE");
    }
    
    public String getObjectWidth() {
        return this.getAttribute("WIDTH");
    }
    
    public Hashtable getObjectParams() {
        return this.createObjectParamsTable();
    }
    
    public String getParameter(final String key) {
        return this.getObjectParams().get(key.toUpperCase());
    }
    
    public Enumeration getParameterNames() {
        return this.getObjectParams().keys();
    }
    
    public void setObjectClassId(final String newClassId) {
        this.setAttribute("CLASSID", newClassId);
    }
    
    public void setObjectCodeBase(final String newCodeBase) {
        this.setAttribute("CODEBASE", newCodeBase);
    }
    
    public void setObjectCodeType(final String newCodeType) {
        this.setAttribute("CODETYPE", newCodeType);
    }
    
    public void setObjectData(final String newData) {
        this.setAttribute("DATA", newData);
    }
    
    public void setObjectHeight(final String newHeight) {
        this.setAttribute("HEIGHT", newHeight);
    }
    
    public void setObjectStandby(final String newStandby) {
        this.setAttribute("STANDBY", newStandby);
    }
    
    public void setObjectType(final String newType) {
        this.setAttribute("TYPE", newType);
    }
    
    public void setObjectWidth(final String newWidth) {
        this.setAttribute("WIDTH", newWidth);
    }
    
    public void setObjectParams(final Hashtable newObjectParams) {
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
                        if (!(node instanceof TextNode)) {
                            continue;
                        }
                        final TextNode string = (TextNode)node;
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
        final Enumeration e = newObjectParams.keys();
        while (e.hasMoreElements()) {
            final Vector attributes = new Vector();
            final String paramName = e.nextElement();
            final String paramValue = newObjectParams.get(paramName);
            attributes.addElement(new Attribute("PARAM", null));
            attributes.addElement(new Attribute(" "));
            attributes.addElement(new Attribute("VALUE", paramValue, '\"'));
            attributes.addElement(new Attribute(" "));
            attributes.addElement(new Attribute("NAME", paramName.toUpperCase(), '\"'));
            final Tag tag = new TagNode(null, 0, 0, attributes);
            kids.add(tag);
        }
        this.setChildren(kids);
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(500);
        ret.append("Object Tag\n");
        ret.append("**********\n");
        ret.append("ClassId = ");
        ret.append(this.getObjectClassId());
        ret.append("\n");
        ret.append("CodeBase = ");
        ret.append(this.getObjectCodeBase());
        ret.append("\n");
        ret.append("CodeType = ");
        ret.append(this.getObjectCodeType());
        ret.append("\n");
        ret.append("Data = ");
        ret.append(this.getObjectData());
        ret.append("\n");
        ret.append("Height = ");
        ret.append(this.getObjectHeight());
        ret.append("\n");
        ret.append("Standby = ");
        ret.append(this.getObjectStandby());
        ret.append("\n");
        ret.append("Type = ");
        ret.append(this.getObjectType());
        ret.append("\n");
        ret.append("Width = ");
        ret.append(this.getObjectWidth());
        ret.append("\n");
        final Hashtable parameters = this.getObjectParams();
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
        ret.append("End of Object Tag\n");
        ret.append("*****************\n");
        return ret.toString();
    }
    
    static {
        mIds = new String[] { "OBJECT" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
