package com.sun.org.apache.xml.internal.utils;

public class NSInfo
{
    public String m_namespace;
    public boolean m_hasXMLNSAttrs;
    public boolean m_hasProcessedNS;
    public int m_ancestorHasXMLNSAttrs;
    public static final int ANCESTORXMLNSUNPROCESSED = 0;
    public static final int ANCESTORHASXMLNS = 1;
    public static final int ANCESTORNOXMLNS = 2;
    
    public NSInfo(final boolean hasProcessedNS, final boolean hasXMLNSAttrs) {
        this.m_hasProcessedNS = hasProcessedNS;
        this.m_hasXMLNSAttrs = hasXMLNSAttrs;
        this.m_namespace = null;
        this.m_ancestorHasXMLNSAttrs = 0;
    }
    
    public NSInfo(final boolean hasProcessedNS, final boolean hasXMLNSAttrs, final int ancestorHasXMLNSAttrs) {
        this.m_hasProcessedNS = hasProcessedNS;
        this.m_hasXMLNSAttrs = hasXMLNSAttrs;
        this.m_ancestorHasXMLNSAttrs = ancestorHasXMLNSAttrs;
        this.m_namespace = null;
    }
    
    public NSInfo(final String namespace, final boolean hasXMLNSAttrs) {
        this.m_hasProcessedNS = true;
        this.m_hasXMLNSAttrs = hasXMLNSAttrs;
        this.m_namespace = namespace;
        this.m_ancestorHasXMLNSAttrs = 0;
    }
}
