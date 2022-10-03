package org.apache.poi.ooxml.util;

import org.apache.poi.util.POILogFactory;
import javax.xml.xpath.XPathFactory;
import org.apache.poi.util.POILogger;

public final class XPathHelper
{
    private static POILogger logger;
    static final XPathFactory xpathFactory;
    
    private XPathHelper() {
    }
    
    public static XPathFactory getFactory() {
        return XPathHelper.xpathFactory;
    }
    
    private static void trySetFeature(final XPathFactory xpf, final String feature, final boolean enabled) {
        try {
            xpf.setFeature(feature, enabled);
        }
        catch (final Exception e) {
            XPathHelper.logger.log(5, new Object[] { "XPathFactory Feature unsupported", feature, e });
        }
        catch (final AbstractMethodError ame) {
            XPathHelper.logger.log(5, new Object[] { "Cannot set XPathFactory feature because outdated XML parser in classpath", feature, ame });
        }
    }
    
    static {
        XPathHelper.logger = POILogFactory.getLogger((Class)XPathHelper.class);
        trySetFeature(xpathFactory = XPathFactory.newInstance(), "http://javax.xml.XMLConstants/feature/secure-processing", true);
    }
}
