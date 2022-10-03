package org.apache.xerces.impl.xpath.regex;

public final class RangeTokenMapFactory
{
    private static RangeTokenMap xmlMap;
    private static RangeTokenMap xml11Map;
    
    static synchronized RangeTokenMap getXMLTokenMap(final short n) {
        if (n == 1) {
            if (RangeTokenMapFactory.xmlMap == null) {
                RangeTokenMapFactory.xmlMap = XMLTokenMap.instance();
            }
            return RangeTokenMapFactory.xmlMap;
        }
        if (RangeTokenMapFactory.xml11Map == null) {
            RangeTokenMapFactory.xml11Map = XML11TokenMap.instance();
        }
        return RangeTokenMapFactory.xml11Map;
    }
    
    static {
        RangeTokenMapFactory.xmlMap = null;
        RangeTokenMapFactory.xml11Map = null;
    }
}
