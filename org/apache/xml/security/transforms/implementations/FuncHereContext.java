package org.apache.xml.security.transforms.implementations;

import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xml.security.utils.I18n;
import org.w3c.dom.Node;
import org.apache.xpath.XPathContext;

public class FuncHereContext extends XPathContext
{
    private FuncHereContext() {
    }
    
    public FuncHereContext(final Node node) {
        super((Object)node);
    }
    
    public FuncHereContext(final Node node, final XPathContext xPathContext) {
        super((Object)node);
        try {
            super.m_dtmManager = xPathContext.getDTMManager();
        }
        catch (final IllegalAccessError illegalAccessError) {
            throw new IllegalAccessError(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + illegalAccessError.getMessage() + "\"");
        }
    }
    
    public FuncHereContext(final Node node, final CachedXPathAPI cachedXPathAPI) {
        super((Object)node);
        try {
            super.m_dtmManager = cachedXPathAPI.getXPathContext().getDTMManager();
        }
        catch (final IllegalAccessError illegalAccessError) {
            throw new IllegalAccessError(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + illegalAccessError.getMessage() + "\"");
        }
    }
    
    public FuncHereContext(final Node node, final DTMManager dtmManager) {
        super((Object)node);
        try {
            super.m_dtmManager = dtmManager;
        }
        catch (final IllegalAccessError illegalAccessError) {
            throw new IllegalAccessError(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + illegalAccessError.getMessage() + "\"");
        }
    }
}
