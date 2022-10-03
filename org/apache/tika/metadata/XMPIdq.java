package org.apache.tika.metadata;

public interface XMPIdq
{
    public static final String NAMESPACE_URI = "http://ns.adobe.com/xmp/identifier/qual/1.0/";
    public static final String PREFIX = "xmpidq";
    public static final String PREFIX_ = "xmpidq:";
    public static final Property SCHEME = Property.externalText("xmpidq:Scheme");
}
