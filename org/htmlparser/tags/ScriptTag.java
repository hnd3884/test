package org.htmlparser.tags;

import org.htmlparser.Node;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.scanners.Scanner;
import org.htmlparser.scanners.ScriptScanner;

public class ScriptTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    protected String mCode;
    
    public ScriptTag() {
        this.setThisScanner(new ScriptScanner());
    }
    
    public String[] getIds() {
        return ScriptTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return ScriptTag.mEndTagEnders;
    }
    
    public String getLanguage() {
        return this.getAttribute("LANGUAGE");
    }
    
    public String getScriptCode() {
        String ret;
        if (null != this.mCode) {
            ret = this.mCode;
        }
        else {
            ret = this.getChildrenHTML();
        }
        return ret;
    }
    
    public void setScriptCode(final String code) {
        this.mCode = code;
    }
    
    public String getType() {
        return this.getAttribute("TYPE");
    }
    
    public void setLanguage(final String language) {
        this.setAttribute("LANGUAGE", language);
    }
    
    public void setType(final String type) {
        this.setAttribute("TYPE", type);
    }
    
    protected void putChildrenInto(final StringBuffer sb, final boolean verbatim) {
        if (null != this.getScriptCode()) {
            sb.append(this.getScriptCode());
        }
        else {
            final SimpleNodeIterator e = this.children();
            while (e.hasMoreNodes()) {
                final Node node = e.nextNode();
                if (!verbatim || node.getStartPosition() != node.getEndPosition()) {
                    sb.append(node.toHtml(verbatim));
                }
            }
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Script Node : \n");
        if (this.getLanguage() != null || this.getType() != null) {
            sb.append("Properties -->\n");
            if (this.getLanguage() != null && this.getLanguage().length() != 0) {
                sb.append("[Language : " + this.getLanguage() + "]\n");
            }
            if (this.getType() != null && this.getType().length() != 0) {
                sb.append("[Type : " + this.getType() + "]\n");
            }
        }
        sb.append("\n");
        sb.append("Code\n");
        sb.append("****\n");
        sb.append(this.getScriptCode() + "\n");
        return sb.toString();
    }
    
    static {
        mIds = new String[] { "SCRIPT" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
