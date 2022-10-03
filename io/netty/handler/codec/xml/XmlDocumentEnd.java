package io.netty.handler.codec.xml;

public final class XmlDocumentEnd
{
    public static final XmlDocumentEnd INSTANCE;
    
    private XmlDocumentEnd() {
    }
    
    static {
        INSTANCE = new XmlDocumentEnd();
    }
}
