package org.htmlparser.tags;

import org.htmlparser.util.ParserUtils;
import org.htmlparser.Node;
import org.htmlparser.util.SimpleNodeIterator;

public class LinkTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    protected String mLink;
    private boolean mailLink;
    private boolean javascriptLink;
    
    public String[] getIds() {
        return LinkTag.mIds;
    }
    
    public String[] getEnders() {
        return LinkTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return LinkTag.mEndTagEnders;
    }
    
    public String getAccessKey() {
        return this.getAttribute("ACCESSKEY");
    }
    
    public String getLink() {
        if (null == this.mLink) {
            this.mailLink = false;
            this.javascriptLink = false;
            this.mLink = this.extractLink();
            int mailto = this.mLink.indexOf("mailto");
            if (mailto == 0) {
                mailto = this.mLink.indexOf(":");
                this.mLink = this.mLink.substring(mailto + 1);
                this.mailLink = true;
            }
            final int javascript = this.mLink.indexOf("javascript:");
            if (javascript == 0) {
                this.mLink = this.mLink.substring(11);
                this.javascriptLink = true;
            }
        }
        return this.mLink;
    }
    
    public String getLinkText() {
        String ret;
        if (null != this.getChildren()) {
            ret = this.getChildren().asString();
        }
        else {
            ret = "";
        }
        return ret;
    }
    
    public boolean isMailLink() {
        this.getLink();
        return this.mailLink;
    }
    
    public boolean isJavascriptLink() {
        this.getLink();
        return this.javascriptLink;
    }
    
    public boolean isFTPLink() {
        return this.getLink().indexOf("ftp://") == 0;
    }
    
    public boolean isIRCLink() {
        return this.getLink().indexOf("irc://") == 0;
    }
    
    public boolean isHTTPLink() {
        return !this.isFTPLink() && !this.isHTTPSLink() && !this.isJavascriptLink() && !this.isMailLink() && !this.isIRCLink();
    }
    
    public boolean isHTTPSLink() {
        return this.getLink().indexOf("https://") == 0;
    }
    
    public boolean isHTTPLikeLink() {
        return this.isHTTPLink() || this.isHTTPSLink();
    }
    
    public void setMailLink(final boolean newMailLink) {
        this.mailLink = newMailLink;
    }
    
    public void setJavascriptLink(final boolean newJavascriptLink) {
        this.javascriptLink = newJavascriptLink;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Link to : " + this.getLink() + "; titled : " + this.getLinkText() + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition() + ", AccessKey=");
        if (this.getAccessKey() == null) {
            sb.append("null\n");
        }
        else {
            sb.append(this.getAccessKey() + "\n");
        }
        if (null != this.getChildren()) {
            int i = 0;
            final SimpleNodeIterator e = this.children();
            while (e.hasMoreNodes()) {
                final Node node = e.nextNode();
                sb.append("   " + i++ + " ");
                sb.append(node.toString() + "\n");
            }
        }
        return sb.toString();
    }
    
    public void setLink(final String link) {
        this.setAttribute("HREF", this.mLink = link);
    }
    
    public String extractLink() {
        String ret = this.getAttribute("HREF");
        if (null != ret) {
            ret = ParserUtils.removeChars(ret, '\n');
            ret = ParserUtils.removeChars(ret, '\r');
        }
        if (null != this.getPage()) {
            ret = this.getPage().getAbsoluteURL(ret);
        }
        return ret;
    }
    
    static {
        mIds = new String[] { "A" };
        mEnders = new String[] { "A", "P", "DIV", "TD", "TR", "FORM", "LI" };
        mEndTagEnders = new String[] { "P", "DIV", "TD", "TR", "FORM", "LI", "BODY", "HTML" };
    }
}
