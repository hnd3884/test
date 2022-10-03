package org.apache.taglibs.standard.tei;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class XmlParseTEI extends TagExtraInfo
{
    private static final String VAR = "var";
    private static final String VAR_DOM = "varDom";
    private static final String SCOPE = "scope";
    private static final String SCOPE_DOM = "scopeDom";
    
    public boolean isValid(final TagData us) {
        return (!Util.isSpecified(us, "var") || !Util.isSpecified(us, "varDom")) && (Util.isSpecified(us, "var") || Util.isSpecified(us, "varDom")) && (!Util.isSpecified(us, "scope") || Util.isSpecified(us, "var")) && (!Util.isSpecified(us, "scopeDom") || Util.isSpecified(us, "varDom"));
    }
}
