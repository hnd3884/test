package org.apache.taglibs.standard.tei;

import javax.servlet.jsp.tagext.TagData;

public class Util
{
    public static boolean isSpecified(final TagData data, final String attributeName) {
        return data.getAttribute(attributeName) != null;
    }
}
