package org.htmlparser.tags;

import org.htmlparser.scanners.Scanner;
import org.htmlparser.scanners.StyleScanner;

public class StyleTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public StyleTag() {
        this.setThisScanner(new StyleScanner());
    }
    
    public String[] getIds() {
        return StyleTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return StyleTag.mEndTagEnders;
    }
    
    public String getStyleCode() {
        return this.getChildrenHTML();
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer();
        String guts = this.toHtml();
        guts = guts.substring(1, guts.length() - 1);
        ret.append("Style node :\n");
        ret.append(guts);
        ret.append("\n");
        return ret.toString();
    }
    
    static {
        mIds = new String[] { "STYLE" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
